package cn.edu.tju.service.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.mapper.*;
import cn.edu.tju.pojo.*;
import cn.edu.tju.service.OrderService;
import cn.edu.tju.utils.ArithUtil;
import cn.edu.tju.utils.DateTimeUtil;
import cn.edu.tju.utils.Pojo2VoUtils;
import cn.edu.tju.utils.PropertiesUtil;
import cn.edu.tju.vo.OrderItemVo;
import cn.edu.tju.vo.OrderProductVo;
import cn.edu.tju.vo.OrderVo;
import cn.edu.tju.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Resource
    private ShippingMapper shippingMapper ;
    @Resource
    private CartMapper cartMapper ;
    @Resource
    private ProductMapper productMapper ;
    @Resource
    private OrderMapper orderMapper ;
    @Resource
    private OrderItemMapper orderItemMapper ;
    //生成订单号的方法
    private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    @Override
    public ServerResponse<OrderVo> create(Integer userId, Integer shippingId) {
        //判断参数是否合法
        if(shippingId == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                            ,ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        //判断shipping是否存在
        Shipping shipping = shippingMapper.selectByUserIdPrimaryKey(userId,shippingId) ;
        if(shipping == null ){
            return ServerResponse.createByErrorMessage("该用户不存在此地址信息") ;
        }
        //根据用户id从购物车中获取选中的商品信息
        List<Cart> cartList = cartMapper.selCheckedCartListByUserId(userId) ;
        //将Cart对象转为OrderItem对象
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空") ;
        }
        List<OrderItem> orderItemList = new ArrayList<>() ;
        BigDecimal orderTotalPrice = new BigDecimal("0") ;
        for(Cart cart : cartList){
            OrderItem orderItem = new OrderItem() ;
            Product product = productMapper.selectByPrimaryKey(cart.getProductId()) ;
            if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在线售卖状态") ;
            }
            //需要判断库存吗？？
            //组装OrderItem对象
            if(product != null ){
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setCurrentUnitPrice(product.getPrice());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setUserId(userId);
                orderItem.setTotalPrice(ArithUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue())) ;
                orderItemList.add(orderItem) ;
                orderTotalPrice = ArithUtil.add(orderTotalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue()) ;
            }
        }
        //生成订单
        Order order = new Order() ;
        Long orderNo = generateOrderNo() ;
        order.setOrderNo(orderNo);
        order.setPayment(orderTotalPrice);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        //插入订单
        int rows = orderMapper.insert(order);
        if(rows == 0 ){
            return ServerResponse.createByErrorMessage("创建订单失败") ;
        }
        //插入orderItem
        for(OrderItem orderItem: orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.insertBatch(orderItemList) ;
        //扣库存
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId()) ;
            if(product != null ){
                product.setStock(product.getStock() - orderItem.getQuantity());
                productMapper.updateByPrimaryKeySelective(product) ;
            }
        }
        //清空购物车
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId()) ;
        }
        OrderVo orderVo = order2OrderVo(order,orderItemList ) ;
        return ServerResponse.createBySuccess(orderVo);
    }

    private OrderVo order2OrderVo(Order order , List<OrderItem> orderItemList){
        //构造shippingVo对象
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId()) ;
        ShippingVo shippingVo = Pojo2VoUtils.shipping2ShippingVo(shipping);
        //构造orderItemVo对象
        List<OrderItemVo> orderItemVoList = new ArrayList<>() ;
        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = Pojo2VoUtils.orderItem2OrderItemVo(orderItem) ;
            orderItemVoList.add(orderItemVo) ;
        }
        //构造OrderVo对象
        OrderVo orderVo = new OrderVo() ;
        BeanUtils.copyProperties(order,orderVo);
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        if(shipping != null ){
            orderVo.setShippingId(shipping.getId());
            orderVo.setReceiverName(shipping.getReceiverName());
        }
        orderVo.setShippingVo(shippingVo);
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo ;
    }
    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        //查询该订单是否存在
       Order order = orderMapper.selectByOrderNoUserId(orderNo,userId) ;
        if(order == null ){
            return ServerResponse.createByErrorMessage("该用户无此订单" ) ;
        }
        //判断订单状态是否是未支付状态
        if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("已付款,无法取消订单") ;
        }
        //做数据库的更新操作
        Order updateOrder = new Order() ;
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode()) ;
        updateOrder.setId(order.getId());
        int rows = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(rows >  0 ){
            return ServerResponse.createBySuccess() ;
        }
        return ServerResponse.createByError() ;
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
        //根据用户id从购物车中获取选中的商品信息
        List<Cart> cartList = cartMapper.selCheckedCartListByUserId(userId) ;
        //将Cart对象转为OrderItem对象
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空") ;
        }
        List<OrderItem> orderItemList = new ArrayList<>() ;
        BigDecimal orderTotalPrice = new BigDecimal("0") ;
        for(Cart cart : cartList){
            OrderItem orderItem = new OrderItem() ;
            Product product = productMapper.selectByPrimaryKey(cart.getProductId()) ;
            if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在线售卖状态") ;
            }
            //需要判断库存吗？？
            //组装OrderItem对象
            if(product != null ){
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setCurrentUnitPrice(product.getPrice());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setUserId(userId);
                orderItem.setTotalPrice(ArithUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue())) ;
                orderItemList.add(orderItem) ;
                orderTotalPrice = ArithUtil.add(orderTotalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue()) ;
            }
        }
        //构造orderItemVo对象
        List<OrderItemVo> orderItemVoList = new ArrayList<>() ;
        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = Pojo2VoUtils.orderItem2OrderItemVo(orderItem) ;
            orderItemVoList.add(orderItemVo) ;
        }
        OrderProductVo orderProductVo = new OrderProductVo() ;
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(orderTotalPrice);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return  ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse detail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNoUserId(orderNo,userId) ;
        if(order == null ){
            return ServerResponse.createByErrorMessage("该用户无此订单" );
        }
        //转为OrderVo对象
        List<OrderItem> orderItemList = orderItemMapper.selOrderItemListByOrderNoUserId(orderNo,userId) ;
        OrderVo orderVo = order2OrderVo(order,orderItemList) ;
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize) ;
        List<Order> orderList = orderMapper.selListByUserId(userId) ;
        //将orderList转为orderVoList
        List<OrderVo> orderVoList = new ArrayList<>() ;
        if(!CollectionUtils.isEmpty(orderList)){
            orderList.forEach(order -> {
                //转为OrderVo对象
                List<OrderItem> orderItemList = orderItemMapper.selOrderItemListByOrderNoUserId(order.getOrderNo(),userId) ;
                OrderVo orderVo = order2OrderVo(order,orderItemList) ;
                orderVoList.add(orderVo) ;
            });
        }
        PageInfo pi= new PageInfo(orderVoList) ;
        return ServerResponse.createBySuccess(pi) ;
    }

}

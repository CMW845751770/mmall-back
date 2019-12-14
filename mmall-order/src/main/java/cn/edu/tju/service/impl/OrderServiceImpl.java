package cn.edu.tju.service.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.mapper.*;
import cn.edu.tju.message.producer.DecreaseStockProducer;
import cn.edu.tju.pojo.*;
import cn.edu.tju.service.OrderService;
import cn.edu.tju.utils.*;
import cn.edu.tju.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
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
    @Resource
    private DecreaseStockProducer decreaseStockProducer ;
    @Resource
    private StringRedisTemplate stringRedisTemplate ;

    //生成订单号的方法
    private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    //获取redis中的用户信息
    private UserVO getUserVOInSession(String token){
        if(StringUtils.isBlank(token)){
            return null ;
        }
        String userJson = stringRedisTemplate.opsForValue().get(token) ;
        if(StringUtils.isBlank(userJson)){
            return null ;
        }
        UserVO userVO = JacksonUtil.json2Bean(userJson , UserVO.class) ;
        return userVO ;
    }

    @Override
    @CachePut(cacheNames = "order_list")
    public ServerResponse create(String userKey, Integer shippingId) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    , ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        //判断参数是否合法
        if(shippingId == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                            ,ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        //判断shipping是否存在
        Shipping shipping = shippingMapper.selectByUserIdPrimaryKey(user.getId(),shippingId) ;
        if(shipping == null ){
            return ServerResponse.createByErrorMessage("该用户不存在此地址信息") ;
        }
        //根据用户id从购物车中获取选中的商品信息
        List<Cart> cartList = cartMapper.selCheckedCartListByUserId(user.getId()) ;
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
                orderItem.setProductId(product.getId())
                        .setProductName(product.getName())
                        .setProductImage(product.getMainImage())
                        .setCurrentUnitPrice(product.getPrice())
                        .setQuantity(cart.getQuantity())
                        .setUserId(user.getId())
                        .setTotalPrice(ArithUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue())) ;
                orderItemList.add(orderItem) ;
                orderTotalPrice = ArithUtil.add(orderTotalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue()) ;
            }
        }
        //生成订单
        Order order = new Order() ;
        Long orderNo = generateOrderNo() ;
        order.setOrderNo(orderNo)
             .setPayment(orderTotalPrice)
             .setUserId(user.getId())
             .setShippingId(shippingId)
             .setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode())
             .setStatus(Const.OrderStatusEnum.WAITING.getCode())//订单状态默认为排队中,等待异步通知修改状态
             .setPostage(0);
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
        //清空购物车
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId()) ;
        }
        OrderVo orderVo = order2OrderVo(order,orderItemList ) ;
        // 异步扣库存  本地事务扣库存 -》 异步扣库存
        List<OrderItemVo> orderItemVoList = orderItemList.stream().map(Pojo2VOUtil::orderItem2OrderItemVo).collect(Collectors.toList());
        String orderItemVoListJsonStr = JacksonUtil.bean2Json(orderItemVoList) ;
        log.info("异步扣库存,发送消息{}",orderItemVoListJsonStr );
        decreaseStockProducer.sendMessage(orderItemVoListJsonStr);
        return ServerResponse.createBySuccess(orderVo);
    }

    //将Order对象构造为OrderVO对象
    private OrderVo order2OrderVo(Order order , List<OrderItem> orderItemList){
        //构造shippingVo对象
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId()) ;
        ShippingVo shippingVo = Pojo2VOUtil.shipping2ShippingVo(shipping);
        //构造orderItemVo对象
        List<OrderItemVo> orderItemVoList = orderItemList.stream().map(Pojo2VOUtil::orderItem2OrderItemVo).collect(Collectors.toList()) ;
        //构造OrderVo对象
        OrderVo orderVo = new OrderVo() ;
        BeanUtils.copyProperties(order,orderVo);
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue())
               .setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue())
               .setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()))
               .setSendTime(DateTimeUtil.dateToStr(order.getSendTime()))
               .setEndTime(DateTimeUtil.dateToStr(order.getEndTime()))
               .setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()))
               .setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()))
               .setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        if(shipping != null ){
            orderVo.setShippingId(shipping.getId());
            orderVo.setReceiverName(shipping.getReceiverName());
        }
        orderVo.setShippingVo(shippingVo);
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo ;
    }
    @Override
    @CacheEvict(cacheNames = {"order_list","order_detail"})
    public ServerResponse cancel(String userKey, Long orderNo) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    , ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        //查询该订单是否存在
        Order order = orderMapper.selectByOrderNoUserId(orderNo,user.getId()) ;
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
    public ServerResponse getOrderCartProduct(String userKey ) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    , ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        //根据用户id从购物车中获取选中的商品信息
        List<Cart> cartList = cartMapper.selCheckedCartListByUserId(user.getId()) ;
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
            //需要判断库存吗？应该不需要，除非数据库中的数据是错的
            //组装OrderItem对象
            if(product != null ){
                orderItem.setProductId(product.getId())
                         .setProductName(product.getName())
                         .setProductImage(product.getMainImage())
                         .setCurrentUnitPrice(product.getPrice())
                         .setQuantity(cart.getQuantity())
                         .setUserId(user.getId())
                         .setTotalPrice(ArithUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue())) ;
                orderItemList.add(orderItem) ;
                orderTotalPrice = ArithUtil.add(orderTotalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue()) ;
            }
        }
        //构造orderItemVo对象
        List<OrderItemVo> orderItemVoList = orderItemList.stream().map(Pojo2VOUtil::orderItem2OrderItemVo).collect(Collectors.toList()) ;
        OrderProductVo orderProductVo = new OrderProductVo() ;
        orderProductVo.setOrderItemVoList(orderItemVoList)
                      .setProductTotalPrice(orderTotalPrice)
                      .setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return  ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    @Cacheable(cacheNames = "order_detail", unless = "#result.status ne 0")
    public ServerResponse detail(String userKey, Long orderNo) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    , ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        Order order = orderMapper.selectByOrderNoUserId(orderNo,user.getId()) ;
        if(order == null ){
            return ServerResponse.createByErrorMessage("该用户无此订单" );
        }
        //转为OrderVo对象
        List<OrderItem> orderItemList = orderItemMapper.selOrderItemListByOrderNoUserId(orderNo,user.getId()) ;
        OrderVo orderVo = order2OrderVo(order,orderItemList) ;
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    @Cacheable(cacheNames = "order_list", unless = "#result.status ne 0")
    public ServerResponse list(String userKey, int pageNum, int pageSize) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    , ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        PageHelper.startPage(pageNum,pageSize) ;
        List<Order> orderList = orderMapper.selListByUserId(user.getId()) ;
        //将orderList转为orderVoList
        List<OrderVo> orderVoList = new ArrayList<>() ;
        if(!CollectionUtils.isEmpty(orderList)){
            orderList.forEach(order -> {
                //转为OrderVo对象
                List<OrderItem> orderItemList = orderItemMapper.selOrderItemListByOrderNoUserId(order.getOrderNo(),user.getId()) ;
                OrderVo orderVo = order2OrderVo(order,orderItemList) ;
                orderVoList.add(orderVo) ;
            });
        }
        PageInfo pi= new PageInfo(orderVoList) ;
        return ServerResponse.createBySuccess(pi) ;
    }

    @Override
    @CacheEvict(cacheNames = {"order_list","order_detail"})
    public void updateOrderStatus(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo) ;
        if(order == null ){
            log.error("异步回调更新订单状态：查无此订单");
            return ;
        }
        if(order.getStatus() > Const.OrderStatusEnum.WAITING.getCode()){
            log.info("订单已被更新，无需重复更新");
            return ;
        }
        Order updateOrder= new Order() ;
        updateOrder.setStatus(Const.OrderStatusEnum.NO_PAY.getCode())
                .setOrderNo(orderNo) ;
        orderMapper.updateByOrderNoSelective(updateOrder) ;
    }

}

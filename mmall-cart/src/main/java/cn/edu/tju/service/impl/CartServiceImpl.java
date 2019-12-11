package cn.edu.tju.service.impl;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.mapper.CartMapper;
import cn.edu.tju.mapper.ProductMapper;
import cn.edu.tju.pojo.Cart;
import cn.edu.tju.pojo.Product;
import cn.edu.tju.service.CartService;
import cn.edu.tju.utils.ArithUtil;
import cn.edu.tju.utils.PropertiesUtil;
import cn.edu.tju.vo.CartProductVo;
import cn.edu.tju.vo.CartVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper ;
    @Resource
    private ProductMapper productMapper ;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId) {
        //判断参数是否正确
        if(count == null || productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                                            ,ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        Cart cart = cartMapper.selCartByUserIdAndProductId(userId,productId) ;
        if(cart == null ){
            Cart cartItem = new Cart() ;
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setQuantity(count) ;
            int rows  = cartMapper.insert(cartItem) ;
            if( rows == 0 ){
                return ServerResponse.createByErrorMessage("加入购物车失败") ;
            }
        }else{
            //更新购物车中的数量
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart) ;
        }
        //返回当前购物车中的状态
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        /**1.根据用户id查询出该用户的所有的加入购物车的商品Cart
         * 2.遍历CaetList 设置productVo对象属性
         * 3.将productVo对象加入到CartVo中
         * 4.设置cartVo对象属性
         */
        List<Cart> cartList = cartMapper.selCartListByUserId(userId) ;
        int unCheckedCount = cartMapper.selUnCheckedCountByUserId(userId) ;
        CartVo cartVo = new CartVo() ;
        List<CartProductVo> cartProductVoList = new ArrayList<>() ;
        BigDecimal cartVoTotalPrice = new BigDecimal("0") ;
        for(Cart cart : cartList){
            CartProductVo cartProductVo = new CartProductVo() ;
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(userId);
            cartProductVo.setProductId(cart.getProductId());
            cartProductVo.setProductChecked(cart.getChecked());
            //根据productId查出Product
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if(product != null ){
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductStock(product.getStock());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                if(product.getStock() >= cart.getQuantity()){
                    //库存足够
                    cartProductVo.setQuantity(cart.getQuantity());
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    cartProductVo.setProductTotalPrice(ArithUtil.mul(cart.getQuantity().doubleValue(),product.getPrice().doubleValue()));
                }else{
                    //库存不足
                    cartProductVo.setQuantity(product.getStock());
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    cartProductVo.setProductTotalPrice(ArithUtil.mul(product.getStock().doubleValue(),product.getPrice().doubleValue()));
                    //更新购车数量信息
                    Cart cartUpdateForQuantity = new Cart() ;
                    cartUpdateForQuantity.setQuantity(product.getStock());
                    cartUpdateForQuantity.setId(cart.getId());
                    int rows = cartMapper.updateByPrimaryKeySelective(cartUpdateForQuantity) ;
                    if (rows == 0 ){
                        return ServerResponse.createByErrorMessage("更新购物车商品数量失败") ;
                    }
                }
                if(cart.getChecked() == Const.Cart.CHECKED){
                    cartVoTotalPrice = ArithUtil.add(cartVoTotalPrice.doubleValue() ,cartProductVo.getProductTotalPrice().doubleValue()) ;
                }
                cartProductVoList.add(cartProductVo) ;
            }
        }
        cartVo.setCartTotalPrice(cartVoTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        cartVo.setAllChecked(unCheckedCount == 0);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if(productId == null || count == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        Cart cart = cartMapper.selCartByUserIdAndProductId(userId, productId);
        if(cart != null ){
            Cart cartItem = new Cart() ;
            cartItem.setQuantity(count) ;
            cartItem.setId(cart.getId());
            int rows = cartMapper.updateByPrimaryKeySelective(cartItem) ;
            if(rows > 0 ){
                return list(userId) ;
            }
        }
        return ServerResponse.createByErrorMessage("更新购物车商品数量失败")  ;
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        if(StringUtils.isBlank(productIds)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        List<String> productIdList = Arrays.asList(productIds.split(",")) ;
        cartMapper.deleteByProductIdList(userId , productIdList) ;
        return list(userId) ;
    }

    @Override
    public ServerResponse<CartVo> selectOrUnselect(Integer userId, Integer checked, Integer productId) {
        //参数校验
        if(checked == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        cartMapper.updateByUserIdAndProductId(userId , checked , productId) ;
        return list(userId) ;
    }

    @Override
    public ServerResponse<Integer> selectCartProductCount(Integer userId) {
        Integer resultCount = cartMapper.selectCarProductCount(userId) ;
        return ServerResponse.createBySuccess(resultCount) ;
    }

}

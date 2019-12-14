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
import cn.edu.tju.utils.JacksonUtil;
import cn.edu.tju.utils.PropertiesUtil;
import cn.edu.tju.vo.CartProductVo;
import cn.edu.tju.vo.CartVo;
import cn.edu.tju.vo.UserVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Resource
    private StringRedisTemplate stringRedisTemplate ;

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
    @CacheEvict(cacheNames = "cart_list",allEntries = true)
    public ServerResponse add(String userKey,Integer productId ,Integer count) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        //判断参数是否正确
        if(count == null || productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                                            ,ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        Cart cart = cartMapper.selCartByUserIdAndProductId(user.getId(),productId) ;
        if(cart == null ){
            Cart cartItem = new Cart() ;
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(user.getId());
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
        return showCartList(user.getId());
    }

    @Override
    @Cacheable(cacheNames = "cart_list", unless = "#result.status ne 0")
    public ServerResponse list(String userKey) {
        /**1.根据用户id查询出该用户的所有的加入购物车的商品Cart
         * 2.遍历CaetList 设置productVo对象属性
         * 3.将productVo对象加入到CartVo中
         * 4.设置cartVo对象属性
         */
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        return showCartList(user.getId()) ;
    }


    private ServerResponse showCartList(Integer userId){
        List<Cart> cartList = cartMapper.selCartListByUserId(userId) ;
        int unCheckedCount = cartMapper.selUnCheckedCountByUserId(userId) ;
        CartVo cartVo = new CartVo() ;
        List<CartProductVo> cartProductVoList = new ArrayList<>() ;
        BigDecimal cartVoTotalPrice = new BigDecimal("0") ;
        for(Cart cart : cartList){
            CartProductVo cartProductVo = new CartProductVo() ;
            cartProductVo.setId(cart.getId())
                         .setUserId(userId)
                         .setProductId(cart.getProductId())
                         .setProductChecked(cart.getChecked());
            //根据productId查出Product
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if(product != null ){
                cartProductVo.setProductMainImage(product.getMainImage())
                             .setProductPrice(product.getPrice())
                             .setProductStatus(product.getStatus())
                             .setProductName(product.getName())
                             .setProductStock(product.getStock())
                             .setProductSubtitle(product.getSubtitle());
                if(product.getStock() >= cart.getQuantity()){
                    //库存足够
                    cartProductVo.setQuantity(cart.getQuantity())
                                 .setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS)
                                 .setProductTotalPrice(ArithUtil.mul(cart.getQuantity().doubleValue(),product.getPrice().doubleValue()));
                }else{
                    //库存不足
                    cartProductVo.setQuantity(product.getStock())
                                 .setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL)
                                 .setProductTotalPrice(ArithUtil.mul(product.getStock().doubleValue(),product.getPrice().doubleValue()));
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
        cartVo.setCartTotalPrice(cartVoTotalPrice)
              .setCartProductVoList(cartProductVoList)
              .setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"))
              .setAllChecked(unCheckedCount == 0);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    @CacheEvict(cacheNames = "cart_list",allEntries = true)
    public ServerResponse update(String userKey, Integer count, Integer productId) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        if(productId == null || count == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        Cart cart = cartMapper.selCartByUserIdAndProductId(user.getId(), productId);
        if(cart != null ){
            Cart cartItem = new Cart() ;
            cartItem.setQuantity(count) ;
            cartItem.setId(cart.getId());
            int rows = cartMapper.updateByPrimaryKeySelective(cartItem) ;
            if(rows > 0 ){
                return showCartList(user.getId()) ;
            }
        }
        return ServerResponse.createByErrorMessage("更新购物车商品数量失败")  ;
    }

    @Override
    @CacheEvict(cacheNames = "cart_list",allEntries = true)
    public ServerResponse deleteProduct(String userKey, String productIds) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        if(StringUtils.isBlank(productIds)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        List<String> productIdList = Arrays.asList(productIds.split(",")) ;
        cartMapper.deleteByProductIdList(user.getId() , productIdList) ;
        return showCartList(user.getId()) ;
    }

    @Override
    public ServerResponse selectOrUnselect(String userKey, Integer checked, Integer productId) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        //参数校验
        if(checked == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc()) ;
        }
        cartMapper.updateByUserIdAndProductId(user.getId() , checked , productId) ;
        return showCartList(user.getId()) ;
    }

    @Override
    public ServerResponse selectCartProductCount(String userKey) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        Integer resultCount = cartMapper.selectCarProductCount(user.getId()) ;
        return ServerResponse.createBySuccess(resultCount) ;
    }

}

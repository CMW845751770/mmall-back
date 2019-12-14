package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.hystrix.CartServiceFallbackFactory;
import cn.edu.tju.vo.CartVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "cart", fallbackFactory = CartServiceFallbackFactory.class)
public interface CartService {

    @RequestMapping("/cart/add.do")
    ServerResponse<CartVo> add(@RequestParam("userKey") String userKey,@RequestParam("productId") Integer productId ,@RequestParam("count") Integer count) ;

    @RequestMapping("/cart/list.do")
    ServerResponse<CartVo> list(@RequestParam("userKey")String userKey) ;

    @RequestMapping("/cart/update.do")
    ServerResponse<CartVo> update(@RequestParam("userKey")String userKey ,@RequestParam("count") Integer count,@RequestParam("productId") Integer productId) ;

    @RequestMapping("/cart/delete_product.do")
    ServerResponse<CartVo> deleteProduct(@RequestParam("userKey") String userKey ,@RequestParam("productIds") String productIds) ;

    @RequestMapping("/cart/select_all.do")
    ServerResponse<CartVo> selectAll(@RequestParam("userKey")String userKey) ;

    @RequestMapping("/cart/un_select_all.do")
    ServerResponse<CartVo> unSelectAll(@RequestParam("userKey") String userKey) ;

    @RequestMapping("/cart/select.do")
    ServerResponse<CartVo> select(@RequestParam("userKey")String userKey ,@RequestParam("productId") Integer productId) ;

    @RequestMapping("/cart/un_select.do")
    ServerResponse<CartVo> unSelect(@RequestParam("userKey") String userKey,@RequestParam("productId") Integer productId) ;

    @RequestMapping("/cart/get_cart_product_count.do")
    ServerResponse<Integer> getCartProductCount(@RequestParam("userKey")String userKey) ;
}

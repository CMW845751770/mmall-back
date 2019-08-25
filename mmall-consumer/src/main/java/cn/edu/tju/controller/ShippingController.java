package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.service.ShippingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/shipping/")
public class ShippingController {

    @Resource
    private ShippingService shippingService ;

    @RequestMapping("add.do")
    public ServerResponse add(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, Shipping shipping){
        return shippingService.add(shipping,userKey) ;
    }

    @RequestMapping("del.do")
    public ServerResponse del(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey
                                    ,@RequestParam("shippingId") Integer shippingId){
        return shippingService.del(userKey,shippingId) ;
    }

    @RequestMapping("update.do")
    public ServerResponse update(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey, @RequestBody Shipping shipping){
        return shippingService.update(shipping,userKey) ;
    }

    @RequestMapping("select.do")
    public ServerResponse select(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey
            ,@RequestParam("shippingId") Integer shippingId){
        return shippingService.select(userKey,shippingId) ;
    }

    @RequestMapping("list.do")
    public ServerResponse list(@CookieValue(value = Const.CURRENT_USER,defaultValue = StringUtils.EMPTY)String userKey
                , @RequestParam(defaultValue = "1") Integer pageNum
                , @RequestParam(defaultValue = "10")Integer pageSize){
        return shippingService.list(userKey,pageNum,pageSize) ;
    }

}

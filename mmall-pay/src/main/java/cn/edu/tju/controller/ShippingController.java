package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.ShippingForm;
import cn.edu.tju.form.UserForm;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.ShippingService;
import org.apache.catalina.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sun.applet.Main;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RequestMapping("/shipping/")
@RestController
public class ShippingController {


    @Resource
    private ShippingService shippingServiceImpl;

    @RequestMapping("add.do")
    public ServerResponse add(String userKey,@RequestBody @Valid ShippingForm shippingForm, BindingResult result) {
        if (result.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    result.getFieldError().getDefaultMessage());
        }
        return shippingServiceImpl.add(userKey, shippingForm);
    }

    @RequestMapping("del.do")
    public ServerResponse del(String userKey, Integer shippingId) {
        return shippingServiceImpl.del(userKey, shippingId);
    }

    @RequestMapping("update.do")
    public ServerResponse update(String userKey, @RequestBody @Valid ShippingForm shippingForm, BindingResult result) {
        if (result.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    result.getFieldError().getDefaultMessage());
        }
        return shippingServiceImpl.update(userKey, shippingForm);
    }

    @RequestMapping("select.do")
    public ServerResponse select(String userKey, Integer shippingId) {
        return shippingServiceImpl.select(userKey, shippingId);
    }

    @RequestMapping("list.do")
    public ServerResponse list(String userKey, @RequestParam(defaultValue = "1") Integer pageNum
            , @RequestParam(defaultValue = "10") Integer pageSize) {

        return shippingServiceImpl.list(userKey, pageNum, pageSize);
    }

}

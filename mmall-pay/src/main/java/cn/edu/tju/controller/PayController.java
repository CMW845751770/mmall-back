package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.PayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/pay/")
@Slf4j
public class PayController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private PayService payServiceImpl;

    private Integer isUserLogin(String userKey) {
        if (StringUtils.isNotBlank(userKey)) {
            User user = (User) redisTemplate.opsForValue().get(userKey);
            if (user != null) {
                return user.getId();
            }
        }
        return Const.USER_NOT_ONLINE;
    }

    @RequestMapping("pay.do")
    public ServerResponse pay(String userKey, Long orderNo, HttpServletRequest request) {
        Integer userId = isUserLogin(userKey);
        if (userId != null && !userId.equals(Const.USER_NOT_ONLINE)) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            log.info("path:{}", path);
            return payServiceImpl.pay(orderNo, userId, path);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

    }

    @RequestMapping("alipay_callback.do")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {

                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());

            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常", e);
        }
        //todo 验证各种数据
        //
        ServerResponse serverResponse = null;
        try {
            serverResponse = payServiceImpl.aliCallback(params);
        } catch (ParseException e) {
            log.error("日期转化错误");
        }
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping("query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPayStatus(String userKey, Long orderNo) {
        Integer userId = isUserLogin(userKey);
        if (userId == null || userId.equals(Const.USER_NOT_ONLINE)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = payServiceImpl.queryOrderPayStatus(userId, orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}




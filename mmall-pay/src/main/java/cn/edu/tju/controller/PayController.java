package cn.edu.tju.controller;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.PayService;
import cn.edu.tju.utils.JacksonUtil;
import cn.edu.tju.vo.UserVO;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/pay/")
@Slf4j
public class PayController {

    @Resource
    private PayService payServiceImpl;
    @Resource
    private StringRedisTemplate stringRedisTemplate ;

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

    @RequestMapping("pay.do")
    public ServerResponse pay(String userKey, Long orderNo, HttpServletRequest request) {
        UserVO userVO = getUserVOInSession(userKey);
        if (userVO != null) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            log.info("path:{}", path);
            return payServiceImpl.pay(orderNo, userVO.getId(), path);
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
        ServerResponse  serverResponse = payServiceImpl.aliCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping("query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPayStatus(String userKey, Long orderNo) {
        UserVO userVO = getUserVOInSession(userKey);
        if (userVO != null) {
            ServerResponse serverResponse = payServiceImpl.queryOrderPayStatus(userVO.getId(), orderNo);
            if (serverResponse.isSuccess()) {
                return ServerResponse.createBySuccess(true);
            }
            return ServerResponse.createBySuccess(false);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
    }
}




package cn.edu.tju.filter;

import cn.edu.tju.commons.Const;
import cn.edu.tju.util.ZuulUtil;
import cn.edu.tju.utils.CookieUtil;
import cn.edu.tju.utils.JacksonUtil;
import cn.edu.tju.vo.UserVO;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: CMW天下第一
 */
@Component
public class LoginFilter extends ZuulFilter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 2;
    }

    @Override
    public boolean shouldFilter() {
        ImmutableSet<String> immutableSet = ImmutableSet.of("/user/login.do","/user/logout.do",
                "/user/register.do","/product/list.do","/user/check_valid.do","/user/forget_get_question.do",
                "/user/forget_check_answer.do","/user/forget_reset_password.do") ;
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String uri = request.getRequestURI();
        if (immutableSet.contains(uri)) {
            return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = CookieUtil.getCookieValue(request, Const.CURRENT_USER);
        if (StringUtils.isBlank(token)) {
            context.setSendZuulResponse(false);
            ZuulUtil.responseHandler(context.getResponse(), "用户未登录");
            return null;
        }
        String userJson = stringRedisTemplate.opsForValue().get(token);
        if (StringUtils.isBlank(userJson)) {
            context.setSendZuulResponse(false);
            ZuulUtil.responseHandler(context.getResponse(), "用户未登录");
            return null;

        }
        UserVO userVO = JacksonUtil.json2Bean(userJson,UserVO.class) ;
        if(userVO == null ){
            context.setSendZuulResponse(false);
            ZuulUtil.responseHandler(context.getResponse(), "用户未登录");
        }
        return null;
    }

}

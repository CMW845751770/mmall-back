package cn.edu.tju.filter;

import cn.edu.tju.commons.Const;
import cn.edu.tju.util.ZuulUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @Author: CMW天下第一
 */
@Component
public class RateLimiterFilter extends ZuulFilter {
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(Const.RATE_LIMIT_TOKENS);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SERVLET_DETECTION_FILTER_ORDER - 1 ;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        if(!RATE_LIMITER.tryAcquire()){
            RequestContext context = RequestContext.getCurrentContext();
            context.setSendZuulResponse(false);
            ZuulUtil.responseHandler(context.getResponse(),"网站流量过大，请稍后访问");
        }
        return null;
    }
}

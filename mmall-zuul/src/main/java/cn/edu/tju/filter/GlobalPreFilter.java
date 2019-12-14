package cn.edu.tju.filter;

import cn.edu.tju.utils.NetworkUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GlobalPreFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE  ;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException
    {

        RequestContext currentContext = RequestContext.getCurrentContext();
        currentContext.set("startTime" , System.currentTimeMillis());
        String ipAddress = NetworkUtil.getIpAddress(currentContext.getRequest());
        log.info("ip为{}的用户于{}进入网关",ipAddress);
        return null;
    }
}

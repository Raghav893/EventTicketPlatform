package com.raghav.eventticketplatform.Filter;

import com.raghav.eventticketplatform.Config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    @Autowired
    private final ProxyManager proxyManager;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String method = request.getMethod();

        BucketConfiguration configuration;
        if (path.equals("/auth/login")&& method.equals("POST")){
            configuration = RateLimitConfig.loginLimiter();//to set per api filter config  to each path
        }
        else if (path.equals("/auth/register")) {
            configuration = RateLimitConfig.registerationLimiter();
        }
        else if (path.equals("/events/create")) {
            configuration = RateLimitConfig.EventCreationLimit();
        }
        else if (path.equals("/events/{eventId}/ticket-types/{ticketTypeId}/purchase")){
            configuration = RateLimitConfig.ticketPurchaseLimiter();
        }
        else {
            configuration = null;
        }
        if (configuration == null){
            filterChain.doFilter(request,response);
            return;
        }

        String key =ip +":"+method+ ":" +path;
        Bucket bucket = proxyManager.getProxy(key,()->configuration);//It injects key and configuration in proxyManager

        if (bucket.tryConsume(1)){
            filterChain.doFilter(request,response);
        }
        else {
            response.setStatus(429);
            response.getWriter().write("Too many Requests !!!!!!!");
        }
    }
}

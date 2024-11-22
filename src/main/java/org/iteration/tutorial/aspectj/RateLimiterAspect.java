package org.iteration.tutorial.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Iteration
 */
@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private RedisSlidingWindowRateLimiter rateLimiter;

    @Pointcut("@annotation(rateLimiter)")
    public void rateLimiterPointcut(RateLimiter rateLimiter) {
    }

    @Around("rateLimiterPointcut(rateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        String userId = (String) joinPoint.getArgs()[0];  // 这里模拟用户参数，SecurityContext中取

        // 判断是否允许请求
        if (!rateLimiterAllowRequest(userId, rateLimiter.windowSize(), rateLimiter.maxRequests())) {
            return "Too many requests. Please try again later.";
        }

        return joinPoint.proceed();
    }

    private boolean rateLimiterAllowRequest(String userId, int windowSize, int maxRequests) {
        return rateLimiter.allowRequest(userId, windowSize, maxRequests);
    }
}

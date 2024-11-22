package org.iteration.tutorial.aspectj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用滑动时间窗口限流算法，限制单位时间内允许访问的次数
 *
 * @author Iteration
 */
@Component
public class RedisSlidingWindowRateLimiter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    public boolean allowRequest(String userId, int windowSize, int maxRequests) {
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + userId;

        // 获取用户请求的时间戳队列
        List<String> requestTimestamps = redisTemplate.opsForList().range(rateLimitKey, 0, -1);

        // 当前时间戳
        long currentTime = System.currentTimeMillis();

        // 清理过期的时间戳
        requestTimestamps = requestTimestamps.stream()
                .filter(timestamp -> currentTime - Long.parseLong(timestamp) <= windowSize * 1000L)
                .collect(Collectors.toList());

        // 判断请求次数是否超过限制
        if (requestTimestamps.size() < maxRequests) {
            // 允许请求，添加当前时间戳到队列
            redisTemplate.opsForList().leftPush(rateLimitKey, String.valueOf(currentTime));
            // 设置队列的过期时间，防止数据积压
            redisTemplate.expire(rateLimitKey, windowSize + 1, java.util.concurrent.TimeUnit.SECONDS);

            return true;
        }

        return false;
    }
}

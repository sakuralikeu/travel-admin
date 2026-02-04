package com.travel.admin.aspect;

import com.travel.admin.common.annotation.RateLimit;
import com.travel.admin.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitAspect {

    private static final ConcurrentHashMap<String, WindowCounter> COUNTERS = new ConcurrentHashMap<>();

    @Around("@annotation(com.travel.admin.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimit rateLimit = signature.getMethod().getAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return joinPoint.proceed();
        }
        String key = buildKey(rateLimit);
        long now = System.currentTimeMillis();
        long windowMillis = rateLimit.windowSeconds() * 1000L;
        WindowCounter counter = COUNTERS.compute(key, (k, existing) -> {
            if (existing == null || now - existing.windowStartMillis >= windowMillis) {
                WindowCounter created = new WindowCounter();
                created.windowStartMillis = now;
                created.count = 1;
                return created;
            }
            existing.count = existing.count + 1;
            return existing;
        });
        if (counter.count > rateLimit.maxRequests()) {
            throw new BusinessException("请求过于频繁, 请稍后再试");
        }
        return joinPoint.proceed();
    }

    private String buildKey(RateLimit rateLimit) {
        if (rateLimit.key() != null && !rateLimit.key().isEmpty()) {
            return rateLimit.key();
        }
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return "default";
        }
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        return ip + ":" + method + ":" + uri;
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private static final class WindowCounter {

        private long windowStartMillis;

        private int count;
    }
}


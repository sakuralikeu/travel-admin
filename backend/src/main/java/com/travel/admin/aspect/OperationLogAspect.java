package com.travel.admin.aspect;

import com.travel.admin.common.annotation.LogOperation;
import com.travel.admin.entity.OperationLog;
import com.travel.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Around("@annotation(com.travel.admin.common.annotation.LogOperation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        boolean success = true;
        String errorMessage = null;
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            success = false;
            errorMessage = ex.getMessage();
            throw ex;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            OperationLog log = buildLog(joinPoint, success, errorMessage, executionTime);
            operationLogService.save(log);
        }
        return result;
    }

    private OperationLog buildLog(ProceedingJoinPoint joinPoint, boolean success, String errorMessage,
                                  long executionTime) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogOperation annotation = method.getAnnotation(LogOperation.class);
        OperationLog log = new OperationLog();
        log.setModule(annotation.module());
        log.setName(annotation.name());
        log.setType(annotation.type());
        HttpServletRequest request = currentRequest();
        if (request != null) {
            log.setRequestUri(request.getRequestURI());
            log.setHttpMethod(request.getMethod());
            log.setIp(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
            String params = Arrays.toString(joinPoint.getArgs());
            log.setRequestParams(params);
        }
        log.setSuccess(success);
        log.setErrorMessage(errorMessage);
        log.setExecutionTimeMillis(executionTime);
        return log;
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}

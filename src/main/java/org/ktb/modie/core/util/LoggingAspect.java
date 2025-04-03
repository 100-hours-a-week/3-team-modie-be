package org.ktb.modie.core.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* org.ktb.modie.service..*(..))")
    private void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();

        logger.debug("[{}] Start: {} - Parameter: {}", MDC.get("requestId"), methodName,
            joinPoint.getArgs());

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            logger.debug("[{}] Completed: {} - time: {}ms", MDC.get("requestId"), methodName, (endTime - startTime));
            return result;
        } catch (Exception e) {
            logger.error("[{}] Error: {} - Method: {} - Cause: {}", MDC.get("requestId"), e.getClass().getSimpleName(),
                methodName, e.getMessage(),
                e);
            throw e;
        }
    }
}

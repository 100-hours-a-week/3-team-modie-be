package org.ktb.modie.core.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* org.ktb.modie.*.*(..))")
    private void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();

        logger.debug("시작aaaaaaaaaaaaaaaaaaaaaaaaaaaaa: {} - 파라미터: {}", methodName, joinPoint.getArgs());

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            logger.debug("완료: {} - 소요시간: {}ms", methodName, (endTime - startTime));
            return result;
        } catch (Exception e) {
            logger.error("오류: {} - 메소드: {} - 원인: {}", e.getClass().getSimpleName(), methodName, e.getMessage(), e);
            throw e;
        }
    }
}

package br.com.rzaninelli.gestor.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ErrorLoggingAspect {

    @AfterThrowing(pointcut = "execution(* br.com.rzaninelli.gestor.service..*(..))", throwing = "ex")

    public void logServiceError(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        log.error("Error in {}.{} with args: {} - Exception: {}", className, methodName, Arrays.toString(args), ex.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* br.com.rzaninelli.gestor.repository..*(..))", throwing = "ex")
    public void logRepositoryError(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.error("Database error in {}.{} - Exception: {}", className, methodName, ex.getMessage());
    }
}

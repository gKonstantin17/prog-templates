package micros.plannerutils.aop;

import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log
public class LoggingAspect {
    // при выполнении всех методов из пакета controller (и под пакетов)
    @Around("execution(* micros.plannertodo.controller ..*(..)))")
    public Object profileContollerMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        // получить сигнатуру метода
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        log.info("-----------Executing "+className+"."+methodName+"--------" );
        // proceed - выполнить метод, без него аспект не будет работать
        Object result = proceedingJoinPoint.proceed();
        return result;
    }
}

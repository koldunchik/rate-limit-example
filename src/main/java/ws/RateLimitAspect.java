package ws;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Aspect
public class RateLimitAspect {
    private final IpChecker ipChecker;

    Logger LOG = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    public RateLimitAspect(IpChecker ipChecker) {
        this.ipChecker = ipChecker;
    }

    @Pointcut("within(ws.*) && @annotation(ws.RateLimited)")
    private void rateLimitAnnotation() {
    }

    @Around("ws.RateLimitAspect.rateLimitAnnotation()")
    public Object limitIfNecessary(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest req = getRequest();
        String ipAddress = Utils.getIpFromRequest(req);

        if (!ipChecker.isLimited(ipAddress)) {
            return pjp.proceed();
        } else {
            HttpServletResponse res = getResponse();
            res.setContentType("text/plain");
            res.setStatus(502);
            return null;
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest();
    }

    private HttpServletResponse getResponse() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getResponse();
    }

}

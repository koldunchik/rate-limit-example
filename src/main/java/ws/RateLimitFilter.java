package ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE )
public class RateLimitFilter implements Filter {

    @Autowired
    private IpChecker ipChecker;

    Logger LOG = LoggerFactory.getLogger(SimpleController.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ipAddress = Utils.getIpFromRequest(req);

        if (!ipChecker.isLimited(ipAddress)) {
            LOG.info(
                    "Logging Request  {} : [{}]{}", req.getMethod(),
                    ipAddress,
                    req.getRequestURI());

            chain.doFilter(request, response);

        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("text/plain");
            httpResponse.setStatus(502);
            LOG.info("Rate is limited");
        }

    }

    @Override
    public void destroy() {

    }

}
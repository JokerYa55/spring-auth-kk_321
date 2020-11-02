package app.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author vasil
 */
@Slf4j
@Service
//@Order(0)
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        log.info("query = {}", req.getQueryString());
        req.getParameterMap().forEach((t, u) -> {
            log.info("t = {} u = {}", t, u);
        });
        log.info("code = {}", req.getParameter("code"));

        chain.doFilter(request, response);
    }

    private void handleNoSecurityContext(ServletRequest request, ServletResponse response, FilterChain chain) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

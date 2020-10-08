package app.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author vasil
 */
@Slf4j
//@Service
//@Order(0)
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        log.info("query = {}", req.getQueryString());
        req.getParameterMap().forEach((t, u) -> {
            log.info("t = {} u = {}", t, u);
        });
        log.info("code = {}", req.getParameter("code"));
//        if (!(request instanceof HttpServletRequest)) {
//            throw new RuntimeException("Expecting a HTTP request");
//        }
//
//        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
//
//        if (context == null) {
//            handleNoSecurityContext(request, response, chain);
//            return;
//        }
//
//        AccessToken accessToken = context.getToken();
//        Integer userId = Integer.parseInt(accessToken.getOtherClaims().get("user_id").toString());

        chain.doFilter(request, response);
    }

    private void handleNoSecurityContext(ServletRequest request, ServletResponse response, FilterChain chain) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

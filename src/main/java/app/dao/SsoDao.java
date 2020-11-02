package app.dao;

import app.util.RestClient;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vasiliy.Andricov
 */
@Slf4j
@Service
public class SsoDao {

    private static final String BEARER_ATTR_NAME = "Bearer ";
    private static final String TOKEN_ATTR_NAME = "token";
    private static final String USERID_ATTR_NAME = "user_id";
    private static final String AUTH_ATTR_NAME = "Authorization";
    private static final String URL_STRING = "url = {}";
    private static final String RESULT_STRING = "result = {}";
    private static final String STATUS_STRING = "status = {}";

    /**
     * Метод получения токена SSO
     *
     * @param pUrl - URL метода SSO для получения токена SSO
     * @param pParam - Параметры запроса в соответствии с документацийей на REST
     * API SSO
     * @return
     */
    public RestResult getToken(final String pUrl, final Map<String, Object> pParam) {
        RestResult result = new RestResult();
        try (RestClient client = new RestClient()) {
            WebTarget target = client.getInstance().target(pUrl);
            log.info(URL_STRING, target.getUri().toString());
            // проверяем тип запроса
            Form getTokenForm = new Form();
            if (pParam != null) {
                pParam.forEach((t, u) -> {
                    if (t.equals("password")) {
                        log.debug(String.format("t = %-25s u = %s", t, "****************"));
                    } else {
                        log.debug(String.format("t = %-25s u = %s", t, u));
                    }
                    getTokenForm.param(t, u.toString());
                });
            }
            log.debug("form = {}", getTokenForm.toString());
            Response res = target.request().post(Entity.form(getTokenForm));
            log.info(STATUS_STRING, res.getStatus());
            result.setStatus(res.getStatus());
            if (res.getStatus() == 200) {
                JSONParser parser = new JSONParser();
                result.setData((JSONObject) parser.parse(res.readEntity(String.class)));
            } else {
                String dataErr = res.readEntity(String.class);
                result.setData(dataErr);
                log.error("res = {}", dataErr);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info(RESULT_STRING, result);
        return result;
    }

}

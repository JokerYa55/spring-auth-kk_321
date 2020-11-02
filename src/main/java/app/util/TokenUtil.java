package app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.keycloak.RSATokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jws.JWSHeader;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author Vasiliy.Andricov
 */
@Slf4j
@Getter
public final class TokenUtil {

    private String token;
    private JSONArray jsonArr = null;
    private JSONObject jsonToken = null;
    private JSONObject jsonRole = null;
    private String userId = null;
    private String clientId = null;
    private String phone = null;
    private String authSmsVerified;
    private String ssoUrl = null;
    private UserRepresentation user = null;
    private String urlGetCert;
    private String sessionId = null;
    private Map<String, Object> cert = null;

    /**
     *
     * @param token
     * @param urlGetCert
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    public TokenUtil(String token, String urlGetCert) throws ParseException, UnsupportedEncodingException {
        this.token = token;
        this.jsonArr = getDecodedJwt(token);
        this.jsonToken = parseToken((String) this.jsonArr.get(1));
        this.jsonRole = (JSONObject) jsonToken.get("resource_access");
        this.ssoUrl = (String) jsonToken.get("iss");
        this.userId = (String) jsonToken.get("sub");
        this.clientId = (String) jsonToken.get("aud");
        this.phone = (String) jsonToken.get("phone");
        this.authSmsVerified = (String) jsonToken.get("AUTH_SMS_VERIFIED");
        this.urlGetCert = urlGetCert;
        this.sessionId = (String) jsonToken.get("session_state");
        this.cert = null;
    }

    /**
     *
     * @param token
     * @param cert
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    public TokenUtil(String token, Map<String, Object> cert) throws ParseException, UnsupportedEncodingException {
        this.token = token;
        this.jsonArr = getDecodedJwt(token);
        this.jsonToken = parseToken((String) this.jsonArr.get(1));
        this.jsonRole = (JSONObject) jsonToken.get("resource_access");
        this.ssoUrl = (String) jsonToken.get("iss");
        this.userId = (String) jsonToken.get("sub");
        this.clientId = (String) jsonToken.get("aud");
        this.phone = (String) jsonToken.get("phone");
        this.authSmsVerified = (String) jsonToken.get("AUTH_SMS_VERIFIED");
        this.urlGetCert = null;
        this.sessionId = (String) jsonToken.get("session_state");
        this.cert = cert;
    }

    /**
     *
     * @return @throws ParseException
     */
    public boolean verifyToken() throws ParseException {
        boolean res = false;
        try {
            //res = verifyTokenByRest();
            res = verifyTokenByPublicKey();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    /**
     *
     * @param role
     * @param client
     * @return
     */
    public boolean isUserInRole(String role, String client) {
        boolean result = false;
        try {
            log.debug(String.format("this.json_role = %s", this.jsonRole.toJSONString()));
            JSONArray roleArr = (JSONArray) ((JSONObject) this.jsonRole.get(client)).get("roles");
            log.debug(String.format("role_arr = %s", roleArr.toJSONString()));
            for (Object item : roleArr) {
                log.debug(String.format("item = %s", item));
                if (((String) item).equals(role)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.debug(String.format("result = %s", result));
        return result;
    }

    /**
     *
     * @param token
     * @return
     * @throws ParseException
     */
    private JSONObject parseToken(String token) throws ParseException {
        JSONObject result = null;
        JSONParser parser = new JSONParser();
        result = (JSONObject) parser.parse(token);
        return result;
    }

    /**
     *
     * @return
     */
    private JSONObject getUserRole() {
        return (JSONObject) jsonToken.get("resource_access");
    }

    /**
     *
     * @return
     */
    private boolean verifyTokenByPublicKey() {
        boolean result = false;
        result = verifyTokenByPublicKey(token);
        return result;
    }

    /**
     *
     * @param token
     * @return
     */
    private boolean verifyTokenByPublicKey(String token) {
        boolean result = false;
        try {
            RSATokenVerifier verifier = RSATokenVerifier.create(token);
            PublicKey publicKey = getRealmPublicKey(verifier.getHeader());
            RSATokenVerifier ts = verifier.realmUrl(getRealmUrl()).publicKey(publicKey).verify();
            result = true;
        } catch (VerificationException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
        return result;
    }

    /**
     *
     * @return
     */
    public String getRealmCertsUrl() {
        String urlKey = urlGetCert;
        return urlKey;
    }

    /**
     *
     * @return
     */
    public String getRealmUrl() {
        return (String) jsonToken.get("iss");
    }

    /**
     *
     * @param jwsHeader
     * @return
     */
    private PublicKey getRealmPublicKey(JWSHeader jwsHeader) {
        return retrievePublicKeyFromCertsEndpoint(jwsHeader);

    }

    /**
     *
     * @param jwsHeader
     * @return
     */
    private PublicKey retrievePublicKeyFromCertsEndpoint(JWSHeader jwsHeader) {
        try {
            ObjectMapper om = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> certInfos = null;
            if (cert == null) {
                certInfos = om.readValue(new URL(getRealmCertsUrl()).openStream(), Map.class);
            } else {
                certInfos = this.cert;
            }
            log.debug(String.format("url = %s", getRealmCertsUrl()));
            List<Map<String, Object>> keys = (List<Map<String, Object>>) certInfos.get("keys");
            Map<String, Object> keyInfo = null;
            for (Map<String, Object> key : keys) {
                String kid = (String) key.get("kid");
                if (jwsHeader.getKeyId().equals(kid)) {
                    keyInfo = key;
                    break;
                }
            }

            if (keyInfo == null) {
                return null;
            }

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            String modulusBase64 = (String) keyInfo.get("n");
            String exponentBase64 = (String) keyInfo.get("e");

            // see org.keycloak.jose.jwk.JWKBuilder#rs256
            Base64.Decoder urlDecoder = Base64.getUrlDecoder();
            BigInteger modulus = new BigInteger(1, urlDecoder.decode(modulusBase64));
            BigInteger publicExponent = new BigInteger(1, urlDecoder.decode(exponentBase64));

            return keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param jwt
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws org.json.simple.parser.ParseException
     */
    public JSONArray getDecodedJwt(String jwt) throws UnsupportedEncodingException, ParseException {
        JSONArray result = new JSONArray();        
        String[] parts = jwt.split("[.]");

        int index = 0;
        for (String part : parts) {
            if (index >= 2) {
                break;
            }
            index++;
            byte[] partAsBytes = part.getBytes("UTF-8");
            String decodedPart = new String(java.util.Base64.getUrlDecoder().decode(partAsBytes), "UTF-8");
            result.add(decodedPart);
        }
        return result;
    }

}

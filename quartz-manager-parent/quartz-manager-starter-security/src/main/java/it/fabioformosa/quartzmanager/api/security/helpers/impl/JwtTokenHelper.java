package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.fabioformosa.quartzmanager.api.security.properties.JwtSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author Fabio.Formosa
 */

public class JwtTokenHelper {

  private static final Logger log = LoggerFactory.getLogger(JwtTokenHelper.class);

  private static String base64EncodeSecretKey(String secretKey) {
    return Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  private final String appName;

  private final JwtSecurityProperties jwtSecurityProps;

  private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

  public JwtTokenHelper(String appName, JwtSecurityProperties jwtSecurityProps) {
    super();
    this.appName = appName;
    this.jwtSecurityProps = jwtSecurityProps;
  }

  public Boolean canTokenBeRefreshed(String token) {
    try {
      final Date expirationDate = verifyAndGetClaimsFromToken(token).getExpiration();
      return expirationDate.compareTo(generateCurrentDate()) > 0;
    } catch (Exception e) {
      log.error("Error getting claims from jwt token due to " + e.getMessage(), e);
      return false;
    }
  }

  private Date generateCurrentDate() {
    return new Date(getCurrentTimeMillis());
  }

  private Date generateExpirationDate() {
    return new Date(getCurrentTimeMillis() + jwtSecurityProps.getExpirationInSec() * 1000);
  }

  private String generateToken(Map<String, Object> claims) {
    return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
      .signWith(SIGNATURE_ALGORITHM, base64EncodeSecretKey(jwtSecurityProps.getSecret())).compact();
  }

  public String generateToken(String username) {
    return Jwts.builder().setIssuer(appName).setSubject(username).setIssuedAt(generateCurrentDate())
      .setExpiration(generateExpirationDate())
      .signWith(SIGNATURE_ALGORITHM, base64EncodeSecretKey(jwtSecurityProps.getSecret())).compact();
  }

  private Claims verifyAndGetClaimsFromToken(String token) {
    Claims claims;
    claims = Jwts.parser().setSigningKey(base64EncodeSecretKey(jwtSecurityProps.getSecret()))
      .parseClaimsJws(token).getBody();
    if (claims == null)
      throw new IllegalStateException("Not found any claims into the JWT token!");
    return claims;
  }

  /**
   * Find a specific HTTP cookie in a request.
   *
   * @param request The HTTP request object.
   * @param name    The cookie name to look for.
   * @return The cookie, or <code>null</code> if not found.
   */
  public Cookie getCookieValueByName(HttpServletRequest request, String name) {
    if (request.getCookies() == null)
      return null;
    for (int i = 0; i < request.getCookies().length; i++)
      if (request.getCookies()[i].getName().equals(name))
        return request.getCookies()[i];
    return null;
  }

  private long getCurrentTimeMillis() {
    return LocalDateTime.now().atZone(ZoneId.of("Europe/Rome")).toInstant().toEpochMilli();
  }

  public String verifyTokenAndExtractUsername(String token) {
    final Claims claims = verifyAndGetClaimsFromToken(token);
    return claims.getSubject();
  }

  public String refreshToken(String token) {
    String refreshedToken;
    try {
      final Claims claims = verifyAndGetClaimsFromToken(token);
      claims.setIssuedAt(generateCurrentDate());
      refreshedToken = generateToken(claims);
    } catch (Exception e) {
      log.error("Error refreshing jwt token due to " + e.getMessage(), e);
      refreshedToken = null;
    }
    return refreshedToken;
  }

  public String retrieveToken(HttpServletRequest request) {
    if (jwtSecurityProps.getCookieStrategy().isEnabled()) {
      Cookie authCookie = getCookieValueByName(request, jwtSecurityProps.getCookieStrategy().getCookie());
      if (authCookie != null)
        return authCookie.getValue();
    }

    if (jwtSecurityProps.getHeaderStrategy().isEnabled()) {
      String authHeader = request.getHeader(jwtSecurityProps.getHeaderStrategy().getHeader());
      if (authHeader != null && authHeader.startsWith("Bearer "))
        return authHeader.substring(7);
    }

    if (request.getParameter("access_token") != null)
      return request.getParameter("access_token");

    return null;
  }

  public void setHeader(HttpServletResponse response, String token) {
    response.addHeader(jwtSecurityProps.getHeaderStrategy().getHeader(), "Bearer " + token);
  }
}

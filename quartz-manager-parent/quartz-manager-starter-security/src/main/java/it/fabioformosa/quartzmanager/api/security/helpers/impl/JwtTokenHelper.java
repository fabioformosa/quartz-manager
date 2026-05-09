package it.fabioformosa.quartzmanager.api.security.helpers.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import it.fabioformosa.quartzmanager.api.security.properties.JwtSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author Fabio.Formosa
 */

public class JwtTokenHelper {

  private static final Logger log = LoggerFactory.getLogger(JwtTokenHelper.class);

  private static SecretKey signingKey(String secretKey) {
    try {
      byte[] keyBytes = MessageDigest.getInstance("SHA-512").digest(secretKey.getBytes(StandardCharsets.UTF_8));
      return new SecretKeySpec(keyBytes, "HmacSHA512");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Unable to create JWT signing key", e);
    }
  }

  private final String appName;

  private final JwtSecurityProperties jwtSecurityProps;

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
    return Jwts.builder().claims(claims).expiration(generateExpirationDate())
      .signWith(signingKey(jwtSecurityProps.getSecret()), Jwts.SIG.HS512).compact();
  }

  public String generateToken(String username) {
    return Jwts.builder().issuer(appName).subject(username).issuedAt(generateCurrentDate())
      .expiration(generateExpirationDate())
      .signWith(signingKey(jwtSecurityProps.getSecret()), Jwts.SIG.HS512).compact();
  }

  private Claims verifyAndGetClaimsFromToken(String token) {
    Claims claims;
    claims = Jwts.parser().verifyWith(signingKey(jwtSecurityProps.getSecret())).build()
      .parseSignedClaims(token).getPayload();
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
      refreshedToken = Jwts.builder().claims(claims).issuedAt(generateCurrentDate()).expiration(generateExpirationDate())
        .signWith(signingKey(jwtSecurityProps.getSecret()), Jwts.SIG.HS512).compact();
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

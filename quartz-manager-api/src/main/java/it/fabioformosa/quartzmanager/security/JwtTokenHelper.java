package it.fabioformosa.quartzmanager.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.fabioformosa.quartzmanager.configuration.properties.JwtSecurityProperties;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Fabio.Formosa
 *
 */

@Slf4j
@Component
public class JwtTokenHelper {

  private static String base64EncodeSecretKey(String secretKey) {
    return Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  @Value("${app.name}")
  private String APP_NAME;


  @Autowired
  private JwtSecurityProperties jwtSecurityProps;

  private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

  public Boolean canTokenBeRefreshed(String token) {
    try {
      final Date expirationDate = getClaimsFromToken(token).getExpiration();
      // String username = getUsernameFromToken(token);
      // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return expirationDate.compareTo(generateCurrentDate()) > 0;
    } catch (Exception e) {
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
    return Jwts.builder()
        .setClaims(claims)
        .setExpiration(generateExpirationDate())
        .signWith( SIGNATURE_ALGORITHM, base64EncodeSecretKey(jwtSecurityProps.getSecret()))
        .compact();
  }

  public String generateToken(String username) {
    return Jwts.builder()
        .setIssuer(APP_NAME)
        .setSubject(username)
        .setIssuedAt(generateCurrentDate())
        .setExpiration(generateExpirationDate())
        .signWith(SIGNATURE_ALGORITHM, base64EncodeSecretKey(jwtSecurityProps.getSecret()))
        .compact();
  }

  private Claims getClaimsFromToken(String token) {
    Claims claims;
    try {
      claims = Jwts.parser()
          .setSigningKey(base64EncodeSecretKey(jwtSecurityProps.getSecret()))
          .parseClaimsJws(token)
          .getBody();
    } catch (Exception e) {
      claims = null;
      log.error("Error getting claims from jwt token due to " + e.getMessage(), e);
    }
    return claims;
  }

  /**
   * Find a specific HTTP cookie in a request.
   *
   * @param request
   *            The HTTP request object.
   * @param name
   *            The cookie name to look for.
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
    return DateTime.now().getMillis();
  }

  public String getToken(HttpServletRequest request) {
    Cookie authCookie = getCookieValueByName(request, jwtSecurityProps.getCookieStrategy().getCookie());
    if ( authCookie != null )
      return authCookie.getValue();

    String authHeader = request.getHeader(jwtSecurityProps.getHeaderStrategy().getHeader());
    if ( authHeader != null && authHeader.startsWith("Bearer "))
      return authHeader.substring(7);

    return null;
  }

  public String getUsernameFromToken(String token) {
    String username;
    try {
      final Claims claims = getClaimsFromToken(token);
      username = claims.getSubject();
    } catch (Exception e) {
      username = null;
      log.error("Error getting claims from jwt token due to " + e.getMessage(), e);
    }
    return username;
  }

  public String refreshToken(String token) {
    String refreshedToken;
    try {
      final Claims claims = getClaimsFromToken(token);
      claims.setIssuedAt(generateCurrentDate());
      refreshedToken = generateToken(claims);
    } catch (Exception e) {
      log.error("Error refreshing jwt token due to " + e.getMessage(), e);
      refreshedToken = null;
    }
    return refreshedToken;
  }

  public void setHeader(HttpServletResponse response, String token) {
    response.addHeader(jwtSecurityProps.getHeaderStrategy().getHeader(), "Bearer " + token);
  }
}

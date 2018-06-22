package it.fabioformosa.quartzmanager.security;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT Temporary disabled
 * 
 * @author Fabio.Formosa
 *
 */

//@Component
public class TokenHelper {

  @Value("${app.name}")
  private String APP_NAME;

  @Value("${jwt.secret}")
  private String SECRET;

  @Value("${jwt.expires_in}")
  private int EXPIRES_IN;

  @Value("${jwt.header}")
  private String AUTH_HEADER;

  @Value("${jwt.cookie}")
  private String AUTH_COOKIE;

  @Autowired
  UserDetailsService userDetailsService;

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

  public String generateToken(String username) {
    return Jwts.builder()
        .setIssuer( APP_NAME )
        .setSubject(username)
        .setIssuedAt(generateCurrentDate())
        .setExpiration(generateExpirationDate())
        .signWith( SIGNATURE_ALGORITHM, SECRET )
        .compact();
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

  public String getToken( HttpServletRequest request ) {
    /**
     *  Getting the token from Cookie store
     */
    Cookie authCookie = getCookieValueByName( request, AUTH_COOKIE );
    if ( authCookie != null )
      return authCookie.getValue();
    /**
     *  Getting the token from Authentication header
     *  e.g Bearer your_token
     */
    String authHeader = request.getHeader(AUTH_HEADER);
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
      refreshedToken = null;
    }
    return refreshedToken;
  }

  private Date generateCurrentDate() {
    return new Date(getCurrentTimeMillis());
  }

  private Date generateExpirationDate() {

    return new Date(getCurrentTimeMillis() + EXPIRES_IN * 1000);
  }

  private Claims getClaimsFromToken(String token) {
    Claims claims;
    try {
      claims = Jwts.parser()
          .setSigningKey(SECRET)
          .parseClaimsJws(token)
          .getBody();
    } catch (Exception e) {
      claims = null;
    }
    return claims;
  }

  private long getCurrentTimeMillis() {
    return DateTime.now().getMillis();
  }

  String generateToken(Map<String, Object> claims) {
    return Jwts.builder()
        .setClaims(claims)
        .setExpiration(generateExpirationDate())
        .signWith( SIGNATURE_ALGORITHM, SECRET )
        .compact();
  }
}

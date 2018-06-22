package it.fabioformosa.quartzmanager.configuration;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import it.fabioformosa.quartzmanager.security.ComboEntryPoint;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationFailureHandler;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationSuccessHandler;
import it.fabioformosa.quartzmanager.security.auth.LogoutSuccess;
import it.fabioformosa.quartzmanager.security.auth.TokenAuthenticationFilter;
import it.fabioformosa.quartzmanager.security.service.impl.CustomUserDetailsService;

/**
 * JWT Temporary disabled
 * 
 * @author Fabio.Formosa
 *
 */

//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigJWT extends WebSecurityConfigurerAdapter {

  @Value("${jwt.cookie}")
  private String TOKEN_COOKIE;

  @Autowired
  private CustomUserDetailsService jwtUserDetailsService;

  // @Autowired
  // private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  @Resource
  private ComboEntryPoint comboEntryPoint;

  @Autowired
  private LogoutSuccess logoutSuccess;

  @Autowired
  private AuthenticationSuccessHandler authenticationSuccessHandler;

  @Autowired
  private AuthenticationFailureHandler authenticationFailureHandler;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder.userDetailsService(jwtUserDetailsService)
    .passwordEncoder(passwordEncoder());

  }

  @Bean
  public TokenAuthenticationFilter jwtAuthenticationTokenFilter() throws Exception {
    return new TokenAuthenticationFilter();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //		http.csrf().ignoringAntMatchers("/api/login", "/api/signup")
    //		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
    http.cors().and().csrf().disable()
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    .exceptionHandling().authenticationEntryPoint(comboEntryPoint).and()
    .addFilterBefore(jwtAuthenticationTokenFilter(), BasicAuthenticationFilter.class)
    .authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage("/api/login")
    .successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
    .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
    .logoutSuccessHandler(logoutSuccess).deleteCookies(TOKEN_COOKIE);

  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }

}

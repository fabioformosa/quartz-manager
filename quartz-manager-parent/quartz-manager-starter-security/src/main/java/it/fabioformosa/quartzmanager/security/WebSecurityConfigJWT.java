package it.fabioformosa.quartzmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabioformosa.quartzmanager.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.security.helpers.LoginConfigurer;
import it.fabioformosa.quartzmanager.security.helpers.impl.*;
import it.fabioformosa.quartzmanager.security.properties.InMemoryAccountProperties;
import it.fabioformosa.quartzmanager.security.properties.JwtSecurityProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static it.fabioformosa.quartzmanager.common.config.QuartzManagerPaths.QUARTZ_MANAGER_LOGIN_PATH;
import static it.fabioformosa.quartzmanager.common.config.QuartzManagerPaths.QUARTZ_MANAGER_LOGOUT_PATH;

/**
 * @author Fabio.Formosa
 */
@ComponentScan(basePackages = {"it.fabioformosa.quartzmanager.security"})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigJWT extends WebSecurityConfigurerAdapter {

  private static final String[] PATTERNS_SWAGGER_UI = {"/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"};

  @Value("${server.servlet.context-path:/}")
  private String contextPath;

  @Value("${app.name:quartz-manager}")
  private String appName;

  @Value("${quartz-manager.security.login-model.form-login-enabled:true}")
  private Boolean formLoginEnabled;
  @Value("${quartz-manager.security.login-model.userpwd-filter-enabled:false}")
  private Boolean userpwdFilterEnabled;

  @Autowired
  private JwtSecurityProperties jwtSecurityProps;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private InMemoryAccountProperties inMemoryAccountProps;


  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    configureInMemoryAuthentication(authenticationManagerBuilder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable() //
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //
      .exceptionHandling().authenticationEntryPoint(restAuthEntryPoint()).and() //
      .addFilterBefore(jwtAuthenticationTokenFilter(), BasicAuthenticationFilter.class) //
      .authorizeRequests().anyRequest().authenticated();

    QuartzManagerHttpSecurity.from(http).withLoginConfigurer(loginConfigurer(), logoutConfigurer()) //
      .login(QUARTZ_MANAGER_LOGIN_PATH, authenticationManager()).logout(QUARTZ_MANAGER_LOGOUT_PATH);

    // temporary disabled csfr
    //    http.csrf().ignoringAntMatchers("/api/login", "/api/signup") //
    //    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()//
      .antMatchers(HttpMethod.GET, PATTERNS_SWAGGER_UI) //
      .antMatchers(HttpMethod.GET, QuartzManagerPaths.WEBJAR_PATH + "/css/**", QuartzManagerPaths.WEBJAR_PATH + "/js/**", QuartzManagerPaths.WEBJAR_PATH + "/img/**", QuartzManagerPaths.WEBJAR_PATH + "/lib/**", QuartzManagerPaths.WEBJAR_PATH + "/assets/**");
  }

  private void configureInMemoryAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    if (inMemoryAccountProps.isEnabled() && inMemoryAccountProps.getUsers() != null && !inMemoryAccountProps.getUsers().isEmpty()) {
      InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryAuth = authenticationManagerBuilder.inMemoryAuthentication();
      inMemoryAccountProps.getUsers()
        .forEach(u -> inMemoryAuth
          .withUser(u.getName())
          .password(encoder.encode(u.getPassword()))
          .roles(u.getRoles().toArray(new String[0])));
    }
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }

  public LoginConfigurer formLoginConfigurer() {
    JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler();
    AuthenticationSuccessHandler authenticationSuccessHandler = new AuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
    AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler();
    LoginConfigurer loginConfigurer = new FormLoginConfig(authenticationSuccessHandler, authenticationFailureHandler);
    return loginConfigurer;
  }

  @Bean
  public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
    JwtTokenHelper jwtTokenHelper = jwtTokenHelper();
    JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler = new JwtAuthenticationSuccessHandlerImpl(contextPath, jwtSecurityProps, jwtTokenHelper, objectMapper);
    return jwtAuthenticationSuccessHandler;
  }

//  @Bean
  public JwtTokenAuthenticationFilter jwtAuthenticationTokenFilter() {
    return new JwtTokenAuthenticationFilter(jwtTokenHelper(), userDetailsService);
  }

  @Bean
  public JwtTokenHelper jwtTokenHelper() {
    return new JwtTokenHelper(appName, jwtSecurityProps);
  }

  public LoginConfigurer loginConfigurer() {
    if (BooleanUtils.isTrue(userpwdFilterEnabled))
      return userpwdFilterLoginConfigurer();
    if (BooleanUtils.isNotFalse(formLoginEnabled))
      return formLoginConfigurer();
    throw new RuntimeException("No login configurer enabled!");
  }

  public LogoutSuccess logoutConfigurer() {
    return new LogoutSuccess(objectMapper);
  }

  @Bean
  public AuthenticationEntryPoint restAuthEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
  }

  @Bean
  @Override
  public UserDetailsService userDetailsServiceBean() throws Exception {
    return super.userDetailsServiceBean();
  }

  public LoginConfigurer userpwdFilterLoginConfigurer() {
    LoginConfigurer loginConfigurer = new JwtUsernamePasswordFiterLoginConfig(jwtAuthenticationSuccessHandler());
    return loginConfigurer;
  }

}

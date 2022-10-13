package it.fabioformosa.quartzmanager.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.api.security.helpers.LoginConfigurer;
import it.fabioformosa.quartzmanager.api.security.helpers.impl.*;
import it.fabioformosa.quartzmanager.api.security.properties.InMemoryAccountProperties;
import it.fabioformosa.quartzmanager.api.security.properties.JwtSecurityProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_LOGIN_PATH;
import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.QUARTZ_MANAGER_LOGOUT_PATH;

/**
 * @author Fabio.Formosa
 */
@ComponentScan(basePackages = {"it.fabioformosa.quartzmanager.security"})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigJWT {

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
  private InMemoryAccountProperties inMemoryAccountProps;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

//  @Bean
//  public AuthenticationManager authManager(HttpSecurity http, UserDetailsService userDetailsService)
//    throws Exception {
//    return http.getSharedObject(AuthenticationManagerBuilder.class)
//      .userDetailsService(userDetailsService)
////      .passwordEncoder(bCryptPasswordEncoder)
//      .passwordEncoder(new BCryptPasswordEncoder())
//      .and()
//      .build();
//  }

  @Bean
  public InMemoryUserDetailsManager  configureInMemoryAuthentication() throws Exception {
    List<UserDetails> users = new ArrayList<>();
    if (inMemoryAccountProps.isEnabled() && inMemoryAccountProps.getUsers() != null && !inMemoryAccountProps.getUsers().isEmpty()) {
      users = inMemoryAccountProps.getUsers().stream()
        .map(u -> User.withDefaultPasswordEncoder()
          .username(u.getName())
          .password(u.getPassword())
          .roles(u.getRoles().toArray(new String[0]))
          .build()).collect(Collectors.toList());
    }
    return new InMemoryUserDetailsManager(users);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, InMemoryUserDetailsManager userDetailsService, AuthenticationManager authenticationManager) throws Exception {
    http.csrf().disable() //
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //
      .exceptionHandling().authenticationEntryPoint(restAuthEntryPoint()).and() //
      .addFilterBefore(jwtAuthenticationTokenFilter(userDetailsService), BasicAuthenticationFilter.class) //
      .authorizeRequests();

    QuartzManagerHttpSecurity.from(http).withLoginConfigurer(loginConfigurer(), logoutConfigurer()) //
      .login(QUARTZ_MANAGER_LOGIN_PATH, authenticationManager).logout(QUARTZ_MANAGER_LOGOUT_PATH);

    http.authorizeRequests().anyRequest().authenticated();

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) ->
      web.ignoring()//
        .antMatchers(HttpMethod.GET, PATTERNS_SWAGGER_UI) //
        .antMatchers(HttpMethod.GET, QuartzManagerPaths.WEBJAR_PATH + "/css/**", QuartzManagerPaths.WEBJAR_PATH + "/js/**", QuartzManagerPaths.WEBJAR_PATH + "/img/**", QuartzManagerPaths.WEBJAR_PATH + "/lib/**", QuartzManagerPaths.WEBJAR_PATH + "/assets/**");
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
  public JwtTokenAuthenticationFilter jwtAuthenticationTokenFilter(UserDetailsService userDetailsService) throws Exception {
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

  public LoginConfigurer userpwdFilterLoginConfigurer() {
    LoginConfigurer loginConfigurer = new JwtUsernamePasswordFiterLoginConfig(jwtAuthenticationSuccessHandler());
    return loginConfigurer;
  }

}

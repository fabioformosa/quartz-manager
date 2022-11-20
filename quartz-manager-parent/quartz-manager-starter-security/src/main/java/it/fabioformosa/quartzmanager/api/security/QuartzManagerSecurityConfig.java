package it.fabioformosa.quartzmanager.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.api.security.helpers.LoginConfigurer;
import it.fabioformosa.quartzmanager.api.security.helpers.impl.*;
import it.fabioformosa.quartzmanager.api.security.properties.InMemoryAccountProperties;
import it.fabioformosa.quartzmanager.api.security.properties.JwtSecurityProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths.*;

/**
 * @author Fabio.Formosa
 */

@ComponentScan(basePackages = {"it.fabioformosa.quartzmanager.api.security"})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class QuartzManagerSecurityConfig {

  private static final String[] PATTERNS_SWAGGER_UI = {"/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"};
  public static final String QUARTZ_MANAGER_API_ANT_MATCHER = QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/**";
  public static final String QUARTZ_MANAGER_UI_ANT_MATCHER = QuartzManagerPaths.WEBJAR_PATH + "/**";

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

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder quartzManagerPasswordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean(name = "quartzManagerInMemoryAuthentication")
  public InMemoryUserDetailsManager  configureInMemoryAuthentication(InMemoryAccountProperties inMemoryAccountProps, PasswordEncoder quartzManagerPasswordEncoder) throws Exception {
    List<UserDetails> users = new ArrayList<>();
    if (inMemoryAccountProps.isEnabled() && inMemoryAccountProps.getUsers() != null && !inMemoryAccountProps.getUsers().isEmpty()) {
      users = inMemoryAccountProps.getUsers().stream()
        .map(u -> User
          .withUsername(u.getUsername())
          .password(quartzManagerPasswordEncoder.encode(u.getPassword()))
          .roles(u.getRoles().toArray(new String[0]))
          .build()).collect(Collectors.toList());
    }
    return new InMemoryUserDetailsManager(users);
  }

  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Bean(name = "quartzManagerFilterChain")
  public SecurityFilterChain filterChain(HttpSecurity http,
                                         @Qualifier("quartzManagerInMemoryAuthentication") InMemoryUserDetailsManager userDetailsService,
                                         AuthenticationManager authenticationManager) throws Exception {
    http.antMatcher(QUARTZ_MANAGER_API_ANT_MATCHER).csrf().disable() //
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //
      .exceptionHandling().authenticationEntryPoint(restAuthEntryPoint()).and() //
      .addFilterBefore(jwtAuthenticationTokenFilter(userDetailsService), BasicAuthenticationFilter.class) //
      .authorizeRequests();

    QuartzManagerHttpSecurity.from(http).withLoginConfigurer(loginConfigurer(), logoutConfigurer()) //
      .login(QUARTZ_MANAGER_LOGIN_PATH, authenticationManager).logout(QUARTZ_MANAGER_LOGOUT_PATH);

    http.authorizeRequests()
      .antMatchers(QUARTZ_MANAGER_API_ANT_MATCHER).authenticated();

    return http.build();
  }

  @Bean(name = "quartzManagerWebSecurityCustomizer")
  public WebSecurityCustomizer webSecurityCustomizer(@Value("${quartz-manager.oas.enabled:false}") Boolean oasEnabled) {
    return web -> {
      web.ignoring()//
        .antMatchers(HttpMethod.GET, QUARTZ_MANAGER_UI_ANT_MATCHER);
      if(BooleanUtils.isNotFalse(oasEnabled))
        web.ignoring()
          .antMatchers(HttpMethod.GET, PATTERNS_SWAGGER_UI);
    };
  }

  public LoginConfigurer formLoginConfigurer() {
    JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler();
    AuthenticationSuccessHandler authenticationSuccessHandler = new AuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
    AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler();
    return new FormLoginConfig(authenticationSuccessHandler, authenticationFailureHandler);
  }

  @Bean(name = "quartzManagerJwtAuthenticationSuccessHandler")
  public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
    JwtTokenHelper jwtTokenHelper = jwtTokenHelper();
    return new JwtAuthenticationSuccessHandlerImpl(contextPath, jwtSecurityProps, jwtTokenHelper, objectMapper);
  }

  public JwtTokenAuthenticationFilter jwtAuthenticationTokenFilter(UserDetailsService userDetailsService) {
    return new JwtTokenAuthenticationFilter(jwtTokenHelper(), userDetailsService);
  }

  @Bean(name = "quartzManagerJwtTokenHelper")
  public JwtTokenHelper jwtTokenHelper() {
    return new JwtTokenHelper(appName, jwtSecurityProps);
  }

  public LoginConfigurer loginConfigurer() {
    if (BooleanUtils.isTrue(userpwdFilterEnabled))
      return userpwdFilterLoginConfigurer();
    if (BooleanUtils.isNotFalse(formLoginEnabled))
      return formLoginConfigurer();
    throw new IllegalStateException("No login configurer enabled!");
  }

  public LogoutSuccess logoutConfigurer() {
    return new LogoutSuccess(objectMapper);
  }

  @Bean(name = "quartzManagerRestAuthEntryPoint")
  public AuthenticationEntryPoint restAuthEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
  }

  public LoginConfigurer userpwdFilterLoginConfigurer() {
    return new JwtUsernamePasswordFiterLoginConfig(jwtAuthenticationSuccessHandler());
  }

}

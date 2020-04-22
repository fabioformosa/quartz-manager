package it.fabioformosa.quartzmanager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import it.fabioformosa.quartzmanager.configuration.helpers.LoginConfig;
import it.fabioformosa.quartzmanager.configuration.properties.InMemoryAccountProperties;
import it.fabioformosa.quartzmanager.security.auth.LogoutSuccess;
import it.fabioformosa.quartzmanager.security.auth.TokenAuthenticationFilter;

/**
 *
 * @author Fabio.Formosa
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigJWT extends WebSecurityConfigurerAdapter {

  private static final String[] PATTERNS_SWAGGER_UI = {"/swagger-ui.html", "/v2/api-docs", "/swagger-resources/**", "/webjars/**"};

  @Value("${quartz-manager.security.jwt.cookie}")
  private String TOKEN_COOKIE;

  //	@Autowired
  //	private CustomUserDetailsService jwtUserDetailsService;

  @Autowired
  private LogoutSuccess logoutSuccess;

  @Autowired
  private LoginConfig loginConfig;

  @Autowired
  private InMemoryAccountProperties inMemoryAccountProps;

  //	@Bean
  //	@Override
  //	public AuthenticationManager authenticationManagerBean() throws Exception {
  //		return super.authenticationManagerBean();
  //	}

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)throws Exception {
    configureInMemoryAuthentication(authenticationManagerBuilder);
    //		authenticationManagerBuilder.userDetailsService(jwtUserDetailsService)
    //		.passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //		http.csrf().ignoringAntMatchers("/api/login", "/api/signup") //
    //		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //

    http.csrf().disable() //
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //
    .exceptionHandling().authenticationEntryPoint(restAuthEntryPoint()).and() //
    .addFilterBefore(jwtAuthenticationTokenFilter(), BasicAuthenticationFilter.class) //
    .authorizeRequests().anyRequest().authenticated();

    loginConfig.login(http, authenticationManager()).logout().logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
    .logoutSuccessHandler(logoutSuccess).deleteCookies(TOKEN_COOKIE);

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()//
    .antMatchers(HttpMethod.GET, PATTERNS_SWAGGER_UI) //
    .antMatchers(HttpMethod.GET,"/css/**", "/js/**", "/img/**", "/lib/**");
  }

  private void configureInMemoryAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    if(inMemoryAccountProps.isEnabled() && inMemoryAccountProps.getUsers() != null && !inMemoryAccountProps.getUsers().isEmpty()) {
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

  @Bean
  public TokenAuthenticationFilter jwtAuthenticationTokenFilter() throws Exception {
    return new TokenAuthenticationFilter();
  }

  //	@Bean
  //	public PasswordEncoder passwordEncoder() {
  //		return new BCryptPasswordEncoder();
  //	}

  @Bean
  public AuthenticationEntryPoint restAuthEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
  }

  @Bean
  @Override
  public UserDetailsService userDetailsServiceBean() throws Exception {
    return super.userDetailsServiceBean();
  }

}

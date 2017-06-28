package oxchains.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import oxchains.invoice.auth.AuthError;
import oxchains.invoice.auth.JwtAuthenticationProvider;
import oxchains.invoice.auth.JwtTokenFilter;

/**
 * @author aiet
 */
@EnableWebSecurity
@Configuration
public class EInvoiceAppConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthError authError;

    public EInvoiceAppConfiguration(@Autowired JwtTokenFilter jwtTokenFilter, @Autowired JwtAuthenticationProvider jwtAuthenticationProvider, @Autowired AuthError authError) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.authError = authError;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
          .cors()
          .and()
          .csrf()
          .disable()
          .authorizeRequests()
          .antMatchers("/token", "/user", "/company")
          .permitAll()
          .antMatchers("/**/*")
          .authenticated()

          .and()
          .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
          .exceptionHandling()
          .authenticationEntryPoint(authError)
          .accessDeniedHandler(authError);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    /**
     * allow cross origin requests
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                  .addMapping("/**")
                  .allowedOrigins("*")
                  .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE")
                  .allowedHeaders("*");
            }
        };
    }
}

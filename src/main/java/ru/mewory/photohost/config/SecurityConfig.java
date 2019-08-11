package ru.mewory.photohost.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/record").hasRole("USER")
//                .antMatchers("/report").hasRole("USER")
                .antMatchers("/report").permitAll()
                .antMatchers("/").hasRole("USER")
                .antMatchers("/test").permitAll()
                .antMatchers("/instaload").hasRole("USER")
                .antMatchers("/journal").hasRole("USER")
                .antMatchers("/dictionaries").hasRole("ADMIN")
                .antMatchers("/parseInstagram").hasRole("USER")
                .antMatchers("/vkload").hasRole("USER")
                .antMatchers("/savePost").hasRole("USER")
                .antMatchers("/instagramloader").hasRole("USER")
                .antMatchers(HttpMethod.POST,"/sendRecord").hasRole("USER")
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/", true)
                .and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}123321").roles("USER", "ADMIN");
        auth.inMemoryAuthentication().withUser("normal").password("{noop}123321").roles("USER");
    }
}

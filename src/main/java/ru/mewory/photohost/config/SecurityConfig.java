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
                .antMatchers("/parseInstagram").hasRole("USER")
                .antMatchers("/vkload").hasRole("USER")
                .antMatchers("/savePost").hasRole("USER")
                .antMatchers(HttpMethod.POST,"/sendRecord").hasRole("USER")
                .and()
                .formLogin().loginPage("/login").successForwardUrl("/record")
                .and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("root").password("zaruba").roles("USER","ADMIN");
        auth.inMemoryAuthentication().withUser("user").password("123321").roles("USER");
        auth.inMemoryAuthentication().withUser("space").password("getlost").roles("USER");
    }
}
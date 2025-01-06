package org.example.sellingcourese.Config;

import jakarta.servlet.http.Cookie;
import org.example.sellingcourese.JWT.JwtAuthenticationFilter;
import org.example.sellingcourese.Service.AccountService;
import org.example.sellingcourese.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Lazy
    @Autowired
    private AccountService accountService;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;

    }
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new DefaultOAuth2UserService();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**","/public/orders/**").permitAll()
                        .requestMatchers("/**").permitAll()// Cho phép tất cả yêu cầu tới /public/** mà không cần JWT
                        .anyRequest().authenticated()  // Các yêu cầu khác yêu cầu xác thực
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .successHandler((request, response, authentication) -> {
//                            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//                            String email = oauth2User.getAttribute("email");
//                            String name = oauth2User.getAttribute("name");
//
//                            // Tạo token JWT
//                            String token = accountService.generateToken(email);
//                            accountService.processOAuthPostLogin(email, name);
//
//                            // Đặt token trong HttpOnly Cookie
//                            Cookie cookie = new Cookie("authToken", token);
//                            cookie.setHttpOnly(true);
//                            cookie.setSecure(false); // Nếu chạy HTTPS thì set true
//                            cookie.setPath("/");
//                            cookie.setMaxAge(24 * 60 * 60); // Thời gian sống 1 ngày
//                            response.addCookie(cookie);
//
//                            // Chuyển hướng đến frontend mà không kèm token trên URL
//                            response.sendRedirect("http://localhost:3000/home"+"?token="+token);
//                        })
//                        .failureUrl("/public/auth/oauth2/failure")
//                )



                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Áp dụng cấu hình CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT filter

        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000","https://webcoursereact.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));  // Thêm header này
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}

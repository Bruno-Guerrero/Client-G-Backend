package org.guerrero.bruno.clientg.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfiguration {

    private static final String[] AUTH_WHITELIST ={

            // -- Swagger
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",


            // -- Login
            "/countbl/users/login/**",

            // -- Registro para admin
            "/countbl/users/register/**",
            // -- Registro para clientes
            "/countbl/clients/register/**",


    };


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*

    1. Cuales van a ser los Request(pedidos) que seran evaluados para saber si el usuario tiene permisos sobre estos request
        a. AnyRequest -> Todos los pedidos
        b. RequestMatcher -> Se evalua solo los que coincidan con las rutas especificadas
        c. RequestMatcher + HttpMethod -> Se evalua solo los que coincidan con las rutas especificadas y con el metodo Http (GET, POST, etc.)

    2. Cual es la regla de autorizacion que se va a aplicar sobre estos Request(pedidos)
        a. permitAll()
        b. denyAll()
        c. hasAnyAuthority()
        d. hasAuthority()
        e. hasRole()
        f. hasAnyRole()
        g. SpEL -> Spring Expression Language
        h. authenticated()

     */


    @Autowired
    JwtRequestFilter jwtRequestFilter;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(

                (auth) -> auth
                        //.anyRequest().permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(AUTH_WHITELIST).permitAll()

                        //permisos customers
                        .requestMatchers(HttpMethod.GET,"/seedair/customers/getByUserId/**").hasAnyAuthority("CUSTOMER","ADMIN")

                        //permisos reservations
                        .requestMatchers(HttpMethod.GET,"/seedair/reservations/active/**").hasAnyAuthority("CUSTOMER")
                        //
                        .requestMatchers(HttpMethod.GET,"/seedair/reservations/list/**").hasAnyAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.GET,"/seedair/reservations/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/seedair/reservations/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/seedair/reservations/register**").hasAnyAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE,"/seedair/reservations/**").hasAnyAuthority("ADMIN")
                        //
                        //permisos parcels
                        .requestMatchers(HttpMethod.POST,"/seedair/parcels/**").hasAnyAuthority("ADMIN","CUSTOMER")
                        //permisos reviews
                        .requestMatchers(HttpMethod.POST,"/seedair/reviews/**").hasAnyAuthority("ADMIN","CUSTOMER")
                        //permisos drones
                        .requestMatchers(HttpMethod.GET,"/seedair/drones/available").hasAnyAuthority("ADMIN", "CUSTOMER")
                        .requestMatchers(HttpMethod.POST,"/seedair/drones/available-by-dates").hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,"/seedair/drones/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/seedair/drones/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/seedair/drones/**").hasAnyAuthority("ADMIN")
                        //permisos maintenances
                        .requestMatchers(HttpMethod.POST,"/seedair/maintenances/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/seedair/maintenances/**").hasAnyAuthority("ADMIN")
                        //permisos operators
                        .requestMatchers(HttpMethod.GET,"/seedair/operators/available/**").hasAnyAuthority("ADMIN","CUSTOMER")
                        .requestMatchers(HttpMethod.GET,"/seedair/operators/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/seedair/operators/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/seedair/operators/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/seedair/operators/update/**").hasAnyAuthority("ADMIN")
                        //permisos payments
                        .requestMatchers(HttpMethod.PUT,"/seedair/payments/**").hasAnyAuthority("ADMIN")
                        //permisos droneModels
                        .requestMatchers(HttpMethod.POST,"/seedair/droneModel/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/seedair/droneModel/**").hasAnyAuthority("ADMIN")
                        //permisos customer
                        .requestMatchers(HttpMethod.GET,"/seedair/customers/**").hasAnyAuthority("ADMIN")
                        //permisos droneBrands
                        .requestMatchers(HttpMethod.GET,"/seedair/brands/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/seedair/brands/**").hasAnyAuthority("ADMIN")

                        .anyRequest().authenticated()

        );

        http.sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

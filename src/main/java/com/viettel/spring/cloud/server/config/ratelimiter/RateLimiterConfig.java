// package com.viettel.spring.cloud.server.config.ratelimiter;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
// import org.springframework.context.annotation.Bean;
// import org.springframework.http.HttpHeaders;
// import org.springframework.stereotype.Component;

// import com.viettel.spring.cloud.server.security.JwtTokenService;

// import lombok.RequiredArgsConstructor;
// import reactor.core.publisher.Mono;

// @Component
// @RequiredArgsConstructor
// public class RateLimiterConfig {
//     @Autowired
//     private final JwtTokenService jwtTokenService;

//     @Bean
//     public KeyResolver userKeyResolver() {
//         return exchange -> {
//             String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//             if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                 String token = authHeader.substring(7);
//                 try {
//                     // ✅ Extract username from JWT (use your actual JWT lib here)
//                     String username = jwtTokenService.extractUsername(token); // implement this
//                     return Mono.just(username != null ? username : "anonymous");
//                 } catch (Exception e) {
//                     return Mono.just("invalid-token");
//                 }
//             }
//             return Mono.just("anonymous");
//         };
//     }
// }

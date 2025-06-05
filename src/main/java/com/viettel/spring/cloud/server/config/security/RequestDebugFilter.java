// package com.viettel.spring.cloud.server.config.security;

// import org.springframework.core.Ordered;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;

// import jakarta.servlet.Filter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
// import jakarta.servlet.http.HttpServletRequest;
// import java.io.IOException;

// @Component
// @Order(Ordered.HIGHEST_PRECEDENCE)
// public class RequestDebugFilter implements Filter {
    
//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//             throws IOException, ServletException {
        
//         HttpServletRequest httpRequest = (HttpServletRequest) request;
//         System.out.println("=== REQUEST DEBUG ===");
//         System.out.println("URI: " + httpRequest.getRequestURI());
//         System.out.println("Method: " + httpRequest.getMethod());
//         System.out.println("User-Agent: " + httpRequest.getHeader("User-Agent"));
//         System.out.println("Authorization: " + httpRequest.getHeader("Authorization"));
//         System.out.println("Remote Addr: " + httpRequest.getRemoteAddr());
//         System.out.println("=====================");
        
//         chain.doFilter(request, response);
//     }
// }
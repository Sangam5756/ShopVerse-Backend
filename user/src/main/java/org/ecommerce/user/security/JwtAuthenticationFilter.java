package org.ecommerce.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@AllArgsConstructor

public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;


    @Override
    protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException{

//        first will read the AuthorizationHeader
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); //this will give header
        String header = request.getHeader("Authorization");
        System.out.println("HEADER = " + header);

        if(authHeader == null || !authHeader.startsWith("Bearer ")){

            filterChain.doFilter(request,response); //no token continue
            return;
        }

        String token = authHeader.substring(7); //removed starting bearer plus one space so 6 +1 =7
        String email = jwtService.extractEmail(token);

//        check if token is valid or token is not validated
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            if(jwtService.isValid(token)){

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,null,null);
                //did not get this
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);



            }
        }

//        3 process
        filterChain.doFilter(request, response);

    }


}

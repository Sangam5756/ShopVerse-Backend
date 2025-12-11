package org.ecommerce.user.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.ecommerce.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;


@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private Long expirationMs;


    public String generateToken(User user){
//in jwts it only expect the date not localDate
        Date now = new Date();
        Date exp = new Date(now.getTime()+expirationMs); //see here will add the current time plus future expiration time

//        here we builded the security token
        return Jwts.builder().
                setSubject(user.getEmail()).
                claim("role",user.getRole().name())
                .claim("fullname",user.getFullName())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractFullName(String token) {
        return parseClaims(token).get("fullName", String.class);
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }


    //    here this function extract the email from token
    public String extractEmail(String token){
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token){
        try{
            parseClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }


//    parsedClaims method
    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }





}

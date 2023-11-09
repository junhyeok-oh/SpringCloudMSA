package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

//    private AuthenticationManager authenticationManager;
    private final UserService userService;
    private final Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException {
        try {
            // Request를 request.getInputStream()으로 받고, RequestLogin 형태로 변경
            // why? POST 방식의 Request은 getInputStream()으로 바꿔주어야 처리할 수 있다.
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            // UsernamePasswordAuthenticationToken으로 Email과 Password를 Token으로 바꿔준다.
            // Token을 getAuthenticationManager에 넘겨서 Email과 Password를 비교해서 인증처리
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
//        String username = ((User)authResult.getPrincipal()).getUsername();
//        UserDto userDetails = userService.getUserDetailsByEmail(username);
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        String username = userDetails.getUsername();

        UserDto userDto = userService.getUserDetailsByEmail(username);
        // user_id 토큰화
        String token = Jwts.builder()
                .setSubject(userDto.getUserId()) // 토큰화 대상
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time")))) // 만료 기간
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret")) // 토큰화 알고리즘
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
    }
}

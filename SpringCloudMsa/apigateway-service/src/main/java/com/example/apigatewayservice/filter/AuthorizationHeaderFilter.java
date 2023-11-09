package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private Environment env;


    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class); // 부모 클래스한테 사용하는 Config 정보 알려주기
        this.env = env;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            if(!isJwtValid(jwt)){
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }
    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        try {
            // 토큰을 풀면, 'sub : user_id' 이런 식으로 저장되어 있다. 이 sub를 가져온다.
            // 가져오기 위해서 토큰화 했던 키값(token.secret)을 넣어준다.
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch (Exception e){
            returnValue = false;
        }

        if(subject == null || subject.isEmpty()){
            returnValue = false;
        }

        // user_id인 subject와 DB에 저장된 user_id와 비교?


        return returnValue;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error_msg, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(error_msg);
        return response.setComplete();
    }
}

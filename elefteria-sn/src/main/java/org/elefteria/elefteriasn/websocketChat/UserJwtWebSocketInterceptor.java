package org.elefteria.elefteriasn.websocketChat;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import org.elefteria.elefteriasn.security.jwt.JwtConfig;
import org.elefteria.elefteriasn.security.jwt.JwtTokenVerifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import javax.security.sasl.AuthenticationException;

public class UserJwtWebSocketInterceptor implements ChannelInterceptor {
    private SecretKey secretKey;
    private JwtConfig jwtConfig;

    public UserJwtWebSocketInterceptor(SecretKey secretKey, JwtConfig jwtConfig) {
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        //get headers from message
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(accessor.getCommand().equals(StompCommand.CONNECT)){
            String authHeader = accessor.getNativeHeader(jwtConfig.getAuthorizationHeader()).get(0);

            if(Strings.isNullOrEmpty(authHeader) || !authHeader.startsWith(jwtConfig.getTokenPrefix())){
                throw new AuthenticationException("Wrong token!");
            }

            // get jwt token from header
            String token = authHeader.split(" ")[1];

            // get authentication from method that parse jwt token
            Authentication authentication = JwtTokenVerifier.getAuthenticationFromToken(token, secretKey);

            // set user
            accessor.setUser(authentication);
        }

        return message;
    }
}

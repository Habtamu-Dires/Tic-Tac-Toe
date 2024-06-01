package com.example.server.websocketconfig;

import com.example.server.jwt.JwtUtil;
import com.example.server.token.TokenRepository;
import com.example.server.user.User;
import com.example.server.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHandshakeHandler implements HandshakeHandler {
    private final HandshakeHandler defaultHandshakeHandler = new DefaultHandshakeHandler();

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    @Override
    public boolean doHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               @NonNull Map<String, Object> attributes)  {
        // Check if username or token is null or invalid
        if (isInvalid(request)) {
            System.out.println("it is invalid ");
            return false;
        }
        // Allow the default handshake process to continue
        return defaultHandshakeHandler.doHandshake(request, response, wsHandler, attributes);
    }

    private boolean isInvalid(ServerHttpRequest request) {
        return true;
//        //Extract JWT token from the WebSocket handshake request
//        String token = extractTokenFromUri(request.getURI());
//
//        if(token != null){
//            String username = validate(token);
//            System.out.println("The token from WS " + token);
//            System.out.println("The username " + username);
//            if(username == null) {
//                return false;
//            }
//            return true;
//
//        } else {
//            return false;
//        }

    }

    private String extractTokenFromUri(URI uri) {
        String query = uri.getQuery();
        if (query != null && query.contains("token=")) {
            return query.substring(query.indexOf("token=") + 6);
        }
        return null;
    }

//    private String extractTokenFromHeader(HttpServletRequest request){
//        List<String> authorizationHeader = headers.get("Authorization");
//        if(authorizationHeader != null && !authorizationHeader.isEmpty()){
//            String header = authorizationHeader.getFirst();
//            if(header.startsWith("Bearer ")){
//                return header.substring(7);
//            }
//        } else {
//            System.out.println("Authorization header problem");
//        }
//        return null;
//    }

    private String validate(String token){
        String  username = jwtUtil.getUsernameFromToken(token);
        User user = userService.findUserByUserName(username);
        var isTokenValid = tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);

        if(user != null && isTokenValid && jwtUtil.isTokenValid(token, user.getUsername())){

            return username;
        }
        return null;
    }
}

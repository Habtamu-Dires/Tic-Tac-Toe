package com.example.server.jwt;

import com.example.server.token.Token;
import com.example.server.token.TokenRepository;
import com.example.server.user.User;
import com.example.server.user.UserService;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final UserService userService;

    @Transactional
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
    {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")){
           jwt = authHeader.substring(7);
            var storedToken = tokenRepository.findByToken(jwt);
            if(storedToken.isPresent()){
              Token  existedToken = storedToken.get();
              existedToken.setExpired(true);
              existedToken.setRevoked(true);
              tokenRepository.save(existedToken);

              //update user
                var user = tokenRepository.findUserById(existedToken.getId());
                if(user.isPresent()){
                    User foundUser = user.get();
                    userService.setOnlineStatus(foundUser.getUsername(), false);
                }
            }
        }
    }
}

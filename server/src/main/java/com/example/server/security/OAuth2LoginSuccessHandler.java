package com.example.server.security;

import com.example.server.token.Token;
import com.example.server.token.TokenRepository;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final TokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException
    {
        String redirectUrl = frontendUrl + "?oauth2=true";
        if(authentication.getPrincipal() instanceof OAuth2User){

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String username = oAuth2User.getName();

            //get token
            List<Token> tokens = tokenRepository.findAllValidTokenByUser(username);
            if(tokens.isEmpty()){
               throw new RuntimeException("error");
            }
            String token = tokens.getFirst().getToken();
             redirectUrl  += "?&token=" + token + "&username=" + username + "&oauth2=true";

        } else{
            System.out.println("not success");
            redirectUrl += "?oauth2=true" ;
        }

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(redirectUrl);
        super.onAuthenticationSuccess(request, response, authentication);

    }
}

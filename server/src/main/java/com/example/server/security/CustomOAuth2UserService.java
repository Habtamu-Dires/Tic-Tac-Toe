package com.example.server.security;

import com.example.server.authority.Authority;
import com.example.server.authority.AuthorityService;
import com.example.server.jwt.JwtUtil;
import com.example.server.token.Token;
import com.example.server.token.TokenRepository;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        System.out.println(attributes);
        String name= (String) attributes.get("name");
        User user = saveUser(name);
        return updateOAuth2UserAttributes(oAuth2User, user);

    }

    private OAuth2User updateOAuth2UserAttributes(OAuth2User oAuth2User, User user){

        Map<String, Object> updatedAttributes = new HashMap<>(oAuth2User.getAttributes());

        updatedAttributes.put("username", user.getUsername());
        updatedAttributes.put("authority", user.getAuthorities().stream()
                .map(Authority::getName).toList());

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                updatedAttributes,
                "username");
    }

    private User saveUser(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null){ // lets save the user
            Authority authority = authorityService.getAuthorityByName("USER");
            if( authority == null){
                authority =  authorityService.addAuthority(new Authority(null, "USER"));
            }

            String randPassword = UUID.randomUUID().toString();
            User userToBeSaved = User.builder()
                    .username(username)
                    .firstName(username)
                    .lastName(username)
                    .password(passwordEncoder.encode(randPassword))
                    .authorities(List.of(authority))
                    .build();
            //authenticate
            customAuthentication(userToBeSaved);
            User savedUser =  userRepository.save(userToBeSaved);

            //generate token
            var jwt = jwtUtil.generateToken(savedUser.getUsername());

            saveToken(savedUser, jwt);

            return savedUser;
        }
        // update name in case it is changed
        user.setFirstName(username);
        //generate token
        var jwt = jwtUtil.generateToken(user.getUsername());
        saveToken(user, jwt);
        
        customAuthentication(user);

        return userRepository.save(user);
    }

    private void saveToken(User user, String jwt){
        Token token = Token.builder()
                .token(jwt)
                .user(user)
                .build();

        tokenRepository.save(token);
    }

    private void customAuthentication(User user){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new SecurityUser(user),
                null,
                user.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                        .toList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}

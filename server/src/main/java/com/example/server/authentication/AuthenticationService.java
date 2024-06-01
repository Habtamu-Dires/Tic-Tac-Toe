package com.example.server.authentication;

import com.example.server.authority.Authority;
import com.example.server.authority.AuthorityService;
import com.example.server.jwt.JwtUtil;
import com.example.server.security.CustomPasswordEncoder;
import com.example.server.token.Token;
import com.example.server.token.TokenRepository;
import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.example.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;

    AuthenticationResponse register(RegisterRequest request){
        Authority authority = authorityService.getAuthorityByName("USER");
        if( authority == null){
          authority =  authorityService.addAuthority(new Authority(null, "USER"));
        }
        User user = User.builder()
                .username(request.username())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .onlineStatus(true)
                .authorities(List.of(authority))
                .build();


        var savedUser = userRepository.save(user);

        var jwt = jwtUtil.generateToken(savedUser.getUsername());


        saveToken(savedUser, jwt);

        return new AuthenticationResponse(jwt, savedUser.getUsername());
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),request.password()
                )
        ); //in case the email or password not correct an exception will be thrown
        //so at this point the user is authenticated
        User user = userService.findUserByUserName(request.username());

        //update user online status
//        userService.setOnlineStatus(user.getUsername(),true);

        //revoke all user token
        revokeAllUserToken(user);
        var jwtToken = jwtUtil.generateToken(user.getUsername());

        saveToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken, user.getUsername());
    }

    private void saveToken(User user, String jwt){
        Token token = Token.builder()
                .token(jwt)
                .user(user)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserToken(User user){
        List<Token> allValidTokenByUser
                = tokenRepository.findAllValidTokenByUser(user.getUsername());

        if(allValidTokenByUser.isEmpty()) return;

        allValidTokenByUser.forEach(t -> {
                t.setExpired(true);
                t.setRevoked(true);
        });

        tokenRepository.saveAll(allValidTokenByUser);
    }
}

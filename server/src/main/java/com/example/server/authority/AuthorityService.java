package com.example.server.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public Authority addAuthority(Authority authority){
       return authorityRepository.save(authority);
    }

    public Authority getAuthorityByName (String name) {
        return authorityRepository.findAuthorityByName(name)
                .orElse(null);
    }

}

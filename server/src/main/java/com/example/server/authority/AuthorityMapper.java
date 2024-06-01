package com.example.server.authority;

public class AuthorityMapper {
    //authority to authority dto
    public static AuthorityDTO authorityToDTO(Authority authority){
        return AuthorityDTO.builder()
                .name(authority.getName())
                .build();
    }

    //authorityDTO to authority
    public static Authority DtoToAuthority(AuthorityDTO authorityDTO){
        return Authority.builder()
                .name(authorityDTO.name())
                .build();
    }
}

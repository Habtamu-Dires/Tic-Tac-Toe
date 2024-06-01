package com.example.server.user;

import com.example.server.authority.AuthorityDTO;
import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
public record UserDTO(String firstName,
                        String lastName,
                        String username,
                        String password,
                        List<AuthorityDTO> authorities ) {}

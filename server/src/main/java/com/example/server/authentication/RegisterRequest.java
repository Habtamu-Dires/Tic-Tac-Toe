package com.example.server.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
public record RegisterRequest(
         String firstName,
         String lastName,
         String username,
         String password
) {

}
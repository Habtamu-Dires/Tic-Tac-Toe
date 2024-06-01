package com.example.server.user;

import lombok.Builder;

@Builder
public record UserMessage(String sender,String recipient, String content,
                          String type, String status){ }


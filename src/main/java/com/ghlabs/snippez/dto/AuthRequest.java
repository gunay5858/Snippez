package com.ghlabs.snippez.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {
    private String username;
    private String password;
}

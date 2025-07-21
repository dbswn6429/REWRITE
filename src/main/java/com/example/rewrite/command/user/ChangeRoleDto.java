package com.example.rewrite.command.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRoleDto {
    private Long uid;
    private String role;
}
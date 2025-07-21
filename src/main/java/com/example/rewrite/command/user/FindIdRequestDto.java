package com.example.rewrite.command.user;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class FindIdRequestDto {

    private String id;
    private String email;
    private String name;
    private String phone;
}
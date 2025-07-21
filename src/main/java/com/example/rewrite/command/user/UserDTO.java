package com.example.rewrite.command.user;

import com.example.rewrite.entity.Users;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private Long uid;
    private String id;
    private String nickname;
    private String email;
    private LocalDateTime regDate; // 또는 String 타입
    private String role;

    // 생성자, Getter, Setter 등 필요에 따라 추가
    // (엔티티를 받아 DTO를 생성하는 생성자나 정적 팩토리 메소드가 유용)
    public static UserDTO fromEntity(Users user) {
        UserDTO dto = new UserDTO();
        dto.setUid(user.getUid());
        dto.setId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setRegDate(user.getRegDate());
        dto.setRole(user.getRole() != null ? user.getRole() : null); // Enum 타입이라면 이름으로 변환
        return dto;
    }
}
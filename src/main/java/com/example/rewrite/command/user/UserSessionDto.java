package com.example.rewrite.command.user;

import com.example.rewrite.entity.Users;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// Serializable -> '직렬화'
// 우리는 값을 저장할때, 각각의 메모리 주소에 값을 저장해줌.
// 하지만 다른 곳에서의 메모리 주소는 현재 메모리 주소와는 아예 관계가 없는 다른 곳이기때문에 의미가 없음.
// 고로, 각각 배치된 데이터를 바이트화시켜서 하나의 값으로 만들고, 그걸 '직렬화' 라고 부름
// 한 시스템에서 다른 시스템으로 객체를 그대로 보내게 되면 파싱 불가능
// 바이트화 시켜서 다른 시스템으로 보낼때, 바이트화 된 데이터는 상대측에서 문제없이 파싱받아 같은 객체를 재구성하게 됨.
// 데이터 저장, 네트워크 통신, 세션 관리에 사용함
public class UserSessionDto implements Serializable {

    // 직렬화 버전 UID 추가
    private static final long serialVersionUID = 1L;

    private Long uid;          // 사용자 고유 번호
    private String id;         // 로그인 아이디
    private String name;       // 사용자 이름
    private String nickname;   // 사용자 닉네임
    private String imgUrl;     // 프로필 이미지 URL
    private String role;       // 사용자 권한

    // Users 엔티티 -> 유저세션DTO로 변환하는 메서드
    public static UserSessionDto fromEntity(Users user) {
        return UserSessionDto.builder()
                .uid(user.getUid())
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .imgUrl(user.getImgUrl())
                .role(user.getRole())
                .build();
    }
}

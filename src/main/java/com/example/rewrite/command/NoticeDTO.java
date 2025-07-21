package com.example.rewrite.command;

import com.example.rewrite.entity.Notice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NoticeDTO {

    private Long noticeId;
    private String title;
    private String content;
    private String img;
    private LocalDateTime regDate;


    // @@@DTO.fromEntity(엔티티객체)하면 DTO로 바꿔줌
    public static NoticeDTO fromEntity(Notice notice) {
        // notice가 null이면 null 반환
        if (notice == null) {
            return null;
        }

        // 새 DTO 객체 생성하여 값 복사
        NoticeDTO dto = new NoticeDTO();
        dto.setNoticeId(notice.getNoticeId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setImg(notice.getImg());
        dto.setRegDate(notice.getRegDate());

        return dto;
    }

}

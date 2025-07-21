package com.example.rewrite.entity; // 패키지 경로는 실제 프로젝트에 맞게 조정하세요.

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*; // 또는 Spring Boot 3+ / Jakarta EE 9+ 사용 시 jakarta.persistence.*
import java.time.LocalDateTime; // regDate 컬럼에 권장되는 타입 (현재는 String으로 매핑)

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "QNA") // SQL 테이블 이름과 동일하게 지정
@DynamicInsert
public class Qna {

    @Id // QNA_ID를 기본 키로 가정합니다. SQL에 PRIMARY KEY 제약조건이 명시되지 않았습니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QNA_ID", nullable = false, length = 255) // SQL 정의에 따라 NOT NULL 및 길이 지정
    private Long qnaId; // SQL 타입 VARCHAR -> Java String

    @Column(name = "TITLE", nullable = true, length = 255) // SQL 정의에 따라 nullable 및 길이 지정
    private String title;

    @Column(name = "CONTENT", nullable = true, length = 255)
    // 참고: 내용(content)이 255자를 넘을 수 있다면 SQL 타입을 TEXT 등으로 변경하고
    // @Lob 어노테이션 사용을 고려하세요. 현재는 SQL 정의 그대로 VARCHAR(255)에 맞춥니다.
    private String content;

    @Column(name = "REG_DATE", nullable = true, length = 255)
    // 경고: 날짜/시간 정보를 VARCHAR로 저장하는 것은 권장되지 않습니다.
    // 가능하면 SQL 타입을 DATETIME 또는 TIMESTAMP로 변경하고, Java 타입을 LocalDateTime으로 사용하는 것이 좋습니다.
    // 예: @Column(name = "REG_DATE") private LocalDateTime regDate;
    private String regDate; // SQL 타입 VARCHAR -> Java String (현재 SQL 정의 기준)

    @Column(name = "ANSWER", nullable = true, length = 255)
    private String answer;

    @Column(name = "ANSWER_STAT", nullable = false, length = 255)
    private String answer_stat;

    @Column(name = "CATEGORY", nullable = true, length = 255)
    private String category;

    @Column(name = "USERID", nullable = true, length = 255)
    private String USERID;

    @Column(name = "FILEATTACHMENT", nullable = true, length = 200 )
    private String fileAttachment;

    @Column(name = "uid", nullable = true, length = 255) // SQL 컬럼명 'Key2' 와 동일하게 지정
    private Long uid; // Java 필드명은 camelCase 권장 (key2)

    // 모든 필드를 받는 생성자 (필요에 따라 추가)
    public Qna(Long qnaId, String title, String content, String regDate, String answer, String answer_stat, String category, String userId, Long uid, String fileAttachment) {
        this.qnaId = qnaId;
        this.title = title;
        this.content = content;
        this.regDate = regDate;
        this.answer = answer;
        this.answer_stat = answer_stat;
        this.category = category;
        this.uid = uid;
        this.fileAttachment = fileAttachment;
    }

    // Lombok @NoArgsConstructor 가 기본 생성자를 만들어줍니다.
    // 필요하다면 다른 생성자를 추가할 수 있습니다.
}
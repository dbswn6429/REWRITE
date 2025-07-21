package com.example.rewrite.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "noreply@rewrite.com"; // 발신자 주소 상수화

    // 공통 HTML 템플릿 상단
    private String getHtmlHeader(String title) {
        return "<div style='background-color:#f8f9fa; " +
                "padding:40px 20px; font-family: \"Malgun Gothic\", \"맑은 고딕\", sans-serif;'>" +
                "  <div style='max-width:600px; margin:0 auto; background-color:#ffffff; padding:30px; border-radius:12px; box-shadow: 0 4px 12px rgba(0,0,0,0.08);'>" +
                "    <h1 style='color:#343a40; text-align:center; font-size:24px; margin-bottom:25px; font-weight: 600;'>" + title + "</h1>";
    }

    // 공통 HTML 템플릿 하단
    private String getHtmlFooter() {
        String privacyPolicyUrl = "https://rewrite.o-r.kr/privacy";

        return "    <p style='margin-top:35px; font-size:15px; color:#495057; text-align:center; font-weight: 600;'>" +
                "      ReWrite 팀 드림" +
                "    </p>" +
                "    <hr style='border: none; border-top: 1px solid #e9ecef; margin: 30px 0;'>" +
                "    <p style='font-size:12px; color:#adb5bd; text-align:center; line-height:1.6;'>" +
                "      ⓒ 2025 ReWrite Inc. | 서울시 성북구<br>" +
                "      <a href='" + privacyPolicyUrl + "' style='color:#adb5bd; text-decoration:none;'>개인정보처리방침</a>" +
                "    </p>" +
                "  </div>" +
                "</div>";
    }

    @Override
    @Async // 비동기 실행 (별도 스레드에서 동작)
    public void sendUserIdByEmail(String toEmail, String userId) {
        log.info("[MailService] 아이디 찾기 메일 발송 시작: {}", toEmail);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // MimeMessageHelper 생성 (멀티파트 true, 인코딩 UTF-8)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[Rewrite] 아이디 찾기 결과 안내");
            helper.setFrom(FROM_ADDRESS); // 발신자 설정

            // HTML 본문 생성
            String htmlContent = getHtmlHeader("아이디 찾기 결과 안내") +
                    "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 20px;'>" +
                    "      안녕하세요." +
                    "    </p>" +
                    "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 25px;'>" +
                    "      요청하신 계정의 아이디는 <strong style='color:#20c997; font-size: 17px;'>[ " + userId + " ]</strong> 입니다." +
                    "    </p>" +
                    "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-top: 30px;'>" +
                    "      서비스를 이용해주셔서 감사합니다." +
                    "    </p>" +
                    getHtmlFooter();

            helper.setText(htmlContent, true); // true: HTML 메일임을 명시

            mailSender.send(mimeMessage);
            log.info("[MailService] 아이디 찾기 메일 발송 성공: {}", toEmail);
        } catch (MessagingException e) { // MimeMessageHelper 관련 예외 처리 (javax.mail)
            log.error("[MailService] 아이디 찾기 메일 구성 실패 - Email: {}, Error: {}", toEmail, e.getMessage(), e);
        } catch (MailException e) { // 메일 발송 관련 예외 처리 (Spring)
            log.error("[MailService] 아이디 찾기 메일 발송 실패 - Email: {}, Error: {}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendUserIdPasswordByEmail(String toEmail, String userId, String password) {
        log.info("[MailService] 비밀번호 찾기(임시 비밀번호) 메일 발송 시작: {}", toEmail);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[Rewrite] 임시 비밀번호 안내");
            helper.setFrom(FROM_ADDRESS);

            // HTML 본문 생성
            String htmlContent = getHtmlHeader("임시 비밀번호 안내") +
                    "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 20px;'>" +
                    "      안녕하세요, " + userId + "님." +
                    "    </p>" +
                    "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 20px;'>" +
                    "      요청하신 임시 비밀번호는 <strong style='color:#dc3545; font-size: 18px; background-color: #fff3cd; padding: 2px 5px; border-radius: 4px;'>[ " + password + " ]</strong> 입니다." + // 비밀번호 강조 및 배경색 추가
                    "    </p>" +
                    "    <p style='font-size:16px; line-height:1.7; color:#856404; font-weight: bold; margin-bottom: 25px; border: 1px solid #ffeeba; background-color: #fff3cd; padding: 12px; border-radius: 5px;'>" + // 경고 메시지 스타일 개선
                    "      <strong style='color:#dc3545;'>[중요]</strong> 보안을 위해 로그인 하신 후 즉시 비밀번호를 변경하여 사용해주시기 바랍니다." +
                    "    </p>" +
                    "   <p style='font-size:16px; line-height:1.7; color:#495057; margin-top: 30px;'>" +
                    "      서비스를 이용해주셔서 감사합니다." +
                    "    </p>" +
                    getHtmlFooter();

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("[MailService] 임시 비밀번호 메일 발송 성공: {}", toEmail);
        } catch (MessagingException e) {
            log.error("[MailService] 임시 비밀번호 메일 구성 실패 - Email: {}, Error: {}", toEmail, e.getMessage(), e);
        } catch (MailException e) {
            log.error("[MailService] 임시 비밀번호 메일 발송 실패 - Email: {}, Error: {}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String userId, String name) {
        log.info("[MailService] 웰컴 이메일 발송 시작: {}", toEmail);

        String baseUrl = "https://rewrite.o-r.kr/";

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[ReWrite] " + name + "님, 새로운 가족이 되신 것을 환영합니다! ♻️");
            helper.setFrom(FROM_ADDRESS);

            String htmlContent =
                    "<div style='background-color:#f8f9fa; padding:40px 20px; font-family: \"Malgun Gothic\", \"맑은 고딕\", sans-serif;'>" +
                            "  <div style='max-width:600px; margin:0 auto; background-color:#ffffff; padding:30px; border-radius:12px; box-shadow: 0 4px 12px rgba(0,0,0,0.08);'>" +
                            // "    <div style='text-align:center; margin-bottom:25px;'>" +
                            // "      <img src='YOUR_LOGO_URL_HERE' alt='ReWrite 로고' style='max-width:150px;'>" + // 로고 URL 추가 시 주석 해제
                            // "    </div>" +
                            "    <h1 style='color:#343a40; text-align:center; font-size:26px; margin-bottom:15px; font-weight: 600;'>" +
                            name + "님, ReWrite의 새로운 가족이 되신 것을 환영합니다! <span style='font-size: 20px;'>♻️</span>" +
                            "    </h1>" +
                            "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 20px;'>" +
                            "      안녕하세요, <strong>" + name + "</strong>님!" +
                            "    </p>" +
                            "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 20px;'>" +
                            "      ReWrite에 오신 것을 진심으로 환영합니다. 저희와 함께 자원 순환이라는 의미 있는 여정을 시작해주셔서 정말 기쁩니다." +
                            "    </p>" +
                            "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 25px;'>" +
                            name + "님의 참여는 잠들어 있는 물건에 새 생명을 불어넣고, 우리 환경에 긍정적인 변화를 만드는 소중한 발걸음이 될 거예요. ReWrite는 단순한 중고 거래 플랫폼을 넘어, '가치 있는 소비'와 '지속 가능한 삶'을 함께 고민하고 실천하는 커뮤니티를 만들어가고 있습니다." +
                            "    </p>" +
                            "    <h2 style='color:#20c997; font-size:20px; margin-top:30px; margin-bottom:15px; border-left: 4px solid #20c997; padding-left: 10px; font-weight: 600;'>" +
                            "      ReWrite에서는 이런 경험을 하실 수 있어요:" +
                            "    </h2>" +
                            "    <ul style='font-size:16px; line-height:1.8; color:#495057; padding-left: 20px; margin-bottom: 30px; list-style-position: outside;'>" +
                            "      <li><strong>믿음직한 거래:</strong> 안전결제 시스템과 사용자 후기를 통해 안심하고 거래하세요.</li>" +
                            "      <li><strong>손쉬운 이용:</strong> 몇 번의 터치만으로 간편하게 물건을 등록하고 찾아볼 수 있습니다.</li>" +
                            "      <li><strong>따뜻한 소통:</strong> 1:1 채팅으로 판매자, 구매자와 편안하게 대화하며 궁금증을 해결하세요.</li>" +
                            "    </ul>" +
                            "    <h2 style='color:#20c997; font-size:20px; margin-top:30px; margin-bottom:15px; border-left: 4px solid #20c997; padding-left: 10px; font-weight: 600;'>" +
                            "      지금 바로 ReWrite를 시작해보세요!" +
                            "    </h2>" +
                            "    <p style='font-size:16px; line-height:1.7; color:#495057; margin-bottom: 30px;'>" +
                            name + "님의 프로필을 완성하고 첫 거래를 시작해보는 건 어떠세요? 멋진 물건을 발견하거나, 안 쓰는 물건에 새 주인을 찾아주는 즐거움을 경험하실 수 있을 거예요." +
                            "    </p>" +
                            "    <div style='text-align:center; margin: 35px 0;'>" +
                            "    </div>" +
                            "    <p style='margin-top:35px; font-size:15px; color:#495057; text-align:center; font-weight: 600;'>" +
                            "      다시 한번 환영의 인사를 전하며,<br>" +
                            "      ReWrite 팀 드림" +
                            "    </p>" +
                            "    <hr style='border: none; border-top: 1px solid #e9ecef; margin: 30px 0;'>" +
                            "    <p style='font-size:12px; color:#adb5bd; text-align:center; line-height:1.6;'>" +
                            "      ⓒ 2025 ReWrite Inc. | 서울시 성북구<br>" +
                            "    </p>" +
                            "  </div>" +
                            "</div>";


            helper.setText(htmlContent, true); // true: HTML 메일 사용

            mailSender.send(mimeMessage);
            log.info("[MailService] 웰컴 이메일 발송 완료: {}", toEmail);
        } catch (MessagingException e) { // javax.mail 예외
            log.error("[MailService] 웰컴 이메일 구성 실패 - Email: {}, UserId: {}, Name: {}, Error: {}", toEmail, userId, name, e.getMessage(), e);
        } catch (MailException e) { // Spring 예외
            log.error("[MailService] 웰컴 이메일 발송 실패 - Email: {}, UserId: {}, Name: {}, Error: {}", toEmail, userId, name, e.getMessage(), e);
        }
    }
}
package com.example.rewrite.controller;

import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Notice;
import com.example.rewrite.entity.Qna;
import com.example.rewrite.entity.Users;
import com.example.rewrite.repository.Notice.NoticeRepository;
import com.example.rewrite.repository.qna.QnaRepository;
import com.example.rewrite.repository.users.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/qna")
public class QnaController {

    @Autowired
    private QnaRepository qnaRepository;

    @Autowired
    private UsersRepository usersRepository;

    // 문의 목록 페이지
//

    @GetMapping("/qnaList")
    public String inquiryList(HttpSession session, RedirectAttributes redirectAttributes,
                              @PageableDefault(size = 10, sort = "qnaId", direction = Sort.Direction.DESC) Pageable pageable,
                              Model model) {

        // 세션에서 사용자 정보 가져오기
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");

        // 사용자가 로그인하지 않은 경우
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
            return "redirect:/user/login";
        }

        Page<Qna> qnaPage;

        // 관리자인 경우 모든 QnA 항목을 볼 수 있음
        if ("admin".equals(user.getRole())) {
            qnaPage = qnaRepository.findAll(pageable);
        } else {
            // 일반 사용자인 경우 자신의 QnA 항목만 볼 수 있음
            qnaPage = qnaRepository.findByUSERID(user.getId(), pageable);
        }

        if (qnaPage == null || qnaPage.isEmpty()) {
            model.addAttribute("message", "조회할 데이터가 없습니다.");
            // 빈 페이지 반환 (리다이렉트하지 않고 빈 목록 표시)
            model.addAttribute("qnaPage", qnaPage);
        } else {
            model.addAttribute("qnaPage", qnaPage);
        }

        // 페이지 블록 계산 - 10개씩
        int pageBlockSize = 10;
        int pageNow = qnaPage.getPageable().getPageNumber() + 1; // 현재 페이지
        int totalPages = qnaPage.getTotalPages();

        // 시작, 끝 계산기
        int startIdx = ((pageNow - 1) / pageBlockSize) * pageBlockSize + 1;
        int endIdx = Math.min(startIdx + pageBlockSize - 1, totalPages);

        model.addAttribute("startIdx", startIdx);
        model.addAttribute("endIdx", endIdx);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNow", pageNow);

        return "qna/qnaList";
    }

    // 문의 작성 페이지
    @GetMapping("/qnaWrite")
    public String writeInquiry(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        // 로그인 체크 (선택적)
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
             return "redirect:/user/login";
//            return "qna/qnaWrite";
        }

        // 빈 Qna 객체를 모델에 추가하여 폼 바인딩에 사용
        model.addAttribute("qna", new Qna());

        return "qna/qnaWrite";
    }

    @PostMapping("/qnaSubmit")
    public String saveInquiry(@ModelAttribute Qna qna, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        // 로그인 체크
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");
        String userId = user.getId();
        System.out.println("유저아이디" + userId);
//        if (userId == null) {
//            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
//            return "redirect:/";
//        }
        Qna newQna = Qna.builder()
                .category(qna.getCategory())
                .title(qna.getTitle())
                .content(qna.getContent())
//                .USERID(session.getId())
                .USERID(userId != null ? userId : "guest") // session.getId() 대신 userId 사용
                .fileAttachment(qna.getFileAttachment())
                .answer_stat("n")
                .build();

        log.info("newQna : {}", newQna);

        // 저장
        qnaRepository.save(newQna);
        redirectAttributes.addFlashAttribute("message", "문의가 접수되었습니다.");
        return "redirect:/qna/qnaList";
    }

    // 문의 상세 페이지
    @GetMapping("/qnaDetail")
    public String qnaDetail(@RequestParam(name="id",required = false) Long qnaId, Model model,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        // 문의 조회
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));

        // 문의 작성자 또는 관리자만 조회 가능 (선택적)
        UserSessionDto user= (UserSessionDto) session.getAttribute("user");
        boolean isAdmin = "admin".equals(user.getRole());

        log.info("isAdmin:{}", isAdmin);

        if (isAdmin || (qna.getUSERID() != null && qna.getUSERID().equals(user.getId()))) {
        } else {
            redirectAttributes.addFlashAttribute("message", "권한이 없습니다.");
            return "redirect:/qna/qnaList";  // redirect: 추가
        }

        model.addAttribute("qna", qna);
        return "qna/qnaDetail";
    }

    // 관리자용 답변 페이지
    @GetMapping("/qnaAnswer")
    public String inquiryAnswer(@RequestParam("id") Long qnaId, Model model,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크
        boolean isAdmin = "admin".equals(session.getAttribute("userRole"));
        if (!isAdmin) {
            redirectAttributes.addFlashAttribute("message", "관리자만 접근 가능합니다.");
            return "redirect:/qna/qnaList";
        }

        // 문의 조회
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));

        model.addAttribute("qna", qna);
        return "qna/qnaAnswer";
    }

    // 답변 저장 처리
    @PostMapping("/qnaAnswer")
    public String saveAnswer(@RequestParam("id") Long qnaId,
                             @RequestParam("answer") String answer,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크
        boolean isAdmin = "admin".equals(session.getAttribute("userRole"));
        if (!isAdmin) {
            redirectAttributes.addFlashAttribute("message", "관리자만 접근 가능합니다.");
            return "redirect:/qna/qnaList";
        }

        // 문의 조회
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));

        // 답변 저장
        qna.setAnswer(answer);
        qnaRepository.save(qna);

        redirectAttributes.addFlashAttribute("message", "답변이 등록되었습니다.");
        return "redirect:/qna/qnaDetail?id=" + qnaId;
    }

    // URL 중복 문제 해결 - /qna/qna 대신 /qna 또는 빈 문자열로 변경
//    @GetMapping("")
//    public String inquiry(HttpSession session, RedirectAttributes redirectAttributes) {
//        return "qna/qna";
//    }

    // 문의 제출 처리 메서드
//    @PostMapping("/inquiry/submit")
//    public String processInquiry(@RequestParam("inquiryType") String category,
//                                 @RequestParam("inquirySubject") String title,
//                                 @RequestParam("inquiryContent") String content,
//                                 @RequestParam("fileAttachment") MultipartFile fileAttachment,
//                                 HttpSession session,
//                                 RedirectAttributes redirectAttributes) {
//        // 세션에서 사용자 ID 가져오기
//        String userId = (String) session.getAttribute("userId");
//        // Qna 객체 생성
//        Qna qna = new Qna();
//        qna.setCategory(category);
//        qna.setTitle(title);
//        qna.setContent(content);
//        qna.setFileAttachment(String.valueOf(fileAttachment));
//        qna.setUSERID(userId != null ? userId : "guest"); // 로그인 안 된 경우 기본값
//        qna.setRegDate(String.valueOf(LocalDateTime.now()));
//        // 저장
//        qnaRepository.save(qna);
//        redirectAttributes.addFlashAttribute("message", "문의가 성공적으로 등록되었습니다.");
//        return "redirect:/qna/qnaList";
//    }
}

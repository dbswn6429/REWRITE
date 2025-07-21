package com.example.rewrite.controller;


import com.example.rewrite.command.NoticeDTO;
import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Notice;
import com.example.rewrite.entity.Qna;
import com.example.rewrite.repository.Notice.NoticeRepository;
import com.example.rewrite.repository.qna.QnaRepository;
import com.example.rewrite.service.notice.NoticeService;
import com.example.rewrite.service.qna.QnaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private QnaRepository qnaRepository;

    @GetMapping("/modifyUsers")
    public String modifyUsers() {
        return "admin/modifyUsers";
    }

    @GetMapping("/noticeWrite")
    public String noticeWrite(Model model) {
        return "admin/noticeWrite";
    }

    @GetMapping("/modifyProducts")
    public String modifyProducts() {
        return "admin/modifyProducts";
    }


    @PostMapping("/write")
    public String write(NoticeDTO noticeDTO, Model model) {
        log.info("디버깅 - write 왔는지 체크");
        noticeService.uploadNotice(noticeDTO);
        return "redirect:/notice/noticeList";
    }

    @PostMapping("/noticeDelete/{noticeId}")
    public String deleteNoticePost(@PathVariable("noticeId") Long id) {
        log.info("공지사항 삭제 요청 (POST) - ID: {}", id);
        try {
            noticeService.deleteNotice(id);
        } catch (Exception e) {
            log.error("공지사항 삭제 중 오류 발생 - ID: {}", id, e);
        }
        return "redirect:/notice/noticeList";
    }

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

        return "admin/qnaList";
    }

    // 문의 상세 페이지
    @GetMapping("/qnaDetail")
    public String qnaDetail(@RequestParam(name = "id", required = false) Long qnaId, Model model,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        // 문의 조회
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));

        // 문의 작성자 또는 관리자만 조회 가능 (선택적)
        UserSessionDto user = (UserSessionDto) session.getAttribute("user");
        boolean isAdmin = "admin".equals(user.getRole());

        log.info("isAdmin:{}", isAdmin);

        if (isAdmin || (qna.getUSERID() != null && qna.getUSERID().equals(user.getId()))) {
        } else {
            redirectAttributes.addFlashAttribute("message", "권한이 없습니다.");
            return "redirect:/admin/qnaList";  // redirect: 추가
        }

        model.addAttribute("qna", qna);
        return "admin/qnaDetail";
    }

    //문의답변작성
    @PostMapping("/saveAnswer")
    public String saveAnswer(@RequestParam("qnaId") Long qnaId,
                             @RequestParam("answer") String answer,
                             RedirectAttributes redirectAttributes) {
        try {
            // 해당 QnA 조회
            Qna qna = qnaRepository.findById(qnaId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));
            // 답변 업데이트
            qna.setAnswer(answer);
            // 답변상태를 'y'로 변경
            qna.setAnswer_stat("y");
            // 변경사항 저장
            qnaRepository.save(qna);
            redirectAttributes.addFlashAttribute("message", "답변이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            log.error("답변 저장 중 오류 발생 - ID: {}", qnaId, e);
            redirectAttributes.addFlashAttribute("error", "답변 저장 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/qnaDetail?id=" + qnaId;
    }



}
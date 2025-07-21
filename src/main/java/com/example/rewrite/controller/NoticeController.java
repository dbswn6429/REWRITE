package com.example.rewrite.controller;

import com.example.rewrite.command.NoticeDTO;
import com.example.rewrite.entity.Notice;
import com.example.rewrite.repository.Notice.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    @GetMapping("/noticeList")
    public String notice(@PageableDefault  (sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        // 아래 detail 과 동일하게 @PageableDefault 의 size 디폴트값이 10이므로, page만 받아서 진행함.
        Page<Notice> noticePage = noticeRepository.findAll(pageable); // 페이지에이블
        model.addAttribute("noticePage", noticePage);

        // 페이지 블록 계산 - 10개씩
        int pageBlockSize = 10;
        int pageNow = noticePage.getPageable().getPageNumber() + 1; // 현재 페이지
        int totalPages = noticePage.getTotalPages();

        // 시작, 끝 계산기
        int startIdx = ((pageNow - 1) / pageBlockSize) * pageBlockSize + 1;
        int endIdx = Math.min(startIdx + pageBlockSize - 1, totalPages);

        model.addAttribute("startIdx", startIdx);
        model.addAttribute("endIdx", endIdx);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNow", pageNow);

        return "notice/noticeList";
    }

    @GetMapping("/noticeDetail")
    public String noticeDetail(Model model, @RequestParam("noticeId") long noticeId, Pageable pageable) {
        // dto 화면에 넘겨주기
        NoticeDTO dto = NoticeDTO.fromEntity(noticeRepository.getNoticeByNoticeId(noticeId));
        model.addAttribute("dto", dto);

        //pageable로 '목록으로' 버튼 누를 때 전에 있던 페이지(ex 5페이지 -> 내용 -> 목록으로 -> 5페이지로 복귀
        //근데 @PageableDefault 에서 size 디폴트값 까보면 size가 10이니까 굳이 넘겨줄 필요 없음. page만 넘겨주면 될듯
        model.addAttribute("pageable", pageable);

        return "notice/noticeDetail";
    }

}

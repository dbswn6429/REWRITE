package com.example.rewrite.service.notice;

import com.example.rewrite.command.NoticeDTO;
import com.example.rewrite.entity.Notice;

public interface NoticeService {
    void uploadNotice(NoticeDTO noticeDTO);
    void deleteNotice(Long Id);
}
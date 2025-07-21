package com.example.rewrite.service.notice;

import com.example.rewrite.command.NoticeDTO;
import com.example.rewrite.entity.Notice;
import com.example.rewrite.repository.Notice.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public void uploadNotice(NoticeDTO noticeDTO) {
        Notice notice = Notice.builder()
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .img(noticeDTO.getImg())
                .build();

        noticeRepository.save(notice);
    }

    @Override
    public void deleteNotice(Long Id) {
        noticeRepository.deleteById(Id);
    }
}
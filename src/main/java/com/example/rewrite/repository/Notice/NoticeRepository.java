package com.example.rewrite.repository.Notice;

import com.example.rewrite.command.NoticeDTO;
import com.example.rewrite.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeCustomRepository {

    Notice getNoticeByNoticeId(long noticeId);
    void deleteNoticeByNoticeId(long noticeId);
}
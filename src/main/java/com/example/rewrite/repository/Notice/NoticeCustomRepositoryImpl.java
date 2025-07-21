package com.example.rewrite.repository.Notice;

import com.example.rewrite.entity.Notice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class NoticeCustomRepositoryImpl implements NoticeCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public int saveNotice(Notice notice) { // 공지사항 올리기

        return 0;

    }






}

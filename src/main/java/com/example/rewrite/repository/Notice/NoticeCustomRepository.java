package com.example.rewrite.repository.Notice;

import com.example.rewrite.entity.Notice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public interface NoticeCustomRepository  {

    public int saveNotice(Notice notice);

}

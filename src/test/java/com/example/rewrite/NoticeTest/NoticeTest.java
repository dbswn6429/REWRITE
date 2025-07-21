package com.example.rewrite.NoticeTest;

import com.example.rewrite.command.user.SignupRequestDto;
import com.example.rewrite.entity.Notice;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;
import com.example.rewrite.repository.Notice.NoticeRepository;
import com.example.rewrite.repository.cart.CartRepository;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.repository.users.UsersRepository;
import com.example.rewrite.service.cart.CartService;
import com.example.rewrite.service.mail.MailService;
import com.example.rewrite.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class NoticeTest {

    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;


}

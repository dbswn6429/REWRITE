package com.example.rewrite.service.user;

import com.example.rewrite.command.UserVO;
import com.example.rewrite.command.user.FindIdRequestDto;
import com.example.rewrite.command.user.LoginRequestDto;
import com.example.rewrite.command.user.SignupRequestDto;
import com.example.rewrite.command.user.UserDTO;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;


public interface UserService {

    Users signup(SignupRequestDto signupRequestDto);
    User loadUserByUsername(String id);
    void userModify(UserVO user);
    void userDelete(Long uid);
    void sendUserIdToEmail(FindIdRequestDto requestDto);
    boolean checkUserByIdAndEmailAndPhoneAndPassword(FindIdRequestDto requestDto);
    void sendUserPwdToEmail(FindIdRequestDto requestDto);
    boolean checkUserByNameAndPhoneAndEmail(FindIdRequestDto requestDto);
    Users getUserInfo(Long uid);
    String sellCount(Long uid);
    Users getProfile(Long uid);
    List<Product> getSellProd(Long uid);
    List<UserDTO> findUsers(String search, String role);
    void changeRole(Long uid, String role);
    void deleteUser(Long uid);
    boolean checkCurrentPassword(Long uid, String password);
    Integer buyCount(Long uid);
}

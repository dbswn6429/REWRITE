package com.example.rewrite.service.user;

import com.example.rewrite.command.UserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert
            ("INSERT INTO Users (ID, PW, NICKNAME, NAME, BIRTH, IMG_URL, PHONE, ROLE) " +
            "VALUES (#{id}, #{pw}, #{nickname}, #{name}, #{birth}, #{imgUrl}, #{phone}, #{role})")
    int registerUser(UserVO userVO);

    @Select("SELECT * FROM Users WHERE ID = #{id} AND PW = #{pw}")
    UserVO loginForm(UserVO userVO);
}

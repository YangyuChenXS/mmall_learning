package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    //mybatic在传递多个参数的时候，需要用到@Param注解，来表示传递的参数
    User selectLogin(@Param("username") String username, @Param("password") String password);

    // 根据用户名查询忘记密码的提示问题
    String selectQuestionByUsername(String username);

    // 根据用户名 问题 问题答案 查询记录数
    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    // 根据用户名 更新密码
    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    // 根据用户id 和 密码 检查该用户是否存在
    int checkPassword(@Param("password") String password, @Param("userId") Integer userId);

    // 根据用户id校验email是否已存在
    int checkEmailByUserId(@Param("email") String email, @Param("userId") Integer userId);


}
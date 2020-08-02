package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/*
 * 提供一个用户服务接口
 */
public interface IUserService {

    /**
     * @Description: 用户登录接口
     * @Author: XiaosongChen
     * @Date: 19:56 2020/7/23
     */
    ServerResponse<User> login(String username, String password);

    /**
     * @Description: 用户注册接口
     * @Author: XiaosongChen
     * @Date: 19:57 2020/7/23
     */
    ServerResponse<String> register(User user);

    /**
     * @Description: 校验用户名和邮箱的接口
     * @Author: XiaosongChen
     * @Date: 20:24 2020/7/23
     */
    ServerResponse<String> checkValid(String str, String type);

    /**
     * @Description: 根据用户名查找忘记密码的提示问题
     * @Author: XiaosongChen
     * @Date: 18:11 2020/7/26
     */
    // ServerResponse 一般没有加泛型 默认是object对象
    ServerResponse selectQuestion(String username);

    /**
     * @Description: 校验忘记密码问题答案是否正确
     * @Author: XiaosongChen
     * @Date: 19:57 2020/7/26
     */
    ServerResponse<String> checkAnswer(String username, String question, String answer);

    /**
     * @Description: 忘记密码中的重置密码开发
     * @Author: XiaosongChen
     * @Date: 18:51 2020/8/2
     */
    ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken);
}

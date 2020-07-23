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
}

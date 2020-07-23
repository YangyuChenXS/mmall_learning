package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")  //注入controller，供controller调用
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @Description: 用户登录方法实现
     * @Author: XiaosongChen
     * @Date: 19:56 2020/7/23
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        //检查登录的用户名是否存在
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        //逻辑到这一步，说明用户名已经存在，但是user为null对象，则说明是密码错误
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //逻辑走到这一步，说明用户存在，需要返回，这时，在返回的时候，需要将密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    /**
     * @Description: 用户注册方法实现
     * @Author: XiaosongChen
     * @Date: 19:57 2020/7/23
     */
    @Override
    public ServerResponse<String> register(User user){
        //检查注册的用户名是否存在
        /*int resultCount = userMapper.checkUsername(user.getUsername());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("用户名已经存在");
        }*/

        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //检查注册的用户的Email是否已存在
        /*resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已经存在");
        }*/
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //设置为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * @Description: 校验用户名和邮箱的方法实现，根据type类型来判断传入的参数str，是用户名还是邮箱
     * @Author: XiaosongChen
     * @Date: 20:25 2020/7/23
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type){
        //如果type值不为空，开始校验
        if(StringUtils.isNotBlank(type)){
            //如果传入的参数是用户名，则查询用户名是否存在
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            //如果传入的参数是邮箱,则查询邮箱是否存在
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("email已经存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }
}

package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        // 虽然用户名和email在用户表里不是主键，但是通过校验，确保唯一
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

    /**
     * @Description: 根据用户名查找忘记密码的提示问题
     * @Author: XiaosongChen
     * @Date: 18:09 2020/7/26
     */
    public ServerResponse selectQuestion(String username){
        // 先校验用户名是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    /*public static void main(String[] args){
        System.out.println(UUID.randomUUID().toString());
    }*/

    /**
     * @Description: 校验忘记密码问题答案是否正确
     * @Author: XiaosongChen
     * @Date: 18:26 2020/7/26
     */
    public ServerResponse<String> checkAnswer(String username, String question, String answer){

        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount>0){
            //说明问题及问题答案是该用户的，并且是正确的
            //生成唯一通用标识符；
            String forgetToken = UUID.randomUUID().toString();
            // 设置token的缓存
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("找回密码问题答案错误");
    }

    /**
     * @Description: 忘记密码中的重置密码开发
     * @Author: XiaosongChen
     * @Date: 18:51 2020/8/2
     */
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken){
        // 校验token是否部位不为空
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        // 校验用户是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        // 获取token，并校验
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        // 判断forgetToken,token是否相等，这种写法比 forgetToken.equals(token)好，不会出现空指针异常
        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * @Description: 登录状态下重置密码功能开发
     * @Author: XiaosongChen
     * @Date: 18:54 2020/8/9
     */
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count(1),如果不指定id，那么结果就是true,即count>0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }

        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * @Description: 更新用户个人信息功能开发
     * @Author: XiaosongChen
     * @Date: 20:28 2020/8/10
     */
    public ServerResponse<User> updateInformation(User user){
        // username是不能被更新的
        //email也要进行校验，校验除去当前用户新的email是否已存在
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }

        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }
}

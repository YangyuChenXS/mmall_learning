package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.sun.deploy.security.BadCertificateDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Description: 门户_用户接口
 * @Author: XiaosongChen
 * @Date: 16:39 2020/7/19
 */
@Controller // Spring注解，起到控制器的作用
@RequestMapping("/user/")  // 将该门户_用户接口的请求地址全都定为/user/下
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * @Description: 用户登录
     * @Author: XiaosongChen
     * @Date: 16:39 2020/7/19
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
    public ServerResponse<User> login(String username, String password, HttpSession session){
        // service-->mybatis-->dao
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            //如果登录成功
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * @Description: 用户登出
     * @Author: XiaosongChen
     * @Date: 19:50 2020/7/23
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * @Description: 用户注册
     * @Author: XiaosongChen
     * @Date: 19:52 2020/7/23
     */
    @RequestMapping(value = "register.do",method = RequestMethod.GET)
    @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * @Description: 校验用户名和Email是否存在，要实时调用该方法，给前台一个实时的反馈，防止未注册用户调用接口
     * @Author: XiaosongChen
     * @Date: 20:20 2020/7/23
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.GET)
    @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * @Description: 获取用户登录信息
     * @Author: XiaosongChen
     * @Date: 17:39 2020/7/26
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
    @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    /**
     * @Description: 忘记密码，将提示问题返回
     * @Author: XiaosongChen
     * @Date: 17:47 2020/7/26
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.GET)
    @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
     public ServerResponse forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
     }

     /**
      * @Description: 校验忘记密码问题答案是否正确
      * @Author: XiaosongChen
      * @Date: 18:23 2020/7/26
      */
     @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
     @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
     public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.checkAnswer(username,question,answer);
     }

    /**
     * @Description: 忘记密码中的重置密码开发
     * @Author: XiaosongChen
     * @Date: 18:42 2020/8/2
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.GET)
    @ResponseBody // 可以将返回的数据，通过SpringMVC插件自动化序列为JSON对象
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken){
        return iUserService.forgetRestPassword(username, passwordNew, forgetToken);
    }
}

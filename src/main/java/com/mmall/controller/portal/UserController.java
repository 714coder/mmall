package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Author:yuantao
 * @Description:
 * @Date:Created in 23:01 2018/3/3
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /**
     * @Desciption:用户登录
     * @Date: 10:20 2018/3/4
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login (String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    /**
     * @Desciption:用户登出
     * @Date: 9:28 2018/3/5
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMessage("用户登出成功");
    }
    /**
     * @Desciption:用户注册
     * @Date: 10:46 2018/3/5
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }
    /**
     * @Desciption:用户注册校验
     * @Date: 10:48 2018/3/5
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null) {
            user.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，无法获取当前用户信息");
    }
    /**
     * @Desciption:忘记密码需要通过密保问题找回密码时，如果忘记密保问题通过这个方法找回问题
     * @Date: 10:35 2018/3/7
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestionByUsername(username);
    }
    /**
     * @Desciption:忘记密码时，使用本地缓存验证答案
     * @Date: 13:30 2018/3/7
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
    /**
     * @Desciption:验证密保答案后重置密码
     * @Date: 13:40 2018/3/7
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRsetPassword(username,passwordNew,forgetToken);
    }
    /**
     * @Desciption:修改密码
     * @Date: 14:14 2018/3/7
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String password,String passwordNew){
        return iUserService.resetPassword(session,password,passwordNew);
    }
    /**
     * @Desciption:ID、用户名、ROLE不能修改
     * @Date: 14:52 2018/3/7
     */
    @RequestMapping(value = "update_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user){
        ServerResponse response = iUserService.updateUserInfo(session,user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
}

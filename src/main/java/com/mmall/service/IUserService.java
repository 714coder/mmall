package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

/**
 * @Author:yuantao
 * @Description:
 * @Date:Created in 10:57 2018/3/4
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> selectQuestionByUsername(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetRsetPassword(String username,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(HttpSession session, String password, String passwordNew);

    ServerResponse<User> updateUserInfo(HttpSession session,User user);
}

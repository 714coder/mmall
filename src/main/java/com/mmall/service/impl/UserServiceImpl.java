package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.sun.deploy.net.proxy.pac.PACFunctions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @Author:yuantao
 * @Description:
 * @Date:Created in 10:59 2018/3/4
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.Email);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.role.ROLE_CUSTOMER);
        String md5Password = MD5Util.MD5EncodeUtf8(user.getPassword());
        user.setPassword(md5Password);
        int resultCount = userMapper.insertSelective(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        int resultCount;
        if(type.equals(Const.USERNAME)){
            resultCount = userMapper.checkUsername(str);
            if(resultCount>0){
                return ServerResponse.createByErrorMessage("用户已存在");
            }
        }
        if(type.equals(Const.Email)){
            resultCount = userMapper.checkEmail(str);
            if(resultCount>0){
                return ServerResponse.createByErrorMessage("邮箱已存在");
            }
        }
        return ServerResponse.createBySuccessMessage("验证成功");
    }
    public ServerResponse<String> checkValidThreeParameters(String base,String str,String type){
        int resultCount=0;
        if(type.equals(Const.PASSWORD)){
            resultCount = userMapper.checkPassword(base,str);
            if(resultCount>0){
                //密码正确
                return ServerResponse.createBySuccess();
            }else{
                //密码错误
                return ServerResponse.createByError();
            }
        }
        if(resultCount>0){
            return ServerResponse.createBySuccess();
        }else{
            return ServerResponse.createByError();
        }
    }

    @Override
    public ServerResponse<String> selectQuestionByUsername(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题及问题答案正确
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("验证问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetRsetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
           return ServerResponse.createByErrorMessage("参数错误，token不能是空");
        }
        ServerResponse vaildResponse = this.checkValid(username,Const.USERNAME);
        if(vaildResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("密码重置成功");
            }else {
                return ServerResponse.createByErrorMessage("token无效，请重新获取重置密码的token");
            }
        }
        return ServerResponse.createByErrorMessage("密码重置失败");
    }

    @Override
    public ServerResponse<String> resetPassword(HttpSession session, String password, String passwordNew){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录请登录后进行操作");
        }
        else {
            //验证原始密码
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            //todo 验证使用username+password或者user.id+password哪个安全性高不出现横向越权
            ServerResponse validResponse = this.checkValidThreeParameters(user.getUsername(),md5Password,Const.PASSWORD);
            if(validResponse.isSuccess()){
                md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
                int resultCode = userMapper.updatePasswordByUsername(user.getUsername(),md5Password);
                if(resultCode>0){
                    return ServerResponse.createBySuccessMessage("修改密码成功");
                }else {
                    return ServerResponse.createByErrorMessage("修改密码失败");
                }
            }else{
                //密码错误
                return ServerResponse.createByErrorMessage("密码错误请重新输入密码");
            }
        }
    }

    @Override
    public ServerResponse<User> updateUserInfo(HttpSession session, User user) {
        User current_user = (User) session.getAttribute(Const.CURRENT_USER);
        if(current_user==null) {
            return ServerResponse.createByErrorMessage("用户未登录，请登录后进行操作");
        }else {
                //用户正常访问
                User update_user = new User();
                int resultCount = userMapper.checkUpdateEmail(user.getEmail(),current_user.getId());
                if(resultCount==0){
                    //更新邮箱成功
                    update_user.setEmail(user.getEmail());
                }else{
                    return ServerResponse.createByErrorMessage("邮箱已被占用请使用其他邮箱");
                }
                update_user.setId(current_user.getId());
                update_user.setUsername(current_user.getUsername());
                update_user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
                update_user.setPhone(user.getPhone());
                update_user.setQuestion(user.getQuestion());
                update_user.setAnswer(user.getAnswer());
                resultCount = userMapper.updateByPrimaryKeySelective(update_user);
                if(resultCount>0){
                    return ServerResponse.createBySuccess("修改用户信息成功",update_user);
                }else {
                    return ServerResponse.createByErrorMessage("修改用户信息失败");
                }

        }
    }

    //backend
    /**
     * @Desciption:判断是否是管理员
     * @Date: 15:18 2018/3/8
     */
    public static ServerResponse<String> checkAdminRole(HttpSession session){
        User current_user = (User) session.getAttribute(Const.CURRENT_USER);
        if(current_user!=null && current_user.getRole().intValue()==Const.role.ROLE_ADMIN){
                //是管理员
                return ServerResponse.createBySuccess();
        }else{
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要管理员权限操作，请以管理员身份登录");
        }
    }
}

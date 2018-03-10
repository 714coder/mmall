package com.mmall.common;

/**
 * @Author:yuantao
 * @Description:
 * @Date:Created in 17:27 2018/3/4
 */
public class Const {

    public static final String CURRENT_USER="currentUser";

    public static final String USERNAME="username";

    public static final String Email="email";

    public static final String PASSWORD="password";

    public static final String STATUS="status";

    public interface role{
        int ROLE_CUSTOMER=0;//普通用户
        int ROLE_ADMIN=1;//管理员
    }
}

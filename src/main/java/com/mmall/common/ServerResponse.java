package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * @Author:yuantao
 * @Description:
 * @Date:Created in 10:59 2018/3/4
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json时候如果value是null,key也会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status=status;
    }
    private ServerResponse(int status,String msg) {
        this.status=status;
        this.msg=msg;
    }
    private ServerResponse(int status,T data) {
        this.status=status;
        this.data=data;
    }
    private ServerResponse(int status,String msg,T data) {
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    @JsonIgnore
    //isSuccess不出现在序列化结果中
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    /**
     * @Desciption:返回使用公共错误信息的ServerResponse
     * @Date: 15:24 2018/3/4
     */
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    /**
     * @Desciption:返回使用具体错误信息的ServerResponse
     * @Date: 15:45 2018/3/4
     */
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    /**
     * @Desciption:
     * @Date: 15:51 2018/3/4
     */
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorcode,String errorMessage){
        return new ServerResponse<T>(errorcode,errorMessage);
    }

}

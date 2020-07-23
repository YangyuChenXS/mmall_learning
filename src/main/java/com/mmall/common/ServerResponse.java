package com.mmall.common;

import net.sf.jsqlparser.schema.Server;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/*
 * 登录接口的响应对象
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)  //添加该注解后，保证序列化json的时候，如果是null对象，key会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status, T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus(){
        return status;
    }
    public String getMsg() {
        return msg;
    }
    public T getData(){
        return data;
    }


/*
    将ServerResponse(int status, String msg)与ServerResponse(int status, T data)改为public
    即可测试发现，当传的是String时，执行的是ServerResponse(int status, String msg)，
    否则是执行构造器ServerResponse(int status, T data)
    public static void main(String[] args) {
        ServerResponse sr1 = new ServerResponse(1,new Object());
        ServerResponse sr2 = new ServerResponse(1,"abc");
        System.out.println("console");
    }
    */

    //判断响应是否成功,状态码为0时，响应成功
    @JsonIgnore  //该注解在Json序列化之后，不会使得isSuccess在Json里面；即不在json序列化结果当中
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    //定义响应状态为成功的构造器的方法
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    /*
    此时，如果若data为String类型，调用该方法，即可得到ServerResponse(int status, T data)构造器
    避免错误调用ServerResponse(int status, String msg)构造器
     */
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    //定义响应状态为失败的构造器的方法
    public static <T> ServerResponse<T> createError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    //返回错误提示
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    //再定义一个返回可以将状态码作为变量的构造器的方法
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

}

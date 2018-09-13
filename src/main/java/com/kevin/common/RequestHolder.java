package com.kevin.common;

import com.kevin.model.SysUser;

import javax.servlet.http.HttpServletRequest;
/**
* ThreadLocal将线程
* */
public class RequestHolder {

    private static final ThreadLocal<SysUser> userHolder=new ThreadLocal<SysUser>();

    private static final ThreadLocal<HttpServletRequest> requestHolder=new ThreadLocal<HttpServletRequest>();

    public static void add(SysUser sysUser){
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }
    public static SysUser getCurrentUser(){
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }

    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }

}

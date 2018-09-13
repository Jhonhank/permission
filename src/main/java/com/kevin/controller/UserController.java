package com.kevin.controller;

import com.kevin.model.SysUser;
import com.kevin.service.SysUserService;
import com.kevin.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController {

    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request,HttpServletResponse response)throws IOException{
        //从session中移除登录信息
        request.getSession().invalidate();
        String path="signin.jsp";
        response.sendRedirect(path);
    }


    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username=request.getParameter("username");
        String password=request.getParameter("password");

        SysUser sysUser=sysUserService.findByKeyWord(username);
        String errorMsg="";
        String ret=request.getParameter("ret");

        if(StringUtils.isBlank(username)){
            errorMsg="用户名不可以为空";
        }else if(StringUtils.isBlank(password)){
            errorMsg="密码不可以为空";
        }else if(sysUser==null){
            errorMsg="查询不到";
        }else if(sysUser.getPassword().equals(MD5Util.encrypt(password))){
            errorMsg="用户名或密码错误";
        }else if(sysUser.getStatus()!=1){
            errorMsg="用户已被冻结，请联系管理员";
        }else {
            //登录成功
            request.getSession().setAttribute("user",sysUser);
            if(StringUtils.isNotBlank(ret)){
                response.sendRedirect(ret);
            }else {
                response.sendRedirect("/admin/index.page");
            }
        }

        request.setAttribute("error",errorMsg);
        request.setAttribute("username",username);
        if(StringUtils.isNotBlank(ret)){
            request.setAttribute("ret",ret);
        }
        String path="signin.jsp";
//        RequestDispatcher rd= request.getRequestDispatcher(path);
//        try{
//            rd.forward(request,response);
//            return;
//        }catch (Exception e){}
        request.getRequestDispatcher(path).forward(request,response);
        return;
    }
}

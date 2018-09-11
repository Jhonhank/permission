package com.kevin.common;

import com.kevin.exception.ParamException;
import com.kevin.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {
        String url=httpServletRequest.getRequestURL().toString();
        ModelAndView mv=null;
        String defaultMsg="System error";
        //.json .page
        /*
        * 我们要求所有的请求都以.json结尾
        * */
        if(url.endsWith(".json")){
            if(e instanceof PermissionException || e instanceof ParamException){
                JsonData result=JsonData.fail(e.getMessage());
                mv=new ModelAndView("jsonView",result.toMap());
            }else{
                log.error("unknow exception,url:"+url,e);
                JsonData result=JsonData.fail(defaultMsg);
                mv=new ModelAndView("jsonView",result.toMap());
            }
        }else if (url.endsWith(".page")){
                log.error("unknow page Exception,url:"+url,e);
                JsonData result=JsonData.fail(defaultMsg);
                mv=new ModelAndView("exception",result.toMap());
        }else{
            log.error("unknow exception,url:"+url,e);
            JsonData result=JsonData.fail(defaultMsg);
            mv=new ModelAndView("jsonView",result.toMap());
        }
        return mv;
    }


}

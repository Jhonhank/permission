package com.kevin.controller;


import com.kevin.common.ApplicationContextHelper;
import com.kevin.common.JsonData;
import com.kevin.exception.PermissionException;
import com.kevin.mapper.SysAclModuleMapper;
import com.kevin.model.SysAclModule;
import com.kevin.param.TestVo;
import com.kevin.util.BeanValidator;
import com.kevin.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        //如果log报红线，则需要在setting中安装lom插件
        log.info("hello");
        throw new PermissionException("test exception");
//        return JsonData.success("hello,permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo){
        log.info("validate");
        /*try{
            Map<String,String> map= BeanValidator.validateObject(vo);
            if(MapUtils.isNotEmpty(map)){
                for (Map.Entry<String,String> entry:map.entrySet()){
                    log.info("{}->{}",entry.getKey(),entry.getValue());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
        SysAclModuleMapper moduleMapper= ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module=moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.objectToString(module));
        BeanValidator.check(vo);
        return JsonData.success("test validate");
    }

}

package com.kevin.controller;

import com.kevin.beans.PageQuery;
import com.kevin.beans.PageResult;
import com.kevin.common.JsonData;
import com.kevin.model.SysUser;
import com.kevin.param.UserParam;
import com.kevin.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveUser(UserParam userParam){
        sysUserService.save(userParam);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateUser(UserParam param){
        sysUserService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(@RequestParam("deptId") int deptId, PageQuery page){
        PageResult<SysUser> result=sysUserService.getPageByDeptId(deptId,page);
        return JsonData.success(result);
    }


}

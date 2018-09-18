package com.kevin.controller;

import com.google.common.collect.Maps;
import com.kevin.beans.PageQuery;
import com.kevin.beans.PageResult;
import com.kevin.common.JsonData;
import com.kevin.model.SysUser;
import com.kevin.param.UserParam;
import com.kevin.service.SysRoleService;
import com.kevin.service.SysTreeService;
import com.kevin.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysTreeService sysTreeService;
    @Resource
    private SysRoleService sysRoleService;

    @RequestMapping("/noAuth.page")
    public ModelAndView noAuth(){
        return new ModelAndView("noAuth");
    }

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
    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData acls(@RequestParam("userId") int userId){
        Map<String,Object> map= Maps.newHashMap();
        map.put("acls",sysTreeService.userAclTree(userId));
        map.put("roles",sysRoleService);
        return JsonData.success(map);
    }
}

package com.kevin.controller;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kevin.beans.PageQuery;
import com.kevin.common.JsonData;
import com.kevin.model.SysRole;
import com.kevin.param.AclParam;
import com.kevin.service.SysAclService;
import com.kevin.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {

    @Resource
    private SysAclService sysAclService;
    @Resource
    private SysRoleService sysRoleService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAclModule(AclParam param){
        sysAclService.save(param);
        return JsonData.success();
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAclModule(AclParam param){
        sysAclService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData list(@RequestParam("aclModuleId") Integer aclModule, PageQuery pageQuery){
        return JsonData.success(sysAclService.getPageByAclModuleId(aclModule,pageQuery));
    }

    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData acls(@RequestParam("aclId") int aclId){
        Map<String,Object> map= Maps.newHashMap();
        List<SysRole> roleList=sysRoleService.getRoleListByAclId(aclId);
        map.put("roles",roleList);
        map.put("users",sysRoleService.getUserListByRoleList(roleList));
        return JsonData.success(map);
    }
}
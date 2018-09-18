package com.kevin.service;

import com.google.common.collect.Lists;
import com.kevin.common.RequestHolder;
import com.kevin.mapper.SysAclMapper;
import com.kevin.mapper.SysRoleAclMapper;
import com.kevin.mapper.SysRoleUserMapper;
import com.kevin.model.SysAcl;
import com.kevin.model.SysUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;



    public List<SysAcl> getCurrentUserAclList(){
        int userId= RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    public List<SysAcl> getRoleAclList(int roleId){
        List<Integer> aclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if (CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    public List<SysAcl> getUserAclList(int userId){
        if(isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        List<Integer> userRoleIdList=sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if(CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);
    }

    /**
    * 自定义超级管理员
    * */
    public boolean isSuperAdmin(){
        SysUser sysUser=RequestHolder.getCurrentUser();
        if(sysUser.getMail().contains("admin")){
            return true;
        }
        return false;
    }

    public boolean hasUrlAcl(String url){
        if(isSuperAdmin()){
            return true;
        }
        List<SysAcl> aclList=sysAclMapper.getByUrl(url);
        if (CollectionUtils.isEmpty(aclList)){
            return true;
        }

        //TODO
        return false;
    }
}
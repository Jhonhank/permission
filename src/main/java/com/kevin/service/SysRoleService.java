package com.kevin.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.kevin.common.RequestHolder;
import com.kevin.exception.ParamException;
import com.kevin.mapper.SysRoleAclMapper;
import com.kevin.mapper.SysRoleMapper;
import com.kevin.mapper.SysRoleUserMapper;
import com.kevin.mapper.SysUserMapper;
import com.kevin.model.SysRole;
import com.kevin.model.SysUser;
import com.kevin.param.RoleParam;
import com.kevin.util.BeanValidator;
import com.kevin.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysLogService sysLogService;

    public void save(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已存在");
        }
        SysRole role=SysRole.builder().name(param.getName()).status(param.getStatus())
                .type(param.getType()).remake(param.getRemake()).build();
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysRoleMapper.insertSelective(role);
        sysLogService.saveRoleLog(null,role);
    }

    public void update(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole before=sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的角色不存在");

        SysRole after=SysRole.builder().id(param.getId()).name(param.getName()).status(param.getStatus())
                .type(param.getType()).remake(param.getRemake()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysRoleMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveRoleLog(before,after);
    }

    public List<SysRole> getAll(){
        return sysRoleMapper.getAll();
    }

    private boolean checkExist(String name,Integer id){
        return sysRoleMapper.countByName(name,id)>0;
    }

    public List<SysRole> getRoleListByUserId(int userId){
        List<Integer> roleIdList=sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }
    public List<SysRole> getRoleListByAclId(int aclId){
        List<Integer> roleIdList=sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }
    public List<SysUser> getUserListByRoleList(List<SysRole> roleList){
        if (CollectionUtils.isEmpty(roleList)){
            return Lists.newArrayList();
        }
        List<Integer> roleIdList=roleList.stream().map(role->role.getId()).collect(Collectors.toList());
        List<Integer> userIdList=sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }
}

package com.kevin.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kevin.beans.LogType;
import com.kevin.common.RequestHolder;
import com.kevin.mapper.SysLogMapper;
import com.kevin.mapper.SysRoleUserMapper;
import com.kevin.mapper.SysUserMapper;
import com.kevin.model.SysLogWithBLOBs;
import com.kevin.model.SysRoleUser;
import com.kevin.model.SysUser;
import com.kevin.util.IpUtil;
import com.kevin.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleUserService {

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysLogMapper sysLogMapper;

    public List<SysUser> getListByRoleId(int roleId){
        List<Integer> userIdList=sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }

    public void changeRoleUsers(int roleId,List<Integer> userIdList){
        List<Integer> originUserIdList=sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if (originUserIdList.size()==userIdList.size()){
            Set<Integer> originUserIdSet= Sets.newHashSet(originUserIdList);
            Set<Integer> userIdSet=Sets.newHashSet(userIdList);
            originUserIdList.removeAll(userIdSet);
            if(CollectionUtils.isEmpty(originUserIdSet)){
                return;
            }
        }
        updateRoleUsers(roleId,userIdList);
        saveRoleUserLog(roleId,originUserIdList,userIdList);
    }
    @Transactional
    public void updateRoleUsers(int roleId,List<Integer> userIdList){
        sysRoleUserMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)){
            return;
        }
        List<SysRoleUser> roleUserList=Lists.newArrayList();
        for (Integer userId:userIdList){
            SysRoleUser roleUser=SysRoleUser.builder().roleId(roleId).userId(userId)
                    .operator(RequestHolder.getCurrentUser().getUsername()).operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operateTime(new Date()).build();
            roleUserList.add(roleUser);
        }
        sysRoleUserMapper.batchInsert(roleUserList);
    }

    private void saveRoleUserLog(int roleId,List<Integer> before,List<Integer> after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_USER);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}

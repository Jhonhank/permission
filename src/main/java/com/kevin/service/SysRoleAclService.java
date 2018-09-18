package com.kevin.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kevin.beans.LogType;
import com.kevin.common.RequestHolder;
import com.kevin.mapper.SysLogMapper;
import com.kevin.mapper.SysRoleAclMapper;
import com.kevin.model.SysLogWithBLOBs;
import com.kevin.model.SysRoleAcl;
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
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysLogMapper sysLogMapper;

    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList){
        List<Integer> originAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if (originAclIdList.size()==aclIdList.size()){
            Set<Integer> originAclIdSet= Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet=Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if(CollectionUtils.isEmpty(originAclIdSet)){
                return;
            }
        }
        updateRoleAcls(roleId,aclIdList);
        saveRoleAclLog(roleId,originAclIdList,aclIdList);
    }

    @Transactional
    public void updateRoleAcls(int roleId,List<Integer> aclIdList){
        sysRoleAclMapper.deleteByRoleId(roleId);

        if(CollectionUtils.isEmpty(aclIdList)){
            return;
        }
        List<SysRoleAcl> roleAclList=Lists.newArrayList();
        for(Integer aclId:aclIdList){
            SysRoleAcl roleAcl=SysRoleAcl
                    .builder().roleId(roleId).aclId(aclId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }

        sysRoleAclMapper.batchInsert(roleAclList);
    }

    private void saveRoleAclLog(int roleId,List<Integer> before,List<Integer> after){
        SysLogWithBLOBs sysLog=new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before==null?"": JsonMapper.objectToString(before));
        sysLog.setNewValue(after==null?"":JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}

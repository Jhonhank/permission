package com.kevin.service;

import com.google.common.base.Preconditions;
import com.kevin.common.RequestHolder;
import com.kevin.exception.ParamException;
import com.kevin.mapper.SysAclMapper;
import com.kevin.mapper.SysAclModuleMapper;
import com.kevin.model.SysAclModule;
import com.kevin.param.AclModuleParam;
import com.kevin.util.BeanValidator;
import com.kevin.util.IpUtil;
import com.kevin.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysLogService sysLogService;

    public void save(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule=SysAclModule.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remake(param.getRemake()).build();
        aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperatorTime(new Date());
        sysAclModuleMapper.insertSelective(aclModule);
        sysLogService.saveAclModuleLog(null, aclModule);
    }

    public void update(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下的相同名称的权限模块");
        }
        SysAclModule before=sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");

        SysAclModule after=SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remake(param.getRemake()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperatorTime(new Date());

        updateWithChild(before,after);
        sysLogService.saveAclModuleLog(before, after);
    }

    @Transactional
    void updateWithChild(SysAclModule before, SysAclModule after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();
        if(!after.getLevel().equals(before.getLevel())){
            List<SysAclModule> aclModules=sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(aclModules)){
                for (SysAclModule aclModule:aclModules){
                    String level=aclModule.getLevel();
                    if(level.indexOf(oldLevelPrefix)==0){
                        level=newLevelPrefix+level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModules);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }

    public boolean checkExist(Integer parentId,String sysAclName,Integer sysAclId){
        return sysAclModuleMapper.countByNameAndParentId(parentId,sysAclName,sysAclId)>0;
    }

    public String getLevel(Integer aclModuleId){
        SysAclModule aclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (aclModule==null){
            return null;
        }
        return aclModule.getLevel();
    }

    public void delete(int aclModuleId){
        SysAclModule aclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        Preconditions.checkNotNull(aclModule,"待删除的权限模块不存在，无法删除");
        if (sysAclModuleMapper.countByParentId(aclModule.getId())>0){
            throw new ParamException("当前模块下面有了模块，无法删除");
        }
        if (sysAclMapper.countByAclModuleId(aclModule.getId())>0){
            throw new ParamException("当前模块下面有用户，无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
    }
}

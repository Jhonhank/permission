package com.kevin.service;

import com.google.common.base.Preconditions;
import com.kevin.beans.PageQuery;
import com.kevin.beans.PageResult;
import com.kevin.common.RequestHolder;
import com.kevin.exception.ParamException;
import com.kevin.mapper.SysAclMapper;
import com.kevin.model.SysAcl;
import com.kevin.param.AclParam;
import com.kevin.util.BeanValidator;
import com.kevin.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Service
public class SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysLogService sysLogService;

    public void save(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl acl=SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remake(param.getRemake()).build();
        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateTime(new Date());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclMapper.insertSelective(acl);
        sysLogService.saveAclLog(null, acl);
    }

    public void update(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl before=sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限点不存在");
        SysAcl after=SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId())
               .url(param.getUrl()).type(param.getType()).status(param.getStatus()).seq(param.getSeq())
                .remake(param.getRemake()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        sysAclMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveAclLog(before, after);
    }

    public boolean checkExist(int aclModuleId,String name,Integer id){
        return sysAclMapper.countByNameAndAclModuleId(aclModuleId,name,id)>0;
    }

    public String generateCode(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date())+"_"+(int)(Math.random()*100);
    }

    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery page){
        BeanValidator.check(page);
        int count=sysAclMapper.countByAclModuleId(aclModuleId);
        if(count>0){
            List<SysAcl> aclList=sysAclMapper.getPageByAclModuleId(aclModuleId,page);
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }

}

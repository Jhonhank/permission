package com.kevin.service;

import com.google.common.base.Preconditions;
import com.kevin.beans.PageQuery;
import com.kevin.beans.PageResult;
import com.kevin.common.RequestHolder;
import com.kevin.exception.ParamException;
import com.kevin.mapper.SysUserMapper;
import com.kevin.model.SysUser;
import com.kevin.param.UserParam;
import com.kevin.util.BeanValidator;
import com.kevin.util.IpUtil;
import com.kevin.util.MD5Util;
import com.kevin.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

//80.67、34.86、25.90、54.50、38、35、39.9、59.15、60、64.90、107、63
@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysLogService sysLogService;

    public void save(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("该号码已注册");
        }
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已注册");
        }
        String password= PasswordUtil.randomPassword();
        password="123456";
        String encryptedPqssword= MD5Util.encrypt(password);
        SysUser sysUser=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .password(encryptedPqssword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemake()).build();
        sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysUser.setOperateTime(new Date());
//        发送email TODO
        sysUserMapper.insertSelective(sysUser);
        sysLogService.saveUserLog(null,sysUser);
    }

    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已注册");
        }
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已注册");
        }
        SysUser before=sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after=SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .password(before.getPassword()).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemake()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveUserLog(before,after);
    }

    public boolean checkEmailExist(String mail,Integer userId){
        return sysUserMapper.countByMail(mail,userId)>0;
    }

    public boolean checkTelephoneExist(String telephone,Integer userId){
        return sysUserMapper.countTelephone(telephone,userId)>0;
    }

    public SysUser findByKeyWord(String keyword){
            return sysUserMapper.findByKeyWord(keyword);
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page){
        BeanValidator.check(page);
        int count=sysUserMapper.countByDeptId(deptId);
        if(count>0){
            List<SysUser> list=sysUserMapper.getPageByDeptId(deptId,page);
            return PageResult.<SysUser>builder().total(count).data(list).build();
        }
        return PageResult.<SysUser>builder().build();
    }

    public List<SysUser> getAll(){
        return null;
    }
}

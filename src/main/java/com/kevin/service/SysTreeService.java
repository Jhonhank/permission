package com.kevin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kevin.dto.AclDto;
import com.kevin.dto.AclModuleLevelDto;
import com.kevin.dto.DeptLevelDto;
import com.kevin.mapper.SysAclMapper;
import com.kevin.mapper.SysAclModuleMapper;
import com.kevin.mapper.SysDeptMapper;
import com.kevin.model.SysAcl;
import com.kevin.model.SysAclModule;
import com.kevin.model.SysDept;
import com.kevin.util.LevelUtil;
import org.springframework.stereotype.Service;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysCoreService sysCoreService;

    public List<AclModuleLevelDto> userAclTree(int userId){
        List<SysAcl> userAclList=sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList=Lists.newArrayList();
        for (SysAcl acl:userAclList){
            AclDto dto=AclDto.adapt(acl);
            dto.setHsaAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> roleTree(int roleId){
        //1.当前用户已分配的权限点
        List<SysAcl> userAclList=sysCoreService.getCurrentUserAclList();
        //当前角色分配的权限点
        List<SysAcl> roleAclList=sysCoreService.getRoleAclList(roleId);
        //当前系统所有权限点
        List<AclDto> aclDtoList=Lists.newArrayList();

        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = sysAclMapper.getAll();
        for (SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            if (userAclIdSet.contains(acl.getId())) {
                dto.setHsaAcl(true);
            }
            if (roleAclIdSet.contains(acl.getId())) {
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if (CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelDtoList=aclModuleTree();
        Multimap<Integer,AclDto> aclDtoMultimap=ArrayListMultimap.create();
        for (AclDto aclDto:aclDtoList){
            if (aclDto.getStatus()==1){
                aclDtoMultimap.put(aclDto.getAclModuleId(),aclDto);
            }
        }
        bindAclsWithOrder(aclModuleLevelDtoList,aclDtoMultimap);
        return aclModuleLevelDtoList;
    }

    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelDtoList,Multimap<Integer,AclDto> multimap){
        if (CollectionUtils.isEmpty(aclModuleLevelDtoList)){
            return;
        }
        for (AclModuleLevelDto dto:aclModuleLevelDtoList){
            List<AclDto> aclDtoList= (List<AclDto>) multimap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)){
                Collections.sort(aclDtoList,aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleLevelDtoList(),multimap);
        }
    }
    /**
    * 权限模块树
    * */
    public List<AclModuleLevelDto> aclModuleTree(){
        List<SysAclModule> aclModuleList=sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList=Lists.newArrayList();
        for (SysAclModule aclModule:aclModuleList){
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }
        //层级递归
        Multimap<String,AclModuleLevelDto> levelDtoMultimap=ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList=Lists.newArrayList();

        for(AclModuleLevelDto dto:dtoList){
            levelDtoMultimap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        Collections.sort(rootList,aclModuleLevelDtoComparator);
        transformAclModuleTree(rootList,LevelUtil.ROOT,levelDtoMultimap);
        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList,String level,Multimap<String,AclModuleLevelDto> levelDtoMultimap){
        for (int i=0;i<dtoList.size();i++){
            AclModuleLevelDto dto=dtoList.get(i);
            String nextLevel=LevelUtil.calculateLevel(level,dto.getId());
            List<AclModuleLevelDto> tempList= (List<AclModuleLevelDto>) levelDtoMultimap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempList)){
                Collections.sort(tempList,aclModuleLevelDtoComparator);
                dto.setAclModuleLevelDtoList(tempList);
                transformAclModuleTree(tempList,nextLevel,levelDtoMultimap);
            }
        }
    }
    /**
     *
     * 部门模块树
     * */
    public List<DeptLevelDto> deptTree(){
        List<SysDept> deptList=sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList= Lists.newArrayList();
       for (SysDept dept:deptList){
           DeptLevelDto dto=DeptLevelDto.adapt(dept);
           dtoList.add(dto);
       }
       return deptListTree(dtoList);
    }

    public List<DeptLevelDto> deptListTree(List<DeptLevelDto> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }
        /*
        * level
        * */
        Multimap<String,DeptLevelDto> levelDtoMultimap= ArrayListMultimap.create();
        List<DeptLevelDto> rootList=Lists.newArrayList();

        for(DeptLevelDto dto:dtoList){
            levelDtoMultimap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        //按照seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq()-o2.getSeq();
            }
        });
        //递归生成树
        transformDeptTree(dtoList,LevelUtil.ROOT,levelDtoMultimap);
        return rootList;
    }

    public void transformDeptTree(List<DeptLevelDto> deptLevelDtoList,String level,Multimap<String,DeptLevelDto> levelDtoMultimap){
            for (int i=0;i<deptLevelDtoList.size();i++){
                //遍历该层的每一个元素
                DeptLevelDto deptLevelDto=deptLevelDtoList.get(i);
                //处理当前层级的数据
                String nextLevel=LevelUtil.calculateLevel(level,deptLevelDto.getId());
                //处理下一层
                List<DeptLevelDto> tempDeptList= (List<DeptLevelDto>) levelDtoMultimap.get(nextLevel);
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(tempDeptList)){
                    //排序
                    Collections.sort(tempDeptList,deptLevelDtoComparator);
                    //设置下一层部门
                    deptLevelDto.setDeptList(tempDeptList);
                    //进入到下一层处理
                    transformDeptTree(tempDeptList,nextLevel,levelDtoMultimap);
                }
            }
    }

    public Comparator<DeptLevelDto>deptLevelDtoComparator=new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto>aclModuleLevelDtoComparator=new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclDto> aclSeqComparator=new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };
}

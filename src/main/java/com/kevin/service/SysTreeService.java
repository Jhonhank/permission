package com.kevin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kevin.dto.DeptLevelDto;
import com.kevin.mapper.SysDeptMapper;
import com.kevin.model.SysDept;
import com.kevin.util.LevelUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

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

}

package org.tdf.sim.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeEntity {
    private String id;
    private String name;
    private int type;
    private int priority;
    private List<TreeEntity> children;


    //1.从root根开始往下递归，val是初始数据，rid是每一层递归根的pid，第一次传入是整个树的根pid
    public List<TreeEntity> build(List<CategoryEntity> val, String rId, int type) {
        //2.创建trl作为每一层递归得到的树，也就是上一层的一个child，返回值需要set到上一层的child里面
        List<TreeEntity> trl = new ArrayList();
        for (int i = 0; i < val.size(); i++) {
            //3.  判断是为了找到当前传入这一级的child，如果能进入证明val.get(i)这个值是rid这个的child
            if (val.get(i).getParentID().equals(rId)) {
                //4. 如果能进来，则证明有根，把val.get(i)，作为根，递归下一层来寻找 val.get(i)的child
                List<TreeEntity> child = build(val, val.get(i).getId(), type).stream()
                        .sorted(Comparator.comparing(TreeEntity::getPriority)).
                                collect(Collectors.toList());
                //5.如果得到的child size大于o，证明这个val.get(i)有子，需要加到setchild里面
                if (child.size() > 0) {
                    //val.get(i)值组长到tree中
                    TreeEntity treeEntity = new TreeEntity();
                    if (type == 0) {
                        //0为中文名称
                        treeEntity.setName(val.get(i).getCategoryNameChs());
                    } else {
                        treeEntity.setName(val.get(i).getCategoryNameEn());
                    }
                    treeEntity.setId(val.get(i).getId());
                    treeEntity.setType(val.get(i).getType());
                    treeEntity.setPriority(val.get(i).getPriority());
                    //把寻找到的childset到tree中
                    treeEntity.setChildren(child);
                    //7.  无论是叶子还是tree都需要添加到trl中返回上一级，然后把这个返回的set到上一级的child里面
                    trl.add(treeEntity);
                //6.和5相反，属于叶子节点，不用添加child
                } else {
                    TreeEntity treeEntity = new TreeEntity();
                    if (type == 0) {
                        //0为中文名称
                        treeEntity.setName(val.get(i).getCategoryNameChs());
                    } else {
                        treeEntity.setName(val.get(i).getCategoryNameEn());
                    }
                    treeEntity.setId(val.get(i).getId());
                    treeEntity.setType(val.get(i).getType());
                    treeEntity.setPriority(val.get(i).getPriority());
                    //同7
                    trl.add(treeEntity);
                }
            }
        }
        return trl;
    }
}

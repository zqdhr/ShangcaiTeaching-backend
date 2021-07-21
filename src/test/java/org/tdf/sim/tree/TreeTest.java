package org.tdf.sim.tree;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tdf.sim.entity.CategoryEntity;
import org.tdf.sim.entity.TreeEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class TreeTest {

    @Test
    public void test(){
        CategoryEntity A_1 = new CategoryEntity("5","中文A_1_1","EnA_1_1","1",0,2,2,"test",LocalDateTime.now(),LocalDateTime.now());
        CategoryEntity A_2 = new CategoryEntity("6","中文A_1_2","EnA_1_2","1",0,1,2,"test",LocalDateTime.now(),LocalDateTime.now());
        CategoryEntity A = new CategoryEntity("1","中文A_1","EnA_1","0",0,1,1,"test",LocalDateTime.now(),LocalDateTime.now());
        CategoryEntity B = new CategoryEntity("2","中文B_1","EnB_1","0",0,1,1,"test",LocalDateTime.now(),LocalDateTime.now());
        CategoryEntity C = new CategoryEntity("3","中文C_1","EnC_1","5",0,1,1,"test",LocalDateTime.now(),LocalDateTime.now());
        CategoryEntity D = new CategoryEntity("4","中文D_1","EnD_1","3",0,1,1,"test",LocalDateTime.now(),LocalDateTime.now());
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        categoryEntities.add(A_1);
        categoryEntities.add(A_2);
        categoryEntities.add(A);
        categoryEntities.add(B);
        categoryEntities.add(C);
        categoryEntities.add(D);
        TreeEntity treeEntity = new TreeEntity();
        List<TreeEntity> list = treeEntity.build(categoryEntities,"0",0);
        System.out.println(list.toString());
    }
}

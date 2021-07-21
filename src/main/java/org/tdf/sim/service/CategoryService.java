package org.tdf.sim.service;

import org.tdf.sim.entity.TreeEntity;
import org.tdf.sim.type.Category;

import java.util.List;


public interface CategoryService {
    List<TreeEntity> getCategoryTree(int type);

    void saveCategory(Category category);

    List<TreeEntity> getCategoryTree(int type,String categoryID);
}

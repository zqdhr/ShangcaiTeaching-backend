package org.tdf.sim.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdf.sim.dao.CategoryDao;
import org.tdf.sim.entity.CategoryEntity;
import org.tdf.sim.entity.TreeEntity;
import org.tdf.sim.service.CategoryService;
import org.tdf.sim.type.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    static final Striang TREE_ROOT = "0";

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public List<TreeEntity> getCategoryTree(int type) {
        List<CategoryEntity> categoryEntities = categoryDao.findAll();
        if (!categoryEntities.isEmpty()) {
            TreeEntity treeEntity = new TreeEntity();
            return treeEntity.build(categoryEntities, TREE_ROOT, type);
        }
        return new ArrayList<>();
    }

    @Override
    public List<TreeEntity> getCategoryTree(int type, String categoryID) {
        Optional<CategoryEntity> categoryEntity = categoryDao.findById(categoryID);
        if (!categoryEntity.isPresent()) {
            return new ArrayList<>();
        }
        List<CategoryEntity> categoryEntities = categoryDao.findAll();
        if (!categoryEntities.isEmpty()) {
            TreeEntity treeEntity = new TreeEntity();
            return treeEntity.build(categoryEntities, categoryID, type);
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCategory(Category category) {
        categoryDao.save(CategoryEntity.builder()
                .annex(category.annex)
                .categoryNameChs(category.categoryNameChs)
                .categoryNameEn(category.categoryNameEn)
                .parentID(category.parentID)
                .priority(category.priority)
                .status(category.status)
                .type(category.type)
                .build());
    }
}

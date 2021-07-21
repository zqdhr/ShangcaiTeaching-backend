package org.tdf.sim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.tdf.sim.entity.TreeEntity;
import org.tdf.sim.service.CategoryService;
import org.tdf.sim.type.Category;
import org.tdf.sim.type.Response;

import java.util.List;


@RestController
public class CategoryController {

    @Autowired
    public CategoryService categoryService;

    @PostMapping(value = "/add_category")
    public Response<List<String>> addCategory(@RequestBody Category category) {
        categoryService.saveCategory(category);
        return Response.success(null);
    }

    @GetMapping(value = "/category_tree")
    public Response<List<TreeEntity>> getCategoryTree(@RequestParam(value = "type") int type,
                                                      @RequestParam(value = "category_id", required = false) String categoryID) {
        if (StringUtils.isEmpty(categoryID)) {
            return Response.success(categoryService.getCategoryTree(type));
        }
        return Response.success(categoryService.getCategoryTree(type, categoryID));

    }

}

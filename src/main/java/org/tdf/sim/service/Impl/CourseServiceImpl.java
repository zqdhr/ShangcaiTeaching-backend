package org.tdf.sim.service.Impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.tdf.sim.dao.*;
import org.tdf.sim.entity.*;
import org.tdf.sim.service.CourseService;
import org.tdf.sim.type.*;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private CourseClassDao courseClassDao;

    @Autowired
    private CourseCategoryDao courseCategoryDao;

    @Autowired
    private ClassDao classDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private UserDao userDao;

    @Override
    public Pair<Boolean, String> saveCourse(AddCoursePost addCoursePost) {
//        if (addCoursePost.classes.size() > 0 && addCoursePost.classes.stream().noneMatch(x -> classDao.findById(x).isPresent())) {
//            return new Pair<>(false, "classes is error");
//        }
//        if (addCoursePost.categories.stream().noneMatch(x -> categoryDao.findById(x).isPresent())) {
//            return new Pair<>(false, "categories is error");
//        }
//        Optional<UserEntity> optional = userDao.findById(addCoursePost.userID);
//        if (!optional.isPresent()) {
//            return new Pair<>(false, "user_id is error");
//        }
//        if (optional.get().getRoleID() != UserEntity.ROLE_ID.TEACHER) {
//            return new Pair<>(false, "user_id is not teacher role");
//        }
//        CourseEntity courseEntity = courseDao.save(CourseEntity.builder()
//                .userID(addCoursePost.userID)
//                .courseName(addCoursePost.courseName)
//                .type(addCoursePost.type)
//                .build());
//        courseClassDao.saveAll(addCoursePost.classes.stream().map(x -> CourseClassEntity.builder()
//                .courseID(courseEntity.getId())
//                .classID(x).build()
//        ).collect(Collectors.toList()));
//
//        courseCategoryDao.saveAll(addCoursePost.categories.stream().map(x -> ExperimentCategoryEntity.builder()
//                .courseID(courseEntity.getId())
//                .categoryID(x).build()
//        ).collect(Collectors.toList()));
        return new Pair<>(true, "");
    }

    @Override
    public List<CourseResp> getCourseHaveClasses(String ownerID, int type) {
//        Optional<UserEntity> optional = userDao.findById(ownerID);
//        if (!optional.isPresent()) {
//            return new ArrayList<>();
//        }
//        if (optional.get().getRoleID() != UserEntity.ROLE_ID.STUDENT) {
//            return new ArrayList<>();
//        }
//        if (StringUtils.isEmpty(optional.get().getClassID())) {
//            return new ArrayList<>();
//        }
//
//        return courseDao.findAllByType(type).stream().filter(x -> courseClassDao.findAllByCourseID(x.getId()).stream().anyMatch(v -> v.getClassID().equals(optional.get().getClassID()))).map(x -> {
//            List<CategoryEntity> categoryEntities = categoryDao.findAllCategoriesByCourseID(x.getId());
//            List<ClassEntity> classEntities = classDao.findAllClassesByCourseID(x.getId());
//            return CourseResp.builder()
//                    .courseID(x.getId())
//                    .courseName(x.getCourseName())
//                    .categoryList(categoryEntities.stream().map(y -> Category.builder()
//                            .annex(y.getAnnex())
//                            .categoryID(y.getId())
//                            .categoryNameChs(y.getCategoryNameChs())
//                            .categoryNameEn(y.getCategoryNameEn())
//                            .parentID(y.getParentID())
//                            .priority(y.getPriority())
//                            .status(y.getStatus())
//                            .type(y.getType())
//                            .build()).collect(Collectors.toList()))
//                    .classesList(
//                            classEntities.stream().map(z -> Classes.builder()
//                                    .classesID(z.getId())
//                                    .className(z.getClassName())
//                                    .department(z.getDepartment())
//                                    .gradeName(z.getGradeName())
//                                    .major(z.getMajor())
//                                    .build()).collect(Collectors.toList())
//                    )
//                    .build();
//        }).collect(Collectors.toList());
        return null;
    }

    @Override
    public List<CourseResp> getCourse(int type, String ownerID) {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//        System.out.println(df.format(new Date()));
        //        System.out.println(df.format(new Date()));

//        return courseDao.findAllByTypeAndUserID(type, ownerID).stream().map(x -> {
//            List<CategoryEntity> categoryEntities = categoryDao.findAllCategoriesByCourseID(x.getId());
//            List<ClassEntity> classEntities = classDao.findAllClassesByCourseID(x.getId());
//            classEntities.forEach(c -> c.setNumber(userDao.findAllByClassID(c.getId()).size()));
//            return CourseResp.builder()
//                    .courseID(x.getId())
//                    .courseName(x.getCourseName())
//                    .categoryList(categoryEntities.stream().map(y -> Category.builder()
//                            .annex(y.getAnnex())
//                            .categoryID(y.getId())
//                            .categoryNameChs(y.getCategoryNameChs())
//                            .categoryNameEn(y.getCategoryNameEn())
//                            .parentID(y.getParentID())
//                            .priority(y.getPriority())
//                            .status(y.getStatus())
//                            .type(y.getType())
//                            .build()).collect(Collectors.toList()))
//                    .classesList(
//                            classEntities.stream().map(z -> Classes.builder()
//                                    .classesID(z.getId())
//                                    .className(z.getClassName())
//                                    .department(z.getDepartment())
//                                    .gradeName(z.getGradeName())
//                                    .major(z.getMajor())
//                                    .numbers(z.getNumber())
//                                    .build()).collect(Collectors.toList())
//                    )
//                    .build();
//        }).collect(Collectors.toList());
        return null;
    }

    @Override
    @Transactional
    public Pair<Boolean, String> bindCourseClasses(BindCourseClasses bindCourseClasses) {
        if (bindCourseClasses.classes.stream().noneMatch(x -> classDao.findById(x).isPresent())) {
            return new Pair<>(false, "classes is error");
        }
        Optional<UserEntity> optional = userDao.findById(bindCourseClasses.userID);
        if (!optional.isPresent()) {
            return new Pair<>(false, "user_id is error");
        }
        if (optional.get().getRoleID() != UserEntity.ROLE_ID.TEACHER) {
            return new Pair<>(false, "user_id is not teacher role");
        }

        if (!courseDao.findById(bindCourseClasses.courseID).isPresent()) {
            return new Pair<>(false, "course_id is error");
        }
        if (courseClassDao.findAllByCourseID(bindCourseClasses.courseID).size() > 0) {
            courseClassDao.deleteAllByCourseID(bindCourseClasses.courseID);
        }
        courseClassDao.saveAll(bindCourseClasses.classes.stream().map(x -> CourseClassEntity.builder()
                .courseID(bindCourseClasses.courseID)
                .classID(x).build()
        ).collect(Collectors.toList()));

        return new Pair<>(true, "");
    }

    @Override
    @Transactional
    public Pair<Boolean, String> deleteCourse(DeleteCoursePost deleteCoursePost) {
        Optional<UserEntity> optional = userDao.findById(deleteCoursePost.userID);
        if (!optional.isPresent()) {
            return new Pair<>(false, "user_id is error");
        }
        if (optional.get().getRoleID() != UserEntity.ROLE_ID.TEACHER) {
            return new Pair<>(false, "user_id is not teacher role");
        }
        Optional<CourseEntity> op = courseDao.findById(deleteCoursePost.courseID);
        if (!op.isPresent()) {
            return new Pair<>(false, "course_id is exist");
        }
        if (!op.get().getUserID().equals(deleteCoursePost.userID)) {
            return new Pair<>(false, "user_id is not the course creator");
        }
        courseCategoryDao.deleteAllByCourseID(deleteCoursePost.courseID);
        courseClassDao.deleteAllByCourseID(deleteCoursePost.courseID);
        courseDao.deleteById(deleteCoursePost.courseID);
        return new Pair<>(true, "");
    }

    @Override
    public Pair<Boolean, String> modifyCourseName(ModifyCourseName modifyCourseName) {
        Optional<UserEntity> optional = userDao.findById(modifyCourseName.userID);
        if (!optional.isPresent()) {
            return new Pair<>(false, "user_id is error");
        }
        if (optional.get().getRoleID() != UserEntity.ROLE_ID.TEACHER) {
            return new Pair<>(false, "user_id is not teacher role");
        }
        Optional<CourseEntity> op = courseDao.findById(modifyCourseName.courseID);
        if (!op.isPresent()) {
            return new Pair<>(false, "course_id is exist");
        }
        if (!op.get().getUserID().equals(modifyCourseName.userID)) {
            return new Pair<>(false, "user_id is not the course creator");
        }
        CourseEntity courseEntity = op.get();
        courseEntity.setCourseName(modifyCourseName.courseName);
        courseDao.save(courseEntity);
        return new Pair<>(true, "");
    }

    @Override
    public List<Classes> getClasses() {
        List<ClassEntity> classEntities = classDao.findAll();
        return classEntities.stream().map(z -> Classes.builder()
                .classesID(z.getId())
                .className(z.getClassName())
                .department(z.getDepartment())
                .gradeName(z.getGradeName())
                .major(z.getMajor())
                .numbers(userDao.findAllByClassID(z.getId()).size())
                .build()).collect(Collectors.toList());
    }

}

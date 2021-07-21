package org.tdf.sim.controller;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tdf.sim.service.CourseService;
import org.tdf.sim.service.SecurityService;
import org.tdf.sim.type.*;
import org.tdf.sim.util.PageTool;

import com.github.kevinsawicki.http.HttpRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.tdf.sim.Constants.JWT_HEADER_KEY;

@RestController
public class CourseController {


    private CourseService courseService;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping(value = "/add_course")
    public Response<String> addCourse(@RequestBody @Validated AddCoursePost addCoursePost) {
        Pair<Boolean, String> pair = courseService.saveCourse(addCoursePost);
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

    @RequestMapping("/doc")
    public Response<Object> readDoc() {
        String body = HttpRequest.get("https://tds-simulation.oss-cn-hongkong.aliyuncs.com/enlightenment.json")
                .connectTimeout(5000)
                .readTimeout(5000)
                .body();
        return Response.success(JSONObject.parse(body));
    }


    @DeleteMapping(value = "/delete_course")
    public Response<String> deleteCourse(@RequestBody @Validated DeleteCoursePost deleteCoursePost) {
        Pair<Boolean, String> pair = courseService.deleteCourse(deleteCoursePost);
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

    @PostMapping(value = "/modify_course_name")
    public Response<String> modifyCourseName(@RequestBody @Validated ModifyCourseName modifyCourseName) {
        Pair<Boolean, String> pair = courseService.modifyCourseName(modifyCourseName);
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }

    @PostMapping(value = "/bind_course_classes")
    public Response<String> bindCourseClasses(@RequestBody @Validated BindCourseClasses bindCourseClasses) {
        Pair<Boolean, String> pair = courseService.bindCourseClasses(bindCourseClasses);
        if (pair.getKey()) {
            return Response.success(null);
        } else {
            return Response.error(pair.getValue());
        }
    }


    @GetMapping(value = "/course")
    public Response<Page<CourseResp>> getCourse(@RequestHeader(JWT_HEADER_KEY) String jwt,
                                                @RequestParam(value = "per_page") Integer perPage,
                                                @RequestParam(value = "page") Integer page,
                                                @RequestParam(value = "type") int type) {
        String ownerID = securityService.getUserId(jwt);
        return Response.success(PageTool.getPageList(courseService.getCourse(type, ownerID), page, perPage));
    }

    @Autowired
    private SecurityService securityService;

    @GetMapping(value = "/course_have_classes")
    public Response<Page<CourseResp>> getCourseHaveClasses(@RequestHeader(JWT_HEADER_KEY) String jwt,
                                                           @RequestParam(value = "per_page") Integer perPage,
                                                           @RequestParam(value = "page") Integer page,
                                                           @RequestParam(value = "type") int type) {
        String ownerID = securityService.getUserId(jwt);
        return Response.success(PageTool.getPageList(courseService.getCourseHaveClasses(ownerID, type), page, perPage));
    }

    @GetMapping(value = "/classes")
    public Response<List<Department>> getClasses() {
        List<Classes> classes = courseService.getClasses();
        List<String> departmentNames = classes.stream().map(Classes::getDepartment).distinct()
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.toList());
        return Response.success(departmentNames.stream().map(x -> {
            List<String> majors = classes.stream().filter(c -> c.department.equals(x)).map(Classes::getMajor).distinct()
                    .filter(m -> !StringUtils.isEmpty(m)).collect(Collectors.toList());
            return Department.builder()
                    .departmentName(x)
                    .majors(majors.stream().map(major -> {
                        List<String> grades = classes.stream().filter(c -> c.major.equals(major) && c.department.equals(x)).map(Classes::getGradeName).distinct().filter(g -> !StringUtils.isEmpty(g)).collect(Collectors.toList());
                        return Department.Major
                                .builder()
                                .majorName(major)
                                .grades(grades.stream().map(g -> {
                                            List<String> clazz = classes.stream().filter(c -> c.major.equals(major) && c.department.equals(x) && c.gradeName.equals(g))
                                                    .map(Classes::getClassName).distinct().filter(cla -> !StringUtils.isEmpty(cla)).collect(Collectors.toList());
                                            return Department.Major.Grade.builder()
                                                    .gradeName(g)
                                                    .classes(clazz.stream().map(cl -> {
                                                        List<String> classID = classes.stream().filter(c -> c.major.equals(major) && c.department.equals(x) && c.gradeName.equals(g) && c.className.equals(cl)).map(Classes::getClassesID).distinct().filter(id -> !StringUtils.isEmpty(id)).collect(Collectors.toList());
                                                        return Department.Major.Grade.Classes.builder()
                                                                .classID(classID.get(0))
                                                                .className(cl)
                                                                .build();
                                                    }).collect(Collectors.toList())).build();
                                        }
                                ).collect(Collectors.toList()))
                                .build();
                    }).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList()));
    }

//    @GetMapping(value = "/search_classes")
//    public Response<List<String>> searchClass(@RequestParam(value = "major", required = false) String major,
//                                              @RequestParam(value = "department", required = false) String department,
//                                              @RequestParam(value = "grade_name", required = false) String gradeName,
//                                              @RequestParam(value = "class_name", required = false) String className) {
//        List<Classes> classes = courseService.getClasses();
//        if (!StringUtils.isEmpty(major) && !StringUtils.isEmpty(department) && !StringUtils.isEmpty(gradeName) && !StringUtils.isEmpty(className)) {
//            return Response.success(classes.stream().filter(x -> x.major.equals(major) && x.department.equals(department) && x.gradeName.equals(gradeName) && x.className.equals(className)).map(Classes::getClassesID).distinct().filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.toList()));
//        }
//
//        if (!StringUtils.isEmpty(major) && !StringUtils.isEmpty(department) && !StringUtils.isEmpty(gradeName)) {
//            return Response.success(classes.stream().filter(x -> x.major.equals(major) && x.department.equals(department) && x.gradeName.equals(gradeName)).map(Classes::getClassName).distinct().filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.toList()));
//        }
//
//        if (!StringUtils.isEmpty(major) && !StringUtils.isEmpty(department)) {
//            return Response.success(classes.stream().filter(x -> x.major.equals(major) && x.department.equals(department)).map(Classes::getGradeName).distinct().filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.toList()));
//        }
//
//        if (!StringUtils.isEmpty(department)) {
//            return Response.success(classes.stream().filter(x -> x.department.equals(department)).map(Classes::getMajor).distinct().filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.toList()));
//        }
//        return Response.success(classes.stream().map(Classes::getDepartment).distinct().filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.toList()));
//    }


}

package org.tdf.sim.service;


import org.tdf.sim.type.*;

import java.util.List;

public interface CourseService {
    Pair<Boolean, String> saveCourse(AddCoursePost addCoursePost);

    List<CourseResp> getCourse(int type,String ownerID);

    List<CourseResp> getCourseHaveClasses(String ownerID,int type);

    Pair<Boolean, String> bindCourseClasses(BindCourseClasses bindCourseClasses);

    Pair<Boolean, String> deleteCourse(DeleteCoursePost deleteCoursePost);

    Pair<Boolean, String> modifyCourseName(ModifyCourseName modifyCourseName);

    List<Classes> getClasses();
}

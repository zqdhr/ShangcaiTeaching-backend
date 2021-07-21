package org.tdf.sim.type;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class CourseResp {

    @JsonProperty("course_id")
    public String courseID;

    @JsonProperty("user_id")
    public String userID;

    @JsonProperty("course_name")
    public String courseName;

    @JsonProperty("classes_list")
    public List<Classes> classesList;

    @JsonProperty("category_list")
    public List<Category> categoryList;


}

package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class AddCoursePost {

    @NotBlank(message = "user_id can not null")
    @JsonProperty("user_id")
    public String userID;

    @NotBlank(message = "course name can not null")
    @JsonProperty("course_name")
    public String courseName;

    public List<String> classes;

    @NotNull
    @Size(min = 1, message = "categories min size is 1")
    public List<String> categories;

    public int type;
}

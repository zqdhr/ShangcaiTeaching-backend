package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class BindCourseClasses {

    @NotBlank(message = "user_id can not null")
    @JsonProperty("user_id")
    public String userID;

    @NotBlank(message = "course id can not null")
    @JsonProperty("course_id")
    public String courseID;

    @Size(min = 1, message = "classes min size is 1")
    public List<String> classes;
}

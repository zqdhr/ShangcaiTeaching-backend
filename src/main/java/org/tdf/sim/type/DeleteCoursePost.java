package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class DeleteCoursePost {

    @NotBlank(message = "user_id can not null")
    @JsonProperty("user_id")
    public String userID;

    @NotBlank(message = "course_id can not null")
    @JsonProperty("course_id")
    public String courseID;

}

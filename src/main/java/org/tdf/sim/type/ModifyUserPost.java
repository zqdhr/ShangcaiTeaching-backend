package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyUserPost {

    @JsonProperty("user_id")
    public String useID;

    @JsonProperty("name")
    public String name;

    @JsonProperty("gender")
    public int gender;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("identification_number")
    public String identificationNumber;

}

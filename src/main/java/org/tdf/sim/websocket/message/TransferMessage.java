package org.tdf.sim.websocket.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferMessage {

    @JsonProperty("room_id")
    private String roomID;

    @JsonProperty("class_id")
    private String classID;

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("amount")
    private Long amount;



}

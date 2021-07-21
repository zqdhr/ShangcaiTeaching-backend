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
public class QuitMessage {

    @JsonProperty("room_id")
    private String roomID;

    @JsonProperty("user_id")
    private String userID;
}

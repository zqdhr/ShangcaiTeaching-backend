package org.tdf.sim.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class KeyValueEntityId implements Serializable {
    private int type;
    private String key;
}

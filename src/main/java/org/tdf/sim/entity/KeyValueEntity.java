package org.tdf.sim.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// key value 数据存储
// 用于记录初始化等信息
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "key_value")
@IdClass(KeyValueEntityId.class)
public class KeyValueEntity {
    @Id
    @Column(nullable = false, name = "sim_type")
    private int type;
    @Id
    @Column(nullable = false, name = "sim_key")
    private String key;
    @Column(nullable = false, name = "sim_value")
    private String value;
}

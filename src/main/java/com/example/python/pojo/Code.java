package com.example.python.pojo;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "country")
@Data
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private String name;
    private String code;
    private String parentCode;
    private String parentName;
    private int level;
    private String type;
    private String url;
    private String parentUrl;
    private String version;
    @Transient
    private List<Code> codes;
}

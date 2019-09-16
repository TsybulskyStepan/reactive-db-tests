package com.ww.company.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Company {

    @Id
    private String id;
    private String name;
    private int foundationDate;

}

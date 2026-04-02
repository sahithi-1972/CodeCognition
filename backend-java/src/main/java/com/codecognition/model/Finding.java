package com.codecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Finding {
    private String id;
    private String severity;  // CRITICAL, HIGH, MEDIUM, LOW, INFO
    private String category;   // Security, Quality, Dependency, Documentation
    private String title;
    private String description;
    private String file;
    private String fix;
    private String code_example;
}

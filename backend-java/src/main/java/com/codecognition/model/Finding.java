package com.codecognition.model;

public class Finding {
    public String id;
    public String severity;  // CRITICAL, HIGH, MEDIUM, LOW, INFO
    public String category;   // Security, Quality, Dependency, Documentation
    public String title;
    public String description;
    public String file;
    public String fix;
    public String code_example;
}

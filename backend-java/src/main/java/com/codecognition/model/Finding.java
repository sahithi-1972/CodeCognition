package com.codecognition.model;

import jakarta.persistence.*;

@Entity
@Table(name = "findings")
public class Finding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long pk_id;

    public String id;
    public String severity;  // CRITICAL, HIGH, MEDIUM, LOW, INFO
    public String category;   // Security, Quality, Dependency, Documentation
    public String title;
    
    @Column(columnDefinition = "LONGTEXT")
    public String description;
    
    public String file;
    
    @Column(columnDefinition = "LONGTEXT")
    public String fix;
    
    @Column(columnDefinition = "LONGTEXT")
    public String code_example;
}

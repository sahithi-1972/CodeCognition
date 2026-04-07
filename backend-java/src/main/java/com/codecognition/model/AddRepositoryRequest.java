package com.codecognition.model;

public class AddRepositoryRequest {
    public String url;
    public String name;
    
    public AddRepositoryRequest() {}
    
    public AddRepositoryRequest(String url, String name) {
        this.url = url;
        this.name = name;
    }
}

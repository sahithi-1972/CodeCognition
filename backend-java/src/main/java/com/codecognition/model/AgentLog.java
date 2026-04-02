package com.codecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentLog {
    private String agent;
    private String msg;
    private String status;  // running, success, alert, warn
}

package com.ii.testautomation.response.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FileResponse extends BaseResponse {
    private Map<String, List<Integer>> validationErrors = new HashMap<>();

    public FileResponse(String status, String statusCode, String message, Map<String, List<Integer>> validationErrors) {
        super(status, statusCode, message);
        this.validationErrors = validationErrors;
    }
}


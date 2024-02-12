package com.ii.testautomation.controllers;

import com.ii.testautomation.service.TestGroupingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgressController {
    @Autowired
    private TestGroupingService testGroupingService;
}

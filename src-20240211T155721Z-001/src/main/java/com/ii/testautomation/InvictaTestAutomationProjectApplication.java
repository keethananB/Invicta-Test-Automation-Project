package com.ii.testautomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = "com.ii.testautomation")
@EnableScheduling
public class InvictaTestAutomationProjectApplication {
    public static void main(String[] args) { SpringApplication.run(InvictaTestAutomationProjectApplication.class, args);}
}

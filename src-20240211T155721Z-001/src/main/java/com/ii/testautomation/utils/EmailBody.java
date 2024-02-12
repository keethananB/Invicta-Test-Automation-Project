package com.ii.testautomation.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class EmailBody {
    private String emailBody1 = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Document</title>\n" +
            "</head>\n" +
            "<body>\n <B> Click the button to Verify </B>" +
            "<a href=\"http://192.168.1.36:3000/verifyEmail/";

   private String emailBody2 ="\">\n <button> Click here </button>" +
           "</a>\n" +
           "</body>\n" +
           "</html>";

}

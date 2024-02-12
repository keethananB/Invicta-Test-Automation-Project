package com.ii.testautomation.enums;

import lombok.Getter;

@Getter
public enum LoginStatus {
    NEW("new"), VERIFIED("verified"), ACTIVE("active"), DEACTIVATE("deactive"), PENDING("pending"), LOCKED("locked"),
    MAILED("Mail Send");
    private String status;

    private LoginStatus(String status)
    {
        this.status = status;
    }

    public static LoginStatus getByStatus(String status) {
        for (LoginStatus loginStatus : values())
        {
            if (loginStatus.getStatus().equals(status))
            {
                return loginStatus;
            }
        }
        throw new AssertionError("Request status not found for given status [status: " + status + "]");
    }
}

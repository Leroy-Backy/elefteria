package org.elefteria.elefteriasn.response;

import lombok.Data;

@Data
public class SuccessResponse {
    private int status;
    private String message;
    private long timestamp;

    public SuccessResponse(){}

    public SuccessResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}

package com.hospital.backend.dto.response;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
public class BaseResponse {
    // Status code
    private Integer statusCode;

    // Generic data field
    private Object data;

    // Status description
    private String description;

    // Status message
    private String messageStatus;

    // Code result 0 and 1 (success 0, error 1)
    private Integer resultCode;

    // Description of result
    private String resultDescription;

    // Time of the response
    private String time;

    // Time taken to process the request API (in milliseconds)
    private Long took;

    // New Update for NWF S04
    private String hiddenDesc;

    public BaseResponse(Integer statusCode, Object data, String description, String messageStatus, Integer resultCode,
                        String resultDescription, String time, Long took) {
        this.statusCode = statusCode;
        this.data = data;
        this.description = description;
        this.messageStatus = messageStatus;
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
        this.time = time;
        this.took = took;
        this.hiddenDesc = resultDescription;
    }
}

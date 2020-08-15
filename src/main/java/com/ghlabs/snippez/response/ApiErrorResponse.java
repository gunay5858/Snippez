package com.ghlabs.snippez.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiErrorResponse {
    private boolean status = false;

    @JsonProperty("error")
    private ApiError apiError;

    public ApiErrorResponse(ApiError apiError) {
        this.apiError = apiError;
    }
}

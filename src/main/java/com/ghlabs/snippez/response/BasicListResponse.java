package com.ghlabs.snippez.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicListResponse {
    private boolean status = true;
    private ArrayList<Object> data;

    @JsonIgnore
    private int statusCode = 200;
}

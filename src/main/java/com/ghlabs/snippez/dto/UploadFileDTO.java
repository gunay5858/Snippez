package com.ghlabs.snippez.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadFileDTO {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}

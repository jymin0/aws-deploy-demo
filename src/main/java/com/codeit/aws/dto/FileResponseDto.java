package com.codeit.aws.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {
    private Long id;
    private String fileName;
    private String description;
    private String contentType;
    private String url;
    private Long size;
}

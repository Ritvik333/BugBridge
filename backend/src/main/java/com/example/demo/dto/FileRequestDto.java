package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequestDto {
    private Long userId;
    private String username;
    private String language;
    private Long subId;
    private Long bugId;

}

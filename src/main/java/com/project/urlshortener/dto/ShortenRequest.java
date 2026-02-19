package com.project.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ShortenRequest {
    @NotBlank(message = "URL cannot be empty")
    private String originalUrl;
    @Size(min = 3, max = 20 , message="Custom alias must be between 3 and 20 characters")
    private String customAlias;

}

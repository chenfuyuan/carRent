package com.example.carrent.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadMessage {
    private boolean success;
    private String message;
    private String path;
}

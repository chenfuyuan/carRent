package com.cfy.android.carrent.service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadMessage {
    private boolean success;
    private String message;
    private String path;
}

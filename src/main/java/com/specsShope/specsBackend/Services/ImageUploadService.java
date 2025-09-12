package com.specsShope.specsBackend.Services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ImageUploadService {
    Map upload(MultipartFile file) throws IOException;
}

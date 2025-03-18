package com.example.ecommerce.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public String uploadImage(MultipartFile file, String path) throws IOException;
}

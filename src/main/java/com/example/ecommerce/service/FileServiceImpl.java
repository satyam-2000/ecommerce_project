package com.example.ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(MultipartFile file, String path) throws IOException {
        // Get the file name of the original file
        String originalFileName = file.getOriginalFilename();

        // Rename the file by generate file name
        String randomId = UUID.randomUUID().toString();
        String newFileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + newFileName;

        // Check if path existed and create
        File folder = new File(path);

        if (folder.exists()) {
            folder.mkdir();
        }

        // Upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // Return filename
        return newFileName;
    }

}

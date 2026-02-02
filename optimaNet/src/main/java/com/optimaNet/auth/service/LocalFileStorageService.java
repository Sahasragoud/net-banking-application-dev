package com.optimaNet.auth.service;

import com.optimaNet.auth.enums.DocumentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService {

    private static final String BASE_PATH = "optimaNet-storage/kyc";

    public String store(Long applicationId, DocumentType documentType, MultipartFile file) throws IOException {

        Path dir = Paths.get(BASE_PATH, applicationId.toString(), documentType.name());
        Files.createDirectories(dir);

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path target = dir.resolve(fileName);
        Files.copy(file.getInputStream(), target);

        return target.toString();
    }
}

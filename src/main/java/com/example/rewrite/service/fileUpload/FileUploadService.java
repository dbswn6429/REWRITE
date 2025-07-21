package com.example.rewrite.service.fileUpload;


import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileUploadService {
    String uploadFile(MultipartFile file, String folderPath) throws IOException;

    // 기존 메서드 호환성을 위한 기본 메서드 (폴더 없이 루트에 업로드)
    default String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, "");
    }
}

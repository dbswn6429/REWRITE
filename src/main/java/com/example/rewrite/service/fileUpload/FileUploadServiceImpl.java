package com.example.rewrite.service.fileUpload;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        // 파일명 생성 (UUID 사용하여 중복 방지)
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        // 폴더 경로가 있는 경우 파일명 앞에 추가 (경로의 끝에 '/'가 없으면 추가)
        String fileName = "";
        if (folderPath != null && !folderPath.isEmpty()) {
            fileName = folderPath;
            if (!folderPath.endsWith("/")) {
                fileName += "/";
            }
        }
        fileName += uuid + ext;

        // BlobInfo 객체 생성
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        // 파일 업로드
        storage.create(blobInfo, file.getBytes());

        // 업로드된 파일의 URL 생성
        return "https://storage.googleapis.com/" + bucketName + "/" + fileName;
    }

}
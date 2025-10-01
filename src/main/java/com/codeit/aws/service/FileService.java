package com.codeit.aws.service;

import com.codeit.aws.dto.FileResponseDto;
import com.codeit.aws.entity.FileEntity;
import com.codeit.aws.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final S3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.prefix:}")
    private String defaultPrefix;

    // baseUrl 제거

    public FileResponseDto uploadFile(MultipartFile file, String description) {
        try {
            String originalName = file.getOriginalFilename();
            String safeName = (originalName == null || originalName.isBlank()) ? "file" : originalName;
            String contentType = file.getContentType();
            long size = file.getSize();
            String fileName = UUID.randomUUID() + "-" + safeName;
            String prefix   = defaultPrefix;

            String s3Key = (prefix == null || prefix.isBlank())
                    ? fileName
                    : ((prefix.endsWith("/") ? prefix.substring(0, prefix.length()-1) : prefix) + "/" + fileName);


            // 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), size));

            String s3Url = s3Client.utilities()
                    .getUrl(GetUrlRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .build())
                    .toExternalForm();

            // DB 저장
            FileEntity fileEntity = new FileEntity(safeName, description, contentType, s3Url, s3Key, size);
            fileEntity = fileRepository.save(fileEntity);

            log.info("File uploaded successfully: {}", s3Key);

            return new FileResponseDto(
                    fileEntity.getId(),
                    fileEntity.getFileName(),
                    fileEntity.getDescription(),
                    fileEntity.getContentType(),
                    fileEntity.getS3Url(),
                    fileEntity.getSize()
            );

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public FileResponseDto getFileById(Long id) {
        var fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));

        return new FileResponseDto(
                fileEntity.getId(),
                fileEntity.getFileName(),
                fileEntity.getDescription(),
                fileEntity.getContentType(),
                fileEntity.getS3Url(),
                fileEntity.getSize()
        );
    }
}

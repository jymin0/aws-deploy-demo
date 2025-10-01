package com.codeit.aws.controller;

import com.codeit.aws.dto.FileResponseDto;
import com.codeit.aws.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {


    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description) {

        log.info("Received file upload request: filename={}, description={}",
                file.getOriginalFilename(), description);

        FileResponseDto response = fileService.uploadFile(file, description);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponseDto> getFile(@PathVariable Long id) {
        log.info("Received file retrieval request: id={}", id);

        FileResponseDto response = fileService.getFileById(id);
        return ResponseEntity.ok(response);
    }



}

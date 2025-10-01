package com.codeit.aws.repository;

import com.codeit.aws.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    // 기본 CRUD 메서드 외에 추가 메서드 필요 시 구현
}

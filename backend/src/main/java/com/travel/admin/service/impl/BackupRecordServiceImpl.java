package com.travel.admin.service.impl;

import java.nio.file.Path;

import com.travel.admin.entity.BackupRecord;
import com.travel.admin.mapper.BackupRecordMapper;
import com.travel.admin.service.BackupRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupRecordServiceImpl implements BackupRecordService {

    private final BackupRecordMapper backupRecordMapper;

    @Override
    public void recordSuccess(Path file, long size, String checksum) {
        BackupRecord record = new BackupRecord();
        record.setFileName(file.getFileName().toString());
        record.setFilePath(file.toAbsolutePath().toString());
        record.setFileSizeBytes(size);
        record.setChecksum(checksum);
        record.setSuccess(true);
        int inserted = backupRecordMapper.insert(record);
        if (inserted != 1) {
            log.warn("插入备份记录失败, file={}", record.getFilePath());
        }
    }

    @Override
    public void recordFailure(String fileName, String errorMessage) {
        BackupRecord record = new BackupRecord();
        record.setFileName(fileName);
        record.setFilePath(null);
        record.setFileSizeBytes(null);
        record.setChecksum(null);
        record.setSuccess(false);
        record.setErrorMessage(errorMessage);
        int inserted = backupRecordMapper.insert(record);
        if (inserted != 1) {
            log.warn("插入失败的备份记录失败, fileName={}", fileName);
        }
    }
}


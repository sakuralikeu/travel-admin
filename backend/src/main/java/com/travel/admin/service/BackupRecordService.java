package com.travel.admin.service;

import java.nio.file.Path;

public interface BackupRecordService {

    void recordSuccess(Path file, long size, String checksum);

    void recordFailure(String fileName, String errorMessage);
}


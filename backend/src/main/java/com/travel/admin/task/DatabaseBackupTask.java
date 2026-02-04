package com.travel.admin.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.travel.admin.service.BackupRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseBackupTask {

    private final BackupRecordService backupRecordService;

    @Value("${backup.enabled:true}")
    private boolean enabled;

    @Value("${backup.retention-days:30}")
    private int retentionDays;

    @Value("${backup.base-dir:backups}")
    private String baseDir;

    @Value("${backup.mysqldump-path:mysqldump}")
    private String mysqldumpPath;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Scheduled(cron = "${backup.cron:0 0 3 * * *}")
    public void backupDatabase() {
        if (!enabled) {
            return;
        }
        String databaseName = extractDatabaseName(datasourceUrl);
        if (databaseName == null) {
            log.error("无法从数据源 URL 中解析数据库名, url={}", datasourceUrl);
            backupRecordService.recordFailure("unknown", "解析数据库名失败");
            return;
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path directory = Paths.get(baseDir, databaseName, activeProfile.toLowerCase(Locale.ROOT));
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            log.error("创建备份目录失败, dir={}", directory, e);
            backupRecordService.recordFailure(databaseName + "_" + timestamp + ".sql", "创建备份目录失败: " + e.getMessage());
            return;
        }
        String fileName = databaseName + "_" + timestamp + ".sql";
        Path file = directory.resolve(fileName);
        String host = extractHost(datasourceUrl);
        int port = extractPort(datasourceUrl);
        ProcessBuilder builder = new ProcessBuilder(
                mysqldumpPath,
                "-h", host,
                "-P", String.valueOf(port),
                "-u", datasourceUsername,
                "-p" + datasourcePassword,
                databaseName
        );
        builder.redirectOutput(file.toFile());
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            log.error("启动 mysqldump 进程失败", e);
            backupRecordService.recordFailure(fileName, "启动 mysqldump 进程失败: " + e.getMessage());
            deleteFileQuietly(file);
            return;
        }
        String errorOutput = readStream(process.getErrorStream());
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待 mysqldump 进程结束时被中断", e);
            backupRecordService.recordFailure(fileName, "等待备份进程结束被中断");
            deleteFileQuietly(file);
            return;
        }
        if (exitCode != 0) {
            log.error("mysqldump 执行失败, exitCode={}, error={}", exitCode, errorOutput);
            backupRecordService.recordFailure(fileName, "mysqldump 执行失败: " + errorOutput);
            deleteFileQuietly(file);
            return;
        }
        long size;
        try {
            size = Files.size(file);
        } catch (IOException e) {
            log.error("读取备份文件大小失败, file={}", file, e);
            backupRecordService.recordFailure(fileName, "读取备份文件大小失败: " + e.getMessage());
            deleteFileQuietly(file);
            return;
        }
        String checksum;
        try {
            checksum = calculateChecksum(file);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("计算备份文件校验和失败, file={}", file, e);
            backupRecordService.recordFailure(fileName, "计算备份文件校验和失败: " + e.getMessage());
            deleteFileQuietly(file);
            return;
        }
        backupRecordService.recordSuccess(file, size, checksum);
        cleanupOldBackups(directory);
    }

    private void cleanupOldBackups(Path directory) {
        if (retentionDays <= 0) {
            return;
        }
        try {
            LocalDateTime threshold = LocalDateTime.now().minusDays(retentionDays);
            Files.list(directory)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            LocalDateTime lastModified = LocalDateTime.ofInstant(
                                    Files.getLastModifiedTime(path).toInstant(),
                                    java.time.ZoneId.systemDefault()
                            );
                            if (lastModified.isBefore(threshold)) {
                                deleteFileQuietly(path);
                            }
                        } catch (IOException e) {
                            log.warn("检查或删除过期备份文件失败, file={}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.warn("遍历备份目录失败, dir={}", directory, e);
        }
    }

    private String extractDatabaseName(String url) {
        int slash = url.lastIndexOf('/');
        if (slash < 0) {
            return null;
        }
        int question = url.indexOf('?', slash);
        if (question < 0) {
            return url.substring(slash + 1);
        }
        if (question == slash + 1) {
            return null;
        }
        return url.substring(slash + 1, question);
    }

    private String extractHost(String url) {
        int scheme = url.indexOf("://");
        if (scheme < 0) {
            return "localhost";
        }
        String remaining = url.substring(scheme + 3);
        int slash = remaining.indexOf('/');
        String hostPort = slash < 0 ? remaining : remaining.substring(0, slash);
        int colon = hostPort.indexOf(':');
        if (colon < 0) {
            return hostPort;
        }
        return hostPort.substring(0, colon);
    }

    private int extractPort(String url) {
        int scheme = url.indexOf("://");
        if (scheme < 0) {
            return 3306;
        }
        String remaining = url.substring(scheme + 3);
        int slash = remaining.indexOf('/');
        String hostPort = slash < 0 ? remaining : remaining.substring(0, slash);
        int colon = hostPort.indexOf(':');
        if (colon < 0) {
            return 3306;
        }
        String portPart = hostPort.substring(colon + 1);
        try {
            return Integer.parseInt(portPart);
        } catch (NumberFormatException e) {
            return 3306;
        }
    }

    private String calculateChecksum(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream inputStream = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    private String readStream(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        try (InputStream in = inputStream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private void deleteFileQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("删除备份文件失败, file={}", file, e);
        }
    }
}


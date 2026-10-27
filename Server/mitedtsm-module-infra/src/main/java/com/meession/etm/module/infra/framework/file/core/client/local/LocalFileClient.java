package com.meession.etm.module.infra.framework.file.core.client.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.meession.etm.module.infra.framework.file.core.client.AbstractFileClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件客户端
 *
 * @author 密讯
 */
@Slf4j
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    /**
     * 获取配置（公开方法）
     */
    public LocalFileClientConfig getLocalConfig() {
        return config;
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        FileUtil.writeBytes(content, filePath);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public String upload(InputStream content, long size, String path, String type) throws IOException {
        // 流式写入，支持大文件
        String filePath = getFilePath(path);
        Path targetPath = Paths.get(filePath);
        
        // 确保父目录存在
        Path parentDir = targetPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        // 流式复制文件
        Files.copy(content, targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("[upload] 本地文件存储路径: {}", targetPath.toAbsolutePath());
        
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        FileUtil.del(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        try {
            return FileUtil.readBytes(filePath);
        } catch (IORuntimeException ex) {
            if (ex.getMessage().startsWith("File not exist:")) {
                return null;
            }
            throw ex;
        }
    }

    @Override
    public InputStream getContentStream(String path) throws IOException {
        String filePath = getFilePath(path);
        Path targetPath = Paths.get(filePath);
        if (!Files.exists(targetPath)) {
            return null;
        }
        return Files.newInputStream(targetPath);
    }

    private String getFilePath(String path) {
        return config.getBasePath() + File.separator + path;
    }

}

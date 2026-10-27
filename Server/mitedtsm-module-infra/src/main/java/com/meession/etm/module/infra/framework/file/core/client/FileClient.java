package com.meession.etm.module.infra.framework.file.core.client;

import java.io.InputStream;

/**
 * 文件客户端
 *
 * @author 密讯
 */
public interface FileClient {

    /**
     * 获得客户端编号
     *
     * @return 客户端编号
     */
    Long getId();

    /**
     * 上传文件
     *
     * @param content 文件流
     * @param path    相对路径
     * @return 完整路径，即 HTTP 访问地址
     * @throws Exception 上传文件时，抛出 Exception 异常
     */
    String upload(byte[] content, String path, String type) throws Exception;

    /**
     * 上传文件（流式，支持大文件）
     * 
     * 默认实现：将流读取为 byte[] 后调用 upload(byte[], String, String)
     * 子类可覆盖此方法实现真正的流式上传，避免内存溢出
     *
     * @param content 文件输入流
     * @param size    文件大小（字节）
     * @param path    相对路径
     * @param type    文件类型
     * @return 完整路径，即 HTTP 访问地址
     * @throws Exception 上传文件时，抛出 Exception 异常
     */
    default String upload(InputStream content, long size, String path, String type) throws Exception {
        // 默认实现：读取流为 byte[]（不适用于大文件）
        byte[] bytes = content.readAllBytes();
        return upload(bytes, path, type);
    }

    /**
     * 删除文件
     *
     * @param path 相对路径
     * @throws Exception 删除文件时，抛出 Exception 异常
     */
    void delete(String path) throws Exception;

    /**
     * 获得文件的内容
     *
     * @param path 相对路径
     * @return 文件的内容
     */
    byte[] getContent(String path) throws Exception;

    /**
     * 获得文件的内容（流式，支持大文件）
     * 
     * 默认实现：将 getContent() 返回的 byte[] 包装为 ByteArrayInputStream
     * 子类可覆盖此方法实现真正的流式读取，避免内存溢出
     *
     * @param path 相对路径
     * @return 文件输入流
     * @throws Exception 获取文件内容时，抛出 Exception 异常
     */
    default InputStream getContentStream(String path) throws Exception {
        // 默认实现：调用 getContent() 后包装为流（不适用于大文件）
        byte[] content = getContent(path);
        return content != null ? new java.io.ByteArrayInputStream(content) : null;
    }

    // ========== 文件签名，目前仅 S3 支持 ==========

    /**
     * 获得文件预签名地址，用于上传
     *
     * @param path 相对路径
     * @return 文件预签名地址
     */
    default String presignPutUrl(String path) {
        throw new UnsupportedOperationException("不支持的操作");
    }

    /**
     * 生成文件预签名地址，用于读取
     *
     * @param url 完整的文件访问地址
     * @param expirationSeconds 访问有效期，单位秒
     * @return 文件预签名地址
     */
    default String presignGetUrl(String url, Integer expirationSeconds) {
        throw new UnsupportedOperationException("不支持的操作");
    }

}

package com.meession.etm.module.infra.dal.dataobject.file;

import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 大文件上传任务表
 *
 * @author iFlow
 */
@TableName("infra_file_upload_task")
@KeySequence("infra_file_upload_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class LargeFileDO extends BaseDO {

    /**
     * 任务编号
     */
    private Long id;

    /**
     * 文件唯一标识(UUID)
     */
    private String fileId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 分片大小(字节)
     */
    private Integer chunkSize;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 已上传分片数
     */
    private Integer uploadedChunks;

    /**
     * 已合并分片数（用于跟踪合并进度）
     */
    private Integer mergedChunks;

    /**
     * 文件哈希（用于秒传检测）
     */
    private String fileHash;

    /**
     * 状态
     */
    private String status;

    /**
     * 用户编号（绑定上传用户）
     */
    private Long userId;

    /**
     * 文件存储路径
     */
    private String path;

    /**
     * MIME类型
     */
    private String contentType;

    /**
     * 错误信息
     */
    private String errorMessage;

    // ========== 状态常量 ==========

    /**
     * 状态：上传中
     */
    public static final String STATUS_UPLOADING = "uploading";
    /**
     * 状态：上传失败
     */
    public static final String STATUS_UPLOADING_FAILED = "uploading_failed";
    /**
     * 状态：合并中
     */
    public static final String STATUS_MERGING = "merging";
    /**
     * 状态：合并失败
     */
    public static final String STATUS_MERGING_FAILED = "merging_failed";
    /**
     * 状态：已完成
     */
    public static final String STATUS_COMPLETED = "completed";
    /**
     * 状态：已中断
     */
    public static final String STATUS_INTERRUPTED = "interrupted";

}

package com.meession.etm.module.infra.dal.mysql.file;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.infra.controller.admin.file.vo.largefile.LargeFilePageReqVO;
import com.meession.etm.module.infra.dal.dataobject.file.LargeFileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 大文件上传任务 Mapper
 *
 * @author iFlow
 */
@Mapper
public interface LargeFileMapper extends BaseMapperX<LargeFileDO> {

    default PageResult<LargeFileDO> selectPage(LargeFilePageReqVO reqVO, Long userId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LargeFileDO>()
                .eq(LargeFileDO::getUserId, userId)
                .likeIfPresent(LargeFileDO::getFilename, reqVO.getFilename())
                .eqIfPresent(LargeFileDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LargeFileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LargeFileDO::getId));
    }

    default LargeFileDO selectByFileId(String fileId) {
        return selectOne(LargeFileDO::getFileId, fileId);
    }

    default List<LargeFileDO> selectListByStatusIn(List<String> statuses) {
        return selectList(new LambdaQueryWrapperX<LargeFileDO>()
                .in(LargeFileDO::getStatus, statuses));
    }

    default int deleteByFileId(String fileId) {
        return delete(LargeFileDO::getFileId, fileId);
    }

}
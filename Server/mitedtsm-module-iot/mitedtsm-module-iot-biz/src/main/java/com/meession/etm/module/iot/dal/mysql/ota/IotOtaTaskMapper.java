package com.meession.etm.module.iot.dal.mysql.ota;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.iot.controller.admin.ota.vo.task.IotOtaTaskPageReqVO;
import com.meession.etm.module.iot.dal.dataobject.ota.IotOtaTaskDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IotOtaTaskMapper extends BaseMapperX<IotOtaTaskDO> {

    default IotOtaTaskDO selectByFirmwareIdAndName(Long firmwareId, String name) {
        return selectOne(IotOtaTaskDO::getFirmwareId, firmwareId,
                IotOtaTaskDO::getName, name);
    }

    default PageResult<IotOtaTaskDO> selectPage(IotOtaTaskPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<IotOtaTaskDO>()
                .eqIfPresent(IotOtaTaskDO::getFirmwareId, pageReqVO.getFirmwareId())
                .likeIfPresent(IotOtaTaskDO::getName, pageReqVO.getName())
                .orderByDesc(IotOtaTaskDO::getId));
    }

    default int updateByIdAndStatus(Long id, Integer whereStatus, IotOtaTaskDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<IotOtaTaskDO>()
                .eq(IotOtaTaskDO::getId, id)
                .eq(IotOtaTaskDO::getStatus, whereStatus));
    }

}

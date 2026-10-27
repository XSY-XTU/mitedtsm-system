package com.meession.etm.module.infra.api.logger;

import com.meession.etm.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import com.meession.etm.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import com.meession.etm.module.infra.service.logger.ApiErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * API 访问日志的 API 接口
 *
 * @author 密讯
 */
@Service
@Validated
public class ApiErrorLogApiImpl implements ApiErrorLogCommonApi {

    @Resource
    private ApiErrorLogService apiErrorLogService;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        apiErrorLogService.createApiErrorLog(createDTO);
    }

}

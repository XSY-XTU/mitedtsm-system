package com.meession.etm.module.ai.framework.ai.core.websearch;

import com.meession.etm.framework.common.util.json.JsonUtils;
import com.meession.etm.module.ai.framework.ai.core.webserch.AiWebSearchRequest;
import com.meession.etm.module.ai.framework.ai.core.webserch.AiWebSearchResponse;
import com.meession.etm.module.ai.framework.ai.core.webserch.bocha.AiBoChaWebSearchClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * {@link AiBoChaWebSearchClient} 集成测试类
 *
 * @author 密讯
 */
public class AiBoChaWebSearchClientTest {

    private final AiBoChaWebSearchClient webSearchClient = new AiBoChaWebSearchClient(
            "sk-REDACTED");

    @Test
    @Disabled
    public void testSearch() {
        AiWebSearchRequest request = new AiWebSearchRequest()
                .setQuery("阿里巴巴")
                .setCount(3);
        AiWebSearchResponse response = webSearchClient.search(request);
        System.out.println(JsonUtils.toJsonPrettyString(response));
    }

}
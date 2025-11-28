package com.yoke.yokeaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename YuManusTest
 * @created_date 2025/11/28 10:26
 * @description
 */
@SpringBootTest
class YokeManusTest {

    @Resource
    private YokeManus yokeManus;

    @Test
    void run() {
        String userPrompt = """  
                我的另一半居住在广州番禺区，请帮我找到 5 公里内合适的约会地点，  
                并结合一些网络图片，制定一份详细的约会计划，  
                并以 PDF 格式输出""";
        String answer = yokeManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}

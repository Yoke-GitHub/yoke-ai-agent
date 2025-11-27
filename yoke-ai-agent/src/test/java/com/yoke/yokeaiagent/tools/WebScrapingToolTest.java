package com.yoke.yokeaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename WebScrapingToolTest
 * @created_date 2025/11/6 20:40
 * @description
 */
@SpringBootTest
public class WebScrapingToolTest {

    @Test
    public void testScrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://www.bing.com";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
    }
}

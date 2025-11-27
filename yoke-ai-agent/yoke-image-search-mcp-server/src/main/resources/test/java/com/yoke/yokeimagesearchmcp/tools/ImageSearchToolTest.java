package com.yoke.yokeimagesearchmcp.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author H
 * @project_name yoke-image-search-mcp
 * @filename ImageSearchToolTest
 * @created_date 2025/11/15 23:53
 * @description
 */
@SpringBootTest
class ImageSearchToolTest {
        @Resource
        private ImageSearchTool imageSearchTool;

        @Test
        void searchImage() {
            String result = imageSearchTool.searchImage("duck");
            Assertions.assertNotNull(result);
        }
}
package com.yoke.yokeaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;


/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename PgVectorVectorStoreConfigTest
 * @created_date 2025/10/31 1:23
 * @description
 */
@SpringBootTest
public class PgVectorVectorStoreConfigTest {

    @Resource
    VectorStore pgVectorVectorStore;

    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("鱼皮的编程导航有什么用，做项目，学编程", Map.of("meta1", "meta1")),
                new Document("鱼总的原创项目教程 codefather.cn"),
                new Document("鱼皮真帅", Map.of("meta2", "meta2")));
        // 添加文档
        pgVectorVectorStore.add(documents);
        // 相似度查询
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("怎么学编程").topK(5).build());
        Assertions.assertNotNull(results);
    }
}

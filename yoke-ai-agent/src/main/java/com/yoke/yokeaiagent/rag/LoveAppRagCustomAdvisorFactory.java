package com.yoke.yokeaiagent.rag;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename LoveAppRagCustomAdvisorFactory
 * @created_date 2025/11/6 18:29
 * @description LoveAppRagCustomAdvisorFactory 自定义RAG检索增强顾问的工厂
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {
    /**
     * 创建自定义RAG检索增强顾问
     * @param vectorStore 向量存储
     * @param status      状态
     * @return 自定义RAG检索增强顾问
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        // 过滤特定状态的文档
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)       // 过滤条件
                .similarityThreshold(0.5)           // 相似度阈值
                .topK(3)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }
}

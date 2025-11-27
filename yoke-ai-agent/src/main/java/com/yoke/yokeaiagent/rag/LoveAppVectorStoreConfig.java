package com.yoke.yokeaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename LoveAppVectorStorreConfig
 * @created_date 2025/10/29 20:26
 * @description LoveAppVectorStoreConfig 恋爱app向量存储配置类（初始化基于内存的向量数据库 Bean）
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter  myTokenTextSplitter;
    
    @Resource
    private MyDocumentEnricher myDocumentEnricher;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载文档
        List<Document> documentList = loveAppDocumentLoader.loadDocuments();
        // 自主分割文档
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documentList);
        // 增强文档
        List<Document> enrichDocuments = myDocumentEnricher.enrichDocumentsByKeyword(documentList);
        simpleVectorStore.doAdd(enrichDocuments);
        return simpleVectorStore;
    }
}

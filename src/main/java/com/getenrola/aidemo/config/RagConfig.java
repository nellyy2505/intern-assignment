package com.getenrola.aidemo.config;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class RagConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel,
                                   ResourceLoader resourceLoader) {

        if (embeddingModel == null) {
            throw new IllegalStateException("No EmbeddingModel bean available – check your Spring AI OpenAI config.");
        }

        // Build the SimpleVectorStore with the non-null embedding model
        VectorStore vectorStore = SimpleVectorStore
                .builder(embeddingModel)
                .build();

        try {
            Resource resource = resourceLoader.getResource("classpath:pen_kb.txt");
            if (resource != null && resource.exists()) {
                TextReader textReader = new TextReader(resource);
                List<Document> docs = textReader.get();

                if (docs != null && !docs.isEmpty()) {
                    vectorStore.add(docs);
                }
            }
        } catch (Exception e) {
            System.out.println("Warning: could not load pen_kb.txt into VectorStore: " + e.getMessage());
        }

        return vectorStore;
    }
}

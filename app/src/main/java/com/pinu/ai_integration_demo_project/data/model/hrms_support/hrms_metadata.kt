package com.pinu.ai_integration_demo_project.data.model.hrms_support

import com.pinu.ai_integration_demo_project.data.system_instructions.hrmsPolicyAssistantInstructions


//semi-structured knowledge
data class PolicyChunk(
    val id: String,
    val text: String,
    val policy: String,
    val section: String,
    val version: String,
)


//semantic search needs an embedding
data class EmbeddedPolicyChunk(
    val chunk: PolicyChunk,
    val embedding: List<Float>,
)


// simple local vector store - just for understanding purposes
class PolicyVectorStore {

    private val chunks = mutableListOf<EmbeddedPolicyChunk>()

    fun add(chunk: EmbeddedPolicyChunk) {
        chunks.add(chunk)
    }

    fun getAll(): List<EmbeddedPolicyChunk> {
        return chunks.toList()
    }

    fun search(queryEmbedding: List<Float>, topK: Int): List<EmbeddedPolicyChunk> {
//        return chunks.sortedBy { cosineSimilarity(queryEmbedding, it.embedding) }.takeLast(topK)
        //Compare query embedding with every stored embedding.
        //Calculate similarity.
        //Sort by similarity.
        //Return top K.

        //Cosine similarity measures how similar two vectors are based on their direction.

        return emptyList()
    }
}


interface EmbeddingService {
    suspend fun embed(text: String): List<Float>

//    EmbeddingService
//    │
//    ├── Gemini/Google embedding API
//    ├── OpenAI embedding API
//    ├── Vertex AI embedding API
//    ├── Hugging Face embedding model
//    └── Local embedding model
}

// Retrieval service
class PolicyRetrievalService(
    private val embeddingService: EmbeddingService,
    private val vectorStore: PolicyVectorStore,
) {

    suspend fun retrieve(query: String, topK: Int = 5): List<EmbeddedPolicyChunk> {
        val queryEmbedding = embeddingService.embed(query)
        return vectorStore.search(queryEmbedding = queryEmbedding, topK = topK)
    }

}


class PolicyContextBuilder {

    fun build(chunks: List<EmbeddedPolicyChunk>): String {

        return chunks.joinToString("\n\n") { item ->
            """
            Policy: ${item.chunk.policy}
            Section: ${item.chunk.section}
            Version: ${item.chunk.version}

            ${item.chunk.text}
            """.trimIndent()
        }
    }
}

// grounding instruction
fun buildPrompt(context: String, question: String): String {

    return """
        $hrmsPolicyAssistantInstructions

        HRMS POLICY CONTEXT:
        $context

        USER QUESTION:
        $question
    """.trimIndent()
}
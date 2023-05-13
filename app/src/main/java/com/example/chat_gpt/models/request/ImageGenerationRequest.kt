package com.example.chat_gpt.models.request

data class ImageGenerationRequest(
    val n: Int,
    val prompt: String,
    val size: String
)
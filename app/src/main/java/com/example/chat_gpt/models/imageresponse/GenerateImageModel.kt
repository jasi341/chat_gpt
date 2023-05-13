package com.example.chat_gpt.models.imageresponse

data class GenerateImageModel(
    val created: Int,
    val `data`: List<Data>
)
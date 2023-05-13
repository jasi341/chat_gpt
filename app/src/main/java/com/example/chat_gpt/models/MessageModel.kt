package com.example.chat_gpt.models

data class MessageModel(
    var isUser : Boolean,
    var isImage : Boolean,
    var message : String
)

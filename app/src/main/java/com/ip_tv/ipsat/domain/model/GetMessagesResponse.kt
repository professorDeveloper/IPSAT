package com.ip_tv.ipsat.domain.model

data class GetMessagesResponse(
    val messages: List<Message>,
    val status: String
)
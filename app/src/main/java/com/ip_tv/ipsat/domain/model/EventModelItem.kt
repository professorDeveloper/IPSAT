/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.domain.model

data class EventModelItem(
    val category: Int,
    val clearkey: String?,
    val id: Int,
    val logo: String,
    val name: String,
    val play_url: String
)
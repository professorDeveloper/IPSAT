/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.domain.model

data class AppUpdate(
    val version: String? = null,
    val releaseDate: Long? = null,
    val isMandatory: Boolean = false,
    val changeLog: String? = null,
    var appLink : String? = null
)

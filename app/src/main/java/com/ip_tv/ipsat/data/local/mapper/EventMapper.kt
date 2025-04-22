/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.data.local.mapper

import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.domain.model.EventModel
import com.ip_tv.ipsat.domain.model.EventModelItem

fun EventModelItem.toChannelResponseItem(): ChannelResponseItem {
    return ChannelResponseItem(
        audioList = this.clearkey,

        category = this.category,
        categoryProperty = this.id.toString(),
        country = this.play_url,
        description = "Unknown",
        id = this.id,
        image = this.logo,
        language = "Unknown",
        this.name,
        "unknown",
    )
}

fun List<EventModelItem>.toChannelsResponse(): ArrayList<ChannelResponseItem> =
    this.map { it.toChannelResponseItem() }.toCollection(arrayListOf())


fun ChannelResponseItem.toEventModelItem(): EventModelItem {
    return EventModelItem(
        this.category,
        this.audioList,
        this.id,
        this.image,
        this.name,
        this.country
    )
}
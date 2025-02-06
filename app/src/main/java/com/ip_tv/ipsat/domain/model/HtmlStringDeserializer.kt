package com.ip_tv.ipsat.domain.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.jsoup.Jsoup

class HtmlStringDeserializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String {
        val html = p.text
        // Use Jsoup to parse and extract plain text
        return Jsoup.parse(html).text()
    }
}

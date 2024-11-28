package com.example.mealmate.network

import com.example.mealmate.models.Amount
import com.example.mealmate.models.MetricAmount
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class AmountDeserializer : JsonDeserializer<Amount> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Amount {
        return if (json.isJsonObject) {
            // Parse as a full Amount object if it's an object
            val jsonObject = json.asJsonObject
            val value = jsonObject["metric"].asJsonObject["value"].asDouble
            val unit = jsonObject["metric"].asJsonObject["unit"].asString
            Amount(MetricAmount(value, unit))
        } else {
            // If amount is a plain number, assume it’s in some default unit
            Amount(MetricAmount(json.asDouble, "unit")) // Replace "unit" with the appropriate default unit
        }
    }
}

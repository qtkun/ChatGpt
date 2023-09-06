package com.qtkun.chatgpt.ext

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

inline fun <reified K, reified V> Moshi.buildMapJsonAdapter(): JsonAdapter<Map<K, V>> {
    val type: Type = Types.newParameterizedType(Map::class.java, K::class.java, V::class.java)
    return this.adapter(type)
}

inline fun <reified T> Moshi.buildJsonAdapter(): JsonAdapter<T> = this.adapter(T::class.java)

inline fun <reified K, reified V> Moshi.toJsonMap(t: Map<K, V>): String {
    val jsonAdapter: JsonAdapter<Map<K, V>> = buildMapJsonAdapter()
    return jsonAdapter.toJson(t)
}

inline fun <reified T> Moshi.fromJson(json: String): T? {
    val jsonAdapter: JsonAdapter<T> = buildJsonAdapter()
    return jsonAdapter.fromJson(json)
}

fun Moshi.createBody(map: Map<String, Any>): RequestBody {
    return toJsonMap(map).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}
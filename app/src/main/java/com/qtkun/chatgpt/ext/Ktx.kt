package com.qtkun.chatgpt.ext

import android.content.res.Resources
import android.util.TypedValue
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

val Int.dp get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(), Resources.getSystem().displayMetrics
).toInt()

inline fun <reified T : Number> T.asDp(): T {
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(), Resources.getSystem().displayMetrics
    )
    return when (T::class) {
        Float::class -> px as T
        Double::class -> px.toDouble() as T
        Int::class -> px.toInt() as T
        else -> throw IllegalStateException("Type not supported")
    }
}
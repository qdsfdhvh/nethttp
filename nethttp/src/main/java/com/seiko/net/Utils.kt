package com.seiko.net

import com.seiko.net.exception.HttpStatusCodeException
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.Serializable
import java.lang.reflect.*
import java.util.*

fun getParameterUpperBound(type: ParameterizedType, index: Int): Type {
  val types = type.actualTypeArguments
  if (index < 0 || index >= types.size) {
    throw IllegalArgumentException("Index $index not in rage [0, ${types.size}) for $type")
  }
  val paramType = types[index]
  if (paramType is WildcardType) {
    return paramType.upperBounds[0]
  }
  return paramType
}

fun getActualTypeParameter(clazz: Class<*>, index: Int): Type {
  val superClass = clazz.genericSuperclass
  if (superClass !is ParameterizedType) {
    throw RuntimeException("Missing type parameter.")
  }
  return getParameterUpperBound(superClass, index)
}

fun Response.throwIfFatal(): ResponseBody {
  if (isSuccessful) return body!!
  throw HttpStatusCodeException(this)
}
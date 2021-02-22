package com.seiko.net.util

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class TypeLiteral<T> {
  val type: Type
    get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
}
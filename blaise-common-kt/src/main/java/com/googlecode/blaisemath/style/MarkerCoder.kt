package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.util.encode.StringCoder

/** Converts markers to/from strings. */
object MarkerCoder: StringCoder<Marker> {
    override fun encode(obj: Marker) = obj.javaClass.simpleName
    override fun decode(str: String) = TODO()
}
package com.googlecode.blaisemath.util

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.geom.rectangle2
import java.awt.Component
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO

/*-
* #%L
* blaise-common
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
* --
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* #L%
*/

/** Utilities for working with images. */
object Images {

    const val BMP = "bmp"
    const val GIF = "gif"
    const val JPEG = "jpeg"
    const val JPG = "jpg"
    const val PNG = "png"

    private val DATA_URI_PREFIX = "data:"
    private val BASE_64_TOKEN = ";base64"

    val IMAGE_FORMATS = listOf(JPG, JPEG, BMP, GIF, PNG)

    /**
     * Encode the target image as standard base-64, with the target format.
     * The result conforms to RFC-4648, and does not include line breaks.
     * @param image the image to encode
     * @param format the image format (gif, png, bmp, jpg, jpeg)
     * @return base-64 string
     * @throws java.io.IOException
     * @see Base64
     */
    @Throws(IOException::class)
    fun encodeStandardBase64(image: BufferedImage, format: String): String {
        require(IMAGE_FORMATS.contains(format.toLowerCase())) { "Unsupported format: $format" }
        return ByteArrayOutputStream().apply {
            ImageIO.write(image, format.toLowerCase(), Base64.getEncoder().wrap(this))
        }.toString()
    }

    /**
     * Create an image from a base-64 string.
     * @param base64 input string
     * @return image
     * @throws java.io.IOException if there's a problem reading from the string
     */
    @Throws(IOException::class)
    fun decodeStandardBase64(base64: String): BufferedImage {
        val bytes = Base64.getDecoder().decode(base64)
        return ImageIO.read(ByteArrayInputStream(bytes))
    }

    /**
     * Encode the target image as standard base-64, with the target format,
     * and return the result as a data URI.
     * The result conforms to RFC-4648, and does not include line breaks.
     * @param image the image to encode
     * @param format the image format (gif, png, bmp, jpg, jpeg)
     * @return base-64 string inside an image data URI
     * @throws java.io.IOException if there's a problem encoding
     * @see Base64
     */
    @Throws(IOException::class)
    fun encodeDataUriBase64(image: BufferedImage, format: String) =
            "$DATA_URI_PREFIX${mimeType(format)}$BASE_64_TOKEN,${encodeStandardBase64(image, format)}"

    /**
     * Create an image from a standard base-64 data URI.
     * @param base64 base-64 string inside an image data URI
     * @return image
     * @throws java.io.IOException if there's a problem reading from the string
     */
    @Throws(IOException::class)
    fun decodeDataUriBase64(base64: String): BufferedImage {
        require(base64.startsWith(DATA_URI_PREFIX)) { "Invalid data URI: $base64" }
        val pos = base64.indexOf(',')
        val content = base64.substring(pos + 1)
        return decodeStandardBase64(content)
    }

    private fun mimeType(format: String): String {
        val fixedFormat = if (JPG == format.toLowerCase()) JPEG else format.toLowerCase()
        return "image/$fixedFormat"
    }

    /**
     * Renders component on a [BufferedImage].
     * @param view the component
     * @return rendered image
     */
    fun renderImage(view: Component): BufferedImage {
        val image = BufferedImage(view.width, view.height, BufferedImage.TYPE_INT_RGB)
        val canvas = image.createGraphics()
        canvas.clip = rectangle2(0, 0, view.width, view.height)
        view.paint(canvas)
        canvas.dispose()
        return image
    }
}
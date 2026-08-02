package com.googlecode.blaisemath.util

import junit.framework.TestCase
import org.junit.Test
import javax.imageio.ImageIO

class ImagesTest {
    var testImage = Images::class.java.getResource("resources/cherries.png")

    @Test
    @Throws(Exception::class)
    fun testEncodeStandardBase64() {
        val bi = ImageIO.read(testImage)
        println(Images.encodeStandardBase64(bi, Images.PNG))
        println(Images.encodeStandardBase64(bi, Images.GIF))
    }

    @Test
    @Throws(Exception::class)
    fun testDecodeStandardBase64() {
        val bi = ImageIO.read(testImage)
        val b64 = Images.encodeStandardBase64(bi, Images.PNG)
        val res = Images.decodeStandardBase64(b64)
        TestCase.assertEquals(b64, Images.encodeStandardBase64(res, Images.PNG))
    }

    @Test
    @Throws(Exception::class)
    fun testEncodeDataUriBase64() {
        val bi = ImageIO.read(testImage)
        println(Images.encodeDataUriBase64(bi, Images.PNG))
        println(Images.encodeDataUriBase64(bi, Images.GIF))
    }

    @Test
    @Throws(Exception::class)
    fun testDecodeDataUriBase64() {
        val bi = ImageIO.read(testImage)
        val b64 = Images.encodeDataUriBase64(bi, Images.PNG)
        val res = Images.decodeDataUriBase64(b64)
        TestCase.assertEquals(b64, Images.encodeDataUriBase64(res, Images.PNG))
    }

}
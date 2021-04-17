package com.googlecode.blaisemath.util.geom

import java.awt.Shape
import java.awt.geom.Point2D

/** Methods for creating shapes from two locations. */
interface ShapeFromPoints {

    /** Generate shape from a press location and a release location. */
    fun create(press: Point2D, release: Point2D): Shape

    companion object {
        /** Creates line from two points  */
        class Line : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = Line2(press, release)
        }

        /** Creates circle from two points (center and outside).  */
        class Circle : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = circle2(press, press.distance(release))
        }

        /** Creates ellipse from two points (corners of frame).  */
        class Ellipse : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = ellipse2FromDiagonal(press, release)
        }

        /** Creates rectangle from two points (corners of rectangle).  */
        class Rectangle : ShapeFromPoints {
            override fun create(press: Point2D, release: Point2D) = rectangle2FromDiagonal(press, release)
        }
    }
}
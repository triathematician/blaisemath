# blaisemath
**blaisemath** is a collection of Java modules for visualization and mathematics. The source code, javadocs, and jar files are available in the  [central maven repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.blaisemath%22).

## Core Libraries
  * **Firestarter** - Swing utilities for editing properties of Java objects, including several property editors and a customizable `PropertySheet` component.
  * **BlaiseCommon** - Some common data structures, annotations, and utilities.
  * **BlaiseGraphics** - A [scene graph](http://en.wikipedia.org/wiki/Scene_graph) and style library for creating interactive graphics on a Swing component.
  * **BlaiseMath** - Miscellaneous mathematics utilities, including coordinate systems, planar geometry, and 3d points.
  * **BlaiseGraphTheory** - Basic graph theory, graph layout algorithms, and visual graphs.

## Experimental ##
  * **BlaiseParser** - Lightweight library for parsing expressions.
  * **BlaiseWidgets** - Various UI widgets built using BlaiseGraphics, including a numeric slider.
  * **BlaiseVisometry** - Adapts BlaiseGraphics for mathematical coordinate systems, making it easy to write code for interactive mathematical objects.
  * **BlaiseSVG** - Converts BlaiseGraphics objects to/from SVG graphics objects and SVG files. _Under development_.
  * **BlaiseGestures** - Provides mouse gestures for use with the Blaise graphics canvas. _Under development_.
  * **BlaiseSketch** - Basic drawing tool based on the blaise stack. _Under development_.

## Project Dependencies
 * Basic
  * **Firestarter** - none
  * **BlaiseMath** - none
  * **BlaiseParser** - none
  * **BlaiseCommon** - `guava`
  * **BlaiseGraphics** - `blaise-common`, `firestarter`
 * Graphs
  * **BlaiseGraphTheory** - `blaise-graphics`, `blaise-math`, `commons-math`, `swing-layout`
  * **BlaiseGraphTheory3** - `blaise-graphics`, `commons-math`, `swing-layout`
 * SVG/Sketch
  * **BlaiseSVG** - `blaise-graphics`
  * **BlaiseGestures** - `blaise-graphics`, `bsaf`
  * **BlaiseSketch** - `blaise-gestures`, `blaise-svg`, `snake-yaml`
 * Widgets
  * **BlaiseWidgets** - `blaise-graphics`, `swing-layout`
 * Visometry
  * **BlaiseVisometry** - `blaise-graphics`, `blaise-math`
 

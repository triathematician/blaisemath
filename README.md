# blaisemath
**blaisemath** is a collection of Java modules for visualization and mathematics. The source code, javadocs, and jar files are available in the  [central maven repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.blaisemath%22).

## Projects

|Type | Name | Purpose | Dependencies |
|-----|------|---------|--------------|
|Core Library|**Firestarter**|Swing utilities for editing properties of Java objects, including several property editors and a customizable `PropertySheet` component.|*none*|
|Core Library|**BlaiseCommon**|Some common data structures, annotations, and utilities.|`guava`|
|Core Library|**BlaiseGraphics**|A [scene graph](http://en.wikipedia.org/wiki/Scene_graph) and style library for creating interactive graphics on a Swing component.|`blaise-common`, `firestarter`|
|Graph Algorithms and Visualization|**BlaiseGraphTheory**|Basic graph theory, graph layout algorithms, and visual graphs.|`blaise-graphics`, `commons-math`, `swing-layout`|
|Graph Algorithms and Visualization|**BlaiseGraphTheory3**|Branch for next generation of `BlaiseGraphTheory`.|`blaise-graphics`, `commons-math`, `swing-layout`|
|SVG Support and Drawing|**BlaiseSVG**|Converts BlaiseGraphics objects to/from SVG graphics objects and SVG files. _Under development_.|`blaise-graphics`|
|SVG Support and Drawing|**BlaiseGestures**|Provides mouse gestures for use with the Blaise graphics canvas. _Under development_.|`blaise-graphics`, `bsaf`|
|SVG Support and Drawing|**BlaiseSketch**|Basic drawing tool based on the blaise stack. _Under development_.|`blaise-gestures`, `blaise-svg`, `snake-yaml`|
|Widget Library|**BlaiseWidgets**|Various UI widgets built using BlaiseGraphics, including a numeric slider.|`blaise-graphics`, `swing-layout`|
|Parser Library|**BlaiseParser**|Lightweight library for parsing expressions.|*none*|
|"Visometry" Experimentation Code|**BlaiseMath**|Miscellaneous mathematics utilities, including coordinate systems, planar geometry, and 3d points.|*none*|
|"Visometry" Experimentation Code|**BlaiseVisometry**|Adapts BlaiseGraphics for mathematical coordinate systems, making it easy to write code for interactive mathematical objects.|`blaise-graphics`, `blaise-math`|

## External Links
* <https://github.com/google/guava>
* <https://commons.apache.org/proper/commons-math/>
* <https://bitbucket.org/asomov/snakeyaml>
* <https://kenai.com/projects/bsaf/pages/Home>
* <https://github.com/FasterXML/jackson>

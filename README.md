# blaisemath
**blaisemath** is a collection of Java modules for visualization and mathematics. The source code, javadocs, and jar files are available in the  [central maven repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.googlecode.blaisemath%22).

## Projects

|Type | Name | Purpose | Dependencies |
|-----|------|---------|--------------|
|Core|**Firestarter**|Swing utilities for editing properties of Java objects, including several property editors and a customizable `PropertySheet` component.|*none*|
|Core|**BlaiseCommon**|Some common data structures, annotations, and utilities.|`guava`|
|Core|**BlaiseGraphics**|A [scene graph](http://en.wikipedia.org/wiki/Scene_graph) and style library for creating interactive graphics on a Swing component.|`blaise-common`, `firestarter`|
|Graphs|**BlaiseGraphTheory**|Basic graph theory, graph layout algorithms, and visual graphs.|`blaise-graphics`, `commons-math`, `swing-layout`|
|Graphs|**BlaiseGraphTheory3**|Branch for next generation of `BlaiseGraphTheory`.|`blaise-graphics`, `commons-math`, `swing-layout`|
|SVG/Drawing|**BlaiseSVG**|Converts BlaiseGraphics objects to/from SVG graphics objects and SVG files. _Under development_.|`blaise-graphics`|
|SVG/Drawing|**BlaiseGestures**|Provides mouse gestures for use with the Blaise graphics canvas. _Under development_.|`blaise-graphics`, `bsaf`|
|SVG/Drawing|**BlaiseSketch**|Basic drawing tool based on the blaise stack. _Under development_.|`blaise-gestures`, `blaise-svg`, `snake-yaml`|
|Widgets|**BlaiseWidgets**|Various UI widgets built using BlaiseGraphics, including a numeric slider.|`blaise-graphics`, `swing-layout`|
|Parser|**BlaiseParser**|Lightweight library for parsing expressions.|*none*|
|Experimentation|**BlaiseMath**|Miscellaneous mathematics utilities, including coordinate systems, planar geometry, and 3d points.|*none*|
|Experimentation|**BlaiseVisometry**|Adapts BlaiseGraphics for mathematical coordinate systems, making it easy to write code for interactive mathematical objects.|`blaise-graphics`, `blaise-math`|

## External Links
* <https://github.com/google/guava>
* <https://commons.apache.org/proper/commons-math/>
* <https://bitbucket.org/asomov/snakeyaml>
* <https://kenai.com/projects/bsaf/pages/Home>
* <https://github.com/FasterXML/jackson>

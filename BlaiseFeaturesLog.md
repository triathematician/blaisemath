**REMARK:** _This list is for the old version of blaise, and does not reflect features currently present or not present in the new version (`blaisecore`)._

#### Apr 2009 ####

  * _(**HUGE**) Restructured `blaise` into six separate packages: `blaiseparser`, `blaisemath`, `firestorm`, `blaise`, `blaise3d`, and `blaisables`._ --EP
  * _Compressed .jar files for packages (saves about 50% of space)._ --EP
  * _Adjusted settings for multiple `blaisable` applets._ --EP
  * _Added differential equations plotters (one and two-variable time-dependent)._ --EP

#### Nov 2008 ####

  * _Added some more bean patterns to standard plotpanels. Added options to show/hide labels along paths._ --EP
  * _Perspective 3D visometry with stereographic display._ --EP
  * _3D vector fields, parametric curves and surfaces, and more visuals._ --EP
  * _Several applets demonstrating dynamic parameter capability. Also posted to web._ --EP
  * _Particle flow models for constrained vector field models._ --EP
  * _Moved animation toolbar to the left side of the plot window by default._ --EP
  * _Implemented animation for vector fields, allowing them to be displayed as animating flows._ --Elisha
  * _Added in rudimentary visualization of 3D plots (on top of the Euclidean2 visometry)._ --EP
  * _Added ability to show/hide any plottable._ --EP

#### Oct 2008 ####

  * _Added bean patterns to `PlotPanel`s for the marker box and animation control, so they can be hidden or shown in the Netbeans GUI builder._ --EP
  * _Added bean pattern for the axis type to `Plot2D` for the Netbeans GUI builder._ --EP
  * _Began work on automatic XML output from the `Settings` class._ --EP

#### June 2008 ####

  * _Added an `FLabel` class displaying any/all of some text, a color, a stroke. Also added an `FLabelBox` class to contain a group of these elements, suitable for a legend on a graph._ --EP
  * _Implemented ability of multiple `Plottable` elements to use the same underlying `ColorModel`, ensuring their colors are always synchronized._ --EP
  * _Fixed problem with aspect ratio in `Euclidean2` plot panels._ --EP
  * _Added ability of `PlotPanel` to synchronize `RangeTimer` with another `PlotPanel`._ --EP
  * _Added ability to label paths (`PointSet2D`), and to display the current point when animating._ --EP
  * _Restructured `blaise` package to organize `Plottable` elements by their corresponding `Visometry`._ --EP
  * _Began work on `Voronoi2D` which will plot Voronoi tesselations. It works with the exception of rays going to infinity._ --EP
  * _Wrote a working version of a contour plot for 2 input/1 output functions. It works, but it's rather slow._ --EP
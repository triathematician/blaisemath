`BlaiseGraphics` is a [scene graph](http://en.wikipedia.org/wiki/Scene_graph) library, used to render and interact with collections of 2d shapes on a Java graphics canvas.

# Usage #
The simplest way to use the library is to draw stylized shapes on a `Graphics2D` object. Doing this is a simple matter of creating a `GraphicComponent` and adding a number of `Graphic` objects to that component.

_EXAMPLES TO BE ADDED LATER_

# Graphic #
`Graphic` is the basic building block of the library, representing an object that can be drawn on a `Graphics2D` canvas. Graphics have the following attributes & methods:
  * **PARENT**: a `GraphicComposite`
  * **DRAW**: method to draw graphic on canvas
  * **DRAW CUSTOMIZATION**: a set of "visibility hints" to customize object appearance
  * **LOCATION**: methods to test whether the graphic contains a given point, or intersects a given rectangle
  * **SELECTION**: flag indicating whether the graphic can be selected
  * **TOOLTIP**: display tooltip at given point (may be disabled)
  * **CONTEXT MENU**: support for populating a context menu, using one or more `ContextMenuInitializer`s
  * **MOUSE HANDLING**: support for both `MouseListener`s and `MouseMotionListener`s (receiving `GraphicMouseEvent`s that include the source graphic as well as usual `MouseEvent` properties)


# Design #

## Design Requirements ##
  * Render a tree of Graphics2D shapes (including text)
  * Separate style customization from shapes
  * Display tooltips for shapes as necessary
  * Feed mouse events to shapes' registered objects as necessary
  * Forward changes from shapes to get a redraw
  * Display mouseover and other "highlight" effects
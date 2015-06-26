Most `Graphic`s in BlaiseGraphics separate the "basic object" represented by the graphic from its appearance (e.g. separation of the rectangle from fill and stroke).

# Available Graphics Classes #
The available graphics classes are summarized below:
  * Shapes
    * `BasicShapeGraphic`
    * `DelegatingShapeGraphic`
    * `LabeledShapeGraphic`
  * Strings
    * `BasicStringGraphic`
  * Points
    * `BasicPointGraphic`
    * `DelegatingPointGraphic`
  * Point sets
    * `BasicPointSetGraphic`
    * `DelegatingPointSetGraphic`
  * Other
    * `DelegatingEdgeSetGraphic`
    * `DelegatingNodeLinkGraphic`

Two patterns are generally used for graphics. "Basic" graphics classes include style information directly within the class, while "delegating" graphics classes defer to an `ObjectStyler` to provide style information. The next section describes more about how style is handled.

# Graphics and Styles #
While styles are not part of the `Graphic` class API, most implementations of `Graphic` make use of styles to render the graphics. BlaiseGraphics has four style interfaces:
  * `PointStyle` (draws a point, or point+angle)
  * `PathStyle` (has stroke and thickness, draws a shape)
  * `ShapeStyle` (draws a shape)
  * `StringStyle` (draws a point+string, provides bounding box information)

## `StyleContext` ##
`StyleContext` is a supplier of each of the four above styles. In the graphics tree, it can be used to supply default styles for any supported graphic type that is added to the tree. It also provides a way to let `Graphic`s defer styles to a parent `GraphicComposite` object.

## `ObjectStyler` ##
`ObjectStyler` provides a way to make style information depend on the "source object" of a `Graphic`, so that styles are computed when the graphics are drawn rather than when they are created. They include the following attributes:
  * A _style delegate_ mapping source objects to a style of given type.
  * A _label delegate_ mapping source objects to labels.
  * A _label style delegate_ mapping source objects to `StringStyle`.
  * A _tip delegate_ mapping source objects to tooltips.
The delegating graphics classes maintain a source object and use an `ObjectStyler` to supply style, label, and tooltip information.
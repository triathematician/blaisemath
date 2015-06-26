# Module Dependencies #
BlaiseGraphics uses JDK 1.6+, and uses the [guava library](https://code.google.com/p/guava-libraries/) and jsr305 for building. It is a maven project with `groupId=com.googlecode.blaisemath` and `artifactId=blaise-graphics`. It is not yet available from the central maven repository.

# Packages #
| **Package** | **Description** | **Notes** | **Dependencies** |
|:------------|:----------------|:----------|:-----------------|
| `com.googlecode.blaisemath.graphics.core` | core `Graphic` API, generic and specific implementations of `Graphic`, event handlers | uses generics rather than a specific graphics canvas | style and utility packages |
| `com.googlecode.blaisemath.graphics.swing` | renderers, handlers, utilities, and graphic objects | uses swing's `Graphics2D` canvas | depends on style, utility, and core packages |
| `com.googlecode.blaisemath.style` | definitions of the main style classes, `AttributeSet`, `Renderer`, and `StyleModifier`, with supporting utilities |           | depends on utility packages only |
| `com.googlecode.blaisemath.util` | miscellaneous utility classes and functionality |           | external dependencies only |
| `com.googlecode.blaisemath.util.coordinate` | utilities for handling generic coordinates |           | external dependencies only |
| `com.googlecode.blaisemath.util.geom` | utilities for working with 2D points; extensions of java's `Point2D` |           | uses `com.googlecode.blaisemath.util.coordinate` only |

# Style Package #
The mains style classes are:
| **Class** | **Description** |
|:----------|:----------------|
| `AttributeSet` | Collection of style attributes |
| `ImmutableAttributeSet` | An attribute set that cannot be changed |
| `Renderer` interface | Renders a generic primitive on a generic canvas using an `AttributeSet` |
| `StyleHints` | A collection of hints that are used by a `StyleModifier` to modify an `AttributeSet` |
| `StyleModifier` interface | Modifies an `AttributeSet` based on a `StyleHints` object, returning a new `AttributeSet` |
| `StyleContext` | Handles a collection of style modifiers |
| `ObjectStyler ` | Provides several _delegate_ functions used to generate styles for an object of arbitrary type, specifically for _labels_, _label style_, _object style_, and _tooltip_ |
| `Styles`  | Utilities including factory methods, default styles, and style modifiers; also contains keys for common style attributes such as _fill_ and _stroke_ |
| `Marker` interface | Creates shapes centered at a given point, with a given radius and orientation |
| `Markers` | Contains several instances of `Marker` |
| `StyleUtilsSVG` | Provides a mechanism for converting an `AttributeSet` to an appropriate CSS attribute string |

The package is built around the combination of a `Renderer`, responsible for drawing the actual graphics elements, and the `AttributeSet`, which provides information to the renderer about how the drawing should be done. For instance, if you're drawing a square on the screen, the location of the square is the _primitive_, the `AttributeSet` describes its color, stroke width, etc., and the `Renderer` draws the square at the appropriate location using the appropriate style. This decomposition allows for rendering of the same basic objects onto multiple types of graphics targets.

`StyleHints`, `StyleModifier`, and `StyleContext` exist to provide a mechanism for quickly changing the appearance of many different objects. For instance, they can be used to change the color of an object when the user mouses over it. The mouse event handler would alter the `StyleHints` associated with the object. The modifiers in the `StyleContext` are then responsible for changing the appearance.

Both `AttributeSet` and `StyleContext` are "cascading", in that they support parent objects of the same type, and use information from the parent objects if not available in the object.

# Graphics Package #
In general, `Graphic` objects combine the state information (e.g. location, shape, etc.) with the style information used to draw them. All graphics provide support for:
  * style
  * rendering on a generic canvas
  * location testing (bounding box & rectangle intersect)
  * tooltips
  * style hints
  * context menu
  * highlighting on mouseover
  * selection
  * mouse handling

Graphics are combined in a "scene graph" using `GraphicComposite` classes. In the swing package, they are added to a `JGraphicComponent`, which has a special graphic composite `JGraphicRoot` for delegating events from the component to the graphics.

## Generic `Graphic` Objects ##
The general graphics classes are:
  * `Graphic` - abstract class, requiring implementations of `contains`, `intersects`, `getStyle`, and `renderTo`
    * `PrimitiveGraphicSupport` - adds a _primitive_, an associated _renderer_, and support for _mouse dragging_ (if it makes sense for the primitive)
      * `PrimitiveGraphic` - adds a _style_
      * `DelegatingPrimitiveGraphic` - adds an _object styler_ and a _source_ object
    * `PrimmitiveArrayGraphicSupport` - adds a list of _primitives_, a _renderer_, and functionality for choosing a _primitive_ based on a mouse location
      * `PrimitiveArrayGraphic` - adds a _style_ common to all primitives
      * `DelegatingPrimitiveArrayGraphic` - adds an _object styler_ and a _source_ object
  * `GraphicComposite` -

`GraphicSupport` implements all functionality on `Graphic`, with the exception of the _draw_, _contains_, and _intersects_ methods. It also adds support for multiple `ContextMenuInitializer`s, _highlighting_ on mouseover events, a _default tooltip_, and an _event trigger_ notifying the parent graphic when its state/style has changed. It provides flags for enabling/disabling _tooltips_, _menus_, _mouse listening_, and _selection_.

`GraphicComposite` maintains a list of graphics and a `StyleContext`. Its primary responsibility is to defer supported functionality to its children. It also listens to changes from its children.

`JGraphicRoot` hooks up a tree of graphic objects to a swing component, `JGraphicComponent`. It maintains a root `StyleContext`, the component's popup menu, and the conversion and delegation of `MouseEvent`s to `GMouseEvent`s. It cannot be added to another `GraphicComposite`. It also asks the component to repaint whenever a graphic in its tree has changed.

`JGraphicComponent` maintains a `JGraphicRoot`, a set of _underlays_ and _overlays_ (`CanvasPainter` objects), and a `SetSelectionModel` with a set of selected graphics.

## `Graphic` Implementations ##
The following are additional generic implementations of `Graphic`:
  * `LabeledPointGraphic`
  * `BasicPointSetGraphic`
In addition, the following composite graphics are derived from `GraphicComposite`:
  * `DelegatingEdgeSetGraphic`
  * `DelegatingNodeLinkGraphic`
  * `DelegatingPointSetGraphic`

The naming convention followed above:
  * `_Support` classes provide partial support, primarily the state of the graphic object to be represented.
  * `Basic_` classes contain one of the main style classes directly as a property.
  * `Delegating_` classes defer style, labels, and tooltips to an `ObjectStyler` class.

## Mouse Handling ##
The `JGraphicRoot` class is responsible for delegating mouse events to graphic objects beneath the mouse. Rather than regular `MouseEvent`s, it passes `GMouseEvent`s that indicate the source `Graphic` of the event. Classes supporting mouse handling are:
  * `GMouseEvent` - wraps a `MouseEvent` to include a `Graphic`
  * `GMouseEvent.Factory` - creates a `GMouseEvent` from a source `Graphic` and `MouseEvent` (intended to be overridden to customize the event beyond what is supported by the default `GMouseEvent`)

### Dragging ###
Additional classes are provided to simplify drag gestures:
  * `GMouseDragHandler` - provides supporting functionality for dragging graphics, translating general mouse handling methods to drag methods _mouseDragInitiated_, _mouseDragInProgress_, and _mouseDragCompleted_ that take as arguments `GMouseEvent`s and the starting `Point` of the drag gesture
    * `GraphicMoveHandler` - constructed with a `DraggableCoordinate` to provide move support
    * `IndexedPointMover` - provides move support for an array primitive

### Selection ###
Selection of graphics is built into `GraphicComponent`, but is enabled by default. The following class provides the mouse handling supporting selection of graphic objects:
  * `GraphicSelector`
The selector also implements `CanvasPainter`, allowing a selection box to be drawn on the component.

### Highlighting ###
  * `GraphicHighlighter` - changes the _highlight_ visibility hint as the mouse enters or exits graphics; enabled by default in `GraphicSupport`, but can also be disabled.



---

# Utility Library #
## Coordinate Handling ##
  * `CoordinateManager` - maintains a collection of points associated with source objects, as well as a cache of "stale" values for points no longer maintained in the collection
  * `CoordinateListener` - listener interface notified when coordinates within the manager change
  * `CoordinateChangeEvent` - may indicate add, remove, or combined add/remove of objects and points in the CoordinateManager

## Mouse Handling ##
  * `PointBean` - marks a class as having a point attribute
  * `DraggablePointBean` - provides a setPoint method used for drag gestures
  * `IndexedPointBean` - marks a class as having an indexed point attribute
  * `DraggableIndexedPointBean` - provides an indexed setPoint method used for drag gestures on one of many points in an object

## Selection ##
`SetSelectionModel` maintains a thread-safe set of selected items, and notifies listeners of changes to the set.

## Formatting ##
`PointFormatters` is a utility library for formatting Points and Point2Ds.

## Graph structures ##
`Edge` is a data structure with two vertices of common type, as typically used within a graph data structure.

## Graphics canvas ##
`CanvasPainter` is an interface with a method for painting on a `Graphics2D` canvas on a `Component`. This is primarily used in BlaiseGraphics as a lightweight method to render overlays like a selection box.

## Context menu ##
`ContextMenuInitializer` is an interface for preparing a context menu, whose method _initialize_ is used to populate a context menu with any actions appropriate for its parameters, which include (1) the source object/graphic, (2) the location, (3) a focus object (optional), and (4) a selection of objects (optional). The `Graphic` class extends this interface, and the `GraphicSupport` class allows multiple initializers to be added to a graphic.
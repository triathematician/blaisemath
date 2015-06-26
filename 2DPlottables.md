This page describes the planar plottables that have been implemented in `blaisecore`. See also the list of GenericPlottables.


# Implemented Features #

**Basic Plottables**
| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `PlaneAxes`       | Coordinate axes with custom labels and positions |                    | **Dyn**         |
| `PlaneBezierCurve` | A Bezier curve along with control points | `VPointSet`        | **Dyn**         |
| `PlaneEllipse`    | An ellipse whose width and height may be manipulated |                    | **Dyn**         |
| `PlaneGrid`       | An underlying grid |                    |                 |
| `PlanePolarGrid`  | A polar grid    |                    |                 |
| `PlaneTriangle`   | A shaded triangle between three points | `VPolygon`         | **Dyn**         |

**Function Plottables**
| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `PlaneCobwebFunction` | Displays a _cobwebbing_ set of lines to display the behavior of a dynamical system. | `VComputedPointPath` | **Dyn**         |
| `PlaneDESolution` | Displays solution to a single-variable first-order differential equation. | `VComputedPointPath` | **Dyn**         |
| `PlaneDE2Solution` | Displays solution to a system of two first-order differential equations. | `VComputedPointPath` | **Dyn**         |
| `PlaneFunctionGraph` | The graph of a simple function, with one input and one output. The domain is the visible portion of the real axis. | `VComputedPath`    |                 |
| `PlanePaddlePoint` | Displays a _"paddle"_ whose axis is fixed and which moves around in response to a vector field. | `VPoint`           | **Anim**, **Dyn** |
| `PlaneParametricArea` | A parametric area, over a supplied square domain (in **R**<sup>2</sup>), which is stored and drawn by a `VRectangle`. Uses an underlying function mapping **R**<sup>2</sup> to **R**<sup>2</sup>. The domain may be dragged around by the user. | `VComputedPath`    | **Dyn**         |
| `PlaneParametricCurve` | A parametric curve, over a supplied domain (interval in **R**). | `VComputedPath`    |                 |
| `PlaneSurfaceFunction` | Represents a function from **R**<sup>2</sup> to **R** by a collection of dots drawn at regular intervals along a grid. | `VPrimitiveMappingPlottable` |                 |
| `PlaneVectorField` | Displays a field of vectors representing (scaled) samples of a vector field. | `VPrimitiveMappingPlottable` |                 |

**Particle-Based Visualizations**
| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `PlaneParticleVectorField` | Displays a vector field as a collection of small particles that are "pushed around" by the field. | `VParticleField`   | **Anim**        |
| `PlaneParticleVectorFieldCurve` | Displays a particle visualization of a vector field where all particles begin along the curve and are then moved off of the curve. | `PlaneParticleVectorField` | **Anim**        |

# Dynamic Interaction with the Plot #

The class `PlanePlotResizer` allows the user to manipulate the boundaries of the plot window dynamically. Interaction modes are as follows:

  * `Drag` pans the window left/right/up/down.
  * `Alt+Drag` displays a box and zooms the window to that box.
  * `Shift+Drag` restricts movement to purely horizontal or vertical
  * `MouseWheel` zooms the plot in and out.

# Experimental & Under Construction #
  * `PlaneImplicitFunction`
  * `PlaneRandomPointsImplicitFunction`

# Old Features #
Features which are ~~crossed out~~ have been implemented in the new version of **blaise**. Those in _italics_ were not implemented in the old version of **blaise** either.

**General**:
~~`Point2D`~~, ~~`PointSet2D`~~, ~~`DynamicPointSet2D`~~, ~~`CirclePoint2D`~~
~~`Segment2D`~~, ~~`Vector2D`~~, ~~`Line2D`~~, ~~`Ray2D`~~, _`Slope2D`_
~~`Polygon2D`~~, ~~`Triangle2D`~~, ~~_`HalfPlane2D`_~~

**Functions**:
_`Piecewise2D`_, ~~`PlaneFunction2D`~~, `ContourPlot2D`, ~~`VectorField2D`~~
~~`FunctionSampleSet2D`~~, `FunctionPoint2D`, `SecantFunction2D`
~~_`IterativeSet2D`_~~, ~~`DESolution2D`~~

**Fun Stuff**:
~~`Clock2D`~~, `FractalEdge2D`

**Specialty**:
~~`Grid2D`~~, ~~`PolarGrid2D`~~
~~`Axes2D`~~, _`PolarAxes2D`_
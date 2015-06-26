These plottables are generic typed classes in the `blaisecore` package. Annotations are as follows:
  * (A) indicates an `AnimatingPlottable`
  * (D) indicates a `DynamicPlottable`

# Concrete Plottables #

| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `VClock`          |  draws a clock at an arbitrary position using the `ClockStyle` primitive style; position is tied to the underlying coordinate |`VInvisiblePoint`   | **Anim**, **Dyn** |
| `VLabel`          |Label at a local coordinate that may be moved around the visometry | `VInvisiblePoint`  | **Dyn**         |
| `VInvisiblePoint` | single point that may be moved around, but is not drawn |                    | **Dyn**         |
| `VLine`           | Line or vector between two points… multiple views of the line (e.g. as a vector, line, or segment) are supported via the style classes |                    | **Dyn**         |
| `VPath`           | A collection of points displayed as a path; coordinates may be set directly, but not manipulated |                    |                 |
| `VPolygon`        | Displayed as a filled in shape rather than a collection of points | `VPointSet`        | **Dyn**         |
| `VPoint`          | A single point that may be moved around the visometry | `VInvisiblePoint`  | **Dyn**         |
| `VPointSet`       | A collection of points that may be individually manipulated |                    | **Dyn**         |
| `VRectangle`      | Rectangle with draggable corners and sides |                    | **Dyn**         |

# Abstract Plottables #

| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `VComputedPath`   | Basic functionality for drawing a path, but requires the path to first be created by a subclass |                    | **Dyn**         |
| `VComputedPointPath` | stores a point which may be moved around freely; subclass is responsible for recomputing the path whenever the point is moved; structurally this is very similar to `VComputedPath` and they may possibly be merged in the future | `VPoint`           | **Dyn**         |
| `VParticleField`  | draws a collection of points (or small trails) displayed individually, and moving about based upon abstract methods |                    | **Anim**        |
| `VPrimitiveMappingPlottable` | draws a collection of primitive elements at various points specified by an underlying `SampleCoordinateSetGenerator` (see PrimitiveMappingPlottable) |                    |                 |
This page describes the 3d plottables that have been implemented in `blaisecore`. See also the list of GenericPlottables.

# Implemented Features #

**Basic Plottables**
| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `SpaceAxes`       | Displays axes and coordinate planes. |                    |                 |
| `SpaceBox`        | A test set of cubes near the origin. |                    |                 |

**Function Plottables**
| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `SpaceFunctionGraph` | Displays a function with two inputs and one output. |                    |                 |
| `SpaceParametricCurve` | Displays a parametric curve with one input and three outputs. |                    |                 |
| `SpaceParametricSurface` | Displays a parametric surface with two inputs and three outputs. |                    |                 |
| `SpaceVectorField` | Displays a function with three inputs and three outputs as a vector field. | `VPrimitiveMappingPlottable` |                 |
| `TwoCurveSurface` | Displays a parametric surface that is created as the span of two input curves. | `SpaceParametricSurface` |                 |


# Testing & Experimental #

| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `DerivedSpaceField` | Displays various fields that are "derived" from a basic field. | `SpaceVectorField` |                 |
| `SpaceParametricSurfacePatch` | Displays a single patch of a surface. |                    |                 |


# Dynamic Interaction with the Plot #

The class `SpacePlotResizer` allows the user to manipulate the boundaries of the plot window dynamically. Interaction modes that move or rotate the camera are as follows:

  * `Drag`: rotates the plot
  * `Ctrl+Drag`: animate rotation, based on the difference between the two mouse coordinates; does not rotate if the computed angle of rotation is too small
  * `Alt+Drag`: translate the center of interest in directions perpendicular to the view direction
  * `Alt-MouseWheel`: moves the camera to or from the center of interest

Here are the interactions that zoom:

  * `MouseWheel`: change the 2D-scale of the view (does not change the 3D projection)
  * `Shift+MouseWheel`: change the focal length, e.g. move the object of interest to/from the camera


# Old Features #

Features which are ~~crossed out~~ have been implemented in the new version of **blaise**. Those in _italics_ were not implemented in the old version of **blaise** either.

**General**:
~~`Point3D`~~, ~~`PointSet3D`~~, ~~`DynamicPointSet3D`~~, ~~`InitialPointSet3D`~~, ~~`Vector3D`~~, `Plane3D`, ~~`Ray3D`~~, ~~`Line3D`~~, _`Triangle3D`_

**Functions**:
~~`Function3D`~~, ~~`VectorField3D`~~, ~~`ParametricCurve3D`~~, ~~`ParametricSurface3D`~~, _`ContourPlot3D`_, `LineIntegral3D`, `FluxIntegral3D`, `DESolution3D`

**Specialty**:
~~`Axes3D`~~, ~~`StandardGrid3D`~~
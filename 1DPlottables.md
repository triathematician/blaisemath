This page describes the 1d plottables that have been implemented in `blaisecore`. See also the list of GenericPlottables.

# Implemented Features #

| **Name of Class** | **Description** | **Base Class(es)** | **Annotations** |
|:------------------|:----------------|:-------------------|:----------------|
| `LineAxis`        |Displays a line and tick marks to represent the axis. |                    |                 |
| `LineFunction`    | Displays an "input" point and an "output" point to represent a function with one real variable. | `VPoint`           | **Dyn**         |


# Dynamic Interaction with the Plot #

The class `LinePlotResizer` handles user interaction with the plot. Interaction modes are as follows:
  * `Drag` pans the axis left and right.
  * `Alt+Drag` displays a box and zooms the window to that box.
  * `MouseWheel` zooms the plot in and out.


# Features Planned & In Development #
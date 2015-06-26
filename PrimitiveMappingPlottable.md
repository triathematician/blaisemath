# Sample Use Cases #

  1. Riemann Sum visualization:
  1. Vector Field visualization:
  1. Vectors sampled along a curve:

# Relevant Interfaces and Classes #

interface `PrimitiveMapper<C,P>`

> _Functionality to associate a Primitive of type `P` to each coordinate `C` in a space. E.g. a vector field assigns arrows to each point in space. Implementing classes should describe precisely how this vector is constructed as a graphics primitive._

interface `SampleSetGenerator<C>`

> _Functionality to generate a list of coordinates of type `C`, e.g. a grid of points displayed on the screen, a grid of points displayed on a square, or a collection of points along a parametric curve._

class `VPrimitiveMappingPlottable<C, P> extends AbstractPlottable<C> implements PrimitiveMapper<C, P>, VisometryChangeListener`

> _Abstract plottable class implementing support for a sampling set._
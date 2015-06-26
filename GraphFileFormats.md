# File Formats in GraphExplorer #
In the GraphExplorer interface, file formats are determined by the selection of file type in the load/save file dialog. Several different file formats for graphs are supported. Descriptions follow below.

_NOTE:_ "edge" denotes an undirected edge in a graph, while "arc" denotes a directed edge.


---


# _Edge list_ #
The simplest file contains a pair of integers (representing an arc) on each line, for example:
```
1 5
2 3
1 4
3 4
4 5
```
The integers may also be separated by any combination of commas and spaces:
```
1, 5
2, 3
1  ,4
3, 4
4 , 5
```
Anything after the second integer is ignored. The returned result is a directed graph, whose vertices correspond to the integers in the file.


---


# _pajek_ #

The _pajek_ file format (`.net`) has more support than any other format, although still only a few features from the standard format are supported. _Pajek_ files use integers to represent nodes, and are divided into sections whose headers begin with an asterisk `*`.

Standard _pajek_ files (usually `.net`) usually contain a section describing the vertices and a section describing the edges. If the vertex section is omitted, the vertices in the resulting graph are inferred from the edges.

_General comments:_
  * Case in section headings is ignored: `*VERTICES` works just as well as `*Vertices`
  * Only the first section describing a collection of edges or arcs is used; the remainder are ignored.
  * Any additional options used by _pajek_ to describe appearance may be given at the end of an input line, but are ignored.
  * Empty lines or lines consisting of just spaces in the file are ignored.


## Vertex Mode ##
A standard vertex declaration is:
```
*Vertices 4
1 "vertex 1"
2 "vertex 2"
3 "vertex 3"
4 last_vertex
```
The first entry in each line is the node's index, and the second is the node's label. Quotes are necessary if there is a space in the label. It is permissible to omit the label, but only if the line is not followed by another argument.

  * **Position argument**: The label may be followed by two (or three) float values representing the position of the node. A third float may be given, but is currently ignored. For example:
```
*Vertices 4
1 "O" 0.0 0.0
2 "1" 1.0 0.0
3 "i" 0.0 1.0
4 "-i" 0.0 -1.0
```

  * **Time argument**: If a bracketed expression `[...]` occurs somewhere in the line after the label, it represents one or more time intervals at which the vertex "exists". Each time interval expression consists of a list of time intervals (integers or floats), or single time instances. The `*` indicates plus or minus infinity, depending on its relative position. _**WARNING:** strict_pajek_may require only integer times... I'm not sure on this._ For example:
```
*Vertices 2
1 "A" [0,3.2-*]
2 "B" [1-2,3.5-4.5,5-6]
```

  * **Other arguments**: Sorry, no other arguments (shape, color, size, etc.) are currently supported.

## Edge list Modes (1 per line) ##
To represent arcs (undirected edges) with one per line:
```
*Arcs
1 2
2 3
3 4
4 5
```
To represent edges, the format is the same
```
*Edges
1 2
2 3
3 4
4 5
```

  * **Weight argument**: A float representing an edge weight may be used as a third argument. Any omitted value is assumed to be 1. Negative values are permissible. For example:
```
*Edges
1 2 1.0
2 3 -3.2
3 4 
4 5 4
```

  * **Time argument**: If a bracketed expression `[...]` occurs somewhere in the line after the edge, it represents one or more time intervals at which the edge"exists". The format is the same as for vertices, for example:
```
*Edges
1 2 [0,3.2-*]
2 3 [1-2,3.5-4.5,5-6]
```

## Edge list Modes (multiple per line) ##
If the arcs/edges are unweighted, list modes can be used to include more than one edge per line. The directed version is:
```
*Arcslist
1 2 3 4 5 6
5 2 3
6 1
```
This creates a graph with edges pointing from 1 to each of 2,3,4,5,6; from 5 to each of 2,3; and from 6 to 1.

The undirected version works the same:
```
*Edgeslist
1 2 3 4
4 2 3
```

## Matrix Mode ##
The matrix mode represents a weighted (or unweighted) graph with a single matrix, for example, here is a directed, unweighted graph:
```
*Matrix
0 0 1 1
1 0 0 1
0 1 0 0
1 0 0 0
```
And an undirected weighted graph:
```
*Matrix
0.0 2.0 1.0 0.0
2.0 0.0 3.5 -1.0
1.0 3.5 0.0 0.0
0.0 -1.0 0.0 0.0
```
_**NOTE:** In the case of a matrix, the resulting graph will be undirected if the matrix is symmetric._


---


# Modified _pajek_ #
The _modified pajek_ format (`.net` or `.netx`) was created for the specific use of GraphExplorer and adds on a few additional modes/features. All of the features of _pajek_ files are supported. In addition:
  * Any line beginning with `%` represents a comment and is ignored.
There are currently two additional modes in _modified pajek_.

## Description Mode ##
The description mode allows for comments that describe the file. This section of the file is currently ignored. For example:
```
*Description
This file represents the familiarity of fifteen famous frog-kings of ancient Tridentia.
```

## Image Mode ##

The image mode allows one to represent the graph using images at the nodes. This feature is experimental, but works in basic situations. The format is similar to the vertex mode, with an index followed by a file name. The file paths should be relative to the file being loaded. For example:
```
*Images
1 "andrew.jpg"
2 "bob.jpg"
3 "cathy.jpg"
4 "denise.jpg"
5 "ed.jpg"
6 "frank.jpg"
```


---


# _UCINET_ #
See also http://www.analytictech.com/networks/dataentry.htm.

The _UCINET_ format consists of a _header_ section and a _data_ section. A sample header section is:
```
DL
N=17 NM=1
format = fullmatrix diagonal present
data:
...
```
The `DL` line declares the start of the header. The `N=17` sets the number of vertices. The `NM=` declares the number of expected matrices. The `format` argument specifies the format of the data to follow (in this case, a full 17x17 adjacency matrix). The `data:` line declares the start of graph data. Comments:
  * May be placed in a single line: `DL N=17 NM=1`.
  * The number of matrices is assumed to be 1 if not specified.
  * The default format is (I believe) `fullmatrix`.

Optionally, labels for the vertices may be declared before the data is specified (labels cannot contain spaces or commas, and may be on separate lines). In this case, a labels section is added before the `data:` section:...
format = ...
labels:
a, b, c, d
e f g
data:
...
}}

Alternately, the labels may be embedded within the data, requiring a `labels embedded` command:
{{{
...
format = ...
labels embedded
...
}}}

== Data Formats ==

The currently supported data formats are `fullmatrix`, `edgelist`, and `nodelist1`.

-----

= _GraphML_ =
_GraphML_ is an XML format with partial support.

-----

= _DyNetML_ =
_DyNetML_ is another XML format with partial support.```
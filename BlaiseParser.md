# Overview #
The **BlaiseParser** library provides basic parsing functionality, currently supporting parsing **real-valued expressions** and basic **boolean-valued expressions**. The parser is _case insensitive_ and also supports various _pseudonyms_ (e.g. "avg" and "average" can both be used for the average of a list of numbers). Developers can download the library as a `.jar` file under the **Downloads** tab. Documentation may be downloaded there, and is also available from https://blaisemath.googlecode.com/svn/tags/pre-maven/BlaiseParser/dist/javadoc/index.html. The source code for **BlaiseParser** is contained in the `BlaiseParser` folder.

The API for **BlaiseParser** is built to be as simple to use as possible. Here is a sample usage, to compile and evaluate the expression `sin(x)` at the value `x=pi`:
```
String stringToParse = "sin(x)";
String variable = "x";
Double value = Math.PI;

SemanticNode sn = RealGrammar.getParser().parseTree(stringToParse);
HashMap<String,Double> variables = new HashMap<String,Double>();
variables.put(variable, value);
sn.assignVariables(variables);
Double result = sn.getValue();

System.out.println("The value of " + stringToParse 
    + " at " + variable + "=" + value
    + " is " + result);
```

# Features #

## Built-in Operators ##

| Name | Description | Type | Remarks |
|:-----|:------------|:-----|:--------|
| `-`, `/`, `^` | subtraction, division, power | binary | ok      |
| `%`  | modulus, e.g. "8%5" is 3 | binary | ok      |
| `*`, `+` | multiplication, addition | multary | ok      |
| `-`  | negation, e.g. "-5" | unary (prefix) | ok      |
| `!`  | factorial, e.g. "5!" is 120 | unary (postfix) | ok      |

## Built-in Constants ##

| Name | Description | Remarks |
|:-----|:------------|:--------|
| `pi` | pi=3.141592653589793... | ok      |
| `e`  | e=2.718281828459045... | ok      |

## Built-in Functions ##

**Power Functions**
| Name | Description | # of Arguments | Remarks |
|:-----|:------------|:---------------|:--------|
| `sqrt` or `squareroot` | square root of the argument | 1              | ok      |
| `cbrt` or `cuberoot` | cube root of the argument | 1              | ok      |
| `hypot` or `hypotenuse` | hypotenuse of two arguments, e.g. "hypot(3,4)" is 5 | 2              | ok      |

**Trig Functions**
| Name | Description | # of Arguments | Remarks |
|:-----|:------------|:---------------|:--------|
| `sin` or `sine` | sine        | 1              | ok      |
| `cos` or `cosine` | cosine      | 1              | ok      |
| `tan` or `tangent` | tangent     | 1              | ok      |
| `asin` or `arcsin` or `arcsine` | inverse of the sine function | 1              | ok      |
| `acos` or `arccos` or `arccosine` | inverse of the cosine function | 1              | ok      |
| `atan` or `arctan` or `arctan` | inverse of the tangent function | 1              | ok      |
| `atan2` | inverse tangent of the argument as a point `(x,y)`, where `y` is the first provided argument and `x` is the second; value is between -pi and pi | 2              | ok      |

**Exponentials, Logs, and Hyperbolic Functions**
| Name | Description | # of Arguments | Remarks |
|:-----|:------------|:---------------|:--------|
| `exp` | exponential of the argument, ie. e^x | 1              | ok      |
| `log` or `ln` | natural logarithm | 1              | ok      |
| `log2` | base-2 logarithm | 1              | ok      |
| `log10` | base-10 logarithm | 1              | ok      |
| `sinh` | hyperbolic sine | 1              | ok      |
| `cosh` | hyperbolic cosine | 1              | ok      |
| `tanh` | hyperbolic tangent | 1              | ok      |

**Rounding Functions**
| Name | Description | # of Arguments | Remarks |
|:-----|:------------|:---------------|:--------|
| `abs` or `absolutevalue` | absolute value of the argument | 1              | ok      |
| `floor` | first integer at or below the argument | 1              | ok      |
| `ceil` or `ceiling` | first integer at or above the argument | 1              | ok      |
| `round` or `rint` | closest integer to the argument | 1              | ok      |
| `signum` or `sign` or `sgn` | the sign of the argument, as +1 or -1 | 1              | ok      |
| `frac` | the "fractional" part of the argument (i.e. what follows the decimal) | 1              | ok      |

**Random Functions**
| Name | Description | # of Arguemnts | Remarks |
|:-----|:------------|:---------------|:--------|
| `rand` or `random` | generates a random number between 0 and 1 | 0              | ok      |
| `rand` or `random` | generates a random number between two arguments | 2              | ok      |

**Statistical Functions**
| Name | Description | # of Arguemnts | Remarks |
|:-----|:------------|:---------------|:--------|
| `avg` or `average` | average of multiple comma-separated values | any            | ok      |
| `min` or `minimum` | minimum of multiple comma-separated values | any            | ok      |
| `max` or `maximum` | maximum of multiple comma-separated values | any            | ok      |
| `sum` or `summation` | sum of multiple comma-separated values | any            | ok      |

## Built-in Branching Statements ##

| Name | Description | Remarks |
|:-----|:------------|:--------|
| `if` | conditional switch | not working |
| `sum` or `summation` | summation over indexed set | not working |
| `prod` or `product` | product over indexed set | not working |
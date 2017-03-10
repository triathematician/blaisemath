# blaise-common `util` package

This contains general purpose utility classes that are used elsewhere in blaise code libraries.

## Threads and Concurrency

Most classes are not designed for multi-threading. Exceptions are:

 - `com.googlecode.blaisemath.util.coordinate.CoordinateManager`,
   a thread-safe object for managing a collection of object locations.
 - `com.googlecode.blaisemath.util.swing.AnimationStep`,
   which launches a background thread for animation effects. The class
   contains a static `ScheduledExecutorService`, and the static method
   `runNTimes()` can be used to run animations using that service.
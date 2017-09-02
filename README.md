# Mystery
Obfuscates the Java class contents of JAR files, using experimental and hopefully interesting ways.

## Features so far
* Class and field renaming (numeric names like ``public class -345364789`` and ``static int 2107639900;`` for an ``unexpected token`` error when compiling following decompilation).
* Shuffles around the positions of fields, methods, annotations and other identifiers.
* Adds bogus exceptions to methods throws clause.
* Removes or replaces line numbers with random integers that can be mapped back to the original numbers.

## Libraries

* ObjectWeb ASM
* Google Guava
* Commons CLI

## License
Apache License 2.0: http://www.apache.org/licenses

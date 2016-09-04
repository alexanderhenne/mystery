# Mystery
Obfuscates Java JAR files

## Features so far
* Class and field renaming
 * RandomNumber - numeric names like ``public class -345364789`` and ``static int 2107639900;`` for an ``unexpected token``error when compiling following decompilation. Names are also reused as much as possible, to abuse another practice forbidden in the language specification.

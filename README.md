# Mystery
Obfuscates the Java class contents of JAR files, using experimental and hopefully interesting ways. Requires Java 8 and an open mind.

## Features so far
* Class and field renaming
 * RandomNumber - numeric names like ``public class -345364789`` and ``static int 2107639900;`` for an ``unexpected token``error when compiling following decompilation. Names are also reused as much as possible, to abuse another practice forbidden in the language specification.
* Identifier shuffling
 * Shuffles around the positions of fields, methods, annotations and other identifiers that an attacker could use to recognize a member.

## Libraries

* ObjectWeb ASM
* Google Guava
* Commons CLI

## License
Apache License 2.0: http://www.apache.org/licenses

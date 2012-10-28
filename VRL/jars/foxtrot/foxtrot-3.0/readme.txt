Welcome to Foxtrot, the easy API for the Java(TM) Foundation Classes (JFC/Swing).

INTRODUCTION

Foxtrot is bundled in a compressed file, that contains
javadoc documentation, libraries, examples and source code.

The structure of the compressed file is as follows:

apidocs/ contains the Foxtrot javadocs
lib/     contains the Foxtrot jars: the core jar and the examples jar
src/     contains the Foxtrot source code

The core jar is lib/foxtrot-core-<version>.jar and must be included in the classpath.
It is no longer necessary, from Foxtrot version 1.2, to include the Foxtrot core jar in the boot classpath.
This way, Foxtrot is compatible with Java Web Start.

The distribution root directory contains this file and the BSD license.


USAGE

If a Swing application is contained in myapp.jar and uses Foxtrot 3.0,
then the command line to start it would be similar to this one (in Windows):

> java -cp foxtrot-core-3.0.jar;myapp.jar my.app.Main


EXAMPLES

To run the examples included in the distribution, use the following
command line (from the distribution root directory):

> java -cp lib\foxtrot-core-3.0.jar;lib\foxtrot-examples-3.0.jar foxtrot.examples.SimpleExample
> java -cp lib\foxtrot-core-3.0.jar;lib\foxtrot-examples-3.0.jar foxtrot.examples.InterruptExample


TESTS

From Foxtrot version 2.0, tests are based on JUnit 3.8.1.


MORE INFORMATION

Refer to the documentation on the web site (http://foxtrot.sourceforge.net)
for further details on how to use the Foxtrot API.

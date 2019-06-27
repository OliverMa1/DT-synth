Solver for Automatic Safety Games
=================================

This file describes how to compile and run the prototype solver for automatic
safety games.

Currently, the prototype does not provide a user interface and games are given
as Java classes. That means that the desired game has to be selected in the
sources (as described below) and the prototype needs to be recompiled.


Prerequisites
-------------

The prototype requires Microsoft Z3 4.3.2 to run. Z3 can be obtained at

    https://github.com/Z3Prover/z3

either as a binary for Windows or as source code.

If downloaded as source code, Z3 needs to be compiled with Java support (i.e.,
using the command "python scripts/mk_make.py --java"). A successful build should
produce the files libz3java.dll and libz3.dll (on Windows), respectively
libz3java.so and libz3.so (on Linux). Refer to Z3's README for further details.

When compiled or downloaded, copy the files libz3.dll and libz3java.dll
(respectively libz3.so and libz3java.so) to the ./lib directory.

Additional prerequisites are a Java Runtime environment and a Java compiler
(preferably version 8) as well as the ANT build tool.


Compiling the prototype
-----------------------

The sources of the prototype contains a build script for the ANT build tool. To
compile the sources using ANT, change into the directory containing the file
build.xml and simply execute the command "ant". The compiled code is copied to
the ./bin directory. (In addition a .jar file is created.)


Running the prototype
---------------------

To run the prototype, several options have to be passed to the Java runtime. In
particular, the following has to be specified:

1) Classpath:
    
	-cp ./lib/automaton.jar;./lib/com.microsoft.z3.jar;./bin/
  
   (On Linux, the separator : has to be used instead of ;)
  
2) Library path (needs to point to the directory containing libz3java.dll,
respectively libz3java.so):

    -Djava.library.path=./lib/

3) The operating system needs to know the location of the file libz3.dll,
respectively libz3.so.  On Windows, this is achieved by adding the corresponding
directory to the PATH environment variables.  On Linux, this is achieved by
adding the corresponding directory to the LD_LIBRARY_PATH environment variables.

On Windows, for instance, the prototype can be started using the command

    java -cp .\bin\;.\lib\automaton.jar;.\lib\com.microsoft.z3.jar -Djava.library.path=.\lib\ edu.illinois.automaticsafetygames.finitelybranching.main.Algorithm
         
         

The prototype outputs statistics of the learning process on termination. If
desired, the one can change the sources to (also) output the learned automaton
(see below).


Changing games
--------------

The classes

    edu.illinois.automaticsafetygames.finitelybranching.main.Algorithm 
	
is responsible for running the prototype. To select a different game or to
change the output of the prototype, this class needs to be edited.

To select a different game, locate the line starting with "IGame game = ...;"
and instantiate the desired game. The package

    edu.illinois.automaticsafetygames.finitelybranching.main.examples

contains all available games. More details about the games (in particular, on
how the games are encoded) can be found in their class files. All games are
derived from a self-explanatory interface IGame, which makes creating new games
straightforward.

To select a different learner, locate the line starting with "ILearner = ...;"
and instantiate either a Z3LearnerSAT object or a RPNILearner object.

To output the learned automaton, uncomment the last System.out.println(...) in
the main method. 

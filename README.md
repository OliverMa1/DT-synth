# DT-synth
Decision Tree Learning Algorithm for Reactive Synthesis
=================================

This file describes how to compile and run the prototype solver for automatic
safety games.

Currently, the prototype does not provide a user interface and games are given
as .json files. This means that the desired game has to be selected in the
sources (as described below) and the prototype needs to be executed with the game as input.


Prerequisites
-------------


Prerequisites are a C++ compiler and C++ runtime environment. Use "sudo apt-get install build-essential"
on Linux to install the prerequisites. On Windows install MinGW or GNUWin32.


Compiling the prototype
-----------------------

Run the makefile in the DT-synth directory with the command "make". This will build z3 and install it as well build and 
compile the prototype.

Running the prototype
---------------------

To run the prototype, several options have to be passed to the C++ runtime. In
particular, the following has to be specified:

1) Game Input:
    
    this is found in data/benchmarks/
    it is required to pick an benchmark in .json format as an input.
    One example is : 
       ./main data/benchmarks/boxGame.json
  
2) The operating system needs to know the location of the file libz3.dll,
respectively libz3.so.  On Windows, this is achieved by adding the corresponding
directory to the PATH environment variables.  On Linux, this is achieved by
adding the corresponding directory to the LD_LIBRARY_PATH environment variables.

On Linux, for instance, the prototype can be started using the command

    ./main data/benchmarks/boxGame.json     
         

The prototype outputs statistics of the learning process on termination 
and all the decision trees produced in the intermediate steps.


Changing games
--------------
Most of the benchmarks are available as .json file in data/benchmarks.
However, the scalable benchmarks are saved as an C++ file in data/benchmarks,
for example GridWorldSequence1D.cpp. Upon compiling and executing this file with 
one parameter, it produces a GridWorldSequence1D.json file with respect to the
parameter. 

One can also create a game by writing a new .json file. Such a file needs
the name of the variables ("variables") and the name of the variables in the next step
("variables_dash"), the maximum amount of successors for each vertex ("successors"),
any additional expressions like "x+y" or "x-y" and the game encoded in SMT-LIB.
A template can be found in data/benchmarks/smt2template/input.smt2.


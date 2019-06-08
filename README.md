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

The prototype requires Microsoft Z3 4.8.0 to run. Z3 can be obtained at

    https://github.com/Z3Prover/z3
    
A successful build should produce the files libz3java.dll and libz3.dll (on Windows), respectively
libz3java.so and libz3.so (on Linux). Refer to Z3's README for further details.

When compiled or downloaded, copy the files libz3.so
(respectively libz3.dll) to the ./lib directory.

Additional prerequisites are a C++ compiler and C++ runtime environment. Use "sudo apt-get install build-essential"
on Linux to install the prerequisites. On Windows install MinGW or GNUWin32.


Compiling the prototype
-----------------------

The sources of the prototype contains a makefile. To compile the sources, change 
into the directory containing the makefile and execute the command "make".


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

    ./main ../benchmarks/infiniteArenaBenchmarks/boxGame.json     
         
starting from the directory ./DT-Synth.

The prototype outputs statistics of the learning process on termination 
and all the decision trees produced in the intermediate steps.

Running the Benchmark Suite
-----------------------
The following instructions describe how the benchmarks can be run on a machine that has the Horn-ICE verification toolkit installed as described above. We provide two different types of benchmarks:

  1. Benchmarks over infinite arena games
  2. scalability benchmarks

For more detailed instructions view instructions.html.

Running the Benchmark Suits and comparison to other Tools
--------------
The entire benchmark suite can be executed using the following command from inside the ./dt/artifact-evaluation-master/scripts/ directory:

sh runAll.sh
The runAll.sh script will generate eight log files, as described below. 
  1. tabulated_infinite_dt.csv lists all the benchmark programs for the infinite arena benchmark using the dt-synth 
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive,
 Negative and Horn samples, the total time taken by the learner and the verification process.
     
  2. tabulated_infinite_rpni.csv lists all the benchmark programs for the infinite arena benchmark using the RPNI solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process.
     
  3. tabulated_infinite_sat.csv lists all the benchmark programs for the infinite arena benchmark using the SAT solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 
     
  4. tabulated_scalability_dt.csv lists all the benchmark programs for the scalability benchmark using the dt-synth 
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 
    
  5. tabulated_scalability_fixed.csv lists all the benchmark programs for the scalability benchmark using the fixed-point algorithm
     program with execution details. The execution details includes the total time taken by the learner and the verification process. 
   
  6. tabulated_scalability_rpni.csv lists all the benchmark programs for the scalability benchmark using the RPNI solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 
     
  7. tabulated_scalability_sat.csv lists all the benchmark programs for the scalability benchmark using the SAT solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive,
 Negative and Horn samples, the total time taken by the learner and the verification process. 
 
  8. tabulated_scalability_dt.csv lists all the benchmark programs for the scalability benchmark using TuLiP 
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 

Running an Individual Benchmark Set
--------------

The ./dt/artifact-evaluation-master/scripts/ contains scripts to execute individual sets of benchmarks and to tabulate their results:

  - The infinite arena benchmarks can be executed and tabulated with sh ./run_infinite.sh

  - The scalability benchmarks can be executed and tabulated with sh ./run_scalability.sh
  
Comparing DT-Synth to GAVS+
--------------

There is no script automating the benchmarks for GAVS+, thus you need to execute following steps to compute the benchmarks:

The python scripts are used to generate the .pddl domain and problem files.

1- Run the python script in ./dt/artifact-evaluation-master/GAVS+. Edit the first line "k = 100" to the desired value to test.

2- Run GAVS+ (use: java -jar GAVS+.jar in ./dt/artifact-evaluation-master/GAVS+/GAVS+_dist_20160404/GAVS+_dist )

3- Go to (GAVS+ ->Planning Domain Definition Language -> Solve PDDL using Symbolic
Games(domain,problem) -> Safety) then select the domain file then the problem file and the solver will run.



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
A template and a README can be found in /benchmarks/smt2template/


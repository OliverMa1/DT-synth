# DT-synth
Decision Tree Learning Algorithm for Reactive Synthesis
=================================

This file describes how to compile and run the prototype solver for automatic
safety games.

Currently, the prototype does not provide a user interface and games are given
as .json files. This means that the desired game has to be selected in the
sources (as described below) and the prototype needs to be executed with the game as input.
Moreover, it is supported under Linux only. 


Compiling the prototype
-----------------------

The sources of the prototype contains a makefile. To compile the sources, change 
into the directory containing the makefile and execute the command "make". Starting from the 
original directory this looks like this:

    cd DT-synth 
    make 




Running the prototype
---------------------

To run the prototype, several options have to be passed to the C++ runtime. In
particular, the following has to be specified:

1) Game Input:
    
    this is found in 
    /benchmarks/infiniteArenaBenchmarks
    it is required to pick an benchmark in .json format as an input.

  
2) The operating system needs to know the location of the file libz3.so. 
On Linux, this is achieved by adding the corresponding directory to 
the LD_LIBRARY_PATH environment variables

Use:

    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:PATH_TO_CURRENT_DIRECTORY/lib/z3/bin/   
     
or 

     export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./lib/z3/bin/

The z3 files are precompiled binaries from  https://github.com/Z3Prover/z3/releases version 4.85.

On Linux the prototype can be started using the command

    ./main ../benchmarks/infiniteArenaBenchmarks/boxGame.json     
         
starting from the directory ./DT-Synth.

The prototype outputs statistics of the learning process on termination 
and all the decision trees produced in the intermediate steps. The last line of the output can be ignored as it is used
by the scripts to generate a .csv file.

## Running the Benchmark Suite of the paper
-----------------------
The following instructions describe how the benchmarks can be run on a machine that has the Horn-ICE verification toolkit installed as described above.

### Running the Benchmark Suits and comparison to SAT-SYNTH and RPNI-SYNTH
--------------
The entire benchmark suite of the paper can be executed using the following command from inside the ./scripts directory:

    sh runAll_Paper.sh

If the script is not enabled run this command first:

    chmod +x runAll_Paper.sh

The runAll.sh script will generate three log files in the directory /scripts/Tabulated-Data, as described below. 
  1. tabulated_infinite_dt.csv lists all the benchmark programs for the infinite arena benchmark using the dt-synth 
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive,
 Negative and Horn samples, the total time taken by the learner and the verification process.
     
  2. tabulated_infinite_rpni.csv lists all the benchmark programs for the infinite arena benchmark using the RPNI solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process.
     
  3. tabulated_infinite_sat.csv lists all the benchmark programs for the infinite arena benchmark using the SAT solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 
     
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
  
  
## Running scalability benchmarks and comparison to other tools

This section describes how to run the scalability benchmarks and compare the results to GAVS+ and TuLiP. 
Note that this requires TuLiP to be installed:

    sh runAll_Scalability.sh
    
The runAll_Scalability.sh script will generate five log files in the directory /scripts/Tabulated-Data, as described below.     
  1. tabulated_scalability_dt.csv lists all the benchmark programs for the scalability benchmark using the dt-synth 
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 
    
  2. tabulated_scalability_fixed.csv lists all the benchmark programs for the scalability benchmark using the fixed-point algorithm
     program with execution details. The execution details includes the total time taken by the learner and the verification process. 
   
  3. tabulated_scalability_rpni.csv lists all the benchmark programs for the scalability benchmark using the RPNI solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 
     
  4. tabulated_scalability_sat.csv lists all the benchmark programs for the scalability benchmark using the SAT solver
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive,
 Negative and Horn samples, the total time taken by the learner and the verification process. 
 
  5. tabulated_scalability_dt.csv lists all the benchmark programs for the scalability benchmark using TuLiP 
     program with execution details. The execution details includes number of rounds between teacher and learner, number of Positive, 
Negative and Horn samples, the total time taken by the learner and the verification process. 


### Comparing DT-Synth to GAVS+
--------------

There is no script automating the benchmarks for GAVS+, thus you need to execute following steps to compute the benchmarks:

The python scripts are used to generate the .pddl domain and problem files.

1- Run the python script in ./dt/artifact-evaluation-master/GAVS+. Edit the first line "k = 100" to the desired value to test.

2- Run GAVS+ (use: java -jar GAVS+.jar in ./dt/artifact-evaluation-master/GAVS+/GAVS+_dist_20160404/GAVS+_dist )

3- Go to (GAVS+ ->Planning Domain Definition Language -> Solve PDDL using Symbolic
Games(domain,problem) -> Safety) then select the domain file then the problem file and the solver will run.






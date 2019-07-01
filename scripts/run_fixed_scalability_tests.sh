#! /bin/bash

path="$(dirname "$PWD")"/SAT_RPNI_FIXED/rational_safety
path1="$(dirname "$PWD")"/scripts/data
echo "$path"
echo "$path1"
run_the_test() {
  cd $path
  ant
  export LD_LIBRARY_PATH=$LIBRARY_PATH:./lib/
  cp $path1/FixedpointAlgorithm.java $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/FixedpointAlgorithm.java
  for x in 20 40 80 120 160 200 250 300; do 
    echo $x
    sed -i "97s/.*/IGame game = new GridWorldSequence1D(${x});/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/FixedpointAlgorithm.java
    ant
    java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/FixedpointAlgorithm > $path1/fixedScale/fixed$x.time
  done
  echo "Test over."
}
run_the_test

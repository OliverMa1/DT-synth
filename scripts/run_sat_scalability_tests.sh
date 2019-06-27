#! /bin/bash

path="$(dirname "$PWD")"/SAT_RPNI_FIXED/rational_safety
path1="$(dirname "$PWD")"/scripts/data

echo "$path"
echo "$path1"
run_the_test() {
  cd $path
  export LD_LIBRARY_PATH=$LIBRARY_PATH:./lib/
  ant
  sed -i '239s/.*/ILearner learner = new Z3LearnerSAT(game.getAlphabetSize());/' $path1/Algorithm.java
  cp $path1/Algorithm.java $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
  for x in 20 40 80 120 160 200 250 300 320; do 
    echo $x
    sed -i "227s/.*/IGame game = new GridWorldSequence1D(${x});/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
    ant
    timeout 600 java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satScale/sat$x.time
  done
  echo "Test over."
}
run_the_test

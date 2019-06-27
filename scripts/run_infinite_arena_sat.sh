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
  echo "Running Diagonal Limited"
  sed -i "227s/.*/IGame game = new DiagonalGame();/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
  ant
  timeout 300 java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satInfinite/satDiagonal.time
  
  echo "Running Box Game"
  sed -i "227s/.*/IGame game = new BoxGame();/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
    ant 
    java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satInfinite/box.time

  echo "Running Solitary Box Game"
  sed -i "227s/.*/IGame game = new SolitaryBoxGame();/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
    ant
    java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satInfinite/solitaryBox.time

  echo "Running Evasion Game"
  sed -i "227s/.*/IGame game = new EvasionGame();/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
    ant
    timeout 300 java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satInfinite/evasion.time

  echo "Running Follow Game"
  sed -i "227s/.*/IGame game = new Follow2D();/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
    ant
    timeout 300 java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satInfinite/follow.time

  echo "Running Program-repair Game"
  sed -i "227s/.*/IGame game = new ProgramRepairGame();/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
    ant
    java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satInfinite/programrepair.time

  echo "Running Square Game"
  sed -i "227s/.*/IGame game = new Quadrat();/" $path/src/edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm.java
    ant
    timeout 300 java -cp ./bin/:./lib/automaton.jar:./lib/com.microsoft.z3.jar -Djava.library.path=./lib/ edu/illinois/automaticsafetygames/finitelybranching/main/Algorithm > $path1/satInfinite/square.time
  echo "Test over."
}
run_the_test

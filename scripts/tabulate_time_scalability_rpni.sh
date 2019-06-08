#! /bin/bash

path="$(dirname "$PWD")"/scripts/data/rpniScale

elapsed() {

  grep ", " $1 | tail -1  

}



run_the_test() {

  FileNames=$(ls $path/*.time)
    echo "Benchmark program, Total learning time(ms), Size of solution, Positive Counterexamples, Negative counterexamples, Existential implication counterexamples, Universal implications counterexamples"> Tabulated-Data/tabulated_scalability_rpni.csv
  for file_name in $FileNames; do
    echo $(basename $file_name) ", " $(elapsed $file_name) >> Tabulated-Data/tabulated_scalability_rpni.csv
  done

  echo "Test over."
}
run_the_test

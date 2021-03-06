#! /bin/bash

path="$(dirname "$PWD")"/scripts/data/rpniInfinite

elapsed() {

  grep ", " $1 | tail -1  

}



run_the_test() {
  touch Tabulated-Data/tabulated_infinite_rpni.csv
  FileNames=$(ls $path/*.time)
    echo "Benchmark program, Total learning time(ms), Size of solution, Positive Counterexamples, Negative counterexamples, Existential implication counterexamples, Universal implications counterexamples"> Tabulated-Data/tabulated_infinite_rpni.csv
  for file_name in $FileNames; do
    echo $(basename $file_name) ", " $(elapsed $file_name) >> Tabulated-Data/tabulated_infinite_rpni.csv
  done

  echo "Test over."
}
run_the_test

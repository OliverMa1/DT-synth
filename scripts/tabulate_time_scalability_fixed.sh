#! /bin/bash

path="$(dirname "$PWD")"/scripts/data/fixedScale

elapsed() {

  grep " " $1 | tail -1  

}



run_the_test() {

  FileNames=$(ls $path/*.time)

    echo "Benchmark program, Total Time(ms)"> Tabulated-Data/tabulated_scalability_fixed.csv
  for file_name in $FileNames; do
    echo $(basename $file_name) ", " $(elapsed $file_name) >> Tabulated-Data/tabulated_scalability_fixed.csv
  done

  echo "Test over."
}
run_the_test

#! /bin/bash

path="$(dirname "$PWD")"/benchmarks/infiniteArenaBenchmarks
echo "$path"

elapsed() {

  grep "," $1 | tail -1  

}

cutter() {
  grep  $1 |  sed 's/.*infiniteArenaBenchmarks.//g' | sed 's/.json.*//g'
}

run_the_test() {

  FileNames=$(ls $path/*.time)

    echo "Benchmark program, Rounds, Positive, Negative, Total Time(ms)"> Tabulated-Data/tabulated_infinite_dt.csv
  for file_name in $FileNames; do
    echo $(basename $file_name) ", " $(elapsed $file_name) >> Tabulated-Data/tabulated_infinite_dt.csv
  done

  echo "Test over."
}
run_the_test

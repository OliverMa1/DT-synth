#! /bin/bash

path="$(dirname "$PWD")"/benchmarks/scalabilityBenchmarks
echo "$path"

run_the_test() {
  FileNames=$(ls $path/*.json)
  g++ -std=c++11 -o a $path/GridWorldSequence1D.cpp
  for file_name in $FileNames; do
    for i in 10 30 50 100; do 
      echo $i
      ./a $i
      echo "testing: " $(basename $file_name) "..."
      timeout 600 ../DT-synth/main $file_name > $file_name$i.time
    done
  done
  echo "Test over."
}
run_the_test

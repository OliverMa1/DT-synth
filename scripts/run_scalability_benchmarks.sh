#! /bin/bash

path="$(dirname "$PWD")"/benchmarks/scalabilityBenchmarks
echo "$path"
export LD_LIBRARY_PATH=/home/tacas19/dt/artifact-evaluation-master/DT-synth/lib/

run_the_test() {
  g++ -std=c++11 -o a $path/GridWorldSequence1D.cpp
  ./a 5
  FileNames=$(ls $path/*.json)
  for file_name in $FileNames; do
    for i in 20 40 80 120 160 200 250 300 400 500 600 700 800; do 
      echo $i
      ./a $i
      echo "testing: " $(basename $file_name) "..."
      timeout 600 ../DT-synth/main $file_name > $file_name$i.time
    done
  done
  echo "Test over."
}
run_the_test

#! /bin/bash

path="$(dirname "$PWD")"/TuLiP
echo "$path"

run_the_test() {
  touch Output.txt
  rm Output.txt
  FileNames=$(ls $path/GridWorldSequence1D.py)
  timeout 300 python $FileNames > /dev/null
  echo "Size, Time(ms), Size"> Tabulated-Data/tabulated_scalability_tulip.csv
  while read p; do
    echo  "$p" >> Tabulated-Data/tabulated_scalability_tulip.csv
  done < Output.txt
  rm Output.txt
  echo "Test over."
}
run_the_test

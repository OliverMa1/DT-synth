#! /bin/bash

path="$(dirname "$PWD")"/benchmarks/infiniteArenaBenchmarks
echo "$path"
export LD_LIBRARY_PATH=/home/tacas19/dt/artifact-evaluation-master/DT-synth/lib/

run_the_test() {

  FileNames=$(ls $path/*.json)
  for file_name in $FileNames; do
    echo "testing: " $(basename $file_name) "..."
	    timeout 600 ../DT-synth/main $file_name > $file_name.time
  done
  echo "Test over."
}
run_the_test

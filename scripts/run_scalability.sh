#! /bin/bash

sh run_scalability_benchmarks.sh
sh run_tulip_scalability.sh
sh run_rpni_scalability_tests.sh
sh run_sat_scalability_tests.sh
sh run_fixed_scalability_tests.sh
sh tabulate_time_scalability_benchmarks.sh
sh tabulate_time_scalability_rpni.sh
sh tabulate_time_scalability_sat.sh
sh tabulate_time_scalability_fixed.sh

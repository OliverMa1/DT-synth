# How to install TuLip

You can find the sequence of steps here: https://colab.research.google.com/drive/1KKxSfB3-Dz1V6U__Sx3-PADub2msYfHN

In case the link is not working follow these steps:

    apt-get update && apt-get install gfortran libatlas-base-dev liblapack-dev libgmp-dev libmpfr-dev graphviz libglpk-dev libboost-dev
    export CVXOPT_BUILD_GLPK=1 && pip install cvxopt==1.1.9
    curl -L -O https://github.com/tulip-control/tulip-control/archive/master.tar.gz
    tar -xzf master.tar.gz
    cd tulip-control-master && pip install .
    apt-get install bison flex
    curl -L -O https://github.com/tulip-control/gr1c/archive/master.tar.gz
    tar -xzf master.tar.gz
    cd gr1c-master/ && ./get-deps.sh && ./build-deps.sh && make all && make install
   
   
It might be the case that the first step needs to be split up and be done incrementally if dependencies cannot be resolved correctly.   
   

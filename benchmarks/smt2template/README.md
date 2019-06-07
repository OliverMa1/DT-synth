# DT-synth
Creating individual Safety games
=================================

This file describes how to create a safety game.

Currently, the prototype does not provide a user interface and games are given
as .json files. This means that the desired game has to be selected in the
sources (as described below) and the prototype needs to be executed with the game as input.


The format of the .json file
-------------

The .json file consists of 3 json Arrays, 1 Number and 1 String. 
1. The first value is an Array called "variables". This Array contains every Variable of the Safety game encoded as String values each. 

2. The second values is an Array called "variables\_dash". This Array defines the names of the variables used by the transition relation of the Safety game. The dashed variables are matched with variables in the Array before by position, e.g. variable on position 1 in Array "variables" translates to variable dashed on position 1 in Array "variables\_dash"

3. The third value is "successors". We require the Safety Game to be finitely branching, thus the number of successors needs to be the minimum amount of outgoing edges one node can have. Entering a too small number here can lead to wrong results.

4. The fourth value is an Array called "exprs". This array saves additional names of expressions that may be added for the learner. For example "x+y" can be saved as expression for the learner to use. This can be used to extend the amount of formulas the learner can express. This formulas are encoded in the fifth value of the .json file.

5. The fifth value called "smt2". A template can be found input.smt2. This value is in SMT-LIB 2 input format. The formula consists of one assert for each Property of the Safety game. The first assert encodes the Initial States, the second assert encodes the Safe States, the third assert encodes Player 0 States, the fourth assert encodes Player 1 States and the fifth assert encodes the transition relation of the Safety Game. All asserts can only use the variables defined in the json Arrays. For every additional expression defined in the array "exprs" there needs to be one additional assert after the 5 mandatory asserts which encodes the "newly" defined expression as an SMT-LIB formula. An example can be found in input.smt2 and laufband.json.


(define (problem pb1)
	(:domain FESTO_MPS )
	(:requirements :strips :negative-preconditions :typing)
	(:objects L1_a L1_b L1_c L1_d L1_e L1_f - robotposition
		  L2_a L2_b L2_c L2_d L2_e L2_f - robotposition
		  L3_a L3_b L3_c L3_d L3_e L3_f - robotposition
		  X Y - robotposition
		  P1 P2 P3 P4 P5 P6 - beltposition
		  R1 R2 R3 R4 R5 R6 - plateposition
		  Ball1 - unit
		  robot - gripper)
		  		  	  
	(:init 
		(in-robot X)
		(at Ball1 P1)
		(free-hand robot)
		(rod-located X P6) (rod-located P5 R1) 
		(rod-located R4 P4) (rod-located P3 Y)
		(belt-connected P6 P1) (belt-connected P1 P5)
		(belt-connected P4 P2) (belt-connected P2 P3)
		(next R1 R2) (next R2 R3) (next R3 R4) (next R4 R5) (next R5 R6) (next R6 R1)
		(faceup Ball1)
		(drillposition R3)
	)
	;; Under the simple setting, the goal is to place the processed unit (it must be drilled) to the rack.
	(:goal (and (drilled Ball1) (at Ball1 L1_a)))
	;; (:goal (and (at Ball1 P5)))
	;; (:goal (and (drilled Ball1) (at Ball1 P4)))
)
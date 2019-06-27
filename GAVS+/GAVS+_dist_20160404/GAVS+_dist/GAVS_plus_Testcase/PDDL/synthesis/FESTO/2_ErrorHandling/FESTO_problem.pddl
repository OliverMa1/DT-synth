(define (problem pb1)
	(:domain FESTO_domain )
	(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions)
	(:objects L1_a L1_b L1_c L1_d L1_e L1_f - robotposition
		  L2_a L2_b L2_c L2_d L2_e L2_f - robotposition
		  L3_a L3_b L3_c L3_d L3_e L3_f - robotposition
		  X Y - robotposition
		  P1 P2 P3 P4 P5 - beltposition
		  R1 R2 R3 R4 R5 R6 - plateposition
		  Ball1 - unit
		  robot - gripper
		  red black white - colortype
		  up down - facetype)
		  		  
	(:init 
		(P0TRAN)
		(in-robot X)
		(at Ball1 P1)
		(free-hand robot)
		(rod-located P5 R1) 
		(rod-located R4 P4) (rod-located P3 Y)
		(belt-connected X P1) (belt-connected P1 P5)
		(belt-connected P4 P2) (belt-connected P2 P3)
		(next R1 R2) (next R2 R3) (next R3 R4) (next R4 R5) (next R5 R6) (next R6 R1)
		(have-face-sensor R2)
		(drillposition R3)
	)
	;; The goal is as follows:
	;; (1) When the object is facing up, drill the unit and store to the rack based on its color.
	;;     (Red -> L1) (Black -> L2) (White -> L3)
	;; (2) When the object is facing down, do not drill the unit and shall return it to the original position
	(:goal (and 	
			(or (face Ball1 up)(face Ball1 down))	
			(and 
				(or (not (face Ball1 up))  
				    ( and 
				    		(drilled Ball1) 
				    		( and 	(or (not (color Ball1 red))(at Ball1 L1_a))
				    			(or (not (color Ball1 white))(at Ball1 L2_a))
				    			(or (not (color Ball1 black))(at Ball1 L3_a))
				    		)
						(or (color Ball1 red)(color Ball1 white)(color Ball1 black))	
				    )
				)
				(or (not (face Ball1 down))  
				    ( and 
						(not (drilled Ball1)) 
						(at Ball1 P1)
				    )
				)
			
			)

	       )
	)
)
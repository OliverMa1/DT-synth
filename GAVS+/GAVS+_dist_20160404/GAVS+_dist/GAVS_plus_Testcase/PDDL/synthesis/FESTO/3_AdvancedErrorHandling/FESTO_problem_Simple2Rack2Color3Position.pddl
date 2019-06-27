(define (problem pb1)
	(:domain FESTO_domain )
	(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions)
	(:objects L1_a L1_b L1_c - rackposition
		  L2_a L2_b L2_c - rackposition 
		  X Y - robotposition
		  P1 P2 P3 P4 P5 P6 - beltposition
		  R1 R2 R3 R4 R5 R6 - plateposition
		  Ball1 - unit
		  robot - gripper
		  red white - colortype
		  up down - facetype 
		  yes no - occupytype)		  		  
	(:init 
		(P0TRAN)
		(in-robot X)
		(at Ball1 P1)
		(free-hand robot)
		(rod-located X P6) (rod-located P5 R1) 
		(rod-located R4 P4) (rod-located P3 Y)
		(belt-connected P6 P1) (belt-connected P1 P5)
		(belt-connected P4 P2) (belt-connected P2 P3)
		(next R1 R2) (next R2 R3) (next R3 R4) (next R4 R5) (next R5 R6) (next R6 R1)
		(have-color-sensor Y)
		(have-face-sensor R2)
		(drillposition R3)
	)
	;; The goal is as follows:
	;; (1) When the object is facing up, drill the unit and store to the rack based on its color.
	;;     (Red -> L1) (White -> L2)
	;;     However, when places in the rack is full "(and (rack-occupied L1_a yes) (rack-occupied L1_b yes))", return the drilled object to the user
	;;     (at least one of them shall be detected)
	;; (2) When the object is facing down, do not drill the unit and shall return it to the original position
	(:goal (and 	
			(or (face Ball1 up)(face Ball1 down))	
			(and 
				(or (not (face Ball1 up))  
				    ( and 
				    		(drilled Ball1) 
				    		( and 	(or (not (color Ball1 red))(or (at Ball1 L1_a) (at Ball1 L1_b) (at Ball1 L1_c) (and  (and (rack-occupied L1_a yes) (rack-occupied L1_b yes) (rack-occupied L1_c yes) (not (rack-occupied L1_a no)) (not (rack-occupied L1_b no)) (not (rack-occupied L1_c no))) (at Ball1 P1))))
				    			(or (not (color Ball1 white))(or (at Ball1 L2_a) (at Ball1 L2_b) (at Ball1 L2_c) (and (and (rack-occupied L2_a yes) (rack-occupied L2_b yes) (rack-occupied L2_c yes) (not (rack-occupied L2_a no)) (not (rack-occupied L2_b no)) (not (rack-occupied L2_c no))) (at Ball1 P1))))
				    		)
						(or (color Ball1 red)(color Ball1 white))	
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


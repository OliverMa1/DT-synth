;;
;; Author: Chih-Hong Cheng @ TU Muenchen
;;
;; In this model, we try to study the setup of the FESTO MPS system, 
;; such that we can extract parameterized actions. With such actions, 
;; it is thus possible to construct an arbitrarily setup by describing the
;; topology of the system (i.e., how components are combined),
;; and synthesize plans or strategies to achieve certain goal.
;;
;; We try to extract actions for the construction of the following system (constructed in I-6) 
;; (a) 648813 Processing unit
;; (b) 696683 Storing unit
;; (c) Two conveybelt units linked together with one sensor for unit detection (2 sets)
;;
;; In this setting, we define the set of parameterized actions, such that we can perform 
;; a setup a FESTO MPS system by creating an instaintiation of a problem, where topologies are
;; explicitly stated as setup.
;;

(define (domain FESTO_domain)
	(:requirements :strips :typing :negative-preconditions)
	(:types robotposition beltposition plateposition - object
  		unit
  		gripper)  		
	(:predicates 
             (in-robot ?pos - robotposition)
             (at ?obj - unit  ?pos - (either robotposition beltposition plateposition))
             (free-hand ?gri - gripper)
             (carry ?obj - unit ?gri - gripper)
             (occupied ?pos  - (either robotposition beltposition plateposition))   
             (belt-connected ?pos1 ?pos2 - beltposition)
             (rod-located ?pos1 ?pos2 - (either robotposition beltposition plateposition)) 
             (next ?pos1 ?pos2 - plateposition)
             (drillposition ?pos - plateposition)
             (drilled ?obj - unit)
             (faceup ?obj - unit)
	)
	;; atomic actions of the robot arm
	(:action robot-move
		:parameters  (?from ?to - robotposition)
		:precondition (and (in-robot ?from))
		:effect (and  (in-robot ?to) (not (in-robot ?from))
		)
	)
		
	(:action robot-pick
		:parameters (?obj - unit ?room - robotposition ?gripper - gripper)
		:precondition  (and (at ?obj ?room) (in-robot ?room) (free-hand ?gripper))
		:effect (and (carry ?obj ?gripper) (not (at ?obj ?room)) 
              		(not (free-hand ?gripper)) (not (occupied ?room))
              	)
        )
 
	(:action robot-drop
		:parameters  (?obj - unit  ?room - robotposition ?gripper - gripper)
		:precondition  (and (carry ?obj ?gripper) (in-robot ?room) (not (occupied ?room)))
		:effect (and (at ?obj ?room) (free-hand ?gripper) (not (carry ?obj ?gripper))
			)
	)
	
		
	;; atomic actions of the transmission belt
	(:action belt-move
		:parameters  (?obj - unit ?from ?to - beltposition)
		:precondition (and (belt-connected ?from ?to) (at ?obj ?from) (not (occupied ?to)) )
		:effect (and  (not (occupied ?from)) (not (at ?obj ?from)) (at ?obj ?to) (occupied ?to) 
			)
	)

	;; atomic actions of the drill
	(:action drill-in
			:parameters  (?obj - unit ?pos - plateposition)
			:precondition (and (faceup ?obj) (at ?obj ?pos) (drillposition ?pos) )
			:effect (and (drilled ?obj))
	)	
	
	
	;; atomic actions of the rotation plate	(here the opeartion is fixed)
	(:action plate-rotate
			:parameters  (?obj1 ?obj2 ?obj3 ?obj4 ?obj5 ?obj6 - unit 
				      ?pos1 ?pos2 ?pos3 ?pos4 ?pos5 ?pos6 - plateposition)
			:precondition (and  (next ?pos1 ?pos2) (next ?pos2 ?pos3) (next ?pos3 ?pos4) 
					    (next ?pos4 ?pos5) (next ?pos5 ?pos6) (next ?pos6 ?pos1))
			:effect ( and 
					(when (at ?obj1 ?pos1) (and (at ?obj1 ?pos2) (not (at ?obj1 ?pos1)) ))
					(when (at ?obj1 ?pos2) (and (at ?obj1 ?pos3) (not (at ?obj1 ?pos2)) ))
					(when (at ?obj1 ?pos3) (and (at ?obj1 ?pos4) (not (at ?obj1 ?pos3)) ))
					(when (at ?obj1 ?pos4) (and (at ?obj1 ?pos5) (not (at ?obj1 ?pos4)) ))
					(when (at ?obj1 ?pos5) (and (at ?obj1 ?pos6) (not (at ?obj1 ?pos5)) ))
					(when (at ?obj1 ?pos6) (and (at ?obj1 ?pos1) (not (at ?obj1 ?pos6)) ))

					(when (at ?obj2 ?pos1) (and (at ?obj2 ?pos2) (not (at ?obj2 ?pos1)) ))
					(when (at ?obj2 ?pos2) (and (at ?obj2 ?pos3) (not (at ?obj2 ?pos2)) ))
					(when (at ?obj2 ?pos3) (and (at ?obj2 ?pos4) (not (at ?obj2 ?pos3)) ))
					(when (at ?obj2 ?pos4) (and (at ?obj2 ?pos5) (not (at ?obj2 ?pos4)) ))
					(when (at ?obj2 ?pos5) (and (at ?obj2 ?pos6) (not (at ?obj2 ?pos5)) ))
					(when (at ?obj2 ?pos6) (and (at ?obj2 ?pos1) (not (at ?obj2 ?pos6)) ))

					(when (at ?obj3 ?pos1) (and (at ?obj3 ?pos2) (not (at ?obj3 ?pos1)) ))
					(when (at ?obj3 ?pos2) (and (at ?obj3 ?pos3) (not (at ?obj3 ?pos2)) ))
					(when (at ?obj3 ?pos3) (and (at ?obj3 ?pos4) (not (at ?obj3 ?pos3)) ))
					(when (at ?obj3 ?pos4) (and (at ?obj3 ?pos5) (not (at ?obj3 ?pos4)) ))
					(when (at ?obj3 ?pos5) (and (at ?obj3 ?pos6) (not (at ?obj3 ?pos5)) ))
					(when (at ?obj3 ?pos6) (and (at ?obj3 ?pos1) (not (at ?obj3 ?pos6)) ))

					(when (at ?obj4 ?pos1) (and (at ?obj4 ?pos2) (not (at ?obj4 ?pos1)) ))
					(when (at ?obj4 ?pos2) (and (at ?obj4 ?pos3) (not (at ?obj4 ?pos2)) ))
					(when (at ?obj4 ?pos3) (and (at ?obj4 ?pos4) (not (at ?obj4 ?pos3)) ))
					(when (at ?obj4 ?pos4) (and (at ?obj4 ?pos5) (not (at ?obj4 ?pos4)) ))
					(when (at ?obj4 ?pos5) (and (at ?obj4 ?pos6) (not (at ?obj4 ?pos5)) ))
					(when (at ?obj4 ?pos6) (and (at ?obj4 ?pos1) (not (at ?obj4 ?pos6)) ))

					(when (at ?obj5 ?pos1) (and (at ?obj5 ?pos2) (not (at ?obj5 ?pos1)) ))
					(when (at ?obj5 ?pos2) (and (at ?obj5 ?pos3) (not (at ?obj5 ?pos2)) ))
					(when (at ?obj5 ?pos3) (and (at ?obj5 ?pos4) (not (at ?obj5 ?pos3)) ))
					(when (at ?obj5 ?pos4) (and (at ?obj5 ?pos5) (not (at ?obj5 ?pos4)) ))
					(when (at ?obj5 ?pos5) (and (at ?obj5 ?pos6) (not (at ?obj5 ?pos5)) ))
					(when (at ?obj5 ?pos6) (and (at ?obj5 ?pos1) (not (at ?obj5 ?pos6)) ))

					(when (at ?obj6 ?pos1) (and (at ?obj6 ?pos2) (not (at ?obj6 ?pos1)) ))
					(when (at ?obj6 ?pos2) (and (at ?obj6 ?pos3) (not (at ?obj6 ?pos2)) ))
					(when (at ?obj6 ?pos3) (and (at ?obj6 ?pos4) (not (at ?obj6 ?pos3)) ))
					(when (at ?obj6 ?pos4) (and (at ?obj6 ?pos5) (not (at ?obj6 ?pos4)) ))
					(when (at ?obj6 ?pos5) (and (at ?obj6 ?pos6) (not (at ?obj6 ?pos5)) ))
					(when (at ?obj6 ?pos6) (and (at ?obj6 ?pos1) (not (at ?obj6 ?pos6)) ))
				)
	)
	
	;; atomic actions of the rod	
	(:action rod-push
		:parameters  (?obj - unit ?from ?to - (either robotposition beltposition plateposition))
		:precondition (and (rod-located ?from ?to) (at ?obj ?from) (not (at ?obj ?to)) (not (occupied ?to)) )
		:effect (and  (at ?obj ?to) (not (at ?obj ?from)) (occupied ?to) 
			)
	)	
	


	
		
	;; atomic actions of the color sensor (omitted currently as second player is not modeled)
			
		
	;; atomic actions of the probe sensor (omitted currently as second player is not modeled)
	
)     
                   
             

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
;; In this domain, we introduce a color sensor such that when the user asks to read from the 
;; sensor, it is possible to probe the color

(define (domain FESTO_domain)
	(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions)
	(:types robotposition rackposition beltposition plateposition - object
  		unit
  		gripper
  		colortype
  		facetype
  		occupytype) 
  	(:constants yes no - occupytype)
	(:predicates 
	     (in-robot ?pos - (either robotposition rackposition) )
	     (at ?obj - unit  ?pos - (either robotposition rackposition beltposition plateposition))
	     (free-hand ?gri - gripper)
	     (carry ?obj - unit ?gri - gripper)
	     (occupied ?pos  - (either robotposition beltposition plateposition)) 
	     (belt-connected ?pos1 - (either robotposition beltposition) ?pos2 - beltposition)
	     (rod-located ?pos1 ?pos2 - (either robotposition rackposition beltposition plateposition)) 
	     (next ?pos1 ?pos2 - plateposition)
	     (drillposition ?pos - plateposition)
	     (drilled ?obj - unit)
             (have-color-sensor ?pos - (either robotposition rackposition beltposition plateposition))
             (color-sensor-on ?gri - gripper)
             (color ?obj - unit ?col - colortype)
             (have-face-sensor ?pos - (either robotposition rackposition beltposition plateposition))
	     (face-sensor-on)
             (face ?obj - unit ?face - facetype)
             (rack-occupied ?pos - rackposition ?occ - occupytype)
             (occupy-sensor-on ?gri - gripper)
             (P0TRAN)
	)
	
	;; atomic actions of the robot arm
	(:action robot-move
		:parameters  (?from ?to - (either robotposition rackposition) )
		:precondition (and (P0TRAN) (in-robot ?from))
		:effect (and  (P0TRAN) (in-robot ?to) (not (in-robot ?from))
		)
	)

	(:action robot-pick-ground
		:parameters (?obj - unit ?room - robotposition ?gripper - gripper)
		:precondition  (and (P0TRAN) (at ?obj ?room) (in-robot ?room) (free-hand ?gripper))
		:effect (and (P0TRAN) (carry ?obj ?gripper) (not (at ?obj ?room)) 
			(not (free-hand ?gripper)) (not (occupied ?room))
		)
	)

	(:action robot-drop-ground
		:parameters  (?obj - unit  ?room - robotposition ?gripper - gripper)
		:precondition  (and (P0TRAN) (carry ?obj ?gripper) (in-robot ?room) (not (occupied ?room)))
		:effect (and (P0TRAN) (at ?obj ?room) (free-hand ?gripper) (not (carry ?obj ?gripper))
			)
	)
	
	(:action robot-check-shelf
		:parameters (?room - rackposition ?gri - gripper)
		:precondition  (and (P0TRAN) (in-robot ?room) (free-hand ?gri))
		:effect (and (not (P0TRAN)) (occupy-sensor-on ?gri) 			
		)
	)
	
	(:action robot-return-occupy
		:parameters (?room - rackposition ?occupy - occupytype ?gri - gripper)
		:precondition  (and (not (P0TRAN)) (in-robot ?room)  (occupy-sensor-on ?gri) (free-hand ?gri))
		:effect (and (P0TRAN) (not (occupy-sensor-on ?gri)) (rack-occupied ?room ?occupy)
		)
	)

	(:action robot-pick-shelf
		:parameters (?obj - unit ?room - rackposition ?gripper - gripper)
		:precondition  (and (P0TRAN) (at ?obj ?room) (in-robot ?room) (free-hand ?gripper) (rack-occupied ?room yes))
		:effect (and (P0TRAN) (carry ?obj ?gripper) (not (at ?obj ?room)) 
			(not (free-hand ?gripper)) (rack-occupied ?room no) (not (rack-occupied ?room yes))
		)
	)

	(:action robot-drop-shelf
		:parameters  (?obj - unit  ?room - rackposition ?gripper - gripper)
		:precondition  (and (P0TRAN) (carry ?obj ?gripper) (in-robot ?room) (rack-occupied ?room no))
		:effect (and (P0TRAN) (at ?obj ?room) (free-hand ?gripper) (not (carry ?obj ?gripper)) (not (rack-occupied ?room no)) (rack-occupied ?room yes)
			)
	)


	;; atomic actions of the transmission belt
	(:action belt-move
		:parameters  (?obj - unit ?from - (either robotposition beltposition) ?to - beltposition)
		:precondition (and (P0TRAN) (belt-connected ?from ?to) (at ?obj ?from) (not (occupied ?to)) )
		:effect (and  (P0TRAN) (not (occupied ?from)) (not (at ?obj ?from)) (at ?obj ?to) (occupied ?to) 
			)
	)

	;; atomic actions of the drill
	;; (here the atomic action has been modified that it can be drilled under all condition)
	(:action drill-in
			:parameters  (?obj - unit ?pos - plateposition)
			:precondition (and (P0TRAN) (at ?obj ?pos) (drillposition ?pos) )
			:effect (and (P0TRAN) (drilled ?obj))
	)	


	;; atomic actions of the rotation plate	(here the opeartion is fixed)
	(:action plate-rotate
			:parameters  (?obj1 ?obj2 ?obj3 ?obj4 ?obj5 ?obj6 - unit 
				      ?pos1 ?pos2 ?pos3 ?pos4 ?pos5 ?pos6 - plateposition)
			:precondition (and  (P0TRAN) (next ?pos1 ?pos2) (next ?pos2 ?pos3) (next ?pos3 ?pos4) 
					    (next ?pos4 ?pos5) (next ?pos5 ?pos6) (next ?pos6 ?pos1))
			:effect ( and 
					(when (at ?obj1 ?pos1) (and (P0TRAN) (at ?obj1 ?pos2) (not (at ?obj1 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj1 ?pos1) (and (P0TRAN) (at ?obj1 ?pos2) (not (at ?obj1 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj1 ?pos2) (and (P0TRAN) (at ?obj1 ?pos3) (not (at ?obj1 ?pos2)) (not (occupied ?pos2)) (occupied ?pos3) ))
					(when (at ?obj1 ?pos3) (and (P0TRAN) (at ?obj1 ?pos4) (not (at ?obj1 ?pos3)) (not (occupied ?pos3)) (occupied ?pos4) ))
					(when (at ?obj1 ?pos4) (and (P0TRAN) (at ?obj1 ?pos5) (not (at ?obj1 ?pos4)) (not (occupied ?pos4)) (occupied ?pos5) ))
					(when (at ?obj1 ?pos5) (and (P0TRAN) (at ?obj1 ?pos6) (not (at ?obj1 ?pos5)) (not (occupied ?pos5)) (occupied ?pos6) ))
					(when (at ?obj1 ?pos6) (and (P0TRAN) (at ?obj1 ?pos1) (not (at ?obj1 ?pos6)) (not (occupied ?pos6)) (occupied ?pos1) ))

					(when (at ?obj2 ?pos1) (and (P0TRAN) (at ?obj2 ?pos2) (not (at ?obj2 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj2 ?pos1) (and (P0TRAN) (at ?obj2 ?pos2) (not (at ?obj2 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj2 ?pos2) (and (P0TRAN) (at ?obj2 ?pos3) (not (at ?obj2 ?pos2)) (not (occupied ?pos2)) (occupied ?pos3) ))
					(when (at ?obj2 ?pos3) (and (P0TRAN) (at ?obj2 ?pos4) (not (at ?obj2 ?pos3)) (not (occupied ?pos3)) (occupied ?pos4) ))
					(when (at ?obj2 ?pos4) (and (P0TRAN) (at ?obj2 ?pos5) (not (at ?obj2 ?pos4)) (not (occupied ?pos4)) (occupied ?pos5) ))
					(when (at ?obj2 ?pos5) (and (P0TRAN) (at ?obj2 ?pos6) (not (at ?obj2 ?pos5)) (not (occupied ?pos5)) (occupied ?pos6) ))
					(when (at ?obj2 ?pos6) (and (P0TRAN) (at ?obj2 ?pos1) (not (at ?obj2 ?pos6)) (not (occupied ?pos6)) (occupied ?pos1) ))

					(when (at ?obj3 ?pos1) (and (P0TRAN) (at ?obj3 ?pos2) (not (at ?obj3 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj3 ?pos1) (and (P0TRAN) (at ?obj3 ?pos2) (not (at ?obj3 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj3 ?pos2) (and (P0TRAN) (at ?obj3 ?pos3) (not (at ?obj3 ?pos2)) (not (occupied ?pos2)) (occupied ?pos3) ))
					(when (at ?obj3 ?pos3) (and (P0TRAN) (at ?obj3 ?pos4) (not (at ?obj3 ?pos3)) (not (occupied ?pos3)) (occupied ?pos4) ))
					(when (at ?obj3 ?pos4) (and (P0TRAN) (at ?obj3 ?pos5) (not (at ?obj3 ?pos4)) (not (occupied ?pos4)) (occupied ?pos5) ))
					(when (at ?obj3 ?pos5) (and (P0TRAN) (at ?obj3 ?pos6) (not (at ?obj3 ?pos5)) (not (occupied ?pos5)) (occupied ?pos6) ))
					(when (at ?obj3 ?pos6) (and (P0TRAN) (at ?obj3 ?pos1) (not (at ?obj3 ?pos6)) (not (occupied ?pos6)) (occupied ?pos1) ))

					(when (at ?obj4 ?pos1) (and (P0TRAN) (at ?obj4 ?pos2) (not (at ?obj4 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj4 ?pos1) (and (P0TRAN) (at ?obj4 ?pos2) (not (at ?obj4 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj4 ?pos2) (and (P0TRAN) (at ?obj4 ?pos3) (not (at ?obj4 ?pos2)) (not (occupied ?pos2)) (occupied ?pos3) ))
					(when (at ?obj4 ?pos3) (and (P0TRAN) (at ?obj4 ?pos4) (not (at ?obj4 ?pos3)) (not (occupied ?pos3)) (occupied ?pos4) ))
					(when (at ?obj4 ?pos4) (and (P0TRAN) (at ?obj4 ?pos5) (not (at ?obj2 ?pos4)) (not (occupied ?pos4)) (occupied ?pos5) ))
					(when (at ?obj4 ?pos5) (and (P0TRAN) (at ?obj4 ?pos6) (not (at ?obj4 ?pos5)) (not (occupied ?pos5)) (occupied ?pos6) ))
					(when (at ?obj4 ?pos6) (and (P0TRAN) (at ?obj4 ?pos1) (not (at ?obj4 ?pos6)) (not (occupied ?pos6)) (occupied ?pos1) ))

					(when (at ?obj5 ?pos1) (and (P0TRAN) (at ?obj5 ?pos2) (not (at ?obj5 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj5 ?pos1) (and (P0TRAN) (at ?obj5 ?pos2) (not (at ?obj5 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj5 ?pos2) (and (P0TRAN) (at ?obj5 ?pos3) (not (at ?obj5 ?pos2)) (not (occupied ?pos2)) (occupied ?pos3) ))
					(when (at ?obj5 ?pos3) (and (P0TRAN) (at ?obj5 ?pos4) (not (at ?obj5 ?pos3)) (not (occupied ?pos3)) (occupied ?pos4) ))
					(when (at ?obj5 ?pos4) (and (P0TRAN) (at ?obj5 ?pos5) (not (at ?obj5 ?pos4)) (not (occupied ?pos4)) (occupied ?pos5) ))
					(when (at ?obj5 ?pos5) (and (P0TRAN) (at ?obj5 ?pos6) (not (at ?obj5 ?pos5)) (not (occupied ?pos5)) (occupied ?pos6) ))
					(when (at ?obj5 ?pos6) (and (P0TRAN) (at ?obj5 ?pos1) (not (at ?obj5 ?pos6)) (not (occupied ?pos6)) (occupied ?pos1) ))

					(when (at ?obj6 ?pos1) (and (P0TRAN) (at ?obj6 ?pos2) (not (at ?obj6 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj6 ?pos1) (and (P0TRAN) (at ?obj6 ?pos2) (not (at ?obj6 ?pos1)) (not (occupied ?pos1)) (occupied ?pos2) ))
					(when (at ?obj6 ?pos2) (and (P0TRAN) (at ?obj6 ?pos3) (not (at ?obj6 ?pos2)) (not (occupied ?pos2)) (occupied ?pos3) ))
					(when (at ?obj6 ?pos3) (and (P0TRAN) (at ?obj6 ?pos4) (not (at ?obj6 ?pos3)) (not (occupied ?pos3)) (occupied ?pos4) ))
					(when (at ?obj6 ?pos4) (and (P0TRAN) (at ?obj6 ?pos5) (not (at ?obj6 ?pos4)) (not (occupied ?pos4)) (occupied ?pos5) ))
					(when (at ?obj6 ?pos5) (and (P0TRAN) (at ?obj6 ?pos6) (not (at ?obj6 ?pos5)) (not (occupied ?pos5)) (occupied ?pos6) ))
					(when (at ?obj6 ?pos6) (and (P0TRAN) (at ?obj6 ?pos1) (not (at ?obj6 ?pos6)) (not (occupied ?pos6)) (occupied ?pos1) ))
				)
	)

	;; atomic actions of the rod	
	(:action rod-push
		:parameters  (?obj - unit ?from ?to - (either robotposition beltposition plateposition))
		:precondition (and (P0TRAN) (rod-located ?from ?to) (at ?obj ?from) (not (at ?obj ?to)) (not (occupied ?to)) )
		:effect (and  (P0TRAN) (at ?obj ?to) (not (at ?obj ?from)) (occupied ?to) (not (occupied ?from))
			)
	)	


	;; atomic actions of the color sensor
	(:action trigger-color-sensor
		:parameters  (?obj - unit ?pos - robotposition ?gri - gripper)
		:precondition  (and (P0TRAN) (at ?obj ?pos) (in-robot ?pos) (free-hand ?gri))
		:effect (and (color-sensor-on ?gri) (not (P0TRAN))
			)
	)		
	(:action return-color-value
		:parameters  (?obj - unit ?pos - robotposition ?color - colortype ?gri - gripper)
		:precondition  (and (not (P0TRAN)) (color-sensor-on ?gri) (in-robot ?pos) (at ?obj ?pos))
		:effect (and (not (color-sensor-on ?gri)) (P0TRAN) (color ?obj ?color)
			)
	)

	;; atomic actions of the probe sensor 
	(:action trigger-probe-sensor
		:parameters  (?obj - unit ?pos - (either robotposition beltposition plateposition))
		:precondition  (and (P0TRAN) (at ?obj ?pos) (have-face-sensor ?pos))
		:effect (and (face-sensor-on) (not (P0TRAN))
			)
	)		
	(:action return-probe-value
		:parameters  (?obj - unit ?pos - (either robotposition beltposition plateposition) ?face - facetype)
		:precondition  (and (not (P0TRAN)) (face-sensor-on) (have-face-sensor ?pos) (at ?obj ?pos))
		:effect (and (not (face-sensor-on)) (P0TRAN) (face ?obj ?face)
			)
	)		
	
	

)     
                   
             

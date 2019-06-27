;; MGSyn: Automatic Synthesis for Industrial Automation
;;
;; This automatically generated file contains the domain description for the system model.
;; The file is PDDL-like for ease of understanding. This file can be fed into the synthesis
;; engine GAVS+ (http://www6.in.tum.de/~chengch/gavs) to generate a high-level control strategy.
;;
;; Contact:
;;  - Chih-Hong Cheng, fortiss GmbH (cheng.chihhong@googlemail.com)
;;  - Michael Geisinger, fortiss GmbH (msg@tum.de) 
;; 

;; Domain Description
;; 
(define (domain testdomain)
	(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions :equality :fluents)
	(:types
		robotposition
		rackposition
		beltposition
		plateposition
		otherposition
		workpiece
		gripperposition
		occupytype
		facetype
		colortype
		device		;; for each device, assign it with a unique identifier (used for parallelization)
		behavioral  ;; for each behavioral interface, assign it with a unique identifier (used for parallelization)
	)
	(:constants
		
		yes no - occupytype
		red white - colortype
		up down - facetype
		beh-robot-check-shelf - behavioral
		beh-trigger-color-sensor - behavioral
		beh-trigger-probe-sensor - behavioral
		beh-robot-move - behavioral
		beh-robot-pick-ground - behavioral
		beh-robot-drop-ground - behavioral
		beh-robot-pick-shelf - behavioral
		beh-robot-drop-shelf - behavioral
		beh-belt-move-b - behavioral
		beh-belt-move-r - behavioral
		beh-drill-in - behavioral
		beh-plate-rotate-one - behavioral
		beh-plate-rotate-two - behavioral
		beh-lever-push - behavioral
	)
	(:predicates
		(in-robot ?pos -  (either rackposition robotposition))
		(at ?obj -  workpiece ?pos -  (either gripperposition beltposition rackposition robotposition plateposition))
		(free-hand ?gri -  gripperposition)
		(occupied ?pos -  (either beltposition robotposition plateposition))
		(belt-connected ?pos1 -  (either beltposition robotposition) ?pos2 -  beltposition)
		(lever-located ?from -  (either beltposition rackposition robotposition plateposition) ?to -  (either beltposition rackposition robotposition plateposition))
		(clockwise-next ?pos1 -  plateposition ?pos2 -  plateposition)
		(drillposition ?pos -  plateposition)
		(drilled ?obj -  workpiece)
		(color-sensor-on)
		(color ?obj -  workpiece ?col -  colortype)
		(have-face-sensor ?pos -  plateposition)
		(face-sensor-on)
		(face ?obj -  workpiece ?face -  facetype)
		(rack-occupied ?pos -  rackposition ?occ -  occupytype)
		(occupy-sensor-on)
		(device-use ?dev - device ?beh - behavioral)
		(device-hold ?dev - device ?pos - (either  robotposition rackposition gripperposition beltposition otherposition plateposition))
		(P0TRAN)
		;; Aggressive technique 0
	)
	(:functions (total-cost))
	
	;; Actuations
	
	
	
	(:action robot-move
		:parameters (?dev - device  ?from -  (either rackposition robotposition) ?to -  (either rackposition robotposition))
		:precondition (and  (device-hold ?dev ?from)(device-hold ?dev ?to)
		   				    ( device-use ?dev beh-robot-move) ( P0TRAN) ( in-robot ?from) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 3) (not (in-robot ?from)) (in-robot ?to))

	)
	
	
	(:action robot-pick-ground
		:parameters (?dev - device  ?obj -  workpiece ?room -  robotposition ?gripper -  gripperposition)
		:precondition (and  (device-hold ?dev ?room)(device-hold ?dev ?gripper)
		   				    ( device-use ?dev beh-robot-pick-ground) ( P0TRAN) ( at ?obj ?room) ( in-robot ?room) ( free-hand ?gripper) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 1) (not (at ?obj ?room)) (at ?obj ?gripper) (not (free-hand ?gripper)) (not (occupied ?room)))

	)
	
	
	(:action robot-drop-ground
		:parameters (?dev - device  ?obj -  workpiece ?room -  robotposition ?gripper -  gripperposition)
		:precondition (and  (device-hold ?dev ?room)(device-hold ?dev ?gripper)
		   				    ( device-use ?dev beh-robot-drop-ground) ( P0TRAN) ( at ?obj ?gripper) ( in-robot ?room) (not ( occupied ?room)) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 1) (not (at ?obj ?gripper)) (at ?obj ?room) (free-hand ?gripper))

	)
	
	
	(:action robot-pick-shelf
		:parameters (?dev - device  ?obj -  workpiece ?room -  rackposition ?gripper -  gripperposition)
		:precondition (and  (device-hold ?dev ?room)(device-hold ?dev ?gripper)
		   				    ( device-use ?dev beh-robot-pick-shelf) ( P0TRAN) ( at ?obj ?room) ( in-robot ?room) ( free-hand ?gripper) ( rack-occupied ?room yes) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 1) (not (at ?obj ?room)) (at ?obj ?gripper) (not (free-hand ?gripper)) (not (rack-occupied ?room yes)) (rack-occupied ?room no))

	)
	
	
	(:action robot-drop-shelf
		:parameters (?dev - device  ?obj -  workpiece ?room -  rackposition ?gripper -  gripperposition)
		:precondition (and  (device-hold ?dev ?room)(device-hold ?dev ?gripper)
		   				    ( device-use ?dev beh-robot-drop-shelf) ( P0TRAN) ( at ?obj ?gripper) ( in-robot ?room) ( rack-occupied ?room no) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 1) (not (at ?obj ?gripper)) (at ?obj ?room) (free-hand ?gripper) (not (rack-occupied ?room no)) (rack-occupied ?room yes))

	)
	
	
	(:action belt-move-b
		:parameters (?dev - device  ?obj -  workpiece ?from -  beltposition ?to -  beltposition)
		:precondition (and  (device-hold ?dev ?from)(device-hold ?dev ?to)
		   				    ( device-use ?dev beh-belt-move-b) ( P0TRAN) ( belt-connected ?from ?to) ( at ?obj ?from) (not ( occupied ?to)) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 3) (not (occupied ?from)) (not (at ?obj ?from)) (at ?obj ?to) (occupied ?to))

	)
	
	
	(:action belt-move-r
		:parameters (?dev - device  ?obj -  workpiece ?from -  robotposition ?to -  beltposition)
		:precondition (and  (device-hold ?dev ?from)(device-hold ?dev ?to)
		   				    ( device-use ?dev beh-belt-move-r) ( P0TRAN) ( belt-connected ?from ?to) ( at ?obj ?from) (not ( occupied ?to)) (not ( in-robot ?from)) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 3) (not (occupied ?from)) (not (at ?obj ?from)) (at ?obj ?to) (occupied ?to))

	)
	
	
	(:action drill-in
		:parameters (?dev - device  ?obj -  workpiece ?posi -  plateposition)
		:precondition (and  (device-hold ?dev ?posi)
		   				    ( device-use ?dev beh-drill-in) ( P0TRAN) ( at ?obj ?posi) ( drillposition ?posi) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 1) (drilled ?obj))

	)
	
	;; /* Model only applicable for one object (when two objects are simultaneously on the plate, we need to return to the previous version ) */
	(:action plate-rotate-one
		:parameters (?dev - device  ?obj -  workpiece ?pos1 -  plateposition ?pos2 -  plateposition ?pos3 -  plateposition ?pos4 -  plateposition ?pos5 -  plateposition ?pos6 -  plateposition)
		:precondition (and  (device-hold ?dev ?pos1)(device-hold ?dev ?pos2)(device-hold ?dev ?pos3)(device-hold ?dev ?pos4)(device-hold ?dev ?pos5)(device-hold ?dev ?pos6)
		   				    ( device-use ?dev beh-plate-rotate-one) ( P0TRAN) ( clockwise-next ?pos1 ?pos2) ( clockwise-next ?pos2 ?pos3) ( clockwise-next ?pos3 ?pos4) ( clockwise-next ?pos4 ?pos5) ( clockwise-next ?pos5 ?pos6) ( clockwise-next ?pos6 ?pos1) 

						)
		:effect (and (when (and (at ?obj ?pos1)) (and  (P0TRAN) (increase (total-cost) 2) (not (at ?obj ?pos1)) (at ?obj ?pos2) (not (occupied ?pos1)) (occupied ?pos2))))

	)
	
	;; /* Model only applicable for one object (when two objects are simultaneously on the plate, we need to return to the previous version ) */
	(:action plate-rotate-two
		:parameters (?dev - device  ?obj1 -  workpiece ?obj2 -  workpiece ?pos1 -  plateposition ?pos2 -  plateposition ?pos3 -  plateposition ?pos4 -  plateposition ?pos5 -  plateposition ?pos6 -  plateposition)
		:precondition (and  (device-hold ?dev ?pos1)(device-hold ?dev ?pos2)(device-hold ?dev ?pos3)(device-hold ?dev ?pos4)(device-hold ?dev ?pos5)(device-hold ?dev ?pos6)
		   				    ( device-use ?dev beh-plate-rotate-two) ( P0TRAN) ( clockwise-next ?pos1 ?pos2) ( clockwise-next ?pos2 ?pos3) ( clockwise-next ?pos3 ?pos4) ( clockwise-next ?pos4 ?pos5) ( clockwise-next ?pos5 ?pos6) ( clockwise-next ?pos6 ?pos1) 

						)
		:effect (and (when (and (at ?obj1 ?pos1)(not (at ?obj2 ?pos1))(not (at ?obj2 ?pos2))(not (at ?obj2 ?pos3))(not (at ?obj2 ?pos4))(not (at ?obj2 ?pos5))(not (at ?obj2 ?pos6))) (and  (P0TRAN) (increase (total-cost) 2) (not (at ?obj1 ?pos1)) (at ?obj1 ?pos2) (not (occupied ?pos1)) (occupied ?pos2)))(when (and (at ?obj1 ?pos1)(at ?obj2 ?pos2)) (and  (P0TRAN) (increase (total-cost) 2) (not (at ?obj1 ?pos1)) (at ?obj1 ?pos2) (not (at ?obj2 ?pos2)) (at ?obj2 ?pos3) (not (occupied ?pos1)) (occupied ?pos2) (occupied ?pos3)))(when (and (at ?obj1 ?pos1)(at ?obj2 ?pos3)) (and  (P0TRAN) (increase (total-cost) 2) (not (at ?obj1 ?pos1)) (at ?obj1 ?pos2) (not (at ?obj2 ?pos3)) (at ?obj2 ?pos4) (not (occupied ?pos1)) (occupied ?pos2) (not (occupied ?pos3)) (occupied ?pos4)))(when (and (at ?obj1 ?pos1)(at ?obj2 ?pos4)) (and  (P0TRAN) (increase (total-cost) 2) (not (at ?obj1 ?pos1)) (at ?obj1 ?pos2) (not (at ?obj2 ?pos4)) (at ?obj2 ?pos5) (not (occupied ?pos1)) (occupied ?pos2) (not (occupied ?pos4)) (occupied ?pos5))))

	)
	
	;; 
	(:action lever-push
		:parameters (?dev - device  ?obj -  workpiece ?from -  (either beltposition robotposition plateposition) ?to -  (either beltposition robotposition plateposition))
		:precondition (and  (device-hold ?dev ?from)(device-hold ?dev ?to)
		   				    ( device-use ?dev beh-lever-push) ( P0TRAN) ( lever-located ?from ?to) ( at ?obj ?from) (not ( at ?obj ?to)) (not ( occupied ?to)) 

						)
		:effect (and (P0TRAN) (increase (total-cost) 1) (not (at ?obj ?from)) (at ?obj ?to) (not (occupied ?from)) (occupied ?to))

	)
	
	
	;; Sensor triggerings
	
	
	;; 
	(:action robot-check-shelf
		:parameters (?dev - device  ?room -  rackposition ?gri -  gripperposition)
		:precondition (and (device-hold ?dev ?room)(device-hold ?dev ?gri)						
		   				    ( device-use ?dev beh-robot-check-shelf) ( P0TRAN) ( in-robot ?room) ( free-hand ?gri) 

					  )
		:effect (and (not (P0TRAN)) (increase (total-cost) 1) (occupy-sensor-on))
 
	)
	
	
	(:action trigger-color-sensor
		:parameters (?dev - device  ?obj -  workpiece ?pos -  robotposition ?gri -  gripperposition)
		:precondition (and (device-hold ?dev ?pos)(device-hold ?dev ?gri)						
		   				    ( device-use ?dev beh-trigger-color-sensor) ( P0TRAN) ( at ?obj ?pos) ( in-robot ?pos) ( free-hand ?gri) 

					  )
		:effect (and (not (P0TRAN)) (increase (total-cost) 1) (color-sensor-on))
 
	)
	
	;; 
	(:action trigger-probe-sensor
		:parameters (?dev - device  ?obj -  workpiece ?pos -  (either beltposition robotposition plateposition))
		:precondition (and (device-hold ?dev ?pos)						
		   				    ( device-use ?dev beh-trigger-probe-sensor) ( P0TRAN) ( at ?obj ?pos) ( have-face-sensor ?pos) 

					  )
		:effect (and (not (P0TRAN)) (increase (total-cost) 1) (face-sensor-on))
 
	)
	
	

	;; Sensor responses
	
	
	;; 
	(:action robot-return-occupy
		:parameters ( ?room -  rackposition ?occupy -  occupytype ?gri -  gripperposition)
		:precondition (and (not (P0TRAN)) (occupy-sensor-on) (in-robot ?room) (free-hand ?gri))
		:effect (and (when (and (rack-occupied ?room yes)) (and  (P0TRAN) (not (occupy-sensor-on))))(when (and (rack-occupied ?room no)) (and  (P0TRAN) (not (occupy-sensor-on))))(when (and (not (rack-occupied ?room no))(not (rack-occupied ?room yes))) (and  (P0TRAN) (rack-occupied ?room ?occupy) (not (occupy-sensor-on)))))
	)
	
	;; 
	(:action return-color-value
		:parameters ( ?obj -  workpiece ?pos -  robotposition ?color -  colortype ?gri -  gripperposition)
		:precondition (and (not (P0TRAN)) (color-sensor-on) (in-robot ?pos) (at ?obj ?pos))
		:effect (and (when (and (color ?obj white)) (and  (P0TRAN) (not (color-sensor-on))))(when (and (color ?obj red)) (and  (P0TRAN) (not (color-sensor-on))))(when (and (not (color ?obj white))(not (color ?obj red))) (and  (P0TRAN) (color ?obj ?color) (not (color-sensor-on)))))
	)
	
	
	(:action return-probe-value
		:parameters ( ?obj -  workpiece ?pos -  (either beltposition robotposition plateposition) ?face -  facetype)
		:precondition (and (not (P0TRAN)) (face-sensor-on) (have-face-sensor ?pos) (at ?obj ?pos))
		:effect (and (when (and (face ?obj down)) (and  (P0TRAN) (not (face-sensor-on))))(when (and (face ?obj up)) (and  (P0TRAN) (not (face-sensor-on))))(when (and (not (face ?obj down))(not (face ?obj up))) (and  (P0TRAN) (face ?obj ?face) (not (face-sensor-on)))))
	)
	
	


)

;; MGSyn: Automatic Synthesis for Industrial Automation
;;
;; This automatically generated file contains the problem description for the system model.
;; The file is PDDL-like for ease of understanding. This file can be fed into the synthesis
;; engine GAVS+ (http://www6.in.tum.de/~chengch/gavs) to generate a high-level control strategy.
;;
;; Contact:
;;  - Chih-Hong Cheng, fortiss GmbH (cheng.chihhong@googlemail.com)
;;  - Michael Geisinger, fortiss GmbH (msg@tum.de) 
;; 

;; Problem Description
(define (problem pb)
		(:domain testdomaindomain)
		(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions :equality :fluents)
		(:objects  
		

			RAS01-X  - robotposition

			RAS01-Y  - robotposition
			RAS01-L1A - rackposition
			RAS01-L1B - rackposition
			RAS01-L2A - rackposition
			RAS01-L2B - rackposition
			robot - gripperposition

			CB01-from  - beltposition


			CB02-from  - beltposition

			CB02-to  - beltposition

			CB03-from  - beltposition

			CB03-to  - beltposition









			RP01-a  - plateposition

			RP01-b  - plateposition

			RP01-c  - plateposition

			RP01-d  - plateposition
			RP01-e - plateposition
			RP01-f - plateposition


		
		
			
			ball1 - workpiece
			ball2 - workpiece
			down - facetype
			up - facetype
			red - colortype
			white - colortype
			yes - occupytype
			no - occupytype
		
	RAS01 - device
	CB01 - device
	CB02 - device
	CB03 - device
	CB04 - device
	Lever01 - device
	Lever02 - device
	Lever03 - device
	RP01 - device
	HS01 - device
	Drill01 - device
		
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
		
		(:init
				(P0TRAN)
				
				
				
				;; Explicit initial conditions
				 ( at ball1 CB03-from)
				 ( at ball2 CB02-from)

				;; Initial conditions for device RAS01
				 ( in-robot RAS01-X)
				 ( free-hand robot)
				(device-use RAS01 beh-robot-check-shelf)
				(device-use RAS01 beh-trigger-color-sensor)
				(device-use RAS01 beh-robot-move)
				(device-use RAS01 beh-robot-pick-ground)
				(device-use RAS01 beh-robot-drop-ground)
				(device-use RAS01 beh-robot-pick-shelf)
				(device-use RAS01 beh-robot-drop-shelf)
				;; Initial conditions for device CB01
				 ( belt-connected CB01-from CB02-from)
				(device-use CB01 beh-belt-move-b)
				;; Initial conditions for device CB02
				 ( belt-connected CB02-from CB02-to)
				(device-use CB02 beh-belt-move-b)
				;; Initial conditions for device CB03
				 ( belt-connected CB03-from CB03-to)
				(device-use CB03 beh-belt-move-b)
				;; Initial conditions for device CB04
				 ( belt-connected RAS01-X CB03-from)
				(device-use CB04 beh-belt-move-r)
				;; Initial conditions for device Lever01
				 ( lever-located CB03-to RP01-a)
				(device-use Lever01 beh-lever-push)
				;; Initial conditions for device Lever02
				 ( lever-located RP01-d CB01-from)
				(device-use Lever02 beh-lever-push)
				;; Initial conditions for device Lever03
				 ( lever-located CB02-to RAS01-Y)
				(device-use Lever03 beh-lever-push)
				;; Initial conditions for device RP01
				 ( clockwise-next RP01-a RP01-b)
				 ( clockwise-next RP01-b RP01-c)
				 ( clockwise-next RP01-c RP01-d)
				 ( clockwise-next RP01-d RP01-e)
				 ( clockwise-next RP01-e RP01-f)
				 ( clockwise-next RP01-f RP01-a)
				(device-use RP01 beh-plate-rotate-two)
				;; Initial conditions for device HS01
				 ( have-face-sensor RP01-b)
				(device-use HS01 beh-trigger-probe-sensor)
				;; Initial conditions for device Drill01
				 ( drillposition RP01-c)
				(device-use Drill01 beh-drill-in)
				

					(device-hold  RAS01	RAS01-X ) 

					(device-hold  RAS01	RAS01-Y ) 
					(device-hold  RAS01	RAS01-L1A)
					(device-hold  RAS01	RAS01-L1B)
					(device-hold  RAS01	RAS01-L2A)
					(device-hold  RAS01	RAS01-L2B)
					(device-hold  RAS01	robot)

					(device-hold  CB01	CB01-from ) 
					(device-hold  CB01	CB02-from ) 

					(device-hold  CB02	CB02-from ) 

					(device-hold  CB02	CB02-to ) 

					(device-hold  CB03	CB03-from ) 

					(device-hold  CB03	CB03-to ) 
					(device-hold  CB04	RAS01-X ) 
					(device-hold  CB04	CB03-from ) 
					(device-hold  Lever01	CB03-to ) 
					(device-hold  Lever01	RP01-a ) 
					(device-hold  Lever02	RP01-d ) 
					(device-hold  Lever02	CB01-from ) 
					(device-hold  Lever03	CB02-to ) 
					(device-hold  Lever03	RAS01-Y ) 

					(device-hold  RP01	RP01-a ) 

					(device-hold  RP01	RP01-b ) 

					(device-hold  RP01	RP01-c ) 

					(device-hold  RP01	RP01-d ) 
					(device-hold  RP01	RP01-e)
					(device-hold  RP01	RP01-f)
					(device-hold  HS01	RP01-b ) 
					(device-hold  Drill01	RP01-c ) 
				
		)
		
		;; 2) One drill by height and another sort by color with return
		;; Reachability specification (the goal specifies the set of target states) 
		(:goal (and (and (or (face ball1 up) (face ball1 down) ) (or (not (face ball1 up) ) (drilled ball1) ) (or (not (face ball1 down) ) (not (drilled ball1) ) ) (at ball1 CB02-from) ) (or (color ball2 red) (color ball2 white) ) (and (or (not (color ball2 red) ) (or (at ball2 RAS01-L1A) (and (and (rack-occupied RAS01-L1A yes) (not (rack-occupied RAS01-L1A no) ) ) (at ball2 CB03-from) ) ) ) (or (not (color ball2 white) ) (or (at ball2 RAS01-L2A) (and (and (rack-occupied RAS01-L2A yes) (not (rack-occupied RAS01-L2A no) ) ) (at ball2 CB03-from) ) ) ) ) )
		)
		;; Safety specification (bounded cost):
		;; Parallel composition operator = SUM
		;; Sequential composition operator = SUM
		;; the upperbound of the sequential cost is 43
)

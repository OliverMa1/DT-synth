;;
;; Author: Chih-Hong Cheng, Emmanuel Carlos Dean Leon, Suraj Nair 
;;
;; In this setting, we define the set of parameterized actions, such that we can perform 
;; a setup for SMErobotics by creating an instaintiation of a problem.
;;
;; 
;; In the advanced setting, we also assume that the human is working collaboratively. 
;; In this way, we can treat the action of the human to be the same as the robot action, 
;; and assign cost on that.
;; 
;; In the implementation, what is required is to perform a signaling to the human to work, 
;; and the robot waits until the task is accomplished. The mutual communication shall also 
;; be implemented.
;; 
;; In this model, each control action is annotated with an integer cost.

(define (domain SMErobotics_quantitative_domain)
	(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions :equality :fluents)	
	(:types 
		spaceposition 
		gripperposition   
		workpiece
		gripper
		grasptype
  	)  
  	(:constants yes no - grasptype)
	(:predicates 
	     ;; Property of a single object
	     (present ?obj - workpiece )
	     (at ?obj - workpiece ?pos - (either spaceposition gripperposition)) 
	     (graspable ?obj - workpiece  ?occ - grasptype)
	     
	     ;; Relations between multiple objects
	     (assembled-on ?obj1 ?obj2 - workpiece)
	     (larger ?obj1 ?obj2 - workpiece)	     
	     
	     (robot-at ?gripper - gripper ?pos - spaceposition)
	     (gripper-position-connect ?gripperposition - gripperposition ?gripper - gripper)
	     (free-hand ?gripper - gripper)
	     
	     ;; Spacial topology
	     (horizontal ?pos1 ?pos2 - spaceposition)
	     (vertical ?pos1 ?pos2 - spaceposition)
	     (high ?pos - spaceposition)
	     
	     ;; Sensor
	     (shape-sensor-on)
	     (probe-shape ?obj - workpiece)
	     (rotate-request-on)
	     
	     ;; Game setting
             (P0TRAN)
	)	
	
	;; Control actions

	;; atomic actions of the robot arm
	(:action robot-move-horizontal
		:parameters  (?gripper - gripper ?from ?to - spaceposition)
		:precondition (and (P0TRAN) (robot-at ?gripper ?from) (horizontal ?from ?to))
		:effect (and   (not (P0TRAN)) (not (robot-at ?gripper ?from)) (robot-at ?gripper ?to) (increase (total-cost) 1) 
		)
	)

	(:action robot-move-vertical
		:parameters  (?gripper - gripper ?from ?to - spaceposition)
		:precondition (and (P0TRAN) (robot-at ?gripper ?from) (vertical ?from ?to))
		:effect (and (not (P0TRAN)) (not (robot-at ?gripper ?from)) (robot-at ?gripper ?to) (increase (total-cost) 1) 
		)
	)

	(:action robot-pick
		:parameters (?obj - workpiece ?from - spaceposition ?to - gripperposition ?gripper - gripper)
		:precondition  (and (P0TRAN) (at ?obj ?from) (present ?obj) (robot-at ?gripper ?from) (not (high ?from)) 
				   (free-hand ?gripper)  (gripper-position-connect ?to ?gripper)
				   (graspable ?obj yes)
			       )
		:effect (and  (not (P0TRAN)) (at ?obj ?to) (not (at ?obj ?from)) 
			(not (free-hand ?gripper)  )  (increase (total-cost) 1)
		)
	)

	(:action robot-drop
		:parameters  (?obj - workpiece ?to - spaceposition ?from - gripperposition ?gripper - gripper)
		:precondition  (and (P0TRAN)  (gripper-position-connect ?from ?gripper) 
				  (robot-at ?gripper ?to) (at ?obj ?from) (not (high ?to)) 
			      )
		:effect (and  (not (P0TRAN)) (at ?obj ?to) (free-hand ?gripper) (not (at ?obj ?from)) (increase (total-cost) 1) 
			)
	)

	(:action robot-idle
		:parameters  (?gripper - gripper)
		:precondition  (and (P0TRAN))
		:effect (and  (not (P0TRAN)) (increase (total-cost) 0) )
	)


	(:action robot-assemble
		:parameters  (?obj1 ?obj2 - workpiece ?from - gripperposition ?to - spaceposition  ?gripper - gripper)
		:precondition  (and (P0TRAN) (gripper-position-connect ?from ?gripper) (not (free-hand ?gripper))
				    (robot-at ?gripper ?to) (at ?obj1 ?from) (at ?obj2 ?to)
				    (not (assembled-on ?obj1 ?obj2)) (larger ?obj2 ?obj1)
				    (present ?obj1) (present ?obj2)
			      )
		:effect (and  (not (P0TRAN)) (free-hand ?gripper) (not (at ?obj1 ?from)) (at ?obj2 ?to)
				(assembled-on ?obj1 ?obj2) (increase (total-cost) 1) 
			)
	)



	
	;; atomic actions of the grasp sensor 
	(:action trigger-shape-sensor
		:parameters  (?obj - workpiece)
		:precondition  (and (P0TRAN) (present ?obj) )
		:effect (and (shape-sensor-on)  (probe-shape ?obj) (not (P0TRAN) ) (increase (total-cost) 1)
			)
	)		
	(:action return-shape-value
		:parameters  (?obj - workpiece ?face - grasptype)
		:precondition  (and (not (P0TRAN)) (probe-shape ?obj) (shape-sensor-on) (not (rotate-request-on)))
		:effect (and (not (shape-sensor-on)) (not (probe-shape ?obj)) (P0TRAN) (graspable ?obj ?face)
			)
	)		
		
	;; Environment actions
	;; The workpiece appears 
	 (:action workpiece-appear
	 	:parameters  (?obj  - workpiece)
		:precondition  (and (not (P0TRAN)) (not (present ?obj)) (not (shape-sensor-on)) (not (rotate-request-on)))
		:effect (and (P0TRAN) (present ?obj))
	)

	;; Nothing happens (either the workpiece has appeared, or the environment just decides not to appear the workpiece)
	(:action nothing-occur
		:parameters  (?obj  - workpiece)
		:precondition  (and (not (P0TRAN)) (not (shape-sensor-on)) (not (rotate-request-on)))
		:effect (and (P0TRAN))
	)


	;; Enforce the user to reposition the workpiece (collaborative worker)

	(:action collaborative-user-rotate-object
		:parameters  (?obj - workpiece)
		:precondition  (and (P0TRAN) (graspable ?obj no) (present ?obj)) 
		:effect (and (graspable ?obj yes) (not (graspable ?obj no)) (not (P0TRAN) ) (increase (total-cost) 1)
			)
	)
		
	(:action questionable-user-rotate-object-req
		:parameters  (?obj - workpiece) 
		:precondition  (and (P0TRAN) (graspable ?obj no) (present ?obj))
		:effect (and (not (P0TRAN)) (rotate-request-on) (increase (total-cost) 1) 
			)
	)	

	(:action questionable-user-rotate-object-rep-positive
		:parameters  (?obj - workpiece )
		:precondition  (and (not (P0TRAN)) (graspable ?obj no) (rotate-request-on)) 
		:effect (and (P0TRAN) (graspable ?obj yes) (not (graspable ?obj no)) (not (rotate-request-on))
			)
	)	

	(:action questionable-user-rotate-object-rep-negative
		:parameters  (?obj - workpiece )
		:precondition  (and (not (P0TRAN)) (graspable ?obj no) (rotate-request-on))
		:effect (and (P0TRAN) (not (rotate-request-on))
			)
	)

)
                   
             

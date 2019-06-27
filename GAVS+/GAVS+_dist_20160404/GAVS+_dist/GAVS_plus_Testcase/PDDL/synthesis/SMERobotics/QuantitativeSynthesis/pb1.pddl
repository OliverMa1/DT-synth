(define (problem pb1)
    	(:domain SMErobotics_quantitative_domain)
    	(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions :equality :fluents)
  	(:objects 
  	 	  bigobj smallobj - workpiece
  	 	  bigposition-down bigposition-up  - spaceposition
  	 	  smallposition-down smallposition-up  - spaceposition
  	 	  initialposition-down initialposition-up - spaceposition
  		  handposition - gripperposition
  	 	  robot - gripper
  	 	  yes no - grasptype
  	)  
  	
  	(:init  
  		;; Object description:
  		;; (present bigobj)
  		;; (present smallobj)
  		(at bigobj bigposition-down) ;; The value is meaningless when the workpiece is not there.
		(at smallobj smallposition-down) ;; The value is meaningless when the workpiece is not there.

		(larger bigobj smallobj)
		
		;; Gripper description
		(robot-at robot initialposition-up)
		(gripper-position-connect handposition robot)
		(free-hand robot)
		
		;; Spacial topology
		;; Horizontal
		(horizontal initialposition-up smallposition-up)
		(horizontal initialposition-up bigposition-up)
		(horizontal bigposition-up smallposition-up)
		(horizontal bigposition-up initialposition-up )
		(horizontal smallposition-up initialposition-up )
		(horizontal smallposition-up bigposition-up )


		;; Vertical
		(vertical initialposition-up initialposition-down)
		(vertical initialposition-down initialposition-up)
		(vertical bigposition-up bigposition-down)
		(vertical bigposition-down bigposition-up)
		(vertical smallposition-up smallposition-down)
		(vertical smallposition-down smallposition-up)

		;; High position
		(high smallposition-up)
		(high bigposition-up)
		(high initialposition-up)
	)	
	
	
	;; Spec 1: Move the robot to the desired position
	;; (:goal  (robot-at robot smallposition-down) 
	;; )
	
	;; Spec 2: Get the object
	;; (:goal (at smallobj handposition) 
	;; )
	
	;; Spec 3: Move the object to the desired position
	;; (:goal  (at smallobj bigposition-down) 
	;; )
	
	;; Spec 4: Assemble the workpiece
	;; (:goal  (assembled-on smallobj bigobj)
	;; )
	
		
	;; Spec 5: Goal-or-loop condition
	;; Either the big object never appear, or it is picked by the robot arm
	;; [] (not (present bigobj)) || <> (at bigobj handposition)	
	;; 
	;; Base specification (goal)
	;; (:goal   (at bigobj handposition)
	;; )
	;; The secondary (loop) condition is provided manually using dialog box
	;; 1. (not (present bigobj)) 

	;; Spec 6: Goal-or-loop condition
	;; Either the big object or the small object never appear, or they are assembled
	;; [] ((not (present bigobj)) || (not (present bigobj))) || <> (assembled-on smallobj bigobj)
	;; 
	;; Base specification (goal)
	(:goal  (assembled-on smallobj bigobj)
	)
	;; The secondary (loop) condition is provided manually using dialog box
	;; 1. (not (present bigobj)) 
	;; 2. (not (present smallobj)) 


	;; We can have more advanced options such as check if the user has helped to position the object.
	;; [] (the workpiece is not present || the user does not help to shape it correctly infinitely) OR <> (the workpieces are assembled appropriately)	
	;; 
	
)
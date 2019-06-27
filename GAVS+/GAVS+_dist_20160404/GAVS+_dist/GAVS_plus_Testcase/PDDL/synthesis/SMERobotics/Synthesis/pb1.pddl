(define (problem pb1)
    	(:domain SMErobotics_domain)
    	(:requirements :strips :typing :negative-preconditions :disjunctive-preconditions)
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
  		;; Object description: all workpieces are present
  		(present bigobj)
  		(present smallobj)
  		(at bigobj bigposition-down) 
		(at smallobj smallposition-down) 

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
	
	;; First we have a simple scenario that both object is present, so the robot just picks them and assemble
	
	;; Move the robot to the desired position
	;; (:goal  (robot-at robot smallposition-down) 
	;; )
	
	;; Get the object
	;; (:goal (at smallobj handposition) 
	;; )
	
	;; Move the object to the desired position
	;; (:goal  (at smallobj bigposition-down) 
	;; )
	
	;; Assemble the workpiece
	(:goal  (assembled-on smallobj bigobj)
	)
	
	;; (:goal  (assembled-on bigobj smallobj)
	;; )
	
	
	;; The winning condition is as following:
	;; Either the big object never appear, or it is picked by the robot arm
	;; [] (not (present bigobj)) || <> (at bigobj handposition)
	;; <> (present bigobj) -> <> (at bigobj handposition)
	;; 
	;; We set the states (present bigobj) && (at bigobj handposition)  to be color 2
	;;                   (present bigobj) && !(at bigobj handposition) to be color 1
	;;                   !(present bigobj) to be color 0
	;;
	;; This corresponds to a weak parity game, where the goal is for the player to win the game with max(Occ(\pho))=even


	;; We can have more advanced options such as check if the user has helped to position the object.
	;; [] (the workpiece is not present || the user does not help to shape it correctly infinitely) OR <> (the workpieces are assembled appropriately)
)
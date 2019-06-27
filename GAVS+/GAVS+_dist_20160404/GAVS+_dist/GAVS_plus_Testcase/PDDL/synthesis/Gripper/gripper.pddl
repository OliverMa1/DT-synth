;; In this model, we create the behavior of uncertainty (faults) 
;; (one robot may suddenly break down and drop the ball to the ground if it holds a ball)
;; See examples in our chair (the arm-like structure)
;; We can view this as fault-tolerance, as we can introduce additional actions 
;; (the actions here are close to templates)

(define (domain gripper)
(:requirements :strips :negative-preconditions)
(:predicates (room ?r)
             (ball ?b)
             (gripper ?g)
             (at-robby ?r)
             (at ?b ?r)
             (free ?g)
             (carry ?o ?g)
             (is-working ?g)
             (trigger)
             (P0TRAN))

	(:action move
		:parameters  (?from ?to)
		:precondition (and  (P0TRAN) (room ?from) (room ?to) (at-robby ?from))
		:effect (and   (not (P0TRAN))  (at-robby ?to) (not (at-robby ?from))))
		
	(:action pick
		:parameters (?obj ?room ?gripper)
		:precondition  (and (P0TRAN) (is-working ?gripper) (ball ?obj) (room ?room) (gripper ?gripper)
                     (at ?obj ?room) (at-robby ?room) (free ?gripper))
		:effect (and  (not (P0TRAN)) (carry ?obj ?gripper) (not (at ?obj ?room)) 
              (not (free ?gripper))))
 
	(:action drop
		:parameters  (?obj  ?room ?gripper)
		:precondition  (and (P0TRAN) (is-working ?gripper) (ball ?obj) (room ?room) (gripper ?gripper)
                     (carry ?obj ?gripper) (at-robby ?room))
		:effect (and  (not (P0TRAN)) (at ?obj ?room) (free ?gripper) (not (carry ?obj ?gripper))))
	

        ;; Fault-behavior
	(:action UNPLUG-DROP
		      :parameters (?obj  ?room ?gripper)
		      :precondition (and (not (P0TRAN)) (is-working ?gripper) (not (trigger)) 
		      		(ball ?obj) (room ?room) (gripper ?gripper) (carry ?obj ?gripper) (at-robby ?room))
	      	      :effect (and  (P0TRAN) (trigger)  (not (is-working ?gripper))
	      			(at ?obj ?room) (free ?gripper) (not (carry ?obj ?gripper)) ) )
	
	(:action UNPLUG-HALT
		      :parameters (?obj  ?gripper)
		      :precondition (and (not (P0TRAN)) (is-working ?gripper) (not (trigger)) 
				(ball ?obj)  (gripper ?gripper) (free ?gripper) (not (carry ?obj ?gripper)) )
	              :effect (and  (P0TRAN) (trigger) (not (is-working ?gripper))  ) )
	
	(:action NOTHING
	  	      :parameters ()
	  	      :precondition (and (not (P0TRAN)) )
  	              :effect (and  (P0TRAN)) )
		
)
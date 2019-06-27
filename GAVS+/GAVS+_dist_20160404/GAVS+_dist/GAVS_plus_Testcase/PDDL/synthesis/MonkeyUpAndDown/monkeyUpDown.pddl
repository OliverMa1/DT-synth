(define (domain monkeyUpDown)	       
  (:requirements :strips :negative-preconditions)
 
  (:constants monkey box knife bananas)
 
  (:predicates (location ?x)
	       (on-floor)
	       (at ?m ?x)
	       (hasknife)
	       (onbox ?x)
	       (hasbananas)
	       (P0TRAN)
	       (trigger))
  

  ;; movement and climbing
  (:action GO-TO
	     :parameters (?x ?y)
	     :precondition (and (P0TRAN) (location ?x) (location ?y) (on-floor) (at monkey ?y))
	     :effect (and (not (P0TRAN)) (at monkey ?x) (not (at monkey ?y))))
  
  (:action CLIMB_UP
	     :parameters (?x)
	     :precondition (and (P0TRAN) (on-floor) (location ?x) (at box ?x) (at monkey ?x))
	     :effect (and (not (P0TRAN)) (onbox ?x) (not (on-floor))))
  
  (:action CLIMB_DOWN
  	     :parameters (?x)
  	     :precondition (and (P0TRAN) (not (on-floor)) (location ?x) (onbox ?x) (at box ?x) (at monkey ?x))
	     :effect (and (not (P0TRAN)) (on-floor) (not (onbox ?x))))
  
  (:action PUSH-BOX
	     :parameters (?x ?y)
	     :precondition (and (P0TRAN) (on-floor) (location ?x) (location ?y) (at box ?y) (at monkey ?y) 
				 (on-floor))
	     :effect (and (not (P0TRAN)) (at monkey ?x) (not (at monkey ?y))
			   (at box ?x)    (not (at box ?y))))

  ;; getting bananas
  (:action GET-KNIFE
	     :parameters (?y)
	     :precondition (and (P0TRAN) (on-floor) (location ?y) (at knife ?y) (at monkey ?y))
	     :effect (and (not (P0TRAN)) (hasknife) (not (at knife ?y))))
  
  (:action GRAB-BANANAS
	     :parameters (?y)
	     :precondition (and (P0TRAN) (location ?y) (hasknife) 
                                 (at bananas ?y) (onbox ?y))
	     :effect (and (not (P0TRAN)) (hasbananas)) )
  
  ;; idle move
  ;; (:action STAY-MONKEY 
  ;;	     :parameters ()
  ;;	     :precondition (and (P0TRAN))
  ;;	     :effect (and (not (P0TRAN)))  )
  
  ;; wind blowing banana
  (:action BLOW
	      :parameters (?x ?y)
	      :precondition (and (not (P0TRAN)) (not (trigger))  (location ?x) (location ?y) (at bananas ?y))
	      :effect (and  (P0TRAN) (at bananas ?x) (not (at bananas ?y)) (trigger)) )
  
  (:action STAY-BANANA
  	      :parameters ()
  	      :precondition (and (not (P0TRAN)) )
  	      :effect (and  (P0TRAN)) )
  )
  
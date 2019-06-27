;;
;;
;; This example is modified from the example in the tool MBP (Model Based Planner) 
;; Contact: chengch@in.tum.de
;; 
;; In MBP, it allows the use of the keyword "oneof" to represent the non-determinacy, and it
;; uses a notion called "strong plan" to work on the synthesis problem.
;; 
;; The notion of "oneof" can be easily modeled by the environment transitions in the game setting.
;; In GAVS+, we use an additional predicate P0TRAN to repesent the change of players (system <-> environment).
;;

(define (domain elevator)
  (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions)
  (:constants f1 f2 f3 f4 f5 f6 f7 f8)
  (:predicates (elev_loc ?x) (elev_req ?x)
  		(P0TRAN))

  (:action move_up
     :parameters ()
     :precondition (and (P0TRAN)
     			(or 
     				(elev_loc f1)
     				(elev_loc f2)
     				(elev_loc f3)
     				(elev_loc f4)
     				(elev_loc f5)
     				(elev_loc f6)
     				(elev_loc f7)
     			)
     		   )
     :effect (and 
               (when (elev_loc f1) (and (not (P0TRAN)) (elev_loc f2) (not (elev_req f2)) (not (elev_loc f1)) ))
               (when (elev_loc f2) (and (not (P0TRAN)) (elev_loc f3) (not (elev_req f3)) (not (elev_loc f2)) ))
               (when (elev_loc f3) (and (not (P0TRAN)) (elev_loc f4) (not (elev_req f4)) (not (elev_loc f3)) ))
               (when (elev_loc f4) (and (not (P0TRAN)) (elev_loc f5) (not (elev_req f5)) (not (elev_loc f4)) ))
               (when (elev_loc f5) (and (not (P0TRAN)) (elev_loc f6) (not (elev_req f6)) (not (elev_loc f5)) ))
               (when (elev_loc f6) (and (not (P0TRAN)) (elev_loc f7) (not (elev_req f7)) (not (elev_loc f6)) ))
               (when (elev_loc f7) (and (not (P0TRAN)) (elev_loc f8) (not (elev_req f8)) (not (elev_loc f7)) ))
              )
   )

  (:action move_down
     :parameters ()
     :precondition (and (P0TRAN)
     			(or 
     				(elev_loc f2)
     				(elev_loc f3)
     				(elev_loc f4)
     				(elev_loc f5)
     				(elev_loc f6)
     				(elev_loc f7)
     				(elev_loc f8)
     			)
     		   )
     :effect (and 
               (when (elev_loc f2) (and (not (P0TRAN)) (elev_loc f1) (not (elev_req f1)) (not (elev_loc f2)) ))
               (when (elev_loc f3) (and (not (P0TRAN)) (elev_loc f2) (not (elev_req f2)) (not (elev_loc f3)) ))
               (when (elev_loc f4) (and (not (P0TRAN)) (elev_loc f3) (not (elev_req f3)) (not (elev_loc f4)) ))
               (when (elev_loc f5) (and (not (P0TRAN)) (elev_loc f4) (not (elev_req f4)) (not (elev_loc f5)) ))
               (when (elev_loc f6) (and (not (P0TRAN)) (elev_loc f5) (not (elev_req f5)) (not (elev_loc f6)) ))
               (when (elev_loc f7) (and (not (P0TRAN)) (elev_loc f6) (not (elev_req f6)) (not (elev_loc f7)) ))
               (when (elev_loc f8) (and (not (P0TRAN)) (elev_loc f7) (not (elev_req f7)) (not (elev_loc f8)) ))
              )
   )

  (:action move_still
     :parameters ()
     :precondition (and (P0TRAN)
     			(or 
     				(elev_loc f1)
     				(elev_loc f2)
     				(elev_loc f3)
     				(elev_loc f4)
     				(elev_loc f5)
     				(elev_loc f6)
     				(elev_loc f7)
     				(elev_loc f8)
     			)
     		   )
     :effect (and 
               (when (elev_loc f1) (and (not (P0TRAN)) (elev_loc f1) (not (elev_req f1))  ))
               (when (elev_loc f2) (and (not (P0TRAN)) (elev_loc f2) (not (elev_req f2))  ))
               (when (elev_loc f3) (and (not (P0TRAN)) (elev_loc f3) (not (elev_req f3))  ))
               (when (elev_loc f4) (and (not (P0TRAN)) (elev_loc f4) (not (elev_req f4))  ))
               (when (elev_loc f5) (and (not (P0TRAN)) (elev_loc f5) (not (elev_req f5))  ))
               (when (elev_loc f6) (and (not (P0TRAN)) (elev_loc f6) (not (elev_req f6))  ))
               (when (elev_loc f7) (and (not (P0TRAN)) (elev_loc f7) (not (elev_req f7))  ))
               (when (elev_loc f8) (and (not (P0TRAN)) (elev_loc f8) (not (elev_req f8))  ))
              )
   )

       
               
  ;; Request (in GAVS+, we model it as environment move)
  
  (:action elev_req
     :parameters (?f)
     :precondition (not (P0TRAN)) 
     :effect (and (P0TRAN) (elev_req ?f) ) 
             )
  )

  (:action elev_no_req
       :parameters ()
       :precondition (not (P0TRAN)) 
       :effect (and (P0TRAN))              
  )

)



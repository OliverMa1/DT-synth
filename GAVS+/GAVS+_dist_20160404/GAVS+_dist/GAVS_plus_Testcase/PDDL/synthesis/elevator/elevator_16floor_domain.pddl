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
  (:constants f1 f2 f3 f4 f5 f6 f7 f8 f9 f10 f11 f12 f13 f14 f15 f16)
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
				(elev_loc f8)
				(elev_loc f9)
				(elev_loc f10)
				(elev_loc f11)
				(elev_loc f12)
				(elev_loc f13)
				(elev_loc f14)
				(elev_loc f15)
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
		(when (elev_loc f8) (and (not (P0TRAN)) (elev_loc f9) (not (elev_req f8)) (not (elev_loc f8)) ))
		(when (elev_loc f9) (and (not (P0TRAN)) (elev_loc f10) (not (elev_req f9)) (not (elev_loc f9)) ))
		(when (elev_loc f10) (and (not (P0TRAN)) (elev_loc f11) (not (elev_req f10)) (not (elev_loc f10)) ))
		(when (elev_loc f11) (and (not (P0TRAN)) (elev_loc f12) (not (elev_req f11)) (not (elev_loc f11)) ))
		(when (elev_loc f12) (and (not (P0TRAN)) (elev_loc f13) (not (elev_req f12)) (not (elev_loc f12)) ))
		(when (elev_loc f13) (and (not (P0TRAN)) (elev_loc f14) (not (elev_req f13)) (not (elev_loc f13)) ))
		(when (elev_loc f14) (and (not (P0TRAN)) (elev_loc f15) (not (elev_req f14)) (not (elev_loc f14)) ))
		(when (elev_loc f15) (and (not (P0TRAN)) (elev_loc f16) (not (elev_req f15)) (not (elev_loc f15)) ))
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
		(when (elev_loc f9) (and (not (P0TRAN)) (elev_loc f8) (not (elev_req f8)) (not (elev_loc f9)) ))
		(when (elev_loc f10) (and (not (P0TRAN)) (elev_loc f9) (not (elev_req f9)) (not (elev_loc f10)) ))
		(when (elev_loc f11) (and (not (P0TRAN)) (elev_loc f10) (not (elev_req f10)) (not (elev_loc f11)) ))
		(when (elev_loc f12) (and (not (P0TRAN)) (elev_loc f11) (not (elev_req f11)) (not (elev_loc f12)) ))
		(when (elev_loc f13) (and (not (P0TRAN)) (elev_loc f12) (not (elev_req f12)) (not (elev_loc f13)) ))
		(when (elev_loc f14) (and (not (P0TRAN)) (elev_loc f13) (not (elev_req f13)) (not (elev_loc f14)) ))
		(when (elev_loc f15) (and (not (P0TRAN)) (elev_loc f14) (not (elev_req f14)) (not (elev_loc f15)) ))
		(when (elev_loc f16) (and (not (P0TRAN)) (elev_loc f15) (not (elev_req f15)) (not (elev_loc f16)) ))
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
				(elev_loc f9)
				(elev_loc f10)
				(elev_loc f11)
				(elev_loc f12)
				(elev_loc f13)
				(elev_loc f14)
				(elev_loc f15)
				(elev_loc f16)
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
		(when (elev_loc f9) (and (not (P0TRAN)) (elev_loc f9) (not (elev_req f9))  ))
		(when (elev_loc f10) (and (not (P0TRAN)) (elev_loc f10) (not (elev_req f10))  ))
		(when (elev_loc f11) (and (not (P0TRAN)) (elev_loc f11) (not (elev_req f11))  ))
		(when (elev_loc f12) (and (not (P0TRAN)) (elev_loc f12) (not (elev_req f12))  ))
		(when (elev_loc f13) (and (not (P0TRAN)) (elev_loc f13) (not (elev_req f13))  ))
		(when (elev_loc f14) (and (not (P0TRAN)) (elev_loc f14) (not (elev_req f14))  ))
		(when (elev_loc f15) (and (not (P0TRAN)) (elev_loc f15) (not (elev_req f15))  ))
		(when (elev_loc f16) (and (not (P0TRAN)) (elev_loc f16) (not (elev_req f16))  ))               
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



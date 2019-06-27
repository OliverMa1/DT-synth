;;
;;
;; This example is modified from the lecture "Verification and Synthesis of Reactive Systems" by Prof. Amir Pnueli
;; in Mini-Course, Universita' di Roma La Sapienza June, 2006
;;
;; Contact: chengch@in.tum.de
;; 
;; In GAVS+, we use an additional predicate P0TRAN to repesent the change of players (system <-> environment).
;;

(define (domain arbiter)
  (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions)
  (:constants  c1 c2)
  (:predicates (request ?x) (grant ?x) (P0TRAN))

  ;; Possible moves of each client (in GAVS+, we model it as environment move)
  
  (:action request_resource
     :parameters (?x)
     :precondition (and (not (P0TRAN)) (not (request ?x)) (not (grant ?x)) )
     :effect (and  (P0TRAN) (request ?x) (not (grant ?x)) )
   )
   
   (:action release_resource
        :parameters (?x)
        :precondition (and (not (P0TRAN)) (request ?x)  (grant ?x))
        :effect (and (P0TRAN) (not (request ?x))  (grant ?x))
   )
   
   ;; Possible moves by the arbitor (controller move)

  (:action grant_resource
     :parameters (?x)
     :precondition (and (P0TRAN) (request ?x) (not (grant ?x)) )
     :effect (and (not (P0TRAN)) (request ?x) (grant ?x) )
   )

   (:action take_resource
     :parameters (?x)
     :precondition (and (P0TRAN) (not (request ?x)) (grant ?x) )
     :effect (and (not (P0TRAN)) (not (request ?x)) (not (grant ?x)) )
   )   
   
   ;; (:action do_nothing
   ;;        :parameters ()
   ;;        :precondition (and (P0TRAN) )
   ;;        :effect (and (not (P0TRAN)) )
   ;;)  

)



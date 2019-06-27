;;
;; For reachability and Buechi conditions, follow the original goal statement.
;; 
;; Generalized Reactivity
;; []<> (not (and (request c1) (grant c1))) && []<> (not (and (request c2) (grant c2))) && []<> (not (and (request c3) (grant c3))) 
;; && []<> (not (and (request c4) (grant c4))) && []<> (not (and (request c5) (grant c5))) 
;; -> 
;; []<> (= (request c1) (grant c1)) && []<> (= (request c2) (grant c2)) && []<> (= (request c3) (grant c3)) && []<> (= (request c4) (grant c4)) 
;; && []<> (= (request c5) (grant c5)) 
;;
;; Single line specification (for GAVS+ to parse)
;; []<> (not (and (request c1) (grant c1))) && []<> (not (and (request c2) (grant c2))) && []<> (not (and (request c3) (grant c3))) && []<> (not (and (request c4) (grant c4))) && []<> (not (and (request c5) (grant c5))) -> []<> (= (request c1) (grant c1)) && []<> (= (request c2) (grant c2)) && []<> (= (request c3) (grant c3)) && []<> (= (request c4) (grant c4)) && []<> (= (request c5) (grant c5)) 
;;

(define (problem arbiter_problem)
        (:domain arbiter)
        (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions)
        (:objects  c1 c2 c3 c4 c5)
	(:init )        
	
	;; without explicitly mentioned, all predicates are set to be false in the initial condition
	;; (not (P0TRAN)) 
	;; (not (request c1)) (not (grant c1)) (not (request c2)) (not (grant c2)) (not (request c3)) (not (grant c3))
	;; (not (request c4)) (not (grant c4)) (not (request c5)) (not (grant c5))		
	
        (:goal ( and (request c1)))
)
  

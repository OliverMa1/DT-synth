;;
;; For reachability and Buechi conditions, follow the original goal statement.
;; 
;; To synthesize the strategy for this planning problem, in GAVS+ 
;; select "PDDL -> game solving -> reachability (or Buechi or Generalized Reactivity)"
;;
;; []<> (or (elev_req f1) (elev_req f2) (elev_req f3) (elev_req f4) (elev_req f5) (elev_req f6) (elev_req f7) (elev_req f8)) 
;; -> []<> (or (not (elev_req f1)) (elev_loc f1)) && []<> (or (not (elev_req f2)) (elev_loc f2)) && []<> (or (not (elev_req f3)) (elev_loc f3))  
;;    && []<> (or (not (elev_req f4)) (elev_loc f4)) && []<> (or (not (elev_req f5)) (elev_loc f5)) && []<> (or (not (elev_req f6)) (elev_loc f6)) 
;;    && []<> (or (not (elev_req f7)) (elev_loc f7)) && []<> (or (not (elev_req f8)) (elev_loc f8)) 
;; 
;; Single line specification (for GAVS+ to parse)
;; []<> (or (elev_req f1) (elev_req f2) (elev_req f3) (elev_req f4) (elev_req f5) (elev_req f6) (elev_req f7) (elev_req f8))  -> []<> (or (not (elev_req f1)) (elev_loc f1)) && []<> (or (not (elev_req f2)) (elev_loc f2)) && []<> (or (not (elev_req f3)) (elev_loc f3))    && []<> (or (not (elev_req f4)) (elev_loc f4)) && []<> (or (not (elev_req f5)) (elev_loc f5)) && []<> (or (not (elev_req f6)) (elev_loc f6))  && []<> (or (not (elev_req f7)) (elev_loc f7)) && []<> (or (not (elev_req f8)) (elev_loc f8)) 
;;

(define (problem navigation_problem)
        (:domain elevator)
        (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions)
        (:objects  f1 f2 f3 f4 f5 f6 f7 f8)
	(:init (P0TRAN) (elev_loc f1))
        (:goal ( and (elev_loc f8)))
)
  

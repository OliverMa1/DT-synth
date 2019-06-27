;;
;; For reachability and Buechi conditions, follow the original goal statement.
;; 
;; To synthesize the strategy for this planning problem, in GAVS+ 
;; select "PDDL -> game solving -> reachability (or Buechi or Generalized Reactivity)"
;;
;; []<> (or (elev_req f1) (elev_req f2) (elev_req f3) (elev_req f4) (elev_req f5) (elev_req f6) (elev_req f7) (elev_req f8)
;;          (elev_req f9)(elev_req f10)(elev_req f11)(elev_req f12)(elev_req f13)(elev_req f14)(elev_req f15)(elev_req f16)
;;          (elev_req f17)(elev_req f18)(elev_req f19)(elev_req f20)(elev_req f21)(elev_req f22)(elev_req f23)(elev_req f24)) 
;; -> []<> (or (not (elev_req f1)) (elev_loc f1)) && []<> (or (not (elev_req f2)) (elev_loc f2)) && []<> (or (not (elev_req f3)) (elev_loc f3))  
;;    && []<> (or (not (elev_req f4)) (elev_loc f4)) && []<> (or (not (elev_req f5)) (elev_loc f5)) && []<> (or (not (elev_req f6)) (elev_loc f6)) 
;;    && []<> (or (not (elev_req f7)) (elev_loc f7)) && []<> (or (not (elev_req f8)) (elev_loc f8)) && []<> (or (not (elev_req f9)) (elev_loc f9))
;;    && []<> (or (not (elev_req f10)) (elev_loc f10)) && []<> (or (not (elev_req f11)) (elev_loc f11)) && []<> (or (not (elev_req f12)) (elev_loc f12))
;;    && []<> (or (not (elev_req f13)) (elev_loc f13)) && []<> (or (not (elev_req f14)) (elev_loc f14)) && []<> (or (not (elev_req f15)) (elev_loc f15))
;;    && []<> (or (not (elev_req f16)) (elev_loc f16)) && []<> (or (not (elev_req f17)) (elev_loc f17)) && []<> (or (not (elev_req f18)) (elev_loc f18))
;;    && []<> (or (not (elev_req f19)) (elev_loc f19)) && []<> (or (not (elev_req f20)) (elev_loc f20)) && []<> (or (not (elev_req f21)) (elev_loc f21))
;;    && []<> (or (not (elev_req f22)) (elev_loc f22)) && []<> (or (not (elev_req f23)) (elev_loc f23)) && []<> (or (not (elev_req f24)) (elev_loc f24))
;;
;; Single line specification (for GAVS+ to parse)
;; []<> (or (elev_req f1) (elev_req f2) (elev_req f3) (elev_req f4) (elev_req f5) (elev_req f6) (elev_req f7) (elev_req f8)(elev_req f9)(elev_req f10)(elev_req f11)(elev_req f12)(elev_req f13)(elev_req f14)(elev_req f15)(elev_req f16) (elev_req f17)(elev_req f18)(elev_req f19)(elev_req f20)(elev_req f21)(elev_req f22)(elev_req f23)(elev_req f24)) -> []<> (or (not (elev_req f1)) (elev_loc f1)) && []<> (or (not (elev_req f2)) (elev_loc f2)) && []<> (or (not (elev_req f3)) (elev_loc f3))  && []<> (or (not (elev_req f4)) (elev_loc f4)) && []<> (or (not (elev_req f5)) (elev_loc f5)) && []<> (or (not (elev_req f6)) (elev_loc f6)) && []<> (or (not (elev_req f7)) (elev_loc f7)) && []<> (or (not (elev_req f8)) (elev_loc f8)) && []<> (or (not (elev_req f9)) (elev_loc f9)) && []<> (or (not (elev_req f10)) (elev_loc f10)) && []<> (or (not (elev_req f11)) (elev_loc f11)) && []<> (or (not (elev_req f12)) (elev_loc f12)) && []<> (or (not (elev_req f13)) (elev_loc f13)) && []<> (or (not (elev_req f14)) (elev_loc f14)) && []<> (or (not (elev_req f15)) (elev_loc f15)) && []<> (or (not (elev_req f16)) (elev_loc f16)) && []<> (or (not (elev_req f17)) (elev_loc f17)) && []<> (or (not (elev_req f18)) (elev_loc f18)) && []<> (or (not (elev_req f19)) (elev_loc f19)) && []<> (or (not (elev_req f20)) (elev_loc f20)) && []<> (or (not (elev_req f21)) (elev_loc f21)) && []<> (or (not (elev_req f22)) (elev_loc f22)) && []<> (or (not (elev_req f23)) (elev_loc f23)) && []<> (or (not (elev_req f24)) (elev_loc f24))
;; 

(define (problem navigation_problem)
        (:domain elevator)
        (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions)
        (:objects  f1 f2 f3 f4 f5 f6 f7 f8 f9 f10 f11 f12 f13 f14 f15 f16 f17 f18 f19 f20 f21 f22 f23 f24)
	(:init (P0TRAN) (elev_loc f1))
        (:goal ( and (elev_loc f24)))
)
  

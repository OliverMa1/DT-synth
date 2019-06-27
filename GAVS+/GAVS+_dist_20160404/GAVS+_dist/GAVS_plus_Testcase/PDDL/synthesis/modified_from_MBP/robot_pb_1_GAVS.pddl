;;
;; For reachability and Buechi conditions, follow the original goal statement.
;; 
;; For Generalized Reactivity conditions (i.e., properties of the form []<> p_1 && []<> p_m => []<> q_1 && []<> q_n),
;; E.g., enter "[]<> ( and (true)) -> []<> ( and (robot_position dep )) && []<> ( and (robot_position store ))"
;; By doing so, winning the game creates a strategy to repeatly visit the room "store" and room "dep". 
;;

(define (problem navigation_problem)
        (:domain robot_navigation)
        (:objects  store lab NE_room SW_room dep)
	(:init (P0TRAN) (robot_position store ))
        (:goal ( and (robot_position dep )))
)
  

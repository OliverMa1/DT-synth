;;
;;    -------------------------------------
;;    |                |                  |
;;    |                |      lab         |
;;    |                                   |
;;    |      store        -----   --------|
;;    |                                   |
;;    |                |      NE_room     |
;;    |                |                  |
;;    |------   ---------------   --------|
;;    |                |                  |
;;    |                |                  |
;;    |                                   |
;;    |    SW_room             dep        |
;;    |                |                  |
;;    |                |                  |
;;    -------------------------------------
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
;; To synthesize the strategy for this planning problem, in GAVS+ select "PDDL -> game solving -> reachability (or Buechi or Generalized Buechi)"
;;

(define (domain robot_navigation)
  (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions)
  (:constants store lab NE_room SW_room dep)
  (:predicates (robot_position ?x)
  		(P0TRAN))

  (:action move_robot_up
     :parameters ()
     :precondition (and (P0TRAN) 
                       ( or (robot_position SW_room)
     	                  (robot_position dep)
     	                  (robot_position NE_room)
     	               )
     	           )
     :effect (and 
               (when (robot_position SW_room) (and (P0TRAN) (robot_position store)  (not (robot_position SW_room)) ))
               (when (robot_position dep) (and (P0TRAN) (robot_position NE_room)  (not (robot_position dep)) ))
               (when (robot_position NE_room) (and (P0TRAN) (robot_position lab)  (not (robot_position NE_room)) ))
              )
   )

  (:action move_robot_down
     :parameters ()
     :precondition (and (P0TRAN) 
                       (or (robot_position store)
     	                  (robot_position lab)
     	                  (robot_position NE_room)
     	               )
     	           )
     :effect (and 
               (when (robot_position store) (and (P0TRAN) (robot_position SW_room)  (not (robot_position store)) ))
               (when (robot_position lab) (and (P0TRAN) (robot_position NE_room)  (not (robot_position lab)) ))
               (when (robot_position NE_room) (and (P0TRAN) (robot_position dep)  (not (robot_position NE_room)) ))
             )
   )

  (:action move_robot_right
     :parameters ()
     :precondition (and (P0TRAN)
     			(or (robot_position SW_room)
                           (robot_position store) 
                         )
                   )
     :effect (and
               (when (robot_position SW_room) (and (P0TRAN) (robot_position dep)  (not (robot_position SW_room)) ))
               (when (robot_position store) (not (P0TRAN))) 
             )
  )

  (:action move_robot_left
     :parameters ()
     :precondition (and (P0TRAN)
     			(or (robot_position dep)
                          (robot_position NE_room) 
                        )
                   )
     :effect (and
               (when (robot_position dep) (and (P0TRAN) (robot_position SW_room)  (not (robot_position dep)) ))
               (when (robot_position NE_room) (and (P0TRAN) (robot_position store)  (not (robot_position NE_room)) )) 
             )
   )
               
               
  ;; Non-deterministic move (in GAVS+, we model it as environment move)
  
  (:action move_NE_room
     :parameters ()
     :precondition (not (P0TRAN)) 
     :effect (and (P0TRAN) (robot_position NE_room)  (not (robot_position store)) 
             )
  )

  (:action move_lab
     :parameters ()
     :precondition (not (P0TRAN))
     :effect (and (P0TRAN) (robot_position lab)  (not (robot_position store)) 
             )
  )            

)



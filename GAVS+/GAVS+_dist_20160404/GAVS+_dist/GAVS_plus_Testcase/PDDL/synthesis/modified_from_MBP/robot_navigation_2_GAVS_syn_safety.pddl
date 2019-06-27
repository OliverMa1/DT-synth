;;
;;    -------------------------------------
;;    |                |                  |
;;    |                |      lab         |
;;    |                                   |
;;    |      store        -----   --------|
;;    |                                   |
;;    |                |      NE_room (E) |
;;    |                |                  |
;;    |------   ---------------   --------|
;;    |                |                  |
;;    |                |                  |
;;    |                                   |
;;    |    SW_room (S)          dep       |
;;    |                |                  |
;;    |                |                  |
;;    -------------------------------------
;;
;; This example is modified from the example in the tool MBP (Model Based Planner) 
;; Contact: chengch@in.tum.de
;; 
;; In this scenario, we try to model the controller of the S robot such that two robots will never simultaneously
;; stay in the same room to ensure high service quality. To achieve this feature, we can perform a projection 
;; such that regardless of the behavior of the E robot (i.e., the user can design the behavior of the E robot freely), 
;; the quality of service is done by the S robot. Our projection creates a safety game.
;;
;; To synthesize the strategy for this planning problem, in GAVS+ select "PDDL -> game solving -> safety"
;; 
;; However, the result shows that the S robot can not guarantee safety.
;; (1) E goes to lab
;; (2) S goes to dep (go to store implies next-move capture)
;; (3) E goes to store 
;; (4) All moves of S imply next-move capture
;;

(define (domain robot_navigation)
  (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions)
  (:constants store lab NE_room SW_room dep)
  (:predicates (robot_E_position ?x) (robot_S_position ?x) (P0TRAN) )
  
  
  (:action move_S_robot_up
     :parameters ()
     :precondition (and (P0TRAN)
     			(or (robot_S_position SW_room)
                       (robot_S_position dep)
                       (robot_S_position NE_room) )
                    )
     :effect (and
               (when (robot_S_position SW_room) (and (not (P0TRAN)) (robot_S_position store)  (not (robot_S_position SW_room)) ))
               (when (robot_S_position dep) (and (not (P0TRAN)) (robot_S_position NE_room)  (not (robot_S_position dep)) ))
               (when (robot_S_position NE_room) (and (not (P0TRAN)) (robot_S_position lab)  (not (robot_S_position NE_room)) ))
             )
   )

  (:action move_S_robot_down
     :parameters ()
     :precondition (and (P0TRAN)
     			(or (robot_S_position store)
                       (robot_S_position lab)
                       (robot_S_position NE_room) )
                   )
     :effect (and
               (when (robot_S_position store) (and (not (P0TRAN)) (robot_S_position SW_room)  (not (robot_S_position store)) ))
               (when (robot_S_position lab) (and (not (P0TRAN)) (robot_S_position NE_room)  (not (robot_S_position lab)) ))
               (when (robot_S_position NE_room) (and (not (P0TRAN)) (robot_S_position dep)  (not (robot_S_position NE_room)) ))
              )
   )

  (:action move_S_robot_right
     :parameters ()
     :precondition (and (P0TRAN)
     			(or (robot_S_position SW_room)
                       (robot_S_position store) )
                   )
     :effect (and
               (when (robot_S_position SW_room) (and (not (P0TRAN)) (robot_S_position dep)  (not (robot_S_position SW_room)) ))
               (when (robot_S_position store) (and (not (P0TRAN)) (robot_S_position NE_room)  (not (robot_S_position store)) )) 
             )
  )


  (:action move_S_robot_right_up
     :parameters ()
     :precondition (and (P0TRAN) (robot_S_position store))
     :effect (and              
               (when (robot_S_position store) (and (not (P0TRAN)) (robot_S_position lab)  (not (robot_S_position store)) )) 
             )
  )

  (:action move_S_robot_left
     :parameters ()
     :precondition (and (P0TRAN)
     			(or (robot_S_position dep)
                       (robot_S_position NE_room)
                       (robot_S_position lab))
                   )
     :effect (and
               (when (robot_S_position dep) (and (not (P0TRAN)) (robot_S_position SW_room)  (not (robot_S_position dep)) ))
               (when (robot_S_position NE_room) (and (not (P0TRAN)) (robot_S_position store)  (not (robot_S_position NE_room)) ))                
               (when (robot_S_position lab) (and (not (P0TRAN)) (robot_S_position store)  (not (robot_S_position lab)) )) 
              )
   )
   
  
 (:action move_E_robot_up
     :parameters ()
     :precondition (and (not (P0TRAN))
     			(or (robot_E_position SW_room)
                       (robot_E_position dep)
                       (robot_E_position NE_room) )
                   )
     :effect (and
               (when (robot_E_position SW_room) (and (P0TRAN) (robot_E_position store)  (not (robot_E_position SW_room)) ))
               (when (robot_E_position dep) (and (P0TRAN) (robot_E_position NE_room)  (not (robot_E_position dep)) ))
               (when (robot_E_position NE_room) (and (P0TRAN) (robot_E_position lab)  (not (robot_E_position NE_room)) ))
             )
   )

  (:action move_E_robot_down
     :parameters ()
     :precondition (and (not (P0TRAN))
     			(or (robot_E_position store)
                       (robot_E_position lab)
                       (robot_E_position NE_room) )
                   )
     :effect (and
               (when (robot_E_position store) (and (P0TRAN) (robot_E_position SW_room)  (not (robot_E_position store)) ))
               (when (robot_E_position lab) (and (P0TRAN) (robot_E_position NE_room)  (not (robot_E_position lab)) ))
               (when (robot_E_position NE_room) (and (P0TRAN) (robot_E_position dep)  (not (robot_E_position NE_room)) ))
              )
   )

  (:action move_E_robot_right
     :parameters ()
     :precondition (and (not (P0TRAN))
      			(or (robot_E_position SW_room)
                            (robot_E_position store) )
                    )
     :effect (and
               (when (robot_E_position SW_room) (and (P0TRAN) (robot_E_position dep)  (not (robot_E_position SW_room)) ))
               (when (robot_E_position store) (and (P0TRAN) (robot_E_position NE_room)  (not (robot_E_position store)) )) 
             )
  )


  (:action move_E_robot_right_up
     :parameters ()
     :precondition (and (not (P0TRAN)) (robot_E_position store))
     :effect (and              
               (when (robot_E_position store) (and (P0TRAN) (robot_E_position lab)  (not (robot_E_position store)) )) 
             )
  )

  (:action move_E_robot_left
     :parameters ()
     :precondition (and (not (P0TRAN))
     			(or (robot_E_position dep)
     		            (robot_E_position NE_room)
     		            (robot_E_position lab))
     		   )
     :effect (and
               (when (robot_E_position dep) (and (P0TRAN) (robot_E_position SW_room)  (not (robot_E_position dep)) ))
               (when (robot_E_position NE_room) (and (P0TRAN) (robot_E_position store)  (not (robot_E_position NE_room)) ))                
               (when (robot_E_position lab) (and (P0TRAN) (robot_E_position store)  (not (robot_E_position lab)) )) 
              )
   )
)



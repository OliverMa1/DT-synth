;;
;;    
;;    -------------------------------------
;;    |                |                  |
;;    |                |      lab         |
;;    |                |                  |
;;    |      store     |-------   --------|
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

(define (domain robot_navigation)
  (:requirements :strips :conditional-effects :disjunctive-preconditions)
  (:constants store lab NE_room SW_room dep)
  (:predicates (robot_position ?x))

  (:action move_robot_up
     :parameters ()
     :precondition (or (robot_position SW_room)
                       (robot_position dep)
                       (robot_position NE_room) )
     :effect (and
               (when (robot_position SW_room) (and (robot_position store)  (not (robot_position SW_room)) ))
               (when (robot_position dep) (and (robot_position NE_room)  (not (robot_position dep)) ))
               (when (robot_position NE_room) (and (robot_position lab)  (not (robot_position NE_room)) ))
             )
   )

  (:action move_robot_down
     :parameters ()
     :precondition (or (robot_position store)
                       (robot_position lab)
                       (robot_position NE_room) )
     :effect (and
               (when (robot_position store) (and (robot_position SW_room)  (not (robot_position store)) ))
               (when (robot_position lab) (and (robot_position NE_room)  (not (robot_position lab)) ))
               (when (robot_position NE_room) (and (robot_position dep)  (not (robot_position NE_room)) ))
              )
   )

  (:action move_robot_right
     :parameters ()
     :precondition (or (robot_position SW_room)
                       (robot_position store) )
     :effect (and
               (when (robot_position SW_room) (and (robot_position dep)  (not (robot_position SW_room)) ))
               (when (robot_position store) (and (robot_position NE_room)  (not (robot_position store)) )) 
             )
  )


  (:action move_robot_left
     :parameters ()
     :precondition (or (robot_position dep)
                       (robot_position NE_room) )
     :effect (and
               (when (robot_position dep) (and (robot_position SW_room)  (not (robot_position dep)) ))
               (when (robot_position NE_room) (and (robot_position store)  (not (robot_position NE_room)) )) 
              )
   )

)



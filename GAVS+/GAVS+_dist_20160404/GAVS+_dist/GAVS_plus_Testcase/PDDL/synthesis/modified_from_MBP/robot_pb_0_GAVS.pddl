(define (problem navigation_problem)
        (:domain robot_navigation)
        (:objects  store lab NE_room SW_room dep)
	(:init  (robot_position store ))
        (:goal ( and (robot_position dep  )))
)
  

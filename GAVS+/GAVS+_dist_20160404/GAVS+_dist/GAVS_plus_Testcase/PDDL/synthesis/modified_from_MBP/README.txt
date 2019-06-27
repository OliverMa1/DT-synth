In the following, we list out examples extended/modified from the MBP (domain, problem, PDDL solver)

1. (robot_navigation_0_GAVS.pddl, robot_pb_0_GAVS.pddl, Forward-Reachability)
The purpose of the problem is to create a plan to achieve the goal. 
Solution exists as a sequential plan.

2. (robot_navigation_1_GAVS.pddl, robot_pb_1_GAVS.pddl, Game-Reachability)
The purpose of the problem is to create a plan to achieve the goal regardless of uncertainty.
Solution exists as a finite state machine.

3. (robot_navigation_1_GAVS.pddl, robot_pb_1_GAVS.pddl, Game-Buechi)
The purpose of the problem is to create a plan to achieve the goal repeatly regardless of uncertainty.
Solution exists as a finite state machine.

4. (robot_navigation_1_GAVS.pddl, robot_pb_1_GAVS.pddl, Game-GR[1])
The purpose of the problem is to create a plan to achieve the goal repeatly regardless of uncertainty.
Solution exists as a finite state machine.

5. (robot_navigation_2_GAVS.pddl, robot_pb_2_GAVS.pddl, Game-Safety)
The purpose of the problem is to create a plan to avoid two robots staying in the same room.
Solution does not exist.

6. (robot_navigation_3_GAVS.pddl, robot_pb_3_GAVS.pddl, Game-Safety)
The purpose of the problem is to create a plan to avoid two robots staying in the same room.
Solution exists as a finite state machine.

#5 10 35 80 100 200
k = 80

string = "(define (domain gws_1d) \n \t (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions) \n \t (:constants "
string2 = "(define (problem gws_1d_pb) \n \t (:domain gws_1d)  \n \t (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions) \n \t     (:objects "


for x in range(0,k+1):
    string +=  "s"+str(x)+" "
    string +=  "e"+str(x)+" "
    string2 +=  "s"+str(x)+" "
    string2 +=  "e"+str(x)+" "

string +=  ") \n \t (:predicates (robot_S_position ?x) (robot_E_position ?x) (P0TRAN) )"
string2 += ") \n \t   (:init (robot_S_position s1) (robot_E_position e1)) \n \t (:goal (or \n \t "

for i in range(0,k):
  string2 += "(and (not(P0TRAN)) (robot_E_position e"+str(i)+") (not(robot_S_position s"+str(i)+")))\n"
string2 += ")))"

#system move left precondition and effect
string +=  "\n \t (:action move_S_robot_left \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in range(1,k+1):
  string +=  "(robot_S_position s"+str(x)+")"
string +=  "))\n \t :effect (and"
for x in range(1,k+1):
  string +=  "\t \t (when (robot_S_position s"+str(x)+") (and (not (P0TRAN)) (robot_S_position s"+str(x-1)+" )  (not (robot_S_position s"+str(x)+" )) ))"

#system move right precondition and effect
string +=  ")\n) \n \t (:action move_S_robot_right \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in range(0,k):
  string +=  "(robot_S_position s"+str(x)+")"
string +=  "))\n \t :effect (and"
for x in range(0,k):
  string +=  "\t \t (when (robot_S_position s"+str(x)+") (and (not (P0TRAN)) (robot_S_position s"+str(x+1)+" )  (not (robot_S_position s"+str(x)+" )) ))"

#system stay precondition and effect
string +=  ")\n) \t (:action move_S_robot_stay \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in range(0,k+1):
  string +=  "(robot_S_position s"+str(x)+")"
string +=  "))\n \t :effect (and"
for x in range(0,k+1):
  string +=  "\t \t (when (robot_S_position s"+str(x)+") (and (not (P0TRAN)) (robot_S_position s"+str(x)+" ) ))"

#Environment move right precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_right \n \t :parameters () \n \t :precondition (and (not (P0TRAN)) \n \t \t (or"
for x in range(0,k):
  string +=  "(robot_E_position e"+str(x)+")"
string +=  "))\n \t :effect (and"
for x in range(0,k):
  string +=  "\t \t (when (robot_E_position e"+str(x)+") (and (P0TRAN) (robot_E_position e"+str(x+1)+" )  (not (robot_E_position e"+str(x)+" )) ))"

#Environment stay precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_stay \n \t :parameters () \n \t :precondition (and (not (P0TRAN)) \n \t \t (or"
for x in range(0,k+1):
  string +=  "(robot_E_position e"+str(x)+")"
string +=  "))\n \t :effect (and"
for x in range(0,k+1):
  string +=  "\t \t (when (robot_E_position e"+str(x)+") (and (P0TRAN) (robot_E_position e"+str(x)+" ) ))"


#Environment move left precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_left \n \t :parameters () \n \t :precondition (and (not (P0TRAN)) \n \t \t (or"
for x in range(1,k+1):
  string +=  "(robot_E_position e"+str(x)+")"
string +=  "))\n \t :effect (and"
for x in range(1,k+1):
  string +=  "\t \t (when (robot_E_position e"+str(x)+") (and (P0TRAN) (robot_E_position e"+str(x-1)+" )  (not (robot_E_position e"+str(x)+" )) ))"
string +=  ")\n) \n)"


f = open('gws_1d.pddl', 'w')
f.write(string)
f.close

f = open('gws_1d_pb.pddl', 'w')
f.write(string2)
f.close

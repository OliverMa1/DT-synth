
k = 250
stateslist = ['farL','farR','L','R','In','faraway','U','UR','UL','B','BR','BL','farU','farB']
S_Left = ['L','faraway','In','farR','R','faraway','UR','faraway','U','BR','faraway','B','faraway','faraway']
S_Right = ['faraway','R','farL','In','L','faraway','UL','U','faraway','BL','B','faraway','faraway','faraway']
S_Up = ['faraway','faraway','BL','BR','B','faraway','In','R','L','farB','faraway','faraway','U','faraway']
S_Down = ['faraway','faraway','UL','UR','U','faraway','farU','faraway','faraway','In','R','L','faraway','B']
E_Left = ['faraway','R','farL','In','L','faraway','UL','U','faraway','BL','B','faraway','faraway','faraway']
E_Right = ['L','faraway','In','farR','R','faraway','UR','faraway','U','BR','faraway','B','faraway','faraway']
E_Up = ['faraway','faraway','UL','UR','U','faraway','farU','faraway','faraway','In','R','L','faraway','B']
E_Down = ['faraway','faraway','BL','BR','B','faraway','In','R','L','farB','faraway','faraway','U','faraway']

S_RightA = []
S_LeftA = []
S_UpA = []
S_DownA = []
stateslistA = []
stateslistB = []
for i in range(0,k+1):
  stateslistA.append('Loc'+str(i))
  if i!=k:
    S_RightA.append('Loc'+str(i+1))
  else:
    S_RightA.append('Loc'+str(i))  
  if i!=0:
    S_LeftA.append('Loc'+str(i-1))
  else:
    S_LeftA.append('Loc'+str(i))


string =  "(define (domain gws_1d_alt) \n \t (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions) \n \t (:constants "
string2 = "(define (problem gws_1d_alt_pb) \n \t (:domain gws_1d_alt)  \n \t (:requirements :strips :conditional-effects :negative-preconditions :disjunctive-preconditions) \n \t     (:objects "

for x in stateslist+stateslistA:
    string +=  str(x)+" "
    string2 +=  str(x)+" "
string2 += ") \n \t   (:init (robot_S_position R) (robot_A_position Loc1)) \n \t (:goal (or \n \t  "
string +=  ") \n \t (:predicates (robot_S_position ?x) (robot_A_position ?x) (P0TRAN) )"
string2 += "(robot_S_position faraway) (robot_A_position Loc0)(robot_A_position Loc"+str(k)+"))))"

#system move left precondition and effect
string +=  "\n \t (:action move_S_robot_left \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (not (P0TRAN)) (robot_S_position "+str(S_Left[i])+" )  (not (robot_S_position "+str(x)+" )) ))"

#system move right precondition and effect
string +=  ")\n) \n \t (:action move_S_robot_right \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (not (P0TRAN)) (robot_S_position "+str(S_Right[i])+" )  (not (robot_S_position "+str(x)+" )) ))"

#system move up precondition and effect
string +=  ")\n) \n \t (:action move_S_robot_Up \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (not (P0TRAN)) (robot_S_position "+str(S_Up[i])+" )  (not (robot_S_position "+str(x)+" )) ))"

#system move down precondition and effect
string +=  ")\n) \n \t (:action move_S_robot_Down \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (not (P0TRAN)) (robot_S_position "+str(S_Down[i])+" )  (not (robot_S_position "+str(x)+" )) ))"


#system stay precondition and effect
string +=  ")\n) \t (:action move_S_robot_stay \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (not (P0TRAN)) (robot_S_position "+str(x)+" ) ))"

#Environment move right precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_right \n \t :parameters () \n \t :precondition (and (not (P0TRAN)) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
for x in stateslistA:
  string +=  "(robot_A_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (P0TRAN) (robot_S_position "+str(E_Right[i])+" )  (not (robot_S_position "+str(x)+" )) ))"
for i,x in enumerate(stateslistA):
  if x != stateslistA[-1]:
    string +=  "\t \t (when (robot_A_position "+str(x)+") (and (P0TRAN)(robot_A_position "+str(S_RightA[i])+" )  (not (robot_A_position "+str(x)+" )) ))"

#Environment stay precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_stay \n \t :parameters () \n \t :precondition (and (not (P0TRAN)) \n \t \t (or"
for x in stateslist[:6]:
  string +=  "(robot_S_position "+str(x)+")"
for x in stateslistA:
  string +=  "(robot_A_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist[:6]):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (P0TRAN) (robot_S_position "+str(x)+" ) ))"
for i,x in enumerate(stateslistA):
  string +=  "\t \t (when (robot_A_position "+str(x)+") (and (P0TRAN) (robot_A_position "+str(x)+" ) ))"

#Environment move left precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_left \n \t :parameters () \n \t :precondition (and (not (P0TRAN)) \n \t \t (or"
for x in stateslist[:6]:
  string +=  "(robot_S_position "+str(x)+")"
for x in stateslistA:
  string +=  "(robot_A_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist[:6]):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (P0TRAN) (robot_S_position "+str(E_Left[i])+" )  (not (robot_S_position "+str(x)+" )) ))"
for i,x in enumerate(stateslistA):
  if x != 'Loc1':
    string +=  "\t \t (when (robot_A_position "+str(x)+") (and (P0TRAN) (robot_A_position "+str(S_LeftA[i])+" )  (not (robot_A_position "+str(x)+" )) ))"


#Environment move up precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_Up \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (not (P0TRAN)) (robot_S_position "+str(E_Up[i])+" )  (not (robot_S_position "+str(x)+" )) ))"

#Environment move down precondition and effect
string +=  ")\n) \n \t (:action move_E_robot_Down \n \t :parameters () \n \t :precondition (and (P0TRAN) \n \t \t (or"
for x in stateslist:
  string +=  "(robot_S_position "+str(x)+")"
string +=  "))\n \t :effect (and"
for i,x in enumerate(stateslist):
  string +=  "\t \t (when (robot_S_position "+str(x)+") (and (not (P0TRAN)) (robot_S_position "+str(E_Down[i])+" )  (not (robot_S_position "+str(x)+" )) ))"

string +=  ")))"
f = open('gws_1d_alt.pddl', 'w')
f.write(string)
f.close

f = open('gws_1d_alt_pb.pddl', 'w')
f.write(string2)
f.close

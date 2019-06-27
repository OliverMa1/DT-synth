k = 4
X = k
Y = k
for i in range(0,(X*Y)):
	print "(and (not(P0TRAN)) (robot_E_position e"+str(i)+") (not(robot_S_position s"+str(i)+")))\n"

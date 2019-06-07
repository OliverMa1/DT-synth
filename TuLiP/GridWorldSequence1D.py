from tulip import transys, spec, synth
import time

for q in [5,10,35,80]:

	k = q
	stateslista = []
	stateslistb = []
	atomiclist = []
	atomiclistb = []
	sys = transys.FTS()
	env1 = transys.FTS()
	env1.owner = 'env'
	speclist = []
	for x in range(0,k+1):
	    atomiclistb.append('b'+str(x))
	for x in range(0,k+1):
	    atomiclist.append('a'+str(x))
	for x in range(0,k+1):
	    stateslista.append('s'+str(x))
	for x in range(0,k+1):
	    stateslistb.append('e'+str(x))

	sys.states.add_from(stateslista)
	env1.states.add_from(stateslistb)
	sys.states.initial.add('s1')
	env1.states.initial.add('e1')

	atomicset = set(atomiclist)
	atomicsetb = set(atomiclistb)
	sys.atomic_propositions.add_from(atomicset)
	env1.atomic_propositions.add_from(atomicsetb)
	for x in range(1,k):
	    sys.transitions.add_comb({'s'+str(x)},{'s'+str(x-1),'s'+str(x),'s'+str(x+1)})
	    env1.transitions.add_comb({'e'+str(x)},{'e'+str(x-1),'e'+str(x),'e'+str(x+1)})

	sys.transitions.add_comb({'s0'}, {'s1','s0'})

	sys.transitions.add_comb({'s'+str(k)}, {'s'+str(k),'s'+str(k-1)})
	env1.transitions.add_comb({'e0'}, {'e1','e0'})

	env1.transitions.add_comb({'e'+str(k)}, {'e'+str(k),'e'+str(k-1)})
	specstring = ''
	for x in range(0,k+1):
	    sys.states.add('s'+str(x), ap={'a'+str(x)})
	    env1.states.add('e'+str(x), ap={'b'+str(x)})

	for x in range(0,k):
	    specstring += '(a'+str(x)+' && b'+str(x)+') || '

	specstring += '(a'+str(k)+' && b'+str(k)+')'
	speclist.append(specstring)

	specset = set(speclist)
	print(specset)
	env_vars = set()

	env_init = set()

	env_prog = set()

	env_safe = set()

	sys_vars = set()

	sys_init = set()

	sys_prog = set()

	sys_safe = specset

	specs = spec.GRSpec(env_vars, sys_vars, env_init, sys_init,
		                  env_safe, sys_safe, env_prog, sys_prog)
	specs.moore = False
	specs.plus_one = False
	specs.qinit = '\A \E'
	start = time.clock()
	ctrl = synth.synthesize(specs, sys=sys,env = env1, solver='gr1c')

	if not ctrl.save('discerete'+str(q)+'.png'):
		print(ctrl)

	finish = time.clock()

	with open("Output.txt", "a") as text_file:
	    text_file.write("{0}: {1}  Size: {2}\n".format(q,finish - start,ctrl.size()))

	#ignore_env_init= True env = env1

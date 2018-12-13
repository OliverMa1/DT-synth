.PHONY: all  dt-synth z3 clean

all:  z3 dt-synth 

dt-synth:
	make ./DT-synth/

z3:
	cd ./z3-z3-4.8.1; \
	python scripts/mk_make.py
	make ./z3-z3-4.8.1/build
	find ./z3-z3-4.8.1/build -name '*.o' | xargs ar rs ./z3-z3-4.8.1/build/z3.a
	
	
clean:

	make -C ./DT-synth clean
	rm -rf ./z3-z3-4.8.1/build/

C++ = g++
CFLAGS =-I. -std=c++11 -I ../z3-z3-4.8.1/src/api/c++ -I ../z3-z3-4.8.1/src/api/ -L ../z3-z3-4.8.1/build/ -lz3
DEPS = teacher.h
OBJ = main.o
ODIR=.
%.o: %.cpp $(DEPS)
	$(C++) -c -o $@ $< $(CFLAGS)
main: $(OBJ)
	$(C++) -o $@ $^ $(CFLAGS)

.PHONY: clean

clean:
	rm -f $(ODIR)/*.o *~ core $(INCDIR)/*~ 

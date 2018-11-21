C++ = g++
CFLAGS =-I. -std=c++11 -I ./lib/src/api/c++ -I ./lib/src/api/ -L ./lib/ -lz3
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

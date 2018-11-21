#include <iostream>
#include <fstream>
int main(int argc, char* argv[])
{
	std::ofstream myfile;
	myfile.open("data/benchmarks/square.json");
	int i = std::atoi(argv[1]);
	myfile << "{\"variables\":[\"x\",\"y\",\"z\"],\"variables_dash\":[\"x_dash\",\"y_dash\",\"z_dash\"],\"successors\":10,\"exprs\":[],\"smt2\":\"(assert(and (and(= x 0) (= y 0)) (= z 0)))(assert(and(and(and(<= x " << argv[1] << ")(<= -"<< argv[1]<<" x))(<= y "<<argv[1]<<"))(<= -"<< argv[1]<<" y)))(assert(= z 0))(assert(= z 1))(assert(and(or(or(= x x_dash)(= x (+ x_dash 1)))(= x (- x_dash 1)))(and(or(or(= y y_dash)(= y (+ y_dash 1)))(= y (- y_dash 1)))(= z (- 1 z_dash)))))\"}";
}

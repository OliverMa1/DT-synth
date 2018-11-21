#include <iostream>
#include <fstream>
int main(int argc, char* argv[])
{
	std::ofstream myfile;
	myfile.open("data/examples/output.json");
	int i = std::atoi(argv[1]);
	int j = i/2;
	myfile << "{\"variables\":[\"x\",\"y\"],\"variables_dash\":[\"x_dash\",\"y_dash\"],\"successors\":10,\"exprs\":[],\"smt2\":\"(assert (and(<= " << 0 <<" x)(and (<= x " << j <<") (= y 0))))(assert (and(<= x " << i-1 <<")(>= x 0)))(assert (= y 0))(assert (= y 1))(assert(or(or(and(<= x " << j << ")(and(= y 0)(and(= y (- 1 y_dash))(or(= x (+ x_dash 1))(= x x_dash)))))(and(<= " << j << " x)(and(= y 0)(and(= y (- 1 y_dash))(= x x_dash)))))(and(= y 1)(and(= y_dash (- 1 y))(or(= x x_dash)(= x (- x_dash 1)))))))\"}";
}

#include <iostream>
#include <fstream>
int main(int argc, char* argv[])
{
	std::ofstream myfile;
	myfile.open("data/examples/output.json");
 // untere gerade argv[1]
 //obere gerade argv[2]
 // untere schranke argv[4]
 // obere schranke argv[3]
	myfile << "{\"variables\":[\"x\",\"y\",\"z\"],\"variables_dash\":[\"x_dash\",\"y_dash\",\"z_dash\"],\"successors\":10,\"exprs\":[\"x+y\",\"x-y\"],\"smt2\":\"(assert (and(= z 0)(and (= x 0) (= y 0))))(assert (and(and(and(>= y (- x "<< argv[1]<<")) (<= y (+ x "<<argv[2]<<")))(<= y "<<argv[3]<<"))(<= -"<<argv[4]<<" y)))(assert (= z 0))(assert (= z 1))(assert(and(and(or(or(= x (+ x_dash 1)) (= x (- x_dash 1))) (= x x_dash))(or(or(= y (+ y_dash 1)) (= y (- y_dash 1))) (= y y_dash)))(= z (- 1 z_dash))))(assert(= 1 (+ x y)))(assert(= 0 (- x y)))\"}";
}

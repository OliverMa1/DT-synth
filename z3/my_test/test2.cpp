#include "z3++.h"
namespace z3{
	void my_test(){
		std::cout << "SMT2LIB Test" << std::endl;
		context c;
		solver s(c);
		//expr foo1 = c.parse_string("(assert(and a b))");
		//expr foo2 = c.parse_string("(declare-const p0 Bool)");
		expr foo = c.parse_file("test.smt2");
		//s.add(foo);
		std::cout << foo << std::endl;
	}
}
int main() {
	z3::my_test();
}

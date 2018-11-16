#include <iostream>
#include <sstream>
#include <vector>

#include "z3++.h"



int main()
{

	try
	{

	z3::context ctx;
	
	std::stringstream smt;
	std::stringstream smt1;
	smt << "(declare-fun f(Int Int) Int)" << std::endl;
	smt << "(declare-const y Int)" << std::endl;
	smt << "(declare-const x Int)" << std::endl;;
	smt << "(assert(=(f x y)(+ x y)))" << std::endl;;
	std::cout << smt.str() << std::endl;
	smt1 << "(declare-const a Int)" << std::endl;
	smt1  << "(assert (<= a 43))" << std::endl;;
	std::cout << smt1.str() << std::endl;
	std::vector<const char*> b;
	b.push_back("a");
	z3::expr a = ctx.int_const(b[0]);
	auto a_kl = a <= 41;
	// Manually define function declarations
	auto a_decl = ctx.function(ctx.str_symbol("a"), z3::sort_vector(ctx), ctx.int_sort());
	auto func_decl_v = z3::func_decl_vector(ctx);
	func_decl_v.push_back(a_decl);
	
	//auto v = ctx.parse_string(smt.str().c_str(), z3::sort_vector(ctx), func_decl_v);
	//std::cout << v.size() << std::endl;
	auto l = ctx.parse_string(smt.str().c_str());

	z3::solver s(ctx);
	//s.add(a_kl);
	/*for (unsigned i=0; i<v.size(); ++i)
	{
		std::cout << v[i] << std::endl;
		//s.add(v[i]);
	}*/
	for (unsigned i=0; i<l.size(); ++i)
	{
		std::cout << l[i] << std::endl;
		//s.add(l[i]);
	}
	
	std::cout << std::endl << s << std::endl;
	
	if (s.check())
	{
		
		auto m = s.get_model();
		std::cout << "SAT!" << std::endl << m << std::endl;
		
	}
	else
	{
		std::cout << "UNSAT" << std::endl;
	}
	
	
	}
	catch (const z3::exception & e)
	{
		std::cout << e.msg() << std::endl;
		//throw;
	}
	
	
}

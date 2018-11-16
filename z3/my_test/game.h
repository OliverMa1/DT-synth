#ifndef HEADERFILE_H
#define HEADERFILE_H
#include <iostream>
#include <vector>
#include <tuple>
#include <typeinfo>
#include "z3++.h"
/** Header for the game object.
 * @file game.h
 * 
 * The game object is used to encode a safety game. It stores initial vertices,
 * safe vertices, player0 vertices, player1 vertices, edges, variables and additional expressions 
 * for the learner.
 * @author Oliver Markgraf
 * @date August 14
 * */
class Game {

	 public:
	 /**
	  * Constructor for a game object, stores all the data in z3::expr
	  * and z3::expr_vector. Ensures that correct amount of variables are given.
	  * @param ctx - context to create the variables and make sure they all have the same context
	  * @param var_char - variables names, stored in a string vector
	  * @param var_dash_char - variables names for the next step, stored in a string vector
	  * @param exprs_var_char - expression variables names, stored in a string vector for the learner
	  * @param smt2lib - string that encodes the game with assertions
	  * @param n - number of maximal successors of each node
	  * */ 
	 Game(z3::context & ctx, const std::vector<std::string> & var_char,const std::vector<std::string> &var_dash_char,const std::vector<std::string> &exprs_var_char,const std::string & smt2lib, int n):  successors(n)
	 {
		if (var_char.size() != var_dash_char.size())
		{
			 throw std::runtime_error("Var Size != Var_Dash Size");
		}
		base = *(new z3::expr_vector(ctx));
		variables_vector = *(new z3::expr_vector(ctx));
		variables_dash_vector = *(new z3::expr_vector(ctx));
		all_variables_vector = *(new z3::expr_vector(ctx));
		exprs_var = *(new z3::expr_vector(ctx));
		exprs = *(new z3::expr_vector(ctx));
		auto func_decl_v = z3::func_decl_vector(ctx);
		for (int i = 0; (unsigned)i < var_char.size(); i ++)
		{
			z3::expr x = ctx.int_const(var_char[i].c_str());
			variables_vector.push_back(x);
			all_variables_vector.push_back(x);
			variables.insert(std::make_pair(var_char[i],x));
			auto a_decl = ctx.function(ctx.str_symbol(var_char[i].c_str()), z3::sort_vector(ctx), ctx.int_sort());
			func_decl_v.push_back(a_decl);
			attributes.push_back(var_char[i]);
		}
		for (int i = 0; (unsigned)i < var_dash_char.size(); i ++)
		{
			z3::expr x = ctx.int_const(var_dash_char[i].c_str());
			variables_dash_vector.push_back(x);
			all_variables_vector.push_back(x);
			auto a_decl = ctx.function(ctx.str_symbol(var_dash_char[i].c_str()), z3::sort_vector(ctx), ctx.int_sort());
			func_decl_v.push_back(a_decl);
		}
		for (int i = 0; (unsigned)i < exprs_var_char.size(); i ++)
		{
			z3::expr x = ctx.int_const(exprs_var_char[i].c_str());
			exprs_var.push_back(x);
			attributes.push_back(exprs_var_char[i]);
		}
		base = ctx.parse_string(smt2lib.c_str(), z3::sort_vector(ctx), func_decl_v);		
		for (int i = 5; (unsigned) i < base.size(); i++)
		{
			if(!base[i].is_eq()){
				throw std::runtime_error("Wrong format for additional expr! Is not an equality");
			}
			const auto& left = base[i].arg(0);
			const auto& right = base[i].arg(1);
			if (!left.is_numeral())
			{
				throw std::runtime_error("Wrong format for additional expr! Left side is not a number");
			}
			if (!right.is_int())
			{
				throw std::runtime_error("Wrong format for additional expr! Right side is not an integer expression");
			}
			exprs.push_back(right);
			expr_map.insert(std::make_pair(exprs_var_char[i-5],right));
		}
		if (exprs_var.size() != exprs.size())
		{
			throw std::runtime_error("Unequal additonal Expr and Expr names!");
		}
	 }

	 z3::expr get_initial_vertices()
	 {
		 return base[0];
	 }
	 z3::expr get_safe_vertices()
	 {
		 return base[1];
	 }
	 z3::expr get_player0_vertices()
	 {
		 return base[2];
	 }
	 z3::expr get_player1_vertices()
	 {
		 return base[3];
	 }	 
	 z3::expr get_edges()
	 {
		 return base[4];
	 }	 
	 z3::expr_vector get_variables_vector()
	 {
		 return variables_vector;
	 }	 
	 z3::expr_vector get_variables_dash_vector()
	 {
		 return variables_dash_vector;
	 }	 
	 z3::expr_vector get_all_variables_vector()
	 {
		 return all_variables_vector;
	 }
	 z3::expr_vector get_exprs_var()
	 {
		 return exprs_var;
	 }
	 z3::expr_vector get_exprs()
	 {
		 return exprs;
	 }
	 std::map<std::string, z3::expr> get_variables()
	 {
		 return variables;
	 }
	 std::map<std::string, z3::expr> get_expr_map()
	 {
		 return expr_map;
	 }
	 int get_successors()
	 {
		 return successors;
	 }
	 std::vector<std::string> & get_attributes()
	 {
		 return attributes;
	 }
	 private:
		z3::context ctx1;
		z3::expr_vector base = *(new z3::expr_vector(ctx1));
		z3::expr_vector variables_vector = *(new z3::expr_vector(ctx1));
		z3::expr_vector variables_dash_vector = *(new z3::expr_vector(ctx1));
		z3::expr_vector all_variables_vector = *(new z3::expr_vector(ctx1));
		z3::expr_vector exprs_var = *(new z3::expr_vector(ctx1));
		z3::expr_vector exprs = *(new z3::expr_vector(ctx1));
		std::map<std::string, z3::expr> variables;
		std::map<std::string, z3::expr> expr_map;
		int successors;
		std::vector<std::string> attributes;
};

#endif

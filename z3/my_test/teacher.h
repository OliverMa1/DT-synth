#include <iostream>
#include <vector>
#include <tuple>
#include <typeinfo>
#include "z3++.h"
/** Header for the teacher.
 * @file teacher.h
 * 
 * This file has methods to implement a teacher such as check conditions that 
 * are required to hold for a winning set.
 * @author Oliver Markgraf
 * @date August 14
 * */
/** Checks the initial condition for a hypothesis of a safety game.
 * I ⊆ W
 * @param hypothesis - hypothesis about the winning set W
 * @param initial_vertices - initial vertices encodeded as z3::expr
 * @param context - context to evaluate the expressions
 * @param variables - variables to get the value of each variable assigned
 * @return The counterexample, encoded as a int vector.
 * */
std::vector<int> check_initial_condition(const z3::expr & hypothesis, const z3::expr & initial_vertices, z3::context & context,const z3::expr_vector & variables)
{

	std::vector<int> result;
	auto solver = z3::solver(context);
	z3::expr check = implies(initial_vertices,hypothesis);
	solver.add(!check);
	if (solver.check() == z3::unsat) {
	}
	else {
		auto m = solver.get_model();
		z3::expr_vector sol(context);
		for (unsigned i = 0; i < m.size(); i++){
			z3::func_decl v = m[i];	
			sol.push_back(m.get_const_interp(v));
		}
		for(int i = 0; (unsigned)i < variables.size(); i++){
			int j = 0;
			Z3_get_numeral_int(context, m.eval(variables[i]), &j);
			result.push_back(j);
		}
	}
	return result;
}
/** Checks the initial condition for a hypothesis of a safety game.
 * W ⊆ F
 * @param hypothesis - hypothesis about the winning set W
 * @param safe_vertices - safe vertices encodeded as z3::expr
 * @param context - context to evaluate the expressions
 * @param variables - variables to get the value of each variable assigned
 * @return The counterexample, encoded as a int vector.
 * */
std::vector<int> check_safe_condition(const z3::expr & hypothesis, const z3::expr & safe_vertices, z3::context & context, const z3::expr_vector & variables)
{	

	std::vector<int> result;
	auto solver = z3::solver(context);
	z3::expr check = implies(hypothesis, safe_vertices);
	solver.add(!check);
	if (solver.check() == z3::unsat) {
	}
	else {
		auto m = solver.get_model();
		z3::expr_vector sol(context);
		for (unsigned i = 0; i < m.size(); i++){
			z3::func_decl v = m[i];	
			sol.push_back(m.get_const_interp(v));
			
		}
		for(int i = 0; (unsigned)i < variables.size(); i++){
			int j = 0;
			Z3_get_numeral_int(context, m.eval(variables[i]), &j);
			result.push_back(j);
		}

	}
	return result;
}
/** Method to build a transform a counterexample from z3::expr to a vector
 * of int vectors. Used for existential and universal counterexamples.
 * 
 * @param counterexample - counterexample of the hypothesis encoded as z3::expr
 * @param start - This is the counterexample node.
 * @param edges - edges of the game graph
 * @param context - context to evaluate the expressions
 * @param variables - variables to get the value of each variable assigned
 * @param variables_dash - variables in the next step 
 * @param all_variables - variables and variables_dash combined
 * @param n - maximum number of successors
 * @return Returns a counterexample as a horn clause. First vector is left side,
 * rest of the vectors are the right side.
 * */
std::vector<std::vector<int>> build_counterexample(const z3::expr & counterexample,const std::vector<int> & start, const z3::expr & edges, z3::context & context, const z3::expr_vector & variables, const z3::expr_vector & variables_dash, const z3::expr_vector & all_variables, const int n)
{

	std::vector<std::vector<int>> result;
	auto solver = z3::solver(context);
	solver.add(counterexample);
	result.push_back(start);
	for(int i = 0; i < n; i++){
		if (solver.check() == z3::sat){
			auto m = solver.get_model();
			z3::expr_vector sol(context);
			for (unsigned l = 0; (unsigned)l < m.size(); l++){
				z3::func_decl v = m[l];	
				sol.push_back(m.get_const_interp(v));			
			}
			z3::expr test = context.bool_val(true);
			for(int j = 0; (unsigned)j < variables_dash.size(); j++){
				test =  (test) && (variables_dash[j] == m.eval(variables_dash[j]));
			}
			std::vector<int> tmp;
			for(int k = 0; (unsigned)k < variables_dash.size(); k++){
				int j = 0;
				Z3_get_numeral_int(context, m.eval(variables_dash[k]), &j);
				tmp.push_back(j);
			}
			result.push_back(tmp);
			solver.add(!test);
		}
		else {
			break;
		}
	}
	return result;
}
/** Method to find a existential counterexample in a hypothesis.
 * 
 * @param hypothesis -  hypothesis of a winning set encoded as z3::expr
 * @param hypothesis_edge_nodes - possible successors of the hypothesis after one step
 * @param vertices - vertices of the game graph
 * @param vertices_dash - vertices encoded with the variables in the next step
 * @param vertices_player0 - vertices that are owned by player0
 * @param edges - edges of the game graph
 * @param context - context to evaluate the expressions
 * @param all_variables - variables and variables_dash combined
 * @param variables - variables to get the value of each variable assigned
 * @param variables_dash - variables in the next step 
 * @param n - maximum number of successors
 * @return Returns a counterexample as a horn clause. First vector is left side,
 * rest of the vectors are the right side.
 * */
std::vector<std::vector<int>> existential_check(const z3::expr & hypothesis, z3::expr & hypothesis_edge_nodes, 
const z3::expr & vertices, const z3::expr & vertices_dash, const z3::expr & vertices_player0, 
const z3::expr & edges, z3::context & context,const z3::expr_vector & all_variables,
 const z3::expr_vector & variables, const z3::expr_vector & variables_dash, const int & n)
{
	std::vector<std::vector<int>> result;
	auto solver = z3::solver(context);
	
	z3::expr nodes_in_V0_and_W_without_successor_in_W = hypothesis && vertices_player0 && !exists(variables_dash,vertices && edges && hypothesis_edge_nodes);

	solver.add(nodes_in_V0_and_W_without_successor_in_W);
	if (solver.check()== z3::sat){
			auto m = solver.get_model();
			z3::expr_vector sol(context);
			for (unsigned i = 0; i < m.size(); i++){
				z3::func_decl v = m[i];	
				sol.push_back(m.get_const_interp(v));
			}
			std::vector<int> tmp;
			z3::expr test = context.bool_val(true);
			for(int i = 0; (unsigned)i < variables.size(); i++){
				test =  (test) && (all_variables[i] == m.eval(all_variables[i]));
				int j = 0;
				Z3_get_numeral_int(context, m.eval(variables[i]), &j);
				tmp.push_back(j);
			}
			return build_counterexample(test && edges, tmp, edges, context, variables,variables_dash, all_variables, n);
	}	
	else {
		return result;
	}
}
/** Method to find a existential counterexample in a hypothesis.
 * 
 * @param hypothesis -  hypothesis of a winning set encoded as z3::expr
 * @param hypothesis_edge_nodes - possible successors of the hypothesis after one step
 * @param vertices - vertices of the game graph
 * @param vertices_dash - vertices encoded with the variables in the next step
 * @param vertices_player1 - vertices that are owned by player1
 * @param edges - edges of the game graph
 * @param context - context to evaluate the expressions
 * @param all_variables - variables and variables_dash combined
 * @param variables - variables to get the value of each variable assigned
 * @param variables_dash - variables in the next step 
 * @param n - maximum number of successors
 * @return Returns a counterexample as a horn clause. First vector is left side,
 * rest of the vectors are the right side.
 * */
std::vector<std::vector<int>> universal_check(const z3::expr & hypothesis, z3::expr & hypothesis_edge_nodes, 
const z3::expr & vertices, const z3::expr & vertices_dash, const z3::expr & vertices_player1, 
const z3::expr & edges, z3::context & context, const z3::expr_vector & all_variables,
 const z3::expr_vector & variables,const z3::expr_vector & variables_dash, const int n){
	
	std::vector<std::vector<int>> result;
	auto solver = z3::solver(context);

	z3::expr nodes_in_V1_and_W_without_successor_in_W =  vertices_player1 && hypothesis && exists(variables_dash,vertices && edges && !hypothesis_edge_nodes);
	
	solver.add(nodes_in_V1_and_W_without_successor_in_W);
	if (solver.check()== z3::sat){
			auto m = solver.get_model();
			z3::expr_vector sol(context);
			for (unsigned i = 0; i < m.size(); i++){
				z3::func_decl v = m[i];	
				sol.push_back(m.get_const_interp(v));			
				}
			z3::expr test = context.bool_val(true);
			std::vector<int> tmp;
			for(int i = 0; (unsigned)i < variables.size(); i++){
				test =  (test) && (variables[i] == m.eval(variables[i]));
				int j = 0;
				Z3_get_numeral_int(context, m.eval(variables[i]), &j);
				tmp.push_back(j);
			}

			return build_counterexample(test && edges, tmp, edges, context, variables,variables_dash, all_variables, n);
	}	
	else {
		return result;
	}
}





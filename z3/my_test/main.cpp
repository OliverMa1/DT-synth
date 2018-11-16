#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <set>
#include <map>
#include <iterator>
#include <typeinfo>
#include "z3++.h"
#include "parser.h"
#include "game.h"
#include "teacher.h"
#include <nlohmann/json.hpp>
#include <chrono>
using namespace std::chrono;
/** Main file for learner and teacher interaction. 
 * @file main.cpp
 * Implements the loop between learner and teacher 
 * and the communication between them.
 * @author Oliver Markgraf
 * @date August 14
 * */
using json = nlohmann::json;
/** @brief Counterexample struct to encode counterexamples.
 * 
 * This struct is used for a compact representation of counterexamples,
 * consisting of int vector that represents the data points of an
 * counterexample and a classification.
 * */
struct Counterexample
{
	std::vector<int> datapoints;
	// -1 = ?; 0 = false; 1 = true
	int classification;
	Counterexample(std::vector<int>  & dp, int c): datapoints(dp), classification(c){}
	friend bool operator==(const Counterexample& c1, const Counterexample& c2)
	{
		bool res;
		res = c1.datapoints == c2.datapoints;
		return res;
	}
	friend bool operator<(const Counterexample& c1, const Counterexample& c2)
	{
		return c1.datapoints < c2.datapoints;
	}
	friend std::ostream& operator<<(std::ostream & stream, const Counterexample & c)
	{
		for (int i = 0; (unsigned)i < c.datapoints.size()-1; i++)
		{
			stream << c.datapoints[i] << ", ";
		}
		stream << c.datapoints[c.datapoints.size()-1];
		if (c.classification == -1)
		{
			stream << ",?";
		}
		else if (c.classification == 0)
		{
			stream << ",false";
		}
		else
		{
			stream << ",true";
		}

		return stream;
	} 
};
	std::map<Counterexample,int> counterexample_map;
	std::map<int,Counterexample> position_map;
	std::vector<Counterexample> counterexample_vector;
	std::vector<std::vector<int>> horn_clauses;
	z3::context ctx;
	
/** Method to read a json file given by the learner.
 * 
 * Translates the json to a z3::expr.
 * @param variables - a map to assign variable names to z3::expr variables
 * @param exprs_map - a map to assign additional expression variables to z3::expr
 * @param level - the level the tree is at, used to print out the hypothesis
 * @param tree - string that is a output for the hypothesis
 * @return Hypothesis from the learner encoded as z3::expr
 * */
z3::expr read_json(json & j,std::map<std::string, z3::expr> & variables, std::map<std::string, z3::expr> & exprs_map, int level, std::string & tree)
{
	tree += "\n";
	for (int i = 0; i < level; i++)
	{
		tree += "  ";
	}
	if(j["attribute"] == "$func")
	{
		return read_json(j["children"][0],variables,exprs_map, level,tree);
	}
	else if (j["children"].is_null())
	{
		int i = j["classification"];
		if (i == 0)
		{
			tree += "true";
			return ctx.bool_val(true);
		}
		else
		{
			tree += "false";
			return ctx.bool_val(false);
		}
	}
	else
	{

		std::string varname = j["attribute"].get<std::string>();
		std::map<std::string, z3::expr>::iterator it;
		it = variables.find(varname);
		if (it == variables.end())
		{
				z3::expr x = (it->second);	
				it = exprs_map.find(varname);
				if (it == exprs_map.end()){							
					std::cout << varname << " nicht gefunden" << std::endl;
					throw std::runtime_error("Varname nicht gefunden");	
				}				
				tree += (varname + " <= " + std::to_string(j["cut"].get<int>()));
				z3::expr expr_x = (it -> second);
				z3::expr left = read_json(j["children"][0],variables,exprs_map,level+1,tree);
				z3::expr right = read_json(j["children"][1],variables,exprs_map,level+1,tree);
				z3::expr c = (expr_x <= ctx.int_val(j["cut"].get<int>()));
				z3::expr b = ite(c,left,right);
				return b;
			
		}
		else
		{
			tree += (varname + " <= " + std::to_string(j["cut"].get<int>()));
			z3::expr x = (it->second);	
			z3::expr left = read_json(j["children"][0],variables,exprs_map, level+1,tree);
			z3::expr right = read_json(j["children"][1],variables,exprs_map, level+1,tree);
			z3::expr c = x <= ctx.int_val(j["cut"].get<int>());
			z3::expr b = ite(c,left,right);
			return b;
		}
		
	}
	
}

/** Method to write attributes in a file for the learner.
 * @param attribute - given attributes to write
 */
void prep(std::vector<std::string> & attributes)
{
	std::ofstream myfile;
	myfile.open("data/dillig12.bpl.attributes");
	myfile << "cat,$func,1\n";
	for (int j = 0; (unsigned)j < attributes.size(); j++){
		std::string s = attributes[j];
		char const *pchar = s.c_str();
		myfile << "int," << pchar << "\n";
		z3::expr x = ctx.int_const(pchar);
	}
	myfile.close();

}
/** Method to write counterexamples for the learner to a file.
 */
void write()
{
	std::ofstream myfile;
	myfile.open("data/dillig12.bpl.data");
	for (int i = 0; (unsigned)i < counterexample_vector.size()-1;i++)
	{
		myfile << 0 << "," << counterexample_vector[i] << "\n";
	}
	myfile << 0 << "," << counterexample_vector[counterexample_vector.size()-1];
	myfile.close();	
	myfile.open("data/dillig12.bpl.horn");
	for (int i = 0; (unsigned)i < horn_clauses.size(); i ++)
	{
		myfile << horn_clauses[i][0];
		for (int j = 1; (unsigned)j < horn_clauses[i].size(); j++)
		{
			myfile << ", " << horn_clauses[i][j];
		}
		myfile << "\n";
	}
}
/** Method to store horn clauses in a global field.
 */ 
void store_horn(std::vector<int> horn)
{
	horn_clauses.push_back(horn);
}
/** Method to evaluate the additional expressions give by the user.
 * Adds the evaluation to the counterexample vector.
 * @param ce - the counterexample vector, used to extend the counterexample
 * @param variables_vector - variables, used to assign values to the counterexample
 * @param exprs - vector of the additional expressions
 * @param expr_vars - variables used in the additional expressions
 */
void eval_exprs(std::vector<int> & ce, const z3::expr_vector & variables_vector, const z3::expr_vector & exprs, const z3::expr_vector & expr_vars)
{
	for (int i = 0; (unsigned)i < expr_vars.size(); i++)
	{
			z3::expr a = exprs[i];
			z3::solver s(ctx);
			for (int j = 0; (unsigned) j < variables_vector.size(); j++)
			{
				s.add(variables_vector[j] == ce[j]);
			}
			if (s.check())
			{
				auto m = s.get_model();
				int b;
				Z3_get_numeral_int(ctx, m.eval(exprs[i]), &b);
				ce.push_back(b);

			}
			else
			{
				throw std::runtime_error("expr can't be evaluated");	
			}
	}
}
/** Method to store counterexamples in a global field.
 * @param ce - counterexample given as a Counterexample object
 * @param variables_vector - passed on to evaluate the additional expressions
 * @param exprs - passed on to evaluate the additional expressions
 * @param expr_vars - passed on to evaluate the additional expressions
 * @return returns the position in the field, where the counterexample was written, -1 if 
 * the method was unsuccessful
 */
int store(Counterexample  ce, const z3::expr_vector & variables_vector, const z3::expr_vector & exprs, const z3::expr_vector & expr_vars)
{

	eval_exprs(ce.datapoints, variables_vector, exprs, expr_vars);
	int position = -1;
	std::map<Counterexample, int>::iterator it = counterexample_map.find(ce);
	if (it != counterexample_map.end())
	{
		position = it -> second;
		std::map<int,Counterexample>::iterator it_pos = position_map.find(position);
		Counterexample ce_found = it_pos -> second;
		if (ce_found.classification == -1 && ce_found.classification < ce.classification)
		{
			position_map.at(position) = ce;
			counterexample_map.erase(ce);
			counterexample_map.insert(std::make_pair(ce,position));
			counterexample_vector[position] = ce;
		}
		else if (ce.classification == -1)
		{
		}
		else {
			std::cout << "Tried to add: " << ce << " Found: " << ce_found << std::endl;
			throw std::runtime_error("Inserted counterexample twice!");
			}		
	}
	else 
	{
		position = counterexample_map.size();
		counterexample_map.insert(std::make_pair(ce, position));
		position_map.insert(std::make_pair(position_map.size(), ce));
		counterexample_vector.push_back(ce);
	}
	return position;
}

/** Method to create and store a counterexample for the initial condition.
 * Sets classification to 0.
 * @param ce - data points of the counterexample, encoded as int vector
 * @param variables_vector - variables as z3::expr
 * @param exprs - additional expressions for the learner
 * @param expr_vars - additional expression variables for the learner
 * @return returns the position of the stored counterexample, -1 if it was unsucessful
 */
int create_and_store_initial_counterexample(std::vector<int> & ce, const z3::expr_vector & variables_vector, const z3::expr_vector & exprs, const z3::expr_vector & expr_vars)
{	
	return store(Counterexample(ce,0),variables_vector, exprs, expr_vars);
}
/** Method to create and store a counterexample for the safe condition.
 * Sets classification to 1.
 * @param ce - data points of the counterexample, encoded as int vector
 * @param variables_vector - variables as z3::expr
 * @param exprs - additional expressions for the learner
 * @param expr_vars - additional expression variables for the learner
 * @return returns the position of the stored counterexample, -1 if it was unsucessful
 */
int create_and_store_safe_counterexample(std::vector<int> & ce, const z3::expr_vector & variables_vector, const z3::expr_vector & exprs, const z3::expr_vector & expr_vars)
{
	return store(Counterexample(ce,1),variables_vector, exprs, expr_vars);
}
/** Method to create and store a counterexample for the existential or universal condition.
 * Sets classification to -1.
 * @param ce - data points of the counterexample, encoded as int vector
 * @param variables_vector - variables as z3::expr
 * @param exprs - additional expressions for the learner
 * @param expr_vars - additional expression variables for the learner
 * @return returns the position of the stored counterexample, -1 if it was unsucessful
 */
int create_and_store_unclassified_counterexample(std::vector<int> & ce, const z3::expr_vector & variables_vector, const z3::expr_vector & exprs, const z3::expr_vector & expr_vars)
{
	return store(Counterexample(ce,-1),variables_vector, exprs, expr_vars);
}
/** Method to create and store a counterexamples for the existential condition.
 * calls @see create_and_store_unclassified_counterexample, to create an unclassified
 * counterexample for each node and its successors.
 * @param ce - data points of the counterexample with its successors, encoded as vector of int vectors
 * @param variables_vector - variables as z3::expr
 * @param exprs - additional expressions for the learner
 * @param expr_vars - additional expression variables for the learner
 * @return returns true if the method was successful, false if it was unsucessful
 */
bool create_and_store_existential_counterexample(std::vector<std::vector<int>> & ce, const z3::expr_vector & variables_vector, const z3::expr_vector & exprs,const z3::expr_vector & expr_vars)
{
	std::vector<int> a;
	std::vector<int> positions;
	bool success = true;
	for (int i = ce.size()-1; i >= 0; i--)
	{
		int position = create_and_store_unclassified_counterexample(ce[i],variables_vector, exprs, expr_vars);
		if (position == -1){
			success = false;
		}
		positions.push_back(position);
	}
	if (positions.size() > 1){
		store_horn(positions);
	}
	else{
		store(Counterexample(ce[0],1),variables_vector, exprs, expr_vars);
	}
	return success;	
}
/** Method to create and store a counterexamples for the universal condition.
 * calls @see create_and_store_unclassified_counterexample, to create an unclassified
 * counterexample for each node and its successors.
 * @param ce - data points of the counterexample with its successors, encoded as vector of int vectors
 * @param variables_vector - variables as z3::expr
 * @param exprs - additional expressions for the learner
 * @param expr_vars - additional expression variables for the learner
 * @return returns true if the method was successful, false if it was unsucessful
 */
bool create_and_store_universal_counterexample(std::vector<std::vector<int>>  & ce, 
const z3::expr_vector & variables_vector,const z3::expr_vector & exprs,const z3::expr_vector & expr_vars)
{
	bool success = true;
	int a = create_and_store_unclassified_counterexample(ce[0],variables_vector, exprs, expr_vars);
	for (int i = 1; (unsigned)i < ce.size(); i++)
	{
		std::vector<int> positions;
		int position = create_and_store_unclassified_counterexample(ce[i],variables_vector, exprs, expr_vars);
		if (position == -1){
			success = false;
		}
		positions.push_back(position);
		positions.push_back(a);
		store_horn(positions);
	}
	return success;		
}
/** Method that calls the initial condition check in teacher.h
 * @param hypothesis - hypothesis about the winning condition
 * @param initial_vertices - initial vertices of the game graph
 * @param context - context to evaluate variables
 * @param variables - z3::expr_vector of the variables used
 * @param exprs - z3::expr_vector of the additional expressions
 * @param expr_vars - variables of the additional expressions
 * @return true if counterexample was found, false if no counterexample was found
 */
bool initial_check(const z3::expr & hypothesis, const z3::expr & initial_vertices, z3::context & context,
 const z3::expr_vector & variables,const z3::expr_vector & exprs, const z3::expr_vector & expr_vars)
{
	std::vector<int> test1;
	bool flag = false;
	test1 = check_initial_condition(hypothesis, initial_vertices, context, variables);
		if (test1.size() == 0){
		}
		else {
			flag = true;
			create_and_store_initial_counterexample(test1,variables, exprs, expr_vars);
		}
		return flag;
}
/** Method that calls the safe condition check in teacher.h
 * @param hypothesis - hypothesis about the winning condition
 * @param safe_vertices - safe vertices of the game graph
 * @param context - context to evaluate variables
 * @param variables - z3::expr_vector of the variables used
 * @param exprs - z3::expr_vector of the additional expressions
 * @param expr_vars - variables of the additional expressions
 * @return true if counterexample was found, false if no counterexample was found
 */
bool safe_check(const z3::expr & hypothesis, const z3::expr & safe_vertices, 
z3::context & context,const z3::expr_vector & variables,const z3::expr_vector & exprs,const z3::expr_vector & expr_vars)

{
	bool flag = false;
	std::vector<int> test2;
	test2 = check_safe_condition(hypothesis,safe_vertices,context,variables);
		
		if (test2.size() == 0){
		}
		else {
			flag = true;
			create_and_store_safe_counterexample(test2,variables, exprs, expr_vars); 
		}
		return flag;
}
/** Method that calls the existential condition check in teacher.h
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
 * @param exprs - z3::expr_vector of the additional expressions
 * @param expr_vars - variables of the additional expressions
 * @return true if counterexample was found, false if no counterexample was found
 */
bool ex_check(const z3::expr & hypothesis, z3::expr & hypothesis_edge_nodes, 
const z3::expr & vertices, const z3::expr & vertices_dash, const z3::expr & vertices_player0, 
const z3::expr & edges, z3::context & context,const z3::expr_vector & all_variables,
 const z3::expr_vector & variables, const z3::expr_vector & variables_dash, const int & n,const z3::expr_vector & exprs
 ,const z3::expr_vector & expr_vars)
 {
		bool flag = false;
	 	std::vector<std::vector<int>> new_test1;
		new_test1 = existential_check(hypothesis, hypothesis_edge_nodes, vertices, vertices_dash,vertices_player0, edges, context, all_variables, variables, variables_dash, n);
	
		if (new_test1.size() == 0){
		}
		else {
			flag = true;
			create_and_store_existential_counterexample(new_test1,variables, exprs, expr_vars);
		}
		return flag;
}
/** Method that calls the universal condition check in teacher.h
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
 * @param exprs - z3::expr_vector of the additional expressions
 * @param expr_vars - variables of the additional expressions
 * @return true if counterexample was found, false if no counterexample was found
 */
bool uni_check(const z3::expr & hypothesis, z3::expr & hypothesis_edge_nodes, 
const z3::expr & vertices, const z3::expr & vertices_dash, const z3::expr & vertices_player1, 
const z3::expr & edges, z3::context & context, const z3::expr_vector & all_variables,
 const z3::expr_vector & variables, const z3::expr_vector & variables_dash,
  const int n, const z3::expr_vector & exprs
 ,const z3::expr_vector & expr_vars)
{
	bool flag = false;
	 std::vector<std::vector<int>> new_test2;
	 new_test2 = universal_check(hypothesis, hypothesis_edge_nodes, vertices, vertices_dash,vertices_player1, edges, context, all_variables, variables, variables_dash, n);
		if (new_test2.size() == 0){
			
		}
		else {
			flag = true;
			create_and_store_universal_counterexample(new_test2,variables, exprs, expr_vars);	
		}
		return flag;
}

/** Method to generate overall stats of the safety game learning process.
 * @param result - string to pass on the result and print it out later
 * @param game - game to access stats
 * @param stats - time statistics
 */
 void generate_stats(std::string & result, Game* & game, int steps, std::vector<double> & stats)
 {
	 result += "Amount of steps needed: " + std::to_string(steps);
	 result += "\n";
	 result += "Amount of variables used: " + std::to_string(game->get_variables_vector().size());
	 result += "\n";
	 result += "Amount of additional expressions used: " + std::to_string(game->get_exprs_var().size());
	 result += "\n";
	 result += "Amount of counterexamples: " + std::to_string(counterexample_vector.size());
	 result += "\n";
	 result += "Amount of horn constraints: " + std::to_string(horn_clauses.size());
	 double avg = 0;
	 for (int i = 0; (unsigned) i < horn_clauses.size(); i++)
	 {
		 avg += horn_clauses[i].size();
	 }
	 avg = avg / horn_clauses.size();
	 result += "\n";
	 result += "Average length of horn constraints: " + std::to_string(avg);
	 result += "\n";
	 result += "Average teacher computation time: " + std::to_string(stats[0]/steps) + " milliseconds";
	 result += "\n";
	 result += "Lowest teacher computation time: " + std::to_string(stats[1]) + " milliseconds";
	 result += "\n";
	 result += "Highest teacher computation time: " + std::to_string(stats[2]) + " milliseconds";	 
	 result += "\n";
	 result += "Average learner computation time: " + std::to_string(stats[3]/steps) + " milliseconds";
	 result += "\n";
	 result += "Lowest learner computation time: " + std::to_string(stats[4]) + " milliseconds";
	 result += "\n";
	 result += "Highest learner computation time: " + std::to_string(stats[5]) + " milliseconds";
	 result += "\n";
	 result += "Total computation time: " + std::to_string(stats[6]) + " milliseconds";
	 result += "\n";
	 result += "Positive counterexamples found: " +std::to_string(stats[7]);
	 result += "\n";
	 result += "Negative counterexamples found: " +std::to_string(stats[8]);
	 result += "\n";
	 result += "Existential counterexamples found: " +std::to_string(stats[9]);
	 result += "\n";
	 result += "Universal counterexamples found: "+ std::to_string(stats[10]);
	 result += "\n";	 
 }
/** Main method that encodes the interaction between teacher and learner.
 * @param argc - number of inputs, should be 2
 * @param argv - argv[1], should be the path to the input file
 */
int main(int argc, char* argv[])
{
	high_resolution_clock::time_point start = high_resolution_clock::now();
	std::vector<double> stats(6,-1);
	z3::expr initial_vertices = ctx.int_val(4);
	z3::expr safe_vertices = ctx.int_val(4);
	z3::expr vertices_player0 = ctx.int_val(4);
	z3::expr vertices_player1 = ctx.int_val(4);
	z3::expr edges = ctx.int_val(4);
	try{
		int pos = 0;
		int neg = 0;
		int ex = 0;
		int uni = 0;
		//std::ifstream ifs("data/zweigeraden/input.json");
		std::ifstream ifs(argv[1]);
		json j = json::parse(ifs);
		z3::expr_vector b(ctx);
		b.push_back(initial_vertices);
                Parser a;
                Game* game = a.parse_json(ctx,j);
		prep(game->get_attributes());
		z3::expr initial_vertices = game->get_initial_vertices();
		z3::expr safe_vertices = game->get_safe_vertices();
		z3::expr vertices_player0 = game->get_player0_vertices();
		z3::expr vertices_player1 = game->get_player1_vertices();
		z3::expr edges = game->get_edges();
		int n = game->get_successors();
		z3::expr_vector variables_vector = game->get_variables_vector();
		z3::expr_vector variables_dash_vector = game->get_variables_dash_vector();
		z3::expr_vector all_variables_vector = game->get_all_variables_vector();

		z3::expr_vector exprs = game->get_exprs();
		z3::expr_vector exprs_var = game->get_exprs_var();
		std::map<std::string, z3::expr> variables = game->get_variables();
		std::map<std::string, z3::expr> expr_map = game->get_expr_map();
		
		//prep_from_json(j,initial_vertices, safe_vertices, vertices_player0, vertices_player1, edges, n);
		try{
			auto vertices = vertices_player0 || vertices_player1;
			auto vertices_dash = vertices.substitute(variables_vector,variables_dash_vector);
			auto hypothesis = ctx.bool_val(true);
			z3::expr hypothesis_edges_test = hypothesis.substitute(variables_vector,variables_dash_vector);
			bool flag = true;
			int safety_counter = 0;
			while (flag)
			{
				flag = false;
				high_resolution_clock::time_point t1 = high_resolution_clock::now();
				flag = initial_check(hypothesis, initial_vertices, ctx, variables_vector, exprs, exprs_var);
				if (flag){
					pos++;
				}
				if (flag == false){
					flag = safe_check(hypothesis,safe_vertices,ctx,variables_vector, exprs, exprs_var);
					if (flag){
						neg++;
					}
				}
				if (flag == false){
					flag = ex_check(hypothesis, hypothesis_edges_test, vertices, vertices_dash,vertices_player0, edges, ctx, all_variables_vector, variables_vector, variables_dash_vector, n, exprs, exprs_var);
					if (flag){
						ex++;
					}
				}
				if (flag == false){
					flag = uni_check(hypothesis, hypothesis_edges_test, vertices, vertices_dash,vertices_player1, edges, ctx, all_variables_vector, variables_vector, variables_dash_vector, n, exprs, exprs_var);
					if (flag){
						uni++;
					}
				}
				high_resolution_clock::time_point t2 = high_resolution_clock::now();
				auto duration = duration_cast<milliseconds>(t2-t1).count();
				stats[0] += duration;
				if (stats[1] == -1 || stats[1] > duration)
				{
					stats[1] = duration;
				}
				if (stats[2] == -1 || stats[2] < duration)
				{
					stats[2] = duration;
				}
				std::cout << "Time taken for the teacher: " << duration << " milliseconds" << std::endl;			
				write();
				t1 = high_resolution_clock::now();
				system("learner/main data/dillig12.bpl");
				t2 = high_resolution_clock::now();
				duration = duration_cast<milliseconds>(t2-t1).count();	
				stats[3] += duration;
				if (stats[4] == -1 || stats[4] > duration)
				{
					stats[4] = duration;
				}
				if (stats[5] == -1 || stats[5] < duration)
				{
					stats[5] = duration;
				}			
				std::ifstream ifs("data/dillig12.bpl.json");
				json j = json::parse(ifs);
				std::string tree = "";
				hypothesis = read_json(j, variables, expr_map,0, tree);
				std::cout << "\n Hypothesis: \n" << tree << std::endl;
				std::cout << "Time taken for the learner: " << duration << " milliseconds " << std::endl;
				hypothesis_edges_test  = hypothesis.substitute(variables_vector,variables_dash_vector);
				safety_counter++;
				if (safety_counter >= 1500)
				{
					flag = false;
					std::cout << "Safety counter reached" << std::endl;
				}
			}
			high_resolution_clock::time_point end = high_resolution_clock::now();
			auto duration = duration_cast<milliseconds>(end-start).count();
			stats.push_back(duration);	
			stats.push_back(pos);
			stats.push_back(neg);
			stats.push_back(ex);
			stats.push_back(uni);
			std::string result;
			generate_stats(result, game, safety_counter,stats);
			std::cout << result;
		}
		catch(std::runtime_error e)
		{
			throw std::runtime_error(e.what());
			return EXIT_FAILURE;
		}

	}
		catch (const z3::exception & e)
	{
		throw std::runtime_error(e.msg());
		return EXIT_FAILURE;
	}
	
	return EXIT_SUCCESS;
}

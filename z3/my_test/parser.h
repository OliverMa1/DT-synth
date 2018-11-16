#include <iostream>
#include <vector>
#include <tuple>
#include <string>
#include <typeinfo>
#include "z3++.h"
#include "game.h"
#include <nlohmann/json.hpp>
using json = nlohmann::json;
/** Header for the parser object.
 * @file parserObject.h
 * 
 * Parser for creating game objects. Can parse json files and create a game object
 * @see parse_json. 
 * @author Oliver Markgraf
 * @date August 14
 * */
class Parser {
	/**	Default constructor
	 * */
	public :Parser()
	{
	}
	/** Method to create a Game object from a json file. 
	 * @param ctx - context to pass on for the game.
	 * @param j - json file to create a game
	 * 
	 * */
	Game* parse_json(z3::context & ctx, const json & j)
	{
		std::vector<std::string> variables_vector;
		std::vector<std::string> variables_dash_vector;
		std::vector<std::string> exprs_var;	
		auto smt2lib = j["smt2"].get<std::string>();
		int i = j["successors"];	
		for (int i = 0; (unsigned)i < j["variables"].size(); i ++)
		{
			variables_vector.push_back(j["variables"][i].get<std::string>());
		}
		for (int i = 0; (unsigned)i < j["variables_dash"].size(); i ++)
		{
			variables_dash_vector.push_back(j["variables_dash"][i].get<std::string>());
		}
		for (int i = 0; (unsigned)i < j["exprs"].size(); i ++)
		{
			exprs_var.push_back(j["exprs"][i].get<std::string>());
		}

		return new Game(ctx,variables_vector, variables_dash_vector, exprs_var, smt2lib, i);
	}
	
	private :
		Game* game;
	
};

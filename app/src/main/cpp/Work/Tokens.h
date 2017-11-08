#pragma once

#include <vector>
#include <functional>
#include <unordered_map>
#include <cmath>
#include <cstdlib>

/**
 * Functional tokens in input, their definitions and underling mathematical implementation
 */
namespace Tokens
{
	namespace Operators
	{
		struct S_Operator // Operator structure
		{
			enum associativity_tag // left or right associativity
			{
				E_left = 0,
				E_right = 1
			};
			size_t presedence; // operator presedence compared to others
			associativity_tag associativity; // left or right
			double(*function)(double, double); // a function that implements operator's behavior (pointer to it)

		};

		/*storage for defined operators*/
		const std::unordered_map<char, S_Operator> operators_container // adding new operators can be done here and just here
		{
			{ '+', { 1, S_Operator::E_left, [](auto operant1, auto operant2) {return operant1 + operant2; }} },
			{ '-', { 1, S_Operator::E_left, [](auto operant1, auto operant2) {return operant1 - operant2; }} },
			{ '*', { 2, S_Operator::E_left, [](auto operant1, auto operant2) {return operant1 * operant2; }} },
			{ '/', { 2, S_Operator::E_left, [](auto operant1, auto operant2) {return operant1 / operant2; }} },
			{ '^', { 3, S_Operator::E_right,[](auto operant1, auto operant2) {return pow(operant1 , operant2); }} }
		};

		bool isOperator(char _in); // prototype of a function to check is the _in char is a defined operator
		const S_Operator& getOperator(char _in); // prototype of a function to get the operator struct based on the input char
	}

	namespace Functions
	{
		using T_MathsFunction = double(*)(double);
		/*storage for defined functions*/
		const std::unordered_map<std::string ,T_MathsFunction> functions_container // define new functions here
		{
			{"unary_minus",[](auto argument) { return -argument; }},
			{"sqrt", [](auto argument) { return sqrt(argument); }},
			{"cbrt", [](auto argument) { return cbrt(argument); } },
			{"exp", [](auto argument) { return exp(argument); }},
			{"sin", [](auto argument) { return sin(argument); }},
			{"cos", [](auto argument) { return cos(argument); }},
			{"ln", [](auto argument) { return log(argument); }},
			{"log2", [](auto argument) { return log2(argument);}},
			{"log10",[](auto argument) { return log10(argument); }},
			{"abs", [](auto argument) { return abs(argument); }},
			{"tg", [](auto argument) { return tan(argument); }},
			{"ctg", [](auto argument) { return 1/tan(argument); }}

		};

		bool isFunction(const std::string& _in); // prototype of a function to check is the _in string is a defined function token
		const T_MathsFunction& getFunction(const std::string& _in); // prototype of a function to get the function struct based on the input string
	}
}

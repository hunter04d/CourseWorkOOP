#pragma once
#include <string>
#include <vector>
#include <functional>
#include <unordered_map>
#include <cmath>
#include <array>
#include <stdexcept>

#include <cstdlib>

/**
 * Functional tokens in input, their definitions and underling mathematical implementation
 */
namespace Tokens
{
	struct S_Operator // Operator structure
	{
		enum associativity_tag // left or right associativity
		{
			E_left = 0,
			E_right = 1
		};
		size_t precedence; // operator precedence compared to others
		associativity_tag associativity; // left or right
		std::function<bool (bool, bool)> function; // a function that implements operator's behavior (pointer to it)

	};

	class BooleanOperator
	{
		std::array<bool, 4> outcomes;
	public:
		BooleanOperator(std::string&& _in)
		{
			if(_in.size() != 4)
			{
				throw std::invalid_argument("_in");
			}
			for (int i = 0; i < 4; ++i)
			{
				if(_in[i] == '0' || _in[i] == '1')
				{
					outcomes[i] = _in[i] - '0';
				}
				else
				{
					throw std::invalid_argument("_in");
				}

			}
		}
		bool operator()(bool op1, bool op2)
		{
			if(op1 == false && op2 == false)
			{
				return outcomes[0];
			} else if (op1 == false && op2 == true)
			{
				return outcomes[1];
			} else if (op1 == true && op2 == false)
			{
				return outcomes[2];
			} else
			{
				return outcomes[3];
			}
		}
	};

	/*storage for defined operators*/
	const std::unordered_map<char, S_Operator> operators_container // adding new operators can be done here and just here
        {
                { '+', S_Operator{ 1, S_Operator::E_left, [](bool operant1, bool operant2) {return BooleanOperator{"0111"}(operant1, operant2); }} },
                { '*', S_Operator{ 2, S_Operator::E_left, [](bool operant1, bool operant2) {return BooleanOperator{"0001"}(operant1, operant2); }} },
                { '/', S_Operator{ 2, S_Operator::E_left, [](bool operant1, bool operant2) {return operant1 / operant2; }} },
                { '^', S_Operator{ 3, S_Operator::E_right,[](bool operant1, bool operant2) {return BooleanOperator{"0110"}(operant1, operant2); }} }
        };
	/**
* @brief isCharOperator - function to check is the _in char is a defined operator
* @param _in - input char
* @return  true if it is indeed an operator, else false
*/
	bool isCharOperator(const char &_in);


	/**
	* @brief getOperatorFromChar - function to get the operator struct based on the input char
	* @param _in - input char
	* @return a reference to a corresponding operator in the operator container
	*/
	const Tokens::S_Operator& getOperatorFromChar(const char& _in);

}

#include "Tokens.h"

/**
 * @brief isOperator - function to check is the _in char is a defined operator
 * @param _in - input char
 * @return  true if it is indeed an operator, else false
 */
bool Tokens::Operators::isOperator(char _in)
{
	return operators_container.count(_in) == 1;
}

/**
 * @brief getOperator - function to get the operator struct based on the input char
 * @param _in - input char
 * @return a reference to a corresponding operator in the operator container
 */
const Tokens::Operators::S_Operator& Tokens::Operators::getOperator(char _in)
{
	return operators_container.at(_in);
}
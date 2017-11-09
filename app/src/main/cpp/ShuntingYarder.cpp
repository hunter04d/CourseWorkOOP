#include "ShuntingYarder.h"
#include <sstream>
#include "Tokens.h"
#include "ParserException.h"
#include <ctype.h>

#include <algorithm>
#include <stack>
#include <VarTable.h>

/**
 * @brief preAnalize - function that converts an expression from mathematical to one that can be procced by shunting yard
 * it mosty just inserts * where it can implied
 * @param input_expr - string to preanalize, changes are made to this string
 */
void ShuntingYarder::preAnalize()
{
    for(size_t i = 0u; i < input_expr.size();++i)
    {
        char& curr_char = input_expr[i];
		if (curr_char == '(' && i != 0)// before a left brace if the last token was a operand or a right brace
        {
            if (isdigit(input_expr[i - 1]))
            {
                int reverse_i = 1;
                while (isdigit(input_expr[i - reverse_i]))
                {
                    if (i - reverse_i == 0)
                    {
                        input_expr.insert(input_expr.begin() + i, '*');
                        break;
                    }
                    ++reverse_i;
                }
                if (input_expr[i-reverse_i] == 'x'|| !isalpha(input_expr[i - reverse_i]))
                {
                    input_expr.insert(input_expr.begin() + i, '*');
                }
            }
			if(input_expr[i-1] == ')')//in between left and right brace
			{
				 input_expr.insert(input_expr.begin() + i, '*');
			}
		}
		if(curr_char == ')' && i <(input_expr.size()-1)) // after a closing right brace before an operand
		{
			if(!Tokens::isOperator(input_expr[i+1]))
			{
				input_expr.insert(input_expr.begin() + (i+1), '*');
			}
		}
        if((curr_char == '0' || curr_char == '1') && i != 0)
        {
            auto& prev_char = input_expr[i-1];
            if(prev_char == '0' || prev_char == '1')
            {
                input_expr.insert(input_expr.begin() + i, '*');
            }
        }
		if (isalpha(curr_char) && i != 0) // in between a function token and a operand
        {
            if (isdigit(input_expr[i - 1]))
            {
                input_expr.insert(input_expr.begin() + i, '*');
            }
        }
    }
}

/**
 * @brief parse - function to convert a function from infix to postfix notation
 * @param input_expr - input function in infix notation
 * @param _var_number - total number of functions to expect (max number of variables allowed)
 * @return function in posfix notation ready to be calculed
 */
ShuntingYarder ShuntingYarder::parse(size_t _var_number) //to RPN
{
    size_t pos(0);
    std::stack<std::string> stack;
	bool had_variable = false; // flag to check if a variable is present
	enum E_StateMachine // state machine to check the input correctness
	{
		expect_operator,
		expect_operand
	}state = expect_operand;
    std::string out;
    while (pos < input_expr.size())
    {
        char curr_char = input_expr[pos];
        //Variable
        auto var_pair = getVariableToken(input_expr, pos, _var_number);
        if (var_pair != -1)
        {
			if(state!= expect_operand)
			{
                throw ParserException(ParserException::ErrorCode::EXPECTED_OPERATOR, pos+1);
			}
			out += input_expr.substr(pos, var_pair) + ' ';
			// has_variable.at(std::stoi(input_expr.substr(pos+1,var_pair-1))-1) = true;
            pos += var_pair;
			had_variable = true;
			state = expect_operator;
            continue;
        }
        // Constant
        if (curr_char == '0' || curr_char == '1')
        {
			if(state!= expect_operand)
			{
				throw ParserException(ParserException::ErrorCode::EXPECTED_OPERATOR, pos+1);
			}
            out += (input_expr.substr(pos, 1) + ' ');
            ++pos;
			state = expect_operator;
            continue;
        }
        // Braces
        if (curr_char == '(')
        {
            stack.push("(");
            ++pos;
            continue;
        }
        if (curr_char == ')')
        {
			if(state != expect_operator)
			{
				throw ParserException(ParserException::ErrorCode::OPERATOR_BEFORE_RIGHT_BRACE, pos +1);
			}
            bool found_left_parenthesis = false;
            while (!(stack.empty() || (found_left_parenthesis = stack.top() == "(")))
            {
                out += stack.top() + ' ';
                stack.pop();
            }
            if (found_left_parenthesis == false) // if the stack is emptied and the left parenthesis was not found
            {
                throw ParserException(ParserException::ErrorCode::MISPATCHED_PARENTHESIS);
            }
			stack.pop(); // the left brace at the top
            ++pos;
			state = expect_operator;
            continue;
        }
        // Unary Minus
        if(curr_char == '-' && pos < input_expr.size()-1)
        {
            if((pos == 0 || input_expr[pos-1] == '(' || Tokens::isOperator(input_expr[pos-1])))
            {
                stack.push("unary_minus");
                ++pos;
                continue;
            }
        }
        // Operators
        if (Tokens::isOperator(curr_char))
        {
            if(pos == 0)
            {
                throw ParserException(ParserException::ErrorCode::OPERATOR_IN_FRONT, 0);
            }
			if(state != expect_operator)
            {
                throw ParserException(ParserException::ErrorCode::EXPECTED_OPERATOR, pos+1);
            }
            while (!stack.empty() && operatorCompare(curr_char, stack.top()))
            {
                out += stack.top() + ' ';
                stack.pop();
            }
            stack.push(std::string(1, curr_char));
            ++pos;
			state = expect_operand;
            continue;
        }
		throw ParserException(ParserException::ErrorCode::UNKNOWN, pos+1);
    }
	while (!stack.empty())
    {
        if (stack.top() == "(")
            throw ParserException(ParserException::ErrorCode::MISPATCHED_PARENTHESIS);
        out += stack.top() + ' ';
        stack.pop();
    }
	if(state == expect_operand)
    {
        throw ParserException(ParserException::ErrorCode::OPERATOR_AT_THE_END);
    }
	if(!had_variable)
    {
        throw ParserException(ParserException::ErrorCode::EVALUATES_TO_CONSTANT);
    }
    output_expr = std::move(out);
    return *this;
}

/**
 * @brief calculateFunc - function that takes a function in RPN and a point and get's function value at that point
 * @param output_expr - function in postfix notation
 * @param _arg_vals - point at which to calculate
 * @return a value of a function at that point
 */
bool ShuntingYarder::calculateFunc(const VarTable& _arg_vals)
{
    std::istringstream stream(output_expr);
    std::stack<bool > numbers;
    std::string token;
    int number_token;
	while (stream >> token) // read a token
    {
		if (token[0] == 'x') // if it is a variable push it's value on to the stack
        {
            numbers.push(_arg_vals.at(std::stoi(token.substr(1))-1));
        }
        else if (std::istringstream(token) >> number_token)
        {
            numbers.push(bool(number_token));
        }
        else
        {
            if (Tokens::isOperator(token[0]))
            {
                if(numbers.size()< 2)
                {
                    throw ParserException(ParserException::ErrorCode::UNKNOWN);
                }
                auto operant2(numbers.top());
                numbers.pop();
                auto operant1(numbers.top());
                numbers.pop();
                const Tokens::S_Operator& v_operator = Tokens::getOperator(token[0]);
                numbers.push(v_operator.function(operant1, operant2));
            } else if(token == "unary_minus")
            {
                if(numbers.size() < 1)
                {
                    throw ParserException(ParserException::ErrorCode::UNKNOWN);
                }
                auto operant = numbers.top();
                numbers.pop();
                numbers.push(!operant);
            }
        }
    }
    return numbers.top();
}

/**
 * @brief getVariableToken - function that checks if there is a spaning variable token at this position and if there is output it's length
 * @param _in - input string
 * @param _pos - position to check from
 * @param _var_number - maximum numbered variable to expect
 * @return -1 if not a variable, else - the length of a variable (>2 since each variable must be numbered)
 */
int ShuntingYarder::getVariableToken(const std::string& _in, size_t _pos,size_t _var_number)
{
    if (_in[_pos] == 'x')
    {
        int count = 1;
        for (size_t i = _pos + count; i < _in.size(); ++i)
        {
            if (isdigit(_in[i]))
            {
                ++count;
            }
            else break;
        }
        if (count == 1)
        {
            throw ParserException(ParserException::ErrorCode::INVALID_VARIABLE);
        }
        if (std::stoi(_in.substr(_pos + 1, count - 1)) > _var_number /*NUM_OF_FUNCTIONS*/)
        {
            throw  ParserException(ParserException::ErrorCode::INVALID_VARIABLE);
        }
        return count;
    }
    return -1;
}

/**
 * @brief operatorCompare - function that compares precedence of the current operator and the operator (or a left brace or a function) on top of the stack
 * @param _in_char - operator
 * @param _tops_of_stack - operator at the top of the stack
 * @return true if top of stack precedence is less(based on the associativity tag) and false otherwise
 */
bool ShuntingYarder::operatorCompare(char _in_char, const std::string& _tops_of_stack)
{
    const Tokens::S_Operator& curr_operator = Tokens::getOperator(_in_char);
	size_t top_of_stack_presedence;
	if (_tops_of_stack == "(") // brace is not an operator
    {
        top_of_stack_presedence = 0;
    }
	else if(_tops_of_stack == "unary_minus") //unary minus aplies only to one operand
    {
        top_of_stack_presedence = 1000;
    }
    else
    {
        top_of_stack_presedence = Tokens::getOperator(_tops_of_stack.at(0)).precedence;
    }
    return (curr_operator.associativity == Tokens::S_Operator::E_left && curr_operator.precedence <= top_of_stack_presedence) ||
           (curr_operator.associativity == Tokens::S_Operator::E_right && curr_operator.precedence < top_of_stack_presedence);
}

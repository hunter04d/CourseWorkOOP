#include "ShuntingYard.h"
#include <sstream>
#include "Tokens.h"
#include <ctype.h>
#include <string>
#include <algorithm>
#include <stack>

/**
 * @brief preAnalize - function that converts an expression from mathematical to one that can be procced by shunting yard
 * it mosty just inserts * where it can implied
 * @param _in - string to preanalize, changes are made to this string
 */
void ShuntingYard::preAnalize(std::string& _in)
{
    for(size_t i = 0u; i < _in.size();++i)
    {
        char& curr_char = _in[i];
		if (curr_char == '(' && i != 0)// before a left brace if the last token was a operand or a right brace
        {
            if (isdigit(_in[i - 1]))
            {
                int reverse_i = 1;
                while (isdigit(_in[i - reverse_i]))
                {
                    if (i - reverse_i == 0)
                    {
                        _in.insert(_in.begin() + i, '*');
                        break;
                    }
                    ++reverse_i;
                }
                if (_in[i-reverse_i] == 'x'|| !isalpha(_in[i - reverse_i]))
                {
                    _in.insert(_in.begin() + i, '*');
                }
            }
			if(_in[i-1] == ')')//in between left and right brace
			{
				 _in.insert(_in.begin() + i, '*');
			}
		}
		if(curr_char == ')' && i <(_in.size()-1)) // after a closing right brace before an operand
		{
			if(!Tokens::Operators::isOperator(_in[i+1]))
			{
				_in.insert(_in.begin() + (i+1), '*');
			}
		}
        if((curr_char == '0' || curr_char == '1') && i != 0)
        {
            auto& prev_char = _in[i-1];
            if(prev_char == '0' || prev_char == '1')
            {
                _in.insert(_in.begin() + i, '*');
            }
        }
		if (isalpha(curr_char) && i != 0) // in between a function token and a operand
        {
            if (isdigit(_in[i - 1]))
            {
                _in.insert(_in.begin() + i, '*');
            }
        }
    }
}

/**
 * @brief shuntingYard - function to convert a function from infix to postfix notation
 * @param _in - input function in infix notation
 * @param _function_number - total number of functions to expect (max number of variables allowed)
 * @return function in posfix notation ready to be calculed
 */
std::string ShuntingYard::shuntingYard(const std::string& _in, size_t _function_number) //to RPN
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
    while (pos < _in.size())
    {
        char curr_char = _in[pos];
        //Variable
        auto var_pair = getVariableToken(_in, pos, _function_number);
        if (var_pair != -1)
        {
			if(state!= expect_operand)
			{
				std::string exception_happened("operator expected at position ");
				exception_happened += std::to_string(pos+1) + ". Got a variable token";
				throw(std::exception(exception_happened.c_str()));
			}
			out += _in.substr(pos, var_pair) + ' ';
			// has_variable.at(std::stoi(_in.substr(pos+1,var_pair-1))-1) = true;
            pos += var_pair;
			had_variable = true;
			state = expect_operator;
            continue;
        }
        // Number
        int num_len = getNumberToken(_in, pos);
        if (num_len != -1)
        {
			if(state!= expect_operand)
			{
				std::string exception_happened("operator expected at position ");
				exception_happened += std::to_string(pos+1) + ". Got a number";
				throw(std::exception(exception_happened.c_str()));
			}
            out += (_in.substr(pos, num_len) + ' ');
            pos += num_len;
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
				std::string exception_happened("before a closing right brace at pos ");
				exception_happened += std::to_string(pos+1) + " there has to be an operand of a kind";
				throw(std::exception(exception_happened.c_str()));
			}
            bool found_left_parenthesis = false;
            while (!(stack.empty() || (found_left_parenthesis = stack.top() == "(")))
            {
                out += stack.top() + ' ';
                stack.pop();
            }
            if (found_left_parenthesis == false) // if the stack is emptied and the left parenthesis was not found
            {
                throw std::exception("Mismatched Parenthesis");
            }
			stack.pop(); // the left brace at the top
            if (!stack.empty() && Tokens::Functions::isFunction(stack.top()))
            {
                out += stack.top() + ' ';
                stack.pop();
            }
            ++pos;
			state = expect_operator;
            continue;
        }
        // Unary Minus
        if(curr_char == '-' && pos < _in.size()-1)
        {
            if((pos == 0 || _in[pos-1] == '(' || Tokens::Operators::isOperator(_in[pos-1])))
            {
                stack.push("unary_minus");
                ++pos;
                continue;
            }
        }
        // Operators
        if (Tokens::Operators::isOperator(curr_char))
        {
            if(pos == 0)
            {
                throw(std::exception("operator at the beginning of a function"));
            }
			if(state != expect_operator)
            {
				std::string exception_happened( "Unexpected operator at position ");
				exception_happened += std::to_string(pos+1);
                throw(std::exception(exception_happened.c_str()));
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
		std::string exception_happened = "Unknown token at position " + std::to_string(pos + 1);
		throw std::exception(exception_happened.c_str());
    }
	while (!stack.empty())
    {
        if (stack.top() == "(")
            throw std::exception("Error: Mismatched parenthesis");
        out += stack.top() + ' ';
        stack.pop();
    }
/*
    for(size_t i = 0; i < _function_number; ++i)
    {
        if(has_variable.at(i) == false)
        {
            throw std::exception("Not all variables are present");
        }
    }
*/
	if(state == expect_operand)
    {
        throw(std::exception("operator without a second operant at the end of the function"));
    }
	if(!had_variable)
	{
		throw(std::exception("function must have at least one variable in it"));
	}
    return out;
}

/**
 * @brief calculateFunc - function that takes a function in RPN and a point and get's function value at that point
 * @param _RPN - function in postfix notation
 * @param _arg_vals - point at which to calculate
 * @return a value of a function at that point
 */
double ShuntingYard::calculateFunc(const std::string& _RPN, const std::vector<double>& _arg_vals)
{
    std::istringstream stream(_RPN);
    std::stack<double> numbers;
    std::string token;
    double number_token;
	while (stream >> token) // read a token
    {
		if (token[0] == 'x') // if it is a variable push it's value on to the stack
        {
            numbers.push(_arg_vals[std::stoi(token.substr(1))-1]);
        }
        else if (std::istringstream(token) >> number_token)
        {
            numbers.push(number_token);
        }
        else
        {
            if (Tokens::Operators::isOperator(token[0]))
            {
                if(numbers.size()< 2)
                {
                    throw std::exception("incorrect intput");
                }
                double operant2(numbers.top());
                numbers.pop();
                double operant1(numbers.top());
                numbers.pop;
                const Tokens::Operators::S_Operator& v_operator = Tokens::Operators::getOperator(token[0]);
                numbers.push(v_operator.function(operant1, operant2));
            }
            else // isFunction(token)
            {
                if(numbers.empty())
                {
                    throw std::exception("incorrect intput");
                }
                double argument(numbers.top());
                numbers.pop();
				numbers.push((Tokens::Functions::getFunction(token))(argument));
            }
        }
    }
    return numbers.top();
}

/**
 * @brief getVariableToken - function that checks if there is a spaning variable token at this position and if there is output it's length
 * @param _in - input string
 * @param _pos - position to check from
 * @param _function_number - maximum numbered variable to expect
 * @return -1 if not a variable, else - the lenght of a variable (>2 since each variable must be numbered)
 */
int ShuntingYard::getVariableToken(const std::string& _in, size_t _pos,size_t _function_number)
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
            throw std::invalid_argument("x must have a number after it");
        }
        if (std::stoi(_in.substr(_pos + 1, count - 1)) > _function_number /*NUM_OF_FUNCTIONS*/)
        {
            throw  std::invalid_argument("A variable has higher index than total number of functions");
        }
        return count;
    }
    return -1;
}

/**
 * @brief getNumberToken - function that checks if there is a spaning number token at this position and if there is output it's length
 * @param _in - input string
 * @param _pos - position to check at
 * @return -1 if it is not a number token, else the length of a token
 */
int ShuntingYard::getNumberToken(const std::string& _in, size_t _pos /*= 0*/)
{
	// number is defined as any number of digits, one dot is possible
    if (isdigit(_in[_pos]))
    {
        size_t count = 1;
		size_t after_dot_count = 0;
        bool dottag = false;
        for (size_t i = _pos + 1; i < _in.size(); ++i)
        {
            if (isdigit(_in[i]))
            {
                ++count;
				if(dottag)
				{
					++after_dot_count;
				}
            }
            else if (_in[i] == '.')
            {
                if (dottag == false)
                {
                    dottag = true; ++count;
                }
                //more then one dot in a num
                else
                {
                    std::string exception_happened = "Unrecognized dot at position " + std::to_string(_pos + count + 1);
                    throw std::exception(exception_happened.c_str());
                }
            }
            else { break; }
        }
		if(after_dot_count > 10)
		{
			throw std::exception("Numbers with the pressision of more than 10 digits after the dot are not allowed in functions");
		}
        return int(count);
    }
    return -1;
}

/**
 * @brief getFuncToken - function that checks if there is a spaning function token at this position and if there is output it's length
 * @param _in - input string
 * @param _pos - positon to check at
 * @return -1 if it is a function token, else the length of a token
 */
int ShuntingYard::getFuncToken(const std::string& _in, size_t _pos /*= 0*/)
{
    //function is defined as letters and numbers until '(' starting with !!!a letter!!!
    //example: log10, ln, sin, cos, ect...
    if (isalpha(_in[_pos]))
    {
        size_t count = 1;
        for (size_t i = _pos + 1; i < _in.size(); ++i)
        {
            if (isalnum(_in[i]))
            {
                ++count;
            }
            if (_in[i] == '(')
            {
                break;
            }
            if (i ==_in.size()-1)
            {
				std::string exception_happened = "Unknown token at position " + std::to_string(_pos + 1) + ": " + _in.substr(_pos, count);
				throw std::exception(exception_happened.c_str());
            }
        }
        if (Tokens::Functions::isFunction(_in.substr(_pos, count)))
            return int(count);
		std::string exception_happened = "Unknown token at position " + std::to_string(_pos + 1) + ": " + _in.substr(_pos, count);
        throw std::exception(exception_happened.c_str());
    }
    return -1;
}

/**
 * @brief operatorCompare - function that compares presedence of the current operator and the operator (or a left brace or a function) on top of the stack
 * @param _in_char - operator
 * @param _tops_of_stack - operator at the top of the stack
 * @return true if top of stack presedence is less(based on the associativity tag) and false otherwise
 */
bool ShuntingYard::operatorCompare(char _in_char, const std::string& _tops_of_stack)
{
    const Tokens::Operators::S_Operator& curr_operator = Tokens::Operators::getOperator(_in_char);
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
        top_of_stack_presedence = Tokens::Operators::getOperator(_tops_of_stack.at(0)).presedence;
    }
    return (curr_operator.associativity == Tokens::Operators::S_Operator::E_left && curr_operator.presedence <= top_of_stack_presedence) ||
           (curr_operator.associativity == Tokens::Operators::S_Operator::E_right && curr_operator.presedence < top_of_stack_presedence);
}

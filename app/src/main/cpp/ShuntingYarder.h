#pragma once
#include <string>
#include <vector>
#include <VarTable.h>
#include <cmath>

/**
 * ShuntingYarder.h - input function analization using shunting yard algorithm (insert '*', convert to postfix and calcutate the result at some point in R^n, n - number of functions in the system)
 */
class ShuntingYarder
{

    std::string input_expr;
    std::string output_expr;

    ShuntingYarder(const std::string& _in) : input_expr(_in) {}
    void preAnalize();//  prototype of a function  that converts an expression from mathematical to one that can be procced by shunting yard
public:
    ShuntingYarder() = delete;


    ShuntingYarder static create(const std::string& _in)
    {
        auto var = ShuntingYarder(_in);
        var.preAnalize();
        return var;
    }


	ShuntingYarder& parse(size_t _var_number = 8);//  prototype of a function function to convert a function from infix to postfix notation

    std::string getVector(size_t _var_num)
    {
        auto var_vals = VarTable(_var_num);
        std::string out;
        while (var_vals.decimal() < (pow(2, _var_num) -1))
        {
            out += (calculateFunc(var_vals) + '0');
            var_vals.Increment();
        }
        out += (calculateFunc(var_vals) + '0');
        return out;
    }

    bool calculateFunc(const VarTable& _arg_vals);//  prototype of a function that takes a function in RPN and a point and get's function value at that point

private:
	int getVariableToken(const std::string& _in, size_t _pos, size_t _var_number);//  prototype of a function that checks if there is a spaning variable token at this position and if there is output it's length


	bool operatorCompare(char _in_char, const std::string& _tops_of_stack);//  prototype of a function that compares precedence of the current operator and the operator (or a left brace or a function) on top of the stack
};

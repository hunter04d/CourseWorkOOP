//
// Created by hunter04d on 08.11.2017.
//

#ifndef COURSEWORKOOP_PARSEREXCEPTION_H
#define COURSEWORKOOP_PARSEREXCEPTION_H


#include <stdexcept>
#include <string>

class ParserException : std::exception
{
    std::string str;
public:
    ParserException(ErrorCode code, int pos = -1) : str(std::to_string(int(code)) + ' ' + pos) { }

    const char* what() const override
    {
        return str.c_str();
    }

    enum class ErrorCode
    {
        UNKNOWN = 0,
        INVALID_VARIABLE = 1,
        EXPECTED_OPERAND = 2,
        EXPECTED_OPERATOR = 3,
        MISPATCHED_PARENTHESIS = 4,
        OPERATOR_IN_FRONT = 5,
        OPERATOR_BEFORE_RIGHT_BRACE = 6,
        EVALUATES_TO_CONSTANT = 7,
        OPERATOR_AT_THE_END = 8,

    };
};


#endif //COURSEWORKOOP_PARSEREXCEPTION_H

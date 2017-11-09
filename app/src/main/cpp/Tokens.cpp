#include "Tokens.h"

bool ::Tokens::isCharOperator(const char &_in)
{
    return operators_container.count(_in) == 1;
}

const Tokens::S_Operator &::Tokens::getOperatorFromChar(const char &_in)
{
    return operators_container.at(_in);
}

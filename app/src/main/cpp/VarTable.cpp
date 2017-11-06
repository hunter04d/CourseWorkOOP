#include "VarTable.h"


void VarTable::Increment()
{
	if (actualValue < (2 << NumOfVars - 1) - 1) ++actualValue;
	else { Set(0); }
	for (int i = NumOfVars - 1; i >= 0; --i)
	{
		if (!F_VarTable.at(i))
		{
			F_VarTable.at(i) = true;
			for (int j = i + 1; j < NumOfVars; ++j)
			{
				F_VarTable.at(j) = 0;
			}
			break;
		}
	}
}

void VarTable::Decrement()
{
	if (actualValue < (2 << NumOfVars - 1) - 1 && actualValue > 0) --actualValue;
	else actualValue = (2 << NumOfVars - 1) - 1;
	F_VarTable = utility::toBinaryVector(actualValue, NumOfVars);
}

void VarTable::Set(size_t _value)
{
	if (_value < (2 << NumOfVars - 1))
	{
		actualValue = _value;
	}
	else actualValue = (2 << NumOfVars - 1) - 1;
	F_VarTable = utility::toBinaryVector(actualValue, NumOfVars);
}

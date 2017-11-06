
#include "PrintTable .h"



void PrintTable(std::vector<bool> &_function)
{
	int NumOfVars = ceil(log2(_function.size()));
	_function.resize(2 << (NumOfVars - 1), false);
	char var = 'A';
	for (auto i = 0; i < NumOfVars; ++i)
	{
		std::cout << ' ' << var << " |";
		++var;
	}
	std::cout << "| Function" << std::endl << "______________________" << std::endl;
	VarTable temp(NumOfVars);
	for (auto j = 0; j < 2 << (NumOfVars - 1); ++j)
	{
		for (auto i = 0; i < NumOfVars; ++i)
		{
			std::cout << ' ' << temp.at(i) << " |";
		}
		std::cout << "| " << _function.at(j) << std::endl;
		temp.Increment();
	}
}

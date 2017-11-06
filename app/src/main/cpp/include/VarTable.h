#pragma once
#include <vector>
#include "UtilityFunc.h"
#include <iostream>

class VarTable
{
	size_t actualValue = 0;
	size_t NumOfVars = 3;
	std::vector<bool> F_VarTable;
public:
	explicit VarTable(size_t _NumOfVars = 3) : NumOfVars(_NumOfVars), F_VarTable(_NumOfVars, false)
	{
	}
	VarTable(size_t _NumOfVars, size_t value) : NumOfVars(_NumOfVars), F_VarTable(_NumOfVars, false)
	{
		Set(value);
	}

	void Increment();

	void Decrement();

	void Set(size_t _value);

	void Resize(size_t _newSize)
	{
		NumOfVars = _newSize;
		F_VarTable.resize(_newSize, false);
	}

	size_t decimal() const
	{
		return actualValue;
	}

	std::vector<bool>::const_reference at(size_t pos) const
	{
		return F_VarTable.at(pos);
	}

	std::vector<bool>::const_reference front() const
	{
		return F_VarTable.front();
	}

	std::vector<bool>::const_reference back() const
	{
		return F_VarTable.back();
	}
	size_t Size() const
	{
		return NumOfVars;
	}

	friend std::ostream& operator<<(std::ostream& _cout, VarTable& _VarTable);
};

inline std::ostream& operator<<(std::ostream& _cout, VarTable& _VarTable)
{
	for (size_t i = 0; i < _VarTable.NumOfVars; ++i)
	{
		bool b = _VarTable.F_VarTable[i];
		_cout << b;
	}
	return _cout;
}

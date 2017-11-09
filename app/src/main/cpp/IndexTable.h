#pragma once
#include "VarTable.h"
#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <string>
#include <tuple>
#include "CoreTable.h"
#include "FunctionBool.h"
#include <set>


//TODO: this class is a mess, proper implementation is in order
class IndexTable
{
	struct S_Index
	{
		bool is_removed = false;
		std::vector<bool> value;

	};

	const size_t size;
	std::vector<std::vector<S_Index>> F_Table;
	std::vector<std::string> naming;
	bool tag = true;

	/*
	* Main functions for removing unneeded combinations
	*/
	void RemoveRow(size_t row) // used in access with FunctionBool object
	{
		for (int i = 0; i < size; ++i)
		{
			F_Table.at(row).at(i).is_removed = true;
		}
	}

	void RemoveSimilar();

	void Consume();
public:

	explicit IndexTable(size_t NumOfVars);

	void RemoveFromFunction(FunctionBool& _func)
	{
		for (size_t i = 0; i < size; ++i)
		{
			if (_func.vector.at(i) == 0)
			{
				RemoveRow(i);
			}
		}
		RemoveSimilar();
		Consume();
	}
	void RemoveFromFunction(FunctionBool&& _func)
	{
		for (size_t i = 0; i < size; ++i)
		{
			if (_func.vector.at(i) == 0)
			{
				RemoveRow(i);
			}
		}
		RemoveSimilar();
		Consume();
	}

	//Printers
	void PrintFile();

	std::string PrintNames();

	std::string Print();

	//Core Table interface
	std::set<size_t> GetUnremovedColPos();

	TermIndex ConvertS_Index_to_TermIndex(size_t _i, size_t _j);

	std::vector<TermIndex> GetTermsInCoreTableForm();
};

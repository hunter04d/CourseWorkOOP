#pragma once

#include <string>
#include <iostream>
#include <vector>
#include <utility>
#include <set>

#include <iostream>
#include <sstream>
#include <numeric>
#include "TermIndex.h"
#include "TagBuilder.h"
#include "FunctionBool.h"
#include "VarTable.h"

struct Edge
{
	bool is_crossed = 0;
	TermIndex term;
	explicit Edge(TermIndex _term) : term(_term) { }
	explicit Edge(VarTable _init_val) : term(_init_val) { }
};

struct Cell
{
	bool includes_min_term = 0;
	bool deleted = 0;
};

class CoreTable
{
	size_t rows;
	size_t coloumns;
	size_t num_of_vars;
	std::set<size_t> deletedCol;
	std::set<size_t> deletedRow;
	std::vector<Edge> min_terms;
	std::vector<Edge> terms;
	std::vector<std::vector<Cell>> F_table;

public:
	CoreTable(std::vector<TermIndex> _min_terms, FunctionBool _func);

	// a  LOT OF TODO
private:
	int OneXInCol(size_t _coloumn)
	{
		bool ret_tag = false;
		int out = -1;
		for (auto i = 0; i < rows; ++i)
		{
			if (F_table.at(i).at(_coloumn).includes_min_term == true)
			{
				if (ret_tag == false)
				{
					ret_tag = true;
					out = i;
					continue;
				}
				if (ret_tag == true)
				{
					return -1;
				}
			}
		}
		return out;
	}

	void DeleteRow(int _row)
	{
		for (auto j = 0; j < coloumns; ++j)
		{
			F_table.at(_row).at(j).deleted = true;
			if (F_table.at(_row).at(j).includes_min_term)
			{
				if (deletedCol.count(j) == 0)
				{
					deletedCol.insert(j);
					DeleteColoumn(j);
				}
			}
		}
	}
	void DeleteColoumn(size_t _coloumn)
	{
		terms.at(_coloumn).is_crossed = true;
		for (auto i = 0; i < rows; ++i)
		{
			F_table.at(i).at(_coloumn).deleted = true;
		}
	}
public:
	void GetCore()
	{
		for (auto j = 0; j < coloumns; ++j)
		{
			int r_row = OneXInCol(j);
			if (r_row != -1)
			{
				if (deletedRow.count(r_row) == 0)
				{
					min_terms.at(r_row).is_crossed = true;
					deletedRow.insert(r_row);
					DeleteRow(r_row);
				}
			}
		}
	}
	std::string ReturnCore()
	{
		std::string out;
		for (const auto& element : deletedRow)
		{
			out += min_terms.at(element).term.ToString();
			out.push_back('v');
		}
		return out;
	}

	std::vector<std::string> ReturnRest();
	

	std::string Print(const std::vector<std::string>& var_names);
};

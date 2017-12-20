#pragma once

#include <string>
#include <iostream>
#include <vector>
#include <utility>
#include <set>
#include "FunctionBool.h"
#include "VarTable.h"
#include <iostream>
#include <sstream>
#include <numeric>
#include "TermIndex.h"
#include "TagBuilder.h"


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
	size_t coloumlns;
	size_t num_of_vars;
	std::set<size_t> deletedCol;
	std::set<size_t> deletedRow;
	std::vector<Edge> min_terms;
	std::vector<Edge> terms;
	std::vector<std::vector<Cell>> F_table;

public:
	CoreTable(std::vector<TermIndex> _min_terms, FunctionBool _func) : rows(_min_terms.size()), coloumlns(_func.PDNF_Size()), num_of_vars(_func.numberOfvars), F_table(rows, std::vector<Cell>(coloumlns))
	{
		auto _PDNF = _func.PDNF();
		for (auto i = 0u; i < coloumlns; ++i)
		{
			terms.emplace_back(VarTable(_func.numberOfvars, _PDNF.at(i)));
		}
		for (auto i = 0u; i < rows; ++i)
		{
			min_terms.emplace_back(_min_terms.at(i));
		}
		for (auto i = 0u; i < rows; ++i)
		{
			for (auto j = 0u; j < coloumlns; ++j)
			{
				if (terms.at(j).term.Includes(min_terms.at(i).term))
				{
					F_table.at(i).at(j).includes_min_term = true;
				}
			}
		}
	}

	// a  LOT OF TODO
private:
	int OneXInCol(size_t _coloumn)
	{
		bool ret_tag = false;
		int out = -1;
		for (auto i = 0; i < rows; ++i)
		{
			if (F_table.at(i).at(_coloumn).includes_min_term==true && ret_tag == false)
			{
				ret_tag = true;
				out = i;
				continue;
			}
			if((F_table.at(i).at(_coloumn).includes_min_term == true) && ret_tag == true)
			{
				return -1;
			}
		}
		return out;
	}

	void DeleteRow(int _row)
	{
		for (auto j = 0; j < coloumlns; ++j)
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
		for (auto j = 0; j < coloumlns; ++j)
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

	std::vector<std::string> ReturnRest()
	{
		std::vector<std::string> out;
		std::vector<size_t> left_out_min_terms;
		std::vector<size_t> left_out_terms;
		for (auto i = 0u; i < min_terms.size(); ++i)
		{
			if (deletedRow.count(i) == 0)
			{
				left_out_min_terms.push_back(i);
			}
		}
		for (auto i = 0u; i < terms.size(); ++i)
		{
			if (deletedCol.count(i) == 0)
			{
				left_out_terms.push_back(i);
			}
		}
		auto N = left_out_min_terms.size();
		if(N == 0 || left_out_terms.size() == 0)
		{
			return std::vector<std::string>(1);
		}
		for (auto K = 0; K <= N; ++K)
		{
			auto current_combinations = utility::NcombK_vector(N, K);
			for (const auto& current_combination : current_combinations) // look at the current combination
			{
				auto curr_terms(left_out_terms);
				for (const auto& current_min_term_index : current_combination)//look at the current min_term
				{
					for (auto i = 0; i < left_out_terms.size(); ++i)
					{
						if (F_table.at(left_out_min_terms.at(current_min_term_index)).at(left_out_terms.at(i)).includes_min_term)
						{
							if (std::find(curr_terms.begin(), curr_terms.end(), left_out_terms.at(i)) != curr_terms.end())
							{
								curr_terms.erase(std::find(curr_terms.begin(), curr_terms.end(), left_out_terms.at(i)));
							}
						}
					}
				}
				if (curr_terms.empty())
				{
					std::string for_pushing;
					for (int i = 0; i < K; ++i)
					{
						for_pushing += min_terms.at(left_out_min_terms.at(current_combination.at(i))).term.ToString();
						for_pushing.push_back('v');
					}
					for_pushing.pop_back();
					out.push_back(for_pushing);
				}
			}
			if (!out.empty())
			{
				break;
			}
		}
		return out;
	}
	

	std::string Print(const std::vector<std::string>& var_names)
	{
		std::ostringstream cout;
		cout << "<thead><tr><th>";
		for (auto k = 0; k < num_of_vars; ++k)
		{
			cout << var_names[k];
		}
        cout << "</th>";

		for (auto j = 0; j < coloumlns; ++j)
		{
			cout << "<th>";
			if (terms.at(j).is_crossed)
			{
				cout << "<s>";
			}
			for (auto k1 = 0; k1 < num_of_vars; ++k1)
			{
				switch (int(terms.at(j).term.at(k1)))
				{
					case 0:
					{
						cout << 0;
						break;
					}
					case 1:
					{
						cout << 1;
						break;
					}
					default: cout << '-';
				}
			}
			if (terms.at(j).is_crossed)
			{
				cout << "</s>";
			}
			cout << "</th>";
				
		}
		cout << "</tr></thead><tbody>";
		for (auto i = 0; i < rows; ++i)
		{
			cout << "<tr><td><b>";
				if (min_terms.at(i).is_crossed)
				{
					cout << "<s>";
				}
				for (auto k1 = 0; k1 < num_of_vars; ++k1)
				{
					switch (min_terms.at(i).term.at(k1))
					{
						case 0:
						{
							cout << 0;
							break;
						}
						case 1:
						{
							cout << 1;
							break;
						}
						default: cout << '-';
					}
				}
			if (min_terms.at(i).is_crossed)
			{
				cout << "</s>";
			}
			cout << "</b></th>";
			for (auto j = 0; j < coloumlns; ++j)
			{
                cout << "<td>";
                if (F_table.at(i).at(j).includes_min_term)
                {
                    if (F_table.at(i).at(j).deleted)
                    {
                        cout << "<s>X</s>";
                    } else {
                        cout << 'X';
                    }
                }
                else if (F_table.at(i).at(j).deleted)
				{
					cout << '-';
				}

				else
				{
					cout << ' ';
				}
                cout << "</td>";
			}
			cout << "</tr>";
		}
        cout << "</tbody>";
		return cout.str();
	}
};

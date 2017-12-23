#include "IndexTable.h"
#include "FunctionBool.h"
#include "TagBuilder.h"
#include <string>
#include <sstream>
#include <cmath>

IndexTable::IndexTable(size_t NumOfVars) :size(2 << NumOfVars - 1), F_Table(size, std::vector<S_Index>(size))
{
	VarTable temp(NumOfVars);
	naming.resize(size);
	for (int i = 0; i < size; ++i)
	{
		for (int j = 0; j < NumOfVars; ++j)
		{
			if (i == 0)
				naming.at(j).push_back(j + 'a');
			F_Table.at(i).at(j).value.resize(1);
			F_Table.at(i).at(j).value.at(0) = temp.at(j);
		}
		temp.Increment();
	}
	temp.Set(0);
	size_t counter(NumOfVars);
	for (auto K = 2; K <= NumOfVars; ++K)
	{
		auto combinationsRef = utility::NcombK_vector(NumOfVars, K);
		size_t CurrNumOfComb = utility::numberof_NcombK(NumOfVars, K);
		for (auto j = counter; j < counter + CurrNumOfComb; ++j)
		{
			for (auto i = 0; i < size; ++i)
			{
				F_Table.at(i).at(j).value.resize(K);
				for (auto k = 0; k < K; ++k)
				{
					F_Table.at(i).at(j).value.at(k) = temp.at(combinationsRef.at(j - counter).at(k));
					if (i == 0)
						naming.at(j).push_back(combinationsRef.at(j - counter).at(k) + 'a');
				}
				temp.Increment();
			}
			temp.Set(0);
		}
		counter += CurrNumOfComb;
	}
}

std::string IndexTable::PrintNames(std::vector<std::string> var_names)
{
	std::ostringstream cout;
    std::vector<std::string> naming_copy = naming;
    cout << "<thead><tr>";
    for (int i = 0; i < naming_copy.size() ; ++i)
    {
        std::vector<std::string> new_names(size);
        for (const auto& x: naming[i])
        {
            new_names.push_back(var_names[x - 'a']);
        }
        std::string out;
        for (const auto& new_name : new_names)
        {
            out += new_name;
        }
        naming_copy[i] = out;
    }
	for (int i = 0; i < size; ++i)
	{
		cout << TagBuilder("th", naming_copy[i].c_str()).build(TagBuilder::BuildParams::CLOSETAG);
	}
    cout << "</tr></thead>";
	return cout.str();
}

std::string IndexTable::Print()
{
	std::ostringstream cout;
    cout << "<tbody>";
	for (auto i = 0u; i < size; ++i)
	{
        cout << "<tr>";
		for (auto j = 0u; j < size; ++j)
		{
            cout << "<td>";
            if (F_Table.at(i).at(j).is_removed)
            {
                cout << "<s>";
            }
			for (auto k = 0u; k < F_Table.at(i).at(j).value.size(); ++k)
			{
                cout << F_Table.at(i).at(j).value.at(k);
			}
            if (F_Table.at(i).at(j).is_removed)
            {
                cout << "</s>";
            }
			cout << "</td>";
		}
		cout << "</tr>";
	}
    cout << "</tbody>";
	return cout.str();
}

std::set<size_t> IndexTable::GetUnremovedColPos()
{
	std::set<size_t> out;
	for (int row = 0; row < size; ++row)
	{
		for (int coloumn = 0; coloumn < size -1; ++coloumn)
		{
			if (F_Table.at(row).at(coloumn).is_removed == false)
			{
				out.insert(coloumn);
			}
		}
	}
/* //TODO: Handle unminimized cases here!!!
	if(out.size() ==0)
	{
		//throw std::exception("exception");
	}
*/
	return out;
}

TermIndex IndexTable::ConvertS_Index_to_TermIndex(size_t _i, size_t _j)
{
	TermIndex out(VarTable(log2(size), _i));
	const auto& str = naming.at(_j);
	char name_ptr = 'a';
	std::vector<size_t> pos;
	for (auto i = 0; i < str.size(); ++i)
	{
		pos.push_back(str.at(i) - name_ptr);
	}
	for (auto i = 0; i < log2(size); ++i)
	{
		if (std::find(pos.begin(), pos.end(), i) == pos.end())
		{
			out.at(i) = TermIndex::crossed;
		}
	}
	return out;
}

std::vector<TermIndex> IndexTable::GetTermsInCoreTableForm()
{
	std::vector<TermIndex> out;
	auto needed_cols = GetUnremovedColPos();
	auto it = needed_cols.begin();
	while (it != needed_cols.end())
	{
		for (auto i = 0; i < size; ++i)
		{
			if (F_Table.at(i).at(*it).is_removed == false)
			{
				auto insert_val = ConvertS_Index_to_TermIndex(i, *it);
				if (std::find(out.begin(), out.end(), insert_val) == out.end())
				{
					out.push_back(insert_val);
				}
			}
		}
		++it;
	}
	return out;
}

void IndexTable::RemoveSimilar()
{
	for (auto coloumn = 0; coloumn < size; ++coloumn)
	{
		for (auto row = 0; row < size; ++row)
		{
			if (F_Table.at(row).at(coloumn).is_removed == true)
			{
				for (auto k = 0; k < size; ++k)
				{
					if (F_Table.at(k).at(coloumn).is_removed || k == row) continue;
					if (F_Table.at(row).at(coloumn).value == F_Table.at(k).at(coloumn).value)
					{
						F_Table.at(k).at(coloumn).is_removed = true;
					}
				}
			}
		}
	}
}
void IndexTable::Consume()
{
	size_t N = static_cast<size_t>(log2(size));
	for (size_t row = 0; row < size; ++row)
	{
		for (size_t K = 0; K < N - 1;++K)
		{
			size_t startfor_i = [&K, &N]()
			{
				size_t sum = 0;
				for (int i = 1; i <= K; ++i)
				{
					sum += utility::numberof_NcombK(N, i);
				}
				return sum;
			}();
			for (size_t i = startfor_i; i < startfor_i + utility::numberof_NcombK(N, K + 1); ++i)
			{
				if (F_Table.at(row).at(i).is_removed == false)
				{
					for (size_t j = startfor_i + utility::numberof_NcombK(N, K + 1); j < size; ++j)
					{
						if (F_Table.at(row).at(j).is_removed == false)
						{
							if (utility::hasString(naming.at(i), naming.at(j)))
							{
								F_Table.at(row).at(j).is_removed = true;
							}
						}
					}
				}
			}
		}
	}
}

#pragma once
#include "VarTable.h"

class TermIndex
{
public:
	enum val
	{
		zero = 0,
		one = 1,
		crossed = 2
	};
private:
	size_t F_size;
	std::vector <val> F_term;
public:
	TermIndex(VarTable _init_val) : F_size(_init_val.Size()), F_term(F_size)
	{
		for (size_t i = 0; i < F_size; ++i)
		{
			F_term.at(i) = static_cast<val>((int)_init_val.at(i));
		}

	}
	bool operator==(const TermIndex& rhs) const
	{
		return F_term == rhs.F_term;
	}
	TermIndex CrossOutAtIndex(size_t index) const
	{
		auto out = *this;
		out.F_term.at(index) = crossed;
		return out;
	}
	bool Includes(TermIndex& _smaller_term)
	{
		for (size_t i = 0; i < F_size; ++i)
		{
			if ((F_term.at(i) == _smaller_term.F_term.at(i)) || _smaller_term.F_term.at(i) == crossed)
			{
				continue;
			}
			return false;
		}
		return true;
	}

	std::string ToString()
	{
		std::string out;
		for (const auto& x : F_term)
		{
			switch (x)
			{
				case zero:
				{
					out.push_back('0');
					break;
				}
				case one:
				{
					out.push_back('1');
					break;
				}
				case crossed:
				{
					out.push_back('-');
					break;
				}
			}
		}
		return out;
	}

	auto& at(size_t i)
	{
		return F_term.at(i);
	}
	auto at(size_t i) const 
	{
		return F_term.at(i);
	}
};

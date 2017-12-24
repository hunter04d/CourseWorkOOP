#pragma once
/*
 * Runtime heavy implementation
 * stores only the needed part
 * everything else is derived in member functions
 */
#include <string>
#include <vector>
#include <stdexcept>
#include "UtilityFunc.h"
#include "TermIndex.h"

class FunctionBool
{
private:
	std::vector<bool> vector;
	const size_t numberOfvars;
public:
	FunctionBool(std::initializer_list<bool> init_list) : vector(init_list), numberOfvars(ceil(log2(init_list.size())))
	{
		size_t numberOfContituents(utility::fastpow2(numberOfvars));
		if (vector.size() != numberOfContituents)
		{
			vector.resize(numberOfContituents, false);
		}
	}

	FunctionBool(std::vector<bool> init_vector) : vector(init_vector), numberOfvars(ceil(log2(init_vector.size())))
	{
		size_t numberOfContituents(utility::fastpow2(numberOfvars));
		if(vector.size() != numberOfContituents)
		{
			vector.resize(numberOfContituents, false);
		}
	}

	FunctionBool(const std::string& init_string) : numberOfvars(
			(size_t) ceil(log2(init_string.size())))
	{
		size_t numberOfContituents(utility::fastpow2(numberOfvars));
		for (auto i = 0 ; i < init_string.size(); ++i)
		{
			auto& curr_char =  init_string.at(i);
			if (curr_char == '0')
			{
				vector.push_back(0);
			}
			else if(curr_char == '1')
			{
				vector.push_back(1);
			}
			else { throw std::invalid_argument("wrong symbol"); }
		}
		if (vector.size() != numberOfContituents)
		{
			vector.resize(numberOfContituents, false);
		}
	}
    bool at(size_t i)
    {
        return vector.at(i);
    }

	std::vector<size_t> PDNF()
	{
		std::vector<size_t> out;
		for (auto i = 0; i < vector.size(); ++i)
		{
			if (vector.at(i) == 1)
				out.push_back(i);
		}
		return out;
	}
	size_t PDNF_Size()
	{
		size_t out(0);
		for (auto i = 0; i < vector.size(); ++i)
		{
			if (vector.at(i) == 1)
				++out;
		}
		return out;
	}

	std::vector<size_t> PСNF()
	{
		std::vector<size_t> out;
		for (auto i = 0; i < vector.size(); ++i)
		{
			if (vector.at(i) == 0)
				out.push_back(i);
		}
		return out;
	}
	size_t PCNF_Size()
	{
		size_t out(0);
		for (auto i = 0; i < vector.size(); ++i)
		{
			if (vector.at(i) == 0)
				++out;
		}
		return out;
	}

    size_t GetNumberOfvars()
    {
        return numberOfvars;
    }
};

class Undefined_FunctionBool
{
private:
	std::vector<TermIndex::val> vector;
	const size_t numberOfvars;
public:
	Undefined_FunctionBool(const std::string& init_string) : numberOfvars(ceil(log2(init_string.size())))
	{
		size_t numberOfContituents(utility::fastpow2(numberOfvars));
		for (auto i = 0; i < init_string.size(); ++i)
		{
			auto& curr_char = init_string.at(i);
			if (curr_char == '0')
			{
				vector.push_back(TermIndex::zero);
			}
			else if (curr_char == '1')
			{
				vector.push_back(TermIndex::one);
			}
			else if (curr_char == '-')
			{
				vector.push_back(TermIndex::crossed);
			}
			else { throw std::invalid_argument("wrong symbol"); }
		}
		if (vector.size() != numberOfContituents)
		{
			vector.resize(numberOfContituents, TermIndex::crossed);
		}
	}
	FunctionBool AsFunctionBool_WithUnknowValuesAs(bool _as = 1)
	{
		std::string out_ctor;
		for (const auto& set: vector)
		{
			if(set == TermIndex::zero)
			{
				out_ctor.push_back('0');
			}
			else if(set == TermIndex::one)
			{
				out_ctor.push_back('1');
			}
			else
			{
				if (_as == 1)
				{ out_ctor.push_back('1'); }
				else { out_ctor.push_back('0'); }
			}
		}
		return FunctionBool(out_ctor);
	}
	char AtAsChar(size_t i) const
    {
        auto x = vector.at(i);
        switch (x)
        {
            case TermIndex::zero:
                return '0';
            case TermIndex::one:
                return '1';
            case TermIndex::crossed:
                return '-';
            default:
                return '-';
        }
    }
    size_t GetNumberOfvars()
    {
        return numberOfvars;
    }

	std::vector<size_t> PDNF_WithUnknowValues()
	{
		std::vector<size_t> out;
		for (auto i = 0; i < vector.size(); ++i)
		{
			if (vector.at(i) == TermIndex::one || vector.at(i) == TermIndex::crossed)
				out.push_back(i);
		}
		return out;
	}
	std::vector<size_t> PDNF_WithOutUnknowValues()
	{
		std::vector<size_t> out;
		for (auto i = 0; i < vector.size(); ++i)
		{
			if (vector.at(i) == TermIndex::one)
				out.push_back(i);
		}
		return out;
	}
};

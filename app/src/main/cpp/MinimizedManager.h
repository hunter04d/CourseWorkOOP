#pragma once
#include <string>
#include <vector>
#include <algorithm>

class MinimizedManager
{
	
	std::string core;
	std::vector<std::string> other_sets;
	std::vector<std::string> other_sets_sorted;
	const size_t num_of_vars;
public:
	MinimizedManager(const std::vector<std::string>& _otherSets, const std::string& _core) : core(_core), other_sets(_otherSets), other_sets_sorted(other_sets), num_of_vars(std::count(core.cbegin(),core.cend(), 'v'))
	{
		std::sort(other_sets_sorted.begin(),other_sets_sorted.end(), [](const auto& set1, const auto& set2)
		{
			return std::count(set1.cbegin(), set1.cend(), '-') > std::count(set2.cbegin(), set2.cend(), '-');
		});
	}

	std::vector<std::string> GetAll()
	{
		std::vector<std::string> out;
		if(other_sets.size() == 0)
		{
			out.push_back(core);
		}
		else
		{
			for (const auto& set: other_sets_sorted)
			{
				out.push_back(core + set);
			}
		}
		return out;
	}
	std::string GetBest()
	{
		return std::string(core + other_sets_sorted[0]);
	}
	std::string GetCore()
    {
        return core.substr(0, core.size()-1);
    }
};


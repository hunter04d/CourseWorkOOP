//
// Created by hunter04d on 19.12.2017.
//
#pragma once

#include <string>

class TagBuiler
{
private:

    std::string name;
    std::string value;
    std::string attributes;
public:
    enum class BuildParams
    {
        CLOSETAG = 0,
        DONTCLOSE = 1
    };

    TagBuiler(const char* name, const char* value) : name(name), value(value)
    {
    }

    TagBuiler* withAttribute(const char* name, const char* value)
    {
        attributes += std::string(name) + "=\"" + value + "\" ";
        return this;
    }

    std::string build(BuildParams param)
    {
        std::string out = std::string("<") + name + attributes + ">" + value;
        if(param == BuildParams::DONTCLOSE)
        {
            return out;
        }
        else
        {
            return out + "</" + name + ">";
        }

    }
};

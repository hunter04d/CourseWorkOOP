#include <jni.h>
#include <string>
#include "ShuntingYarder.h"
#include "MinimizedManager.h"
#include "CoreTable.h"
#include "IndexTable.h"
#include "FunctionBool.h"
#include "ParserException.h"
#include <sstream>

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_hunter04d_android_booleanminimizer_NativeLib_calculateMinification(JNIEnv *env, jclass instance, jstring str, jboolean is_all_cases)
{
    auto inChar = env->GetStringUTFChars(str, 0);
    std::string in(inChar);
    env->ReleaseStringUTFChars(str, inChar);
    try {
        Undefined_FunctionBool last_func = Undefined_FunctionBool(in);
        IndexTable index_table(last_func.numberOfvars);
        index_table.RemoveFromFunction(last_func.AsFunctionBool_WithUnknowValuesAs(1));
        CoreTable core_table(index_table.GetTermsInCoreTableForm(),
                             last_func.AsFunctionBool_WithUnknowValuesAs(0));
        core_table.GetCore();
        MinimizedManager manager(core_table.ReturnRest(), core_table.ReturnCore());
        if (is_all_cases == false)
        {
            auto out = manager.GetBest();
            auto ret= (jobjectArray)env->NewObjectArray(1,env->FindClass("java/lang/String"),env->NewStringUTF(""));
            env->SetObjectArrayElement(ret,0, env->NewStringUTF(out.c_str()));
            return ret;
        }
        else
        {
            auto out = manager.GetAll();
            auto ret= (jobjectArray)env->NewObjectArray(out.size(),env->FindClass("java/lang/String"),env->NewStringUTF(""));
            for(int i = 0; i < out.size(); ++i)
            {
                env->SetObjectArrayElement(ret,i, env->NewStringUTF(out[i].c_str()));
            }
            return ret;
        }
    }
    catch(...)
    {
        return (jobjectArray)env->NewObjectArray(1,env->FindClass("java/lang/String"),env->NewStringUTF("error"));
    }
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_hunter04d_android_booleanminimizer_NativeLib_parseExpresion(JNIEnv *env, jclass  type, jstring expr_, jdouble varNum)
{
    const char *expr = env->GetStringUTFChars(expr_, 0);
    try
    {
        int var_num = int(varNum);
        std::string out = (ShuntingYarder::create(expr)).parse().getVector(var_num);
        env->ReleaseStringUTFChars(expr_, expr);
        auto ParserResult = env->FindClass("com/hunter04d/android/booleanminimizer/ParserResult");
        auto ctor = env->GetMethodID(ParserResult, "<init>", "(Ljava/lang/String;Z)V");
        return  env->NewObject(ParserResult, ctor, env->NewStringUTF(out.c_str()), true);
    }
    catch (ParserException& exception)
    {
        env->ReleaseStringUTFChars(expr_, expr);
        auto ParserResult = env->FindClass("com/hunter04d/android/booleanminimizer/ParserResult");
        auto ctor = env->GetMethodID(ParserResult, "<init>", "(Ljava/lang/String;Z)V");
        return env->NewObject(ParserResult, ctor, env->NewStringUTF(exception.what()), false);
    }



}extern "C"
JNIEXPORT jstring JNICALL
Java_com_hunter04d_android_booleanminimizer_NativeLib_stringOfVarTable(JNIEnv *env, jclass type, jint n, jint num_of_vars)
{
    VarTable v(num_of_vars,n);
    return env->NewStringUTF(v.toString().c_str());
}extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_hunter04d_android_booleanminimizer_NativeLib_getTablesHtml(JNIEnv *env, jclass type, jstring str_, jstring str1_)
{
    const char *str = env->GetStringUTFChars(str_, 0);
    std::string in(str);
    const char *str1 = env->GetStringUTFChars(str1_, 0);
    std::string varNames(str1);
    env->ReleaseStringUTFChars(str_, str);
    env->ReleaseStringUTFChars(str1_, str1);
    std::istringstream iss(varNames);
    std::vector<std::string> var_names;
    do
    {
        std::string subs;
        iss >> subs;
        var_names.push_back(subs);
    } while (iss);
    try {
        std::vector<std::string> tables;
        Undefined_FunctionBool last_func = Undefined_FunctionBool(in);
        IndexTable index_table(last_func.numberOfvars);
        tables.push_back(index_table.PrintNames(var_names) + index_table.Print());
        index_table.RemoveFromFunction(last_func.AsFunctionBool_WithUnknowValuesAs(1));
        tables.push_back(index_table.PrintNames(var_names) + index_table.Print());
        CoreTable core_table(index_table.GetTermsInCoreTableForm(), last_func.AsFunctionBool_WithUnknowValuesAs(0));
        tables.push_back(core_table.Print(var_names));
        core_table.GetCore();
        tables.push_back(core_table.Print(var_names));
        MinimizedManager manager(core_table.ReturnRest(), core_table.ReturnCore());
        tables.push_back(manager.GetCore());

        auto ret= (jobjectArray)env->NewObjectArray(6,env->FindClass("java/lang/String"),env->NewStringUTF(""));
        for(int i = 0; i < tables.size(); ++i) {
            env->SetObjectArrayElement(ret, i, env->NewStringUTF(tables[i].c_str()));
        }
        return ret;

    }
    catch(...)
    {
        return (jobjectArray)env->NewObjectArray(1,env->FindClass("java/lang/String"),env->NewStringUTF("error"));
    }
}
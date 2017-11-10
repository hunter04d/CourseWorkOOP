#include <jni.h>
#include <string>
#include "ShuntingYarder.h"
#include "MinimizedManager.h"
#include "CoreTable.h"
#include "IndexTable.h"
#include "FunctionBool.h"
#include "ParserException.h"


extern "C"
JNIEXPORT jstring JNICALL
Java_com_hunter04d_android_booleanminimizer_NativeLib_stringFromJNI(JNIEnv *env, jclass instance, jstring str)
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
        std::string out;
        out = manager.GetBest();
        return env->NewStringUTF(out.c_str());
    }
    catch(...)
    {
        return env->NewStringUTF("error");
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
        return env->NewObject(ParserResult, ctor, env->NewStringUTF(exception.what()), true);
    }



}
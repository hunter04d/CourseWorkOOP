#include <jni.h>
#include <string>
#include "MinimizedManager.h"
#include "CoreTable.h"
#include "IndexTable.h"
#include "FunctionBool.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hunter04d_android_booleanminimizer_NativeLib_stringFromJNI(JNIEnv *env, jobject instance, jstring str)
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
        auto b = MinimizedManager::Namefy(manager.GetBest());
        out += std::string(b[0].c_str()) + "\n" + std::string(b[1].c_str()) + "\n";
        return env->NewStringUTF(out.c_str());
    }
    catch(...)
    {
        return env->NewStringUTF("error");
    }
}
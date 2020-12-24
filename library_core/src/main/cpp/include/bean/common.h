//
// Created by #Suyghur, on 2020/12/23.
//

#ifndef FLYFUNGAMESDK_COMMON_H
#define FLYFUNGAMESDK_COMMON_H

#include <string>
#include <map>
#include <memory>

using namespace std;

class Common {
public:

    map<string, string> paramsMap;

    static Common &get_instance() {
        static Common instance;
        return instance;
    }

private:
    Common() {}

    Common(const Common &other);

};

#endif //FLYFUNGAMESDK_COMMON_H

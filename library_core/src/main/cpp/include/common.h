//
// Created by #Suyghur, on 2020/12/23.
//

#ifndef FLYFUNGAMESDK_KTX_COMMON_H
#define FLYFUNGAMESDK_KTX_COMMON_H

#include <string>
#include <map>
#include <memory>


class Common {
public:

    std::map<std::string, std::string> paramsMap;

    static Common &getInstance() {
        static Common instance;
        return instance;
    }

private:
    Common() {}

    Common(const Common &other);

};

#endif //FLYFUNGAMESDK_KTX_COMMON_H

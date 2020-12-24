//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_BASE64_H
#define FLYFUNGAMESDK_BASE64_H

#include <string>

using namespace std;

string Base64Encode(unsigned char const *, unsigned int len);

string Base64Decode(string const &enc);

#endif //FLYFUNGAMESDK_BASE64_H

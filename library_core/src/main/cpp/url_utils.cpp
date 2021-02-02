//
// Created by #Suyghur, on 2020/12/18.
//

#include "include/url_utils.h"

inline unsigned char toHex(unsigned char c) {
    return c > 9 ? c + 55 : c + 48;
}

unsigned char fromHex(unsigned char in) {
    unsigned char out;
    if (in >= 'A' && in <= 'Z') {
        out = in - 'A' + 10;
    } else if (in >= 'a' && in <= 'z') {
        out = in - 'a' + 10;
    } else if (in >= '0' && in <= '9') {
        out = in - '0';
    } else {
        assert(0);
    }
    return out;
}


std::string urlEncode(const std::string &str) {
    std::string result;
    size_t len = str.length();
    for (size_t i = 0; i < len; i++) {
        if (isalnum((unsigned char) str[i]) || (str[i] == '-') || (str[i] == '_') ||
            (str[i] == '.') || (str[i] == '~')) {
            result += str[i];
        } else if (str[i] == ' ') {
            result += '+';
        } else {
            result += '%';
            result += toHex((unsigned char) str[i] >> 4);
            result += toHex((unsigned char) str[i] % 16);
        }
    }
    return result;
}

std::string urlDecode(const std::string &str) {
    std::string result;
    size_t len = str.length();
    for (size_t i = 0; i < len; i++) {
        if (str[i] == '+') result += ' ';
        else if (str[i] == '%') {
            assert(i + 2 < len);
            unsigned char high = fromHex((unsigned char) str[++i]);
            unsigned char low = fromHex((unsigned char) str[++i]);
            result += high * 16 + low;
        } else result += str[i];
    }
    return result;
}
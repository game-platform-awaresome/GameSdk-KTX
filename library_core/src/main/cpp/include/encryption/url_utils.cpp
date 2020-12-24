//
// Created by #Suyghur, on 2020/12/18.
//

#include "url_utils.h"

inline unsigned char to_hex(unsigned char c) {
    return c > 9 ? c + 55 : c + 48;
}

unsigned char from_hex(unsigned char in) {
    unsigned char _out;
    if (in >= 'A' && in <= 'Z') {
        _out = in - 'A' + 10;
    } else if (in >= 'a' && in <= 'z') {
        _out = in - 'a' + 10;
    } else if (in >= '0' && in <= '9') {
        _out = in - '0';
    } else {
        assert(0);
    }
    return _out;
}


string url_encode(const string &str) {
    string _result;
    size_t _len = str.length();
    for (size_t i = 0; i < _len; i++) {
        if (isalnum((unsigned char) str[i]) || (str[i] == '-') || (str[i] == '_') ||
            (str[i] == '.') || (str[i] == '~')) {
            _result += str[i];
        } else if (str[i] == ' ') {
            _result += '+';
        } else {
            _result += '%';
            _result += to_hex((unsigned char) str[i] >> 4);
            _result += to_hex((unsigned char) str[i] % 16);
        }
    }
    return _result;
}

string url_decode(const string &str) {
    string _result;
    size_t _len = str.length();
    for (size_t i = 0; i < _len; i++) {
        if (str[i] == '+') _result += ' ';
        else if (str[i] == '%') {
            assert(i + 2 < _len);
            unsigned char _high = from_hex((unsigned char) str[++i]);
            unsigned char _low = from_hex((unsigned char) str[++i]);
            _result += _high * 16 + _low;
        } else _result += str[i];
    }
    return _result;
}
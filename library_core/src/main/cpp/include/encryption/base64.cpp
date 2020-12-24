//
// Created by #Suyghur, on 2020/12/18.
//

#include "base64.h"

static const string BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

static inline bool IsBase64(unsigned char c) {
    return (isalnum(c) || (c == '+') || (c == '/'));
}

string Base64Encode(unsigned char const *raw, unsigned int len) {
    string result;
    int i = 0;
    int j = 0;
    unsigned char array3[3];
    unsigned char array4[4];

    while (len--) {
        array3[i++] = *(raw++);
        if (i == 3) {
            array4[0] = (array3[0] & 0xfc) >> 2;
            array4[1] = ((array3[0] & 0x03) << 4) + ((array3[1] & 0xf0) >> 4);
            array4[2] = ((array3[1] & 0x0f) << 2) + ((array3[2] & 0xc0) >> 6);
            array4[3] = array3[2] & 0x3f;

            for (i = 0; i < 4; i++) {
                result += BASE64_CHARS[array4[i]];
            }
            i = 0;
        }
    }

    if (i) {
        for (j = 0; j < 3; j++) {
            array3[j] = '\0';
        }

        array4[0] = (array3[0] & 0xfc) >> 2;
        array4[1] = ((array3[0] & 0x03) << 4) + ((array3[1] & 0xf0) >> 4);
        array4[2] = ((array3[1] & 0x0f) << 2) + ((array3[2] & 0xc0) >> 6);
        array4[3] = array3[2] & 0x3f;

        for (j = 0; (j < i + 1); j++) {
            result += BASE64_CHARS[array4[j]];
        }

        while ((i++ < 3)) {
            result += '=';
        }
    }
    return result;
}

string Base64Decode(string const &enc) {
    int len = enc.size();
    int i = 0;
    int j = 0;
    int input = 0;
    unsigned char array3[3], array4[4];
    string result;

    while (len-- && (enc[input] != '=') && IsBase64(enc[input])) {
        array4[i++] = enc[input];
        input++;
        if (i == 4) {
            for (i = 0; i < 4; i++) {
                array4[i] = BASE64_CHARS.find(array4[i]);
            }

            array3[0] = (array4[0] << 2) + ((array4[1] & 0x30) >> 4);
            array3[1] = ((array4[1] & 0xf) << 4) + ((array4[2] & 0x3c) >> 2);
            array3[2] = ((array4[2] & 0x3) << 6) + array4[3];

            for (i = 0; i < 3; i++) {
                result += array3[i];
            }
            i = 0;
        }
    }

    if (i) {
        for (j = i; j < 4; j++) {
            array4[j] = 0;
        }
        for (j = 0; j < 4; j++) {
            array4[j] = BASE64_CHARS.find(array4[j]);
        }

        array3[0] = (array4[0] << 2) + ((array4[1] & 0x30) >> 4);
        array3[1] = ((array4[1] & 0xf) << 4) + ((array4[2] & 0x3c) >> 2);
        array3[2] = ((array4[2] & 0x3) << 6) + array4[3];

        for (j = 0; (j < i - 1); j++) {
            result += array3[j];
        }
    }
    return result;
}
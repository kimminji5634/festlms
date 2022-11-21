package com.zerobase.fastlms.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    // 두 값을 비교하는 메서드
    public static boolean equals(String plaintext, String hashed) { /*checkpw 들어가서 파라미터값 적기*/

        if (plaintext == null || plaintext.length() < 1) {
            return false;
        }

        if (hashed == null || hashed.length() < 1) {
            return false;
        }
            // 아래의 두 값이 같으면 true, 다르면 false
            return BCrypt.checkpw(plaintext, hashed);
    }

    // 비밀번호 값을 주는 메서드
    public static String encPassword(String plaintext) {
        if (plaintext == null || plaintext.length() < 1) {
            return "";
        }
        return BCrypt.hashpw(plaintext, BCrypt.gensalt());
    }
}

package org.springframework.security.crypto.password;

import org.springframework.security.crypto.password.HackedDigester;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.Digester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by daka on 09/08/16.
 */
public class HackedStandardPasswordEncoder implements PasswordEncoder {


    private final HackedDigester digester;
    private final byte[] secret;
    private final BytesKeyGenerator saltGenerator;
    private static final int DEFAULT_ITERATIONS = 1024;

    public HackedStandardPasswordEncoder() {
        this("");
    }

    public HackedStandardPasswordEncoder(CharSequence secret) {
        this("SHA-256", secret);
    }

    public String encode(CharSequence rawPassword) {
        byte[] salt = this.saltGenerator.generateKey();
        return this.encode(rawPassword, salt);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = this.decode(encodedPassword);
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return this.matches(digested, this.digest(rawPassword, salt));
    }

    private HackedStandardPasswordEncoder(String algorithm, CharSequence secret) {
        this.digester = new HackedDigester(algorithm, 1);
        this.secret = Utf8.encode(secret);
        this.saltGenerator = KeyGenerators.secureRandom();
    }

    private String encode(CharSequence rawPassword, byte[] salt) {
        byte[] digest = this.digest(rawPassword, salt);
        return new String(Hex.encode(digest));
    }

    private byte[] digest(CharSequence rawPassword, byte[] salt) {
        System.out.println( "Hex salt: "+ new String(Hex.encode(salt)));
        System.out.println( "Hex password: "+ new String(Hex.encode(Utf8.encode(rawPassword))));
        System.out.println( "Hex secret: "+ new String(Hex.encode(this.secret)));
        byte[] concat = EncodingUtils.concatenate(new byte[][]{salt, this.secret, Utf8.encode(rawPassword)});
        System.out.println( "Concatenated salt+password+secret: "+ new String(Hex.encode(concat)));
        byte[] digest = this.digester.digest(concat);
        return EncodingUtils.concatenate(new byte[][]{salt, digest});
    }

    private byte[] decode(CharSequence encodedPassword) {
        return Hex.decode(encodedPassword);
    }

    private boolean matches(byte[] expected, byte[] actual) {
        if(expected.length != actual.length) {
            return false;
        } else {
            int result = 0;

            for(int i = 0; i < expected.length; ++i) {
                result |= expected[i] ^ actual[i];
            }

            return result == 0;
        }
    }
}

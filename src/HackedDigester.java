package org.springframework.security.crypto.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.security.crypto.codec.Hex;


final class HackedDigester {
    private final MessageDigest messageDigest;
    private final int iterations;

    public HackedDigester(String algorithm, int iterations) {
        try {
            this.messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var4) {
            throw new IllegalStateException("No such hashing algorithm", var4);
        }

        this.iterations = iterations;
    }

    public byte[] digest(byte[] value) {
        System.out.println("JAVA about to digest this: "+ new String (Hex.encode(value)));
        MessageDigest var2 = this.messageDigest;
        synchronized(this.messageDigest) {
            for(int i = 0; i < this.iterations; ++i) {
                value = this.messageDigest.digest(value);
                System.out.println("JAVA digest: "+ new String (Hex.encode(value)) + " iteration: " + (i+1));
            }

            return value;
        }
    }
}

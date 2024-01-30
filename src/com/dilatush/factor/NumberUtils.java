package com.dilatush.factor;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class NumberUtils {

    private final Random random;


    public NumberUtils() {
        random  = new SecureRandom();
    }


    public BigInteger getRandomPrime( final int _bits ) {
        return BigInteger.probablePrime( _bits, random );
    }


    public Integer getNum( final String _str ) {

        try {
            int ans = Integer.parseInt( _str );
            return ans >= 0 ? ans : null;
        }
        catch( NumberFormatException _e ) {
            return null;
        }
    }

}

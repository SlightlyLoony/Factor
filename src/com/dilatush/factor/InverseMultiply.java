package com.dilatush.factor;

import java.math.BigInteger;

public class InverseMultiply {


    final private BigInteger   target;
    final private int          bits;
    final private int[]        carries;
    final private BigInteger[] subproducts;
    final private BigInteger   product;


    public InverseMultiply( final BigInteger _target ) {

        if( _target == null ) throw new IllegalArgumentException( "Missing product" );

        // set up our instance...
        target             = _target;
        bits               = target.bitLength();
        int bitsWithSpares = bits + (bits >>> 3);
        carries            = new int[bitsWithSpares];
        subproducts        = new BigInteger[bitsWithSpares];
        product            = BigInteger.ZERO;
    }


    public void factor() {

    }
}

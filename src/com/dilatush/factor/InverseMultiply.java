package com.dilatush.factor;

import java.math.BigInteger;
import java.util.Arrays;

public class InverseMultiply {

    final private FactorBitArray target;
    final private int            size;
    final private int            sizeWithSpares;
    final private int[]          carries;
    final private FactorBitArray product;
    final private FactorBitArray operandA;
    final private FactorBitArray operandB;


    public InverseMultiply( final BigInteger _target ) {

        // sanity checks...
        if( _target == null ) throw new IllegalArgumentException( "Missing target product" );

        // set up our instance...
        target             = new FactorBitArray( _target );
        size               = target.size();
        sizeWithSpares     = size + Math.max( 32, size >>> 4 );
        carries            = new int[ sizeWithSpares ];
        product            = new FactorBitArray( sizeWithSpares );
        operandA           = new FactorBitArray( sizeWithSpares );
        operandB           = new FactorBitArray( sizeWithSpares );
    }


    public void factor() {

        operandA.set( 5, 1 );
        operandA.set( 4, 1 );
        operandA.set( 3, 1 );
        operandA.set( 1, 1 );
        operandA.set( 0, 1 );
        operandB.set( 5, 1 );
        operandB.set( 4, 1 );
        operandB.set( 3, 1 );
        operandB.set( 2, 1 );
        operandB.set( 0, 1 );
        multiply( 0 );

        BigInteger a = operandA.toBigInteger();
        BigInteger b = operandB.toBigInteger();
        BigInteger p = product.toBigInteger();
        BigInteger x = new BigInteger( "99999999999999999999999999999999999999999999999999999999999999999999999999999" );
        FactorBitArray y = new FactorBitArray( x );
        BigInteger z = y.toBigInteger();

        // make a crude guess at the value of our factors...
        int bitPos = 0;
        while( product.highestOnePos() < target.highestOnePos() ) {

            int sp = subProduct( bitPos );

            multiply( 0 );  // 21 * 18 very wrong

            bitPos++;
        }

        product.hashCode();
    }


    private int bitProduct( final int _posA, final int _posB ) {
        return operandA.get( _posA ) & operandB.get( _posB );
    }


    private int subProduct( final int _posB ) {
        int subProduct = carries[_posB];
        for( int i = 0; i <= _posB; i++ ) {
            subProduct += bitProduct( i, _posB - i );
        }
        return subProduct;
    }


    private int interiorSubProduct( final int _posB ) {
        if( _posB < 2 ) return 0;
        int subProduct = carries[_posB];
        for( int i = 2; i <= _posB; i++ ) {
            subProduct += bitProduct( i, _posB - i );
        }
        return subProduct;
    }


    private void multiply( final int _startBit ) {

        Arrays.fill( carries, _startBit + 1, sizeWithSpares, 0 );
        product.clear( _startBit, sizeWithSpares );
        for( int i = _startBit; i < sizeWithSpares - 1; i++ ) {
            int sp = subProduct( i );
            carries[i+1] = sp >>> 1;
            product.set( i, sp & 1 );
        }
    }


    static public void main( final String[] _args ) {
        InverseMultiply im = new InverseMultiply( new BigInteger( "3599" ) );
        im.factor();

        im.hashCode();
    }
}

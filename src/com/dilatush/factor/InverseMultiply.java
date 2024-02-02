package com.dilatush.factor;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

public class InverseMultiply {


    final private BitSet target;
    final private int    bits;
    final private int    bitsWithSpares;
    final private int[]  carries;
    final private BitSet product;
    private BitSet       operandA;
    private BitSet       operandB;


    public InverseMultiply( final BigInteger _target ) {

        if( _target == null ) throw new IllegalArgumentException( "Missing product" );

        // set up our instance...
        target             = BitSet.valueOf( _target.toByteArray() );
        bits               = target.length();
        bitsWithSpares     = bits + (bits >>> 3);
        carries            = new int[bitsWithSpares];
        product            = new BitSet( bitsWithSpares );
        operandA           = new BitSet( bitsWithSpares );
        operandB           = new BitSet( bitsWithSpares );
    }


    public void factor() {

    }


    private int subProduct( final int _posA, final int _posB ) {
        return (operandA.get( _posA ) && operandB.get( _posB )) ? 1 : 0;
    }


    private int subProduct( final int _posB ) {
        int subProduct = carries[_posB];
        for( int i = 0; i <= _posB; i++ ) {
            subProduct += subProduct( i, _posB - i );
        }
        return subProduct;
    }


    private void multiply( final int _startBit ) {

        Arrays.fill( carries, _startBit + 1, bitsWithSpares, 0 );
        product.clear( _startBit, bitsWithSpares );
        for( int i = _startBit; i < bitsWithSpares - 1; i++ ) {
            int sp = subProduct( i );
            carries[i+1] = sp >>> 1;
            product.set( i, (sp & 1) != 0 );
        }
    }


    static public void main( final String[] _args ) {
        InverseMultiply im = new InverseMultiply( new BigInteger( "3599" ) );
        im.operandA = BitSet.valueOf( new BigInteger( "61" ).toByteArray() );
        im.operandB = BitSet.valueOf( new BigInteger( "59" ).toByteArray() );
        im.multiply( 0 );

        im.hashCode();
    }
}

package com.dilatush.factor;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

public class InverseMultiply {

    // table providing bit position "guesses" based on subproduct parity (s), target parity (t),
    // bit position zero (z), and the bit position parity (p)...
    @SuppressWarnings( "SpellCheckingInspection" )
    final static int[][] GUESSES = new int[][] {
            // zspt      a  b...
            /* 0000 */ { 1, 1 },
            /* 0001 */ { 1, 0 },
            /* 0010 */ { 0, 0 },
            /* 0011 */ { 0, 1 },
            /* 0100 */ { 1, 0 },
            /* 0101 */ { 1, 1 },
            /* 0110 */ { 0, 1 },
            /* 0111 */ { 0, 0 },
            /* 1000 */ { 1, 0 },
            /* 1001 */ { 1, 1 },
            /* 1010 */ { 0, 1 },
            /* 1011 */ { 1, 1 }
    };

    final private BitSet target;
    final private int    bits;
    final private int    bitsWithSpares;
    final private int[]  carries;
    final private BitSet product;
    final private BitSet operandA;
    final private BitSet operandB;


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

        // make a crude guess at the value of our factors...
        int bitPos = 0;
        while( product.length() < target.length() ) {

            int sp = subProduct( bitPos );

            int guessIndex = ((bitPos == 0) ? 1 : 0);
            guessIndex = (guessIndex << 1) + (sp & 1);
            guessIndex = (guessIndex << 1) + (bitPos & 1);
            guessIndex = (guessIndex << 1) + (target.get( bitPos ) ? 1 : 0);

            operandA.set( bitPos, GUESSES[guessIndex][0] == 1 );
            operandB.set( bitPos, GUESSES[guessIndex][1] == 1 );

            multiply( 0 );  // 21 * 18 very wrong

            bitPos++;
        }

        product.hashCode();
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
        im.factor();

        im.hashCode();
    }
}

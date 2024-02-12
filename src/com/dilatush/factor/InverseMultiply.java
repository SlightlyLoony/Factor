package com.dilatush.factor;

import java.math.BigInteger;
import java.util.Arrays;

public class InverseMultiply {

    final private FactorBitArray target;
    final private int            size;
    final private int            sizeWithSpares;
    final private int[]          carries;
    final private FactorBitArray product;
    private FactorBitArray operandA;
    private FactorBitArray operandB;


    private BigInteger factor1;
    private BigInteger factor2;


    public InverseMultiply( final BigInteger _target ) {

        // sanity checks...
        if( _target == null ) throw new IllegalArgumentException( "Missing _target" );
        if( _target.compareTo( BigInteger.ZERO ) <= 0 ) throw new IllegalArgumentException( "_target must be >= 1" );

        // set up our instance...
        target             = new FactorBitArray( _target );
        size               = target.size();
        sizeWithSpares     = size + Math.max( 32, size >>> 4 );
        carries            = new int[ sizeWithSpares ];
        product            = new FactorBitArray( sizeWithSpares );
        operandA           = new FactorBitArray( sizeWithSpares );
        operandB           = new FactorBitArray( sizeWithSpares );
    }


    /**
     * Attempt to factor the target product in this instance.  If successful, returns {@code true} and the two factors are available through the getters.  Otherwise,
     * returns {@code false}.
     *
     * @return Returns {@code true} if factoring was successful.
     */
    public boolean factor() {


        // if our target is 1, return the trivial answer...
        if( target.highestOnePos() == 0 ) {
            factor1 = BigInteger.ONE;
            factor2 = BigInteger.ONE;
            return true;
        }


        // if our target is even, return the trivial answer...
        if( target.get( 0 ) == 0 ) {
            factor1 = BigInteger.TWO;
            factor2 = target.toBigInteger().shiftRight( 1 );
            return true;
        }

        // make a crude guess at the value of our factors...
        int thb = target.highestOnePos();
        int bitPos = 0;
//        while( product.highestOnePos() < thb ) {
//
//            if( bitPos == 0 ) {
//                if( target.get( 0 ) == 1 ) {
//                    operandA.set( 0, 1 );
//                    operandB.set( 0, 1 );
//                }
//                else {
//                    operandA.set( 0, 0 );
//                    operandB.set( 0, 1 );
//                }
//                bitPos++;
//                continue;
//            }
//
//            var highA = (0 == (bitPos & 1));
//            var isp = interiorSubProduct( bitPos );
//            var trgB = target.get( bitPos );
//
//            if( highA ) {
//                if( ((isp & 1) & trgB) == 1 ) {
//                    operandA.set( bitPos, 1 );
//                    operandB.set( bitPos, 1 );
//                }
//                else if( ((isp & 1) | trgB) == 1 ) {
//                    operandA.set( bitPos, 1 );
//                    operandB.set( bitPos, 0 );
//                }
//                else {
//                    operandA.set( bitPos, 0 );
//                    operandB.set( bitPos, 0 );
//                }
//            }
            operandA = new FactorBitArray( new BigInteger( "61" ), 44 );
            operandB = new FactorBitArray( new BigInteger( "59" ), 44 );
            multiply( 0 );

            bitPos++;
//        }

        return true;
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


    public BigInteger factor1() {

        return factor1;
    }


    public BigInteger factor2() {

        return factor2;
    }


    /**
     * Multiply {@code operandA} with {@code operandB}, starting at the given bit position.  The product is in {@code product}.
     *
     * @param _startBit The starting bit position to multiply.
     */
    private void multiply( final int _startBit ) {

        // clear any carries from previous operation...
        Arrays.fill( carries, _startBit + 1, sizeWithSpares, 0 );

        // clear any product bits from previous operations...
        product.clear( _startBit, sizeWithSpares );

        // iterate until there's no point...
        var bitPos = _startBit;
        var maxBit = operandA.highestOnePos() + operandB.highestOnePos();
        do {
            // get the subproduct for this bit position...
            int sp = subProduct( bitPos );

            // propagate any carry...
            carries[bitPos + 1] = sp >>> 1;

            // set the product bit to 0 or 1...
            product.set( bitPos, sp & 1 );

            // move to the next bit...
            bitPos++;

        // while there are bits to multiply or carries to propagate...
        } while( (bitPos <= maxBit) || (carries[bitPos] > 0) );
    }


    static public void main( final String[] _args ) {
        InverseMultiply im = new InverseMultiply( new BigInteger( "3599" ) );
        im.factor();

        im.hashCode();
    }
}

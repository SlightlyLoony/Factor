package com.dilatush.factor;


import java.math.BigInteger;

/**
 * <p>
 *     Allows an arbitrarily large binary non-negative integer number to be treated as an array of bits.  Instances provide methods for manipulating individual
 *     bits or ranges of bits, and for conversion to and from numbers.
 * </p><p>
 *     Instances of this class are mutable and <i>not</i> threadsafe.
 * </p>
 */
public class FactorBitArray {

    final private long[] bits;  // the array of bits contained in this instance, stored as little-endian and treated as unsigned...
    final private int    size;  // the number of bits in this array...


    /**
     * Creates a new instance of this class with a bit array set to the bits in the given value interpreted as an unsigned long.
     *
     * @param _value The unsigned long value providing the bits for this class.
     */
    public FactorBitArray( final long _value ) {
        size = 64 - Long.numberOfLeadingZeros( _value );
        bits = new long[] { _value };
    }


    /**
     * Creates a new instance of this class with a bit array set to the bits in the given value.
     *
     * @param _value The value providing the bits for this class.
     */
    public FactorBitArray( final BigInteger _value ) {

        size = _value.bitLength();

        // note that these bytes are big-endian order whereas the bits array of longs is in little-endian order...
        byte[] bytes = _value.toByteArray();

        // we stuff 8 bytes into each long...
        int longsLen = Math.max( 1, (1 + bytes.length) >>> 3 );
        bits = new long[ longsLen ];

        // march through our bytes, copying them to the correct place in our array of longs...
        for( int bytesCnt = 0; bytesCnt < bytes.length; bytesCnt++ ) {

            // invert the bytes index to handle the big-endian to little-endian change...
            int bytesInd = bytes.length - (1 + bytesCnt);

            // the longs index is just the uninverted bytes index divided by 8...
            int longsInd = bytesCnt >>> 3;

            // this index is the bit offset of a given byte within a long...
            int shiftCnt = (bytesCnt & 7) << 3;

            // finally we use these indices to copy one byte from the BigInteger to our new instance...
            bits[ longsInd ] |= (long) (bytes[ bytesInd ] & 0xff) << shiftCnt;
        }
    }


    /**
     * Returns the current value (0 or 1) of the bit in this set at the given bit position.
     *
     * @param _bitPos The position of the bit to return, which must be in the range [0..(size - 1)].
     * @return The value (0 or 1) of the bit in this set at the given bit position.
     */
    public int get( final int _bitPos ) {

        // sanity checks...
        if( (_bitPos < 0) || (_bitPos >= size) ) throw new IllegalArgumentException( "_bitPos must be non-negative and less than " + size + ", was: " + _bitPos );

        // compute our indices...
        int longsInd = _bitPos >>> 6;
        int bitsInd = _bitPos & 0x3f;

        return (longsInd >>> bitsInd) & 1;
    }


    /**
     * Sets the bit in this set at the given bit position to the given value (0 or 1).  Returns the value (0 or 1) of the bit in this set at the given bit
     * position before this method was called.
     *
     * @param _bitPos The position of the bit to set and return, which must be in the range [0..(size - 1)].
     * @param _value The value (0 or 1) to assign to the bit in this set at the given bit position.
     * @return The value (0 or 1) of the bit in this set at the given bit position before this method was called.
     */
    public int set( final int _bitPos, final int _value ) {

        // fetch the current value, so we can return it...
        int retVal = get( _bitPos );

        // sanity checks...
        if( (_value < 0) || (_value > 1) ) throw new IllegalArgumentException( "_value must be zero or one, was: " + _value );

        // compute our indices...
        int longsInd = _bitPos >>> 6;
        int bitsInd = _bitPos & 0x3f;

        // stuff our new value...
        bits[ longsInd ] =
                (_value == 0) ?
                        retVal & ~(1L << _bitPos) :
                        retVal | (1L << _bitPos);

        return retVal;
    }


    static public void main( final String[] _args) {

        FactorBitArray fba = new FactorBitArray( new BigInteger( "3599" ) );
        int sb1 = fba.get( 2 );
        int sb0 = fba.get( 5 );

        fba.hashCode();
    }
}

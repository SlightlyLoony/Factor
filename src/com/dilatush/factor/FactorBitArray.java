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

    private int          ms1;  // bit position of the most significant bit set to a 1...


    /**
     * Creates a new instance of this class with a bit array set to the bits in the given value interpreted as an unsigned long.
     *
     * @param _value The unsigned long value providing the bits for this class.
     */
    public FactorBitArray( final long _value ) {
        size = 64 - Long.numberOfLeadingZeros( _value );
        bits = new long[] { _value };
        ms1 = size - 1;
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
        ms1 = size - 1;
    }


    public int get( final int _bitPos ) {
        return (int)((bits[ _bitPos >>> 6 ] >>> (_bitPos & 0x3f)) & 1);
    }


    static public void main( final String[] _args) {

        FactorBitArray fba = new FactorBitArray( new BigInteger( "3599" ) );
        int sb1 = fba.get( 2 );
        int sb0 = fba.get( 5 );

        fba.hashCode();
    }
}

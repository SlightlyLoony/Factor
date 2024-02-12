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

    final public static int MAXSIZE = 10000;

    final private long[] bits;  // the array of bits contained in this instance, stored as little-endian and treated as unsigned...
    final private int    size;  // the number of bits in this array...


    /**
     * Creates a new instance of this class with a bit array of the given size with all the bits set to zero.
     *
     * @param _bits The number of bits for this instance, which must be in the range [1..MAXSIZE].
     */
    @SuppressWarnings( "unused" )
    public FactorBitArray( final int _bits ) {

        // sanity checks...
        if( (_bits < 1) || (_bits > MAXSIZE) ) throw new IllegalArgumentException( "_bits out of range: " + _bits );

        // initialize our instance...
        size = _bits;
        bits = new long[ 1 + ((size - 1) >>> 6) ];
    }


    /**
     * Creates a new instance of this class with a bit array set to the bits in the given value.
     *
     * @param _value The value providing the bits for this class.
     */
    public FactorBitArray( final BigInteger _value ) {
        this( _value, 0 );
    }


    /**
     * Creates a new instance of this class with a bit array set to the bits in the given value.
     *
     * @param _value The value providing the bits for this class.
     * @param _minSize The minimum number of bits to allocate.
     */
    public FactorBitArray( final BigInteger _value, final int _minSize ) {

        // sanity checks...
        if( _value == null ) throw new IllegalArgumentException( "_value is missing" );
        if( _value.signum() < 0 ) throw new IllegalArgumentException( "_value is negative" );

        // set the size of this array, ensuring that it's long enough to contain our given number...
        size = Math.max( _minSize, _value.bitLength() );

        // note that these bytes are big-endian order whereas the bits array of longs is in little-endian order...
        byte[] bytes = _value.toByteArray();

        // we stuff 8 bytes into each long...
        int longsLen = Math.max( 1, 1 + (size >>> 6) );
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
     * Returns the bit position of the most significant bit set in this bit array, or -1 if all bits are cleared (zeroes).
     *
     * @return The bit position of the most significant bit set in this bit array, or -1 if all bits are cleared (zeroes).
     */
    public int highestOnePos() {

        // march from most to least significant long, looking for set bits...
        for( int longsInd = bits.length - 1; longsInd >= 0; longsInd-- ) {
            int l0 = Long.numberOfLeadingZeros( bits[longsInd] );
            if( l0 != 64 )
                return (longsInd << 6) + (63 - l0);
        }
        return -1;
    }


    /**
     * Returns the current value (0 or 1) of the bit in this array at the given bit position.
     *
     * @param _bitPos The position of the bit to return, which must be in the range [0..(size - 1)].
     * @return The value (0 or 1) of the bit in this array at the given bit position.
     */
    public int get( final int _bitPos ) {

        // sanity checks...
        if( (_bitPos < 0) || (_bitPos >= size) ) throw new IllegalArgumentException( "_bitPos must be non-negative and less than " + size + ", was: " + _bitPos );

        // compute our indices...
        int longsInd = _bitPos >>> 6;
        int bitsInd = _bitPos & 0x3f;

        return (int) ((bits[longsInd] >>> bitsInd) & 1);
    }


    /**
     * Sets the bit in this array at the given bit position to the given value (0 or 1).  Returns the value (0 or 1) of the bit in this array at the given bit
     * position before this method was called.
     *
     * @param _bitPos The position of the bit to set and return, which must be in the range [0..(size - 1)].
     * @param _value The value (0 or 1) to assign to the bit in this array at the given bit position.
     * @return The value (0 or 1) of the bit in this array at the given bit position before this method was called.
     */
    @SuppressWarnings("UnusedReturnValue")
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
                        bits[ longsInd ] & ~(1L << bitsInd) :
                        bits[ longsInd ] | (1L << bitsInd);

        return retVal;
    }


    /**
     * Clears (sets to zero) all the bits in this set, starting with the bit at the given starting position, through the bit at the given ending position - 1.
     *
     * @param _startPos The bit position of the first bit in this array to clear.
     * @param _endPos The bit position + 1 of the last bit in this array to clear.
     */
    public void clear( final int _startPos, final int _endPos ) {

        // sanity checks...
        if( (_startPos < 0) || (_startPos > size) ) throw new IllegalArgumentException( "_startPos must be non-negative and less than or equal to " + size + ", was: " + _startPos );
        if( (_endPos < 0) || (_endPos > size) ) throw new IllegalArgumentException( "_endPos must be non-negative and less than or equal to " + size + ", was: " + _endPos );
        if( _endPos < _startPos ) throw new IllegalArgumentException( "_endPos must >= _startPos" );

        // some setup...
        int  startLongsInd = _startPos >>> 6;
        long startBitsMask = (1L << (_startPos & 0x3f)) - 1;
        int  endLongsInd   = (_endPos- 1) >>> 6;
        long endBitsMask   = -1L << (((_endPos- 1) & 0x3f) + 1);

        // now the actual clearing...
        for( int longsInd = startLongsInd; longsInd <= endLongsInd; longsInd++ ) {

            if( (longsInd == startLongsInd) && (longsInd == endLongsInd) )
                bits[longsInd] &= (startBitsMask | endBitsMask);
            else if( longsInd == startLongsInd )
                bits[longsInd] &= startBitsMask;  // 0=0;1=1;2=3
            else if( longsInd == endLongsInd )
                bits[longsInd] &= endBitsMask;  //0=~1;1=~3;2=~7;63=~
            else
                bits[longsInd] = 0;
        }
    }


    /**
     * Returns a {@link BigInteger} with the same bit pattern as this instance.
     *
     * @return A {@link BigInteger} with the same bit pattern as this instance.
     */
    public BigInteger toBigInteger() {

        // The bytes array we're going to build is a big-endian transposition of the little-endian array of longs in this instance, and to guarantee a positive
        // number in the result, we insert a leading zero byte.  Hence, the complicated index and shift math...
        byte[] bytes = new byte[2 + (highestOnePos() >>> 3)];

        // iterate over the bytes, MSB to LSB...
        for( int i = 0; i < (bytes.length - 1); i++ ) {

            // which long to look in for this byte...
            int longsInd  =  ((bytes.length - 2) - i) >>> 3;

            // how many bits to shift right to get the right byte as LSB...
            int byteShift = (((bytes.length - 2) - i) & 7) << 3;

            // stuff our byte away with the index offset by one to leave a leading 0 byte, guaranteeing a positive result...
            bytes[i + 1] |= (0xff & (bits[longsInd] >>> byteShift));
        }

        return new BigInteger( bytes );
    }


    /**
     * @return The size of this instance in bits.
     */
    public int size() {
        return size;
    }
}

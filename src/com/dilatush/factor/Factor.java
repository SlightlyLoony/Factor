package com.dilatush.factor;

import java.math.BigInteger;
import java.util.Base64;

public class Factor {

    /**
     * Main entry point for Factor program.
     *
     * @param _args Command line arguments.
     */
    static public void main( final String[] _args ) {

        // some setup...
        NumberUtils utils = new NumberUtils();

        // figure out we should do based on command line arguments...

        // parse the flags, if any...
        boolean generateFlag = false;
        boolean decimalFlag = false;
        if( (_args.length >= 1) && (_args[0].charAt( 0 ) == '-') ) {
            generateFlag = _args[0].contains( "g" );
            decimalFlag = _args[0].contains( "d" );
        }

        // if we're generating some test numbers...
        if( generateFlag ) {
            Integer bits;
            if( _args.length >= 2 ) {

                // get the number of bits for the product...
                bits = utils.getNum( _args[ 1 ] );
                if( (bits == null) || (bits < 16) || (bits > 10000 ) ) {
                    System.out.println( "Number of product bits is invalid; must be in the range [16..10000]: " + _args[ 1 ] );
                    System.exit( 1 );
                }

                // get the factors...
                int factor1bits = bits >>> 1;
                BigInteger factor1 = utils.getRandomPrime( factor1bits );
                int factor2bits = bits - factor1bits;
                BigInteger factor2 = utils.getRandomPrime( factor2bits );

                // get the product to factor...
                BigInteger product = factor1.multiply( factor2 );

                // output the strings...
                if( decimalFlag ) {
                    System.out.println( product );
                    System.out.println();
                    System.out.println( factor1 );
                    System.out.println();
                    System.out.println( factor2 );
                }

                else {
                    Base64.Encoder encoder = Base64.getEncoder();
                    System.out.println( encoder.encodeToString( product.toByteArray() ) );
                    System.out.println();
                    System.out.println( encoder.encodeToString( factor1.toByteArray() ) );
                    System.out.println();
                    System.out.println( encoder.encodeToString( factor2.toByteArray() ) );
                }

                // and we're done...
                System.exit( 0 );
            }

            else {
                System.out.println( "Generating a test number requires a second argument with the number of bits." );
                System.exit( 1 );
            }
        }

        // otherwise we're factoring...

        // read in our number to factor...
        BigInteger product = null;
        if( decimalFlag ) {
            if( _args.length >= 2 )
                product = new BigInteger( _args[ 1 ] );
            else {
                System.out.println( "Missing the second argument: the number to factor, in decimal." );
                System.exit( 1 );
            }
        }

        else {
            if( _args.length >= 1 ) {
                Base64.Decoder decoder = Base64.getDecoder();
                product = new BigInteger( decoder.decode( _args[0] ) );
            }
            else {
                System.out.println( "Missing the argument: the number to factor, in Base64 notation." );
                System.exit( 1 );
            }
        }

        // now we get down to business...
        InverseMultiply invMult = new InverseMultiply( product );
        invMult.factor();
    }
}

/*

419520287946460139

725897149

577933511

 */
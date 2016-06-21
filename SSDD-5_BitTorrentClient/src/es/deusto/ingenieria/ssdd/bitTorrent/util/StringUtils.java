package es.deusto.ingenieria.ssdd.bitTorrent.util;

import javax.xml.bind.DatatypeConverter;

public final class StringUtils {
	//Hexadecimal characters
	static final String HEX_DIGITS = "0123456789ABCDEF";
	
	/**
	 * Transforms an array of bytes to an URL encoded String.
	 * @param
	 * 			bytes byte[] the source array of bytes.
	 * @return
	 * 			the resulting String. 
	 */
	public static String toURLEncodedString(byte[] bytes ) {
        StringBuffer result = new StringBuffer(bytes.length*2);
        char c;
        
        for( int i = 0; i < bytes.length; i++ ) {
            c = (char) bytes[i];

            switch( c ) {
            	case '.':	
            	case '-':  
            	case '_':
            	case '~':
            		result.append(c);
            		break;
              default:
                if( (c >= 'a' && c <= 'z') || 
                	(c >= 'A' && c <= 'Z') || 
                	(c >= '0' && c <= '9') ) {
                    result.append(c);
                } else {
                    result.append('%');
                    result.append(HEX_DIGITS.charAt((c & 0xF0) >> 4 ));
                    result.append(HEX_DIGITS.charAt(c & 0x0F));
                }
            }
        }

        return result.toString();
    }

	/**
	 * Transforms an array of bytes to a binary Hexadecimal String.
	 * @param
	 * 			bytes byte[] the source array of bytes.
	 * @return
	 * 			the resulting String. 
	 */
	public static String toHexString(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}
}

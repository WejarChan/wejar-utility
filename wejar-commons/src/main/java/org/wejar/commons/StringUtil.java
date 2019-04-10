package org.wejar.commons;

public class StringUtil {

	public static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static String reverse(final String str) {
		if (str == null) {
			return null;
		}
		return new StringBuilder(str).reverse().toString();
	}
}
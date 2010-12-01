package pl.edu.agh.mobile.adhoccom;

import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;


public class TextUtils {
	/**
	 * Metoda przeklejona z netu :). Ułatwia formatowanie tekstu.
	 *
	 */
	public static CharSequence setSpanBetweenTokens(CharSequence text,
			String token, CharacterStyle... cs) {
		// Start and end refer to the points where the span will apply
		int tokenLen = token.length();
		int start = text.toString().indexOf(token) + tokenLen;
		int end = text.toString().indexOf(token, start);

		if (start > -1 && end > -1) {
			// Copy the spannable string to a mutable spannable string
			SpannableStringBuilder ssb = new SpannableStringBuilder(text);
			for (CharacterStyle c : cs)
				ssb.setSpan(c, start, end, 0);

			// Delete the tokens before and after the span
			ssb.delete(end, end + tokenLen);
			ssb.delete(start - tokenLen, start);

			text = ssb;
		}

		return text;
	}
}

package com.soogbad.soogbadcalendar;

public class CustomHyphenator {

    // Inserts a soft hyphen between every pair of letters in each word, except right after the first letter and right before the last one
    // Android then decides whether to break words using these soft hyphens, rather than using android's dictionaries, which is empty for Hebrew
    public static String markBreakPoints(String text) {
        StringBuilder result = new StringBuilder(text.length() * 2);
        int i = 0;
        while(i < text.length()) {
            if(!Character.isLetter(text.charAt(i))) {
                result.append(text.charAt(i++));
                continue;
            }
            int wordEnd = i;
            while(wordEnd < text.length() && Character.isLetter(text.charAt(wordEnd))) wordEnd++;
            for(int j = i; j < wordEnd; j++) {
                result.append(text.charAt(j));
                if(j > i && j < wordEnd - 2) result.append('\u00AD');
            }
            i = wordEnd;
        }
        return result.toString();
    }

}

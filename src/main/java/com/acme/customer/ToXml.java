package com.acme.customer;

public class ToXml {

    public static String toXml(String s) {

        StringBuffer str = new StringBuffer();
        int len = s != null ? s.length() : 0;

        for (int i = 0; i < len; i++) {
            char ch = s == null ? ' ' : s.charAt(i);
            switch (ch) {
                case '<':
                    str.append("&lt;");
                    break;
                case '>':
                    str.append("&gt;");
                    break;
                case '&':
                    str.append("&amp;");
                    break;
                case '"':
                    str.append("&quot;");
                    break;
                case '\'':
                    str.append("&apos;");
                    break;
                default:
                    str.append(ch);
            }

           
        }

        return str.toString();

    }

}

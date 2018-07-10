package com.example.t_tazhan.roomposition;

public class Constant {
    public static final String A = "78:02:F8:50:7B:6F";
    public static final String B = "50:8F:4C:F2:49:E3";
    public static final String C = "60:F6:77:A8:81:FC";
    public static final String D = "E4:46:DA:4A:F7:7B";
    public static final String E = "C0:EE:FB:EA:B2:65";
    public static final String F = "AC:C1:EE:5E:35:16";
    public static final String G = "C4:0B:CB:F4:52:87";
    public static final String H = "78:02:F8:EA:B2:65";

    public static String getBeacon(String beacon) {
        String value = beacon;
        switch (beacon) {
            case A :
                value = "A";
                break;
            case B :
                value = "B";
                break;
            case C :
                value = "C";
                break;
            case D :
                value = "D";
                break;
            case E :
                value = "E";
                break;
            case F :
                value = "F";
                break;
            case G :
                value = "G";
                break;
            case H :
                value = "H";
                break;
        }
        return value;
    }
}

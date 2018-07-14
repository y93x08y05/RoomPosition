package com.example.t_tazhan.roomposition.util;

public class Constant {
    public static final String A = "19:18:FC:06:F7:7C";
    public static final String B = "19:18:FC:06:F7:79";
    public static final String C = "19:18:FC:06:F7:76";
    public static final String D = "19:18:FC:06:F7:6E";
    public static final String E = "19:18:FC:06:F7:68";
    public static final String F = "19:18:FC:06:F7:53";
    public static final String G = "19:18:FC:06:F7:51";
    public static final String H = "19:18:FC:06:F7:15";
    public static String getBeacon(String beacon) {
        String value;
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
                //找不到G
            case H :
                value = "H";
                break;
            default:
                value = "mac";
        }
        return value;
    }
}

package com.advancedprogramming2.advancedprogramming2ex4;

 class Utils {

     /**
      * The isValidIPv4 function gets as parameters a String ip
      * and returns true if it is a valid IPv4 Address else it returns false.
      * @param ip String ip to check its validity.
      * @return true if the String ip from the parameters is a valid IPv4 Address
      * else false.
      */
     static boolean isValidIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String[] splittedIP = ip.split("\\.");
        if (splittedIP.length != 4) {
            return false;
        }
        for (String str : splittedIP) {
            try {
                int num = Integer.parseInt(str);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return !ip.endsWith(".");
    }

}

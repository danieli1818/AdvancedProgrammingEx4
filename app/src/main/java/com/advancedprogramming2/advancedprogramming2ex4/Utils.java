package com.advancedprogramming2.advancedprogramming2ex4;

 class Utils {

     /**
      * The isNotValidIPv4 function gets as parameters a String ip
      * and returns true if it isn't a valid IPv4 Address else it returns false.
      * @param ip String ip to check its validity.
      * @return true if the String ip from the parameters isn't a valid IPv4 Address
      * else false.
      */
     static boolean isNotValidIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return true;
        }
        String[] splittedIP = ip.split("\\.");
        if (splittedIP.length != 4) {
            return true;
        }
        for (String str : splittedIP) {
            try {
                int num = Integer.parseInt(str);
                if (num < 0 || num > 255) {
                    return true;
                }
            } catch (Exception e) {
                return true;
            }
        }
        return ip.endsWith(".");
    }

}

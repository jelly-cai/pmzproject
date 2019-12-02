package com.jellycai.test;

public class Rs232Utils {

    /**
     * 16进制重量转10进制
     * @param weight16
     * @return
     */
    public static int getWeight16To10(String weight16){
        return Integer.valueOf(weight16,16);
    }

    /**
     * 获得重量
     * @param data
     * @return
     */
    public static int getWeight(String data){
        int size = Integer.parseInt(data.substring(4,6));
        String weight16 = data.substring(6,6 + size * 2);
        return getWeight16To10(weight16);
    }

}

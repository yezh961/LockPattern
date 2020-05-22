package com.yezh.lockpattern.utils;


public class MethUtil {

    /**
     * 点到圆心的距离 是否小于半径
     *
     * @param movingX   目标点x坐标
     * @param movingY   目标点y坐标
     * @param dotRadius 半径
     * @param centerX   圆心x坐标
     * @param centerY   圆心y坐标
     * @return
     */
    public static boolean checkInRound(float movingX, float movingY, int dotRadius, int centerX, int centerY) {
        return (Math.sqrt((movingX - centerX) * (movingX - centerX) + (movingY - centerY) * (movingY - centerY)) < dotRadius);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
                + Math.abs(y1 - y2) * Math.abs(y1 - y2));
    }
}

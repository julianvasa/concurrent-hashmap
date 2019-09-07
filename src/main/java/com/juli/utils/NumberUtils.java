package com.juli.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {
    public static String roundAndScale(BigDecimal num){
        DecimalFormat f = new DecimalFormat("##0.00");
        return f.format(num.setScale(2, RoundingMode.HALF_UP));
    }
}

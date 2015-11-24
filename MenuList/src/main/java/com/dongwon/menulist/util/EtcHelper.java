package com.dongwon.menulist.util;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by dongwon on 2015-06-03.
 */
public class EtcHelper {

    public static String makePriceString(int value){
        return String.format("%s %,d", Currency.getInstance(Locale.KOREA).getSymbol(), value);
    }
}

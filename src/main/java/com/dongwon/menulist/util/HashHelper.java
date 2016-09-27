package com.dongwon.menulist.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dongwon on 2015-04-26.
 */
public class HashHelper {

    public static String md5Hex(InputStream inputStream){
        try {
            return new String(Hex.encodeHex(DigestUtils.md5(inputStream))).toUpperCase();
        } catch (IOException e) {
            TrackHelper.sendException(e);
            return "";
        }
    }
}

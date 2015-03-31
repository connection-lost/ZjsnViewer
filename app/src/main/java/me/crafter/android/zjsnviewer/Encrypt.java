package me.crafter.android.zjsnviewer;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * Created by Frank on 2015/3/31.
 */
public class Encrypt{

        public static String encrypt(){
            double dotNetTime = DockInfo.currentUnix() * 10000000 + 621355968000000000D;
            String unencrypted = "api/initData&t=" + dotNetTime;
            String encrypted = "";
            try {
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.update(unencrypted.getBytes(),0,unencrypted.length());
                encrypted = new BigInteger(1,m.digest()).toString(16);
            } catch (java.security.NoSuchAlgorithmException e) {
            }
            encrypted = unencrypted + "&e=" + encrypted;
            return encrypted;
        }

}

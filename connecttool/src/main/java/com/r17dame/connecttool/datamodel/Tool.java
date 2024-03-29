package com.r17dame.connecttool.datamodel;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.r17dame.connecttool.R;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Tool {

    //    Currency Code
//    Code	USD	TWD	CNY	JPY	KRW	VND	THB	MYR	SGD
//    key	1	2	4	8	16	32	64	128	256
    public static String getCurrencyCode(String _code) {

        int code = Integer.parseInt(_code);
        switch (code) {
            case 1:
                return "USD";
            case 2:
                return "TWD";
            case 4:
                return "CNY";
            case 8:
                return "JPY";
            case 16:
                return "KRW";
            case 32:
                return "VND";
            case 64:
                return "THB";
            case 128:
                return "MYR";
            case 256:
                return "SGD";
            default:
                return "_";
        }
    }

    ;

    public static String getTimestamp() {
        Date currentDate = new Date();
        System.out.println("Current Timestamp: " + currentDate.getTime() + " milliseconds");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateString = formatter.format(currentDate);
        return dateString + "Z";
    }


    public static String getxSignature(String data, String RSAstr) throws Exception {
        PrivateKey privateKey = Tool.stringtoprivatekey(RSAstr);

        //Let's sign our message
        String signature = Tool.sign(data, privateKey);

        return signature.replace("\n", "").replace("\r", "");
    }


    public static PrivateKey stringtoprivatekey(String privateKeyString) {
        try {
            if (privateKeyString.contains("-----BEGIN PRIVATE KEY-----") || privateKeyString.contains("-----END PRIVATE KEY-----"))
                privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
            if (privateKeyString.contains("-----BEGIN RSA PRIVATE KEY-----") || privateKeyString.contains("-----END RSA PRIVATE KEY-----"))
                privateKeyString = privateKeyString.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "");

            privateKeyString = privateKeyString.replaceAll("\\r|\\n", "");
            byte[] privateKeyDER = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                privateKeyDER = Base64.getDecoder().decode(privateKeyString);

                PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyDER);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);


                return privateKey;
            } else {
                privateKeyDER = android.util.Base64.decode(privateKeyString, android.util.Base64.DEFAULT);

                PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyDER);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
                PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

                return privateKey;
            }


        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sign(String plainText, PrivateKey privateKey) {
        try {
            Signature privateSignature = null;
            privateSignature = Signature.getInstance("SHA256withRSA");

            privateSignature.initSign(privateKey);
            privateSignature.update(plainText.getBytes(UTF_8));

            byte[] signature = privateSignature.sign();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(signature);
            } else {
                return android.util.Base64.encodeToString(signature, android.util.Base64.DEFAULT);
            }
            //return android.util.Base64.encodeToString(signature, Base64.CRLF);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static void RemoveAccessToken(SharedPreferences.Editor editor) {
        editor.putString(String.valueOf(R.string.expiresTs), "");
        editor.putString(String.valueOf(R.string.access_token), "");
        editor.putString(String.valueOf(R.string.refresh_token), "");
        editor.apply();
    }


}

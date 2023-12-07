package com.r17dame.connecttool.datamodel;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Tool {

    public static String getTimestamp() {
        Date currentDate = new Date();
        System.out.println("Current Timestamp: " + currentDate.getTime() + " milliseconds");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateString = formatter.format(currentDate);
        return dateString + "Z";
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getxSignature(String data,String RSAstr) throws Exception {
        PrivateKey privateKey = Tool.stringtoprivatekey(RSAstr);

        //Let's sign our message
        String signature = Tool.sign(data, privateKey);
        return signature;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PrivateKey stringtoprivatekey(String privateKeyString) {
        try {
            if (privateKeyString.contains("-----BEGIN PRIVATE KEY-----") || privateKeyString.contains("-----END PRIVATE KEY-----"))
                privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
            if (privateKeyString.contains("-----BEGIN RSA PRIVATE KEY-----") || privateKeyString.contains("-----END RSA PRIVATE KEY-----"))
                privateKeyString = privateKeyString.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "");

            privateKeyString = privateKeyString.replaceAll("\\r|\\n", "");
            byte[] privateKeyDER = new byte[0];
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            }else{
//                privateKeyDER = android.util.Base64.decode(privateKeyString, android.util.Base64.DEFAULT);
//            }
            privateKeyDER = Base64.getDecoder().decode(privateKeyString);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//        }else{
//            return android.util.Base64.encodeToString(signature, android.util.Base64.DEFAULT);
//        }
        return Base64.getEncoder().encodeToString(signature);
        //return android.util.Base64.encodeToString(signature, Base64.CRLF);
    }


}

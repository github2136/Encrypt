package com.github2136.encrypt;

import android.support.annotation.StringDef;
import android.util.Log;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对称加密
 * Created by yubin on 2018/1/12.<br>
 * ECB不可添加向量IV<br>
 * DES/DESede IV长度为8字节<br>
 * AES IV长度为16字节
 * DES key长度为8字节<br>
 * DESede key长度为16字节或24字节<br>
 * AES key长度为16字节、24字节或32字节<br>
 * RSA 生成的key至少1024位
 */

public class EncryptUtil {
    public static final String DES = "DES";//64位(bit)
    public static final String DESede = "DESede";//128位(bit)192位(bit)
    public static final String AES = "AES";//128位(bit)192位(bit)
    public static final String RSA = "RSA";//1024+
    //如果添加了mode就必须添加padding
    public static final String MODE_CBC = "/CBC";//DES、DESede、AES
    public static final String MODE_CFB = "/CFB";//DES、DESede、AES
    public static final String MODE_CTR = "/CTR";//DES、DESede、AES
    public static final String MODE_CTS = "/CTS";//DES、DESede、AES
    public static final String MODE_ECB = "/ECB";//DES、DESede、AES、RSA
    public static final String MODE_OFB = "/OFB";//DES、DESede、AES
    public static final String MODE_NONE = "/NONE";//RSA

    public static final String PADDING_PKCS5 = "/PKCS5Padding";//DES、DESede、AES
    //如果加密内容不是8字节的整数倍就会报错
    public static final String PADDING_NO = "/NoPadding";//DES、DESede、AES、RSA
    public static final String PADDING_ISO10126 = "/ISO10126Padding";//DES、DESede、AES
    public static final String PADDING_OAEP = "/OAEPPadding";//RSA
    public static final String PADDING_OAEP_SHA_1 = "/OAEPwithSHA-1andMGF1Padding";//RSA
    public static final String PADDING_OAEP_SHA_256 = "/OAEPwithSHA-256andMGF1Padding";//RSA
    public static final String PADDING_PKCS1 = "/PKCS1Padding"; //RSA

    //加密类型
    private String mEncryptType = null;
    //加密key
    private byte[] mKey = null;
    //加密内容
    private byte[] mData = null;
    //加密向量
    private byte[] mIV = null;
    //填充模式
    private String mPadding = null;
    //模式
    private String mMode = null;

    @StringDef({DES, DESede, AES, RSA})
    @interface EncryptType {}

    public static EncryptUtil getInstance(@EncryptType String encrypt) {
        return new EncryptUtil(encrypt);
    }

    public static EncryptUtil getInstance() {
        return new EncryptUtil();
    }

    private EncryptUtil() { }

    private EncryptUtil(String encrypt) {
        mEncryptType = encrypt;
    }

    public EncryptUtil setType(@EncryptType String encrypt) {
        mEncryptType = encrypt;
        return this;
    }

    /**
     * 设置key
     */
    public EncryptUtil setKey(byte[] key) {
        this.mKey = key;
        return this;
    }

    @StringDef({MODE_CBC, MODE_CFB, MODE_CTR, MODE_CTS, MODE_ECB, MODE_OFB, MODE_NONE})
    @interface EncryptMode {}

    public EncryptUtil setMode(@EncryptMode String mode) {
        this.mMode = mode;
        return this;
    }

    @StringDef({PADDING_PKCS1, PADDING_PKCS5, PADDING_NO, PADDING_ISO10126, PADDING_OAEP, PADDING_OAEP_SHA_1, PADDING_OAEP_SHA_256})
    @interface EncryptPadding {}

    /**
     * 填充模式
     */
    public EncryptUtil setPadding(@EncryptPadding String padding) {
        this.mPadding = padding;
        return this;
    }

    /**
     * 设置向量
     */
    public EncryptUtil setIV(byte[] iv) {
        this.mIV = iv;
        return this;
    }

    /**
     * 设置加密或解密内容
     */
    public EncryptUtil setData(byte[] data) {
        this.mData = data;
        return this;
    }

    /**
     * 重置
     */
    public void Reset() {
        mEncryptType = null;
        mKey = null;
        mData = null;
        mIV = null;
        mPadding = null;
        mMode = null;
    }

    /**
     * 加密
     *
     * @return
     */
    public byte[] encrypt() {
        try {
            Key key;
            if (RSA.equals(mEncryptType)) {
                X509EncodedKeySpec x509eks = new X509EncodedKeySpec(mKey);
                KeyFactory keyfactory = KeyFactory.getInstance(RSA);
                key = keyfactory.generatePublic(x509eks);
            } else {
                key = new SecretKeySpec(mKey, mEncryptType);//生成密钥
            }
            Cipher cipher = Cipher.getInstance(mEncryptType + mMode + mPadding);
            if (!mEncryptType.equals(RSA) && !mMode.equals(MODE_ECB)) {
                IvParameterSpec ips = new IvParameterSpec(mIV);
                cipher.init(Cipher.ENCRYPT_MODE, key, ips);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            return cipher.doFinal(mData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EncryptUtil", e.getMessage());
            return null;
        }
    }

    /**
     * 解密
     *
     * @return
     */
    public byte[] decrypt() {
        try {
            Key key;
            if (RSA.equals(mEncryptType)) {
                PKCS8EncodedKeySpec pkcs8eks = new PKCS8EncodedKeySpec(mKey);
                KeyFactory keyfactory = KeyFactory.getInstance(RSA);
                key = keyfactory.generatePrivate(pkcs8eks);
            } else {
                key = new SecretKeySpec(mKey, mEncryptType);//生成密钥
            }
            Cipher cipher = Cipher.getInstance(mEncryptType + mMode + mPadding);
            if (!mEncryptType.equals(RSA) && !mMode.equals(MODE_ECB)) {
                IvParameterSpec ips = new IvParameterSpec(mIV);
                cipher.init(Cipher.DECRYPT_MODE, key, ips);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            return cipher.doFinal(mData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EncryptUtil", e.getMessage());
            return null;
        }
    }

    public static Object getKey(@EncryptType String type, int size) {
        try {
            if (type.equals(RSA)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(type);
                keyPairGenerator.initialize(size, new SecureRandom());
                KeyPair kp = keyPairGenerator.genKeyPair();
                return kp;
            } else {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(type);
                keyGenerator.init(size);
                SecretKey secretKey = keyGenerator.generateKey();
                return secretKey;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e("EncryptUtil", e.getMessage());
            return null;
        }
    }
}
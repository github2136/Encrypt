package com.github2136.encrypt.encrypt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github2136.encrypt.ClipboardUtil;
import com.github2136.encrypt.EncryptUtil;
import com.github2136.encrypt.R;

import java.security.KeyPair;

import javax.crypto.SecretKey;

public class RSAActivity extends AppCompatActivity {
    private Spinner spMode, spPadding;
    private EditText etPrikey, etPubkey, etContent, etResult;
    private TextView tvPrikey, tvPubkey;
    private Button btnKey, btnEncrypt, btnDecrypt, btnCopyPri, btnCopyPub;
    private RadioButton rbKey128, rbKey256, rbKey512;
    private ClipboardUtil mClipboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa);
        setTitle("RSA");

        mClipboardUtil = new ClipboardUtil(this);

        spMode = (Spinner) findViewById(R.id.sp_mode);
        spPadding = (Spinner) findViewById(R.id.sp_padding);
        etPrikey = (EditText) findViewById(R.id.et_prikey);
        etPubkey = (EditText) findViewById(R.id.et_pubkey);

        etContent = (EditText) findViewById(R.id.et_content);
        etResult = (EditText) findViewById(R.id.et_result);

        tvPrikey = (TextView) findViewById(R.id.tv_prikey);
        tvPubkey = (TextView) findViewById(R.id.tv_pubkey);

        btnKey = (Button) findViewById(R.id.btn_key);
        btnEncrypt = (Button) findViewById(R.id.btn_encrypt);
        btnDecrypt = (Button) findViewById(R.id.btn_decrypt);
        btnCopyPri = (Button) findViewById(R.id.btn_copy_pri);
        btnCopyPub = (Button) findViewById(R.id.btn_copy_pub);

        rbKey128 = (RadioButton) findViewById(R.id.rb_key_128);
        rbKey256 = (RadioButton) findViewById(R.id.rb_key_256);
        rbKey512 = (RadioButton) findViewById(R.id.rb_key_512);

        btnKey.setOnClickListener(mOnClickListener);
        btnEncrypt.setOnClickListener(mOnClickListener);
        btnDecrypt.setOnClickListener(mOnClickListener);
        btnCopyPub.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_key: {
                    etPrikey.setText(null);
                    etPubkey.setText(null);
                    KeyPair kp;

                    if (rbKey128.isChecked()) {
                        kp = (KeyPair) EncryptUtil.getKey(EncryptUtil.RSA, 1024);
                    } else if (rbKey256.isChecked()) {
                        kp = (KeyPair) EncryptUtil.getKey(EncryptUtil.RSA, 2048);
                    } else {
                        kp = (KeyPair) EncryptUtil.getKey(EncryptUtil.RSA, 4096);
                    }

                    tvPrikey.setText(Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.NO_WRAP));
                    tvPubkey.setText(Base64.encodeToString(kp.getPublic().getEncoded(), Base64.NO_WRAP));
                }
                break;
                case R.id.btn_encrypt: {
                    byte[] key;
                    if (TextUtils.isEmpty(etPubkey.getText().toString())) {
                        key = Base64.decode(tvPubkey.getText().toString(), Base64.NO_WRAP);
                    } else {
                        key = Base64.decode(etPubkey.getText().toString(), Base64.NO_WRAP);
                    }

                    byte[] encryptData = EncryptUtil.getInstance(EncryptUtil.RSA)
                            .setKey(key)
                            .setData(etContent.getText().toString().getBytes())
                            .setMode(getMode())
                            .setPadding(getPadding())
                            .encrypt();
                    if (encryptData != null) {
                        String encrypt = Base64.encodeToString(encryptData, Base64.NO_WRAP);
                        etResult.setText(encrypt);
                    } else {
                        etResult.setText("失败");
                    }
                }
                break;
                case R.id.btn_decrypt: {
                    byte[] key;

                    if (TextUtils.isEmpty(etPrikey.getText().toString())) {
                        key = Base64.decode(tvPrikey.getText().toString(), Base64.NO_WRAP);
                    } else {
                        key = Base64.decode(etPrikey.getText().toString(), Base64.NO_WRAP);
                    }

                    byte[] decryptData = EncryptUtil.getInstance(EncryptUtil.RSA)
                            .setKey(key)
                            .setData(Base64.decode(etResult.getText().toString(), Base64.NO_WRAP))
                            .setMode(getMode())
                            .setPadding(getPadding())
                            .decrypt();
                    if (decryptData != null) {
                        String decrypt = new String(decryptData);
                        etContent.setText(decrypt);
                    } else {
                        etContent.setText("失败");
                    }
                }
                break;
                case R.id.btn_copy_pri:
                    mClipboardUtil.copy("encrypt_private", tvPrikey.getText().toString());
                    break;
                case R.id.btn_copy_pub:
                    mClipboardUtil.copy("encrypt_public", tvPubkey.getText().toString());
                    break;
            }
        }
    };

    private String getMode() {
        switch (spMode.getSelectedItemPosition()) {
            case 0:
                return EncryptUtil.MODE_ECB;
            case 1:
                return EncryptUtil.MODE_NONE;
            default:
                return null;
        }
    }

    private String getPadding() {
        switch (spPadding.getSelectedItemPosition()) {
            case 0:
                return EncryptUtil.PADDING_NO;
            case 1:
                return EncryptUtil.PADDING_OAEP;
            case 2:
                return EncryptUtil.PADDING_OAEP_SHA_1;
            case 3:
                return EncryptUtil.PADDING_OAEP_SHA_256;
            case 4:
                return EncryptUtil.PADDING_PKCS1;
            default:
                return null;
        }
    }
}

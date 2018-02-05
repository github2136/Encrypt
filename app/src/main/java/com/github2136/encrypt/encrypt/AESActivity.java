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

import javax.crypto.SecretKey;

public class AESActivity extends AppCompatActivity {
    private Spinner spMode, spPadding;
    private EditText etPrikey, etIv, etContent, etResult;
    private TextView tvPrikey;
    private Button btnKey, btnEncrypt, btnDecrypt, btnCopyPri;
    private RadioButton rbByte, rbBase64, rbKey16, rbKey24, rbKey32;
    private ClipboardUtil mClipboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes);
        setTitle("AES");

        mClipboardUtil = new ClipboardUtil(this);

        spMode = (Spinner) findViewById(R.id.sp_mode);
        spPadding = (Spinner) findViewById(R.id.sp_padding);
        etPrikey = (EditText) findViewById(R.id.et_prikey);

        etIv = (EditText) findViewById(R.id.et_iv);
        etContent = (EditText) findViewById(R.id.et_content);
        etResult = (EditText) findViewById(R.id.et_result);

        tvPrikey = (TextView) findViewById(R.id.tv_prikey);

        btnKey = (Button) findViewById(R.id.btn_key);
        btnEncrypt = (Button) findViewById(R.id.btn_encrypt);
        btnDecrypt = (Button) findViewById(R.id.btn_decrypt);
        btnCopyPri = (Button) findViewById(R.id.btn_copy_pri);

        rbByte = (RadioButton) findViewById(R.id.rb_byte);
        rbBase64 = (RadioButton) findViewById(R.id.rb_base64);
        rbKey16 = (RadioButton) findViewById(R.id.rb_key_16);
        rbKey24 = (RadioButton) findViewById(R.id.rb_key_24);
        rbKey32 = (RadioButton) findViewById(R.id.rb_key_32);

        btnKey.setOnClickListener(mOnClickListener);
        btnEncrypt.setOnClickListener(mOnClickListener);
        btnDecrypt.setOnClickListener(mOnClickListener);
        btnCopyPri.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_key: {
                    etPrikey.setText(null);
                    SecretKey key;
                    if (rbKey16.isChecked()) {
                        key = (SecretKey) EncryptUtil.getKey(EncryptUtil.AES, 128);
                    } else if (rbKey24.isChecked()) {
                        key = (SecretKey) EncryptUtil.getKey(EncryptUtil.AES, 192);
                    } else {
                        key = (SecretKey) EncryptUtil.getKey(EncryptUtil.AES, 256);
                    }
                    tvPrikey.setText(Base64.encodeToString(key.getEncoded(), Base64.NO_WRAP));
                }
                break;
                case R.id.btn_encrypt: {
                    byte[] key;
                    if (TextUtils.isEmpty(etPrikey.getText().toString())) {
                        key = Base64.decode(tvPrikey.getText().toString(), Base64.NO_WRAP);
                    } else {
                        if (rbByte.isChecked()) {
                            key = etPrikey.getText().toString().getBytes();
                        } else {
                            key = Base64.decode(etPrikey.getText().toString(), Base64.NO_WRAP);
                        }
                    }

                    byte[] encryptData = EncryptUtil.getInstance(EncryptUtil.AES)
                            .setKey(key)
                            .setData(etContent.getText().toString().getBytes())
                            .setMode(getMode())
                            .setIV(etIv.getText().toString().getBytes())
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
                        if (rbByte.isChecked()) {
                            key = etPrikey.getText().toString().getBytes();
                        } else {
                            key = Base64.decode(etPrikey.getText().toString(), Base64.NO_WRAP);
                        }
                    }

                    byte[] decryptData = EncryptUtil.getInstance(EncryptUtil.AES)
                            .setKey(key)
                            .setData(Base64.decode(etResult.getText().toString(), Base64.NO_WRAP))
                            .setMode(getMode())
                            .setIV(etIv.getText().toString().getBytes())
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
            }
        }
    };

    private String getMode() {
        switch (spMode.getSelectedItemPosition()) {
            case 0:
                return EncryptUtil.MODE_CBC;
            case 1:
                return EncryptUtil.MODE_CFB;
            case 2:
                return EncryptUtil.MODE_CTR;
            case 3:
                return EncryptUtil.MODE_CTS;
            case 4:
                return EncryptUtil.MODE_ECB;
            case 5:
                return EncryptUtil.MODE_OFB;
            default:
                return null;
        }
    }

    private String getPadding() {
        switch (spPadding.getSelectedItemPosition()) {
            case 0:
                return EncryptUtil.PADDING_ISO10126;
            case 1:
                return EncryptUtil.PADDING_NO;
            case 2:
                return EncryptUtil.PADDING_PKCS5;
            default:
                return null;
        }
    }
}

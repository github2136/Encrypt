package com.github2136.encrypt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.KeyPair;

import javax.crypto.SecretKey;

public class EncryptActivity extends AppCompatActivity {
    private Spinner spType, spMode, spPadding;
    private EditText etPrakey, etIv, etContent, etResult;
    private TextView tvPrikey, tvPubkey;
    private Button btnKey, btnEncrypt, btnDecrypt, btnCopyPri, btnCopyPub;
    private LinearLayout llPub;
    private ClipboardUtil mClipboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        mClipboardUtil = new ClipboardUtil(this);
        spType = (Spinner) findViewById(R.id.sp_type);
        spMode = (Spinner) findViewById(R.id.sp_mode);
        spPadding = (Spinner) findViewById(R.id.sp_padding);
        etPrakey = (EditText) findViewById(R.id.et_prakey);

        etIv = (EditText) findViewById(R.id.et_iv);
        etContent = (EditText) findViewById(R.id.et_content);
        etResult = (EditText) findViewById(R.id.et_result);

        tvPrikey = (TextView) findViewById(R.id.tv_prikey);
        tvPubkey = (TextView) findViewById(R.id.tv_pubkey);

        btnKey = (Button) findViewById(R.id.btn_key);
        btnEncrypt = (Button) findViewById(R.id.btn_encrypt);
        btnDecrypt = (Button) findViewById(R.id.btn_decrypt);
        btnCopyPri = (Button) findViewById(R.id.btn_copy_pri);
        btnCopyPub = (Button) findViewById(R.id.btn_copy_pub);
        llPub = (LinearLayout) findViewById(R.id.ll_pub);

        spType.setOnItemSelectedListener(mOnItemSelectedListener);

        btnKey.setOnClickListener(mOnClickListener);
        btnEncrypt.setOnClickListener(mOnClickListener);
        btnDecrypt.setOnClickListener(mOnClickListener);
        btnCopyPri.setOnClickListener(mOnClickListener);
        btnCopyPub.setOnClickListener(mOnClickListener);

    }

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 3) {
                llPub.setVisibility(View.VISIBLE);
                etPrakey.setText(null);
                etPrakey.setEnabled(false);
            } else {
                llPub.setVisibility(View.GONE);
                etPrakey.setEnabled(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_key:
                    etPrakey.setText(null);
                    if (getType().equals(EncryptUtil.RSA)) {
                        KeyPair kp = (KeyPair) EncryptUtil.getKey(getType(), getKeySize());
                        tvPrikey.setText(Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.NO_WRAP));
                        tvPubkey.setText(Base64.encodeToString(kp.getPublic().getEncoded(), Base64.NO_WRAP));
                    } else {
                        SecretKey key = (SecretKey) EncryptUtil.getKey(getType(), getKeySize());
                        tvPrikey.setText(Base64.encodeToString(key.getEncoded(), Base64.NO_WRAP));
                    }
                    break;
                case R.id.btn_encrypt: {
                    byte[] key;
                    if (getType().equals(EncryptUtil.RSA)) {
                        key = Base64.decode(tvPubkey.getText().toString(), Base64.NO_WRAP);
                    } else {
                        if (TextUtils.isEmpty(etPrakey.getText().toString())) {
                            key = Base64.decode(tvPrikey.getText().toString(), Base64.NO_WRAP);
                        } else {
                            key = etPrakey.getText().toString().getBytes();
                        }
                    }
                    byte[] encryptData = EncryptUtil.getInstance(getType())
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
                    if (getType().equals(EncryptUtil.RSA)) {
                        key = Base64.decode(tvPrikey.getText().toString(), Base64.NO_WRAP);
                    } else {
                        if (TextUtils.isEmpty(etPrakey.getText().toString())) {
                            key = Base64.decode(tvPrikey.getText().toString(), Base64.NO_WRAP);
                        } else {
                            key = etPrakey.getText().toString().getBytes();
                        }
                    }
                    byte[] decryptData = EncryptUtil.getInstance(getType())
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
                case R.id.btn_copy_pub:
                    mClipboardUtil.copy("encrypt_public", tvPubkey.getText().toString());
                    break;
            }
        }
    };

    private String getType() {
        switch (spType.getSelectedItemPosition()) {
            case 0:
                return EncryptUtil.DES;
            case 1:
                return EncryptUtil.DESede;
            case 2:
                return EncryptUtil.AES;
            case 3:
                return EncryptUtil.RSA;
            default:
                return null;
        }
    }

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
            case 6:
                return EncryptUtil.MODE_NONE;
            default:
                return null;
        }
    }

    private String getPadding() {
        switch (spPadding.getSelectedItemPosition()) {
            case 0:
                return EncryptUtil.PADDING_PKCS5;
            case 1:
                return EncryptUtil.PADDING_NO;
            case 2:
                return EncryptUtil.PADDING_ISO10126;
            case 3:
                return EncryptUtil.PADDING_OAEP;
            case 4:
                return EncryptUtil.PADDING_OAEP_SHA_1;
            case 5:
                return EncryptUtil.PADDING_OAEP_SHA_256;
            case 6:
                return EncryptUtil.PADDING_PKCS1;
            default:
                return null;
        }
    }

    private int getKeySize() {
        switch (spType.getSelectedItemPosition()) {
            case 0:
                return 64;
            case 1:
                return 192;
            case 2:
                return 192;
            case 3:
                return 1024;
            default:
                return 0;
        }
    }
}

package com.github2136.encrypt;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MDActivity extends AppCompatActivity {
    private Spinner spType;
    private EditText etContent;
    private TextView tvResult;
    private Button btnOk, btnCopy;
    private ClipboardUtil mClipboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md);
        mClipboardUtil = new ClipboardUtil(this);
        spType = (Spinner) findViewById(R.id.sp_type);
        etContent = (EditText) findViewById(R.id.et_content);
        tvResult = (TextView) findViewById(R.id.tv_result);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCopy = (Button) findViewById(R.id.btn_copy);
        btnOk.setOnClickListener(mOnClickListener);
        btnCopy.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_ok:
                    try {
                        String type = spType.getSelectedItem().toString();
                        if (type.equals("SHA-224")) {
                            if (Build.VERSION.SDK_INT > 8 || Build.VERSION.SDK_INT < 22) {
                                tvResult.setText("该Android版本不支持该类型");
                                return;
                            }
                        }
                        String result = getMD(etContent.getText().toString().getBytes("UTF-8"), type);
                        tvResult.setText(result);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_copy:
                    mClipboardUtil.copy("MessageDigest", tvResult.getText().toString());
                    Toast.makeText(MDActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    public String getMD(byte[] data, String algorithm) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance(algorithm).digest(data);
        } catch (NoSuchAlgorithmException e) {
            return "失败";//   throw new RuntimeException("Huh, " + algorithm + " should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}

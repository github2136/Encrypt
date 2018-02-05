package com.github2136.encrypt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github2136.encrypt.encrypt.AESActivity;
import com.github2136.encrypt.encrypt.DESActivity;
import com.github2136.encrypt.encrypt.DESedeActivity;
import com.github2136.encrypt.encrypt.RSAActivity;

public class EncryptActivity extends AppCompatActivity {

    private Button btnDES, btnDESede, btnAES, btnRSA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        setTitle("加密");
        btnDES = (Button) findViewById(R.id.btn_des);
        btnDESede = (Button) findViewById(R.id.btn_desede);
        btnAES = (Button) findViewById(R.id.btn_aes);
        btnRSA = (Button) findViewById(R.id.btn_rsa);

        btnDES.setOnClickListener(mOnClickListener);
        btnDESede.setOnClickListener(mOnClickListener);
        btnAES.setOnClickListener(mOnClickListener);
        btnRSA.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.btn_des:
                    intent = new Intent(EncryptActivity.this, DESActivity.class);
                    break;
                case R.id.btn_desede:
                    intent = new Intent(EncryptActivity.this, DESedeActivity.class);
                    break;
                case R.id.btn_aes:
                    intent = new Intent(EncryptActivity.this, AESActivity.class);

                    break;
                case R.id.btn_rsa:
                    intent = new Intent(EncryptActivity.this, RSAActivity.class);
                    break;
            }
            startActivity(intent);
        }
    };
}
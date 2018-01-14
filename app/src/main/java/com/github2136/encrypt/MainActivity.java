package com.github2136.encrypt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btnMD, btnEncrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMD = (Button) findViewById(R.id.btn_md);
        btnEncrypt = (Button) findViewById(R.id.btn_encrypt);

        btnMD.setOnClickListener(mOnClickListener);
        btnEncrypt.setOnClickListener(mOnClickListener);

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_md:
                    startActivity(new Intent(MainActivity.this, MDActivity.class));
                    break;
                case R.id.btn_encrypt:
                    startActivity(new Intent(MainActivity.this, EncryptActivity.class));
                    break;
            }
        }
    };
}
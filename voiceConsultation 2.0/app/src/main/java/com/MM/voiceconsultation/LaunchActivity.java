package com.MM.voiceconsultation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LaunchActivity extends AppCompatActivity {
    private Button btGoToMain;
    private Button btGoToChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);


        btGoToMain = (Button) findViewById(R.id.btGoToMain);
        btGoToChat = (Button) findViewById(R.id.btGoToChat);

        OnClick onClick = new OnClick();
        btGoToMain.setOnClickListener(onClick);
        btGoToChat.setOnClickListener(onClick);

    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case R.id.btGoToMain:
                    intent = new Intent(LaunchActivity.this,MainActivity.class);
                    //intent.putExtra("from","FromMain");
                    break;
                case R.id.btGoToChat:
                    intent = new Intent(LaunchActivity.this,chatActivity.class);
                    //intent.putExtra("from","FromMain");
                    break;


            }
            startActivity(intent);
        }
    }
}

package com.inmd.deu_checkapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lakue.lakuepopupactivity.PopupActivity;
import com.lakue.lakuepopupactivity.PopupGravity;
import com.lakue.lakuepopupactivity.PopupType;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();

    //객체화
    private Button send;
    private EditText Name, Phone;
    private MainActivity sContext;
    private String roomdata, myName, myphone, myroom;;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sContext = this;

        // 버튼
        send = (Button) findViewById(R.id.button);
        // 텍스트
        Name = (EditText) findViewById(R.id.Name);
        Phone = (EditText) findViewById(R.id.Phone);
        //체크박스
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        CheckBox savebuten = (CheckBox) findViewById(R.id.checkBox2);



        qrScan = new IntentIntegrator(this);


        boolean boo = PreferenceManager.getBoolean(sContext, "check");
        if (boo) {
            Phone.setText(PreferenceManager.getString(sContext, "phone"));
            Name.setText(PreferenceManager.getString(sContext, "Name"));
            savebuten.setChecked(true);
        }

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    //체크박스를 클릭하면
                    String data2 = Name.getText().toString();
                    //알림 부분
                    if (Name.getText().toString().length() == 0 || Phone.getText().toString().length() == 0) {
                        Intent intent = new Intent(getBaseContext(), PopupActivity.class);
                        intent.putExtra("type", PopupType.NORMAL);
                        intent.putExtra("gravity", PopupGravity.CENTER);
                        intent.putExtra("title", "ERROR ");
                        intent.putExtra("content", "전화번호나 이름에 작성된 내용이 없습니다.");
                        intent.putExtra("buttonCentzer", "닫기");
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(getBaseContext(), PopupActivity.class);
                        intent.putExtra("type", PopupType.NORMAL);
                        intent.putExtra("gravity", PopupGravity.CENTER);
                        intent.putExtra("title", "Succesfull");
                        intent.putExtra("content", "제출버튼을 눌려서 제출을 해주세요.");
                        intent.putExtra("buttonCenter", "제출");
                        startActivityForResult(intent, 1);

                        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
                        qrScan.setPrompt("먼저 QR코드를 사각형에 알맞게 넣어주세요.");
                        qrScan.initiateScan();
                    }


                } else {
                    //체크박스를 클릭하지 않으면
                    if (Name.getText().toString().length() == 0 || Phone.getText().toString().length() == 0) {
                        Intent intent = new Intent(getBaseContext(), PopupActivity.class);
                        intent.putExtra("type", PopupType.NORMAL);
                        intent.putExtra("gravity", PopupGravity.CENTER);
                        intent.putExtra("title", "ERROR ");
                        intent.putExtra("content", "전화번호나 이름에 작성된 내용이 없습니다.");
                        intent.putExtra("buttonCenter", "닫기");
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(getBaseContext(), PopupActivity.class);
                        intent.putExtra("type", PopupType.NORMAL);
                        intent.putExtra("gravity", PopupGravity.CENTER);
                        intent.putExtra("title", "ERROR");
                        intent.putExtra("content", "개인정보 수집에 동의를 해주세요.");
                        intent.putExtra("buttonCenter", "단기");
                        startActivityForResult(intent, 1);
                    }
                }

                if (savebuten.isChecked()) {
                    //입력창에서 텍스트를 가져와 PreferenceManager에 저장함
                    PreferenceManager.setString(sContext, "phone", Phone.getText().toString());
                    PreferenceManager.setString(sContext, "Name", Name.getText().toString());
                    PreferenceManager.setBoolean(sContext, "check", savebuten.isChecked());

                } else {
                    PreferenceManager.setBoolean(sContext, "check", savebuten.isChecked());
                    PreferenceManager.clear(sContext);
                }
            }

        });
    }

    public void updateUserInfo() {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("Phone", myphone)
                .add("Name", myName)
                .add("room", myroom)
                .build();

        Request request = new Request.Builder()

                .url("***")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(updateUserInfoCallback);
    }

    private Callback updateUserInfoCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("TEST", "ERROR Message : " + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();
            Log.d("TEST", "responseDatae : " + responseData);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // todo
                roomdata = result.getContents();
                myroom = roomdata;
                myName = ((EditText) (findViewById(R.id.Name))).getText().toString();
                myphone = ((EditText) (findViewById(R.id.Phone))).getText().toString();
                updateUserInfo();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

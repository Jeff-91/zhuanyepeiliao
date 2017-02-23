package com.example.uibestpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

//    private TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMsgs(); // 初始化消息数据
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    sendRequestWithOkHttp(content);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容

                }
            }
        });
    }

    private void sendRequestWithOkHttp(final String msgsend){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    String msgsend = inputtext.getText().toString();
                    String baseUrl = "http://www.tuling123.com/openapi/api?key=6a94a09f577648ab98f221e7e7ae686f&info=";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(baseUrl + msgsend)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    if ("小二".equals(msgsend)) {
                        responseData = "叫我干嘛？";
                        showResponse(responseData);
                    } else {
                        parseJSONWithJSONObject(responseData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String text = jsonObject.getString("text");
            showResponse(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Msg msg = new Msg(response, Msg.TYPE_RECEIVED);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
            }
        });
    }

    private void initMsgs() {
        Msg msg1 = new Msg("hello，无聊的话我可以陪你聊天哦", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
    }

}

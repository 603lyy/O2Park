package com.yaheen.o2park;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yaheen.o2park.bean.ChatMsgEntity;
import com.yaheen.o2park.bean.TbChatMsg;
import com.yaheen.o2park.util.AudioUtils;
import com.yaheen.o2park.util.SysUtils;
import com.yaheen.o2park.widget.NameGroupView;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class MainActivity extends AppCompatActivity {

    private WebSocketConnection mConnection = new WebSocketConnection();

    private TextView tvOpen;

    private NameGroupView groupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOpen = findViewById(R.id.tv_open);
        groupView = findViewById(R.id.group_view);

        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                initWebSocket();
                AudioUtils.getInstance().speakText("欢迎老师的莅临");
            }
        });

        //初始化语音对象
        AudioUtils.getInstance().init(MainActivity.this);
    }

    private void initWebSocket() {
        //注意连接和服务名称要一致
        /*final String wsuri = "ws://192.168.0.2：8080/st/sosWebSocketService?userCode="
                + spu.getValue(LoginActivity.STR_USERNAME);*/
        String wsuri = "ws://192.168.199.13:8080/loles" + "/ws/chat.do?hardwareId="
                + SysUtils.android_id(MainActivity.this) + "&courseCode=YY";

        if (mConnection == null) {
            mConnection = new WebSocketConnection();
        }

        try {
            mConnection.connect(wsuri, new mWebSocketHandler());
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    private class mWebSocketHandler extends WebSocketHandler {
        @Override
        public void onOpen() {
            tvOpen.setVisibility(View.GONE);
            groupView.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTextMessage(String text) {
            Gson gson = new Gson();
            TbChatMsg chatMsg = gson.fromJson(text, TbChatMsg.class);
            groupView.addName(chatMsg.getMsg());
            AudioUtils.getInstance().speakText(chatMsg.getMsg());
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            super.onBinaryMessage(payload);
        }

        @Override
        public void onClose(int code, String reason) {
            Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }
}

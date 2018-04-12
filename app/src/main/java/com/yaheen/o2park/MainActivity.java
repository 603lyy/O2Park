package com.yaheen.o2park;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yaheen.o2park.bean.ChatMsgEntity;
import com.yaheen.o2park.bean.TbChatMsg;
import com.yaheen.o2park.util.AudioUtils;
import com.yaheen.o2park.util.SysUtils;
import com.yaheen.o2park.widget.NameGroupView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

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
                AudioUtils.getInstance().speakText("欢迎黄勇老师的莅临");
//                parkTest();
            }
        });

        //初始化语音对象
        AudioUtils.getInstance().init(getApplicationContext());
    }

    private void parkTest() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fStart", "2020-03-31 23:59:59");
            jsonObject.put("fLotID", "77661");
            jsonObject.put("fMobile", "");
            jsonObject.put("fParkID", "304");
            jsonObject.put("fEnd", "2018-04-10 23:59:59");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RequestParams params = new RequestParams("https://api.dev.kapark.cn/Pay/PrePay");
        params.addQueryStringParameter("appId", "kp_app_i");
        params.addQueryStringParameter("iTotalFee", "20000");
        params.addQueryStringParameter("strDetail", "卡趴测试车场4333333车位月保管理费200元");
        params.addQueryStringParameter("strPassWord", "9B8AAE6BC5812DC174BEE436C8EAE4F5");
        params.addQueryStringParameter("strPayType", "APPWECHAT");
        params.addQueryStringParameter("strUserName", "18620648850");
        params.addParameter("strAttach", jsonObject);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("lin", "onSuccess: " + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("lin", "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
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
        AudioUtils.destorySpeak();
    }
}

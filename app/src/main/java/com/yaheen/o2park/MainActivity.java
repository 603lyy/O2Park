package com.yaheen.o2park;

import android.animation.Animator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
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

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class MainActivity extends AppCompatActivity {

    private WebSocketConnection mConnection = new WebSocketConnection();

    private TextView tvOpen, tvSend, tvName;

    private NameGroupView groupView;

    private RelativeLayout relativeLayout;

    private LinearLayout llVoice;

    private SpannableString ss;

    private int i = 0;

    /**
     * 界面改变的次数
     */
    private int changeTime = 1;

    private String nameStr[] = {"柯雪莉  女士", "南可安  先生", "邓家禧  先生", "白梅霞  女士",
            "吴文慧  女士", "彭浩贤  先生", "麦天恩  先生", "梁敬章  先生", "梁振声  先生", "刘慧诗  女士"};

    private String companyStr[] = {"以色列领事", "以色列领事", "香港特区驻粤办", "香港特区驻粤办",
            "香港贸发局", "香港商会（广东）", "香港商会（广东）", "香港商会（广东）",
            "香港电讯盈科中国", "希尔顿酒店"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        tvOpen = findViewById(R.id.tv_open);
        tvSend = findViewById(R.id.tv_send);
        tvName = findViewById(R.id.tv_name);
        llVoice = findViewById(R.id.ll_voice);
        groupView = findViewById(R.id.group_view);
        relativeLayout = findViewById(R.id.rl_parent);

        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                initWebSocket();
//                parkTest();
//                groupView.setVisibility(View.VISIBLE);
//                llVoice.setVisibility(View.GONE);
                changeView();
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showWellcomeView();
//                if(!AudioUtils.getInstance().isSpeaking()){
//                    ss = new SpannableString(nameStr[i] + "\n" + companyStr[i]);
//                    ss.setSpan(new AbsoluteSizeSpan(50), 0, nameStr.length - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    ss.setSpan(new AbsoluteSizeSpan(35), nameStr.length - 1, ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tvName.setText(ss);
//                }
//                AudioUtils.getInstance().addText("欢迎" + nameStr[i] + "的莅临");
//                i++;
//                if (i == 10) {
//                    i = 0;
//                }
                SpannableString span = new SpannableString(nameStr[0] + "\n" + companyStr[0]);
                span.setSpan(new AbsoluteSizeSpan(14), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new AbsoluteSizeSpan(8), 8, span.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                groupView.addName(span);
            }
        });

        //初始化语音对象
        AudioUtils.getInstance().init(getApplicationContext(), speakListener);
        groupView.setListener(animatorListener);

        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (groupView.getVisibility() == View.VISIBLE && changeTime == 0) {
                    SpannableString span = new SpannableString(nameStr[AudioUtils.getInstance().getIndex()] + "\n" + companyStr[AudioUtils.getInstance().getIndex()]);
                    span.setSpan(new AbsoluteSizeSpan(14), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(new AbsoluteSizeSpan(8), 8, span.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    groupView.addName(span);
                    changeTime = 1;
                }
                if (groupView.getVisibility() == View.GONE) {
                    changeTime = 0;
                }
            }
        });
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if(AudioUtils.getInstance().hasNext()){
                showWellcomeView();
                ss = new SpannableString(nameStr[(AudioUtils.getInstance().getIndex()+1)%10] + "\n" + companyStr[(AudioUtils.getInstance().getIndex()+1)%10]);
                ss.setSpan(new AbsoluteSizeSpan(50), 0, nameStr.length - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(35), nameStr.length - 1, ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvName.setText(ss);
            }
            AudioUtils.getInstance().speakText();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private SynthesizerListener speakListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            llVoice.setVisibility(View.GONE);
            groupView.setVisibility(View.VISIBLE);
            changeView();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

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
        String wsuri = "ws://192.168.199.112:8080/o2park" + "/ws/chat.do?hardwareId="
                + SysUtils.android_id(MainActivity.this);

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
            changeView();
            Toast.makeText(MainActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTextMessage(String text) {
            Gson gson = new Gson();
            TbChatMsg chatMsg = gson.fromJson(text, TbChatMsg.class);
            tvSend.setText(chatMsg.getMsg());
//            groupView.addName(chatMsg.getMsg());
//            AudioUtils.getInstance().speakText(chatMsg.getMsg());
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

    private void changeView() {
        ViewGroup.LayoutParams Params = groupView.getLayoutParams();
        Params.width = relativeLayout.getHeight();
        groupView.setLayoutParams(Params);
    }

    public void showWellcomeView(){
        groupView.setVisibility(View.GONE);
        llVoice.setVisibility(View.VISIBLE);
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

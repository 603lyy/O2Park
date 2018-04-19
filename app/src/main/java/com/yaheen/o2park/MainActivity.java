package com.yaheen.o2park;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcB;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.yaheen.o2park.bean.TbChatMsg;
import com.yaheen.o2park.util.AudioUtils;
import com.yaheen.o2park.util.Converter;
import com.yaheen.o2park.util.SysUtils;
import com.yaheen.o2park.widget.NameGroupView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import static com.yaheen.o2park.util.NFCUtils.ByteArrayToHexString;
import static com.yaheen.o2park.util.NFCUtils.toStringHex;

public class MainActivity extends AppCompatActivity {

    /**
     * 定时发送信息
     */
    public static final int MSG_SEND = 1000;

    private NfcB nfcbTag;
    private Tag tagFromIntent;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNdefExchangeFilters;

    private WebSocketConnection mConnection = new WebSocketConnection();

    private TextView tvOpen, tvSend, tvName;

    private EditText etIP;

    private NameGroupView groupView;

    private RelativeLayout relativeLayout;

    private LinearLayout llVoice, llBtn;

    private String ex_id = "", types = "";

    private SpannableString ss;

    private int i = 0;

    /**
     * 名字列表的顺序
     */
    private int index = 0;

    /**
     * 界面改变的次数
     */
    private int changeTime = 1;

    /**
     * 是否在读名字或者做动画
     */
    private boolean isDoing = false;

    private String nameStr[] = new String[100];
//            {"柯雪莉  女士", "南可安  先生", "邓家禧  先生", "白梅霞  女士",
//            "吴文慧  女士", "彭浩贤  先生", "麦天恩  先生", "梁敬章  先生", "梁振声  先生", "刘慧诗  女士"};

    private String companyStr[] = new String[100];
//            {"以色列领事", "以色列领事", "香港特区驻粤办", "香港特区驻粤办",
//            "香港贸发局", "香港商会（广东）", "香港商会（广东）", "香港商会（广东）",
//            "香港电讯盈科中国", "希尔顿酒店"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        etIP = findViewById(R.id.et_ip);
        llBtn = findViewById(R.id.ll_btn);
        tvOpen = findViewById(R.id.tv_open);
        tvSend = findViewById(R.id.tv_send);
        tvName = findViewById(R.id.tv_name);
        llVoice = findViewById(R.id.ll_voice);
        groupView = findViewById(R.id.group_view);
        relativeLayout = findViewById(R.id.rl_parent);

        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initWebSocket();
//                parkTest();
//                SpannableString span = new SpannableString("杨鸿南  先生\n广州壹物壹码物联网信息技\n术有限公司");
//                span.setSpan(new AbsoluteSizeSpan(14), 0, nameStr[index].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                span.setSpan(new AbsoluteSizeSpan(8), nameStr[index].length(), span.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                groupView.addName(span);
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessageDelayed(MSG_SEND, 4 * 1000);
                showWellcomeView();
                if (!AudioUtils.getInstance().isSpeaking()) {
                    ss = new SpannableString(nameStr[i] + "\n" + companyStr[i]);
                    ss.setSpan(new AbsoluteSizeSpan(50), 0, nameStr[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new AbsoluteSizeSpan(35), nameStr[i].length(), ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvName.setText(ss);
                }
                AudioUtils.getInstance().addText("欢迎" + nameStr[i] + "的莅临");
                i++;
                if (i == 10) {
                    i = 0;
                }

//                SpannableString span = new SpannableString(nameStr[0] + "\n" + companyStr[0]);
//                span.setSpan(new AbsoluteSizeSpan(14), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                span.setSpan(new AbsoluteSizeSpan(8), 8, span.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                groupView.addName(span);
            }
        });

        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (groupView.getVisibility() == View.VISIBLE && changeTime == 0) {
                    index = AudioUtils.getInstance().getIndex();
                    if (companyStr[index].length() > 12) {
                        StringBuilder stringBuilder = new StringBuilder(companyStr[index]);
                        stringBuilder.insert(12, "\n");
                        companyStr[index] = stringBuilder.toString();
                    }
                    SpannableString span = new SpannableString(nameStr[index] + "\n" + companyStr[index]);
                    span.setSpan(new AbsoluteSizeSpan(14), 0, nameStr[index].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(new AbsoluteSizeSpan(8), nameStr[index].length(), span.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    groupView.addName(span);
                    changeTime = 1;
                }
                if (groupView.getVisibility() == View.GONE) {
                    changeTime = 0;
                }
            }
        });

        //初始化语音对象
        AudioUtils.getInstance().init(getApplicationContext(), speakListener);
        groupView.setListener(animatorListener);
        initNFC();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter == null) {
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            return;
        }

        //nfc自动读取芯片内容后调用activity的onResume
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, null, null);
            resolvIntent(getIntent());
        }
    }

    private void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);// 设备注册
        if (mNfcAdapter == null) {
            // 判断设备是否可用
            Toast.makeText(this, "该设备不支持NFC功能", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefDetected = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("*/*");// text/plain
        } catch (IntentFilter.MalformedMimeTypeException e) {
        }

        IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ttech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mNdefExchangeFilters = new IntentFilter[]{ndefDetected, ttech, td};
    }

    private void resolvIntent(Intent intent) {
        String action = intent.getAction();
        //toast(action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            tagFromIntent = getIntent()
                    .getParcelableExtra(NfcAdapter.EXTRA_TAG);
            getresult(tagFromIntent);
//            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
//                    NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage[] msgs;
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//            } else {
//                // Unknown tag type
//                byte[] empty = new byte[]{};
//                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,   //NdefRecord.TNF_UNKNOWN
//                        empty, empty, empty);
//                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
//                msgs = new NdefMessage[]{msg};
//            }
//            setUpWebView(msgs);
            // dialog(ByteArrayToHexString(msgs[0].getRecords()[0].getPayload()));
            //	dialog(msgs[0].getRecords()[0].getPayload()));
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            // 处理该intent
            tagFromIntent = getIntent()
                    .getParcelableExtra(NfcAdapter.EXTRA_TAG);
            getresult(tagFromIntent);

        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            types = "Tag";
            tagFromIntent = getIntent()
                    .getParcelableExtra(NfcAdapter.EXTRA_TAG);
            getresult(tagFromIntent);
        }
    }

    void getresult(Tag tag) {
        ArrayList<String> list = new ArrayList<String>();
        types = "";
        for (String string : tag.getTechList()) {
            list.add(string);
            types += string.substring(string.lastIndexOf(".") + 1, string.length()) + ",";
        }
        types = types.substring(0, types.length() - 1);
        if (list.contains("android.nfc.tech.MifareUltralight")) {
            String str = readTagUltralight(tag);
            setNoteBody(str);
        }
    }

    public String readTagUltralight(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            StringBuffer sb = new StringBuffer();
            byte[] no10 = new byte[4];  //校验芯片
            byte[] no11 = new byte[4];  //数据块数量

            byte[] readTag = mifare.readPages(10);

            byte[] readCount = mifare.readPages(11);

            if (readTag.length >= 4) {

                for (int i = 0; i < 4; i++) {
                    no10[i] = readTag[i];
                }

                String tagStr = toStringHex(ByteArrayToHexString(no10));

                if (tagStr.equals("YAHN")) {
                    for (int i = 0; i < 4; i++) {
                        no11[i] = readCount[i];
                    }

                    String countStr = toStringHex(ByteArrayToHexString(no11));
                    int count = Integer.valueOf(countStr.trim());

                    for (int i = 12; i < (count); i++) {
                        byte[] readResult = mifare.readPages(i);
                        if (i % 4 == 0) {
                            if (i == count) {
                                byte[] codeEnd = new byte[4];
                                for (int j = 0; j < 4; j++) {
                                    codeEnd[j] = readResult[j];
                                }
                                sb.append(ByteArrayToHexString(codeEnd));
                            } else {
                                sb.append(ByteArrayToHexString(readResult));
                            }
                        }
                    }
                }
            }
            //  String  str=toStringHex(sb.toString());

//            String finalResult = AESUtils.decryptToString(toStringHex(sb.toString()), "X2Am6tVLnwMMX8kVgdDk5w==");
            String finalResult = toStringHex(sb.toString());

            return finalResult;

        } catch (IOException e) {
//            Log.e(TAG, "IOException while writing MifareUltralight message...", e);
            return "";
        } catch (Exception ee) {
//            Log.e(TAG, "IOException while writing MifareUltralight message...", ee);
            return "";
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e) {
//                    Log.e(TAG, "Error closing tag...", e);
                }
            }
        }
    }

    private void setNoteBody(final String body) {

        if (!TextUtils.isEmpty(body)) {
            String[] bodys = body.trim().split("\\|");

            if (bodys.length > 2 || bodys.length == 3) {
                String companys[] = bodys[2].trim().split("#");
                TbChatMsg chatMsg = new TbChatMsg(bodys[0], bodys[1], companys[0]);
                speakName(chatMsg);
            }
        }
    }

    //属性动画监听
    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (AudioUtils.getInstance().hasNext()) {
                showWellcomeView();
                index = AudioUtils.getInstance().getIndex();
                ss = new SpannableString(nameStr[index + 1] + "\n" + companyStr[index + 1]);
                ss.setSpan(new AbsoluteSizeSpan(50), 0, nameStr[index + 1].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(35), nameStr[index + 1].length(), ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvName.setText(ss);
            } else {
                isDoing = false;
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

    //语音播放监听
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
        String wsuri = "ws://" + etIP.getText().toString() + "/o2park/ws/chat.do?hardwareId="
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
            llBtn.setVisibility(View.GONE);
            groupView.setVisibility(View.VISIBLE);
            changeTime = 1;
            changeView();
            mHandler.sendEmptyMessageDelayed(MSG_SEND, 4 * 60 * 1000);
            Toast.makeText(MainActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTextMessage(String text) {
            Gson gson = new Gson();
            TbChatMsg chatMsg = gson.fromJson(text, TbChatMsg.class);
            speakName(chatMsg);
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

    private void speakName(TbChatMsg chatMsg) {
        companyStr[i] = chatMsg.getUnit();
        if (chatMsg.getUserName().equals("Shirly Coifman") || chatMsg.getUserName().equals("Nadav Cohen")) {
            nameStr[i] = chatMsg.getUserName();
        } else if ("F".equals(chatMsg.getSex())) {
            nameStr[i] = chatMsg.getUserName() + "  女士";
        } else {
            nameStr[i] = chatMsg.getUserName() + "  先生";
        }
        if (!isDoing) {
            showWellcomeView();
            ss = new SpannableString(nameStr[i] + "\n" + companyStr[i]);
            ss.setSpan(new AbsoluteSizeSpan(50), 0, nameStr[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(35), nameStr[i].length(), ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvName.setText(ss);
            isDoing = true;
        }
        AudioUtils.getInstance().addText("欢迎" + nameStr[i] + "的莅临");
        i++;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SEND:
                    sendMsg();
                    break;
            }
        }
    };

    public void sendMsg() {
        if (mConnection.isConnected()) {
            String str = "心跳";
            mConnection.sendTextMessage(str);
        } else {
            initWebSocket();
        }
        mHandler.sendEmptyMessageDelayed(MSG_SEND, 4 * 60 * 1000);
    }

    private void checkUser(TbChatMsg chatMsg) {
        RequestParams params = new RequestParams("http://tlep2.yaheen.com/eapi/checkUser.do");
        params.addQueryStringParameter("username", chatMsg.getUserName());
        params.addQueryStringParameter("sex", chatMsg.getSex());
        params.addQueryStringParameter("unit", chatMsg.getUnit());
        params.setHeader("Accept-Language", "zh-CN,zh");
        params.setHeader("Content-Type", "application/json");
        params.setHeader("Charset", "utf-8");
        params.setHeader("Accept", "text/html,application/xhtml+xml,application/xml");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void changeView() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) groupView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        params.width = relativeLayout.getHeight();
        groupView.setLayoutParams(params);
    }

    public void showWellcomeView() {
        groupView.setVisibility(View.GONE);
        llVoice.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        // 读取uidgetIntent()
        byte[] myNFCID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        ex_id = Converter.getHexString(myNFCID, myNFCID.length);
        // 读取uidgetIntent()
        setIntent(intent);
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

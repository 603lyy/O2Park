package com.yaheen.o2park.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class AudioUtils {

    private static AudioUtils audioUtils;

    private static SpeechSynthesizer mySynthesizer;

    private static SynthesizerListener synthesizerListener;

    private Context context;

    // 默认本地发音人
    public static String voicerLocal = "xiaoyan";

    private List<String> nameList = new ArrayList<>();

    private int index = 0;

    public AudioUtils() {
    }

    /**
     * 描述:单例
     */
    public static AudioUtils getInstance() {
        if (audioUtils == null) {
            synchronized (AudioUtils.class) {
                if (audioUtils == null) {
                    audioUtils = new AudioUtils();
                }
            }
        }
        return audioUtils;
    }

    private InitListener myInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("mySynthesiezer:", "InitListener init() code = " + code);
        }
    };


    /**
     * 描述:初始化语音配置
     */
    public void init(Context context, SynthesizerListener listener) {
        this.context = context;
        synthesizerListener = listener;

        //处理语音合成关键类
        mySynthesizer = SpeechSynthesizer.createSynthesizer(context, myInitListener);
        //设置使用本地引擎
        mySynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mySynthesizer.setParameter(SpeechConstant.ENGINE_MODE, SpeechConstant.MODE_MSC);
        //设置发音人资源路径
        mySynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath(context));
        //设置发音人
//        mySynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置音调
//        mySynthesizer.setParameter(SpeechConstant.PITCH, "50");
        //设置音量
//        mySynthesizer.setParameter(SpeechConstant.VOLUME, "50");

    }

    //获取发音人资源路径
    private String getResourcePath(Context context) {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voicerLocal + ".jet"));
        return tempBuffer.toString();
    }

    public void addText(String content) {
        nameList.add(content);
        if (!mySynthesizer.isSpeaking()) {
            speakText(nameList.get(index));
        }
    }

    public void speakText() {
        if (index == nameList.size()) {
            return;
        }
        speakText(nameList.get(index));
    }

    /**
     * 描述:根据传入的文本转换音频并播放
     */
    private void speakText(String content) {
        if (mySynthesizer.isSpeaking()) {
            return;
        }
        index++;

        mySynthesizer.startSpeaking(content, synthesizerListener);
    }

    public boolean hasNext(){
        if(index==nameList.size()){
            return false;
        }else {
            return true;
        }
    }

    public boolean isSpeaking(){
        return mySynthesizer.isSpeaking();
    }

    public int getIndex() {
        return (index - 1)%10;
    }

    public static void destorySpeak() {
        if (mySynthesizer != null) {
            mySynthesizer.stopSpeaking();
            mySynthesizer.destroy();
        }
    }
}

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

public class AudioUtils {

    private static AudioUtils audioUtils;

    private static SpeechSynthesizer mySynthesizer;

    // 默认本地发音人
    public static String voicerLocal = "xiaoyan";

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
    public void init(Context context) {
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

    /**
     * 描述:根据传入的文本转换音频并播放
     */
    public void speakText(String content) {
        int code = mySynthesizer.startSpeaking(content, new SynthesizerListener() {
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

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }

    public static void destorySpeak() {
        if (mySynthesizer != null) {
            mySynthesizer.stopSpeaking();
            mySynthesizer.destroy();
        }
    }
}

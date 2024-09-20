package org.webrtc.video;

import androidx.annotation.Nullable;

import org.webrtc.EglBase;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.VideoCodecInfo;
import org.webrtc.VideoDecoder;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.WrappedVideoDecoderFactory;
import android.os.Build;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class CustomVideoDecoderFactory implements VideoDecoderFactory {
    private SoftwareVideoDecoderFactory softwareVideoDecoderFactory = new SoftwareVideoDecoderFactory();
    private WrappedVideoDecoderFactory wrappedVideoDecoderFactory;
    private boolean forceSWCodec  = false;
    private List<String> priorities;

    private List<String> forceSWCodecs = new ArrayList<>();

    public  CustomVideoDecoderFactory(EglBase.Context sharedContext) {
        this.wrappedVideoDecoderFactory = new WrappedVideoDecoderFactory(sharedContext);
    }

    public void setForceSWCodec(boolean forceSWCodec) {
        this.forceSWCodec = forceSWCodec;
    }

    public void setForceSWCodecList(List<String> forceSWCodecs) {
        this.forceSWCodecs = forceSWCodecs;
    }

    @Nullable
    @Override
    public VideoDecoder createDecoder(VideoCodecInfo videoCodecInfo) {
        if(forceSWCodec) {
            return softwareVideoDecoderFactory.createDecoder(videoCodecInfo);
        }
        if(!forceSWCodecs.isEmpty()) {
            if(forceSWCodecs.contains(videoCodecInfo.name)) {
                return softwareVideoDecoderFactory.createDecoder(videoCodecInfo);
            }
        }
        return wrappedVideoDecoderFactory.createDecoder(videoCodecInfo);
    }

//    @Override
//    public VideoCodecInfo[] getSupportedCodecs() {
//        if(forceSWCodec && forceSWCodecs.isEmpty()) {
//            System.out.println("softwareVideoDecoderFactory");
//            return softwareVideoDecoderFactory.getSupportedCodecs();
//        }
//        System.out.println("wrappedVideoDecoderFactory");
//        return wrappedVideoDecoderFactory.getSupportedCodecs();
//    }

    @Override
    public VideoCodecInfo[] getSupportedCodecs() {
        System.out.println("Decoder Android SDK Version:"+Build.VERSION.SDK_INT);
        final List<VideoCodecInfo> supported = new ArrayList<>(Arrays.asList(wrappedVideoDecoderFactory.getSupportedCodecs()));
        for(VideoCodecInfo codecInfo: supported){
            System.out.println("Decoder codecInfo.name:"+codecInfo.name);
        }
        final VideoCodecInfo[] sorted = new VideoCodecInfo[1];
        for (VideoCodecInfo supportedElement : supported) {
            if("H264".equals(supportedElement.name)){
                sorted[0] = supportedElement;
            }
        }
        System.out.println("Decoder sorted.length:"+sorted.length);
        for(VideoCodecInfo sort:sorted){
            System.out.println("Decoder sort.name:"+sort.name);
        }

        return sorted;
    }

    public static final String H264_FMTP_PROFILE_LEVEL_ID = "profile-level-id";
    public static final String H264_FMTP_LEVEL_ASYMMETRY_ALLOWED = "level-asymmetry-allowed";
    public static final String H264_FMTP_PACKETIZATION_MODE = "packetization-mode";
    public static final String H264_PROFILE_CONSTRAINED_BASELINE = "42e0";
    public static final String H264_PROFILE_CONSTRAINED_HIGH = "640c";
    public static final String H264_LEVEL_3_1 = "1f"; // 31 in hex.
    public static final String H264_CONSTRAINED_HIGH_3_1 = H264_PROFILE_CONSTRAINED_HIGH + H264_LEVEL_3_1;
    public static final String H264_CONSTRAINED_BASELINE_3_1 = H264_PROFILE_CONSTRAINED_BASELINE + H264_LEVEL_3_1;
}

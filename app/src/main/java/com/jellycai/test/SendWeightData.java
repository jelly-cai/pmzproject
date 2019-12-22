package com.jellycai.test;

import android.util.Log;

import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

public class SendWeightData implements IPulseSendable {

    public static String TAG = "SendWeightData";

    private double weight;

    public SendWeightData(double weight) {
        this.weight = weight;
    }

    @Override
    public byte[] parse() {
        String data = getDataNo() + getDataDate() + getDescriptionCode() + getWeight() + getEnd();
        int length = data.length();
        String lengthStr = "";
        if (length < 10) {
            lengthStr = "000" + length;
        } else if (length < 100) {
            lengthStr = "00" + length;
        } else if (length < 1000) {
            lengthStr = "0" + length;
        } else {
            lengthStr = length + "";
        }
        data = lengthStr + data;
        Log.d(TAG, "parse: " + data);
        return data.getBytes();
    }

    /**
     * 获得重量
     * @return
     */
    private String getWeight() {
        String weightEnd = String.format("%.4f", weight);
        weightEnd = weightEnd.replace(".","");
        for (int i = 0; i < 9 - weightEnd.length(); i++) {
            weightEnd = "0" + weightEnd;
        }
        return weightEnd;
    }

    /**
     * 电文号
     *
     * @return
     */
    private String getDataNo() {
        return "200010";
    }

    /**
     * 电文日期
     *
     * @return
     */
    private String getDataDate() {
        return StringUtils.getDate();
    }

    /**
     * 描述码
     *
     * @return
     */
    private String getDescriptionCode() {
        return "M1S1D";
    }

    /**
     * 结束符
     *
     * @return
     */
    private String getEnd() {
        return "\\r";
    }

}

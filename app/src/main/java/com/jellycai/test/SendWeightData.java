package com.jellycai.test;

import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

public class SendWeightData implements IPulseSendable {
    private int weight;

    public SendWeightData(int weight) {
        this.weight = weight;
    }

    @Override
    public byte[] parse() {
        String data = getDataNo() + getDataDate() + getDescriptionCode() + weight + getEnd();
        int length = data.length();
        String lengthStr = "";
        if(length < 10){
            lengthStr = "000" + lengthStr;
        }else if(length < 100){
            lengthStr = "00" + lengthStr;
        }else if(length < 1000){
            lengthStr = "0" + lengthStr;
        }else{
            lengthStr = lengthStr + "";
        }
        data = lengthStr + data;
        return data.getBytes();
    }

    /**
     * 电文号
     * @return
     */
    private String getDataNo(){
        return "M1S101";
    }

    /**
     * 电文日期
     * @return
     */
    private String getDataDate(){
        return StringUtils.getDate();
    }

    /**
     * 描述码
     * @return
     */
    private String getDescriptionCode(){
        return "M1S1D";
    }

    /**
     * 结束符
     * @return
     */
    private String getEnd(){
        return "\\r";
    }

}

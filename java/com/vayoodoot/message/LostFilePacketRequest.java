package com.vayoodoot.message;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 9, 2007
 * Time: 2:29:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class LostFilePacketRequest extends Message {

    protected static final String messageName = LostFilePacketRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String fileName;
    protected String filePackets;

    public LostFilePacketRequest() {
        super(messageName);
    }

    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("file_name")) {
            this.fileName = elementValue;
        }
        if (elementName.equalsIgnoreCase("file_packets")) {
            this.filePackets = elementValue;
        }


    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");

        // Get the Hashmap from the super class
        HashMap hm = getValuesMap();

        hm.put("FILE_NAME", fileName);
        hm.put("FILE_PACKETS", filePackets);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePackets() {
        return filePackets;
    }

    public void setFilePackets(String filePackets) {
        this.filePackets = filePackets;
    }

    public Message[] getSplittedMessages() throws MessageException {

        // The size of the packet should not be more that 512 bytes

        String[] packets = filePackets.split(",");
        List list = new ArrayList();
        LostFilePacketRequest packetRequest = null;
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<packets.length; i++) {
            if (i%150 == 0) {
                if (packetRequest != null) {
                    list.add(packetRequest);
                    packetRequest.setFilePackets(sb.toString());
                }

                sb.setLength(0);
                packetRequest = new LostFilePacketRequest();
                packetRequest.setLoginName(loginName);
                packetRequest.setSessionToken(sessionToken);
                packetRequest.setFileName(fileName);
                packetRequest.setIp(getIp());
                packetRequest.setPort(getPort());
            } else {
                sb.append(",");
            }
            sb.append(packets[i]);
        }
        if (!list.contains(packetRequest)) {
            packetRequest.setFilePackets(sb.toString());
            list.add(packetRequest);
        }

        return (Message[])list.toArray(new Message[list.size()]);

    }


    public static void main (String args[]) throws Exception {

        LostFilePacketRequest packetRequest = new LostFilePacketRequest();
        packetRequest.setLoginName("login");
        packetRequest.setSessionToken("session");
        packetRequest.setFileName("file");
        packetRequest.setFilePackets("107,108,109,110,111,216,235,236,237,238,239,693,704,705,1442,1443,1444,1445,2053,2054,2055,2056,2057,2058,2060,2223,2224,2225,2226,2227,2228,2229,2348,2475,2476,3562,3680,3681,3683,3684,3685,3686,3687,3688,3689,3690,3691,3692,3693,3694,3695,3696,3697,3698,3699,3700,3701,3702,3703,3704,3705,3706,3707,3708,3709,3710,3712,3713,3714,3715,3716,3775,3776,3777,3778,3779,3780,3781,3782,3783,3784,3785,3786,3811,3812,3813,3814,3819,3820,4025,4026,4904,4905,4906,4907,5106,5181,5684,5814,7430,7533,7552,7553,7554,7555,7556,7557,7558,7559,7561,7562,8565,8566,8567,8568,8569,8570,8571,8572,8573,8574,8575,8746,8760,8761,8762,8763,8764,8767,9751,9752,9753,9754,10629,11788,11789,11790,11892,11977,12175,12651,12652,12653,12654,12655,12656,12657,12658,12659,12660,12661,12663,12664,12690,12691,12692,12693,12694,12695,12696,12697,12698,12699,12700,12701,12702,12703,12704,12705,12706,12707,12708,12709,12710,12711,12712,12713,12714,12715,12716,12717,13066,13067,13069,13070,13071,13072,13073,13074,14484,14485,14878,15068,15087,15088,15089,15135,15139,15140,15141,15142,15143,15144,15145,15146,15147,15318,15319,15320,15321,15322,15323,15324,16110,16126,16127,16131,16761,16762,16763,16764,16765,16766,16767,16768,16769,16770,16771,16772,16773,16774,16775,16776,16777,16778,16779,16780,16781,16782,16783,16784,16785,16786,16787,16788,16789,16790,16791,16792,16793,16794,16795,16796,16797,16798,16799,16800,16801,16802,16803,16804,16805,16806,16807,16808,16809,16810,16811,16812,16813,16814,16815,16816,16817,16818,16819,16820,16821,16822,16823,16824,16825,16826,16827,16828,16829,16830,16831,16832,16833,16834,16835,16836,16837,16838,16839,16840,16841,16842,16843,16844,16845,16846,16847,16848,16849,16850,16851,16852,16853,16854,16855,16856,16857,16858,16859,16860,16861,16866,16867,17388,17389,17390,17391,17392,19067,19384,19403,19404,19405,19406,19407,19555,19556,19557,20131,20132,20133,20134,20135,20136,20137,20138,20139,21543,21544,21545,21546,21547,21548");


        Message[] messages = packetRequest.getSplittedMessages();
        for (int i=0; i<messages.length; i++) {
            System.out.println("Message:" + i + ":" + messages[i].getXMLString());
        }
        System.out.println("The messages: " + messages.length);

    }

}

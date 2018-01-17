//package com.newnius.chatroom.client;
//
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.google.gson.Gson;
//import com.newnius.util.CRCallback;
//import com.newnius.util.CRLogger;
//import com.newnius.util.CRMsg;
//import com.newnius.util.CRObject;
//
//
//public class Receiver implements Runnable {
//    private Map<Integer, CRCallback> callbacks = new HashMap<>();
//    private Socket socket;
//    private PrintWriter writer;
//    private BufferedReader reader;
//    private CRLogger logger = CRLogger.getLogger(getClass());
//
//    public Receiver(CRObject user) {
//        logger.debug(" started");
//    }
//
//    public boolean attachCallback(int code, CRCallback cbi) {
//        try {
//            if (callbacks.containsKey(code))
//                callbacks.remove(code);
//            callbacks.put(code, cbi);
//            logger.info("Attach callback, code:" + code);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return true;
//    }
//
//    public boolean detachCallback(int code) {
//        callbacks.remove(code);
//        logger.info("Detach callback, code:" + code);
//        return true;
//    }
//
//    @Override
//    public void run() {
//        try {
//            socket = new Socket(Config.getServerIP(), Config.getS2CPORT());
//            socket.setKeepAlive(true);
//            socket.setSoTimeout(0);
//            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
//            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            /* request to bind push */
//            String str = new Gson().toJson(new CRMsg(91, user));
//            logger.info("Sent: " + str);
//            writer.println(str);
//
//            /* heart packet */
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        while (!writer.checkError()) {
//                            Thread.sleep(5000);
//                            writer.println();
//                        }
//                        logger.info("Heart packet stopped.");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//            while (Config.isClientWork() && !socket.isClosed()) {
//                str = reader.readLine();
//                //connection closed
//                if (str == null)
//                    break;
//                //received heart packet
//                if (str.length() == 0)
//                    continue;
//                logger.info("Received:" + str);
//                CRMsg msg = new Gson().fromJson(str, CRMsg.class);
//                if (callbacks.containsKey(msg.getErrno())) {
//                    callbacks.get(msg.getErrno()).callback(msg);
//                }
//            }
//            close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void close() {
//        try {
//            logger.info("Close socket");
//            writer.close();
//            reader.close();
//            socket.close();
//
//            writer = null;
//            reader = null;
//            socket = null;
//        } catch (IOException ex) {
//            logger.error("Error occur during close.");
//        }
//    }
//
//}

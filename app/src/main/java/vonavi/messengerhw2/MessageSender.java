package vonavi.messengerhw2;

import com.google.gson.JsonObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import vonavi.messengerhw2.interfaces.ErrorsFromServer;
import vonavi.messengerhw2.interfaces.ErrorsInSystem;

/**
 * Created by Валентин on 29.11.2015.
 */
//отправитель сообщений
public class MessageSender implements Runnable, ErrorsFromServer, ErrorsInSystem {
    private final OutputStream mStream;
    public String message = "";
    public static final String HOST = "188.166.49.215";
    public static final int PORT = 7777;


    public MessageSender(Socket communicationSocket) throws IOException {
        if (communicationSocket.isClosed()) {
            communicationSocket = new Socket(HOST, PORT);
        }
        mStream = new BufferedOutputStream(communicationSocket.getOutputStream());
    }

    @Override
    public void run() {
        if (message.endsWith("}")) {
            byte[] data = message.getBytes(Charset.forName("UTF-8"));

            try {
                mStream.write(data);
                mStream.flush();
            } catch (IOException e) {
                MainActivity.handler.sendEmptyMessage(SERVER_ERROR);
                e.printStackTrace();
            }
        }
    }
}

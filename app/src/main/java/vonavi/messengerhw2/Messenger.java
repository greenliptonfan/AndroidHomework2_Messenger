package vonavi.messengerhw2;

import android.os.Handler;

import java.io.IOException;
import java.net.Socket;

import vonavi.messengerhw2.interfaces.ErrorsFromServer;
import vonavi.messengerhw2.interfaces.ErrorsInSystem;

public class Messenger implements Runnable, ErrorsFromServer, ErrorsInSystem {

    public static final String HOST = "188.166.49.215";
    public static final int PORT = 7777;
    public Thread senderThread;
    public Thread receiverThread;
    public Thread listenerThread;
    public MessageSender sender;
    public MessageReceiver receiver;
    public MessageListener listener;
    Socket socket;
    @Override
    public void run() {
        System.out.println("Welcome to messenger! Connecting to server...");

        try {
            socket = new Socket(HOST, PORT);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Abnormal interruption. Good bye");
        }
    }

    public void sendMessage(String msg) {
        try {
            sender = new MessageSender(socket);
            sender.message = msg;
            senderThread = new Thread(sender);
            senderThread.start();
            senderThread.join();
        } catch (InterruptedException | IOException e){
            MainActivity.handler.sendEmptyMessage(SERVER_ERROR);
        }
    }
    public String getResponce() {
        try {
            receiver = new MessageReceiver(socket);
            receiverThread = new Thread(receiver);
            receiverThread.start();
            receiverThread.join();
        } catch (InterruptedException | IOException e) {
            MainActivity.handler.sendEmptyMessage(SERVER_ERROR);
            e.printStackTrace();
        }
        return receiver.response;
    }
    /**public void responseListener(Handler handler) {
        try {
            listener = new MessageListener(socket);
            listener.handler = handler;
            listenerThread = new Thread(listener);
            listenerThread.start();
        }  catch (IOException e) {
            MainActivity.handler.sendEmptyMessage(SERVER_ERROR);
            e.printStackTrace();
        }
    }**/
}
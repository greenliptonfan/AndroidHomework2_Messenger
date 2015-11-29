package vonavi.messengerhw2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;

import vonavi.messengerhw2.interfaces.ErrorsFromServer;
import vonavi.messengerhw2.interfaces.ErrorsInSystem;

/**
 * Created by Валентин on 29.11.2015.
 */
//приемник сообщений
public class MessageReceiver implements Runnable, ErrorsFromServer,ErrorsInSystem {
//    private final Socket mSocket;
    private final InputStream mStream;
    public String response = "";

    @Override
    public void run() {
        try {
            byte[] data = new byte[32768]; //массив данных типа byte
            int offset = 0; //смещение
            int readBytes;
            String result;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                readBytes = mStream.read(data);
                if (readBytes != -1) {
                    outputStream.write(data, offset, readBytes);
                    outputStream.flush();
                    result = outputStream.toString();
                    response = result;
                }
        } catch (IOException e) {
            MainActivity.handler.sendEmptyMessage(SERVER_ERROR);
            e.printStackTrace();
        }
    }

    //абстрактный класс сообщения, методы которого должны быть добавлены в любой класс, использующий этот интерфейс
    public interface Message {
        void parse(JsonObject json);
        void doOutput();
    }
/**


**/
    // hashmap содержащий пары строчка-сообщение
//    public HashMap<String, Message> mMessages = new HashMap<>();
    // конструктор класса
    public MessageReceiver(Socket communicationSocket) throws IOException {
        mStream = new BufferedInputStream(communicationSocket.getInputStream()); //получить входящий поток

//        mMessages.put("welcome", new Welcome());
//        mMessages.put("register", new Registration());
//        mMessages.put("auth", new Auth());
    }
/**    //переопределение метода run
    @Override
    public void run() {
        try {
            boolean stop = false; //остановка
            boolean cleanup = false; //очистка
            byte[] data = new byte[32768]; //массив данных типа byte
            int offset = 0; //смещение
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JsonParser parser = new JsonParser();

            do {
                if (cleanup) {
                    outputStream.reset(); //очистить поток
                    offset = 0;
                    cleanup = false;
                }
                int readBytes = mStream.read(data); //прочитать входящий поток в массив data и получить количество
                if (readBytes != -1) {
                    outputStream.write(data, offset, readBytes); //записать данные во воходящий поток
                    offset += readBytes; //увеличить смещение
                    outputStream.flush(); //сбросить данные
                    String result = outputStream.toString("utf-8");
                    if (result.endsWith("}")) {
                        try {
                            JsonElement element = parser.parse(result);
                            JsonObject json = element.getAsJsonObject();
                            JsonElement action = json.get("action");
                            if (action != null) {
                                Message message = mMessages.get(action.getAsString());
                                if (message != null) {
                                    message.parse(json);
                                    message.doOutput();
                                    cleanup = true;
                                }
                            }
                        }
                        catch (JsonSyntaxException e) {
                            //not full json, continue
                        }
                    }
                }
                else {
                    stop = true;
                }
            } while (!stop);
            mStream.close();
            mSocket.close();
            System.out.println("Connection closed from server side. Good bye!");
        }
        catch (SocketException e) {
            //connection is closed;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    } **/
}


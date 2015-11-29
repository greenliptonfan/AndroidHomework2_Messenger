package vonavi.messengerhw2;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import vonavi.messengerhw2.interfaces.ErrorsInSystem;
import vonavi.messengerhw2.interfaces.ErrorsFromServer;

public class MainActivity extends AppCompatActivity implements ErrorsInSystem, ErrorsFromServer {

    public static Handler handler;
    static Messenger messenger;
    SplashScreenFrg splash;
    Toast toast;
    FragmentTransaction transaction;
    public static android.support.v7.app.ActionBar actionBar;
    int error = 0;
    String response;
    JsonElement action;
    public static String uid, sid, nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.hide();
/////////////////
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                error = msg.what;
                switch (msg.what) {
                    case CONNECTION_DISABLE:
                        toast = Toast.makeText(getApplicationContext(),
                                "Error of connection: network disable",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                    case SERVER_ERROR:
                        toast = Toast.makeText(getApplicationContext(),
                                "Service unavailable",
                                Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    case INVALID_PASS:
                        toast = Toast.makeText(getApplicationContext(),
                                "Invalid login or password",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                    case INVALID_DATA:
                        toast = Toast.makeText(getApplicationContext(),
                                "Json error",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                    case ALREADY_EXIST:
                        toast = Toast.makeText(getApplicationContext(),
                                "Login or nick or channel already exist",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                    case ALREADY_REGISTER:
                        toast = Toast.makeText(getApplicationContext(),
                                "User already register",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                    case NEED_REGISTER:
                        toast = Toast.makeText(getApplicationContext(),
                                "Need register",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                    case NEED_AUTH:
                        toast = Toast.makeText(getApplicationContext(),
                                "Need authorisation",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                    case UNKNOWN_ERROR:
                        toast = Toast.makeText(getApplicationContext(),
                                "Unknown error",
                                Toast.LENGTH_LONG);
                        toast.show();
                        errorOfAuth();
                        break;
                }
            }
        };
/////////////////

        if (!checkDataConnection()) {
           handler.sendEmptyMessage(CONNECTION_DISABLE);
        } else {
            try {
                messenger = new Messenger();
                Thread socketThread = new Thread(messenger);
                socketThread.start();
                socketThread.join();

                if (error == 0) {
                    response = messenger.getResponce();
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(response);
                    JsonObject json = element.getAsJsonObject();
                    action = json.get("action");

                    String actionAsString = action.getAsString();
                    splash = new SplashScreenFrg();
                    if (actionAsString.equals("welcome")) {

                        //вывод сообщения в логи

                        Welcome welcome = new Welcome();
                        welcome.parse(json);
                        welcome.doOutput();
                        // сплэшскрин и попытка залогиниться
                        Thread timerThread = new Thread() {
                            public void run() {
                                try {
                                    transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentMain, splash);
                                    transaction.commit();
                                    sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        timerThread.start();

                        final AuthRegFrg authReg = new AuthRegFrg();
                        Thread timeThread = new Thread() {
                            public void run() {
                                try {
                                    sleep(2000);
                                    transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentMain, authReg);
                                    transaction.commit();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        timeThread.start();

                    } else {
                        handler.sendEmptyMessage(UNKNOWN_ERROR);
                    }
                } else {
                    handler.sendEmptyMessage(UNKNOWN_ERROR);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void errorOfAuth() {
        try {
            messenger = new Messenger();
            Thread socketThread = new Thread(messenger);
            socketThread.start();
            socketThread.join();

            if (error == 0) {
                response = messenger.getResponce();
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response);
                JsonObject json = element.getAsJsonObject();
                action = json.get("action");

                String actionAsString = action.getAsString();
                splash = new SplashScreenFrg();
                if (actionAsString.equals("welcome")) {

                    //вывод сообщения в логи

                    Welcome welcome = new Welcome();
                    welcome.parse(json);
                    welcome.doOutput();
                    // сплэшскрин и попытка залогиниться
                    Thread timerThread = new Thread() {
                        public void run() {
                            try {
                                transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentMain, splash);
                                transaction.commit();
                                sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    timerThread.start();

                    final AuthRegFrg authReg = new AuthRegFrg();
                    Thread timeThread = new Thread() {
                        public void run() {
                            try {
                                sleep(2000);
                                transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentMain, authReg);
                                transaction.commit();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    timeThread.start();

                } else {
                    handler.sendEmptyMessage(UNKNOWN_ERROR);
                }
            } else {
                handler.sendEmptyMessage(UNKNOWN_ERROR);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         catch (Exception e) {
            System.out.println("Exception.");
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count <= 1) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    //класс сообщения-приветствия
    private class Welcome implements MessageReceiver.Message {
        private String message;
        private long time;
        //парсим json
        @Override
        public void parse(JsonObject json) {
            message = json.get("message").getAsString();
            time = json.get("time").getAsLong();
        }
        //выводим результат
        @Override
        public void doOutput() {
            Date serverTime = new Date(time);
            System.out.println(message);
            System.out.println("Server time is: "+serverTime);
            System.out.println();
            System.out.println("Enter one of commands: exit, login, register");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean checkDataConnection() {
        boolean status = false;
        ConnectivityManager connectivityMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityMgr.getActiveNetworkInfo() != null &&
                connectivityMgr.getActiveNetworkInfo().isAvailable() &&
                connectivityMgr.getActiveNetworkInfo().isConnected())
            status = true;
        return status;
    }
}

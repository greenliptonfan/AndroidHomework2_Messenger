package vonavi.messengerhw2;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import vonavi.messengerhw2.interfaces.ErrorsFromServer;
import vonavi.messengerhw2.interfaces.ErrorsInSystem;

/**
 * Created by Валентин on 29.11.2015.
 */
public class AuthRegFrg extends Fragment implements ErrorsFromServer, ErrorsInSystem {

    private static final String LOGIN = "login";
    private static final String PASS = "pass";
    public static boolean logout = false;

    SharedPreferences sPref;
    FragmentTransaction transaction;
    EditText login, password;
    Button btnLogin, btnRegister;
    int status = 0;
    String action = "";
    JsonElement actionElement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sPref = getActivity().getSharedPreferences("auth_parameters", getActivity().MODE_PRIVATE);
        String login = sPref.getString(LOGIN, "");
        String pass = sPref.getString(PASS, "");

        if (!checkDataConnection()) {
            MainActivity.handler.sendEmptyMessage(CONNECTION_DISABLE);
        } else {
            if (login != "" && pass != "" && !logout) {
                sendAuth(login, pass);
            }
        }

        return inflater.inflate(R.layout.frg_auth, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login = (EditText) view.findViewById(R.id.editLogin);
        password = (EditText) view.findViewById(R.id.editPass);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);

        View.OnClickListener oclBtnLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean err = false;
                if (login.getText().toString().trim().equalsIgnoreCase("")) {
                    login.setError("Login doesn't empty");
                    err = true;
                }

                if (password.getText().toString().trim().equalsIgnoreCase("")) {
                    password.setError("Password doesn't empty");
                    err = true;
                }

                if (!err) {
                    sendAuth(login.getText().toString(), password.getText().toString());
                }
            }
        };

        View.OnClickListener oclBtnRegister = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean err = false;
                if (login.getText().toString().trim().equalsIgnoreCase("")) {
                    login.setError("Login doesn't empty");
                    err = true;
                }

                if (password.getText().toString().trim().equalsIgnoreCase("")) {
                    password.setError("Password doesn't empty");
                    err = true;
                }

                if (!err) {
                    final EditText edtNick = new EditText(getActivity());
                    edtNick.setHint("Nick");

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Enter your nickname")
                            .setView(edtNick)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (edtNick.getText().toString().trim().equalsIgnoreCase("")) {
                                        edtNick.setError("Nickname doesn't empty");
                                    } else {
                                        sendRegister(login.getText().toString(), password.getText().toString(),
                                                edtNick.getText().toString());
                                    }
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        };
        btnLogin.setOnClickListener(oclBtnLogin);
        btnRegister.setOnClickListener(oclBtnRegister);
    }

    public void sendAuth(String login, String pass) {
        AuthSend as = new AuthSend();
        as.proceedInput(login, pass);
        MainActivity.messenger.sendMessage(as.getAction());
        String response = MainActivity.messenger.getResponce();

        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response);
            JsonObject json = element.getAsJsonObject();
            actionElement = json.get("action");
            action = actionElement.getAsString();

            if (action.equals("auth")) {
               AuthResponse ar = new AuthResponse();
                ar.parse(json);
                ar.doOutput();

                if (ar.status == 0) {
                    sPref = getActivity().getSharedPreferences("auth_parameters", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString(LOGIN, login);
                    editor.putString(PASS, pass);

                    editor.apply();
                    ChatsListFrg chats = new ChatsListFrg();
                    MainActivity.sid = ar.sid;
                    MainActivity.uid = ar.uid;
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentMain, chats);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    MainActivity.handler.sendEmptyMessage(status);
                }
            }
        } catch (Exception e) {
            MainActivity.handler.sendEmptyMessage(INVALID_DATA);
            e.printStackTrace();
        }
    }

    public void sendRegister(String login, String pass, String nick) {
        RegistrationSend rs = new RegistrationSend();
        rs.proceedInput(login, pass, nick);
        MainActivity.messenger.sendMessage(rs.getAction());
        String response = MainActivity.messenger.getResponce();

        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response);
            JsonObject json = element.getAsJsonObject();
            actionElement = json.get("action");
            action = actionElement.getAsString();

            if (action.equals("register")) {
                RegistrationResponse rr = new RegistrationResponse();
                rr.parse(json);
                rr.doOutput();

                if (rr.status == 0) {
                    sPref = getActivity().getSharedPreferences("auth_parameters", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString(LOGIN, login);
                    editor.putString(PASS, pass);

                    editor.apply();
                    ChatsListFrg chats = new ChatsListFrg();
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentMain, chats);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    MainActivity.handler.sendEmptyMessage(status);
                }
            }
        } catch (Exception e) {
            MainActivity.handler.sendEmptyMessage(INVALID_DATA);
            e.printStackTrace();
        }
    }

    private static class RegistrationSend {
        private String mLogin;
        private String mPass;
        private String mNick;

        public void proceedInput(String login, String pass, String nick) {
            mLogin = login;
            mPass = pass;
            mNick = nick;
        }

        public String getAction() {
            if (mLogin != null && mPass != null && mNick != null) {
                JsonObject action = new JsonObject();
                action.addProperty("action", "register");
                JsonObject data = new JsonObject();
                data.addProperty("login", mLogin);
                data.addProperty("pass", md5(mPass));
                data.addProperty("nick", mNick);
                action.add("data", data);
                return action.toString();
            }
            return null;
        }
    }

    private static class AuthSend {

        private String mLogin;
        private String mPass;


        public void proceedInput(String login, String pass) {
            mLogin = login;
            mPass = pass;
        }

        public String getAction() {
            if (mLogin != null && mPass != null) {
                JsonObject action = new JsonObject();
                action.addProperty("action", "auth");
                JsonObject data = new JsonObject();
                data.addProperty("login", mLogin);
                data.addProperty("pass", md5(mPass));
                action.add("data", data);
                return action.toString();
            }
            return null;
        }
    }
    //класс сообщения о регистрации
    private class RegistrationResponse implements MessageReceiver.Message {
        private int status;
        private String error;
        //парсим json
        @Override
        public void parse(JsonObject json) {
            JsonObject data = json.get("data").getAsJsonObject();
            status = data.get("status").getAsInt();
            error = data.get("error").getAsString();
        }
        //выводим результат регистрации
        @Override
        public void doOutput() {
            if (status == 0) {
                System.out.println("Registration successful!");
            }
            else {
                System.out.println("Error! " + error);
            }
        }
    }
    //класс сообщения об авторизации
    private class AuthResponse implements MessageReceiver.Message {
        private int status;
        private String error;
        private String uid;
        private String sid;
        //парсим json
        @Override
        public void parse(JsonObject json) {
            JsonObject data = json.get("data").getAsJsonObject();
            status = data.get("status").getAsInt();
            error = data.get("error").getAsString();
            uid = data.get("cid").getAsString();
            sid = data.get("sid").getAsString();
        }
        //выводим результат автоизации
        @Override
        public void doOutput() {
            if (status == 0) {
                System.out.println("Authorization successful!");
            }
            else {
                System.out.println("Error! " + error);
            }
        }
    }

    public static String md5(String s) {
        String md5sum = null;
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            md5sum = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5sum;
    }

    public boolean checkDataConnection() {
        boolean status = false;
        ConnectivityManager connectivityMgr = (ConnectivityManager)
                getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        if (connectivityMgr.getActiveNetworkInfo() != null &&
                connectivityMgr.getActiveNetworkInfo().isAvailable() &&
                connectivityMgr.getActiveNetworkInfo().isConnected())
            status = true;
        return status;
    }
}

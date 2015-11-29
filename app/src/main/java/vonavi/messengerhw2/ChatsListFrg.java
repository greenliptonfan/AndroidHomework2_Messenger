package vonavi.messengerhw2;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import vonavi.messengerhw2.interfaces.ErrorsFromServer;
import vonavi.messengerhw2.interfaces.ErrorsInSystem;

/**
 * Created by Валентин on 29.11.2015.
 */
public class ChatsListFrg extends Fragment implements ErrorsFromServer, ErrorsInSystem{

    ListView lvChatsList;
    Button btn_logout;
    FragmentTransaction transaction;

    private static final String LOGIN = "login";
    private static final String PASS = "pass";

    SharedPreferences sPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_chatslist, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        lvChatsList = (ListView) view.findViewById(R.id.listView);
        MainActivity.actionBar.show();
        MainActivity.actionBar.setTitle("Список чатов");

        btn_logout = (Button) view.findViewById(R.id.button_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPref = getActivity().getSharedPreferences("auth_parameters", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString(LOGIN, "");
                editor.putString(PASS, "");
                editor.apply();
                AuthRegFrg authRegFrg = new AuthRegFrg();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentMain, authRegFrg);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}

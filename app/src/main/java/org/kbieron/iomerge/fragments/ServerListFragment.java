package org.kbieron.iomerge.fragments;

import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.kbieron.iomerge.Preferences_;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.database.ServerBean;
import org.kbieron.iomerge.database.ServerDAO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@EFragment(R.layout.server_list)
public class ServerListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @Pref
    protected Preferences_ prefs;

    @ViewById(R.id.server_list_view)
    protected ListView listView;

    @ViewById(R.id.address)
    protected TextView addressView;

    @ViewById(R.id.port)
    protected TextView portView;

    @Bean
    protected ServerDAO serverDAO;

    private ArrayAdapter<ServerBean> listAdapter;

    private Pattern addressPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private Pattern portPattern = Pattern.compile("^\\d{1,4}$");

    @Click(R.id.addBtn)
    protected void add() {
        Matcher addressMatcher = addressPattern.matcher(addressView.getText().toString());
        Matcher portMatcher = portPattern.matcher(portView.getText().toString());

        if (!(addressMatcher.matches() && portMatcher.matches())) {
            showWrongFormatToast();
            return;
        }

        serverDAO.open();
        serverDAO.createServer(addressMatcher.group(), Integer.parseInt(portMatcher.group()));
        serverDAO.close();
        addressView.setText("");
        portView.setText("");
        refresh();
    }

    private void showWrongFormatToast() {
        Toast.makeText(getActivity(), R.string.wrong_address_format, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        refresh();
    }

    private void refresh() {
        serverDAO.open();
        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, serverDAO.getAllServers());
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(listAdapter);
        serverDAO.close();


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ServerBean item = listAdapter.getItem(position);
        serverDAO.open();
        serverDAO.deleteServer(item);
        serverDAO.close();
        refresh();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ServerBean item = listAdapter.getItem(position);
        prefs.edit() //
                .serverAddress().put(item.getAddress()) //
                .serverPort().put(item.getPort()) //
                .apply();
        refresh();
    }
}
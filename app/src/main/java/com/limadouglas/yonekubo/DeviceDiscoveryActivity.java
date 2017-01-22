package com.limadouglas.yonekubo;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceDiscoveryActivity extends ListActivity {

    ArrayAdapter<String> arrayAdapter;
    ButtonsRepository bancoDeDados;
    private boolean existe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        existe = false;
        bancoDeDados = new ButtonsRepository(this);

        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.layout_header, lv, false);
        //((TextView) header.findViewById(R.id.txt_disp_encontrados)).setText("\nDispositivos Próximos\n");
        lv.addHeaderView(header, null, false);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(arrayAdapter);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, intentFilter);
        dispPareados();
    }



    public void dispPareados() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        for(BluetoothDevice btDevice : btAdapter.getBondedDevices()) {
            arrayAdapter.add(btDevice.getName() + "\n" + btDevice.getAddress());
        }

    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(btDevice.getName() + "\n" + btDevice.getAddress());


                for(BluetoothDevice btDevice2 : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
                    if(btDevice.getAddress().equals(btDevice2.getAddress()))
                        existe = true;
                }

                if(!existe)
                    arrayAdapter.add(btDevice.getName() + "\n" + btDevice.getAddress());

                existe = false;

            }

        }
    };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String item = (String) getListAdapter().getItem((int)id);
        String btName = item.substring(0, item.indexOf("\n"));
        String btAddress = item.substring(item.indexOf("\n")+1, item.length());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("btName", btName);
        returnIntent.putExtra("btAddress", btAddress);
        setResult(RESULT_OK, returnIntent);

        bancoDeDados.setMAC(btAddress);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}

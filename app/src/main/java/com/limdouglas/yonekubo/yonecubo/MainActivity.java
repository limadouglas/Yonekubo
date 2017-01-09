package com.limdouglas.yonekubo.yonecubo;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static final int REQUEST_ENABLED_BT = 1;
    static final int REQUEST_DISCOVERY_BT = 2;
    private ListView listView;
    private SimpleCursorAdapter cursorAdapter;
    private Cursor cursor;
    ButtonsRepository banco;
    public EditText btnNome;
    public EditText btnComando;
    public static TextView viewStatic;

    List<BotaoEstrutura> listaBotao;

    ConnectionThread connection;
    private  long idContextSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // INICIO DO CODIGO AUTOMATICO.
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirDialogoAdicionarEditar(1);                   // abrir dialogo.
            }
        });
        // FIM DO CODIGO AUTOMATICO.


        banco = new ButtonsRepository(this);
        connection = null;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(this, "Adaptador não encontrado!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!btAdapter.isEnabled()) {
                Intent btIntentRequest = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(btIntentRequest, REQUEST_ENABLED_BT);
            }
        }

        viewStatic = (TextView) findViewById(R.id.view_static);     // bug no snackbar.

        carregarListView();

        if(banco.getMAC() != null) {
            connection = new ConnectionThread(banco.getMAC());
            connection.start();
        }

    }



    void carregarListView() {

        SQLiteDatabase db = banco.getReadableDatabase();
        listView = (ListView) findViewById(R.id.listView);


        registerForContextMenu(listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v,
                                           int pos, long id) {

                idContextSelecionado = id; //id do registo associado à linha clicada.
                //Toast.makeText(MainActivity.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        listaBotao = banco.getBotoes();
        ArrayList<String> itens = new ArrayList<>();

        if (listaBotao.size() > 0)
            for (BotaoEstrutura botao : listaBotao) {
                itens.add(botao.getNome());
            }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itens);
        listView.setAdapter(arrayAdapter);

        // registrando listview no OnItemClickListener para poder manipular o evento click.
       listView.setOnItemClickListener(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLED_BT:
                if (resultCode == RESULT_OK)
                    Toast.makeText(this, "Bluetooth Ativado", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "O aplicativo não funciona sem Bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case REQUEST_DISCOVERY_BT:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(findViewById(R.id.id_layout_main), "Conectando com " + data.getStringExtra("btName") + " / " + data.getStringExtra("btAddress"), Snackbar.LENGTH_LONG).setAction("action", null).show();

                    finalizarThread();

                   /* Um descanso rápido, para evitar bugs esquisitos. */
                    try {
                        Thread.sleep(1000);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }

                    connection = new ConnectionThread(banco.getMAC());
                    connection.start();

                    /* Um descanso rápido, para evitar bugs esquisitos. */
                    try {
                        Thread.sleep(1000);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
                break;
        }
    }



    void exibirDialogoAdicionarEditar(int operacao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialogo_cadastro, null);
        builder.setView(v);

        btnNome = (EditText) v.findViewById(R.id.btn_nome);
        btnComando = (EditText) v.findViewById(R.id.btn_comando);

        if(operacao == 1) {
            builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (btnNome.getText().length() > 0 && btnComando.getText().length() > 0) {
                        banco.setBotao((btnNome.getText().toString()), (btnComando.getText().toString()));
                        Toast.makeText(MainActivity.this, "Adicionado com Sucesso", Toast.LENGTH_SHORT).show();
                        reiniciarActivity();
                    } else {
                        btnNome.setText("");
                        btnComando.setText("");
                        Toast.makeText(MainActivity.this, "Erro: Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                        exibirDialogoAdicionarEditar(1);
                    }

                }

            });

        }else if(operacao == 2) {

            btnNome.setText(listaBotao.get((int)idContextSelecionado).getNome());
            btnComando.setText(listaBotao.get((int)idContextSelecionado).getComando());

            builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (btnNome.getText().length() > 0 && btnComando.getText().length() > 0) {
                        banco.editarBotao(listaBotao.get((int) idContextSelecionado).getId(), btnNome.getText().toString(), btnComando.getText().toString());
                        Toast.makeText(MainActivity.this, "Editado com Sucesso", Toast.LENGTH_SHORT).show();
                        reiniciarActivity();
                    } else {
                        btnNome.setText("");
                        btnComando.setText("");
                        Toast.makeText(MainActivity.this, "Erro: Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                        exibirDialogoAdicionarEditar(2);
                    }

                }

            });
        }

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, Menu.FIRST, 0, "Editar");
        menu.add(0, Menu.FIRST + 1, 0, "Excluir");
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getItemId() == Menu.FIRST) {
            exibirDialogoAdicionarEditar(2);
        } else {
            banco.excluirBotao(listaBotao.get((int)idContextSelecionado).getId());
            Toast.makeText(this, "Excluido com Sucesso!", Toast.LENGTH_SHORT).show();
            reiniciarActivity();
        }

        return super.onContextItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.meu_botao) {
            Intent encontrarDispIntent = new Intent(this, DeviceDiscoveryActivity.class);
            startActivityForResult(encontrarDispIntent, REQUEST_DISCOVERY_BT);
        }
        return super.onOptionsItemSelected(item);
    }



    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                Snackbar.make(viewStatic, "Ocorreu um erro durante a conexão D:" , Snackbar.LENGTH_LONG).setAction("action", null).show();
            else if(dataString.equals("---S"))
                Snackbar.make(viewStatic, "Conectado :D" , Snackbar.LENGTH_LONG).setAction("action", null).show();

        }
    };



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(connection != null) {
            byte[] data = listaBotao.get(i).getComando().getBytes();
            connection.write(data);
            //Toast.makeText(this, listaBotao.get(i).getComando(), Toast.LENGTH_SHORT).show();      // mostrar sinal enviado.
        } else
            Snackbar.make(viewStatic, "Selecione um dispositivo!" , Snackbar.LENGTH_LONG).setAction("action", null).show();

    }

    void finalizarThread() {
        if(connection != null)
            if(connection.running) {
                connection.cancel();
                /* Um descanso rápido, para evitar bugs esquisitos. */
                try {
                    Thread.sleep(1000);
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
    }

    void reiniciarActivity() {
        Intent intent = getIntent();
        finalizarThread();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        finalizarThread();
        super.onDestroy();
    }

}

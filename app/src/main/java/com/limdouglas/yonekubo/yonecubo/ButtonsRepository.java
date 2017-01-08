package com.limdouglas.yonekubo.yonecubo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class ButtonsRepository extends SQLiteOpenHelper {

    public String sql = "CREATE TABLE IF NOT EXISTS BOTAO (" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NOME TEXT," +
            "COMANDO TEXT);";

    public String sql2 = "CREATE TABLE IF NOT EXISTS MAC (" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "COD_MAC TEXT);";

    public ButtonsRepository(Context context) {
        super(context, "DB_CARRO_YONECUBO", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    public void setBotao(String nome, String comando) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("NOME", nome);
        values.put("COMANDO", comando);

        db.insert("BOTAO", null, values);
        db.close();
    }

    public List<BotaoEstrutura> getBotoes() {

        SQLiteDatabase db = getWritableDatabase();

        BotaoEstrutura btEstrutura;
        List<BotaoEstrutura> listBotao = new ArrayList<BotaoEstrutura>();

        Cursor cursor = db.query("BOTAO", new String[]{"COMANDO", "NOME"}, null, null, null, null, null);

        while(cursor.moveToNext()) {
            btEstrutura = new BotaoEstrutura();
            btEstrutura.setNome(cursor.getString(cursor.getColumnIndex("NOME")));
            btEstrutura.setComando(cursor.getString(cursor.getColumnIndex("COMANDO")));
            listBotao.add(btEstrutura);
        }

        db.close();
        cursor.close();

        return listBotao;
    }


    public void setMAC(String mac){
        SQLiteDatabase db = getWritableDatabase();

        // excluindo MAC antigo.
        db.delete("MAC", null, null);

        //gravando novo MAC.
        ContentValues values = new ContentValues();
        db.insert("MAC", null, values);
        db.close();
    }

    public String getMAC(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("MAC", new String[]{"COD_MAC"}, null, null, null, null, null);
        cursor.moveToNext();
        if(cursor != null)
            return cursor.getString(cursor.getColumnIndex("COD_MAC"));
        else
            return null;
    }
}

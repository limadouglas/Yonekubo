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

        Cursor cursor = db.query("BOTAO", null, null, null, null, null, null);

        while(cursor.moveToNext()) {
            btEstrutura = new BotaoEstrutura();
            btEstrutura.setId(cursor.getInt(cursor.getColumnIndex("_ID")));
            btEstrutura.setNome(cursor.getString(cursor.getColumnIndex("NOME")));
            btEstrutura.setComando(cursor.getString(cursor.getColumnIndex("COMANDO")));

            listBotao.add(btEstrutura);
        }

        db.close();
        cursor.close();

        return listBotao;
    }




    public void editarBotao(int id, String nome, String comando) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NOME", nome);
        values.put("COMANDO", comando);
        db.update("BOTAO", values, "_ID = ?", new String[]{String.valueOf(id)});
        db.close();

    }

    public void excluirBotao(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("BOTAO", "_ID = ?", new String[]{String.valueOf(id)});
        db.close();
    }





    public void setMAC(String mac){
        SQLiteDatabase db = getWritableDatabase();

        // excluindo MAC antigo.
        db.delete("MAC", null, null);

        //gravando novo MAC.
        ContentValues values = new ContentValues();
        values.put("COD_MAC", mac);
        db.insert("MAC", null, values);
        db.close();
    }

    public String getMAC(){
        SQLiteDatabase db = getReadableDatabase();
        String result;
        Cursor cursor = db.query("MAC", null, null, null, null, null, null);

        if(cursor.moveToNext())
            result = cursor.getString(cursor.getColumnIndex("COD_MAC"));
        else
            result = null;

        db.close();
        cursor.close();
        return result;
    }
}

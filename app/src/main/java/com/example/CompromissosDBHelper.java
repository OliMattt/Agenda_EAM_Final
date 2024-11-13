package com.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CompromissosDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "agenda.db";
    private static final int DATABASE_VERSION = 1;

    public CompromissosDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CompromissosDBSchema.CompromissoTable.NAME + " (" +
                CompromissosDBSchema.CompromissoTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CompromissosDBSchema.CompromissoTable.Cols.DESCRICAO + " TEXT, " +
                CompromissosDBSchema.CompromissoTable.Cols.DATA + " TEXT, " +
                CompromissosDBSchema.CompromissoTable.Cols.HORA + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CompromissosDBSchema.CompromissoTable.NAME);
        onCreate(db);
    }

    public long adicionarCompromisso(Compromisso compromisso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dataISO = sdf.format(compromisso.getDataHora().getTime());

        values.put("descricao", compromisso.getDescricao());
        values.put("data", dataISO);
        values.put("hora", compromisso.getHoraFormatada());

        long resultado = db.insert("compromissos", null, values);
        db.close();

        return resultado;
    }

    public List<Compromisso> buscarCompromissosPorData(String data) {
        List<Compromisso> compromissos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dataISO = sdf.format(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(data));

            cursor = db.query(
                    CompromissosDBSchema.CompromissoTable.NAME,
                    null,
                    CompromissosDBSchema.CompromissoTable.Cols.DATA + " = ?",
                    new String[]{dataISO},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String descricao = cursor.getString(cursor.getColumnIndexOrThrow(CompromissosDBSchema.CompromissoTable.Cols.DESCRICAO));
                    String hora = cursor.getString(cursor.getColumnIndexOrThrow(CompromissosDBSchema.CompromissoTable.Cols.HORA));
                    String dataStr = cursor.getString(cursor.getColumnIndexOrThrow(CompromissosDBSchema.CompromissoTable.Cols.DATA));

                    Calendar dataCalendar = Calendar.getInstance();
                    dataCalendar.setTime(sdf.parse(dataStr));

                    Compromisso compromisso = new Compromisso(dataCalendar, descricao);
                    compromisso.setHora(hora);
                    compromissos.add(compromisso);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("CompromissosDBHelper", "Erro ao buscar compromissos por data", e);
        } finally {

        }

        return compromissos;
    }
}

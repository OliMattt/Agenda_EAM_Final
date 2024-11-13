package com.example;

import android.annotation.SuppressLint;
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

public class CompromissosDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "agenda.db";
    private static final int DATABASE_VERSION = 1;

    public CompromissosDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long adicionarCompromisso(Compromisso compromisso) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Verifica se o compromisso já existe no banco
            if (!compromissoExiste(compromisso)) {
                ContentValues values = new ContentValues();
                values.put(CompromissosDBSchema.CompromissoTable.Cols.DESCRICAO, compromisso.getDescricao());
                values.put(CompromissosDBSchema.CompromissoTable.Cols.DATA, compromisso.getDataFormatada());
                values.put(CompromissosDBSchema.CompromissoTable.Cols.HORA, compromisso.getHoraFormatada());

                // Insere o compromisso no banco de dados
                id = db.insert(CompromissosDBSchema.CompromissoTable.NAME, null, values);
            }
        } catch (Exception e) {
            Log.e("CompromissosDB", "Erro ao adicionar compromisso", e);
        } finally {
            db.close();
        }
        return id;
    }

    // Verifica se o compromisso já existe
    private boolean compromissoExiste(Compromisso compromisso) {
        SQLiteDatabase db = this.getReadableDatabase();
        String data = compromisso.getDataFormatada();
        String descricao = compromisso.getDescricao();
        Cursor cursor = db.query(
                CompromissosDBSchema.CompromissoTable.NAME,
                null,
                CompromissosDBSchema.CompromissoTable.Cols.DATA + " = ? AND " +
                        CompromissosDBSchema.CompromissoTable.Cols.DESCRICAO + " = ?",
                new String[]{data, descricao},
                null,
                null,
                null
        );
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }


    // Método para buscar compromissos por data
    public List<Compromisso> buscarCompromissosPorData(String data) {
        List<Compromisso> compromissos = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();  // Abre o banco de dados para leitura
            cursor = db.query(
                    CompromissosDBSchema.CompromissoTable.NAME,
                    null,
                    CompromissosDBSchema.CompromissoTable.Cols.DATA + " = ?",
                    new String[]{data},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String descricao = cursor.getString(cursor.getColumnIndex(CompromissosDBSchema.CompromissoTable.Cols.DESCRICAO));
                    @SuppressLint("Range") String hora = cursor.getString(cursor.getColumnIndex(CompromissosDBSchema.CompromissoTable.Cols.HORA));
                    @SuppressLint("Range") String dataStr = cursor.getString(cursor.getColumnIndex(CompromissosDBSchema.CompromissoTable.Cols.DATA));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Calendar dataCalendar = Calendar.getInstance();
                        dataCalendar.setTime(sdf.parse(dataStr));

                        Compromisso compromisso = new Compromisso(dataCalendar, descricao);
                        compromisso.setDescricao(descricao);
                        compromissos.add(compromisso);
                    } catch (ParseException e) {
                        Log.e("CompromissosDB", "Erro ao converter data", e);
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("CompromissosDB", "Erro ao buscar compromissos por data", e);
        } finally {
        }

        return compromissos;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + CompromissosDBSchema.CompromissoTable.NAME + " (" +
                CompromissosDBSchema.CompromissoTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CompromissosDBSchema.CompromissoTable.Cols.DESCRICAO + " TEXT, " +
                CompromissosDBSchema.CompromissoTable.Cols.DATA + " TEXT, " +
                CompromissosDBSchema.CompromissoTable.Cols.HORA + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CompromissosDBSchema.CompromissoTable.NAME);
        onCreate(db);
    }

    // Método para buscar todos os compromissos
    public List<Compromisso> buscarTodosCompromissos() {
        List<Compromisso> compromissos = new ArrayList<>();
        Cursor cursor = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Query para selecionar todos os compromissos ordenados por data e hora
            cursor = db.query(
                    CompromissosDBSchema.CompromissoTable.NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    CompromissosDBSchema.CompromissoTable.Cols.DATA + " ASC, " +
                            CompromissosDBSchema.CompromissoTable.Cols.HORA + " ASC"
            );

            SimpleDateFormat sdfData = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String descricao = cursor.getString(cursor.getColumnIndex(CompromissosDBSchema.CompromissoTable.Cols.DESCRICAO));
                    @SuppressLint("Range") String dataStr = cursor.getString(cursor.getColumnIndex(CompromissosDBSchema.CompromissoTable.Cols.DATA));
                    @SuppressLint("Range") String horaStr = cursor.getString(cursor.getColumnIndex(CompromissosDBSchema.CompromissoTable.Cols.HORA));

                    // Converte data e hora para o tipo Calendar
                    Calendar dataHora = Calendar.getInstance();
                    dataHora.setTime(sdfData.parse(dataStr));
                    dataHora.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaStr.split(":")[0]));
                    dataHora.set(Calendar.MINUTE, Integer.parseInt(horaStr.split(":")[1]));

                    // Cria um novo objeto Compromisso e adiciona à lista
                    Compromisso compromisso = new Compromisso(dataHora, descricao);
                    compromissos.add(compromisso);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("CompromissosDB", "Erro ao buscar todos os compromissos", e);
        } finally {

        }

        return compromissos;
    }

    // Método para limpar o banco de dados
    public void limparBancoDeDados() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(CompromissosDBSchema.CompromissoTable.NAME, null, null); // Remove todos os registros
        } catch (Exception e) {
            Log.e("CompromissosDB", "Erro ao limpar o banco de dados", e);
        }
    }
}

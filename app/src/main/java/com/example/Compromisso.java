package com.example;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Compromisso {
    private Calendar dataHora;  // Usando Calendar para armazenar data e hora
    private String descricao;

    // Construtor
    public Compromisso(Calendar dataHora, String descricao) {
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Calendar getDataHora() {
        return dataHora;
    }

    public void setDataHora(Calendar dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Método para formatar data e hora como String
    public String getDataFormatada() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dataHora.getTime());
    }

    public String getHoraFormatada() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dataHora.getTime());
    }

    public void setHora(String hora) {
        try {
            String[] partesHora = hora.split(":");
            int horaInt = Integer.parseInt(partesHora[0]);
            int minutoInt = Integer.parseInt(partesHora[1]);
            this.dataHora.set(Calendar.HOUR_OF_DAY, horaInt);
            this.dataHora.set(Calendar.MINUTE, minutoInt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


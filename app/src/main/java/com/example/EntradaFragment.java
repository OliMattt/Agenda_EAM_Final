package com.example;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agenda_eam.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EntradaFragment extends Fragment {

    private EditText editDescricao;
    private Button btnData, btnHora, btnOk;
    private Calendar dataHora;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrada, container, false);

        // Inicializar elementos da tela
        editDescricao = view.findViewById(R.id.edit_descricao);
        btnData = view.findViewById(R.id.btn_data);
        btnHora = view.findViewById(R.id.btn_hora);
        btnOk = view.findViewById(R.id.btn_ok);
        dataHora = Calendar.getInstance();

        // Configurar os botões para abrir diálogos de data e hora
        btnData.setOnClickListener(v -> abrirDialogoData());
        btnHora.setOnClickListener(v -> abrirDialogoHora());
        btnOk.setOnClickListener(v -> registrarCompromisso());

        return view;
    }

    private void abrirDialogoData() {
        DataPickerFragment.newInstance((year, month, day) -> {
            dataHora.set(Calendar.YEAR, year);
            dataHora.set(Calendar.MONTH, month);
            dataHora.set(Calendar.DAY_OF_MONTH, day);

            btnData.setText(dateFormat.format(dataHora.getTime()));
            btnData.setError(null); // Limpa o erro se a data for selecionada
        }).show(getChildFragmentManager(), "dataPicker");
    }

    private void abrirDialogoHora() {
        TimePickerFragment.newInstance((view, hourOfDay, minute) -> {
            dataHora.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dataHora.set(Calendar.MINUTE, minute);

            btnHora.setText(timeFormat.format(dataHora.getTime()));
            btnHora.setError(null); // Limpa o erro se a hora for selecionada
        }).show(getChildFragmentManager(), "timePicker");
    }

    private void registrarCompromisso() {
        String descricao = editDescricao.getText().toString();

        // Validações
        if (descricao.isEmpty()) {
            editDescricao.setError("Descrição não pode estar vazia!");
            return;
        }
        if (btnData.getText().toString().equals("DATA")) {
            btnData.setError("Selecione a data!");
            return;
        }
        if (btnHora.getText().toString().equals("HORA")) {
            btnHora.setError("Selecione a hora!");
            return;
        }

        // Cria o objeto Compromisso
        Compromisso compromisso = new Compromisso(dataHora, descricao);
        CompromissosDBHelper dbHelper = new CompromissosDBHelper(requireContext());

        // Verificar se o compromisso já existe no banco de dados
        List<Compromisso> compromissosExistentes = dbHelper.buscarCompromissosPorData(compromisso.getDataFormatada());
        boolean compromissoJaExiste = false;

        for (Compromisso c : compromissosExistentes) {
            if (c.getDescricao().equals(compromisso.getDescricao()) &&
                    c.getHoraFormatada().equals(compromisso.getHoraFormatada())) {
                compromissoJaExiste = true;
                break;
            }
        }

        if (!compromissoJaExiste) {
            // Adiciona o compromisso ao banco de dados
            long resultado = dbHelper.adicionarCompromisso(compromisso);

            if (resultado != -1) {
                Toast.makeText(requireContext(), "Compromisso registrado com sucesso!", Toast.LENGTH_SHORT).show();

                // Limpar os campos após registrar
                editDescricao.setText("");
                btnData.setText("DATA");
                btnHora.setText("HORA");
            } else {
                Toast.makeText(requireContext(), "Erro ao registrar compromisso.", Toast.LENGTH_SHORT).show();
                Log.e("EntradaFragment", "Erro ao inserir compromisso no banco de dados.");
            }
        } else {
            Toast.makeText(requireContext(), "Compromisso já existe e não foi duplicado.", Toast.LENGTH_SHORT).show();
        }


    }
}

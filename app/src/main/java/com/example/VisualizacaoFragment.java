package com.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agenda_eam.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VisualizacaoFragment extends Fragment {

    private Button btnHoje, btnOutraData;
    private ImageButton btnLimpar;
    private TextView textListaCompromissos,textDataSelecionada;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizacao, container, false);

        // Inicializa os componentes
        btnHoje = view.findViewById(R.id.btn_hoje);
        btnOutraData = view.findViewById(R.id.btn_outra_data);
        btnLimpar = view.findViewById(R.id.btn_limpar);
        textListaCompromissos = view.findViewById(R.id.text_lista_compromissos);
        textDataSelecionada = view.findViewById(R.id.text_data_selecionada);

        // Carrega os compromissos de hoje ao criar o fragmento
        carregarCompromissosDeHoje();

        // Associa os eventos dos botões
        btnHoje.setOnClickListener(v -> carregarCompromissosDeHoje());
        btnOutraData.setOnClickListener(v -> abrirDialogoData());
        btnLimpar.setOnClickListener(v -> limparBancoDeDados());

        return view;
    }

    public void carregarCompromissosDeHoje() {
        // Obter a data de hoje formatada
        Calendar hoje = Calendar.getInstance();
        String dataHoje = dateFormat.format(hoje.getTime());

        textDataSelecionada.setText(dataHoje);;

        // Busca compromissos de hoje no banco de dados e exibe na interface
        CompromissosDBHelper dbHelper = new CompromissosDBHelper(requireContext());
        List<Compromisso> compromissosDeHoje = dbHelper.buscarCompromissosPorData(dataHoje);

        if (compromissosDeHoje == null || compromissosDeHoje.isEmpty()) {
            textListaCompromissos.setText("Nenhum compromisso para hoje.");
        } else {
            atualizarListaCompromissos(compromissosDeHoje);
        }
        // Ajusta o texto do botão para "Outra Data"
        btnOutraData.setText("Outra Data");
    }

    private void atualizarListaCompromissos(List<Compromisso> compromissosFiltrados) {
        StringBuilder sb = new StringBuilder();
        if (compromissosFiltrados.isEmpty()) {
            sb.append("Nenhum compromisso para esta data.");
        } else {
            // Exibe somente os compromissos cadastrados
            for (Compromisso compromisso : compromissosFiltrados) {
                sb.append(compromisso.getHoraFormatada()).append(": ").append(compromisso.getDescricao()).append("\n");
            }
        }
        textListaCompromissos.setText(sb.toString());
    }

    private void limparBancoDeDados() {
        CompromissosDBHelper dbHelper = new CompromissosDBHelper(requireContext());
        dbHelper.getWritableDatabase().delete(CompromissosDBSchema.CompromissoTable.NAME, null, null);
        textListaCompromissos.setText("");
        Toast.makeText(requireContext(), "Banco de dados limpo!", Toast.LENGTH_SHORT).show();
    }

    private void abrirDialogoData() {
        // Abre um diálogo para o usuário selecionar uma data
        DataPickerFragment.newInstance((year, month, day) -> {
            Calendar dataSelecionada = Calendar.getInstance();
            dataSelecionada.set(year, month, day);
            String dataFormatada = dateFormat.format(dataSelecionada.getTime());


            textDataSelecionada.setText(dataFormatada);

            // Busca compromissos do banco de dados para a data selecionada
            CompromissosDBHelper dbHelper = new CompromissosDBHelper(requireContext());
            List<Compromisso> compromissosSelecionados = dbHelper.buscarCompromissosPorData(dataFormatada);

            if (compromissosSelecionados.isEmpty()) {
                textListaCompromissos.setText("");
                Toast.makeText(requireContext(), "Nenhum compromisso para esta data.", Toast.LENGTH_SHORT).show();
            } else {
                // Atualiza a exibição com os compromissos da data selecionada
                atualizarListaCompromissos(compromissosSelecionados);
            }
        }).show(getChildFragmentManager(), "dataPicker");
    }
}
package br.edu.ufersa.numerico.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.ufersa.numerico.R;

public class Tab1 extends Fragment implements View.OnClickListener{
    private interfaceDataCommunicator mCallback;
    private EditText editN, editError, editValue, editIterations;
    private TextView txtValue;
    private double[][] matrix;
    private int row, column, size;
    private TextView txtMatrix;
    private Resources res;
    private ConstraintLayout inputLayout;
    private Button calculate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab1, container, false);
    }



    /**
     * Interface de comunicação da matriz
     */
    public interface interfaceDataCommunicator {
        void sendMatrix(double[][] matrix, double erro, int maxIterations);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewsIds();
        size = 0;//inicializar para a IDE parar de  chorar
        res = getActivity().getResources();

    }

    private void findViewsIds() {
        View view = getView();
        editN = (EditText) view.findViewById(R.id.editN);
        editError = (EditText) view.findViewById(R.id.editError);
        editValue = (EditText) view.findViewById(R.id.editValor);
        editIterations = (EditText) view.findViewById(R.id.editMaxNumberOfIterations);
        editValue.setOnClickListener(this);
        inputLayout = (ConstraintLayout) view.findViewById(R.id.inputLayout);

        Button ok = (Button) view.findViewById(R.id.btnOK);
        ok.setOnClickListener(this);

        calculate = (Button) view.findViewById(R.id.btnCalcular);
        calculate.setOnClickListener(this);

        txtValue = (TextView) view.findViewById(R.id.txtValue);
        txtMatrix = (TextView) view.findViewById(R.id.txtMatrix);

        //handle the enter of editValue
        editValue.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    try {
                        if(editValue != null) {
                            insertIntoMatrix(Double.parseDouble(editValue.getText().toString()));
                            editValue.setText(null);
                            return true;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

    }

    private void insertIntoMatrix(Double value){
        if (row < size) {
            if (column < size + 1) {
                matrix[row][column] = value;
                insertValueIntoMatrixTxt(column, String.valueOf(value));
                column++;
                if (column < size + 1)
                    updateMatrixCurrentIndexNumbers((row+1),(column+1));
                   // txtValue.setText(String.format(res.getString(R.string.matrix_position), (row+1), (column+1)));
                else if (row + 1 < size + 1)
                    updateMatrixCurrentIndexNumbers((row+2),(1));
                    //txtValue.setText(String.format(res.getString(R.string.matrix_position), (row+2), 1));

            } else {
                column = 0;
                row++;

                if (row < size) {
                    matrix[row][column] = value;
                    insertValueIntoMatrixTxt(column, String.valueOf(value));
                    column++;
                    updateMatrixCurrentIndexNumbers((row+1),(column+1));
                    //txtValue.setText(String.format(res.getString(R.string.matrix_position), (row+1), (column+1)));

                } else
                    Toast.makeText(getContext(), "matrix já preenchida.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Manipula os apertos de botões
     * @param v id do botão
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOK:
                showLayouts();
                size = Integer.parseInt(editN.getText().toString());
                matrix = new double[size][size+1];
                row = 0;
                column = 0;
                txtValue.setText(String.format(res.getString(R.string.matrix_position), 1, 1));
                Toast.makeText(getContext(), res.getString(R.string.new_matrix), Toast.LENGTH_SHORT).show();
                txtMatrix.setText("");

                //tentar esconder o teclado
                try{
                    // Check if no view has focus:
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;

            case R.id.btnCalcular:
                    mCallback.sendMatrix(matrix, getError(), getMaxIterations());
                    ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.container);
                    viewPager.setCurrentItem(1);
                    break;
        }
    }

    /**
     * atualiza o número dos índices no textview que orienta os índices atuais
     * @param row linha atual
     * @param column coluna atual
     */
    private void updateMatrixCurrentIndexNumbers(int row, int column){
        txtValue.setText(String.format(res.getString(R.string.matrix_position), row, column));

    }

    /**
     * insere um valor no textView da matriz em construção
     * @param column índice da coluna, para orientar a quebra de linha
     * @param value valor a ser inserido
     */
    private void insertValueIntoMatrixTxt(int column, String value){

        if(column == 0)
            txtMatrix.append("\n");

        txtMatrix.append(String.valueOf(value) + " ");

    }

    /**
     * mostra os layouts ocultos
     */
    private void showLayouts(){
        inputLayout.setVisibility(View.VISIBLE);
        calculate.setVisibility(View.VISIBLE);
    }

    private double getError(){
        double error;
        try{
            error = Double.parseDouble(String.valueOf(editError.getText()));
            return error;
        }catch (Exception e){
            Toast.makeText(getContext(), res.getString(R.string.wrong_error), Toast.LENGTH_LONG).show();
            return 0.05;
        }
    }

    private int getMaxIterations() {
        int maxInterations;
        try{
            maxInterations = Integer.parseInt(String.valueOf(editIterations.getText()));
            return maxInterations;
        }catch (Exception e){
            Toast.makeText(getContext(), res.getString(R.string.wrong_interations), Toast.LENGTH_LONG).show();
            return 50;
        }
    }

    /**
     * Versões antigas do android usam a Activity, enquanto as mais novas usam o Context como parâmetro
     * @param context activity or context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (interfaceDataCommunicator) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement interface");
            }
        }else{
            try {
                mCallback = (interfaceDataCommunicator) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement interface");
            }
        }
    }

    @Override
    public void onDetach() {
        mCallback = null; //avoid leaking
        super.onDetach();
    }

}

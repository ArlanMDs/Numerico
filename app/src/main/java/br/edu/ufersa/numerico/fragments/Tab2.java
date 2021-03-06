package br.edu.ufersa.numerico.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.ufersa.numerico.R;
import br.edu.ufersa.numerico.model.Jacobi;


public class Tab2 extends Fragment {
    public TextView log;
    private TextView info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab2, container, false);
        
    }

    public void updateMatrix(double[][] matrix, double error, int maxIterations) {
        info.setVisibility(View.GONE);
        log.setText("");
        Jacobi jacobiMatrix = new Jacobi(this, matrix);

        jacobiMatrix.showMatrix();
        if (!jacobiMatrix.makeDominant()) {
            log.append(Html.fromHtml("<font color=#e01515>O sistema não é diagonalmente dominante: O método não garante a convergência.</font>"));
        }else {
            log.append(Html.fromHtml("<font color=#e01515>O sistema é dominante: </font>"));
            log.append("\n\n");
            jacobiMatrix.showMatrix();
            log.append("Critério das linhas: ");
            if (jacobiMatrix.lineCriterionIsValid()) {
                log.append("\n\n");
                log.append(Html.fromHtml("<font color=#e01515>O critério das linhas garante a convergência. </font>"));
                log.append("\n\nIterações:\n\n");
                jacobiMatrix.solve(error, maxIterations);
            }else
                log.append(Html.fromHtml("<font color=#e01515>O critério das linhas não garante a convergência. O método não será aplicado. </font>"));
        }

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();


    }

    private void findViews() {
        log = (TextView)getView().findViewById(R.id.txtLog);
        info = (TextView)getView().findViewById(R.id.txtInfo);

    }


}

/*
 *  GNU General Public License
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

/* * This class provides a simple implementation of the Jacobi method for solving
 * systems of linear equations. */

/*
  How to use:
  The program reads an augmented matrix from standard input,
  for example:

   3
   5 -2  3 -1
  -3  9  1  2
   2 -1 -7  3

  If the matrix isn't diagonally dominant the program tries
  to convert it(if possible) by rearranging the rows.
*/
package br.edu.ufersa.numerico.model;


import android.text.Html;

import java.text.DecimalFormat;
import java.util.Arrays;

import br.edu.ufersa.numerico.fragments.Tab2;


public class Jacobi {

    private double[][] M;
    private final Tab2 context;

    public Jacobi(Tab2 context, double[][] matrix){
        this.context = context;
        M = matrix;

    }

    public void showMatrix() {

        int n = M.length;
        for (double[] aM : M) {
            for (int j = 0; j < n + 1; j++)
                context.log.append(aM[j] + " ");
            context.log.append("\n\n");
        }
    }

    /**
     * aplica o critério das linhas na matriz
     * @return resultado do critério
     */
    public boolean lineCriterionIsValid(){
        int n = M.length;
        double alfa[] = new double[n];
        boolean x = true;

        for(int i=0; i < n; i++){
            double sum = 0.0;
            for(int j=0; j < n; j++){
                if(j != i){
                    sum += Math.abs(M[i][j]) / Math.abs(M[i][i]);
                    alfa[i] = sum;
                    if (sum >= 1)
                        x = false;
                }
            }
        }

        context.log.append("\n\n"+ Arrays.toString(formatArray(alfa)));

        return x;
    }

    private boolean transformToDominant(int r, boolean[] V, int[] R){
        int n = M.length;
        if (r == M.length) {
            double[][] T = new double[n][n + 1];
            for (int i = 0; i < R.length; i++) {
                System.arraycopy(M[R[i]], 0, T[i], 0, n + 1);
            }

            M = T;

            return true;
        }

        for (int i = 0; i < n; i++) {
            if (V[i]) continue;

            double sum = 0;

            for (int j = 0; j < n; j++)
                sum += Math.abs(M[i][j]);

            if (2 * Math.abs(M[i][r]) > sum) { // diagonally dominant?
                V[i] = true;
                R[r] = i;

                if (transformToDominant(r + 1, V, R))
                    return true;

                V[i] = false;
            }
        }

        return false;
    }


    /**
     * Returns true if is possible to transform M(data member) to a diagonally
     * dominant matrix, false otherwise.
     */
    public boolean makeDominant() {
        boolean[] visited = new boolean[M.length];
        int[] rows = new int[M.length];

        Arrays.fill(visited, false);

        return transformToDominant(0, visited, rows);
    }


    /**
     * Applies Jacobi method to find the solution of the system
     * of linear equations represented in matrix M.
     * M is a matrix with the following form:
     * a_11 * x_1 + a_12 * x_2 + ... + a_1n * x_n = b_1
     * a_21 * x_1 + a_22 * x_2 + ... + a_2n * x_n = b_2
     * .                 .                  .        .
     * .                 .                  .        .
     * .                 .                  .        .
     * a_n1 * x_n + a_n2 * x_2 + ... + a_nn * x_n = b_n
     */
    public void solve(double error, int maxIterations) {
        int iterations = 0;
        int n = M.length;
        double[] X = new double[n]; // Approximations
        double[] P = new double[n]; // Prev
        double[] abError = new double[n];
        DecimalFormat df = new DecimalFormat("#.000");

        Arrays.fill(X, 0);
        Arrays.fill(P, 0);

        while (true) {
            for (int i = 0; i < n; i++) {
                double sum = M[i][n]; // b_n

                for (int j = 0; j < n; j++)
                    if (j != i)
                        sum -= M[i][j] * P[j];

                X[i] = 1 / M[i][i] * sum;
            }

            context.log.append("\n\nX_" + iterations + " = {");

            for (int i = 0; i < n; i++)
                context.log.append(Html.fromHtml(Double.parseDouble(df.format(X[i])) + "<font color=#e01515> ; </font>"));

            context.log.append("}");


            iterations++;
            if (iterations == 1)
                continue;

            boolean stop = true;

            //daria para simplificar para um laço só, por enquanto fica assim para ficar mais visível
            for (int i = 0; i < n; i++)
                abError[i] = Math.abs(X[i] - P[i]);

            if (getMax(abError) > error)
                stop = false;

            context.log.append("\n   Erro absoluto: "+ Arrays.toString(formatArray(abError)));
            context.log.append("\n   Erro relativo: "+ String.valueOf(Double.parseDouble(df.format(getMax(abError)/getMax(X)))));


            if (stop || iterations == maxIterations) {
                break;
            }
            P = X.clone();
        }
    }

    /**
     * returns the highest value in an array
     * @param inputArray array
     * @return highest value
     */
    private double getMax(double[] inputArray){
        double maxValue = inputArray[0];
        for(int i=1;i < inputArray.length;i++){
            if(inputArray[i] > maxValue){
                maxValue = inputArray[i];
            }
        }
        return maxValue;
    }

    private double[] formatArray(double[] array){

        DecimalFormat df = new DecimalFormat("#.000");

        for(int i=0; i < array.length; i++){
            array[i] = Double.parseDouble(df.format(array[i]));
        }
        return array;
    }
}
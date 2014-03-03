/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;

/**
 *
 * @author Peru
 */
public class DataPoint {
public int[] attributes;
public double weights;//para almacenar los pesos para adaboost

public DataPoint(int numAttributes){
        attributes= new int[numAttributes];
}

public DataPoint (int numAttributes, double weight){
        attributes= new int[numAttributes];
        weights = weight;
}

public void setWeight(double weight){
    this.weights=weight;
}

}

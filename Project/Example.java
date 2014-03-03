/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;

/**
 *
 * @author Peru
 */
public class Example {

    DataPoint point;
    int classLabel;
    double weight;


    public Example(DataPoint instance, int label, double weight) {
                this.point = instance;
                this.classLabel = label;
                this.weight = weight;
            }

  public double getWeight(int attribute) {
                return point.weights;
            }
  public void setWeight(double newWeight) {
                this .weight = newWeight;
            }

}
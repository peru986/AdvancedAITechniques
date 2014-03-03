/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;

import java.util.ArrayList;

/**
 *
 * @author Peru
 */
public class AdaBoost {

   private int maxRounds =100;
   private DecisionStump baseLearner;

   public AdaBoost(DecisionStump baseLearner,int maxRounds){
       this.maxRounds= maxRounds;
       this.baseLearner = baseLearner;
   }

   public int getMaxRounds() {
                return maxRounds;
            }

   public void setMaxRounds(int n) {
                this .maxRounds = n;
            }
   public DecisionStump getBaseLearner() {
                return baseLearner;
            }

   public void setBaseLearner(DecisionStump learner) {
                this .baseLearner = learner;
            }

   public void batchTrain(ArrayList dataSet){
      double [][] weightedData = new double[dataSet.size()][maxRounds];


   }



}

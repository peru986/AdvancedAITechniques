/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Peru
 */
public class Main {
static String path= "";
static int selection =0;
static BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));


    public static void choosePath(){
        System.out.println("Select the database you want as training set.");
        System.out.println("NOTE: IT MUST HAVE THE FIRST LINE WITH THE ATTRIBUTES NAMES SEPARATED BY SPACES");
        System.out.println();
      /*  System.out.println("3. Mushrooms");
        System.out.println("4. Soy-beans");
        System.out.println("5. Chess");
        System.out.println("6. Tik-Tak-Toe");*/
        System.out.println("Please write the full path of the database file and press enter:");
        try{
            path = (teclado.readLine());
            }catch (Exception e){}

    }

    public static void prune (DecisionTree arbol){
        String umbral = new String();
        double umbralD = 0;
        System.out.println("Enter an threshold (0<threshold<1) ");
        try{
            umbral = (teclado.readLine());
             umbralD = Double.valueOf(umbral).doubleValue();
             arbol.gainThreshold = umbralD;

            }catch (Exception e){}

    }




 public static void main(String[] args) throws Exception {
     int num=0;
     int estado;
     long startTime, endTime,totalTime;
    

     System.out.println("----Welcome----");
     System.out.println("Select the type of classifier");
     System.out.println("1. Naive Bayes");
     System.out.println("2. Decision Tree");
     
     try{
     selection = Integer.parseInt(teclado.readLine());
     }catch (Exception e){}
     //System.out.println(selection);

     switch (selection) {
        case 1 :

            naiveBayes clasiffier = new naiveBayes();
            choosePath();
            estado = clasiffier.readData(path);
            if (estado >=0){
                System.out.println("Select the validation type:");
                System.out.println("1. 10-fold Cross-Validation");
                System.out.println("2. Bias-Variance Decomposition");
                System.out.println("3. No validation");
                try{
                     selection = Integer.parseInt(teclado.readLine());
                 }catch (Exception e){}

                switch (selection){

                    case 1:
                        System.out.println();
                        startTime = System.nanoTime();

                        clasiffier.kfoldValidation(10);

                        endTime = System.nanoTime();
                        totalTime = (endTime-startTime);
                        System.out.println( totalTime + " NanoSeconds");

                        System.exit(0);

                    case 2:
                        System.out.println();
                        startTime = System.nanoTime();

                        clasiffier.BVDescompose(clasiffier.db);

                        endTime = System.nanoTime();
                        totalTime = (endTime-startTime);
                        System.out.println( totalTime + " NanoSeconds");

                        System.exit(0);

                    case 3:
                        System.out.println();
                        startTime = System.nanoTime();

                        clasiffier.trainBayes();

                        endTime = System.nanoTime();
                        totalTime = (endTime-startTime);
                        System.out.println( totalTime + " NanoSeconds");
                        System.exit(0);

                }


            }
 


        case 2 :
            DecisionTree decisionTree = new DecisionTree();
            choosePath();
            estado = decisionTree.readData(path);

            if (estado >=0){
                System.out.println("Select the validation type:");
                System.out.println("1. 10-fold Cross-Validation");
                System.out.println("2. Bias-Variance Decomposition");
                System.out.println("3. Prunning: establish an information gain threshold");
                System.out.println("4. No validation");
                try{
                     selection = Integer.parseInt(teclado.readLine());
                 }catch (Exception e){}

                switch (selection){

                    case 1:
                        System.out.println();
                        startTime = System.nanoTime();

                        decisionTree.kFoldValidation(10);

                        endTime = System.nanoTime();
                        totalTime = (endTime-startTime);
                        System.out.println( totalTime + " NanoSeconds");

                        System.exit(0);

                    case 2:
                        System.out.println();
                        startTime = System.nanoTime();

                        decisionTree.BVDescompose();

                        endTime = System.nanoTime();
                        totalTime = (endTime-startTime);
                        System.out.println( totalTime + " NanoSeconds");

                        System.exit(0);

                    case 3:
                        prune(decisionTree);

                        System.out.println("Select the validation type:");
                        System.out.println("1. 10-fold Cross-Validation(PRUNNING)");
                        System.out.println("2. Bias-Variance Decomposition(PRUNNING)");
                        System.out.println("3. No validation(PRUNNING)");
                        try{
                             selection = Integer.parseInt(teclado.readLine());
                         }catch (Exception e){}

                        switch (selection){

                            case 1:
                                System.out.println();
                                startTime = System.nanoTime();

                                decisionTree.kFoldValidation(10);

                                endTime = System.nanoTime();
                                totalTime = (endTime-startTime);
                                System.out.println( totalTime + " NanoSeconds");

                                System.exit(0);

                            case 2:
                                System.out.println();
                                startTime = System.nanoTime();

                                decisionTree.BVDescompose();

                                endTime = System.nanoTime();
                                totalTime = (endTime-startTime);
                                System.out.println( totalTime + " NanoSeconds");

                                System.exit(0);

                            case 3:
                                System.out.println();
                                startTime = System.nanoTime();

                                decisionTree.descomposeNode(decisionTree.root);

                                endTime = System.nanoTime();
                                totalTime = (endTime-startTime);
                                System.out.println( totalTime + " NanoSeconds");

                                System.exit(0);


                        }


                    case 4:
                        System.out.println();
                        startTime = System.nanoTime();

                        decisionTree.descomposeNode(decisionTree.root);

                        endTime = System.nanoTime();
                        totalTime = (endTime-startTime);
                        System.out.println( totalTime + " NanoSeconds");

                        System.exit(0);





            

        default: System.out.println("Please insert one of the numbers");
        }

    
   


       }
            default: System.out.println("Please insert one of the numbers");

      }
     }
    }




/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Peru
 */
public class DecisionStump {

    static ArrayList db= new ArrayList();
    double [][] m_Distribution;
    int numAttributes;
    ArrayList <ArrayList> domains;
    String[] attributeNames;
    Node root= new Node();
    double m_SplitPoint=1;
    double currVal;
    int m_AttIndex;


    /** The distribution of class values or the means in each subset. */
   // private double[][] m_Distribution;
    ArrayList<DataPoint> m_instances;
    int numClases = 2;

    public void buildClassifier(ArrayList instances){
        int bestAtrib = -1;
        double bestVal = 99999;
        double bestPoint = -99999;
        double currVal;

        double[][] bestDist = new double[3][domains.get(numAttributes-1).size()];
        m_instances = new ArrayList(instances);
        numClases = domains.get(numAttributes-1).size();

        //for each attribute
        boolean first = true;
        for (int i = 0; i< numAttributes-1;i++){
            m_Distribution = new double [3][numClases];

            currVal = findSplit(i);
            System.out.println("el atributo elegido es: "+currVal);

        
            if ((first) || (currVal<bestVal)){
                bestVal = currVal;
                bestAtrib = i;
                bestPoint = m_SplitPoint;
                for (int j=0;j<3 ; j++){
                    System.arraycopy(m_Distribution[j], 0, bestDist[j],0, numClases);
                    System.out.println("m_dist: "+m_Distribution[j]);
                    System.out.println("bestDist : "+bestDist[j]);
                }

             }

            //first attribute investigated
            first = false;
        }

        // Set attribute, split point and distribution.
        m_AttIndex = bestAtrib;
        m_SplitPoint = bestPoint;
        m_Distribution = bestDist;

        for (int i=0; i<m_Distribution.length;i++){
            double sumCounts = sum(m_Distribution[i]);


           if (sumCounts == 0) { // This means there were only missing attribute values
                            System.arraycopy(m_Distribution[2], 0,
                                    m_Distribution[i], 0,
                                    m_Distribution[2].length);
                            normalize(m_Distribution[i]);
                        } else {
                             normalize(m_Distribution[i], sumCounts);
            }

          }

        // Save memory
       // m_instances = new ArrayList(m_instances,0);

    }

public static/*@pure@*/double sum(double[] doubles) {

                double sum = 0;

                for (int i = 0; i < doubles.length; i++) {
                    sum += doubles[i];
                }
                return sum;
            }

public static void normalize(double[] doubles) {

                double sum = 0;
                for (int i = 0; i < doubles.length; i++) {
                    System.out.println("doubles["+i+"] = "+doubles[i]);
                    sum += doubles[i];
                }
                normalize(doubles, sum);
            }

 public static void normalize(double[] doubles, double sum) {

                if (Double.isNaN(sum)) {
                    throw new IllegalArgumentException(
                            "Can't normalize array. Sum is NaN.");
                }
                if (sum == 0) {
                    // Maybe this should just be a return.
                    throw new IllegalArgumentException(
                            "Can't normalize array. Sum is zero.");
                }
                for (int i = 0; i < doubles.length; i++) {
                    doubles[i] /= sum;
                }
            }



    public double findSplit(int index){
        double bestVal = 9999;
       // double currVal = 1;
        double[][] counts = new double [domains.get(index).size()+1][numClases];
        double[] sumCounts = new double[numAttributes-1];
        double[][] bestDist = new double[3][numAttributes-1];

        //compute counts for all values
        for (int i=0;i < m_instances.size();i++){
            DataPoint inst = m_instances.get(i);
            System.out.println("peso[" +i+"]="+inst.weights);
            counts[inst.attributes[index]][inst.attributes[numAttributes-1]] += inst.weights;
        }

        //compute sum of counts
        for (int i=0; i< domains.get(index).size();i++){
            for (int j=0;j<domains.get(numAttributes-1).size();j++){
                sumCounts[j] += counts[i][j];
            }
        }

        //make split counts for each possible split and evaluate
        System.arraycopy(counts[domains.get(index).size()], 0, m_Distribution[2], 0, domains.get(numAttributes-1).size());

        for (int i = 0; i<domains.get(index).size();i++){
            for (int j = 0;j< domains.get(numAttributes-1).size();j++){
                 m_Distribution[0][j] = counts[i][j];
                 m_Distribution[1][j] = sumCounts[j] - counts[i][j];
            }
             currVal = entropyConditionedOnRows(m_Distribution);

            if (currVal < bestVal){
                bestVal = currVal;
                m_SplitPoint =(double) i;
                for (int j =0; j<3;j++){
                    System.arraycopy(m_Distribution[j], 0, bestDist[j], 0, domains.get(numAttributes-1).size());
                }
            }
        }
        System.out.println("SPLIT POINT: "+ m_SplitPoint);
        System.arraycopy(sumCounts, 0, bestDist[2], 0,domains.get(numAttributes-1).size());




        m_Distribution = bestDist;
                return bestVal;
    }

    private static double lnFunc(double num) {

                // Constant hard coded for efficiency reasons
                if (num < 1e-6) {
                    return 0;
                } else {
                    return num * Math.log(num);
                }
            }

    public static boolean eq(double a, double b) {
        double SMALL = 1e-6;

                return (a - b < SMALL) && (b - a < SMALL);
            }

 public static double entropyConditionedOnRows(double[][] matrix) {

                double returnValue = 0, sumForRow, total = 0;

                for (int i = 0; i < matrix.length; i++) {
                    sumForRow = 0;
                    for (int j = 0; j < matrix[0].length; j++) {
                        returnValue = returnValue + lnFunc(matrix[i][j]);
                        sumForRow += matrix[i][j];
                    }
                    returnValue = returnValue - lnFunc(sumForRow);
                    total += sumForRow;
                }
                if (eq(total, 0)) {
                    return 0;
                }
                return -returnValue / (total * Math.log(2));
            }


    public int getSymbolValue(int attribute, String symbol){
    int index = domains.get(attribute).indexOf(symbol); //devuelve -1 si no contiene el valor
    if (index <0){
        domains.get(attribute).add(symbol);
        return domains.get(attribute).size() -1; //si no esta dicho simbolo se anhade

    }
    return index;
}

    public int readData(String filename) throws IOException{

    System.out.println("Leyendo datos...");
    FileInputStream in = null;


    try{
        File inputFile = new File(filename);
        in = new FileInputStream(inputFile);
        System.out.println("paso 0.01");
    }catch (Exception e){
        System.out.println("unable to open data file:"+ filename + "\n"+ e );
        return 0;
    }
    System.out.println("antes buffer");
    BufferedReader bufin = new BufferedReader (new InputStreamReader (in));
    System.out.println("leido buffer");
    String input;
    System.out.println("paso 0.1");
    while (true){
        input = bufin.readLine();
        if (input==null){
            System.err.println( "No data found in the data file: " + filename + "\n");
            return 0;
        }
        if (input.startsWith("//")) continue;
        if (input.equals("")) continue;
        break;

    }

    System.out.println("paso1...");
    StringTokenizer tokenizer = new StringTokenizer(input);
    numAttributes = tokenizer.countTokens();
    System.out.println("numero de atributos detectados: "+numAttributes);
    if (numAttributes <=1) {
        System.err.println( "Read line: " + input);
        System.err.println( "Could not obtain the names of attributes in the line");
        System.err.println( "Expecting at least one input attribute and one output attribute");
        return 0;
    }

    domains= new ArrayList(numAttributes);
    for (int i = 0; i<numAttributes;i++) domains.add(i, new ArrayList());  //inicializo el arraylist con el tamaño num atributos
    attributeNames = new String[numAttributes];

    for (int i = 0; i< numAttributes;i++){
        attributeNames[i] = tokenizer.nextToken(); //obtengo los nombres de los atributos (1a fila del file)
      //  System.out.println("attributeNames[i]: "+attributeNames[i]);
    }


    while (true){
        input = bufin.readLine();

        //System.out.println("linea leída: "+ input );
        if (input == null) break;
        if (input.startsWith("//")) continue;
        if (input.equals("")) continue;

        tokenizer = new StringTokenizer(input,","); //establezco el delimitador "," para obtener los campos segun el formato del archivo
        int numtokens = tokenizer.countTokens();
        if (numtokens!=numAttributes){
            //System.err.println( "Read " + root.getData().size() + " data");
            System.err.println( "Last line read: " + input);
            System.err.println( "Expecting " + numAttributes  + " attributes");
            return 0;
        }
       // System.out.println("creamos un data Point");
        DataPoint point = new DataPoint(numAttributes);
        for (int i =0; i<numAttributes;i++){

            //System.out.println("siguiente token:"+ tokenizer.nextToken());
            point.attributes[i] = getSymbolValue(i,tokenizer.nextToken() );
        //    System.out.println("point.attributes[i]: "+ point.attributes[i] );
        }
        db.add(point); //añadimos dicho datapoint al arraylist de datos

    }
    
    //prueba para incializar pesos
    for (int i=0;i<db.size();i++){
        DataPoint point =(DataPoint)db.get(i);
        point.setWeight(1.0/db.size());
        //System.out.println("size = "+ db.size()+ "    "+ (1.0/db.size()));
        System.out.println("PESO ASIGNADO = "+ point.weights);
    }

   // System.out.println("salimos del bucle y cerramos el fichero.");
    bufin.close();
    return 1;




}

    public static void main(String[] argv) throws IOException {

          String path = new String("c:/prueba/ballons.data");

                DecisionStump decisor = new DecisionStump();
                decisor.readData(path);
                decisor.buildClassifier(db);
            }
}

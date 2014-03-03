/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 *
 * @author Peru
 */
public class naiveBayes {

    int numAttributes;
    ArrayList <ArrayList> domains;
    String[] attributeNames;
    ArrayList db= new ArrayList();
    ArrayList testDb = new ArrayList();
    ArrayList probAtrib= new ArrayList();
    ArrayList <Double> vectorError = new ArrayList();
    Double errorTotal=new Double(0);

    public int readData(String filename) throws IOException{

    System.out.println("Leyendo datos...");
    FileInputStream in = null;


    try{
        File inputFile = new File(filename);
        in = new FileInputStream(inputFile);
        
    }catch (Exception e){
        System.out.println("unable to open data file:"+ filename + "\n"+ e );
        return 0;
    }
   
    BufferedReader bufin = new BufferedReader (new InputStreamReader (in));
   
    String input;
    
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
      
    }


    while (true){
        input = bufin.readLine();

        
        if (input == null) break;
        if (input.startsWith("//")) continue;
        if (input.equals("")) continue;

        tokenizer = new StringTokenizer(input,","); //establezco el delimitador "," para obtener los campos segun el formato del archivo
        int numtokens = tokenizer.countTokens();
        if (numtokens!=numAttributes){
           
            System.err.println( "Last line read: " + input);
            System.err.println( "Expecting " + numAttributes  + " attributes");
            return 0;
        }
       
        DataPoint point = new DataPoint(numAttributes);
        for (int i =0; i<numAttributes;i++){

            
            point.attributes[i] = getSymbolValue(i,tokenizer.nextToken() );
        
        }
        db.add(point); //añadimos dicho datapoint al arraylist de datos

    }
   
    bufin.close();
    return 1;




}

//function to read from a file the test data.
    public int readTestData(String path) throws IOException{

    this.testDb = new ArrayList();
    

    System.out.println("Leyendo datos del TEST...");
    FileInputStream in = null;


    try{
        File inputFile = new File(path);
        in = new FileInputStream(inputFile);

    }catch (Exception e){
        System.out.println("unable to open data file:"+ path + "\n"+ e );
        return 0;
    }

    BufferedReader bufin = new BufferedReader (new InputStreamReader (in));

    String input;


    while (true){

        input = bufin.readLine();

       
        if (input == null) break;
        if (input.startsWith("//")) continue;
        if (input.equals(" ")) continue;

        StringTokenizer tokenizer = new StringTokenizer(input,",");
        int numTestAttributes = tokenizer.countTokens();
      


        if (numTestAttributes != numAttributes) {
            System.err.println( "Expecting the same number of attributes than the training set");
            return 0;
            }

        DataPoint point = new DataPoint(numAttributes);
        for (int i =0; i<numAttributes;i++){

            
            point.attributes[i] = getSymbolValue(i,tokenizer.nextToken() );
            
        }
        
        this.testDb.add(point); //añadimos dicho datapoint al arraylist de datos de test


    }
    bufin.close();
    return 1;
    }



    public int getSymbolValue(int attribute, String symbol){
    int index = domains.get(attribute).indexOf(symbol); //devuelve -1 si no contiene el valor
    if (index <0){
        domains.get(attribute).add(symbol);
        return domains.get(attribute).size() -1; //si no esta dicho simbolo se anhade

    }
    return index;
}

    public ArrayList getSubconjunto(ArrayList data, int atributo, int valor){
    ArrayList subConjunto = new ArrayList();

    int num = data.size();
    for (int i= 0; i < num; i++){
        DataPoint point = (DataPoint) data.get(i);
        if (point.attributes[atributo]==valor) subConjunto.add(point);
    }
    return subConjunto;
}

    public int numApariciones (ArrayList bd,int atributo,int valor){
        int num = bd.size();
        int count=0;
        for (int i=0;i<num;i++){
            DataPoint point = (DataPoint) bd.get(i);
            if (point.attributes[atributo]==valor) count++;
        }
        return count;
    }

public void trainBayes(){
    double countA;
    double Prob=0;
    for (int i =0;i< (domains.size()-1);i++){//por cada atributo
        HashMap atribI = new HashMap();
        
        countA=0;


        for (int w=0;w<domains.get(i).size();w++){ //por cada uno de los valores del atributo
            ArrayList fila = new ArrayList();

            for (int j=0;j< domains.get(numAttributes-1).size();j++){ //por cada clase
                 ArrayList subSet = new ArrayList(getSubconjunto(db,numAttributes-1,j)); //obtenemos el subset de dicha clase
                
                 
                 if (subSet.isEmpty()){
                     countA =0;
                 }else{
                 countA = (numApariciones(subSet,i,w));
                 countA = countA/ subSet.size(); //obtenemos la probabilidad

                 
                }

                 


                 fila.add(j,countA); //anhadimos al arraylist cada prob de clase Ci

            }
            atribI.put(w, fila); //metemos dicho arrayList en el hashmap

        }
        probAtrib.add(i, atribI); //metemos el hashMap en nuestro arrayList principal probAtrib
        
    }
 
}


public void pintaProb(){
    for (int i =0;i< domains.size()-1;i++){
        HashMap mapa = (HashMap) probAtrib.get(i);

        for (int w=0;w<domains.get(i).size();w++){
            ArrayList atributeValue= (ArrayList)mapa.get(w);
            for (int j=0;j< domains.get(numAttributes-1).size();j++){
                System.out.println("P("+domains.get(i).get(w)+" | "+domains.get(numAttributes-1).get(j)+" ) = " +atributeValue.get(j));
            }
        }

    }
}
public int testInstance(DataPoint point){
    double probCond=1;
    double bestProb=0;
    int claseMax=0;

    

    for (int clase=0;clase< domains.get(numAttributes-1).size();clase++){
           
            probCond=1;
            

            for (int i=0;i< point.attributes.length-1;i++){


                
                HashMap mapa = (HashMap)probAtrib.get(i);
                ArrayList probClases = (ArrayList)mapa.get(point.attributes[i]);

               Object aux = probClases.get(clase);
              
               String str = aux.toString(); double prob = Double.valueOf(str).doubleValue(); //convierto el object a double pa operar
              

               probCond= probCond * prob;
        }


            if (probCond>= bestProb){
            bestProb =probCond;
            claseMax= clase;

        }

    }


 
    return claseMax;
}

public void testBayes(){
    int contadorTest= 0;
    double probCond=1;
    double bestProb=0;
    int claseMax=0;
    double contCorrect=0;

    while(contadorTest!= testDb.size()){
        DataPoint point = (DataPoint)testDb.get(contadorTest);
        bestProb=0;
        claseMax=0;
      
        
        for (int clase=0;clase< domains.get(numAttributes-1).size();clase++){
           
            
            probCond=1;
            double probClase = numApariciones(db,numAttributes-1,clase)/db.size();
         

            for (int i=0;i< point.attributes.length-1;i++){


                
                HashMap mapa = (HashMap)probAtrib.get(i);
                ArrayList probClases = (ArrayList)mapa.get(point.attributes[i]);
               
               Object aux = probClases.get(clase);
              
               String str = aux.toString(); double prob = Double.valueOf(str).doubleValue(); //convierto el object a double pa operar
             
              
            

              
              probCond= probCond* prob;
           
        }
    

        if (probCond > bestProb){
        
            bestProb =probCond;
            claseMax= clase;
        
        }

        

        }if (claseMax== point.attributes[numAttributes-1]){
            //System.out.println("CORRECTO, clase ganadora = "+domains.get(numAttributes-1).get(claseMax));
            contCorrect++;
        } else {
           // System.out.println("INCORRECTO, clase ganadora = "+domains.get(numAttributes-1).get(claseMax)+" ,clase ganadora real:"+domains.get(numAttributes-1).get(point.attributes[numAttributes-1]));
        }
        contadorTest++;
    }
  double clasifError= (testDb.size()-contCorrect)/testDb.size();
 
  vectorError.add(clasifError);
}

public void kfoldValidation(int k ){

    ArrayList copiaData= new ArrayList();
   
    ArrayList auxData= new ArrayList();
    ArrayList auxTest= new ArrayList();

 
    double numElems= db.size()/k;

    copiaData= db;
   
     for (int i=0;i<k;i++){
        auxData= new ArrayList(copiaData);
        auxTest = new ArrayList();

        for (int j=0;j<numElems;j++){
            auxTest.add(copiaData.get( (int) (Math.random()*copiaData.size()))); //cogemos una fila aleatoria
        }

        auxData.removeAll(auxTest);
        this.db=new ArrayList(auxData);
        this.testDb = new ArrayList(auxTest);

    
         this.trainBayes();
         this.testBayes();

    }
    for (int i=0;i<vectorError.size();i++){
   
            errorTotal = errorTotal+ vectorError.get(i);
         

}System.out.println("TESTING ERROR TOTAL: "+errorTotal/k);



}
public void swap(int i, int j, ArrayList db) {

                DataPoint in =(DataPoint) db.get(i);
                db.set(i, db.get(j));
                db.set(j, in);
            }
public ArrayList randomize(ArrayList db) { //arreglao

                for (int j = db.size() - 1; j > 0; j--)
                    swap(j, (int )Math.floor(Math.random()*(j+1)),db );
                   
                return db;
}
public ArrayList dividirArray(ArrayList db, int inicio,int fin){
    ArrayList aux = new ArrayList();
    for (int i=inicio ;i<fin;i++){
        aux.add(db.get(i));
    }
    return aux;

}
public void pintaConjunto(ArrayList data){
    System.out.println("PINTAMOS EL CONJUNTO: ");
    for (int i =0;i<data.size();i++){
        DataPoint point = (DataPoint)data.get(i);
        for (int j =0;j<point.attributes.length;j++){

             System.out.print(domains.get(j).get(point.attributes[j])+",");

        }System.out.println();
    }
}

public void BVDescompose(ArrayList db){
    double m_bias,m_variance,m_sigma;
    int trainIterations= 50;
    double m_error;
    int numClases = domains.get(numAttributes-1).size();
    ArrayList backupBD= new ArrayList(db);
    ArrayList data= new ArrayList(db);
    ArrayList trainSet = new ArrayList();
    ArrayList testSet = new ArrayList(db);

    int sizeTrainSet = data.size()/2; //ojo q puede petar aki
    
    data = randomize(data);

 
    for (int i=0 ;i<sizeTrainSet;i++){
     
        trainSet.add(data.get(i));
    }
    testSet.removeAll(trainSet);
   
    
    int numTest = testSet.size();
    double [][] instanceProbs = new double [numTest][numClases];

    m_error = 0;
    for (int i=0;i<trainIterations;i++ ){
      
        trainSet= randomize(trainSet);
        ArrayList train = new ArrayList(dividirArray(trainSet,0,sizeTrainSet/2));
       
        
        this.db = train;
     
        trainBayes();

        for (int j = 0; j<numTest;j++){
            DataPoint instancia = (DataPoint)testSet.get(j);
            int pred = (int) this.testInstance(instancia);
            if (pred != instancia.attributes[numAttributes -1]){
                m_error++;
            }
 
            
            instanceProbs[j][pred]++ ; //matriz para almacenar los aciertos
            
            
        }

        }
    m_error /= (trainIterations * numTest);

    //medias de BV sobre cada instancia
    m_bias =0;
    m_variance = 0;
    m_sigma = 0;

    for (int i=0;i< numTest;i++){
        DataPoint actual = (DataPoint)testSet.get(i);
        double [] predProbs = instanceProbs[i]; //array de prob de las predicion de cada clase para una instania
        double pActual,pPred;
        double bsum=0, vsum=0,ssum=0;

        for (int j=0; j< numClases;j++){
            pActual = (actual.attributes[numAttributes-1]==j) ? 1:0 ; //zero one loss function
            pPred = predProbs[j] / trainIterations;
          
            bsum += ( (pActual - pPred)*(pActual - pPred) -(pPred * (1- pPred)) ) / (trainIterations -1);
            vsum = vsum+  (pPred * pPred); 
            ssum += pActual * pActual; 
        }
        m_bias += bsum;
        m_variance += (1- vsum);
        m_sigma += (1 - ssum); 

    }
    m_bias /= (2*numTest);
    m_variance /= (double)(2*numTest);
    m_sigma /= (double)(2*numTest);
    System.out.println("Bias: "+m_bias);
    System.out.println("Variance: "+m_variance);
    System.out.println("Sigma: "+m_sigma);
    System.out.println("Error: "+ m_error);

this.db = backupBD;

    }
   
    
  


    public int calculateBayes(int[] camposBuscar, int[] labelInstances){ //nos devuelve el numero de clase a la q tiene mas prob de pertenecer

        double probCond=1;
        int bestClass=0;
        double bestProb=1.0;

        for (int j =0;j< domains.get(numAttributes-1).size();j++){
            
            int countC= numApariciones(db ,numAttributes-1,j);
            
            ArrayList subSet = new ArrayList(getSubconjunto(db,numAttributes-1,j));
           
            for (int i=0;i< camposBuscar.length;i++){

               
                double countA = (numApariciones(subSet,camposBuscar[i],labelInstances[i]) );
                countA = countA/ subSet.size();

                if (countA==0.0) countA=0.05;
                
                probCond= countA * probCond; //p(d1|Ci)*p(d2|Ci)* ...
                
            }

            System.out.println("Prob(Di|"+ domains.get(numAttributes-1).get(j)+") = "+ probCond);
            if (probCond <= bestProb){
                bestProb= probCond;
                bestClass = j;
            }
            



        }
        return bestClass;



    }

    public static void main (String[] args) throws Exception{




    //long startTime = System.nanoTime();

    //cars
    //String path = new String("c:/prueba/car.data");
    //String pathTest = new String("c:/prueba/carTest.data");

    //tic tac toe
    //String path = new String("c:/prueba/tic-tac-toe.data");


    //ballons
   //String path = new String("c:/prueba/ballons.data");
  //  String pathTest = new String("c:/prueba/ballonsTest.data");

    //plantas
   //String path = new String("c:/prueba/agaricus-lepiota.data");
    //String pathTest = new String("c:/prueba/ballonsTest.data");

    //soybean
    //String path = new String("c:/prueba/soybean-small.data");


    //CHESS
    //String path = new String("c:/prueba/chess.data");


   
    //naiveBayes clasificador = new naiveBayes();
    //clasificador.readData(path);
    //clasificador.readTestData(pathTest);

    //clasificador.BVDescompose(clasificador.db);
    //clasificador.kfoldValidation(10);
    //clasificador.trainBayes();
    //clasificador.testBayes();
    //clasificador.calculateBayes(atributes,values );
    //long endTime = System.nanoTime();
    //long totalTime = (endTime-startTime);
    //System.out.println( totalTime + " NanoSeconds");

    }


}

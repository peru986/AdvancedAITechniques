/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;

/**
 *
 * @author Peru
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class DecisionTree {
    double gainThreshold=0; //GAIN THRESHOLD FOR PRUNNING
    int numAttributes;   
    ArrayList <ArrayList> domains;
    String[] attributeNames;
    ArrayList testData;
    ArrayList <Double> vectorError = new ArrayList();
    Double errorTotal= new Double(0);
   
    Node root= new Node();



public void descomposeNode(Node nodo){
    double mejorEntropia=0;
    boolean elegido = false;
    int atributoElegido=-1;
    

    int numData = nodo.getData().size();
    int numInputAttributes = numAttributes -1;


    double entropy= calculateEntropy(nodo.getData());
    nodo.setEntropy(entropy);
    
    if ((nodo.getEntropy()==0) && (!nodo.getData().isEmpty())){ //all samples belong to the same class

        nodo.setClaseLeaf(((DataPoint)nodo.getData().get(0)).attributes[numAttributes-1]);
        return;}

    if (nodo.getData().isEmpty()){ //no samples left
        int atrib = mayorityVote(nodo.getParent());
        nodo.setClaseLeaf(atrib);
        return;
    }


    //ahora miramos el atributo que provoque mayor ganancia

    for (int i =0; i< numInputAttributes;i++){
        int numValues = domains.get(i).size();

        if (alreadyDescomposed(nodo,i)) continue; //si el atributo ya esta descompuesto en un nodo padre anterior pasamos de el.


        double entropiaMedia = 0; //variable para almacenar la ganancia del atributo i
        for (int j=0;j< numValues;j++){ //por cada valor de un atributo
            ArrayList subConjunto = getSubconjunto(nodo.getData(),i,j);
            

            if (subConjunto.isEmpty()) continue; //si el subconjunto no tiene ninguna terna pasamos de el.
            double subEntropia = calculateEntropy(subConjunto);
            entropiaMedia += subEntropia * subConjunto.size();
            

        }

        entropiaMedia = entropiaMedia/numData; //ganancia (no es la ganancia en si, es lo q se le restaria a la entropia del nodo evaluado)
    
        double gain = entropy - entropiaMedia;
    
        if ((elegido== false ) && (gain>= gainThreshold)){ //PREPRUNNNING ARREGLAR ESTO Q SOLO NOS SACA UNA CLASE UNACC
            elegido = true;
            mejorEntropia= entropiaMedia;
            atributoElegido = i;
        } else{
            if ((entropiaMedia<mejorEntropia)&& (gain>= gainThreshold)){  //si la entropia del hijo es menor, mayor sera la ganancia
                elegido = true;
                mejorEntropia= entropiaMedia;
                atributoElegido=i;
            }
        }

        if ((i==numInputAttributes-1) && (elegido==false)){
            System.out.println("PRUNNING REALIZED" );
            nodo.setClaseLeaf(mayorityVote(nodo.getParent()));
            return;
        }

    }
   if (atributoElegido==-1){
        System.out.println("NO MORE ATTRIBUTES TO SPLIT");
        nodo.setClaseLeaf(mayorityVote(nodo.getParent()));
        return;
    }
    //ahora dividimos el data usando el atributo selecionado
    int numValues = domains.get(atributoElegido).size(); //obtenemos el numero de branches que tiene dicho atributo
    nodo.setDecompositionAttribute(atributoElegido);
    nodo.setChildren(new Node[numValues] );


    for (int j=0;j< numValues ; j++){

        nodo.getChildren()[j]= new Node();
        nodo.getChildren()[j].setParent(nodo);
        nodo.getChildren()[j].setData(getSubconjunto(nodo.getData(),atributoElegido,j));
        nodo.getChildren()[j].setDecompositionValue(j);

    }

    for (int k=0;k<numValues;k++){
        descomposeNode(nodo.getChildren()[k]);
    }

    //ahora que acabamos nos cargamos los datos de cada node para ahorrar espacio
    nodo.setData(null);

}

public int mayorityVote(Node nodo){
    int mayoritaria = -1;

    for (int i = 0; i< domains.get(numAttributes-1).size();i++){
        int numClasei = numApariciones(nodo.getData(),numAttributes-1,i);
        if (numClasei>mayoritaria) mayoritaria=i;

    }
    return mayoritaria;



}

public int numApariciones (ArrayList data,int atributo,int valor){
        int num = data.size();
        int count=0;
        for (int i=0;i<num;i++){
            DataPoint point = (DataPoint) data.get(i);
            if (point.attributes[atributo]==valor) count++;
        }
        return count;
    }

//crea un subconjunto formado por ternas que contienen el atributo "atributo" con valor "valor"
public ArrayList getSubconjunto(ArrayList data, int atributo, int valor){
    ArrayList subConjunto = new ArrayList();

    int num = data.size();
    for (int i= 0; i < num; i++){
        DataPoint point = (DataPoint) data.get(i);
        if (point.attributes[atributo]==valor) subConjunto.add(point);
    }
    return subConjunto;
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

public double calculateEntropy(ArrayList data){ //no tocar OK
    int numData= data.size();
    if (numData==0) return 0;

    int attribute = numAttributes-1; //campo variable de clase(output)
    int numValues = domains.get(attribute).size(); //numValues = numero de variables de clase
    double sum =0;
    
    
    for (int i=0;i< numValues;i++){
        int count=0;
        for (int j=0;j<numData;j++){
            DataPoint point = (DataPoint) data.get(j);
            if (point.attributes[attribute]== i ) count++; //nos cuenta las apariciones de una variable de clase
            
        }
        double probabilidad = 1.* count/numData; // aki calculamos el (p/(n+p+..))
        if (count > 0) sum += -probabilidad * ((Math.log(probabilidad)/Math.log(2)));
    }
    return sum;
    

}

//mira recursivamente si el atributo ya esta usado para descomponer
public boolean alreadyDescomposed(Node nodo,int attribute){ 

    if (nodo.getChildren() != null){
        if (nodo.getDecompositionAttribute() ==attribute) return true;
    }
    if (nodo.getParent()==null) return false;
    return alreadyDescomposed(nodo.getParent(),attribute);

     }

public int getSymbolValue(int attribute, String symbol){
    int index = domains.get(attribute).indexOf(symbol); //devuelve -1 si no contiene el valor
    if (index <0){
        domains.get(attribute).add(symbol);
        return domains.get(attribute).size() -1; //si no esta dicho simbolo se anhade

    }
    return index;
}


public int testInstance(DataPoint point){
    Node nodo = root;
    boolean fin=false;

    int branchAttrib= nodo.getDecompositionAttribute();

    while (!fin){

        branchAttrib = nodo.getDecompositionAttribute();

        if (nodo.getChildren()== null){
            fin = true;
             if (nodo.getClaseLeaf()==-1) return mayorityVote(nodo.getParent());

        return nodo.getClaseLeaf();
        }
        if (nodo.getClaseLeaf()!= -1){
            return nodo.getClaseLeaf();

        }

           for (int i=0;i<nodo.getChildren().length;i++){ //en cada hijo buscamos si el value del split es el de nuestro test
            
      

            if (nodo.getChildren()[i].getDecompositionValue()==point.attributes[branchAttrib]){
                nodo= nodo.getChildren()[i]; //vamos por la rama del hijo cuyo atributo es el de nuestra linea de test.
            
                break;
        }
}
    }
    return -1;
    }

public double trainingError(){
    int outputattr = numAttributes-1;
    int contadorTest=0;
    double contCorrect=0;
    boolean fin=false;
 

    Node nodo= root;

    while(contadorTest!= testData.size()){
        fin=false;
        DataPoint point = (DataPoint)testData.get(contadorTest); //obtenemos fila por fila los tests

        while (!fin){


        int branchAttrib= nodo.getDecompositionAttribute();

        if (nodo.getChildren()== null){
            fin=true;
            if (nodo.getClaseLeaf() == point.attributes[outputattr]){
                contCorrect++;
            }
            else{

            }
            contadorTest++;
            nodo= root;
            continue;
        }

        for (int i=0;i<nodo.getChildren().length;i++){ //en cada hijo buscamos si el value del split es el de nuestro test
            if (nodo.getChildren()[i].getDecompositionValue()==point.attributes[branchAttrib]){
                nodo= nodo.getChildren()[i]; //vamos por la rama del hijo cuyo atributo es el de nuestra linea de test.
                break;
            }
        }
        }
    }
    double clasifError = (double)(testData.size() - contCorrect) / testData.size();
    System.out.println("Classification ERROR: "+ clasifError+ " with testing set size= "+ testData.size());
    vectorError.add(clasifError);//vector para almacenar los errores para los k del kfold
    return clasifError;

 
     }

public void kFoldValidation(int k){

    ArrayList copiaData= new ArrayList();
    ArrayList copiaTest= new ArrayList();
    ArrayList auxData= new ArrayList();
    ArrayList auxTest= new ArrayList();

    int numElem = (int)root.getData().size()/k; //tamaño de los subconjuntos de entrenamiento

    copiaData = root.getData();
    copiaTest = this.testData;
    
     for (int i=0;i<k;i++){

         auxData= new ArrayList(copiaData);
         auxTest = new ArrayList();
         

         for (int j=0;j<numElem;j++){
                auxTest.add(copiaData.get( (int) (Math.random()*copiaData.size())));
         }
         
       

         auxData.removeAll(auxTest);
     
         root = new Node();
         root.setData(auxData);
         this.testData= auxTest;

    
         this.crearArbol();
         double error= trainingError();

         auxTest.clear();
         auxData.clear();




    }
for (int i=0;i<vectorError.size();i++){
    System.out.println("Error del k ="+ i + " = "+vectorError.get(i));
            errorTotal = errorTotal+ vectorError.get(i);
            //System.out.println("ERROR TOTAL: "+errorTotal);

}
    System.out.println("TESTING ERROR TOTAL: "+errorTotal/k);


}



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
            System.err.println( "Read " + root.getData().size() + " data");
            System.err.println( "Last line read: " + input);
            System.err.println( "Expecting " + numAttributes  + " attributes");
            return 0;
        }
      
        DataPoint point = new DataPoint(numAttributes);
        for (int i =0; i<numAttributes;i++){

            
            point.attributes[i] = getSymbolValue(i,tokenizer.nextToken() );
        
        }
        root.getData().add(point); //añadimos dicho datapoint al arraylist de datos

    }
  
    bufin.close();
    return 1;

}

public int readTestData(String path) throws IOException{

    this.testData = new ArrayList();

    
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
            System.out.println("point.attributes[i]: "+ point.attributes[i] );
        }
        System.out.println("anhadimos una fila al testData");
        this.testData.add(point); //añadimos dicho datapoint al arraylist de datos de test


    }
    bufin.close();
    return 1;
    }


public void crearArbol(){
   
    descomposeNode(root);

}


public void pintarData(){
    for (int i=0;i< this.domains.size();i++){
    System.out.println(this.domains.get(i));
    }
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


public void BVDescompose(){ //mirar q esta copiado y pegado del naive bayes
    double m_bias,m_variance,m_sigma;
    int trainIterations= 50;
    double m_error;
    int numClases = domains.get(numAttributes-1).size();
    
    ArrayList db = new ArrayList(root.getData());
    ArrayList backupBD= new ArrayList(db);
    ArrayList data= new ArrayList(db);
    ArrayList trainSet = new ArrayList();
    ArrayList testSet = new ArrayList(db);

    int sizeTrainSet = data.size()/2; //ojo q puede petar aki

    data = randomize(data);

    //dividimos en 2
    for (int i=0 ;i<sizeTrainSet;i++){
      //  System.out.println("ITERATION NUMBER: "+i);
        trainSet.add(data.get(i));
    }
    testSet.removeAll(trainSet);
    //fin division

    int numTest = testSet.size();
    double [][] instanceProbs = new double [numTest][numClases];

    m_error = 0;
    for (int i=0;i<trainIterations;i++ ){
       // System.out.println("ITERATION NUMBER: "+i);
        trainSet= randomize(trainSet);
        ArrayList train = new ArrayList(dividirArray(trainSet,0,sizeTrainSet/2));
        //ahora entrenamos

        this.root.setData(train);
        // pintaConjunto(train);
        descomposeNode(root);

        for (int j = 0; j<numTest;j++){
            DataPoint instancia = (DataPoint)testSet.get(j);
            int pred = (int) this.testInstance(instancia);
            if (pred != instancia.attributes[numAttributes -1]){
                m_error++;
            }

            if (pred!=-1){
            instanceProbs[j][pred]++ ; //matriz para almacenar los aciertos
            }

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
            //System.out.println("pPred: "+pPred);
            bsum += ( (pActual - pPred)*(pActual - pPred) -(pPred * (1- pPred)) ) / (trainIterations -1);
            vsum = vsum+  (pPred * pPred); //System.out.println("vsum: "+vsum);
            ssum += pActual * pActual; //System.out.println("ssum: "+ssum);
        }
        m_bias += bsum;// System.out.println("m_bias: "+m_bias);
        m_variance += (1- vsum);// System.out.println("m_variance: "+m_variance);
        m_sigma += (1 - ssum); //System.out.println("m_sigma: "+m_sigma);

    }
    m_bias /= (2*numTest);
    m_variance /= (double)(2*numTest);
    m_sigma /= (double)(2*numTest);
    System.out.println("Bias: "+m_bias);
    System.out.println("Variance: "+m_variance);
    System.out.println("Sigma: "+m_sigma);
    System.out.println("Error: "+ m_error);

this.root.setData(backupBD);

    }





public static void main (String[] args) throws Exception{
  /*  int num = args.length;
    if (num != 1){
        System.out.println("You need to specify the name of datafile in command line");
        return;
    }*/

    System.out.println("Parametro correcto");
    DecisionTree arbol = new DecisionTree();
    
    
    //long startTime = System.currentTimeMillis();

    long startTime = System.nanoTime();

    //cars
    //String path = new String("c:/prueba/car.data");
    //String pathTest = new String("c:/prueba/carTest.data");

    //tic tac toe
    //String path = new String("c:/prueba/tic-tac-toe.data");


    //ballons
  // String path = new String("c:/prueba/ballons.data");
  //  String pathTest = new String("c:/prueba/ballonsTest.data");

    //plantas
   String path = new String("c:/prueba/agaricus-lepiota.data");
    //String pathTest = new String("c:/prueba/ballonsTest.data");

    //soybean
    //String path = new String("c:/prueba/soybean-small.data");


    //CHESS
    //String path = new String("c:/prueba/chess.data");

    int estado = arbol.readData(path);
    //System.out.println("arboldata: "+arbol.root.getData());
   // int estadoTest = arbol.readTestData(pathTest);

    boolean kfold = true;


    //arbol.crearArbol();
    //if ((kfold==true) && (estado<=0)){

        arbol.BVDescompose();
       //arbol.kFoldValidation(10);

    long endTime = System.nanoTime();
    long totalTime = (endTime-startTime);
        //long endTime = System.currentTimeMillis();
		//long totalTime = (endTime-startTime)/1000;
                System.out.println( totalTime + " NanoSeconds");
    //}else{
    //int estado = arbol.readData(args[0]);
  //  if (estado<= 0) return;

  //  arbol.crearArbol();
  //  System.out.println("ARBOLETE CREADO-------");
   // }
    
    
}



}

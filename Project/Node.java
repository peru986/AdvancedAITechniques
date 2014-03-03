/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ai;

import java.util.*;
/**
 *
 * @author Peru
 */

class Node {
    private Node parent;
    private Node[] children;
    private ArrayList data;
    private double entropy;
    private int decompositionAttribute;
    private int decompositionValue;
    private int claseLeaf;

    public Node(){
        super();
        this.data= new ArrayList();
        this.claseLeaf = -1;
       // this.decompositionValue=-1;
        
    }
    
   int getDecompositionAttribute(){
        return decompositionAttribute;
    }
    public void setDecompositionAttribute(int atrib){
        this.decompositionAttribute= atrib;
    }
    public void setDecompositionValue(int value){
        this.decompositionValue= value;
    }
     int getDecompositionValue(){
        return decompositionValue;
    }
     public void setClaseLeaf(int clase){
         this.claseLeaf=clase;
     }
     int getClaseLeaf(){
         return this.claseLeaf;
     }
    
    public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	public void setData(ArrayList data) {
		this.data = data;
	}

	public ArrayList getData() {
		return data;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	 double getEntropy() {
		return entropy;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}

	public Node[] getChildren() {
		return children;
	}
        public Node getChilden(int j){
            return children[j];
        }

	

	

  public  void setEntropy(double[] entropy) {
        double entropyTotal= 0 ;
        for (int i =0;i<entropy.length;i++){
            entropyTotal += entropy[i];
            this.entropy= entropyTotal;
        }

    }






}

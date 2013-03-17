package com.i2r.dm.drugeles;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;

public class Eigenvalues {
	   
   public static void main(String[] args) { 
      int N = 2;

      // create a symmetric positive definite matrix
      Matrix A = Matrix.random(N, N);
      
      A = A.transpose().times(A);

      A.set(0,0,1.93);
      A.set(0,1,-0.4);
      A.set(1,0,-0.4);
      A.set(1,1,0.33);

      A.print(9, 6);
      
      // compute the spectral decomposition
      EigenvalueDecomposition e = A.eig();
      Matrix V = e.getV();
      Matrix D = e.getD();
 

      System.out.print("A =");
      A.print(9, 6);
      System.out.print("D =");
      D.print(9, 6);
      System.out.print("V =");
      V.print(9, 6);

      //Eigenvalue visualization
      double eigenv1=D.get(0,0);
      double eigenv2=D.get(1,1);	 
      double major, minor, eigenV,hint,rotation;
      if(eigenv1>eigenv2){
    	  major=eigenv1;
    	  minor=eigenv2;
    	  eigenV=V.get(0,0);
    	  hint=V.get(1,0);
    	 
      }else{
    	  major=eigenv2;
    	  minor=eigenv1;
    	  eigenV=V.get(0,1);
    	  hint=V.get(1,1);
    	}
      
      if(hint>0){
		   rotation=Math.acos(eigenV) * 180/Math.PI;
	  }else{
		  rotation=-1*Math.acos(eigenV) * 180/Math.PI;
	  }
      
      System.out.println("Major:");
      System.out.println("\t"+major+"\n");
      System.out.println("Minor:");
      System.out.println("\t"+minor+"\n");
      System.out.println("Rotation:");
      System.out.println("\t"+rotation+"\n");
     
      
      // check that V is orthogonal
      System.out.print("||V * V^T - I|| = ");
      System.out.println(V.times(V.transpose()).minus(Matrix.identity(N, N)).normInf());

      // check that A V = D V
      System.out.print("||AV - DV|| = ");
      System.out.println(A.times(V).minus(V.times(D)).normInf());
   }

}
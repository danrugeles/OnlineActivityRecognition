package com.i2r.dm.drugeles;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class Klass {

	public double[] mean={0,0}; 
	public double[][] cov;
	public Matrix icov;
	public int numelem=0;
	 
	public final int id;
	public static int numclass=0;
	public static int numfeats;
	
	//Visualization parameters
	public double rotation=0;
	public double minor=0;
	public double major=0;
	
	public Klass(int numfeats){
		this.id=this.numclass;
		this.numfeats=numfeats;
		this.numclass++;
		double[] m =new double[numfeats];
		double[][] c =new double[numfeats][numfeats];
		
		for(int i=0;i<numfeats;i++){
			m[i]=0;
			for(int j=0;j<numfeats;j++){
				c[i][j]=0;
			}
		}
		this.mean=m;
		this.cov=c;
	}
	
	public Klass(double[] mean, double[][] cov, int numclass, int numfeats){
		this.numclass++;
		this.numfeats=numfeats;
		this.id=this.numclass;
		this.mean=mean;
		this.cov=cov;
		this.numelem=0;
	}

	@Override
	public String toString(){
		String result;
		result="id:\n";
		result=result+this.id+"\n";
		result=result+"mean:\n";
		for(int i=0;i<numfeats;i++){
			result=result+"\t";
			result=result+mean[i]+"\n";
		}
		result=result+"Covariance:\n";
		for(int i=0;i<numfeats;i++){
			for(int j=0;j<numfeats;j++){
				result=result+"\t";
				result=result+cov[i][j];
			}
			result=result+"\n";
		}
		return result;
	}
	
	public void reestimateDistribution(double data[]){

		for (int i=0;i<numfeats;i++){	
			this.mean[i]=(this.mean[i]*numelem+data[i])/(numelem+1);	
		}
		
		for (int i=0;i<numfeats;i++){
			for (int j=0;j<numfeats;j++){
				this.cov[i][j]=(cov[i][j]*numelem+(data[i]-this.mean[i])*(data[j]-this.mean[j]))/(numelem+1);	
			}
		}
		this.numelem++;		
		
		
	}
	
	public void printCov(){
		String result="Covariance from class: "+this.id+"\n";
		for(int i=0;i<numfeats;i++){
			for(int j=0;j<numfeats;j++){
				result=result+this.cov[i][j];
				result=result+"\t";	
			}
			result=result+"\n";
		}
		System.out.println(result);
	}
	
	public void printICov(){
		String result="Inverse Covariance from class: "+this.id+"\n";
		for(int i=0;i<numfeats;i++){
			for(int j=0;j<numfeats;j++){
				result=result+this.icov.get(i,j);
				result=result+"\t";	
			}
			result=result+"\n";
		}
		System.out.println(result);
	}
	
	public void printMean(){
		String result="Mean from class:"+this.id+"\n";
		for(int i=0;i<numfeats;i++){
				result=result+this.mean[i];
				result=result+"\t";	
		}
		result=result+"\n";
		System.out.println(result);
	}
	
	public void printVisParameters(){
		String result="Visualization Parameters for class "+this.id+":\n";
		result=result+"Major :"+this.major+"\nMinor :"+minor+"\nRotation :"+this.rotation+"\n";
		System.out.println(result);
	}
	
	public double MultivariateGaussian(double[] point){
		
		Matrix cov=new Matrix(this.cov);
		//Matrix icov=cov.inverse();	
		
		Matrix mean= new Matrix(this.mean,1);
		Matrix p= new Matrix(point,1);
		
		Matrix exponent=p.minus(mean).times(this.icov).times(p.minus(mean).transpose());
		
		return Math.pow(2.0*Math.PI,-numfeats/2.0)*Math.pow(cov.det(),-0.5)*Math.exp(-0.5*exponent.get(0, 0));
	}
	
	
	 public void updateVisualizationParameters() { 

	      // Get the covariance matrix
	      Matrix A = new Matrix(this.cov);
	      	      
	      // Compute the spectral decomposition
	      EigenvalueDecomposition e = A.eig();
	      Matrix V = e.getV();
	      Matrix D = e.getD();
	      
	      /*System.out.print("A =");
	      A.print(9, 6);
	      System.out.print("D =");
	      D.print(9, 6);
	      System.out.print("V =");
	      V.print(9, 6);*/

	      // Find Eigenvalue visualization parameters
	      double eigenv1=D.get(0,0);
	      double eigenv2=D.get(1,1);	 
	      double eigenV,hint;
	      
	      if(eigenv1>eigenv2){
	    	  this.major=eigenv1;
	    	  this.minor=eigenv2;
	    	  eigenV=V.get(0,0);
	    	  hint=V.get(1,0);
	    	 
	      }else{
	    	  this.major=eigenv2;
	    	  this.minor=eigenv1;
	    	  eigenV=V.get(0,1);
	    	  hint=V.get(1,1);
	    	}
	      
	      if(hint>0){
			   this.rotation=Math.acos(eigenV) * 180/Math.PI;
		  }else{
			   this.rotation=-1*Math.acos(eigenV) * 180/Math.PI;
		  }
	      
	      /*System.out.println("Major:");
	      System.out.println("\t"+major+"\n");
	      System.out.println("Minor:");
	      System.out.println("\t"+minor+"\n");
	      System.out.println("Rotation:");
	      System.out.println("\t"+rotation+"\n");*/
	 }

	
	
	public static void main(String [] a){
	Klass c=new Klass(2);
	double[]p={2,1};
	
	System.out.println(p[0]+"-"+p[1]);
	c.reestimateDistribution(p);
	System.out.println(c);
	p[0]=3;
	p[1]=2;
	System.out.println(p[0]+"-"+p[1]);
	c.reestimateDistribution(p);
	System.out.println(c);
	p[0]=4;
	p[1]=3;
	System.out.println(p[0]+"-"+p[1]);
	c.reestimateDistribution(p);
	System.out.println(c);
	p[0]=3;
	p[1]=1;
	System.out.println(p[0]+"-"+p[1]);
	c.reestimateDistribution(p);
	System.out.println(c);
	p[0]=2;
	p[1]=1;
	System.out.println(p[0]+"-"+p[1]);
	c.reestimateDistribution(p);
	System.out.println(c);
	p[0]=5;
	p[1]=4;
	System.out.println(p[0]+"-"+p[1]);
	c.reestimateDistribution(p);
	System.out.println(c);
	p[0]=0.9;
	p[1]=0.2;
	System.out.println(p[0]+"-"+p[1]);
	c.reestimateDistribution(p);
	System.out.println(c);
	p[0]=0.9;
	p[1]=0.6;
	
	System.out.println(c.MultivariateGaussian(p));
	}
	
}

package com.i2r.dm.drugeles;

import java.util.ArrayList;
import java.util.*;
import Jama.Matrix;

public class OnlineBayes {

	int window;
	int numclass;
	ArrayList<Klass> classes = new ArrayList<Klass>();
	final int numfeats;
	
	public OnlineBayes(int numfeats){
		//this.window=Window;
		this.numfeats=numfeats;
	}
	
	public void Train(double data[],int klass){
		if(klass>=Klass.numclass){
			Klass c=new Klass(numfeats);
			c.reestimateDistribution(data);
			classes.add(c);
			c.updateVisualizationParameters();
		}
		else{
			Klass c=classes.get(klass);
			c.reestimateDistribution(data);
			classes.set(klass, c);
			c.updateVisualizationParameters();
		}
	}
	
	public void Adapt(double p[] ){
		
		ArrayList<Matrix> icovs=new ArrayList<Matrix>();
		
		//Check if all covariance matrices are valid
		try{
			Iterator<Klass> it=classes.iterator();
			
			while(it.hasNext()){
				Klass c = it.next();
				Matrix cov=new Matrix(c.cov);
				icovs.add(cov.inverse());
				
			}
			
		}catch(Exception e){
			//System.out.println("At least one covariance matrix could not be found");
			return;
			
		}
		
		//Update covariances	
		Iterator<Klass> it_classes=classes.iterator();
		Iterator<Matrix> it_icov =icovs.iterator();			
		while(it_classes.hasNext()){
			Klass c = it_classes.next();
			c.icov=it_icov.next();
		}
		
		//Find the appropiate class
		Iterator<Klass> it=classes.iterator();
		int appropiateclass=-1;
		double highestprob=0;
		while(it.hasNext()){
			Klass c = it.next();
			if(c.MultivariateGaussian(p)>highestprob){
				highestprob=c.MultivariateGaussian(p);
				appropiateclass=c.id;
			}
		}
	
		//Reestimate distribution
		this.classes.get(appropiateclass).reestimateDistribution(p);	
		
		//Update Visualization parameters
		this.classes.get(appropiateclass).updateVisualizationParameters();
		
	}
		
	
	public static void main(String[] argv){
		OnlineBayes ob =new OnlineBayes(2);
		double[]p={1,1};
		
		ob.Train(p,0);
		p[0]=1.1;
		p[1]=1.2;
		ob.Train(p,0);
		p[0]=1.8;
		p[1]=2.1;
		ob.Train(p,0);
		p[0]=1.9;
		p[1]=2;
		ob.Train(p,0);
		p[0]=1.1;
		p[1]=2.5;
		ob.Train(p,0);
		p[0]=1.4;
		p[1]=1.7;
		ob.Train(p,0);
		p[0]=1.7;
		p[1]=2.1;
		ob.Train(p,0);
		p[0]=1.9;
		p[1]=2.1;
		ob.Train(p,0);
		
		p[0]=1;
		p[1]=1.5;
		ob.Train(p,0);
		ob.classes.get(0).printCov();
		ob.classes.get(0).printMean();
		ob.classes.get(0).printVisParameters();
		p[0]=1.2;
		p[1]=1;
		ob.Train(p,0);
		ob.classes.get(0).printCov();
		ob.classes.get(0).printMean();
		ob.classes.get(0).printVisParameters();
		p[0]=1.4;
		p[1]=1.2;
		ob.Train(p,0);
		ob.classes.get(0).printCov();
		ob.classes.get(0).printVisParameters();
		p[0]=1.3;
		p[1]=1.2;
		ob.Train(p,0);
		ob.classes.get(0).printCov();
		ob.classes.get(0).printMean();
		ob.classes.get(0).printVisParameters();
		
		
		p[0]=8;
		p[1]=9;
		ob.Train(p,1);
		p[0]=8.2;
		p[1]=8.6;
		ob.Train(p,1);
		p[0]=8.3;
		p[1]=8.1;
		ob.Train(p,1);
		p[0]=8.9;
		p[1]=8.1;
		ob.Train(p,1);
		p[0]=8.3;
		p[1]=8.1;
		ob.Train(p,1);
		p[0]=9.1;
		p[1]=9.2;
		ob.Train(p,1);
		p[0]=8.3;
		p[1]=8.2;
		ob.Train(p,1);
		p[0]=9.1;
		p[1]=8.1;
		ob.Train(p,1);
		p[0]=8.0;
		p[1]=8.6;
		ob.Train(p,1);
		p[0]=7.9;
		p[1]=8.2;
		ob.Train(p,1);
		
		ob.classes.get(1).printCov();
		ob.classes.get(1).printMean();
		ob.classes.get(1).printVisParameters();
		
		p[0]=8.5;
		p[1]=8.5;
		ob.Train(p,1);
		ob.classes.get(1).printCov();
		ob.classes.get(1).printMean();
		ob.classes.get(1).printVisParameters();
		p[0]=8.6;
		p[1]=8.4;
		ob.Train(p,1);
		ob.classes.get(1).printCov();
		ob.classes.get(1).printMean();
		ob.classes.get(1).printVisParameters();
		p[0]=8;
		p[1]=8.2;
		ob.Train(p,1);
		ob.classes.get(1).printCov();
		ob.classes.get(1).printMean();
		ob.classes.get(1).printVisParameters();
		
		System.out.println("////Class 1 Starts Adapting////\n");
		p[0]=8.8;
		p[1]=8.2;
		ob.Adapt(p);
		ob.classes.get(0).printVisParameters();
		ob.classes.get(1).printVisParameters();
		p[0]=8.1;
		p[1]=8.1;
		ob.Adapt(p);
		ob.classes.get(0).printVisParameters();
		ob.classes.get(1).printVisParameters();
		p[0]=9.4;
		p[1]=9.5;
		ob.Adapt(p);
		ob.classes.get(0).printVisParameters();
		ob.classes.get(1).printVisParameters();
		p[0]=9.1;
		p[1]=8;
		ob.Adapt(p);
		ob.classes.get(0).printVisParameters();
		ob.classes.get(1).printVisParameters();
		
		System.out.println("////Now Class 0 Starts Adapting////\n");
		
		p[0]=1.1;
		p[1]=1.3;
		ob.Adapt(p);
		ob.classes.get(0).printVisParameters();
		ob.classes.get(1).printVisParameters();
		p[0]=2.1;
		p[1]=2.4;
		ob.Adapt(p);
		ob.classes.get(0).printVisParameters();
		ob.classes.get(1).printVisParameters();
		p[0]=1.2;
		p[1]=2.1;
		ob.Adapt(p);
		ob.classes.get(0).printVisParameters();
		ob.classes.get(1).printVisParameters();
		
		//ob.classes.get(1).printICov();
		
	}
	
}

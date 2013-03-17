package com.i2r.dm.samz;

import java.util.*;

import android.util.Log;
import Jama.Matrix;

public class OnlineBayes {

	int window;
	static int numclass=0;
	ArrayList<Klass> classes = new ArrayList<Klass>();
	final int numfeats;
	double energy=0;
	double maxdifference=0;
	double maximum=0;
	double minimum=10000;
	
	
	public OnlineBayes(int numfeats){
		//this.window=Window;
		this.numfeats=numfeats;
	}
	
	//Bug when initialiting classes. for avoiding small mistakes, 
	//Activities must start in order
	public int Train(double data[],int klass){
		if(klass>=Klass.numclass){
			Klass c=new Klass(numfeats);
			
			OnlineBayes.numclass++;
			c.reestimateDistribution(data);
			classes.add(c);
			
			c.updateVisualizationParameters();
			return c.id;
		}
		else{
			Klass c=classes.get(klass);
			c.reestimateDistribution(data);
			classes.set(klass, c);
			c.updateVisualizationParameters();
			return c.id;
		}
		
	}
	
	public void updateAllVisualizationParameters(){
		for(Klass cl:classes){
			cl.updateVisualizationParameters();
		}
	}
	
	public int[] Adapt(double p[] ){
		
		int[] id_act={-1,-1};
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
			id_act[0]=-1;
			id_act[1]=-1;
			return id_act;
			
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
	
		id_act[0]=classes.get(appropiateclass).id;
		id_act[1]=appropiateclass;
		return id_act;
	}
	
	public void updateFeatures(double value){
		this.energy=this.energy+Math.pow(value-10.3,2);
		if(value>this.maximum){
			this.maximum=value;
			this.maxdifference=this.maximum-this.minimum;
		}
		if(value<this.minimum){
			this.minimum=value;
			this.maxdifference=this.maximum-this.minimum;
		}
	}
	
	public void resetFeatures(){
		this.energy=0;
		this.maxdifference=0;
		this.maximum=0;
		this.minimum=10000;
	}
	
	public void resetAllDistributions(){
		for(Klass cl:classes){
			cl.resetDistribution();
		}
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
		
		System.out.println("KL CONVERGENCES:");
		System.out.println(ob.classes.get(0).KLDivergence(ob.classes.get(1)));
		System.out.println(ob.classes.get(1).KLDivergence(ob.classes.get(0)));
		//ob.classes.get(1).printICov();
		
		
		System.out.println(ob.classes.get(0).KLDivergence(ob.classes.get(1)));
		
		System.out.println(ob.classes.get(1).KLDivergence(ob.classes.get(0)));
		
		
	}
	
}

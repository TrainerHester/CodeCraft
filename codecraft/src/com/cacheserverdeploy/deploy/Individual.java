package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Individual implements Comparable<Individual>{
	/**
	 * @param DNA 个体DNA序列
	 * @param fitness 个体适应度
	 * @param a 值越小，复制的强制就越趋向与那些适应度大的个体
	 */
	public boolean [] DNA;
	public int fitness;
	public static Graph graph;
	public static Set<Integer> forbid;
	public static Set<Integer> mustid;
	public static double a;
	
	public Individual(int length, List<Integer> list, int fitness,double a) {
		/**
		 * 个体类的构造函数
		 */
		Individual.a=a;
		this.fitness = fitness;
		this.DNA = new boolean [length];
		for(int i = 0; i < length; i++) {
			DNA[i] = false;
		}
		
		for(int i = 0; i < list.size(); i++) {
			DNA[list.get(i)] = true;
		}
	}
	
	public static void setGraph(Graph graph) {
		Individual.graph = graph;
	}
	
	public static void setForbid(Set<Integer> forbid) {
		Individual.forbid = forbid;
	}
	public static void setMustid(Set<Integer> mustid) {
		Individual.mustid = mustid;
	}
	
	public void mutation(double p1) {
		/**
		 *变异方法
		 *@param p 变异概率 一般为0.005 与 0.05之间
		 */
		double rand = Math.random();
		if(rand >=0 && rand < p1) {
			removeOneServerRandomly();
//			if(Population.generationCount<200){
//				removeOneServerRandomly();
//				//removeOneServerRandomly();
//			}
		} else if(rand >= p1 && rand < 2 * p1) {
			addOneServerRandomly();
		} else {
			removeOneServerRandomly();
			addOneServerRandomly();
		}		
		refresh();
	}
	
	public void crossover(Individual other) {
		/**
		 * 交叉方法
		 * @param other 另一个个体
		 */
		if(this.fitness == other.fitness) {
			return;
		}
		Random random = new Random();
		int position = random.nextInt(DNA.length - 1);
		for(int i = 0; i < DNA.length; i++) {
			if(i < position + 1) {
				boolean temp = DNA[i];
				DNA[i] = other.DNA[i];
				other.DNA[i] = temp;
			} else {
				break;
			}
		}
		//this.refresh();
		//other.refresh();
	}
	
	private void removeOneServerRandomly() {
		Random random = new Random();	
		List<Integer> List = new ArrayList<Integer>();
		for(int i = 0; i < DNA.length; i++) {
			if(DNA[i] && !mustid.contains(i)) {
				List.add(i);
			}
		}
		int rand = random.nextInt(List.size());
		int temp = List.get(rand);
		DNA[temp] = !DNA[temp];
	}
	
	private void addOneServerRandomly() {
		Random random = new Random();
		List<Integer> List = new ArrayList<Integer>();
		for(int i = 0; i < DNA.length; i++) {
			if(!DNA[i] && !forbid.contains(i)) {
				List.add(i);
			}
		}
		int rand = random.nextInt(List.size());
		int temp = List.get(rand);
		DNA[temp] = !DNA[temp];
	}
	
	public void change() {
		Random random = new Random();
		int position = 0;
		do {
			position = random.nextInt(DNA.length);
		} while(forbid.contains(position));
		//System.out.println(position);
		this.DNA[position] = !this.DNA[position];
		this.refresh();		
	}
	
	public Individual myclone() {
		/**
		 * 深复制方法
		 */
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < DNA.length; i++) {
			if(DNA[i]) {
				list.add(i);
			}
		}
		Individual clone = new Individual(this.DNA.length, list, this.fitness,a);
		return clone;
	}

	@Override
	public int compareTo(Individual other) {
		return this.fitness - other.fitness;
	}

	public void refresh() {
		/**
		 * 更新方法，每次DNA产生变化必须更新适应度
		 */
		Graph copy = Deploy.graphGenerator();
		List<Integer> serverList = new ArrayList<Integer>();
		for(int i = 0; i < DNA.length; i++) {
			if(DNA[i]) {
				serverList.add(i);
			}
		}
		copy.addServerList(serverList);
		List<Integer> out = Deploy.getOutput(copy,copy.getDemand(),2);
		if(out==null) {
			this.fitness = 0;
			System.out.println("Warning");
			return;
		} else {
			int fee = out.get(1);
			System.out.println("Sum fee:" + fee);
			int maxFee = Graph.consumerList.size() * Graph.serverCost;
			fitness = (int) (maxFee - fee);
			if(fitness < 0) {
				fitness =0;
			}
		}
		
	}	
}

package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {
	/**
	 * 种群类
	 * @param group 种群中的个体列表
	 * @param pc 交叉概率
	 * @param pe 变异概率
	 */
	
	public List<Individual> group;
	public Individual elite;
	public static int generationCount = 0;
	public double pc;
	public double pe;
	
	public Population(Individual individual, int n, double pc, double pe) {
		/**
		 * 种群类的构造函数
		 * @param i 初始个体
		 * @param n 种群中个体的数目
		 * @param pc 交叉概率
		 * @param pe 变异概率
		 */
		
		this.group = new ArrayList<Individual>();
		this.elite = individual;
		for(int i = 0; i < n; i++) {
			Individual clone = individual.myclone();
			clone.mutation(pe);
			this.group.add(clone);
		}
		this.pc = pc;
		this.pe = pe;
	}
	
	public void select() {
		/**
		 * 选择算子 最优保存策略
		 */
		Collections.sort(group);
		int best = group.get(group.size()-1).fitness;
		if(best > elite.fitness) {
			elite = group.get(group.size()-1).myclone();
		}
		int count = 0;
		while(count < group.size() / 2.7) {
			group.remove(0);
			group.add(elite.myclone());
			count++;
		}
		generationCount++;
	}
	
	public void cross() {
		/**
		 * 交叉算子
		 */
		Collections.shuffle(group);
		for(int i = 0; i < group.size(); i+=2) {
			if(Math.random() < pc) {
				group.get(i).crossover(group.get(i+1));
			}
		}
	}
	
	public void mutate(double num,int geshu) {
		/**
		 * 变异算子
		 */
		for(int i = 0; i < geshu; i++) {
			group.get(i).mutation(num);
		}
	}
	
	public boolean allTheSame() {
		/**
		 * 判断种群是否已经一致
		 */
		Collections.sort(group);
		if(group.get(0).fitness == group.get(group.size() - 1).fitness) {
			return true;
		} else {
			return false;
		}
	}
}

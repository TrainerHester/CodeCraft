package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import java.util.Map.Entry;

public class Graph {
	/**
	 * 图类
	 * @param struct 保存图的结构，存储形式为邻接表
	 * @param netVertexNum 网络节点数目
	 * @param edgeList 边集
	 * @param serverList 服务器表
	 * @param consumerList 消费节点
	 * @param maxCount 最大服务器个数
	 * @param minCount 最少服务器的个数
	 */
	public Map<Integer, int[]> [] struct; //ArrayList数组，下标为节点id，ArrayList中的元素是数对,第一个数为节点id,第二个数为链路id
	public static List<ConsumerVertex> consumerList;
	public static int serverCost;
	private static final int INFINITY = Integer.MAX_VALUE;
	
	
	public int getVertexNum() {
		return struct.length;
	}
	
	public int getMaxCount() {
		return consumerList.size();
	}
	
	public int getDemand() {
		int demand = 0;
    	for(int i = 0;i < consumerList.size(); i++) {
    		demand += consumerList.get(i).demand;//计算总需求
    	}
    	return demand;
	}
	
	public Graph(int netVertexNum, Edge [] edgeList) { //构造方法
		this.struct = new HashMap [netVertexNum + 2];


		//初始化过程
		for(int i = 0; i < netVertexNum + 2; i++) {
			struct[i] = new HashMap<Integer, int[]>();
		}
		
		for(int i = 0; i < edgeList.length; i++) {
			Edge thisEdge = edgeList[i];
			struct[thisEdge.startPoint].put(thisEdge.endPoint, new int[] {thisEdge.band, thisEdge.price});
			struct[thisEdge.endPoint].put(thisEdge.startPoint, new int[] {thisEdge.band, thisEdge.price});
		}
	}
	
	public void addServerList(List<Integer> serverList) {
		
		for(int i = 0; i < serverList.size(); i++) {//增加服务器虚拟节点,定为所有节点的倒数第二个，只有出没有入
			int id = serverList.get(i);
			int band = INFINITY;//带宽表示为该点的输出能力
			int price = 0;//虚拟节点，单价为0
			struct[struct.length - 2].put(id, new int[] {band, price}) ;
		}
		
		for(int i = 0; i < consumerList.size();i++) {//增加消费总结点,定为所有节点的最后一个,只有入没有出
			int start = consumerList.get(i).connectedVertex;
			int end = struct.length - 1;
			int band = consumerList.get(i).demand;//带宽表示为需求
			int price = 0;//虚拟节点，单价为0
			struct[start].put(end, new int[] {band, price});
		}
	}
	
	public List<Integer> getTopN(int n) {
		List<Integer> output = new ArrayList<Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for(int i = 0; i < struct.length - 2; i++) {
			map.put(i, getOuputAbility(i));
		}
		List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
		    public int compare(Map.Entry<Integer, Integer> o1,
		            Map.Entry<Integer, Integer> o2) {
		        return (o2.getValue() - o1.getValue());
		    }
		});
		for(int i = 0; i < n; i++) {
			output.add(list.get(i).getKey());
		}
		return output;
	}
	
	public List<Integer> getLastN(int n) {
		List<Integer> output = new ArrayList<Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for(int i = 0; i < struct.length - 2; i++) {
			map.put(i, getOuputAbility(i));
		}
		List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
		    public int compare(Map.Entry<Integer, Integer> o1,
		            Map.Entry<Integer, Integer> o2) {
		        return (o2.getValue() - o1.getValue());
		    }
		});
		Collections.reverse(list);
		for(int i = 0; i < n; i++) {
			output.add(list.get(i).getKey());
		}
		return output;
	}
	
	public List<Integer> getTopServer(int n) {
		List<Integer> output = new ArrayList<Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for(int i = 0; i < consumerList.size(); i++) {
			map.put(consumerList.get(i).connectedVertex, getOuputAbility(consumerList.get(i).connectedVertex));
		}
		List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
		    public int compare(Map.Entry<Integer, Integer> o1,
		            Map.Entry<Integer, Integer> o2) {
		        return (o2.getValue() - o1.getValue());
		    }
		});
		for(int i = 0; i < n; i++) {
			output.add(list.get(i).getKey());
			//System.out.println(list.get(i).getKey());
			//System.out.println(list.get(i).getValue());
		}
		
		return output;
	}
	
	private int getOuputAbility(int i) {
		/**
		 * 获取节点的输出能力
		 * @param i 节点编号
		 * @return 输出能力
		 */
		int result = 0;
		for(Integer key: struct[i].keySet()) {
			result += struct[i].get(key)[0];
		}
		return result;
	}
	
	
	
}

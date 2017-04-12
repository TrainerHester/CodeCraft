package com.cacheserverdeploy.deploy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Queue;
import java.util.Set;

public class Deploy implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3840820909564598408L;
	/**
     * 你需要完成的入口
     * <功能详细描述>
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
	private static int INFINITY = 9999; //静态变量,用于表示无穷
	
    public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
    	/*for(String s: graphContent) {
    		System.out.println(s);
    	}*/
    	Deploy deploy = new Deploy();
    	
    	String [] num = graphContent[0].split(" ");
    	int netVertexNum = Integer.parseInt(num[0]);	//网络节点数目
    	int edgeNum = Integer.parseInt(num[1]);			//链路数目
    	int consumerVertexNum = Integer.parseInt(num[2]); //消费节点数目
    	int serverCost = Integer.parseInt(graphContent[2]); //服务器价格
    	
    	Edge [] edgeList = new Edge [edgeNum];	//Edge数组
    	for(int i = 0; i < edgeNum; i++) {
    		edgeList[i] = deploy.new Edge(i, graphContent[4 + i]); //存储链路信息
    	}
    	
    	ArrayList<ConsumerVertex> consumerVertexList = new ArrayList<ConsumerVertex>(); //消费节点数组
    	ArrayList<Integer> serverList = new ArrayList<Integer>();
    	int demand = 0;
    	for(int i = 0;i < consumerVertexNum; i++) {
    		consumerVertexList.add(deploy.new ConsumerVertex(graphContent[5 + edgeNum + i])); //存储消费节点信息
    		demand += consumerVertexList.get(i).demand;
    		//serverList.add(consumerVertexList.get(i).connectedVertex);
    	}
    	String best = "4 11 17 20 22 25 30 36 37 46 49 54 56 57 60 61 66 69 72 81 83 85 89 97 103 106 108 114 123 126 128 131 135 137 139 147 155 159"; 
    	String [] s = best.split(" ");
      	for(String ss : s) {
      		serverList.add(Integer.parseInt(ss));
      	}
//    	serverList.remove(serverList.size() - 1);
//    	serverList.remove(serverList.size() - 1);
   	
//    	ArrayList<Integer> a = new ArrayList<Integer>();
//    	a.add(12);
//    	a.add(1);
//    	a.add(141);
//    	a.add(44);
//    	a.add(135);
//    	a.add(81);
//    	a.add(28);
//    	a.add(90);
//    	a.add(43);
//    	a.add(85);
//    	a.add(118);
//    	a.add(103);
//    	a.add(63);
//    	a.add(75);
//    	a.add(76);
//    	a.add(146);
//    	a.add(137);
//    	a.add(26);
//    	a.add(2);
//    	a.add(139);
//    	a.add(54);
//    	a.add(126);
//    	a.add(114);
//    	a.add(51);
//    	a.add(68);
//    	a.add(4);
//    	a.add(147);
//    	a.add(136);
//    	a.add(35);
//    	a.add(149);
//    	a.add(15);
//    	a.add(29);
//    	a.add(130);
//    	a.add(142);
//    	a.add(113);
//    	a.add(110);
//    	a.add(55);
//    	a.add(91);
//    	a.add(112);
//    	a.add(111);
//    	a.add(0);
//    	a.add(25);
//    	a.add(53);
    	
    	
//    	Graph graph = deploy.new Graph(netVertexNum, edgeList, consumerVertexList, a);	//将图转化为邻接表的结构
//    	System.out.println(graph.maxCount);
//    	System.out.println(graph.minCount);
    	Graph graph = deploy.new Graph(netVertexNum, edgeList, consumerVertexList, serverList);	
    	ArrayList<ArrayList<Integer>> out = function(graph.myclone(), demand, serverCost);
    	int fee = sumFee(graph, serverCost, out);
    	System.out.println(fee);
    	
    	int iterations = 1000;
    	for(int i = 0; i < iterations; i++) {
    		//1.随机增加服务器
    		//2.随机减少服务器
    		//3.随机改变服务器的位置
    		//to be continued.......
    	}
    	
    	
    	//ArrayList<ArrayList<Integer>> out = function(graph.myclone(), demand, serverCost);
    	//int fee = sumFee(graph, serverCost, out);
    	//System.out.println(fee);
        return new String[]{"17","\r\n","0 8 0 20"};
    }
    
    
    class Vertex {
		public int id;
		public boolean known;
		public int [] dv;
		public int pv;
		
		public Vertex(int id, boolean known, int dv, int pv) {
			this.dv = new int [2];
			this.id = id;
			this.known = known;
			this.dv[0] = dv;
			this.dv[1] = INFINITY; //表示路径上最小带宽
			this.pv = pv;
		}
	}
    	
	class Edge implements Serializable {
    	/**
		 * 
		 */
		private static final long serialVersionUID = -759628232779267634L;
		public int id;
    	public int startPoint;
    	public int endPoint;
    	public int upBand;
    	public int downBand;
    	public int price;
    	public int band;

    	Edge(int id, int startPoint, int endPoint, int band, int price) { 
    		this.id = id;
    		this.startPoint = startPoint;
    		this.endPoint = endPoint;
    		this.upBand = band;
    		this.downBand = band;
    		this.band = band;
    		this.price = price;
    	}

		Edge(int id, String parameters) {
			this.id = id;
    		String [] forInput = parameters.split(" ");
    		this.startPoint = Integer.parseInt(forInput[0]);
    		this.endPoint = Integer.parseInt(forInput[1]);
    		this.upBand = Integer.parseInt(forInput[2]);
    		this.downBand = this.upBand;
    		this.band = this.downBand;
    		this.price = Integer.parseInt(forInput[3]);
		}
    	
    }
	
	public class ConsumerVertex implements Comparable<ConsumerVertex>{
    	/*
    	 * 消费节点类
    	 * @param id 编号
    	 * @param connectedVertex 相连的网络节点编号
    	 * @param demand 需求带宽
    	 */
    	public int id;
    	public int connectedVertex;
    	public int demand;
    	
    	ConsumerVertex(String parameters) { //构造方法
    		String [] forInput = parameters.split(" ");
    		this.id = Integer.parseInt(forInput[0]);
    		this.connectedVertex = Integer.parseInt(forInput[1]);
    		this.demand = Integer.parseInt(forInput[2]);
    	}

		@Override
		public int compareTo(ConsumerVertex another) {
			// TODO Auto-generated method stub
			return new Integer(this.demand).compareTo(new Integer(another.demand));
		}    	
    }
	
	public class OnePath {
		public int startPoint;
		public int endPoint;
		public int band;
		
		OnePath(int startPoint, int endPoint, int band) {
			this.startPoint = startPoint;
			this.endPoint = endPoint;
			this.band = band;
		}
	}
	
	public class Path {
		public ArrayList<OnePath> pathList;
		public int start;
		public int end;
		
		Path(int start, int end) {
			this.start = start;
			this.end =end;
			this.pathList = new ArrayList<OnePath>();
		}
		
		Path(int start, int end, ArrayList<OnePath> pathList) {
			this(start, end);
			for(OnePath onePath: pathList) {
				this.addPath(onePath);
			}
		}
		
		void addPath(OnePath onepath) {
			if(pathList.isEmpty()) {
				pathList.add(onepath);
				return;
			}
			int i = 0;
			for(; i < pathList.size(); i++) {
				OnePath thisOne = pathList.get(i);
				if(onepath.startPoint == thisOne.startPoint && onepath.endPoint == thisOne.endPoint) {
					thisOne.band += onepath.band;
					return;
				} else if(onepath.startPoint == thisOne.endPoint && onepath.endPoint == thisOne.startPoint) {
					if(thisOne.band > onepath.band) {
						thisOne.band -= onepath.band;
					} else if(thisOne.band < onepath.band) {
						onepath.band -= thisOne.band;
						pathList.remove(i);
						pathList.add(onepath);
					} else {
						pathList.remove(i);
					}
						
					return;
				}
			}
			if(i == pathList.size()) {
				pathList.add(onepath);
			}
		}
		
		public int fee(Graph graph){
			int output = 0;
			for(OnePath o : pathList) {
				int start = o.startPoint;
				int end = o.endPoint;
				output += graph.edgeList.get(graph.struct[start].get(end)).price * o.band;
			}
			//System.out.println(output);
			return output;
		}
		
		public int sumFlow() {
			int output = 0;
			for(OnePath o : pathList) {
				if(o.startPoint == start) {
					output += o.band;
				}
			}
			return output;
		}
		
		public ArrayList<ArrayList<Integer>> fromPathToString() {
			SimpleGraph simple = new SimpleGraph(this);
			return simple.getAllPath(start, end);
		}
		
		/*public void fromPathToString(SimpleGraph graph) {
			SimpleGraph simple = new SimpleGraph(this);
			simple.getAllPath(start, end, graph);
		}*/
		
		
		
	}
	
	class SimpleGraph {
		
		class Point {
			public int id;
			public int dist;
			public int path;
			
			Point(int id, int dist, int path) {
				this.id = id;
				this.dist = dist;
				this.path = path;
			}
		}
		
		HashMap<Integer, HashMap<Integer, OnePath>> struct;
		HashMap<Integer, Point> pointMap;
		
		SimpleGraph(Path path) {
			this.struct = new HashMap<Integer, HashMap<Integer, OnePath>>();
			this.pointMap = new HashMap<Integer, Point>();
			ArrayList<OnePath> pathList = path.pathList;
			for(int i = 0; i < pathList.size(); i++) {
				OnePath present = pathList.get(i);
				if(!struct.containsKey(present.startPoint)) {
					struct.put(present.startPoint, new HashMap<Integer, OnePath>());
					pointMap.put(present.startPoint, new Point(present.startPoint, INFINITY, -1));
				}
				
				if(!struct.containsKey(present.endPoint)) {
					struct.put(present.endPoint, new HashMap<Integer, OnePath>());
					pointMap.put(present.endPoint, new Point(present.endPoint, INFINITY, -1));
				}
				struct.get(present.startPoint).put(present.endPoint, present);
			}
		}
		
		SimpleGraph(List<Edge> path) {
			this.struct = new HashMap<Integer, HashMap<Integer, OnePath>>();
			this.pointMap = new HashMap<Integer, Point>();
			for(int i = 0; i < path.size(); i++) {
				OnePath present = new OnePath(path.get(i).startPoint, path.get(i).endPoint, path.get(i).band);
				if(!struct.containsKey(present.startPoint)) {
					struct.put(present.startPoint, new HashMap<Integer, OnePath>());
					pointMap.put(present.startPoint, new Point(present.startPoint, INFINITY, -1));
				}
				
				if(!struct.containsKey(present.endPoint)) {
					struct.put(present.endPoint, new HashMap<Integer, OnePath>());
					pointMap.put(present.endPoint, new Point(present.endPoint, INFINITY, -1));
				}
				struct.get(present.startPoint).put(present.endPoint, present);
			}
		}
		
		public ArrayList<Integer> bfs(int start, int end) {
			ArrayList<Integer> output = new ArrayList<Integer>();
			Queue<Point> queue = new LinkedList<Point>();
			for(Integer key: pointMap.keySet()) {
				pointMap.put(key, new Point(key, INFINITY, -1));
			}
			
			pointMap.get(start).dist = 0;
			queue.offer(pointMap.get(start));
			
			while(!queue.isEmpty() && pointMap.get(end).dist == INFINITY) {
				Point p = queue.poll();
				HashMap<Integer, OnePath> linkedPoint = struct.get(p.id);
				for(Integer key: linkedPoint.keySet()) {
					if(pointMap.get(key).dist == INFINITY) {
						pointMap.get(key).dist = pointMap.get(p.id).dist + 1;
						pointMap.get(key).path = p.id;
						queue.offer(pointMap.get(key));
					}
				}
			}
			int pv = end;
			while(pv != -1) {
				output.add(pv);
				pv = pointMap.get(pv).path;
			}
			if(output.size() == 1) {
				return null;
			}
			Collections.reverse(output);
			//System.out.println(output.toString());
			return output;
		}
		
		public ArrayList<OnePath> getOneSetByBFS(int server) {
			ArrayList<OnePath> output = new ArrayList<OnePath>();
			Queue<Point> queue = new LinkedList<Point>();
			for(Integer key: pointMap.keySet()) {
				pointMap.put(key, new Point(key, INFINITY, -1));
			}
			
			pointMap.get(server).dist = 0;
			queue.offer(pointMap.get(server));
			
			while(!queue.isEmpty()) {
				Point p = queue.poll();
				HashMap<Integer, OnePath> linkedPoint = struct.get(p.id);
				for(Integer key: linkedPoint.keySet()) {//目测此处要修改
					output.add(linkedPoint.get(key));
					queue.offer(pointMap.get(key));
//					if(pointMap.get(key).dist == INFINITY) {
//						pointMap.get(key).dist = pointMap.get(p.id).dist + 1;
//						output.add(linkedPoint.get(key));
//						queue.offer(pointMap.get(key));
//						System.out.println(pointMap.get(key).id);
//						/*if(!custom.contains(linkedPoint.get(key).endPoint)) {
//							queue.offer(pointMap.get(key));
//						}*/
//					}
				}
			}
			return new ArrayList<OnePath>(new HashSet<OnePath>(output));
		}
		
		/*public void getAllPath(int start, int end) {
			int demand = 0;
			for(Integer key: struct.get(start).keySet()) {
				demand += struct.get(start).get(key).band;
			}
			
			while(demand > 0) {
				ArrayList<Integer> path = bfs(start, end);
				if(path == null) {
					break;
				}
				int band = INFINITY;
				for(int i = 0; i < path.size() - 1; i++) {
					int one = path.get(i);
					int two = path.get(i + 1);
					if(band > struct.get(one).get(two).band) {
						band = struct.get(one).get(two).band;
					}
				}
				
				for(int i = 0; i < path.size() - 1; i++) {
					int one = path.get(i);
					int two = path.get(i + 1);
					struct.get(one).get(two).band -= band;
					if(struct.get(one).get(two).band == 0) {
						struct.get(one).remove(two);
					}
				}
				demand -= band;
				System.out.println("path: " + path.toString() + " , band: " + String.valueOf(band));
			}
			
		}*/
		
		public ArrayList<ArrayList<Integer>> getAllPath(int start, int end) {
			int demand = 0;
			ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
			for(Integer i : struct.keySet()) {
				HashMap<Integer, OnePath> thisPoint = struct.get(i);
				for(Integer key : thisPoint.keySet()) {
					OnePath o = thisPoint.get(key);
					if(o.endPoint == end) {
						demand += o.band;
					}
				}
			}
			/*
			for(Integer key: struct.get(start).keySet()) {
				demand += struct.get(start).get(key).band;
			}*/
			
			while(demand > 0) {
				ArrayList<Integer> path = bfs(start, end);
				if(path == null) {
					break;
				}
				int band = INFINITY;
				for(int i = 0; i < path.size() - 1; i++) {
					int one = path.get(i);
					int two = path.get(i + 1);
					if(band > struct.get(one).get(two).band) {
						band = struct.get(one).get(two).band;
					}
				}
				
				for(int i = 0; i < path.size() - 1; i++) {
					int one = path.get(i);
					int two = path.get(i + 1);
					struct.get(one).get(two).band -= band;
					//System.out.println(struct.get(one).get(two).band);
					//graph.struct.get(one).get(two).band -= band;
					//System.out.println(struct.get(one).get(two).band);
					if(struct.get(one).get(two).band == 0) {
						struct.get(one).remove(two);
						//graph.struct.get(one).remove(two);
					}
				}
				demand -= band;
				System.out.println("path: " + path.toString() + " , band: " + String.valueOf(band));
				path.add(band);
				output.add(path);
				
			}
			return output;
			
		}
	}
	
	public class Graph implements Serializable{
    	/*
    	 * 图类
    	 * @param struct 保存图的结构，存储形式为邻接表
    	 * @param netVertexNum 网络节点数目
    	 */
		private static final long serialVersionUID = 872390113109L;
    	public HashMap<Integer, Integer> [] struct; //ArrayList数组，下标为节点id，ArrayList中的元素是数对,第一个数为节点id,第二个数为链路id
    	public int netVertexNum;
    	public ArrayList<Edge> edgeList;
    	public ArrayList<Integer> serverList;
    	public int maxCount;
    	public int minCount;
    	
    	public Graph(int netVertexNum, Edge [] edgeList, ArrayList<ConsumerVertex> consumerList, ArrayList<Integer> serverList) { //构造方法
    		this.struct = new HashMap [netVertexNum + 2];
    		this.edgeList = new ArrayList<Edge>();
    		this.serverList = serverList;
    		this.maxCount = consumerList.size();
    		this.minCount = 0;
    		for(int i = 0; i < edgeList.length;i++) {
    			this.edgeList.add(edgeList[i]);//加入原始的边
    		}
     		this.netVertexNum = netVertexNum + 2;
    		
    		for(int i = 0; i < netVertexNum + 2; i++) {
    			struct[i] = new HashMap<Integer, Integer>();
    		}
    		
    		for(int i = 0; i < edgeList.length; i++) {
    			Edge thisEdge = edgeList[i];
    			struct[thisEdge.startPoint].put(thisEdge.endPoint, i);
    			struct[thisEdge.endPoint].put(thisEdge.startPoint, i);
    		}
    		
    		Set<Integer> set = new HashSet<Integer>();
    		for(int i = 0; i < consumerList.size(); i++) {
    			set.add(consumerList.get(i).connectedVertex);
    		}
    		
    		
    		int [] serverBand = new int [serverList.size()];//服务器节点的输出能力
    		for(int i = 0; i < serverList.size(); i++) {
    			int temp = 0;
    			HashMap<Integer, Integer> now = struct[serverList.get(i)];
    			for(Integer key : now.keySet()) {
    				temp += this.edgeList.get(now.get(key)).band;
    			}
    			if(set.contains(serverList.get(i))) {
    				for(int j = 0; j < consumerList.size(); j++) {
    					if(consumerList.get(j).connectedVertex == serverList.get(i)) {
    						temp += consumerList.get(j).demand;
    					}
    				}
    			}
    			serverBand[i] = temp;
    		}
    		
    		int id = this.edgeList.size() - 1;
    		for(int i = 0; i < serverList.size(); i++) {//增加服务器虚拟节点,定为所有节点的倒数第二个
    			int start = struct.length - 2;
    			int end = serverList.get(i);
    			id++;
    			int band = serverBand[i];
    			int price = 0;
    			Edge edge = new Edge(id, start, end, band, price);
    			this.edgeList.add(edge);
    			struct[struct.length - 2].put(end, id);
    		}
    		
    		for(int i = 0; i < consumerList.size();i++) {//增加消费总结点,定为所有节点的最后一个
    			id++;
    			int start = consumerList.get(i).connectedVertex;
    			int end = struct.length - 1;
    			int band = consumerList.get(i).demand;
    			int price = 0;
    			Edge edge = new Edge(id, start, end, band, price);
    			this.edgeList.add(edge);
    			struct[start].put(end, id);
    		}
    		
    		Map<Integer, ConsumerVertex> m = new HashMap<Integer, ConsumerVertex>();
        	
    		int demand = 0;
        	for(int i = 0;i < consumerList.size(); i++) {
        		m.put(consumerList.get(i).connectedVertex, consumerList.get(i));
        		demand += consumerList.get(i).demand;
        	}
    		List<Integer> eachVertex = new ArrayList<Integer>();
        	for(int i = 0; i < struct.length - 2; i++) {
        		int temp = 0;
        		for(Integer key: struct[i].keySet()) {
        			temp += this.edgeList.get(struct[i].get(key)).band;
        		}
        		if(m.containsKey(i)) {
        			temp += m.get(i).demand;
        		}
        		eachVertex.add(temp);
        	}
        	
        	Collections.sort(eachVertex);
        	Collections.reverse(eachVertex);
        	for(int t = 0; t < demand; minCount++) {
        		t += eachVertex.get(minCount);
        	}
        	minCount++; //确定服务器个数下限
    		
    	}
    	
    	public void change(Path p) {
    		for(OnePath o : p.pathList) {
    			int start = o.startPoint;
    			int end = o.endPoint;
    			int flag;
    			if(!struct[start].keySet().contains(end)) {
    				continue;
    			}
    			Edge thisEdge = edgeList.get(struct[start].get(end));
    			if(thisEdge.startPoint == start) {
    				thisEdge.upBand -= o.band;
    				flag = thisEdge.upBand;
    			} else {
    				thisEdge.downBand -= o.band;
    				flag = thisEdge.downBand;
    			}
    			if(flag == 0) {
    				struct[start].remove(end);
    			}
    			
    		}
    	}
    	
    	public Graph myclone() {
    		Graph graph = null;
    		try {
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			ObjectOutputStream oos = new ObjectOutputStream(baos);
    			oos.writeObject(this);
    			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    			ObjectInputStream ois = new ObjectInputStream(bais);
    			graph = (Graph) ois.readObject();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
    			e.printStackTrace();
    		}
    		return graph;
    	}
    }
	
	public static List<Integer> dijkstra(Graph graph, int startPoint, int endPoint) {
    	/*
    	 * 求两个节点之间的最短路径，将单价作为链路权值
    	 * 原理是dijkstra算法,时间复杂度O(VlogV),V表示节点数目
    	 * @param graph 图
    	 * @param startPoint 起点
    	 * @param endPoint 终点
    	 * @return List<Integer> 路径中的节点坐标集，注意是反向的，即从终点到起点
    	 */
    	class Heap {
    		/*
    		 * Heap类，表示最小堆，用于寻找当前最短路径对应的节点
    		 */
    		
    		public Vector<Vertex> vertexList; //节点向量
    		
    		Heap() { //构造函数1
    			this.vertexList = new Vector<Vertex>();
    		}
    		
    		public void insert(Vertex ver) {
    			/*
    			 * 将元素加入到最小堆
    			 * @param ver 待加入的元素
    			 * 
    			 */
    			int hole = vertexList.size();
    			vertexList.add(ver);
    			for(; hole > 0 && (ver.dv[0] < vertexList.get((hole - 1)/2).dv[0] ||  
    					(ver.dv[0] == vertexList.get((hole - 1)/2).dv[0]) && 
    					(ver.dv[1] > vertexList.get((hole - 1)/2).dv[1])); hole = (hole - 1)/2) {
    				vertexList.set(hole, vertexList.get((hole - 1)/2));
    			}
    			vertexList.set(hole, ver);
    		}
    		
    		public Vertex deleteMin() {
    			/*
    			 * 删除最小的元素并返回
    			 * 
    			 */
    			if(vertexList.isEmpty()) {
    				return null;
    			}
    			Vertex forReturn = vertexList.get(0);
    			Vertex tmp = vertexList.lastElement();
    			vertexList.set(0, tmp);
    			int hole = 0;
    			int child;
    			for(;hole * 2 + 1 < vertexList.size(); hole = child) {
    				child = hole * 2 + 1;
    				if(child != vertexList.size() - 1 && (vertexList.get(child).dv[0] > vertexList.get(child+1).dv[0]
    						|| (vertexList.get(child).dv[0] == vertexList.get(child+1).dv[0] && 
    							vertexList.get(child).dv[1] < vertexList.get(child+1).dv[1]))) {
    					child++;
    				}
    				if(vertexList.get(child).dv[0] < tmp.dv[0] || (vertexList.get(child).dv[0] == tmp.dv[0] &&
    						vertexList.get(child).dv[1] > tmp.dv[1])) {
    					vertexList.set(hole, vertexList.get(child));
    				} else {
    					break;
    				}
    			}
    			vertexList.set(hole, tmp);
    			vertexList.remove(vertexList.size() - 1);
    			return forReturn;
    		}
    		
    	}
    	
    	Deploy deploy = new Deploy();
    	Vertex [] vertexList = new Vertex [graph.netVertexNum];
    	for(int i = 0; i < graph.netVertexNum; i++) {
    		vertexList[i] = deploy.new Vertex(i, false, INFINITY, -1);
    	}
    	vertexList[startPoint].dv[0] = 0;
    	Heap heap = new Heap();
    	heap.insert(vertexList[startPoint]);
    	
    	while(true) {
    		Vertex forProcess;
    		while(true) {
    			forProcess = heap.deleteMin();
    			if(forProcess == null) {
    				//System.out.println("No path");
    				return null;
    			}
    			if(!forProcess.known) {
    				break;
    			}
    		}
    		forProcess.known = true;
    		HashMap<Integer, Integer> hashMap = graph.struct[forProcess.id];
    		Iterator<Integer> it = hashMap.keySet().iterator();
    		while(it.hasNext()) {
    			Integer key = (Integer) it.next();
    			Vertex update = vertexList[key];
    			Edge line = graph.edgeList.get(hashMap.get(key));
    			if(!update.known && (update.dv[0] > forProcess.dv[0] + line.price ||
    					(update.dv[0] == forProcess.dv[0] + line.price && update.dv[1] < min(forProcess.dv[1], line.band)))) {
    				update.dv[1] = min(forProcess.dv[1], line.band);
    				//路径上的最小带宽
    				update.dv[0] = forProcess.dv[0] + line.price;//to be continued...................................
    				update.pv = forProcess.id;
    				heap.insert(update);
    			}	
    		}
    		
    		if(forProcess.id == endPoint) {
    			break;
    		}
    	}
    	List<Integer> resultList = new ArrayList<Integer>();
    	int thisVertexId = vertexList[endPoint].pv;
    	while(thisVertexId != -1) {
    		resultList.add(thisVertexId);
    		thisVertexId = vertexList[thisVertexId].pv;
    	}

    	Collections.reverse(resultList);
    	resultList.add(endPoint);
    	System.out.println("----------------------------------------------------");
    	System.out.println(new StringBuilder(resultList.toString()));
    	System.out.println("----------------------------------------------------");
    	return resultList;
    }
	
//	public static Path maxFlowMinFee(Graph graph, int start, int end) {
//		Deploy deploy = new Deploy();
//		int theta = INFINITY;
//		List<Integer> output = dijkstra(graph, start, end);
//		if(output==null) {
//			return null;
//		}
//		Path path = deploy.new Path(start, end);
//		while(output != null) {
//			for(int i = 0; i < output.size() - 1; i++) {
//				int startPoint = output.get(i);
//				int endPoint = output.get(i + 1);
//				int band;
//				Edge thisEdge = graph.edgeList.get((int) graph.struct[startPoint].get(endPoint));
//				band = thisEdge.band;
//				if(band < theta) {
//					theta = band;//找出路径上的最小带宽
//				}
//			}
//			for(int i = 0; i < output.size() - 1; i++) {
//				int startPoint = output.get(i);
//				int endPoint = output.get(i + 1);
//				int band;
//				Edge thisEdge = graph.edgeList.get((int) graph.struct[startPoint].get(endPoint));
//				band = thisEdge.band;
//				path.addPath(deploy.new OnePath(startPoint, endPoint, theta));
//				if(band == theta) {
//					graph.struct[startPoint].remove(endPoint);
//					if(i != 0 && i != output.size() - 1) {
//						thisEdge.price = - thisEdge.price;
//					} 
//				} else {
//					thisEdge.band -= theta;
//				}
//			}
//			theta = INFINITY;
//			output = dijkstra(graph, start, end);
//			if(output == null) {
//				System.out.println("Fail");
//			}
//		}
//		return path;
//		
//	}
	
	public static Path maxFlowMinFee(Graph graph, int start, int end, int demand) {
		Deploy deploy = new Deploy();
		int presentFlow = 0;
		int theta = INFINITY;
		List<Integer> output = dijkstra(graph, start, end);
		if(output==null) {
			return null;
		}
		Path path = deploy.new Path(start, end);
		while(output != null && presentFlow < demand) {
			for(int i = 0; i < output.size() - 1; i++) {
				int startPoint = output.get(i);
				int endPoint = output.get(i + 1);
				int band;
				Edge thisEdge = graph.edgeList.get((int) graph.struct[startPoint].get(endPoint));
				if(thisEdge.startPoint == startPoint) {
					band = thisEdge.upBand;
				} else {
					band = thisEdge.downBand;
				}
				if(band < theta) {
					theta = band;
				}
			}
			if(theta + presentFlow < demand) {
				presentFlow += theta;
			} else {
				theta = demand - presentFlow;
				presentFlow = demand;	
			}
			for(int i = 0; i < output.size() - 1; i++) {
				int startPoint = output.get(i);
				int endPoint = output.get(i + 1);
				int band;
				Edge thisEdge = graph.edgeList.get((int) graph.struct[startPoint].get(endPoint));
				if(thisEdge.startPoint == startPoint) {
					band = thisEdge.upBand;
				} else {
					band = thisEdge.downBand;
				}
				path.addPath(deploy.new OnePath(startPoint, endPoint, theta));
				if(band == theta) {
					graph.struct[startPoint].remove(endPoint);
					if(i != 0 && i != output.size() - 1) {
						thisEdge.price = - thisEdge.price;
					} 
				} else {
					if(thisEdge.startPoint == startPoint) {
						thisEdge.upBand -= theta;
					} else {
						thisEdge.downBand -= theta;
					}
					//thisEdge.band -= theta;
				}
			}
			theta = INFINITY;
			output = dijkstra(graph, start, end);
			if(output == null && presentFlow < demand) {
				System.out.println("Fail");
				return null;
			}
		}
		/*
		for(int i = 0; i < path.pathList.size(); i++) {
			String out = String.valueOf(path.pathList.get(i).startPoint) + " -> " + String.valueOf(path.pathList.get(i).endPoint);
			out += (" : " + String.valueOf(path.pathList.get(i).band)); 
			System.out.println(out);
		}
		*/
		return path;
		
	}
	
	public static void getOutput(List<Edge> list, int [] serverId, Set<Integer> customId) {
		SimpleGraph graph = new Deploy().new SimpleGraph(list);
		for(int i = 0; i < serverId.length; i++) {
			int server = serverId[i];
			ArrayList<OnePath> onePathList = graph.getOneSetByBFS(server);
			Set<Integer> subCustomId = new HashSet<Integer>();
			for(OnePath onepath : onePathList) {
				if(customId.contains(onepath.endPoint)) {
					subCustomId.add(onepath.endPoint);
				}
			}
			
			
			for(Integer c : subCustomId) {
				
				if(server == c) {
					continue;
				}
				Path thisPath = new Deploy().new Path(server, c, onePathList);
				thisPath.fromPathToString();
			}
		}
		
	}
	
	public static int min(int x, int y) {
		if(x > y) {
			return y;
		} else {
			return x;
		}
	}
    
    public static ArrayList<ArrayList<Integer>> function(Graph graph, int demand, int serverCost ) {
    	int netVertexNum = graph.netVertexNum;
    	for(int i = 0; i < graph.struct.length; i++) {
    		String out = "Vertex " + String.valueOf(i) + ": ";
    		for(Integer key: graph.struct[i].keySet()) {
    			out += ("[Vertex " + String.valueOf(key) + " Edge " + String.valueOf(graph.struct[i].get(key)) + "]");
    		}
    		System.out.println(out);
    	}
    	System.out.println("-------------------------------------------------------------------------------");

    	Graph clone = graph.myclone();//备份 Significant!!!!!!!!!!!!
    	Path path = maxFlowMinFee(clone, netVertexNum-2, netVertexNum-1, demand);
    	ArrayList<ArrayList<Integer>> out = path.fromPathToString();
//    	int fee = 0; //总价
//    	for(int i = 0; i < out.size(); i++) {
//    		ArrayList<Integer> sub = out.get(i);
//    		if(sub.size() == 4) {
//    			continue;
//    		}
//    		int band = sub.get(sub.size() - 1);
//    		//sub.remove(sub.size() - 1);
//    		//sub.remove(sub.size() - 1);
//    		//sub.remove(0);
//    		System.out.println(sub.toString());
//    		for(int j = 0; j < sub.size() - 2; j++) {
//    			fee += graph.edgeList.get(graph.struct[sub.get(j)].get(sub.get(j+1))).price * band;
//    		}
//    	}
//    	System.out.println(fee + graph.serverList.length * serverCost);
    	return out;
    }
    
    public static int sumFee(Graph graph, int serverCost, ArrayList<ArrayList<Integer>> out) {
    	int fee = 0; //总价
    	for(int i = 0; i < out.size(); i++) {
    		ArrayList<Integer> sub = out.get(i);
    		if(sub.size() == 4) {
    			continue;
    		}
    		int band = sub.get(sub.size() - 1);
    		//sub.remove(sub.size() - 1);
    		//sub.remove(sub.size() - 1);
    		//sub.remove(0);
    		System.out.println(sub.toString());
    		for(int j = 0; j < sub.size() - 2; j++) {
    			fee += graph.edgeList.get(graph.struct[sub.get(j)].get(sub.get(j+1))).price * band;
    		}
    	}
    	//System.out.println(fee + graph.serverList.length * serverCost);
    	fee += graph.serverList.size() * serverCost;
    	return fee;
    }
}

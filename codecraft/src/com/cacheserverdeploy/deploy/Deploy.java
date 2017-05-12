package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Deploy {
	/**
	 * 你需要完成的入口 <功能详细描述>
	 * 
	 * @param graphContent
	 *            用例信息文件
	 * @return [参数说明] 输出结果信息
	 * @see [类、类#方法、类#成员]
	 */
	private static final int INFINITY = Integer.MAX_VALUE;; // 静态变量,用于表示无穷
	private static boolean flag = true;
	static List<Integer> serverList2 = new ArrayList<Integer>();
	static Graph graph;
	public static int netVertexNum;
	public static Edge[] edgeList;

	public static Graph graphGenerator() {
		return new Graph(netVertexNum, edgeList);
	}

	public static String[] deployServer(String[] graphContent) {
		/** do your work here **/
		String[] num = graphContent[0].split(" ");
		Deploy.netVertexNum = Integer.parseInt(num[0]); // 网络节点数目
		int edgeNum = Integer.parseInt(num[1]); // 链路数目
		int consumerVertexNum = Integer.parseInt(num[2]); // 消费节点数目
		int serverCost = Integer.parseInt(graphContent[2]); // 服务器价格

		Deploy.edgeList = new Edge[edgeNum]; // Edge数组
		for (int i = 0; i < edgeNum; i++) {
			edgeList[i] = new Edge(i, graphContent[4 + i]); // 存储链路信息
		}

		List<ConsumerVertex> consumerVertexList = new ArrayList<ConsumerVertex>(); // 消费节点数组
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		// List<Integer> bestStart = new ArrayList<Integer>();
		for (int i = 0; i < consumerVertexNum; i++) {
			consumerVertexList.add(new ConsumerVertex(graphContent[5 + edgeNum + i])); // 存储消费节点信息
			map.put(consumerVertexList.get(i).connectedVertex, consumerVertexList.get(i).id); // 用网络节点搜索消费节点
			// bestStart.add(consumerVertexList.get(i).connectedVertex);
		}
		Graph.consumerList = consumerVertexList;
		Graph.serverCost = serverCost;
		graph = new Graph(netVertexNum, edgeList);// 构图

		int count = graph.getMaxCount();
		List<Integer> serverList1 = graph.getTopServer(count);

		int minFee = INFINITY;
		List<Integer> bestStart = null;

		double flagNum = 0;
		int geshu = 0;
		int mustDemand = 0;
		int fordDemand = 0;
		if (netVertexNum < 200) {
			flagNum = 0.66;
			geshu = 1;
			fordDemand = 30;
			mustDemand = 120;
		} else if (netVertexNum > 200 && netVertexNum < 600) {
			flagNum = 0.45;
			geshu = 3;
			fordDemand = 30;
			mustDemand = 120;
		} else {
			flagNum = 0.75;
			geshu = 1;
			fordDemand = 30;
			mustDemand = 120;
		}

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				// System.out.println("time is over!");
				flag = false;
			}
		}, 87500);// 设定指定的时间time,此处为85000毫秒

		int countMin = 0;
		while (count >= 0 && flag) {
			Graph copy = new Graph(netVertexNum, edgeList);
			List<Integer> serverList3 = new ArrayList<Integer>();
			for (int i = 0; i < count; i++) {
				serverList3.add(serverList1.get(i));
			}
			for (Integer i : serverList2) {
				serverList3.add(i);
			}
			copy.addServerList(serverList3);
			List<Integer> out = getOutput(copy, copy.getDemand(), 2);
			// if (out.get(out.size() - 1).get(0) == -1) {
			if (out == null) {
				if (netVertexNum < 600) {
					serverList2.add(serverList1.get(count));
					count--;
				} else {
					serverList2.add(serverList1.get(count));
					serverList2.add(serverList1.get(count + 1));
					count -= 2;
				}
			} else {
				if (netVertexNum < 600) {
					count--;
				} else {
					count -= 2;
				}
				int fee = out.get(1);
				// 计算总费用
				if (fee > graph.getMaxCount() * Graph.serverCost) {
					break;
				}
				if (fee < minFee) {
					System.out.println(fee);
					minFee = fee;
					bestStart = serverList3;
					countMin = 0;
				} else {
					countMin++;
				}
				out.remove(out.size() - 1);
			}
			if (countMin > 5) {
				break;
			}
		}
		Set<Integer> fordid = new HashSet<Integer>();
		Set<Integer> mustid = new HashSet<Integer>();
		for (int i = 0; i < consumerVertexList.size(); i++) {
			ConsumerVertex temp = consumerVertexList.get(i);
			if (temp.demand < fordDemand) {
				// nodeList.remove(temp.connectedVertex);
				fordid.add(temp.connectedVertex);
			} else if (temp.demand > mustDemand) {
				mustid.add(temp.connectedVertex);
			} else {
				;
			}
		}
		// List<Integer> lastn = graph.getLastN((int) (netVertexNum * 0.1));
		// Set<Integer> forbid = new HashSet<Integer>(lastn);
		Individual.setGraph(graph);
		// Individual.setForbid(forbid);
		Individual.setForbid(fordid);
		Individual.setMustid(mustid);
		int maxFee = (Graph.consumerList.size()) * Graph.serverCost;
		Individual i1 = new Individual(netVertexNum, bestStart, (int) (maxFee - minFee), 1);
		// Individual i1 = new Individual(netVertexNum, bestStart, 0);
		Population p = new Population(i1, 20, 0.75, 1.0 / netVertexNum * 2);

		// count = 50000;
		// int same = 0;
		int x = p.elite.fitness;
		// while(count > 0 && !p.allTheSame() && flag) {
		while (flag) {
			p.select();
			if (!flag) {
				break;
			}
			// p.cross();
			// if(!flag) {
			// break;
			// }
			p.mutate(flagNum, geshu);
			if (p.elite.fitness <= x) {
				// same++;
			} else {
				// same = 0;
				x = p.elite.fitness;
			}
			count--;
		}

		bestStart.clear();

		for (int i = 0; i < p.elite.DNA.length; i++) {
			if (p.elite.DNA[i]) {
				bestStart.add(i);
			}
		}

		timer.cancel();
		Graph clone = new Graph(netVertexNum, edgeList);
		clone.addServerList(bestStart);
		List<List<Integer>> bestout = getOutput(clone, clone.getDemand());
		minFee = bestout.get(bestout.size() - 1).get(1);
		bestout.remove(bestout.size() - 1);
		System.out.println("minFee: " + minFee);
		System.out.println("Population: " + p.generationCount);
		return getFinalStringArray(bestout, map); // 最终返回字符串数组
		// return null;
	}

	public static List<Integer> SPFA(Graph graph, int start, int end) {
		Vertex[] vertexList = new Vertex[graph.getVertexNum()];
		for (int i = 0; i < graph.getVertexNum(); i++) {
			vertexList[i] = new Vertex(i, false, INFINITY, -1);
		}
		vertexList[start].dv = 0;
		List<Integer> queue = new LinkedList<Integer>();
		queue.add(start);
		int queueSum = 0;
		while (queue.size() != 0) {
			int now = queue.get(0);
			Vertex out = vertexList[now];
			int ave = queueSum / queue.size();
			while (out.dv > ave) {
				queue.remove(0);
				queue.add(now);
				now = queue.get(0);
				out = vertexList[now];
			}
			queueSum -= out.dv;
			queue.remove(0);// Large Lable Last
			out.known = false;
			Map<Integer, int[]> hashMap = graph.struct[now];
			for (Integer key : hashMap.keySet()) {
				Vertex v = vertexList[key];
				int[] line = hashMap.get(key);
				if (out.dv + line[1] < v.dv) {
					v.dv = out.dv + line[1];
					v.pv = now;
					if (!v.known) {
						v.known = true;
						queueSum += v.dv;
						if (queue.size() != 0 && vertexList[queue.get(0)].dv > v.dv) {
							queue.add(0, key);// SLF: Small Label First
						} else {
							queue.add(key);
						}
					}
				}
			}
		}
		List<Integer> resultList = new ArrayList<Integer>();
		int thisVertexId = vertexList[end].pv;
		while (thisVertexId != -1) {
			resultList.add(thisVertexId);
			thisVertexId = vertexList[thisVertexId].pv;
		}
		if (resultList.size() < 2) {
			return null;
		}
		Collections.reverse(resultList);
		resultList.add(end);
		return resultList;
	}

	// public static List<Integer> SPFA(Graph graph, int start, int end) {
	// Vertex [] vertexList = new Vertex [graph.getVertexNum()];
	// for(int i = 0; i < graph.getVertexNum(); i++) {
	// vertexList[i] = new Vertex(i, false, INFINITY, -1);
	// }
	// vertexList[start].dv = 0;
	// List<Integer> queue = new ArrayList<Integer>();
	// queue.add(start);
	// while(queue.size() != 0) {
	// int now = queue.get(0);
	// Vertex out = vertexList[now];
	// queue.remove(0);
	// out.known = false;
	// Map<Integer, int[]> hashMap = graph.struct[now];
	// for(Integer key : hashMap.keySet()) {
	// Vertex v = vertexList[key];
	// int [] line = hashMap.get(key);
	// if(out.dv + line[1] < v.dv) {
	// v.dv = out.dv + line[1];
	// v.pv = now;
	// if(!v.known) {
	// v.known = true;
	// if(queue.size() != 0 && vertexList[queue.get(0)].dv > v.dv) {
	// queue.add(0, key);//SLF: Small Label First
	// } else {
	// queue.add(key);
	// }
	// }
	// }
	// }
	// }
	// List<Integer> resultList = new ArrayList<Integer>();
	// int thisVertexId = vertexList[end].pv;
	// while(thisVertexId != -1) {
	// resultList.add(thisVertexId);
	// thisVertexId = vertexList[thisVertexId].pv;
	// }
	// Collections.reverse(resultList);
	// if(resultList.size() < 2) {
	// return null;
	// }
	// resultList.add(end);
	// return resultList;
	// }

	public static List<Edge> maxFlowMinFee(Graph graph, int start, int end) {
		/**
		 * 最大流最小路径算法，具体实现过程我自己都看不懂了
		 * 
		 * @param graph
		 *            图
		 * @param start
		 *            起点
		 * @param end
		 *            终点
		 * @param demand
		 *            总需求
		 */
		int presentFlow = 0;
		int theta = INFINITY;
		int demand = graph.getDemand();
		int sumFee = 0;
		// List<Integer> output = dijkstra(graph, start, end);
		List<Integer> output = SPFA(graph, start, end);
		List<Edge> path = new ArrayList<Edge>();
		if (output == null) {
			return null;
		}
		while (output != null && presentFlow < demand) {
			int wholePrice = 0;
			for (int i = 0; i < output.size() - 1; i++) {
				int startPoint = output.get(i);
				int endPoint = output.get(i + 1);
				int[] thisEdge = graph.struct[startPoint].get(endPoint);
				int band = thisEdge[0];
				wholePrice += thisEdge[1];
				if (band < theta) {
					theta = band;// 计算路径上的最小带宽
				}
			}
			if (theta + presentFlow < demand) {
				presentFlow += theta;
			} else {
				theta = demand - presentFlow;
				presentFlow = demand;
			}
			sumFee += theta * wholePrice;
			for (int i = 0; i < output.size() - 1; i++) {
				int startPoint = output.get(i);
				int endPoint = output.get(i + 1);
				int[] thisEdge = graph.struct[startPoint].get(endPoint);
				int band = thisEdge[0];

				path.add(new Edge(startPoint, endPoint, theta));
				if (band == theta) {
					graph.struct[startPoint].remove(endPoint);
					if (i != 0 && i != output.size() - 2) {// point1
						thisEdge[1] = -thisEdge[1];
						graph.struct[endPoint].put(startPoint, new int[] { theta, thisEdge[1] });
					}
				} else {
					thisEdge[0] -= theta;
				}
			}
			theta = INFINITY;
			output = SPFA(graph, start, end);
			if (output == null && presentFlow < demand) {
				System.out.println("Fail");
				return null;
			}
		}
		sumFee += graph.struct[graph.struct.length - 2].size() * Graph.serverCost;
		path.add(new Edge(sumFee, 0, 0));
		return path;

	}

	public static List<Integer> getOutput(Graph graph, int demand, int num) {
		/**
		 * 使用最大流最小费用算法获取输出,不输出路径
		 * 
		 * @param graph
		 *            当前的图
		 * @param demand
		 *            总需求
		 * @return 链路数据 该参数类似一个二维数组，每一行表示一条路径，第一个数一定是虚拟总服务器，倒数第二个数一定是虚拟消费节点，
		 *         最后一个数为链路带宽
		 */
		int netVertexNum = graph.getVertexNum();
		List<Edge> path = maxFlowMinFee(graph, netVertexNum - 2, netVertexNum - 1);
		int fee = 0;
		if (path == null) {
			return null;
		}
		fee = path.get(path.size() - 1).startPoint;
		path.remove(path.size() - 1);
		List<Integer> out = new ArrayList<Integer>();
		out.add(0); // 0表示正确输出
		out.add(fee);
		return out;
	}

	public static List<List<Integer>> getOutput(Graph graph, int demand) {
		/**
		 * 使用最大流最小费用算法获取输出
		 * 
		 * @param graph
		 *            当前的图
		 * @param demand
		 *            总需求
		 * @return 链路数据 该参数类似一个二维数组，每一行表示一条路径，第一个数一定是虚拟总服务器，倒数第二个数一定是虚拟消费节点，
		 *         最后一个数为链路带宽
		 */
		int netVertexNum = graph.getVertexNum();
		List<Edge> path = maxFlowMinFee(graph, netVertexNum - 2, netVertexNum - 1);
		int fee = 0;
		if (path == null) {
			// List<List<Integer>> warning = new ArrayList<List<Integer>>();
			// List<Integer> t = new ArrayList<Integer>();
			// t.add(-1);//最后一行为-1，代表异常
			// warning.add(t);
			// return warning;
			return null;
		}
		fee = path.get(path.size() - 1).startPoint;
		path.remove(path.size() - 1);
		List<List<Integer>> out = new SimpleGraph(path).getAllPath(netVertexNum - 2, netVertexNum - 1);
		List<Integer> s = new ArrayList<Integer>();
		s.add(0); // 0表示正确输出
		s.add(fee);
		out.add(s);
		return out;
	}

	public static String[] getFinalStringArray(List<List<Integer>> out, Map<Integer, Integer> map) {
		/**
		 * 转换成最后写入文件的结果
		 * 
		 * @out 输出结果，待转换
		 * @map 网络节点到消费节点的转换表
		 */
		int lineCount = out.size();
		String[] output = new String[lineCount + 2];
		output[0] = String.valueOf(lineCount);// 第一行链路数目
		output[1] = "";// 第二行为空行
		for (int i = 0; i < lineCount; i++) {// 网络节点到消费节点 带宽
			List<Integer> line = out.get(i);
			StringBuffer s = new StringBuffer();
			for (int j = 1; j < line.size() - 2; j++) {
				int t = line.get(j);
				s.append(String.valueOf(t)).append(" ");
			}
			int c = map.get(line.get(line.size() - 3));
			s.append(String.valueOf(c)).append(" ");
			s.append(line.get(line.size() - 1));
			output[i + 2] = new String(s);
		}
		return output;
	}

	// public static boolean judge(Graph g, List<List<Integer>> out) {
	// Graph graph = g.myclone();
	// for(List<Integer> oneline : out) {
	// int band = oneline.get(oneline.size() - 1);
	// for(int i = 0; i < oneline.size() - 2; i++) {
	// int start = oneline.get(i);
	// int end = oneline.get(i+1);
	// if(!graph.struct[start].containsKey(end)) {
	// System.out.println("Band Error!");
	// return false;
	// }
	// int[] e = graph.struct[start].get(end);
	// if(e[0] < band) {
	// System.out.println("Band Error!");
	// return false;
	// }
	// e[0] -= band;
	// if(e[0] == 0) {
	// graph.struct[start].remove(end);
	// }
	// }
	// }
	// for(ConsumerVertex c : Graph.consumerList) {
	// int v = c.connectedVertex;
	// if(graph.struct[v].containsKey(graph.getVertexNum() - 1)) {
	// System.out.println("Consumer Vertex Error!");
	// return false;
	// }
	// }
	// return true;
	// }

	public static void simulatedAnnealing(Graph graph) {
		List<Integer> serverList = graph.getTopServer(graph.getMaxCount());
		int netVertexNum = graph.getVertexNum();
		List<Integer> lastn = graph.getLastN((int) (graph.getVertexNum() * 0.2));
		Set<Integer> forbid = new HashSet<Integer>(lastn);
		Individual.setGraph(graph);
		Individual.setForbid(forbid);

		serverList.remove(serverList.size() - 1);
		Individual i1 = new Individual(netVertexNum - 2, serverList, 0, 0.001);
		i1.refresh();

		Individual oldOne = i1.myclone();
		double T = i1.fitness;
		double Ts = 0.99;
		int count = 1;
		while (count < 200000) {
			Individual i = i1.myclone();
			i1.change();
			int fw = i1.fitness;
			if (fw == 0) {
				System.out.println("bad!");
				i1 = i;
				continue;
			}
			if (fw > oldOne.fitness) {
				oldOne = i1.myclone();
			} else {
				i1 = oldOne.myclone();
				// double pro = Math.random();
				// double p = Math.exp((fw - oldOne.fitness) / T);
				// if(p > pro) {
				// oldOne = i1.myclone();
				// } else {
				// i1 = oldOne.myclone();
				// }
			}
			T = T * Ts;
			count++;
			// System.out.println(count);
		}
		System.out.println("Min fee:" + (graph.getMaxCount() * Graph.serverCost - oldOne.fitness));
	}

	public static double T(int t) {
		int t0 = 1000;
		int a = 1;
		return t0 / Math.log(1 + a * t);
	}

}

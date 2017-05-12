package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

class SimpleGraph {
	/**
	 * 表示一个简单的图，其实该类应该和Graph类差不多，可以考虑用Graph继承此类
	 * @param INFINITY 静态变量表示无穷大
	 * @param struct 用邻接表表示图结构，形式为键值对，键为节点的标号，值同样是键值对的形式，键表示该点延伸的节点，值直接是两个点之间的边
	 * @param pointMap 表示图中的节点，键为节点编号，值为节点本身
	 */
	
	private static final int INFINITY = Integer.MAX_VALUE;
	Map<Integer, Map<Integer, Integer>> struct;

	public SimpleGraph(List<Edge> path) {
		/**
		 * 构造函数
		 * @param path 路径构图
		 */
		this.struct = new HashMap<Integer, Map<Integer, Integer>>();//初始化struct
		for(int i = 0; i < path.size(); i++) {
			Edge present = path.get(i);//取出单条路径
			int start = present.startPoint;
			int end = present.endPoint;
			int band = present.band;
			if(!struct.containsKey(start)) {//初始化起点
				struct.put(start, new HashMap<Integer, Integer>());
			}
			
			if(!struct.containsKey(end)) {//初始化终点
				struct.put(end, new HashMap<Integer, Integer>());
			}
			if(!struct.get(start).containsKey(end) && !struct.get(end).containsKey(start)) {
				struct.get(start).put(end, band);//构图
			} else if(struct.get(start).containsKey(end)) {
				int b = struct.get(start).get(end);
				struct.get(start).put(end, b + band);//同为正向
			} else if(struct.get(end).containsKey(start)) {
				//反向冲突
				int b = struct.get(end).get(start);
				if(b > band) {
					struct.get(end).put(start, b - band);
				} else if(b < band) {
					struct.get(start).put(end, band - b);
					struct.get(end).remove(start);
				} else {
					struct.get(end).remove(start);
				}
			}
		}
	}
	
	public List<Integer> bfs(int start, int end) {
		/**
		 * 使用广度优先遍历法获取最短路径
		 * @param start 起点
		 * @param end 终点
		 * @return 路径编号，从起点至终点
		 */
		Map<Integer, int []> pointMap = new HashMap<Integer, int []>();
		List<Integer> output = new ArrayList<Integer>();//初始化输出
		Queue<Integer> queue = new LinkedList<Integer>();//初始化队列
		for(Integer key: struct.keySet()) {
			pointMap.put(key, new int[] {INFINITY, -1});//初始化所有节点的参数。-1表示该节点目前没有前驱
		}
		
		pointMap.get(start)[0] = 0;//起点的距离初始化为0
		queue.offer(start);//起点加入队列
		//队列非空或者终点未到达时进行迭代
		while(!queue.isEmpty() && pointMap.get(end)[0] == INFINITY) {
			Integer p = queue.poll();	//队首出队
			Map<Integer, Integer> linkedPoint = struct.get(p);//获取该节点的邻接表
			for(Integer key: linkedPoint.keySet()) {
				if(pointMap.get(key)[0] == INFINITY) {//若该节点未知将该点入队
					pointMap.get(key)[0] = pointMap.get(p)[0] + 1;//距离自增1
					pointMap.get(key)[1] = p;//标记前驱
					queue.offer(key);//入队
				}
			}
		}
		int pv = end;
		while(pv != -1) { //将路径反向导入output,起点的pv为-1,当搜索到-1时表示结束
			output.add(pv);
			pv = pointMap.get(pv)[1];
		}
		if(output.size() == 1) {//结果只有终点，表示起点与终点不存在路径，返回null
			return null;
		}
		Collections.reverse(output);//反向变为正向
		return output;
	}
	
	public List<List<Integer>> getAllPath(int start, int end) {
		/**
		 * 一对一获取所有路径，针对最大流最小费用算法输出符合官方格式的结果
		 * @param start 起点
		 * @param end 终点
		 * @return 多条路径，类似二维数组，每一行表示一条路径
		 */
		List<List<Integer>> output = new ArrayList<List<Integer>>();
		while(true) {//未达到需求时进行迭代
			List<Integer> path = bfs(start, end);//广度优先遍历搜索路径
			if(path == null) {
				break;//无路径表示出现异常，直接退出输出null		
			}
			int band = INFINITY;//消耗带宽取路径上的最小带宽
			for(int i = 0; i < path.size() - 1; i++) {
				int one = path.get(i);
				int two = path.get(i + 1);
				if(band > struct.get(one).get(two)) {
					band = struct.get(one).get(two);
				}
			}

			for(int i = 0; i < path.size() - 1; i++) {//根据消耗的带宽更新图，以便于下一次迭代
				int one = path.get(i);
				int two = path.get(i + 1);
				int b = struct.get(one).get(two);
				if(b == band) {//若路径上的带宽消耗完直接删除
					struct.get(one).remove(two);
				} else {
					struct.get(one).put(two, b - band);
				}
			}
			path.add(band);
			output.add(path);
		}
		return output;
	}
}

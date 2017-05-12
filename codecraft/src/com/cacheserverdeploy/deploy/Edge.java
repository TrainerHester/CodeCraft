package com.cacheserverdeploy.deploy;

class Edge {
	/**
	 * @param startPoint 起点
	 * @param endPoint 终点
	 * @param price 价格
	 * @param band 总带宽
	 */
	public int startPoint;
	public int endPoint;
	public int price;
	public int band;
	
	public Edge(int start, int end, int band) {
		this.startPoint = start;
		this.endPoint = end;
		this.price = 0;
		this.band = band;
	}

	public Edge(int id, String parameters) {
		/**
		 * Edge的另一个构造函数
		 * @param id Edge的编号
		 * @param parameters 根据实例文件输入一行，各参数用空格分割，分别是起点、终点、带宽、单价
		 */
		String [] forInput = parameters.split(" ");
		this.startPoint = Integer.parseInt(forInput[0]);
		this.endPoint = Integer.parseInt(forInput[1]);
		this.band = Integer.parseInt(forInput[2]);
		this.price = Integer.parseInt(forInput[3]);
	}
	
}

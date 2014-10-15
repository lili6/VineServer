package vine.core.utils;

import java.util.Random;

public class RandomUtil {
	public static Random random = new Random(); 
	
	/**
	 * getRandom
	 * @return
	 */
	public static Random getRandom(){
		return random;
	}
	
	/** 检测几率 */
	public static boolean checkRate(int rate){
		if(rate <= 0) return false;
		if(rate > 100) rate = 100;
		return random.nextInt(101) < rate ? true : false;
	}
	
	/** 检测几率 */
	public static boolean checkRate(int max, int rate){
		if(max <= 0 || rate <= 0) return false;
		if(rate > max) rate = max;
		return random.nextInt(max + 1) < rate ? true : false;
	}
	
	/**
	 * 返回[0,num]区间的随机数，结果值包含0和num
	 * @param num
	 * @return
	 */
	public static int nextInt(int num){
		if(num < 0) return 0;
		return random.nextInt(num + 1);
	}
	
	/**
	 * 返回[min,max]区间的随机数，结果值包含min和max
	 * if(a < 0 || b < 0) return 0. if(a == b) return a;
	 * @param a
	 * @param b
	 * @return
	 */
	public static int nextInt(int a, int b){
		if(a < 0 || b < 0) return 0;
		if(a == b) return a;
		int min = a <= b ? a : b;
		int max = a >= b ? a : b;
		int temp = max - min;
		int r = random.nextInt(temp + 1);
		return min + r;
	}
	
	/**
	 * 提供几个几率值，按几率抽签，抽到后返回该几率所处参数位置的索引
	 * @param ratios 一些几率值
	 * @return 几率所处参数位置的索引，返回－1则表示抽取失败
	 */
	public static int drawByRatio(int... ratios) {
		int sum = 0;
		for (int ratio : ratios) {
			sum += ratio;
		}
		int index = -1;
		int[] ratioIndexs = new int[sum];
		int currIndex = 0;
		for (int i = 0; i < ratios.length; i++) {
			for (int j = 0; j < ratios[i]; j++) {
				ratioIndexs[currIndex] = i;
				currIndex++;
			}
		}
		index = ratioIndexs[RandomUtil.nextInt(sum - 1)];
		return index;
	}
}

package vine.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {
	
	/**
	 * 计算百分数
	 * @param value
	 * @param per
	 * @return
	 */
	public static int calcPercentage(int value, int per){
		return value * per * 100 / 10000;
	}
	
	/**
	 * 计算价格
	 * @param value 默认价格
	 * @param per 百分比
	 * @param tag 0-不处理 1-四舍五入 2-向上取整 3-向下取整
	 * @return
	 */
	public static int calcPrice(int value, int per, int tag){
		if(value <= 0 || per <= 0) return 0;
		int temp = (int) Math.round(value * per * 1.0 / 100);
		return temp;
	}
	
	/**
	 * 计算百分比
	 * @param hasNumber  当前值
	 * @param needNumber 总值
	 * @return 例：40.11，表示40.11%
	 */
	public static float computePercent(long hasNumber, long needNumber) {
		if (needNumber == 0) {
			return 0;
		}
		float progress = new BigDecimal(100).divide(new BigDecimal(needNumber), 2, RoundingMode.HALF_EVEN)
				.multiply(new BigDecimal(hasNumber)).floatValue();
		return progress;
	}
}

import java.util.Random;

/**
 * 主函数：四个简单排序方法，冒泡、选择、插入、快速
 * @author heym
 * @time 2018/05/26
 * 
 */


public class MainFun {
	
	private static int[] arrays;

	/**
	 * 生成长度为n的数组
	 */
	public static void setRandomArrays(int n) {
		arrays = new int[n];
		Random random = new Random();
		for(int i = 0; i < n; i++) {
			arrays[i] =random.nextInt(100000);
		}
	}
	
	/**
	 * 打印原数组
	 */
	public static void printFun() {
		System.out.println("原数组:");
		for(int array:arrays) {
			System.out.print(array+" ");
		}
		System.out.println();
	}
	
	/**
	 * 四种排序并简单打印
	 * @param n 数组长度
	 */
	public static void simpleSortPrint(int n) {
		//数组生成
		setRandomArrays(n);
		printFun();
		
		//排序对象生成
		Sort sort = new Sort();

		//冒泡排序
		sort.setArrays(arrays);
		sort.bubbleSort();
		sort.printFun("bubbleSort");

		//选择排序
		sort.setArrays(arrays);
		sort.selectionSort();
		sort.printFun("selectionSort");
		
		//插入排序
		sort.setArrays(arrays);
		sort.insertSort();
		sort.printFun("insertSort");
		
		//快速排序
		sort.setArrays(arrays);
		sort.quickSort(0, arrays.length-1);
		sort.printFun("quickSort");
				
	}

	
	/**
	 * 计算排序耗时
	 * @param n 数组长度
	 * @param m 统计次数
	 * @return timeArrays 耗时数组
	 */
	public static void sortTimeAnalys(int n,int m) {
		String[] sortNameArrays = {"bubbleSort","selectionSort","insertSort","quickSort"};
		float[][] timeArrays = new float[sortNameArrays.length][m];
		long startTime;
		long endTime;
		
		//排序对象生成
		Sort sort = new Sort();
		for(int i = 0; i < m; i++) {
			//数组生成
			setRandomArrays(n);
			//循环计算耗时
			for(int j= 0;j < sortNameArrays.length; j++) {
				startTime = System.currentTimeMillis();
				//根据参数调用方法
				sort.toSort(sortNameArrays[j], arrays);
				endTime = System.currentTimeMillis();
				//单位 ：s
				timeArrays[j][i] = (float)(endTime- startTime)/1000;
			}
			
		}
		
		//计算平均值和方差
		float[] MeanTimeArrays = new float[sortNameArrays.length];
		float[] varianceTimeArrays = new float[sortNameArrays.length];
		for(int i = 0; i< sortNameArrays.length; i++) {
			MeanTimeArrays[i] = getMean(timeArrays[i]);
			varianceTimeArrays[i] = getVariance(timeArrays[i]);
		}
		//均值和方差打印
		for(int i = 0; i < sortNameArrays.length; i++) {
			System.out.printf("%-13s 排序， 平均耗时： %8.8f, 方差： %8.8f\n",sortNameArrays[i] 
						,MeanTimeArrays[i] 
							,varianceTimeArrays[i]);
		}
	}
	
    /**
     * 求方差
     * @param arrarys
     * @return
     */
    public static float getVariance(float[] arrarys) {
    	float variance = 0;
    	float sum = 0, sum2 = 0;
        for (int i = 0; i < arrarys.length; i++) {
            sum += arrarys[i];
            sum2 += arrarys[i] * arrarys[i];
        }
        variance = sum2 / arrarys.length - (sum / arrarys.length) * (sum / arrarys.length);
        return variance;
    }
    
    /**
     * 求和
     *
     * @param arrarys
     * @return
     */
    public static float getSum(float[] arrarys) {
    	float sum = 0;
        for (float num : arrarys) {
            sum += num;
        }
        return sum;
    }
    
    /**
     * 求均值
     * @param arrarys
     * @return
     */
    public static float getMean(float[] arrarys) {
        return getSum(arrarys) / arrarys.length;
    }
	
	public static void main(String[] args) {
		/*简单排序打印*/
		simpleSortPrint(10);
		
		/*耗时简单分析*/		
		sortTimeAnalys(1000,10000);

	}
	
	

}

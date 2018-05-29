import java.util.Random;

/**
 * 主函数：几个排序方法
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
			arrays[i] =random.nextInt(1000);
		}
	}
	
	/**
	 * 打印方法
	 */
	public static void printFun() {
		System.out.println("原数组:");
		for(int array:arrays) {
			System.out.print(array+" ");
		}
		System.out.println();
	}
	
	
	public static void main(String[] args) {
		
		//数组生成
		setRandomArrays(15);
		printFun();
		
		//排序对象生成
		Sort sort = new Sort();

		//冒泡排序
		sort.setArrays(arrays);
		sort.bubbleSort();
		sort.printFun();

		//选择排序
		sort.setArrays(arrays);
		sort.selectionSort();
		sort.printFun();
		

	}
	

}


/**
 * 排序类
 * @author heym
 * @time 2018/05/26
 */

public class Sort {
	
	/*存储数组*/
	private int[] arrays;
	
	public int[] getArrays() {
		return arrays;
	}

	/**
	 * 重写set方法
	 */
	public void setArrays(int[] arrays) {
		this.arrays = new int[arrays.length];
		for(int i = 0; i < arrays.length; i++) {
			this.arrays[i] = arrays[i];
		}
	}

	public void printFun() {
		for(int array:this.arrays) {
			System.out.print(array+" ");
		}
		System.out.println();
	}
	
	/**
	 * 冒泡排序
	 * @return arrays
	 */
	public int[] bubbleSort() {
		int tmp;
		for(int j=arrays.length;j > 1;j--) {
			//优化：置换标识，未发生则跳出循环
			int isChange = 0;
			//轮次
			for(int i=0;i<j-1;i++) {
				if(arrays[i] > arrays[i+1]) {//稳定的排序
					tmp = arrays[i];
					arrays[i] = arrays[i+1];
					arrays[i+1] = tmp;
					isChange = 1;
				}
			}
			if(isChange == 0) {
				break;
			}
		}
		System.out.println("冒泡排序完成：");
		return this.arrays;
	}
	
	/**
	 * 选择排序(非稳定）
	 * @return arrays
	 */
	public int[] selectionSort() {
		
		int maxValue;
		int index;
		int tmp;
		
		for(int j = this.arrays.length-1; j > 0; j--) {
			maxValue = this.arrays[0];
			index = 0;
			for(int i = 0; i <= j; i++) {
				if(maxValue < this.arrays[i]) {
					maxValue = this.arrays[i];
					index = i;
				}
			}
			//System.out.println(max+" "+index);
			tmp = this.arrays[j];
			this.arrays[j] = maxValue;
			this.arrays[index] = tmp;
			
		}
		System.out.println("选择排序完成：");
		return this.arrays;
	}
	
	
	
}

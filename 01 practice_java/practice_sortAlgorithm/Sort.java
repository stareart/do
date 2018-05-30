
/**
 * 简单排序类：冒泡、选择、插入、快速
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

	/**
	 * 打印
	 * @param sortName
	 */
	public void printFun(String sortName) {
		switch(sortName) {
		case "bubbleSort":
			System.out.println("冒泡排序完成：");
			break;
		case "selectionSort":
			System.out.println("选择排序完成：");
			break;
		case "insertSort":
			System.out.println("插入排序完成：");
			break;
		case "quickSort":
			System.out.println("快速排序完成：");
			break;
		default:
			System.out.println("未知排序：");
			break;
		}
		for(int array:this.arrays) {
			System.out.print(array+" ");
		}
		System.out.println();
	}
	
	/**
	 * 根据输入参数进行排序
	 * @param sortName
	 */
	public void toSort(String sortName,int[] arrays) {
		setArrays(arrays);
		switch(sortName) {
		case "bubbleSort":
			bubbleSort();
			break;
		case "selectionSort":
			selectionSort();
			break;
		case "insertSort":
			insertSort();
			break;
		case "quickSort":
			quickSort(0, arrays.length-1);
			break;
		default:
			System.out.println("未知排序：");
			break;
		}
		
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
		
		return this.arrays;
	}
	
	
	/**
	 * 插入排序
	 * @return arrays
	 */
	public int[] insertSort() {
		for(int i = 1; i < this.arrays.length; i++) {
			int tmp = this.arrays[i];
			int j = i - 1;
			while(j >= 0 && tmp < this.arrays[j]) {
				this.arrays[j+1] = this.arrays[j];
				j--;
			}
			this.arrays[j+1] = tmp;
		}
		
		return this.arrays;
	}
	
	
	/**
	 * 快速排序
	 * @param L、R
	 * @return arrays
	 */
	public int[] quickSort(int L, int R) {
		
		int i=L;
		int j=R;
		int base = this.arrays[(L+R)/2];
		
		//保证左边比中间值小、右边比中间值大
		while(i <= j) {
			
			//找到左边需要交换的值的位置
			while(base > this.arrays[i])
				i++;
			//找到右边需要交换的值的位置
			while(base < this.arrays[j])
				j--;
			//交换
			if(i <= j) {
				int tmp = this.arrays[i];
				this.arrays[i] = this.arrays[j];
				this.arrays[j] = tmp;
				i++;
				j--;
			}
		}
		//左边接着排，直到只有一个值
		if(L < j)
			quickSort(L, j);

		//右边接着排，直到只有一个值
		if(R > i)
			quickSort(i, R);

		return this.arrays;
	}
	
}

import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * heym 2018/06/07
 * 
 * 1、从1到31中任选三个不重复数的进行组合，求所有组合
 * 2、计算组合的平均值，并求出平均值出现的次数，以及出现概率。 例如， 1,2,3， 平均值为 2， 出现1次， 出现概率 = 1 / 所有组合数目。 
 * 3、输出 csv格式数据：平均值，出现次数，所占百分比，例如   2 ， 1， %  
 * 
 * 知识点：1、List  2、Map 3、输出文件
*/
public class CombinationImpl {

	private static int min = 1;
	private static int max = 31;

	/**
	 * 1、找出组合
	 * 从min到max中任选三个不重复数的进行组合，求所有组合
	 * @return  combinationList 组合列表 
	 */
	public static List<List<Integer>> setCombinationList() {
		List<List<Integer>> combinationList = new ArrayList<List<Integer>>();
		//组合
		for(int i = min; i <= max ; i++) {
			for(int j = i+1; j <= max ; j++) {
				if(i==j)  continue;
				for(int k = j+1; k <= max ; k++) {
					if(k==i || k==j)  continue;
					List<Integer> combination = new ArrayList<Integer>();
					combination.add(i);
					combination.add(j);
					combination.add(k);
					combinationList.add(combination);
				}
			}
		}
/*		for(int i =0;i<combinationList.size();i++) {
			System.out.println(combinationList.get(i)+""+combinationList.size());
		}*/
		return combinationList;
	}
	
	/**
	 * 2、求平均值、次数(概率)
	 * 计算组合的平均值，并求出平均值出现的次数，以及出现概率。 例如， 1,2,3， 平均值为 2， 出现1次， 出现概率 = 1 / 所有组合数目。
	 * @param args
	 */
	public static Map<Float,Integer> listStatics(List<List<Integer>> combinationList) {

		Map<Float,Integer> avgMap =new TreeMap<Float,Integer>();
		
		for(int i =0;i<combinationList.size();i++) {
			float avg = ((combinationList.get(i).get(0)+combinationList.get(i).get(1)+combinationList.get(i).get(2))/(float)3);
			//若平均值不存在，赋键值为1；若存在，键值加1
			if(avgMap.get(avg) == null) 
				avgMap.put(avg, 1);
			else {
				int tmp = avgMap.get(avg);
				avgMap.remove(avg);
				avgMap.put(avg, tmp + 1);
			}
		}
/*		for(float avg:avgMap.keySet()){
			System.out.println(avg);
		}*/
		return avgMap;
	}
	
	/**
	 * 输出为csv文件
	 * 3、输出 csv格式数据：平均值，出现次数，所占百分比，例如   2 、 1 %  
	 */
	public static void outputCSV(Map<Float,Integer> avgMap)  throws IOException, IllegalArgumentException, IllegalAccessException{
		File file = new File("d:\\conbination.csv");
		//构建输出流，同时指定编码
		OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
	    
		//写头文件，写完后换行
	    String[] titles = {"平均值","出现次数","所占百分比"};
	    for(String title : titles){
	      ow.write(title);
	      ow.write(",");
	    }
	    ow.write("\r\n");
	    
	    //写主体数据
		float sumTimes = 0;
	    for(float times:avgMap.values())
	    	sumTimes += times;
	    for(Float avg:avgMap.keySet()) {
	    	ow.write(avg+"");
	    	ow.write(",");
	    	ow.write(avgMap.get(avg)+"");
	    	ow.write(",");
	    	ow.write((avgMap.get(avg)/(float)sumTimes)*100+"%");
	    	ow.write("\r\n");
	    }
	    ow.flush();
	    ow.close();
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException {

		outputCSV(listStatics(setCombinationList()));
	}

}

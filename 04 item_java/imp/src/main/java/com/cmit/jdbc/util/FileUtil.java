package com.cmit.jdbc.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
* function：复制目录、扫描文件夹
* author：heym
* create time：2018-12-07
*/
public class FileUtil {
	
	/**复制文件夹入口
	 * @param sourceDir
	 * @param targetDir
	 */
	public static void copySourceDir(String sourceDir, String targetDir) {
		
		List<File> files = traverseFileTree(new File(sourceDir));
		
		if(files == null) {
			return ;
		}
		
		for(File f : files) {
			makeDir(f, targetDir, sourceDir);
		}
	}
	/**遍历获取源文件file目录及子目录
	 * @param file
	 * @return
	 */
	public static List<File> traverseFileTree(File file) {
		List<File> files = new ArrayList<File>();
		
		if(!file.isDirectory()) {
			return null;
		}
		
		files.add(file);
		
		File[] subFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(new File(dir, name).isDirectory()) {
					return true;
				}
				return false;
			}
		});
		
		if(subFiles == null || subFiles.length == 0) {
			return files;
		}
		
		for(File f : subFiles) {
			files.addAll(traverseFileTree(f));
		}
		return files;
	}
	
	/**创建与源文件目录对应的success|error目录
	 * @param targetDir
	 */
	public static void makeDir(File sourceFile, String targetDir, String sourceDir) {
		
		String absolutePath = sourceFile.getAbsolutePath();
		String subDir = absolutePath.replace(sourceDir, "");
		
		File file = new File(targetDir + File.separator + "BAK" + File.separator + subDir);
		if(!file.exists()) {
			file.mkdirs();
		}
		file = new File(targetDir + File.separator + "ERROR" + File.separator + subDir);
		if(!file.exists()) {
			file.mkdirs();
		}
		file = new File(targetDir + File.separator + "WORK" + File.separator + subDir);
		if(!file.exists()) {
			file.mkdirs();
		}
			
	}
	
	/**过滤文件
	 * @param sourceDir 源文件根目录
	 * @return
	 */
	public static List<File> fileFilter(String sourceDir,String fileNameList) {
		
		List<File> allFiles = null;
		
		File file = new File(sourceDir);
		
		// 判断file是否存在
		if(!file.exists()) {
			return null;
		}
		
		// 判断是否目录
		if(!file.isDirectory()) {
			return null;
		}
		
		File[] files = file.listFiles(new FilenameFilter() {
			
			// dir:目录| name:dir下文件夹和文件名
			@Override
			public boolean accept(File dir, String name) {
				File file = new File(dir, name);
				if(System.currentTimeMillis()-file.lastModified() < 10*1000) return false;//正在传输
				if(file.isDirectory()) {
					return true;
				} else {
					//循环匹配规则
					Pattern pattern=Pattern.compile(fileNameList);
					Matcher matcher = pattern.matcher(name);
					if(matcher.matches()) {
						return true;
					}
				}
				return false;
			}
		});
		
		
		allFiles = new ArrayList<File>();
		for(File subfile : files) {
			if(subfile.isFile()) {
				allFiles.add(subfile);
			} else if(subfile.isDirectory()) {
				allFiles.addAll(fileFilter(subfile.getAbsolutePath(),fileNameList));
			}
		}
		
		Collections.sort(allFiles, new Comparator<File>() {
			
			@Override
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath()); 
			}
		});
		
		return allFiles;
	}
	
	/**
	 * 移动文件
	 * @param file
	 * @param sourceDir
	 * @param targetDir
	 * @param flag  0：移动到WORK  1：移动到BAK 2：移动到ERROR
	 * @return
	 */
	public static File moveFile(File file, String sourceDir, String targetDir, int flag) {
		
		if(sourceDir == null) {
			return null;
		}
		if(targetDir == null) {
			return null;
		}
		String absolutePath = file.getAbsolutePath();
		File targetFile = null;
		String subDir = null;
		String workdir = "WORK";
		switch(flag) {
		case(0):
			subDir = absolutePath.substring(absolutePath.lastIndexOf(sourceDir)+sourceDir.length());
			targetFile = new File(targetDir + File.separator + workdir + File.separator + subDir );
			break;
		case(1):
			subDir = absolutePath.substring(absolutePath.lastIndexOf(workdir)+workdir.length());
			targetFile = new File(targetDir + File.separator + "BAK" + File.separator + subDir );
			break;
		case(2):
			subDir = absolutePath.substring(absolutePath.lastIndexOf(workdir)+workdir.length());
			targetFile = new File(targetDir + File.separator + "ERROR" + File.separator + subDir);
			break;
		}
		try {
			if(targetFile.exists()) {
				FileUtils.deleteQuietly(targetFile);
			}
			FileUtils.moveFile(file, targetFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetFile;
		
	}

}

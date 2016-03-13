package com.mycom.build.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static List<File> getComponetJars(File rootFolder, final String startName) {
		List<File> files = new ArrayList<File>();
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				//component jar but not test
				String name = pathname.getName();
				return name.startsWith(startName) &&  name.endsWith(".jar") && !name.endsWith("tests.jar");
			}
		};

		processFolder(rootFolder, files, fileFilter);

		return files;
	}

	private static void processFolder(File rootFolder, List<File> fileList, FileFilter fileFilter) {
		File[] files = rootFolder.listFiles(fileFilter);
		for (File file : files) {
			if (file.isFile()) {
				fileList.add(file);
			} else {
				processFolder(file, fileList, fileFilter);
			}
		}
	}
	
	public static String getRelativePath(File rootFolder, File file)
	{
		String rootPath = rootFolder.getAbsolutePath().replace("\\", "/");
		String filePath = file.getAbsolutePath().replace("\\", "/");
		String relativePath = filePath.replace(rootPath, "");
		if(relativePath.startsWith("/"))
		{
			relativePath=relativePath.substring(1);
		}
		return relativePath;
	}
}

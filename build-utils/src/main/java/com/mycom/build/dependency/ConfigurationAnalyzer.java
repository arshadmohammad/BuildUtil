package com.mycom.build.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.mycom.build.model.ConfigParamModel;
import com.mycom.build.report.ReportGenerator;
import com.mycom.build.util.FileUtil;
import com.mycom.build.util.Util;

public class ConfigurationAnalyzer {

	public static void main(String[] args) throws Exception {
		Configuration config = new PropertiesConfiguration("conf.properties");
		File baseRelease = new File(config.getString("base.release"));
		System.out.println(baseRelease.getAbsolutePath());
		File currentRelease = new File(config.getString("current.release"));
		System.out.println(currentRelease.getAbsolutePath());
		String jarsToBeAnalyzed = config.getString("jars.to.be.analysed");
		System.out.println(jarsToBeAnalyzed);
		@SuppressWarnings("unchecked")
		List<String> includePackages = config.getList("include.package");
		String baseReleaseName = config.getString("base.release.name");
		String currentReleaseName = config.getString("current.release.name");

		System.out.println(baseRelease);
		System.out.println(includePackages);

		ConfigurationAnalyzer analyzer = new ConfigurationAnalyzer();
		try {
			System.out.println(Util.getTime()+" Analysing base");
			List<ConfigParamModel> base = analyzer.analyzeConfiguration(baseRelease, jarsToBeAnalyzed, includePackages);
			System.out.println(Util.getTime()+" base analysis done");
			System.out.println(Util.getTime()+" Analysing current");
			List<ConfigParamModel> current = analyzer.analyzeConfiguration(currentRelease, jarsToBeAnalyzed,
					includePackages);
			System.out.println(Util.getTime()+" current analysis done");
			System.out.println(Util.getTime()+" Analysing base deleted");
			List<ConfigParamModel> deletedFromBase = new ArrayList<ConfigParamModel>();
			for (ConfigParamModel baseConigParam : base) {
				// If current does not contain then it is deleted from base
				if (!current.contains(baseConigParam)) {
					deletedFromBase.add(baseConigParam);
				}
			}
			System.out.println(Util.getTime()+" base deleted analysis done");
			System.out.println(Util.getTime()+" Analysing current added");
			List<ConfigParamModel> newInCurrent = new ArrayList<ConfigParamModel>();
			for (ConfigParamModel currentConfigParam : current) {
				// If base does not contain then it is new in current
				if (!base.contains(currentConfigParam)) {
					newInCurrent.add(currentConfigParam);
				}
			}
			System.out.println(Util.getTime()+" current added analysis done");
			System.out.println(Util.getTime()+" Generating report");
			ReportGenerator.writeConfigParamReport(base, current, deletedFromBase, newInCurrent, baseReleaseName,
					currentReleaseName);
			System.out.println(Util.getTime()+" Done!");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<ConfigParamModel> analyzeConfiguration(File rootFolder, String jarsToBeAnalyzed,
			List<String> includePackages) throws IOException {
		List<ConfigParamModel> configParamModels = new ArrayList<ConfigParamModel>();
		List<File> files = FileUtil.getComponetJars(rootFolder, jarsToBeAnalyzed);
		// File f = new File("D:/workspace/hadoop-common-2.7.2.jar");
		// File("D:/gitHome/BigDataSamples/bds-common/target/bds-common-1.0.0.jar");
		for (File f : files) {
			String fileRelativePath=FileUtil.getRelativePath(rootFolder, f);
			List<JavaClass> javaClasse = getJavaClasse(f, includePackages);
			for (JavaClass jc : javaClasse) {
				Field[] fields = jc.getFields();
				if (null == fields) {
					System.out.println("no field in " + jc.getClassName());
				}
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					// value null means run time constant
					if (field.isStatic() && field.isFinal() && field.getConstantValue() != null) {
						ConfigParamModel model = new ConfigParamModel();
						model.jarName = fileRelativePath;
						model.className = jc.getClassName();
						model.fieldName = field.getName();
						model.fieldValue = field.getConstantValue().toString();
						configParamModels.add(model);
					}
				}
			}
		}

		return configParamModels;

	}

	private List<JavaClass> getJavaClasse(File jarFile, List<String> includePackages) {
		List<JavaClass> javaClasses = new ArrayList<JavaClass>();
		JarFile jarFileObjs=null;
		try {
			jarFileObjs = new JarFile(jarFile);
		} catch (IOException e1) {
			System.out.println("Failed to open "+jarFile.getAbsolutePath());
			e1.printStackTrace();
			throw new RuntimeException(e1.getMessage());
			//return javaClasses;
		}
		Enumeration<JarEntry> entries = jarFileObjs.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String className = jarEntry.getName();
			if (className.endsWith(".class") && isCondidatePackage(className, includePackages)) {
				JavaClass jc = null;
				try {
					ClassParser cp = new ClassParser(jarFileObjs.getInputStream(jarEntry), className);
					jc = cp.parse();
				} catch (IOException e) {
					System.out.println("Faile to parse class " + className + " in  jar " + jarFileObjs.getName());
					e.printStackTrace();
					continue;
				}
				javaClasses.add(jc);
			} else {
				System.out.println("Skipping " + className);
			}
		}
		Util.close(jarFileObjs);
		return javaClasses;
	}

	private boolean isCondidatePackage(String className, List<String> includePackages) {
		className = className.replace("\\", "/");
		int lastIndexOf = className.lastIndexOf('/');
		if (-1 == lastIndexOf) {
			// Class with default package
			return false;
		}
		String[] packages = className.split("/");
		for (String pkg : includePackages) {
			// skip the class name
			for (int i = 0; i < packages.length - 1; i++) {
				if (packages[i].equals(pkg)) {
					return true;
				}
			}
		}
		return false;
	}

}

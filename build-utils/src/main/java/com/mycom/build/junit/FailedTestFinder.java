package com.mycom.build.junit;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FailedTestFinder {
    private File logDir;

    public FailedTestFinder(File logDir) {
        this.logDir = logDir;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[] { "D:/tmp/logs" };
        }
        FailedTestFinder failedTestFinder = new FailedTestFinder(new File(args[0]));
        List<String> failedClasses = failedTestFinder.getFailedTestClasses();
        String result = formatResult(failedClasses);
        System.out.println(result);

    }

    private static String formatResult(List<String> failedClasses) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < failedClasses.size(); i++) {
            stringBuffer.append(failedClasses.get(i));
            if (i != (failedClasses.size())) {
                stringBuffer.append(" ");
            }
        }
        return stringBuffer.toString();
    }

    private List<String> getFailedTestClasses() {
        if (!logDir.exists()) {
            System.out.println("Log dir '" + logDir.getAbsolutePath() + "' does not exist.");
        }
        List<File> testXMLFiles = getTestXMLFiles(logDir);
        List<String> failedTestClasses = new ArrayList<String>();
        for (File file : testXMLFiles) {
            if (isFailed(file)) {
                String name = getClassName(file);
                failedTestClasses.add(name);
            }
        }
        return failedTestClasses;

    }

    private String getClassName(File file) {
        String name = file.getName().replace(".xml", "");
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return name;
        } else {
            name = name.substring(lastIndexOf + 1);
        }
        return name;
    }

    private boolean isFailed(File xmlFile) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            NodeList elementsByTagName = doc.getElementsByTagName("testsuite");
            Node item = elementsByTagName.item(0);
            NamedNodeMap attributes = item.getAttributes();
            int errors = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
            if (errors != 0) {
                return true;
            }
            int failure = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());
            if (failure != 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return false;
    }

    private static List<File> getTestXMLFiles(File logs) {
        List<File> files = new ArrayList<File>();
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                String name = pathname.getName();
                return name.endsWith(".xml");
            }
        };
        processFolder(logs, files, fileFilter);
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

}

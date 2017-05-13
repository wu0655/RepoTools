package com.example;


import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Dom4j读写xml
 *
 * @author whwang
 */
public class TestDom4j {
    static HashMap<String, String> mHash = new HashMap<>();
    static String mRepoDir = null;
    static String mDirname = null;
    static String mOut = null;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "]=" + args[i]);
        }

        mRepoDir = args[0];
        if (args.length >= 2)
            mDirname = args[1];
        if (args.length >= 3)
            mOut = args[2];
        else
            mOut = "repo_cmd.sh";

        File f = new File(mRepoDir);
        String filename = null;
        try {
            filename = f.getCanonicalPath() + "/.repo/manifest.xml";
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseWithSAX(filename);
    }


    public static void parseWithSAX(String name) {
        try {
            File f = new File(name);
            if (f.exists()) {
                SAXReader xmlReader = new SAXReader();
                Document doc = xmlReader.read(f);
                Element root = doc.getRootElement();
                readNode(root, "");
            } else
                System.out.println(name + " is not exist");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("map size=" + mHash.size());
        try {
            FileWriter writer = new FileWriter(mOut);
            BufferedWriter bw = new BufferedWriter(writer);

            for (String gname : mHash.keySet()) {
                String cmd = "repo sync -cd --no-tags -j2 " + gname;
                bw.write(cmd);
                bw.newLine();
            }
            bw.flush();
            writer.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File f = new File(mOut);
        System.out.println("out = " + f.getAbsolutePath());
    }

    @SuppressWarnings("unchecked")
    public static void readNode(Element root, String prefix) {
        if (root == null) return;
        // 获取属性

        if (root.getName().equals("project")) {
            List<Attribute> attrs = root.attributes();
            String repo_name = null;
            String repo_path = null;
            if (attrs != null && attrs.size() > 0) {
                for (Attribute attr : attrs) {
                    if (attr.getName().equals("name"))
                        repo_name = attr.getValue();
                    else if (attr.getName().equals("path"))
                        repo_path = attr.getValue();
                }

                if (repo_name.isEmpty() || repo_path.isEmpty()) {
                    System.out.println("manifest.xml has error?");
                } else if ((mDirname == null) || repo_path.startsWith(mDirname)) {
                    mHash.put(repo_name, repo_path);
                }
                //System.out.println(repo_name + " = " + repo_path);
            }
        }

        // 获取他的子节点
        List<Element> childNodes = root.elements();
        prefix += "\t";
        for (Element e : childNodes) {
            readNode(e, prefix);
        }
    }
}
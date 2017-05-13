package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SortedGitSize {
    private static final boolean DEBUG = false;

    String m_path = null;

    Map<String, Long> m_map = new HashMap<String, Long>();
    List<Map.Entry<String, Long>> m_infoIds = null;

    public SortedGitSize(String path) {
        m_path = path;
    }

    public long sortGitbySize(String topDir, String fileName) {
        long totalsize = 0;

        try {
            File file = new File(fileName);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                String absPath = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    absPath = topDir + lineTxt;
                    //System.out.println(absPath);

                    File gitdir = new File(absPath);
                    if (gitdir.isDirectory() && gitdir.exists()) {
                        long size = sdl(absPath, 0);
                        if (DEBUG)
                            System.out.println(absPath + "=" + size);
                        if (size != 0)
                            m_map.put(absPath, size);
                        totalsize += size;
                    }
                }
                read.close();
            } else {
                System.out.println(fileName + "doesn't exist");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        m_infoIds =  new ArrayList<Map.Entry<String, Long>>(m_map.entrySet());
        Collections.sort(m_infoIds, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                int ret;
                Long t1 = o1.getValue();
                Long t2 = o2.getValue();
                if (t2 > t1)
                    ret = -1;
                else if (t2 == t1)
                    ret = 0;
                else
                    ret = 1;
                return ret;
                //return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });


        return totalsize;
    }

    public long sortGitbySize() {
        long totalsize = 0;

        try {
            Iterator iter = m_map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                String absPath = key.toString();

                File gitdir = new File(absPath);
                if (gitdir.isDirectory() && gitdir.exists()) {
                    long size = sdl(absPath, 0);
                    if (DEBUG)
                        System.out.println(absPath + "=" + size);
                    if (size != 0)
                        m_map.put(absPath, size);
                    totalsize += size;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        m_infoIds =  new ArrayList<Map.Entry<String, Long>>(m_map.entrySet());
        Collections.sort(m_infoIds, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                int ret;
                Long t1 = o1.getValue();
                Long t2 = o2.getValue();
                if (t2 > t1)
                    ret = -1;
                else if (t2 == t1)
                    ret = 0;
                else
                    ret = 1;
                return ret;
                //return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        return totalsize;
    }

    public void printSortedGit() {
        //long total = 0;
        for (int i = 0; i < m_infoIds.size(); i++) {
            String path = m_infoIds.get(i).getKey();
            long val = m_infoIds.get(i).getValue();
            val = val /1024 /1024;
            if (val > 0)
                System.out.println("No " + i + " " + path + " = " + val + "MB");
        }
    }

    long sdl(String dirname, long dirsize)
    {
        File dir=new File(dirname);
        long size = 0;
        String f[]=dir.list();
        File f1;
        for(int i=0;i<f.length;i++)
        {
            f1 = new File (dirname+"/"+f[i]);
            try {
                if (isSymlink(f1))
                    continue;
                else if (f1.isFile())
                    size += f1.length();
                else
                    size += sdl(dirname + "/" + f[i], 4096);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //System.out.println(dirname + "=" + size);
        return dirsize + size;
    }

    public boolean isSymlink(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File must not be null");
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    public int obtainGitList() {
        String ret = null;
        StringBuffer cmd = new StringBuffer();
        String git_path = m_path + "/.repo/project-objects";
        cmd.append("find ");
        cmd.append(git_path);
        cmd.append(" -name *.git");
        System.out.println("cmd =" + cmd.toString());

        try {
            Process ps = Runtime.getRuntime().exec(cmd.toString());
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                m_map.put(line, (long)0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (DEBUG)
            dump();
        return m_map.size();
    }

    public void dump() {
        Iterator iter = m_map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            System.out.println(key.toString() + "=" + val.toString());
        }

        System.out.println("map.size =" + m_map.size());
    }
}

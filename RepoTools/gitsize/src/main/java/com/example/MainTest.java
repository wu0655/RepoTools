package com.example;

import java.io.File;

/**
 * Created by wupeng on 17-3-12.
 */

public class MainTest {
    //new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
    public static void main(String args[]) {
        boolean error = false;
        for (int i=0; i< args.length; i++) {
            System.out.println("args[" + i + "]=" + args[i]);
        }

        String repo_path = args[0] + "/.repo/project-objects";
        try {
            File f = new File(repo_path);
            if (! f.isDirectory()) {
                System.out.println("REPO DIR is not a directory\n");
                error = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (error) {
            printUsuage();
            return;
        }

        SortedGitSize test = new SortedGitSize(repo_path);
        int count = test.obtainGitList();
        if (count == 0) {
            System.out.println("not git repo founded.\n");
            printUsuage();
            return;
        }
        test.sortGitbySize();
        test.printSortedGit();
        System.out.println("end");
}

    public static void printUsuage() {
        System.out.println("Usage: java -jar gitsize.jar [ANDROID DIR]\n");
    }
}

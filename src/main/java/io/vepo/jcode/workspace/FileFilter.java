package io.vepo.jcode.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFilter {

    private List<String> ignoreList;

    public FileFilter(List<String> ignoreList) {
        this.ignoreList = new ArrayList<>(ignoreList);
        this.ignoreList.add(".git");
    }

    public boolean ignore(File file) {
        return ignoreList.stream().anyMatch(file.getName()::matches);
    }

}

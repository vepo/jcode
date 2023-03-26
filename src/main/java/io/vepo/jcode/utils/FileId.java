package io.vepo.jcode.utils;

import static java.util.Objects.requireNonNull;

import java.io.File;

public interface FileId {
    public static String idFromFile(File file) {
        requireNonNull(file, "File cannot be null!");
        return file.getAbsolutePath().replaceAll("[^A-Za-z0-9]", "-");
    }
}

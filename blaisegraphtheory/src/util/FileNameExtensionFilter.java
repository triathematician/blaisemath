/*
 * FileNameExtensionFilter.java
 * Created Aug 10, 2010
 */

package util;

import java.io.File;

/**
 * A file filter that uses a set list of extensions & also allows directories.
 * File name extension comparisons are case insensitive.
 * @author Elisha Peterson
 */
public final class FileNameExtensionFilter extends javax.swing.filechooser.FileFilter {

    String description;
    String[] extensions;

    /**
     * Constructs file filter with specified list of extensions
     * @param description description of the filter
     * @param extensions supported extensions for this filter.
     */
    public FileNameExtensionFilter(String description, String... extensions) {
        this.description = description;
        this.extensions = extensions;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String extension = getExtension(f);
        if (extension == null)
            return false;
        for (String s : extensions)
            if (extension.equals(s.toLowerCase()))
                return true;
        return false;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        if (extensions.length == 0)
            return description;
        StringBuilder result = new StringBuilder(description);
        result.append(" (*.").append(extensions[0]);
        for (int i = 1; i < extensions.length; i++)
            result.append(", *.").append(extensions[i]);
        result.append(")");
        return result.toString();
    }



    //
    // STATIC UTILTIIES
    //

    /*
     * Get the extension of a file.
     */
    protected static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}

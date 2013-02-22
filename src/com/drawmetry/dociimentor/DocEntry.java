package com.drawmetry.dociimentor;

import java.util.regex.Matcher;

/**
 * The DocEntry class is a simple data structure that contains the id, which is
 * used as the primary key in the database table, and the file name.
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class DocEntry {

    private String fileName;
    private int id;
    private Matcher matcher;

    /**
     * Creates a new instance of DocEntry
     * @param fileName the name of the document
     * @param id the primary key 
     */
    public DocEntry(String fileName, int id) {
        if (id < 1) {
            throw new IllegalArgumentException("id must me greater than 0");
        }
        matcher = DocumentObject.FILE_NAME_PATTERN.matcher(fileName);
        if (!matcher.matches()) {
            assert false;
        }
        this.fileName = fileName;
        this.id = id;
    }

    /**
     * Get the file name
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the document name.
     * @param name the document name
     */
    public void setFileName(String name) {
        this.fileName = name;
    }

    /**
     * Gets the entry's id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the entry
     * @param id the entry's id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets a string representation of an entry
     * @return the string representation
     */
    @Override
    public String toString() {
        return id + ": " + fileName;

    }

    /**
     * Tests whether the document identified by this entry is a later revision
     * of the document identified by other based on their document names.
     * @param other the entry to compare with
     * @return 0 is this fileName is not comparable to other fileName. 
     * Otherwise, returns a negative number if this has a lesser revision than 
     * other. 
     */
    public int compare(DocEntry other) {
        if (other == null) {
            return 0;
        }
        Matcher m = DocumentObject.FILE_NAME_PATTERN.matcher(other.fileName);
        if (!m.matches()) {
            return 0;
        }
        String s0 = m.group(DocumentObject.YR_DCN);
        String s1 = matcher.group(DocumentObject.YR_DCN);
        if (s0.equals(s1)) {
            int rev0 = Integer.parseInt(matcher.group(DocumentObject.REV));
            int rev1 = Integer.parseInt(m.group(DocumentObject.REV));
            return rev0 - rev1;
    }
        return 0;
}
}

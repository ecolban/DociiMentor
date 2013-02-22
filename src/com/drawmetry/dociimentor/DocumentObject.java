package com.drawmetry.dociimentor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Object representation of an entry in one of the working group tables.
 * DocumentObject instances are compared by comparing their uploadDate
 * 
 * @author Erik
 */
public class DocumentObject implements Comparable<DocumentObject> {

    public static final Pattern FILE_NAME_PATTERN = Pattern.compile(
            "(([\\w-]{2,})-((\\d{2})-(\\d{4}))-(\\d{2}))-(\\w{4})-(.*)");
    public static final int FILE_PREFIX = 1;
    public static final int WG = 2;
    public static final int YR_DCN = 3;
    public static final int YEAR = 4;
    public static final int DCN = 5;
    public static final int REV = 6;
    public static final int GROUP_CODE = 7;
    public static final int REST = 8;
    private int year;
    private int dcn;
    private int revision;
    private String groupCode;
    private String title;
    private String authors;
    private Date uploadDate;
    private URL url;
    private String fileName;
    private String notes;
    private static final DateFormat mentorDateFormat =
            new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);

    static {
        mentorDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    /**
     * Constructor to be used when constructing a DocumentObject instance from
     * an entry in the database.
     * 
     * @param id
     * @param year
     * @param dcn
     * @param rev
     * @param groupName
     * @param title
     * @param authors
     * @param uploadDate
     * @param url
     * @param notes
     * @throws ParseException
     * @throws MalformedURLException 
     */
    public DocumentObject(int id, int year, int dcn, int rev,
            String groupCode, String title, String authors,
            Date uploadDate, String url, String notes)
            throws ParseException, MalformedURLException {
        this.year = year;
        this.dcn = dcn;
        this.revision = rev;
        this.groupCode = groupCode;
        this.title = title;
        this.authors = authors;
        this.uploadDate = new Date(uploadDate.getTime());
        this.url = new URL(url);
        fileName = this.url.getPath();
        fileName = new File(fileName).getName();
        this.notes = notes;
    }

    /**
     * Constructor used when an instance from String's. Used, e.g., when 
     * constructing an instance from parsed source page. 
     * 
     * @param id
     * @param year
     * @param dcn
     * @param rev
     * @param groupCode
     * @param title
     * @param authors
     * @param uploadDate
     * @param url
     * @param notes
     * @throws ParseException
     * @throws MalformedURLException 
     */
    public DocumentObject(int id, String year, String dcn, String rev,
            String groupCode, String title, String authors,
            String uploadDate, String url, String notes)
            throws ParseException, MalformedURLException {
        this.year = Integer.parseInt(year);
        this.dcn = Integer.parseInt(dcn);
        this.revision = Integer.parseInt(rev);
        this.groupCode = groupCode;
        this.title = title;
        this.authors = authors;
        this.uploadDate = mentorDateFormat.parse(uploadDate);
        this.url = new URL(url);
        fileName = this.url.getPath();
        fileName = new File(fileName).getName();
        this.notes = notes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocumentObject other = (DocumentObject) obj;
        if (this.url != other.url && (this.url == null || !this.url.equals(other.url))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(DocumentObject obj) {
        if (obj == null) {
            return 1;
        }
        return uploadDate.compareTo(((DocumentObject) obj).uploadDate);
    }

    /**
     * 
     * @return URL
     */
    public URL getURL() {
        return url;
    }

    /**
     * Sets the URL
     * 
     * @param url the URL
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return the authors
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * Sets the authors
     * 
     * @param authors 
     */
    public void setAuthors(String authors) {
        this.authors = authors;
    }

    /**
     * 
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * 
     * @return the DCN
     */
    public int getDcn() {
        return this.dcn;
    }

    /**
     * 
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name
     * @param fileName 
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * 
     * @return the revision
     */
    public int getRev() {
        return revision;
    }

    /**
     * 
     * @return the group code
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * 
     * @return the upload date as a String according to ET time zone
     */
    public String getUploadDateString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return df.format(uploadDate);
    }

    /**
     * 
     * @return the upload date as an SQL time stamp
     */
    public java.sql.Timestamp getUploadTimeStamp() {
        return new java.sql.Timestamp(uploadDate.getTime());
    }

    /**
     * 
     * @return the upload date as a Date instance.
     */
    public Date getUploadDate() {
        return uploadDate;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                year, dcn, revision, groupCode, title, authors, fileName,
                getUploadDateString());
    }

    /**
     * The file prefix is a String gg-yy-ssss-rr, where gg is the working group,
     * e.g., 11 for WG 802.11, yy is the two last digits of the year, ssss is
     * the DCN, rr is the revision.
     * @return 
     */
    String getFilePrefix() {
        Matcher m = FILE_NAME_PATTERN.matcher(fileName);
        if (m.matches()) {
            return m.group(FILE_PREFIX);
        } else {
            return null;
        }
    }

    public String getDocumentId() {
        Matcher m = FILE_NAME_PATTERN.matcher(fileName);
        if (m.matches()) {
            return String.format("IEEE 802.%s-%s-%s-%s-%s",
                    m.group(WG), m.group(YEAR), m.group(DCN), 
                    m.group(REV), m.group(GROUP_CODE));
        } else {
            assert false;
            return null;
        }
    }
}

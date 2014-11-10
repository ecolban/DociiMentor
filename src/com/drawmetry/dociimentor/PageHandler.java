package com.drawmetry.dociimentor;

import com.drawmetry.dociimentor.database.DataAccessObject;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Instances of this class is used to parse pages downloaded from
 * 
 * https://mentor.docii.org/802.xx/documents
 * 
 * An instance scrapes information about each document on a page and saves it
 * in the database unless the information has already been captured.
 * 
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class PageHandler {

    private static final String[] entryString = new String[]{
        "<tr .*>", // group 1: ignore);
        // <td><div class="date_time">18-Mar-2013 ET</div></td>
        "<td><div class=\"date_time\">[0-9a-zA-Z\\- ]+</div></td>",
        "<td class=\"dcn_ordinal\">(\\d+)</td>", // year
        "<td class=\"dcn_ordinal\">(\\d+)</td>", // dcn
        "<td class=\"dcn_ordinal\">(\\d+)</td>", // revision
        "<td>(.*)</td>", // group
        "<td class=\"long\">\\s*(.*)</td>", // title
        "<td class=\"long\">(.*)</td>", //authors
        "<td><div class=\"date_time\">(.*)</div></td>", // upload date-time
        "<td class=\"list_actions\"><a href=\"(.*)\">Download</a></td>", //url
        "</tr>"};
    private static final Pattern[] entryPattern = new Pattern[entryString.length];
    private static final Pattern nextOnPattern = Pattern.compile(
            "<span class=\"next on\">Next</span>");

    static {
        for (int i = 0; i < entryString.length; i++) {
            entryPattern[i] = Pattern.compile(entryString[i]);
        }
    }
    private int patternIndex = 0;
    private String year;
    private String dcn;
    private String revision;
    private String groupCode;
    private String title;
    private String authors;
    private String dateTime;
    private String urlString;
    private URL url;
    private DataAccessObject db;
    private boolean nextOff = true;
    private final int maxPages;
    private int page = 0;
    private int minYear;
    private int maxYear;
    private String[] groupCodes;
    private final String table;
    private URL urlContext;
    private int numDocsOnPage = 0;

    /**
     * Constructor
     * 
     * @param ui the {@link UI} instance that has all the contextual 
     * information needed. 
     * 
     */
    public PageHandler(UI ui) {
        this.db = ui.getDb();

        this.minYear = ui.getWorkingGroup().getMinYears();
        this.maxYear = Math.min(
                ui.getWorkingGroup().getMaxYears(),
                new GregorianCalendar().get(Calendar.YEAR));
        this.maxPages = ui.getWorkingGroup().getMaxPages();
        this.groupCodes = ui.getWorkingGroup().getGroupCodes();
        this.table = ui.getWorkingGroup().getTable();
        this.urlContext = ui.getWorkingGroup().getHostUrl();
    }

    /**
     * Handles one line read from the page.
     * 
     * @param line 
     */
    public void readLine(String line) {

        Matcher nextOffMatcher = nextOnPattern.matcher(line);
        if (nextOffMatcher.find()) {
            nextOff = false;
        }

        Matcher matcher = entryPattern[patternIndex].matcher(line);
        switch (patternIndex) {
            case 0:
            case 1:
                if (matcher.matches()) {
                    patternIndex++;
                } else {
                    patternIndex = 0;
                }
                break;
            case 2:
                if (matcher.matches()) {
                    year = matcher.group(1);
                    if (isGoodYear(year)) {
                        patternIndex++;
                        break;
                    }
                }
                patternIndex = 0;
                break;
            case 3:
                if (matcher.matches()) {
                    dcn = matcher.group(1);
                    patternIndex++;
                } else {
                    patternIndex = 0;
                }
                break;
            case 4:
                if (matcher.matches()) {
                    revision = matcher.group(1);
                    patternIndex++;
                } else {
                    patternIndex = 0;
                }
                break;
            case 5:
                if (matcher.matches()) {
                    patternIndex++;
                    break;
                } else {
                    patternIndex = 0;
                }
                break;
            case 6:
                if (matcher.matches()) {
                    title = matcher.group(1);
                    patternIndex++;
                } else {
                    patternIndex = 0;
                }
                break;
            case 7:
                if (matcher.matches()) {
                    authors = matcher.group(1);
                    patternIndex++;
                } else {
                    patternIndex = 0;
                }
                break;
            case 8:
                if (matcher.matches()) {
                    dateTime = matcher.group(1);
                    patternIndex++;
                } else {
                    patternIndex = 0;
                }
                break;
            case 9:
                if (matcher.matches()) {
                    urlString = matcher.group(1);
                    try {
                        url = new URL(urlContext, urlString);
                        String fileName = url.getPath();
                        fileName = new File(fileName).getName();
                        Matcher m = DocumentObject.FILE_NAME_PATTERN.matcher(fileName);
                        if (m.matches()) {
                            groupCode = m.group(DocumentObject.GROUP_CODE);
                            if (isGoodGroup(groupCode)) {
                                patternIndex++;
                            } else {
                                patternIndex = 0;
                            }
                        } else {
                            patternIndex = 0;
                        }
                    } catch (MalformedURLException ex) {
                        url = null;
                        patternIndex = 0;
                        UI.LOGGER.log(Level.SEVERE, null, ex);
                    }
                } else {
                    patternIndex = 0;
                }
                break;
            case 10:
                if (matcher.matches()) {
                    try {
                        DocumentObject doc = new DocumentObject(-1,
                                year, dcn, revision, groupCode, title, authors,
                                dateTime, new URL(urlContext, urlString).toString(), "");
                        List<DocEntry> entries = db.findEntries(table, doc.getFileName());
                        if (entries.isEmpty()) {
                            db.saveRecord(table, doc);
                            numDocsOnPage++;
                        }
                    } catch (ParseException ex) {
                        UI.LOGGER.log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        UI.LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
                patternIndex = 0;
                break;
            default:
        }
    }

    /**
     * Reset for a next page
     */
    public void newPage() {
        page++;
        numDocsOnPage = 0;
        nextOff = true;  // last page
        patternIndex = 0;
    }

    /**
     * 
     * @return true if done
     */
    public boolean done() {
        return nextOff || page >= maxPages;
    }

    private boolean isGoodYear(String year) {
        int y = 0;
        try {
            y = Integer.parseInt(year);
        } catch (NumberFormatException ex) {
            assert false;
        }
        return (minYear <= y && y <= maxYear);
    }

    private boolean isGoodGroup(String groupCode) {
        assert groupCode != null;
        boolean found = false;
        for (int i = 0; !found && i < groupCodes.length; i++) {
            found = groupCode.equals(groupCodes[i]);
        }
        return found;
    }

    public int getNumDocsOnPage() {
        return numDocsOnPage;
    }
    
    public int getPage() {
        return page;
    }
}

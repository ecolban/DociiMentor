package com.drawmetry.dociimentor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is used to parse the config.xml file. It is also used to retrieve
 * configuration parameter read from the config.xml file.
 * 
 * This class is a singleton class and the parameters are retrieved through 
 * static methods
 *
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class Configuration extends DefaultHandler {

    private static File configFile;
    private String user;
    private String password;
    private String driver;
    private String derbyUrl;
    private String schema;
    private File systemHome;
    private File localFilesRoot;
    private WorkingGroup currentWorkingGroup;
    private List<WorkingGroup> workingGroups = new ArrayList<WorkingGroup>();
    private Map<String, WorkingGroup> workingGroupMap = new HashMap<String, WorkingGroup>();
    private List<DirectoryMatcher> directories;
    private static Configuration instance;
    private String database;
    private static final ResourceBundle messageBundle =
            ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle");
    private static final String ALL_YEARS = messageBundle.getString("ALL YEARS");
    private static final String ALL_GROUPS = messageBundle.getString("ALL GROUPS");

    private static class DirectoryMatcher {

        private final String replacement;
        private final Pattern pattern;

        private DirectoryMatcher(String directory, String regexp) {
//            assert localFilesRoot != null;
            assert directory != null;// && directory.exists() && directory.isDirectory();
            replacement = directory;
            pattern = Pattern.compile(regexp);
        }
    }

    private static class Group {

        private final String code;
        private final String name;

        private Group(String code, String name) {
            assert code != null && code.length() == 4;
            assert name != null;
            this.code = code;
            this.name = name;
        }
    }

    public static class WorkingGroup {

        private final String name;
        private final String table;
        private final URL host;
        private final String path;
        private int minYears;
        private int maxYears;
        private int maxPages;
        private final List<Group> groups;

        private WorkingGroup(String name, String table, URL host, String path) {
            assert name != null;
            assert table != null;
            assert host != null;
            assert path != null;
            this.name = name;
            this.table = table;
            this.host = host;
            this.path = path;
            this.groups = new ArrayList<Group>();
            this.minYears = 2000;
            this.maxYears = 2020;
            this.maxPages = 2;
        }

        public URL getHostUrl() {
            return host;
        }

        public String getPath() {
            return path;
        }

        public int getMinYears() {
            return minYears;
        }

        public int getMaxYears() {
            int yearNow = new GregorianCalendar().get(Calendar.YEAR);
            return Math.min(maxYears, yearNow);
        }

        public String[] getYears() {
            int max = getMaxYears();
            String[] result = new String[max - minYears + 2];
            result[0] = ALL_YEARS;
            for (int i = max; i >= minYears; i--) {
                result[max - i + 1] = "" + i;
            }
            return result;
        }

        public int getMaxPages() {
            return maxPages;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getTable() {
            return table;
        }

        public Map<String, String> getGroupCodeToNameMap() {
            Map<String, String> map = new HashMap<String, String>();
            for (Group g : groups) {
                map.put(g.code, g.name);
            }
            return map;
        }

        public Map<String, String> getGroupNameToCodeMap() {
            Map<String, String> map = new HashMap<String, String>();
            for (Group g : groups) {
                map.put(g.name, g.code);
            }
            return map;
        }

        public String[] getGroupNames() {
            String[] result = new String[groups.size() + 1];
            result[0] = ALL_GROUPS;
            for (int i = 1; i < result.length; i++) {
                result[i] = groups.get(i - 1).name;
            }
            return result;
        }

        public String[] getGroupCodes() {
            String[] result = new String[groups.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = groups.get(i).code;
            }
            return result;
        }
    }

    public static void main(String[] args) {
        File root = new File("D:/IEEE/docs/");
        move(root, root);
    }

    private static void move(File root, File subdir) {
        List<DirectoryMatcher> dms = new ArrayList<DirectoryMatcher>();
        dms.add(new DirectoryMatcher("802-ec/20$1", "ec-(\\d\\d)-.+"));
        dms.add(new DirectoryMatcher("802wg$1/20$2", "(\\w\\w)-(\\d\\d)-.+"));
        for (File f : subdir.listFiles()) {
            if (f.isDirectory()) {
                move(root, f);
            } else {
                String fName = f.getName();
                boolean found = false;
                for (DirectoryMatcher dm : dms) {
                    Matcher m = dm.pattern.matcher(fName);
                    if (m.matches()) {
                        StringBuffer sb = new StringBuffer();
                        m.appendReplacement(sb, dm.replacement);
                        File parent = new File(root, sb.toString());
                        if (!parent.exists()) {
                            parent.mkdirs();
                        }
                        File destination = new File(parent, fName);
                        f.renameTo(destination);
                        System.out.format("%s ==> %s\n", fName, parent.getAbsoluteFile());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    f.renameTo(new File(root, fName));
                }
            }
        }
    }

    public static void initialize() {
        instance = new Configuration();
    }

    /**
     * Gets the names of all the Working Groups listed in the dociiconfig.xml file.
     * These names appear in {@link UserInterface#workingGroupComboBox}
     * 
     * @return a String array with the names.
     */
    public static String[] getWorkingGroupNames() {
        String[] result = new String[instance.workingGroups.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = instance.workingGroups.get(i).name;
        }
        return result;
    }

    /**
     * Gets the database tables (one for each working group).
     * 
     * @return a String array with the names of the tables
     */
    public static String[] getWorkingGroupTables() {
        String[] result = new String[instance.workingGroups.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = instance.workingGroups.get(i).table;
        }
        return result;
    }

    /**
     * Gets the WorkingGroup instance with the given name
     * @param name the name of the working group.
     * @return 
     */
    public static WorkingGroup getWorkingGroup(String name) {
        return instance.workingGroupMap.get(name);
    }

    /**
     * Get the directory of the database
     * @return the directory of the database
     */
    public static File getSystemHome() {
        return instance.systemHome;
    }

    /**
     * Gets the user id for the database
     * @return the user
     */
    public static String getUser() {
        return instance.user;
    }

    /**
     * Gets the password for the user
     * @return the password
     */
    public static String getPassword() {
        return instance.password;
    }

    /**
     * Gets the database driver
     * @return the driver, e.g., "org.apache.derby.jdbc.EmbeddedDriver"
     */
    public static String getDriver() {
        return instance.driver;
    }

    /**
     * Gets the Derby URL
     * @return the Derby URL, e.g., "jdbc:derby:"
     */
    public static String getDerbyUrl() {
        return instance.derbyUrl;
    }

    /**
     * Gets the database schema, e.g., "APP"
     * @return 
     */
    public static String getSchema() {
        return instance.schema;
    }

    /**
     * Gets the database name.
     * @return the database name
     */
    public static String getDatabase() {
        return instance.database;
    }

    /**
     * Gets the database properties
     * @return the database properties
     */
    public static Properties getProperties() {
        Properties props = new Properties();
        props.put("user", instance.user);
        props.put("password", instance.password);
        props.put("derby.driver", instance.driver);
        props.put("derby.url", instance.derbyUrl);
        props.put("derby.system.home", instance.systemHome);
        props.put("db.name", instance.database);
        props.put("schema", instance.schema);
        return props;
    }

    /**
     * 
     * @param fileName
     * @return the directory where to find or store the file with the given 
     * file name.
     */
    public static File getDirectory(String fileName) {
        if (instance.directories == null) {
            return instance.localFilesRoot;
        }
        for (DirectoryMatcher dm : instance.directories) {
            Matcher matcher = dm.pattern.matcher(fileName);
            if (matcher.matches()) {
                StringBuffer sb = new StringBuffer();
                matcher.appendReplacement(sb, dm.replacement);
                File directory = new File(instance.localFilesRoot, sb.toString());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                return directory;
            }
        }
        return instance.localFilesRoot;
    }

    /**
     * Private constructor. Used to instantiate the single instance of this 
     * class.
     */
    private Configuration() {
        configFile = new File(System.getProperty("user.home"), ".dociimentor/dociiconfig.xml");
        parseDocument();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser parser = factory.newSAXParser();

            //parse the file and also register this class for call backs
            parser.parse(configFile, this);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger("com.drawmetry.dociimentor").log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger("com.drawmetry.dociimentor").log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger("com.drawmetry.dociimentor").log(Level.SEVERE, null, ex);
        }
    }
//Event Handlers

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {
        //reset
        if (qName.equalsIgnoreCase("dociimentor")) {
        } else if (qName.equalsIgnoreCase("derby")) {
            startDerby(atts);
        } else if (qName.equalsIgnoreCase("localfiles")) {
            startLocalFiles(atts);
        } else if (qName.equalsIgnoreCase("sub")) {
            startSub(atts);
        } else if (qName.equalsIgnoreCase("workinggroup")) {
            startWorkingGroup(atts);
        } else if (qName.equalsIgnoreCase("docgroup")) {
            startGroup(atts);
        } else if (qName.equalsIgnoreCase("years")) {
            startYears(atts);
        } else {
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("workinggroup")) {
            endWorkingGroup();
        }

    }

    private void startDerby(Attributes atts) throws SAXException {
        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equalsIgnoreCase("user")) {
                user = atts.getValue(i);
            } else if (atts.getQName(i).equalsIgnoreCase("password")) {
                password = atts.getValue(i);
            } else if (atts.getQName(i).equalsIgnoreCase("driver")) {
                driver = atts.getValue(i);
            } else if (atts.getQName(i).equalsIgnoreCase("url")) {
                derbyUrl = atts.getValue(i);
            } else if (atts.getQName(i).equalsIgnoreCase("schema")) {
                schema = atts.getValue(i);
            } else if (atts.getQName(i).equalsIgnoreCase("systemhome")) {
                File sysHome = new File(atts.getValue(i));
                boolean success = false;
                if (!sysHome.isAbsolute()) {
                    sysHome = new File(System.getProperty("user.home"), sysHome.getPath());
                }
                if (!sysHome.exists()) {
                    success = sysHome.mkdirs();
                } else if (!sysHome.isDirectory()) {
                    success = false;
                } else {
                    success = true;
                }
                if (!success) {
                    throw new SAXException(sysHome.toString() + " is not a directory");
                } else {
                    systemHome = sysHome;
                }
            } else if (atts.getQName(i).equalsIgnoreCase("database")) {
                database = atts.getValue(i);
            }
        }
    }

    private void startLocalFiles(Attributes atts) throws SAXException {
        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equalsIgnoreCase("root")) {
                localFilesRoot = new File(atts.getValue(i));
                if(!localFilesRoot.isAbsolute()) {
                    localFilesRoot = new File(System.getProperty("user.home"), atts.getValue(i));
                }
                boolean success;
                if (!localFilesRoot.exists()) {
                    success = localFilesRoot.mkdirs();
                } else if (!localFilesRoot.isDirectory()) {
                    success = false;
                } else {
                    success = true;
                }
                if (!success) {
                    localFilesRoot = null;
                    throw new SAXException(atts.getValue(i) + " is not a directory.");
                }
            }
        }
    }

    private void startSub(Attributes atts) throws SAXException {
        String dir = null;
        String regexp = null;
        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equalsIgnoreCase("dir")) {
                dir = atts.getValue(i);
            } else if (atts.getQName(i).equalsIgnoreCase("regexp")) {
                regexp = atts.getValue(i);
            }
        }
        if (dir != null && regexp != null) {
//            boolean success = true;
//            File directory = new File(localFilesRoot, dir);
//            if (!directory.exists()) {
//                success = directory.mkdirs();
//            } else if (!directory.isDirectory()) {
//                success = false;
//            } else {
//                success = true;
//            }
//            if (!success) {
//                throw new SAXException(dir + " is not a directory");
//            }
            DirectoryMatcher dm = new DirectoryMatcher(dir, regexp);
            if (directories == null) {
                directories = new ArrayList<DirectoryMatcher>();
            }
            directories.add(dm);
        }
    }

    private void startWorkingGroup(Attributes atts) throws SAXException {
        String name = null;
        String table = null;
        String host = null;
        String path = null;
        try {
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equalsIgnoreCase("name")) {
                    name = atts.getValue(i);
                } else if (atts.getQName(i).equalsIgnoreCase("table")) {
                    table = atts.getValue(i);
                } else if (atts.getQName(i).equalsIgnoreCase("host")) {
                    host = atts.getValue(i);
                } else if (atts.getQName(i).equalsIgnoreCase("path")) {
                    path = atts.getValue(i);
                }
            }
            URL hostUrl = new URL(host);
            currentWorkingGroup = new WorkingGroup(name, table, hostUrl, path);
            workingGroups.add(currentWorkingGroup);
            workingGroupMap.put(name, currentWorkingGroup);
        } catch (MalformedURLException ex) {
            throw new SAXException(ex);
        }
    }

    private void startGroup(Attributes atts) {
        String code = null;
        String name = null;

        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equalsIgnoreCase("code")) {
                code = atts.getValue(i);
            } else if (atts.getQName(i).equalsIgnoreCase("name")) {
                name = atts.getValue(i);
            }
        }
        assert code != null && code.length() == 4 && name != null;
        assert currentWorkingGroup != null && currentWorkingGroup.groups != null;
        currentWorkingGroup.groups.add(new Group(code, name));
    }

    private void startYears(Attributes atts) {

        for (int i = 0; i < atts.getLength(); i++) {
            if (atts.getQName(i).equalsIgnoreCase("min")) {
                currentWorkingGroup.minYears = Integer.parseInt(atts.getValue(i));
            } else if (atts.getQName(i).equalsIgnoreCase("max")) {
                currentWorkingGroup.maxYears = Integer.parseInt(atts.getValue(i));
            } else if (atts.getQName(i).equalsIgnoreCase("pages")) {
                currentWorkingGroup.maxPages = Integer.parseInt(atts.getValue(i));
            }
        }
    }

    private void endWorkingGroup() {
        currentWorkingGroup = null;
    }
}

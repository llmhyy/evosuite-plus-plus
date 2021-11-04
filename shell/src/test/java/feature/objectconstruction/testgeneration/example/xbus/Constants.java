package feature.objectconstruction.testgeneration.example.xbus;

import java.text.SimpleDateFormat;

/*     */ public class Constants {   
	public static final String RC_OK = "RC_OK";   
	public static final String RC_NOK = "RC_NOK";   
	public static final String CONFIGURATION_TRUE = "true";   
	public static final String CONFIGURATION_FALSE = "false";   
	public static final String TYPE_TEXT = "Text";   
	public static final String TYPE_OBJECT = "Object";   
	public static final String TYPE_BINARY = "Binary";   
	public static final String TYPE_XML = "XML";   
	public static final int IFCONTENTCLASS_BYTEARRAYLIST = 0;   
	public static final int IFCONTENTCLASS_STRING = 1;   
	public static final String XBUSXMLMESSAGE_DOCUMENT = "XBUS_Document";   
	public static final String XBUSXMLMESSAGE_CALL = "XBUS_Call";   
	public static final String XBUSXMLMESSAGE_DATA = "XBUS_Data";   
	public static final String XBUSXMLMESSAGE_ID = "Id";   
	public static final String XBUSXMLMESSAGE_FUNCTION = "Function";   
	public static final String XBUSXMLMESSAGE_SOURCE = "Source";   
	public static final String XBUSXMLMESSAGE_ADDRESS = "Address";   
	public static final String XBUSXMLMESSAGE_TIMESTAMP = "Timestamp";   
	public static final String XBUSXMLMESSAGE_RETURNCODE = "Returncode";   
	public static final String XBUSXMLMESSAGE_ERRORCODE = "Errorcode";   
	public static final String XBUSXMLMESSAGE_ERRORTEXT = "Errortext";   
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");  
	public static final byte NEWLINE = 10;   public static final byte CARRIAGE_RETURN = 13; 
	public static final String FILE_SEPERATOR = "";   
	public static final String XBUS_HOME = "";  
	public static final String XBUS_ETC = "";   
	public static final String XBUS_LOG = "";  
	public static final String XBUS_PLUGIN_ETC = "";  
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";  
	public static final String FILE_NAME_DATE_FORMAT = ".yyyyMMddHHmmssSSS";  
	public static final String AS400_DATE_FORMAT = "yyyyMMdd"; 
	public static final String AS400_CALL_DATE_FORMAT = "ddMMyyyy"; 
	private static SimpleDateFormat dateFormat;   
	public static final String QUEUE_DUMP_DELIMITER = "|||||nextMessage|||||"; 
	public static final String CHAPTER_SYSTEM = "System"; 
	public static final String CHAPTER_BASE = "Base"; 
	public static final String KEY_FILENAME = "Filename";  
	public static final String KEY_SEND_RESOL = "ConflictResolution"; 
	public static final String KEY_ENCODING = "Encoding";  
	public static final String KEY_RECEIVE_RESOL = "FinalResolution";  
	public static final String KEY_CSV_HAS_HEADER = "HasHeader";  
	public static final String KEY_CSV_QUOTE_CHAR = "QuoteChar"; 
	public static final String KEY_CSV_FIELD_SEPARATOR = "FieldSeparator";
	public static final String KEY_CSV_ALWAYS_QUOTE = "AlwaysQuote";   
	public static final String KEY_CSV_DESCRIPTION_FILE = "DescriptionFile";
	public static final String SYS_ENCODING = "";
	public static final String WRITE_APPEND = "Append";  
	public static final String WRITE_OVERWRITE = "Overwrite";  
	public static final String WRITE_ERROR = "Error";   
	public static final String WRITE_RENAME = "Rename";  
	public static final int BLKSIZ = 32768;   
	public static final String TEMP_SUFFIX = "xTmp";  
	public static final String BACKUP_SUFFIX = ".save"; 
	public static final String READ_PRESERVE = "Preserve";  
	public static final String READ_RENAME = "Rename";   
	public static final String READ_DELETE = "Delete";  
	public static final String READ_DELETEFILE = "DeleteFile"; 
	public static final String READ_DELETEMEMBER = "DeleteMember";
	public static final String READ_CALLPROGRAM = "CallProgram";  
	public static final String RENAME_SUFFIX = ".back";   
	public static final String READ_ERROR = "Error";   
	public static final String READ_IGNORE = "Ignore";   
	public static final String READ_PROCESS = "Process"; 
	public static final String LOCATION_INTERN = "I";   
	public static final String LOCATION_EXTERN = "E";   
	public static final String LAYER_COREBASE = "00";   
	public static final String LAYER_TECHNICAL = "01";  
	public static final String LAYER_PROTOCOL = "02";  
	public static final String LAYER_APPLICATION = "03"; 
	public static final String LAYER_BASE = "04";   
	public static final String LAYER_ADMIN = "05";  
	public static final String LAYER_BOOTSTRAP = "06"; 
	public static final String PACKAGE_TECHNICAL_TECHNICAL = "000"; 
	public static final String PACKAGE_TECHNICAL_FILE = "001";  
	public static final String PACKAGE_TECHNICAL_AS400 = "002";  
	public static final String PACKAGE_TECHNICAL_DATABASE = "003";  
	public static final String PACKAGE_TECHNICAL_HTTP = "004";   
	public static final String PACKAGE_TECHNICAL_MQ = "005";   
	public static final String PACKAGE_TECHNICAL_JAVA = "006";  
	public static final String PACKAGE_TECHNICAL_MAIL = "007";   
	public static final String PACKAGE_TECHNICAL_SOCKET = "008"; 
	public static final String PACKAGE_TECHNICAL_MISC = "009";   
	public static final String PACKAGE_TECHNICAL_FTP = "010";  
	public static final String PACKAGE_TECHNICAL_LDAP = "011";
	public static final String PACKAGE_PROTOCOL_PROTOCOL = "000";  
	public static final String PACKAGE_PROTOCOL_AS400 = "001";  
	public static final String PACKAGE_PROTOCOL_BYTEARRAYLIST = "002"; 
	public static final String PACKAGE_PROTOCOL_RECORDS = "003";  
	public static final String PACKAGE_PROTOCOL_SIMPLEOBJECT = "004";
	public static final String PACKAGE_PROTOCOL_SIMPLETEXT = "005"; 
	public static final String PACKAGE_PROTOCOL_SOAP = "006";   
	public static final String PACKAGE_PROTOCOL_XML = "007"; 
	public static final String PACKAGE_PROTOCOL_SIMPLE = "008";  
	public static final String PACKAGE_PROTOCOL_CSV = "009";  
	public static final String PACKAGE_APPLICATION_ROUTER = "001";
	public static final String PACKAGE_APPLICATION_ADAPTER = "002"; 
	public static final String PACKAGE_APPLICATION_APPLICATIONFACTORY = "003";
	public static final String PACKAGE_BASE_NOTIFYERROR = "001";  
	public static final String PACKAGE_BASE_JOURNAL = "002";  
	public static final String PACKAGE_BASE_XBUSSYSTEM = "003";  
	public static final String PACKAGE_BASE_DELETEDMESSAGESTORE = "004"; 
	public static final String PACKAGE_ADMIN_ADMIN = "000"; 
	public static final String PACKAGE_ADMIN_JMX = "001";  
	public static final String PACKAGE_ADMIN_HTML = "002"; 
	public static final String PACKAGE_ADMIN_SOAP = "003";  
	public static final String PACKAGE_COREBASE_COREBASE = "000"; 
	public static final String PACKAGE_COREBASE_CONFIG = "001";  
	public static final String PACKAGE_COREBASE_TRACE = "002";   
	public static final String PACKAGE_COREBASE_TIMEOUTCALL = "003";   
	public static final String PACKAGE_COREBASE_XML = "004";   
	public static final String PACKAGE_COREBASE_STRINGS = "005";
/*     */    public static final String PACKAGE_COREBASE_ARITHMETIC = "006";
/*     */    public static final String PACKAGE_COREBASE_REFLECTION = "007";
/*     */    public static final String PACKAGE_COREBASE_BYTEARRAYS = "008";
/*     */    public static final String PACKAGE_BOOTSTRAP_BOOTSTRAP = "000";
/*     */    public static final String LAYER_SAMPLE = "99";
/*     */    public static final String PACKAGE_SAMPLE_SAMPLE = "888";
/*     */    public static final String LAYER_TESTDRIVER = "99";
/*     */    public static final String PACKAGE_TESTDRIVER_TESTDRIVER = "999";
/*     */    public static final String POSTPROCESSING_PERSYSTEM = "perSystem";
/*     */    public static final String POSTPROCESSING_FINAL = "final";
/*     */ 
/*  14 */    }
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */    public static final String getXMLEncoding() throws XException {
///*  27 */       String encoding = Configuration.getInstance().getValueOptional("Base", "XML", "Encoding");
///*     */ 
///*  29 */       if (encoding != null) {
///*     */ 
///*  31 */          return encoding;
///*     */ 
///*     */ 
///*     */       } else {
///*  35 */          encoding = Configuration.getInstance().getValueOptional("Base", "Encoding", "XML");
///*     */ 
///*  37 */          return encoding != null ? encoding : "UTF-8";
///*     */       }
///*     */    }
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */    public static String getLineSeperator(String platform) throws XException {
///* 106 */       String result = null;
///* 107 */       if (platform.equals("Unix")) {
///* 108 */          result = "\n";      } else {
///* 109 */          if (!platform.equals("Windows")) {         }
///* 110 */          result = "\r\n";
///*     */ 
///*     */ 
///* 113 */             List params = new Vector();
///* 114 */             params.add(platform);
///* 115 */             throw new XException("I", "00", "000", "20", params);
///*     */ 
///*     */       }
///*     */ 
///* 119 */       return result;
///*     */    }
///*     */    static {
///* 122 */       FILE_SEPERATOR = File.separator;
///*     */ 
///* 124 */       XBUS_HOME = System.getProperty("xbus.home");
///*     */ 
///* 126 */       XBUS_ETC = XBUS_HOME + FILE_SEPERATOR + "etc" + FILE_SEPERATOR;
///*     */ 
///*     */ 
///* 129 */       XBUS_LOG = XBUS_HOME + FILE_SEPERATOR + "log" + FILE_SEPERATOR;
///*     */ 
///*     */ 
///* 132 */       XBUS_PLUGIN_ETC = XBUS_HOME + FILE_SEPERATOR + "plugin" + FILE_SEPERATOR + "etc" + FILE_SEPERATOR;
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///* 144 */       dateFormat = null;
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */    public static final SimpleDateFormat getDateFormat() {
///* 154 */       if (dateFormat == null) {
///*     */ 
///* 156 */          dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
///*     */       }
///* 158 */       return dateFormat;
///*     */    }
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */    public static final String getDateAsString() {
///* 169 */       return (new SimpleDateFormat(".yyyyMMddHHmmssSSS")).format(new Date());
///*     */    }
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */    public static final String getAS400DateFormat() {
///* 181 */       return (new SimpleDateFormat("yyyyMMdd")).format(new Date());
///*     */    }
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///*     */ 
///* 211 */       SYS_ENCODING = System.getProperty("file.encoding");
///*     */ }

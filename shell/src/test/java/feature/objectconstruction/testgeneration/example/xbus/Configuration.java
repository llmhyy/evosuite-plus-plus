package feature.objectconstruction.testgeneration.example.xbus;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/*     */ public class Configuration {
/*     */    public static final String STANDARD_CONFIG = "standard";
/*     */    private static final String XBUS_HOME = "$XBUS_HOME$";
/*     */    public static final String VARIABLE_PREFIX = "$VARIABLE_";
/*     */    public static final String VARIABLE_END = "$";
/*     */    public static final String MAPPING_DEFAULT = "Default";
/*     */    private static Hashtable mInstances = new Hashtable();
/*     */    private static final Object classLock = Configuration.class;
/*     */    private Hashtable mCache = null;
///*     */    private ConfigSource mSource = null;
/*     */ 
/*     */    private Configuration(String source) {
/*  67 */       if (Constants.XBUS_HOME == null) {
/*     */ 
/*  69 */          System.out.println("I_00_000_2 XBUS_HOME has not been set!");
/*  70 */          System.exit(1);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */ 
///*  76 */       this.mSource = new PropertiesSource(source);
/*  77 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static Configuration getInstance() throws XException {
/*  92 */       return getInstance("standard");
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static Configuration getInstance(String source) throws XException {
/* 109 */       Object var1 = classLock;
/*     */       synchronized(classLock) {
/* 111 */          Configuration instance = (Configuration)mInstances.get(source);
/* 112 */          if (instance == null) {
/*     */ 
/* 114 */             instance = new Configuration(source);
/* 115 */             instance.readCache();
/* 116 */             mInstances.put(source, instance);
/*     */          }
/* 118 */          return instance;
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static void refresh() throws XException {
/* 129 */       refresh("standard");
/* 130 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static void refresh(String source) throws XException {
/* 140 */       mInstances.remove(source);
/* 141 */       getInstance(source);
/* 142 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getValue(String chapter, String section, String key) throws XException {
/* 163 */       String returnString = this.getValueInternal(chapter, section, key);
/* 164 */       if (returnString == null) {
/*     */ 
/* 166 */          List params = new Vector();
/* 167 */          params.add(chapter);
/* 168 */          params.add(section);
/* 169 */          params.add(key);
/* 170 */          throw new XException("I", "00", "000", "1", params);
/*     */ 
/*     */ 
/*     */ 
/*     */       } else {
/* 175 */          return returnString;
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public int getValueAsInt(String chapter, String section, String key) throws XException {
/* 197 */       String returnString = this.getValueInternal(chapter, section, key);
/* 198 */       if (returnString == null) {
/*     */ 
/* 200 */          List params = new Vector();
/* 201 */          params.add(chapter);
/* 202 */          params.add(section);
/* 203 */          params.add(key);
/* 204 */          throw new XException("I", "00", "000", "1", params);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       } else {
/*     */          try {
/* 212 */             int retInt = Integer.parseInt(returnString);
/* 213 */             return retInt;
/*     */ 
/* 215 */          } catch (NumberFormatException var7) {
/*     */ 
/* 217 */             List params = new Vector();
/* 218 */             params.add(chapter);
/* 219 */             params.add(section);
/* 220 */             params.add(key);
/* 221 */             throw new XException("I", "00", "000", "3", params);
/*     */          }
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public int getValueAsIntOptional(String chapter, String section, String key) throws XException {
/* 247 */       String returnString = this.getValueInternal(chapter, section, key);
/* 248 */       if (returnString == null) {
/*     */ 
/* 250 */          return 0;
/*     */ 
/*     */ 
/*     */       } else {
/*     */          try {
/* 255 */             int retInt = Integer.parseInt(returnString);
/* 256 */             return retInt;
/*     */ 
/* 258 */          } catch (NumberFormatException var7) {
/*     */ 
/* 260 */             List params = new Vector();
/* 261 */             params.add(chapter);
/* 262 */             params.add(section);
/* 263 */             params.add(key);
/* 264 */             throw new XException("I", "00", "000", "3", params);
/*     */          }
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public long getValueAsLongOptional(String chapter, String section, String key) throws XException {
/* 290 */       String returnString = this.getValueInternal(chapter, section, key);
/* 291 */       if (returnString == null) {
/*     */ 
/* 293 */          return 0L;
/*     */ 
/*     */ 
/*     */       } else {
/*     */          try {
/* 298 */             long retLong = Long.parseLong(returnString);
/* 299 */             return retLong;
/*     */ 
/* 301 */          } catch (NumberFormatException var7) {
/*     */ 
/* 303 */             List params = new Vector();
/* 304 */             params.add(chapter);
/* 305 */             params.add(section);
/* 306 */             params.add(key);
/* 307 */             throw new XException("I", "00", "000", "5", params);
/*     */          }
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public boolean getValueAsBoolean(String chapter, String section, String key) throws XException {
/* 332 */       String returnString = this.getValueInternal(chapter, section, key);      Vector params;
/* 333 */       if (returnString == null) {
/*     */ 
/* 335 */          params = new Vector();
/* 336 */          params.add(chapter);
/* 337 */          params.add(section);
/* 338 */          params.add(key);
/* 339 */          throw new XException("I", "00", "000", "1", params);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 344 */       } else if ("true".toUpperCase().equals(returnString.toUpperCase())) {
/*     */ 
/*     */ 
/* 347 */          return true;
/*     */ 
/*     */ 
/* 350 */       } else if ("false".toUpperCase().equals(returnString.toUpperCase())) {
/*     */ 
/*     */ 
/* 353 */          return false;
/*     */ 
/*     */ 
/*     */ 
/*     */       } else {
/* 358 */          params = new Vector();
/* 359 */          params.add(chapter);
/* 360 */          params.add(section);
/* 361 */          params.add(key);
/* 362 */          throw new XException("I", "00", "000", "7", params);
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public boolean getValueAsBooleanOptional(String chapter, String section, String key) throws XException {
/* 390 */       String returnString = this.getValueInternal(chapter, section, key);
/* 391 */       if (returnString == null) {
/*     */ 
/* 393 */          return false;
/*     */ 
/*     */ 
/* 396 */       } else if ("true".toUpperCase().equals(returnString.toUpperCase())) {
/*     */ 
/*     */ 
/* 399 */          return true;
/*     */ 
/*     */ 
/* 402 */       } else if ("false".toUpperCase().equals(returnString.toUpperCase())) {
/*     */ 
/*     */ 
/* 405 */          return false;
/*     */ 
/*     */ 
/*     */ 
/*     */       } else {
/* 410 */          List params = new Vector();
/* 411 */          params.add(chapter);
/* 412 */          params.add(section);
/* 413 */          params.add(key);
/* 414 */          throw new XException("I", "00", "000", "7", params);
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getValueOptional(String chapter, String section, String key) {
/* 438 */       return this.getValueInternal(chapter, section, key);
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public List getChapters() {
/* 449 */       TreeSet chapters = new TreeSet(this.mCache.keySet());
/* 450 */       return new Vector(chapters);
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public List getSections(String chapter) {
/* 463 */       Hashtable sections = (Hashtable)this.mCache.get(chapter);
/* 464 */       if (sections == null) {
/*     */ 
/* 466 */          return null;
/*     */ 
/*     */       } else {
/* 469 */          TreeSet sectionSet = new TreeSet(sections.keySet());
/* 470 */          return new Vector(sectionSet);
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public Map getKeysAndValues(String chapter, String section) {
/* 486 */       Hashtable sections = (Hashtable)this.mCache.get(chapter);
/* 487 */       if (sections == null) {
/*     */ 
/* 489 */          return null;
/*     */ 
/*     */       } else {
/* 492 */          Hashtable keys = (Hashtable)sections.get(section);
/*     */ 
/* 494 */          return keys != null ? new TreeMap(keys) : null;
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    private String getValueInternal(String chapter, String section, String key) {
/* 517 */       Hashtable sectTable = (Hashtable)this.mCache.get(chapter);
/* 518 */       if (sectTable == null) {
/*     */ 
/* 520 */          return null;
/*     */ 
/*     */       } else {
/* 523 */          Hashtable keyTable = (Hashtable)sectTable.get(section);
/* 524 */          return keyTable == null ? null : (String)keyTable.get(key);
/*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    private void readCache() throws XException {
///* 542 */       this.mCache = this.mSource.readCache();
///* 543 */       this.replaceVariables(this.mCache);
/* 544 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    private void replaceVariables(Hashtable cache) throws XException {
/* 553 */       Map variables = this.getVariables(cache);
/*     */ 
/* 555 */       Enumeration chapters = cache.elements();
/* 556 */       while(chapters.hasMoreElements()) {
/*     */ 
/* 558 */          Hashtable chapter = (Hashtable)chapters.nextElement();
/* 559 */          Enumeration sections = chapter.elements();
/* 560 */          while(sections.hasMoreElements()) {
/*     */ 
/* 562 */             Hashtable section = (Hashtable)sections.nextElement();            String key;            String value;
/* 563 */             for(Enumeration keys = section.keys(); keys.hasMoreElements(); section.put(key, value)) {
/*     */ 
/* 565 */                key = (String)keys.nextElement();
/* 566 */                value = (String)section.get(key);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 571 */                int variablePosNew = value.indexOf("$VARIABLE_");
/* 572 */                if (variablePosNew >= 0) {
/*     */ 
/* 574 */                   int variablePosOld = -99999;
/* 575 */                   String variable = null;
/* 576 */                   for(Set variablesKeySet = null; variablePosNew >= 0 && variablePosOld != variablePosNew; variablePosNew = value.indexOf("$VARIABLE_")) {
/*     */ 
/*     */ 
/*     */ 
/* 580 */                      variablePosOld = variablePosNew;
/* 581 */                      if (variables == null) {
/*     */ 
/* 583 */                         throw new XException("I", "04", "003", "4");
/*     */ 
/*     */                      }
/*     */ 
/* 587 */                      variablesKeySet = variables.keySet();
/* 588 */                      if (variablesKeySet == null) {
/*     */ 
/* 590 */                         throw new XException("I", "04", "003", "4");
/*     */ 
/*     */                      }
/*     */ 
/* 594 */                      String variablesKey = null;
/* 595 */                      Iterator it = variablesKeySet.iterator();
/* 596 */                      while(it.hasNext()) {
/*     */ 
/* 598 */                         variablesKey = (String)it.next();
/* 599 */                         variable = (String)variables.get(variablesKey);
/* 600 */                         if (value.indexOf(variablesKey) >= 0) {
/*     */ 
///* 602 */                            value = XStringSupport.replaceAll(value, variablesKey, variable);
/*     */ 
/*     */                         }
/*     */                      }
/*     */                   }
/*     */ 
/* 608 */                   if (variablePosOld == variablePosNew) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 614 */                      List params = new Vector();
/* 615 */                      params.add(new Integer(variablePosOld));
/* 616 */                      params.add(value);
/* 617 */                      throw new XException("I", "04", "003", "3", params);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                   }
/*     */                }
/*     */ 
/* 627 */                if (value.indexOf("$XBUS_HOME$") >= 0) {
/*     */ 
///* 629 */                   value = XStringSupport.replaceAll(value, "$XBUS_HOME$", Constants.XBUS_HOME);
/*     */ 
/*     */ 
/*     */ 
/*     */                }
/*     */             }
/*     */          }
/*     */       }
/*     */ 
/* 638 */    }
/*     */ 
/*     */ 
/*     */    private Map getVariables(Hashtable cache) {
				  return null;
///* 642 */       Hashtable sections = (Hashtable)cache.get("Base");
///*     */ 
///* 644 */       Map variablesConf = null;
///* 645 */       if (sections == null) {
///*     */ 
///* 647 */          return null;
///*     */ 
///*     */       } else {
///* 650 */          Hashtable keys = (Hashtable)sections.get("Variable");
///*     */ 
///* 652 */          if (keys == null) {
///*     */          } else {
///* 654 */             variablesConf = new TreeMap(keys);
///*     */ 
///*     */ 
///*     */ 
///* 658 */             return null;
///*     */ 
///*     */ 
///* 661 */             Hashtable variablesNew = new Hashtable();
///*     */ 
///* 663 */             if (variablesConf != null) {
///*     */ 
///* 665 */                String key = null;
///* 666 */                String variable = null;
///* 667 */                Iterator it = variablesConf.keySet().iterator();
///*     */                while(it.hasNext()) {
///* 669 */                   key = (String)it.next();
///* 670 */                   variable = (String)variablesConf.get(key);
///* 671 */                   key = "$VARIABLE_" + key + "$";
///*     */ 
///* 673 */                   variablesNew.put(key, variable);
///*     */                }
///*     */             }
///*     */ 
///* 677 */             return variablesNew;
///*     */          }
///*     */       }
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static String getClass(String type, String name) throws XException {
/* 693 */       return getInstance("xbus").getValue("Class", type, name);
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static String getMapping(String section, String key) throws XException {
/* 711 */       return getInstance("mapping").getValue("Mapping", section, key);
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static String getMappingOptional(String section, String key) throws XException {
/* 730 */       return getInstance("mapping").getValueOptional("Mapping", section, key);
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static String getMappingDefault(String section) throws XException {
/* 739 */       return getMapping(section, "Default");
/*     */    }
/*     */ }
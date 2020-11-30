package feature.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConvenientMap extends HashMap<String, Serializable> {
  public static final long serialVersionUID = 384681318L;
  
  private static Log log = LogFactory.getLog(ConvenientMap.class);
  
  public Serializable get(String key) {
    Serializable value = (Serializable)get(key);
    if (value == null)
      throw new NullPointerException("Unable to locate key '" + key + "'"); 
    return value;
  }
  
  private String getStringNONPE(String key) {
    Object o = get(key);
    return (o == null) ? null : ("".equals(o) ? null : (String)o);
  }
  
  public String getString(String key) {
    Object o = get(key);
    if (o instanceof String)
      return (String)o; 
    return o.toString();
  }
  
  public String getString(String key, String defaultValue) {
    String res = getStringNONPE(key);
    return (res == null) ? defaultValue : res;
  }
  
  public Integer getInt(String key) {
    Object o = get(key);
    try {
      if (o instanceof Integer)
        return (Integer)o; 
      return Integer.valueOf(Integer.parseInt(o.toString()));
    } catch (Exception e) {
      log.warn(String.format("Exception extracting int for key '%s'", new Object[] { key }), e);
      return null;
    } 
  }
  
  public Integer getInt(String key, Integer defaultValue) {
    try {
      return getInt(key);
    } catch (Exception e) {
      return defaultValue;
    } 
  }
  
  public Long getLong(String key) {
    Object o = get(key);
    try {
      if (o instanceof Long)
        return (Long)o; 
      return Long.valueOf(Long.parseLong(o.toString()));
    } catch (Exception e) {
      log.warn(String.format("Exception extracting long for key '%s'", new Object[] { key }), e);
      return null;
    } 
  }
  
  public Long getLong(String key, Long defaultValue) {
    try {
      return getLong(key);
    } catch (Exception e) {
      return defaultValue;
    } 
  }
  
  public Boolean getBoolean(String key) {
    Object o = get(key);
    try {
      if (o instanceof Boolean)
        return (Boolean)o; 
      return Boolean.valueOf(Boolean.parseBoolean(o.toString()));
    } catch (Exception e) {
      log.warn(String.format("Exception extracting boolean for key '%s'", new Object[] { key }), e);
      return null;
    } 
  }
  
  public Boolean getBoolean(String key, Boolean defaultValue) {
    try {
      return getBoolean(key);
    } catch (Exception e) {
      return defaultValue;
    } 
  }
  
  public List<String> getStrings(String key) {
    Object val = get(key);
    if (val instanceof List) {
      ArrayList<String> arrayList = new ArrayList<String>(((List)val).size());
      for (Object o : (List) val)
        arrayList.add(o.toString()); 
      return arrayList;
    } 
    if (val instanceof String[])
      return Arrays.asList((String[])val); 
    String[] unescaped = getString(key).split(" *, *");
    ArrayList<String> result = new ArrayList<String>(unescaped.length);
    for (String s : unescaped)
      result.add(s.replaceAll("&comma;", ",").replaceAll("&amp;", "&")); 
    return result;
  }
  
  public List<String> getStrings(String key, List<String> defaultValues) {
    try {
      return getStrings(key);
    } catch (NullPointerException e) {
      return defaultValues;
    } catch (IllegalArgumentException e) {
      log.warn(String.format("The property %s was expected to be a list of Strings, but it was not. Using default %s instead", new Object[] { key, defaultValues }));
      return defaultValues;
    } 
  }
  
  public String[] getStrings(String key, String[] defaultValues) {
    List<String> result = getStrings(key, (defaultValues == null) ? null : Arrays.<String>asList(defaultValues));
    return (result == null) ? null : result.<String>toArray(new String[result.size()]);
  }
  
  public static class Pair<T, U> {
    private T t;
    
    private U u;
    
    public Pair(T firstValue, U secondValue) {
      this.t = firstValue;
      this.u = secondValue;
    }
    
    public T getFirst() {
      return this.t;
    }
    
    public U getSecond() {
      return this.u;
    }
  }
  
  protected Pattern numberPattern = Pattern.compile("(.+)\\( *(\\-?[0-9]+) *\\).*");
  
  public List<Pair<String, Integer>> getIntValues(String key, Integer defaultValue) {
    Object o = get(key);
    try {
      return (List<Pair<String, Integer>>)o;
    } catch (ClassCastException e) {
      log.trace("intValues not stored directly for key '" + key + "'");
      List<String> elements = getStrings(key);
      List<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>(elements.size());
      for (String element : elements) {
        Matcher numberMatcher = this.numberPattern.matcher(element);
        if (numberMatcher.matches()) {
          result.add(new Pair<String, Integer>(numberMatcher.group(1), Integer.valueOf(Integer.parseInt(numberMatcher.group(2)))));
          continue;
        } 
        result.add(new Pair<String, Integer>(element.trim(), defaultValue));
      } 
      return result;
    } 
  }
  
  public String toString(boolean verbose) {
    if (!verbose)
      return toString(); 
    StringWriter sw = new StringWriter(1000);
    sw.append("ConvenientMap(");
    boolean later = false;
    for (Map.Entry<String, Serializable> entries : entrySet()) {
      if (later)
        sw.append(", "); 
      later = true;
      sw.append(entries.getKey()).append("=");
      sw.append(((Serializable)entries.getValue()).toString());
    } 
    sw.append(")");
    return sw.toString();
  }
}


package feature.objectconstruction.testgeneration.example.xbus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/*     */ public class MessageHandler {
/*     */    private static Hashtable mInstances = new Hashtable();
/*     */    private static final Object classLock = Configuration.class;
/*     */    private Hashtable mMessages = null;
/*     */ 
/*     */    public String getMessage(String key, List params) {
/*  46 */       String messageText = null;
/*  47 */       messageText = this.getMessageOptional(key, params);
/*     */ 
/*  49 */       if (messageText == null) {
/*     */ 
/*  51 */          messageText = "Key: " + key + " not found in message file";
/*  52 */          Trace.error(messageText);
/*     */       }
/*     */ 
/*  55 */       return messageText;
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
/*     */    public String getMessageOptional(String key, List params) {
/*  69 */       String messageText = null;
/*     */ 
/*  71 */       if (this.mMessages == null) {
/*     */ 
/*  73 */          return null;
/*     */ 
/*     */ 
/*     */       } else {
/*  77 */          messageText = (String)this.mMessages.get(key);
/*  78 */          if (messageText == null) {
/*     */ 
/*  80 */             return null;
/*     */ 
/*     */          } else {
/*  83 */             int counter = 1;
/*  84 */             if (params != null) {
/*     */ 
/*  86 */                String paramText = null;
/*  87 */                Object paramObject = null;
/*     */                Iterator it = params.iterator();
/*  89 */                while(it.hasNext()) {
/*     */ 
/*  91 */                   paramObject = it.next();
/*  92 */                   if (paramObject != null) {
/*     */ 
/*  94 */                      paramText = paramObject.toString();
/*     */ 
/*     */ 
/*     */                   } else {
/*  98 */                      paramText = "<null>";
/*     */                   }
/* 100 */                   String paramCounter = "$" + counter + "$";
/* 101 */                   if (messageText.indexOf(paramCounter) >= 0) {
/*     */ 
/* 103 */                      messageText = XStringSupport.replaceAll(messageText, paramCounter, paramText);
/*     */ 
/* 105 */                      ++counter;
/*     */                   }               }
/*     */             }
/*     */ 
/* 109 */             return messageText;
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
/*     */    public static MessageHandler getInstance(String basename) {
/* 124 */       Object var1 = classLock;
/*     */       synchronized(classLock) {
/* 126 */          MessageHandler instance = (MessageHandler)mInstances.get(basename);
/* 127 */          if (instance == null) {
/*     */ 
/* 129 */             instance = new MessageHandler(basename);
/* 130 */             mInstances.put(basename, instance);
/*     */          }
/* 132 */          return instance;
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
/*     */    private MessageHandler(String basename) {
/* 144 */       this.mMessages = new Hashtable();
/*     */ 
/* 146 */       this.addMessages(basename, Constants.XBUS_ETC);
/* 147 */       this.addMessages(basename, Constants.XBUS_PLUGIN_ETC);
/* 148 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */    private void addMessages(String basename, String dir) {
/* 153 */       Locale locale = Locale.ENGLISH;
/*     */ 
/* 155 */       String postfix = "_" + locale.toString() + ".properties";
/*     */ 
/* 157 */       File dirFile = new File(dir);
/* 158 */       String[] messagesFiles = dirFile.list(new MessagesFilter(basename, postfix));
/*     */ 
/*     */ 
/* 161 */       for(int i = 0; messagesFiles != null && i < messagesFiles.length; ++i) {
/*     */ 
/*     */ 
/* 164 */          Properties newProps = new Properties();
/*     */ 
/*     */ 
/*     */          try {
/* 168 */             FileInputStream instream = new FileInputStream(dir + messagesFiles[i]);
/* 169 */             newProps.load(instream);
/* 170 */             instream.close();
/*     */ 
/* 172 */          } catch (FileNotFoundException var13) {
/*     */ 
/* 174 */             System.out.println("Cannot find messagefile");
/* 175 */             System.exit(1);
/*     */ 
/* 177 */          } catch (IOException var14) {
/*     */ 
/* 179 */             System.out.println("Cannot find messagefile");
/* 180 */             System.exit(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */          }
/*     */ 
/* 187 */          String key = null;         Enumeration keys = newProps.keys();
/* 188 */          while(keys.hasMoreElements()) {
/*     */ 
/* 190 */             key = (String)keys.nextElement();
/*     */ 
/* 192 */             this.mMessages.put(key, newProps.get(key));
/*     */          }      }
/*     */ 
/* 195 */    }
/*     */ }
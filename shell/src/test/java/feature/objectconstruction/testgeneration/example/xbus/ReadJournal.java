package feature.objectconstruction.testgeneration.example.xbus;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/*     */ public class ReadJournal {
/*     */    private int mNumber;
/*     */    private char mType;
/*     */    private String mSystem;
/*     */    private String mFunction;
/*     */    private String mMessageId;
/*     */    private String mRequestMessage;
/*     */    private String mRequestTimestamp;
/*     */    private String mResponseMessage;
/*     */    private String mResponseTimestamp;
/*     */    private String mReturncode;
/*     */    private int mErrorcode;
/*     */    private String mErrormessage;
/*     */ 
/*     */    public int getNumber() {
/*  41 */       return this.mNumber;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setNumber(int number) {
/*  49 */       this.mNumber = number;
/*  50 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public char getType() {
/*  57 */       return this.mType;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setType(char type) {
/*  65 */       this.mType = type;
/*  66 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getSystem() {
/*  73 */       return this.mSystem;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setSystem(String system) {
/*  81 */       this.mSystem = system;
/*  82 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getFunction() {
/*  89 */       return this.mFunction;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setFunction(String function) {
/*  97 */       this.mFunction = function;
/*  98 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getMessageId() {
/* 105 */       return this.mMessageId;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setMessageId(String messageId) {
/* 113 */       this.mMessageId = messageId;
/* 114 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getRequestMessage() {
/* 121 */       return this.mRequestMessage;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setRequestMessage(String requestMessage) {
/* 129 */       this.mRequestMessage = requestMessage;
/* 130 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getRequestTimestamp() {
/* 137 */       return this.mRequestTimestamp;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setRequestTimestamp(String requestTimestamp) {
/* 145 */       this.mRequestTimestamp = requestTimestamp;
/* 146 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getResponseMessage() {
/* 153 */       return this.mResponseMessage;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setResponseMessage(String responseMessage) {
/* 161 */       this.mResponseMessage = responseMessage;
/* 162 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getResponseTimestamp() {
/* 169 */       return this.mResponseTimestamp;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setResponseTimestamp(String responseTimestamp) {
/* 177 */       this.mResponseTimestamp = responseTimestamp;
/* 178 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getReturncode() {
/* 185 */       return this.mReturncode;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setReturncode(String returncode) {
/* 193 */       this.mReturncode = returncode;
/* 194 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public int getErrorcode() {
/* 201 */       return this.mErrorcode;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setErrorcode(int errorcode) {
/* 209 */       this.mErrorcode = errorcode;
/* 210 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getErrormessage() {
/* 217 */       return this.mErrormessage;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setErrormessage(String errormessage) {
/* 225 */       this.mErrormessage = errormessage;
/* 226 */    }
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
/*     */    public static List readSelected(HashMap selectionMap, String orderBy, String sorting) throws XException {
/* 242 */       String sqlBefehl = "SELECT * FROM journal ";
/*     */       Vector params;
/* 244 */       if (!selectionMap.isEmpty()) {
/*     */ 
/*     */ 
/* 247 */          sqlBefehl = sqlBefehl + "WHERE ";
/* 248 */          List keyList = new Vector();
/* 249 */          keyList.addAll(selectionMap.keySet());
/*     */ 
/* 251 */          for(int i = 0; i < keyList.size(); ++i) {
/*     */ 
/* 253 */             String fieldName = (String)keyList.get(i);
/* 254 */             String fieldValue = (String)selectionMap.get(fieldName);
/*     */ 
/* 256 */             if (fieldName.equals("RequestTimeMin")) {
/*     */ 
/* 258 */                sqlBefehl = sqlBefehl + "jo_request_timestamp >='" + fieldValue + "' ";
/*     */ 
/* 260 */             } else if (fieldName.equals("RequestTimeMax")) {
/*     */ 
/* 262 */                sqlBefehl = sqlBefehl + "jo_request_timestamp <='" + fieldValue + "' ";
/*     */ 
/* 264 */             } else if (fieldName.equals("RequestMessage")) {
/*     */ 
/* 266 */                sqlBefehl = sqlBefehl + "jo_request_message LIKE '%" + fieldValue + "%'";
/*     */ 
/*     */ 
/* 269 */             } else if (fieldName.equals("ResponseMessage")) {
/*     */ 
/* 271 */                sqlBefehl = sqlBefehl + "jo_response_message LIKE '%" + fieldValue + "%'";
/*     */ 
/*     */ 
/* 274 */             } else if (fieldName.equals("Type")) {
/*     */ 
/* 276 */                sqlBefehl = sqlBefehl + "jo_type ='" + fieldValue + "' ";
/*     */ 
/* 278 */             } else if (fieldName.equals("Returncode")) {
/*     */ 
/* 280 */                sqlBefehl = sqlBefehl + "jo_returncode ='" + fieldValue + "' ";
/*     */ 
/* 282 */             } else if (fieldName.equals("System")) {
/*     */ 
/* 284 */                sqlBefehl = sqlBefehl + "jo_system ='" + fieldValue + "' ";
/*     */ 
/* 286 */             } else if (fieldName.equals("Function")) {
/*     */ 
/* 288 */                sqlBefehl = sqlBefehl + "jo_function ='" + fieldValue + "' ";
/*     */             } else {
/* 290 */                if (!fieldName.equals("MessageId")) {
/*     */                }
/* 292 */                sqlBefehl = sqlBefehl + "jo_message_id ='" + fieldValue + "' ";
/*     */ 
/*     */ 
/*     */ 
/* 296 */                   params = new Vector();
/* 297 */                   params.add(fieldName);
/* 298 */                   throw new XException("I", "05", "000", "3", params);
/*     */ 
/*     */ 
/*     */             }
/*     */ 
/* 303 */             if (i < keyList.size() - 1) {
/*     */ 
/* 305 */                sqlBefehl = sqlBefehl + "AND ";
/*     */ 
/*     */             }
/*     */          }
/*     */       }
/*     */ 
/*     */       String order;
/* 312 */       if (orderBy.equals("Number")) {
/*     */ 
/* 314 */          order = "jo_id";
/*     */ 
/* 316 */       } else if (orderBy.equals("Returncode")) {
/*     */ 
/* 318 */          order = "jo_returncode";
/*     */ 
/* 320 */       } else if (orderBy.equals("System")) {
/*     */ 
/* 322 */          order = "jo_system";
/*     */ 
/* 324 */       } else if (orderBy.equals("Function")) {
/*     */ 
/* 326 */          order = "jo_function";
/*     */ 
/* 328 */       } else if (orderBy.equals("MessageId")) {
/*     */ 
/* 330 */          order = "jo_message_id";
/*     */ 
/* 332 */       } else if (orderBy.equals("type")) {
/*     */ 
/* 334 */          order = "jo_type";
/*     */       } else {
/* 336 */          if (!orderBy.equals("RequestTimestamp")) {
/*     */          }
/* 338 */          order = "jo_request_timestamp";
/*     */ 
/*     */ 
/*     */ 
/* 342 */             List params1 = new Vector();
/* 343 */             params1.add(orderBy);
/* 344 */             throw new XException("I", "05", "000", "4", params1);
/*     */ 
/*     */ 
/*     */       }
/*     */ 
/* 349 */       sqlBefehl = sqlBefehl + "ORDER BY " + order;
/*     */ 
/* 351 */       if (sorting.equals("Descending")) {
/*     */ 
/* 353 */          sqlBefehl = sqlBefehl + " DESC";
/*     */ 
/*     */       }
/*     */ 
/*     */       try {
/* 358 */          String dbConnectionName = Configuration.getInstance().getValueOptional("Base", "Journal", "DBConnection");
/*     */ 
/* 360 */          if (dbConnectionName == null) {
/*     */ 
/* 362 */             dbConnectionName = "UNNAMED";
/*     */          }
/* 364 */          DBConnection dbCon = DBConnection.getInstance(dbConnectionName);
/*     */ 
/* 366 */          ResultSet result = dbCon.executeRead(sqlBefehl);
/* 367 */          params = new Vector();
/*     */ 
/* 369 */          while(result.next()) {
/*     */ 
/* 371 */             ReadJournal journal = new ReadJournal();
/* 372 */             journal.setNumber(result.getInt("jo_id"));
/* 373 */             if (result.getString("jo_type") != null && result.getString("jo_type").length() != 0) {
/*     */ 
/*     */             } else {
/* 376 */                journal.setType(' ');
/*     */ 
/*     */ 
/*     */ 
/* 380 */                journal.setType(result.getString("jo_type").charAt(0));
/*     */             }
/* 382 */             journal.setSystem(result.getString("jo_system"));
/* 383 */             journal.setFunction(result.getString("jo_function"));
/* 384 */             journal.setMessageId(result.getString("jo_message_id"));
/* 385 */             journal.setRequestMessage(result.getString("jo_request_message"));
/*     */ 
/* 387 */             journal.setRequestTimestamp(result.getString("jo_request_timestamp"));
/*     */ 
/* 389 */             journal.setResponseMessage(result.getString("jo_response_message"));
/*     */ 
/* 391 */             journal.setResponseTimestamp(result.getString("jo_response_timestamp"));
/*     */ 
/* 393 */             journal.setReturncode(result.getString("jo_returncode"));
/* 394 */             journal.setErrorcode(result.getInt("jo_errorcode"));
/* 395 */             journal.setErrormessage(result.getString("jo_errormessage"));
/*     */ 
/* 397 */             params.add(journal);
/*     */          }
/*     */ 
/* 400 */          return params;
/*     */ 
/* 402 */       } catch (Exception var10) {
/*     */ 
/* 404 */          throw new XException("I", "05", "000", "0", var10);
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
/*     */    public String toString() {
/* 424 */       return this.mNumber + " | " + this.mMessageId + " | " + this.mFunction + " | " + this.mType + " | " + this.mSystem + " | " + this.mRequestTimestamp + " | " + this.mResponseTimestamp + " | " + this.mReturncode;
/*     */    }
/*     */ }
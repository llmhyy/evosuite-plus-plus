package feature.objectconstruction.testgeneration.example.xbus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JournalBean {
/*     */    private List entries;
/*     */    private static final String collumnBgCollor = "#D1D0C7";
/*     */    private static final String greyCollor = "#E7E9E6";
/*     */    private static final String whiteCollor = "white";
/*     */    private boolean collor = false;
/*     */    private HashMap journalMap = new HashMap();
/*     */    private String message;
/*     */    private String details;
/*     */    private String journIndex;
/*     */    private static final String openTagTable_message = "<table width=\"400\" border=0 cellpadding=0 cellspacing=0><tr class=\"journal\"><td colspan=\"2\"><p class=\"zwkopf\">";
/*     */    private static final String detailsTableMessage = "<tr bgcolor=\"#E7E9E6\"><td width=\"10\">&nbsp;</td><td>";
/*     */    private static final String deatilsTable = "</td></tr><tr class=\"journal\"><td colspan=\"2\">&nbsp;&nbsp;&nbsp;</td></tr>";
/*     */    private static final String detailsTagFirst = "<tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">";
/*     */    private static final String detailsTagOne = "</td></tr><tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">";
/*     */    private static final String detailsTagTwo = "</td></tr><tr class=\"journal\"><td width=\"160\" valign=\"top\">";
/*     */    private static final String detailsTd = "</td><td>";
/*     */    private static final String openTagTdTr = "<tr><td>";
/*     */    private static final String openTagTd = "<td>";
/*     */    private static final String openTagTd_journal = "<td class=\"journal\">";
/*     */    private static final String openTagTd_function = "<td class=\"function\">";
/*     */    private static final String openTagTd_selection = "<td class=\"selection\">";
/*     */    private static final String openTagTr_selection = "<tr align=\"center\" bgcolor=";
/*     */    private static final String openTagTr_bgcolor = "<tr bgcolor=";
/*     */    private static final String openTagP = "<p class=\"zwkopf\">";
/*     */    private static final String fullTagH4 = "<h4>Details</h4>";
/*     */    private static final String paramDetails = "message=leer&details=true&index=";
/*     */    private static final String openTagA_messageIdPage = "<a class = \"call\" href=\"MessageIdPage.jsp?messageId=";
/*     */    private static final String openTagA_messagePage = "<a class = \"section\" href=\"MessagePage.jsp?";
/*     */    private static final String openTagA_memo = "<a class=\"section\" name=\"memo\" href=\"#memo\" title=\"";
/*     */    private static final String openTagImage = "<image src=\"/xbus/admin/images/memo.gif\" border=0 align=left></a>";
/*     */    private static final String openTagTable_details = "<br><br><table width=\"100%\" border=0 cellpadding=0 cellspacing=0>";
/*     */    private static final String openTagTrTdColspan = "<tr class=\"journal\"><td colspan=\"2\">";
/*     */    private static final String fullTagTrTdColspan = "<tr><td colspan=\"2\">&nbsp;&nbsp;</td></tr>";
/*     */    private static final String fullTagA_javasriptBack = "<tr class=\"journal\"><td colspan=2 align=left><a class=\"section\" href= \"javascript:history.back()\"><--- Back</a>";
/*     */    private static final String fullTagA_javascriptClose = "<tr class=\"journal\" ><td colspan=2 align=left><a class=\"section\" href= \"javascript:self.close()\"><--- Close</a>";
/*     */    private static final String fullTagTrTdColspanColor = "<tr bgcolor=\"#E7E9E6\"><td colspan=\"2\">&nbsp;&nbsp;</td></tr>";
/*     */    private static final String closeTagA_memoRequest = "request_memo\" onClick = \"openMessage('request','false','";
/*     */    private static final String closeTagA_memoResponse = "response_memo\" onClick = \"openMessage('response','false','";
/*     */    private static final String closeTagA_memoError = "error_memo\" onClick = \"openMessage('error','false','";
/*     */    private static final String closeTagA_messageId = "\" title=\"select to message_id\" target=\"haupt\">";
/*     */    private static final String closeTagA = "</a>";
/*     */    private static final String closeTagA_details = "\" title=\"go to the details\" target=\"haupt\">--></a>";
/*     */    private static final String closeTagP = "</p>";
/*     */    private static final String closeTag = ">";
/*     */    private static final String closeTagTd = "</td>";
/*     */    private static final String closeTagTdTr = "</td></tr>";
/*     */    private static final String closeTagA_memo = "')\">";
/*     */    private static final String closeTagTdTrTable = "</td></tr></table>";
/*     */    private static final String noData = "No entries found!";
/*     */    private static final String backspace = "&nbsp;";
/*     */    private String type;
/*     */    private String system;
/*     */    private String function;
/*     */    private String message_id;
/*     */    private String request_message;
/*     */    private String response_message;
/*     */    private String returncode;
/*     */    private String requestTimeMin;
/*     */    private String requestTimeMax;
/*     */    private String orderBy;
/*     */    private String sorting;
/*     */    private HashMap selectionMap = new HashMap();
/*     */ 
/*     */    public void setSelectionMap(String selection) {
/*  90 */       if ("leer".equals(selection)) {
/*     */ 
/*  92 */          this.selectionMap = new HashMap();
/*     */ 
/*     */ 
/*     */       } else {
/*  96 */          this.selectionMap = new HashMap();
/*  97 */          this.selectionMap.put("MessageId", selection);
/*     */       }
/*  99 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setSorting(String newSorting) {
/* 106 */       if (newSorting != null) {
/*     */ 
/* 108 */          this.sorting = newSorting;
/*     */ 
/*     */ 
/*     */       } else {
/* 112 */          this.sorting = "Descending";
/*     */       }
/* 114 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setOrderBy(String newOrderBy) {
/* 122 */       this.orderBy = newOrderBy;
/*     */ 
/* 124 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setType(String newType) {
/* 132 */       if (newType != null && !newType.equals("--------------------")) {
/*     */ 
/* 134 */          this.type = newType;
/* 135 */          this.selectionMap.put("Type", this.type);
/*     */       }
/* 137 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setSystem(String newSystem) {
/* 145 */       if (newSystem != null && !newSystem.equals("--------------------")) {
/*     */ 
/*     */ 
/* 148 */          this.system = newSystem;
/* 149 */          this.selectionMap.put("System", this.system);
/*     */       }
/* 151 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setFunction(String newFunction) {
/* 159 */       if (newFunction != null) {
/*     */ 
/* 161 */          this.function = newFunction;
/* 162 */          this.selectionMap.put("Function", this.function);
/*     */       }
/* 164 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setMessageId(String newMessageId) {
/* 172 */       if (newMessageId != null) {
/*     */ 
/* 174 */          this.message_id = newMessageId;
/* 175 */          this.selectionMap.put("MessageId", this.message_id);
/*     */       }
/* 177 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setRequest_message(String newRequest_message) {
/* 185 */       if (newRequest_message != null) {
/*     */ 
/* 187 */          this.request_message = newRequest_message;
/* 188 */          this.selectionMap.put("RequestMessage", this.request_message);
/*     */       }
/* 190 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setResponse_message(String newResponse_message) {
/* 198 */       if (newResponse_message != null) {
/*     */ 
/* 200 */          this.response_message = newResponse_message;
/* 201 */          this.selectionMap.put("ResponseMessage", this.response_message);
/*     */       }
/* 203 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setReturncode(String newReturncode) {
/* 211 */       if (newReturncode != null && !newReturncode.equals("--------------------")) {
/*     */ 
/*     */ 
/* 214 */          this.returncode = newReturncode;
/* 215 */          this.selectionMap.put("Returncode", this.returncode);
/*     */       }
/* 217 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setRequestTimeMin(String newRequestTimeMin) {
/* 225 */       if (newRequestTimeMin != null) {
/*     */ 
/* 227 */          this.requestTimeMin = newRequestTimeMin;
/* 228 */          this.selectionMap.put("RequestTimeMin", this.requestTimeMin);
/*     */       }
/* 230 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setRequestTimeMax(String newRequestTimeMax) {
/* 238 */       if (newRequestTimeMax != null) {
/*     */ 
/* 240 */          this.requestTimeMax = newRequestTimeMax;
/* 241 */          this.selectionMap.put("RequestTimeMax", this.requestTimeMax);
/*     */       }
/* 243 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setMessage(String newMessage) {
/* 251 */       this.message = newMessage;
/* 252 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setJournIndex(String newIndex) {
/* 259 */       this.journIndex = newIndex;
/* 260 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public void setDetails(String newDetails) {
/* 267 */       this.details = newDetails;
/* 268 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    private String replaceChar(String message) {
/* 275 */       StringBuffer messageBuffer = new StringBuffer();
/*     */ 
/* 277 */       for(int i = 0; i < message.length(); ++i) {
/*     */ 
/* 279 */          char messageChar = message.charAt(i);
/* 280 */          if (messageChar == '<') {
/*     */ 
/* 282 */             messageBuffer.append("&lt;");
/*     */ 
/* 284 */          } else if (messageChar == '>') {
/*     */ 
/* 286 */             messageBuffer.append("&gt;");
/*     */ 
/* 288 */          } else if (messageChar == '\'') {
/*     */ 
/* 290 */             messageBuffer.append("&quot;");
/*     */ 
/*     */ 
/*     */ 
/*     */          } else {
/* 295 */             messageBuffer.append(messageChar);
/*     */ 
/*     */          }
/*     */       }
/*     */ 
/* 300 */       return messageBuffer.toString();
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    private String checkColor(boolean newCollor) {
/* 308 */       if (newCollor) {
/*     */ 
/* 310 */          this.collor = false;
/* 311 */          return "#E7E9E6";
/*     */ 
/*     */ 
/*     */       } else {
/* 315 */          this.collor = true;
/* 316 */          return "white";
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
/*     */    public String getDataAsTableRows() throws XException {
/* 332 */       this.entries = ReadJournal.readSelected(this.selectionMap, this.orderBy, this.sorting);
/*     */ 
/* 334 */       StringBuffer dataBuffer = new StringBuffer();
/* 335 */       int index = 0;
/*     */ 
/* 337 */       this.collor = false;
/*     */ 
/* 339 */       if (!this.entries.iterator().hasNext()) {
/*     */ 
/* 341 */          dataBuffer.append("<tr><td>");
/* 342 */          dataBuffer.append("<p class=\"zwkopf\">");
/* 343 */          dataBuffer.append("No entries found!");
/* 344 */          dataBuffer.append("</p>");
/* 345 */          dataBuffer.append("</td></tr>");
/*     */ 
/*     */ 
/*     */       } else {
/* 349 */          dataBuffer.append(this.getCollumnsAsTableRows());
/*     */ 
/* 351 */          for(Iterator it = this.entries.iterator(); it.hasNext(); ++index) {
/*     */ 
/*     */ 
/* 354 */             ReadJournal journal = (ReadJournal)it.next();
/* 355 */             this.journalMap.put((new Integer(index)).toString(), journal);
/*     */ 
/* 357 */             dataBuffer.append(this.getJournalAsTableRows(journal, index));
/*     */ 
/*     */ 
/*     */          }
/*     */       }
/*     */ 
/* 363 */       this.entries.clear();
/* 364 */       return dataBuffer.toString();
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
/*     */    public String getJournalAsTableRows(ReadJournal journal, int index) {
/* 379 */       StringBuffer journalBuffer = new StringBuffer();
/*     */ 
/* 381 */       journalBuffer.append("<tr bgcolor=");
/* 382 */       journalBuffer.append(this.checkColor(this.collor));
/* 383 */       journalBuffer.append(">");
/* 384 */       journalBuffer.append("<td class=\"journal\">");
/* 385 */       journalBuffer.append("<a class = \"section\" href=\"MessagePage.jsp?");
/* 386 */       journalBuffer.append("message=leer&details=true&index=");
/* 387 */       journalBuffer.append(index);
/* 388 */       journalBuffer.append("\" title=\"go to the details\" target=\"haupt\">--></a>");
/*     */ 
/*     */ 
/* 391 */       journalBuffer.append("<td class=\"journal\">");
/* 392 */       journalBuffer.append((new Integer(journal.getNumber())).toString());
/* 393 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 396 */       journalBuffer.append("<td class=\"function\">");
/* 397 */       journalBuffer.append((new Character(journal.getType())).toString());
/* 398 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 401 */       journalBuffer.append("<td class=\"journal\">");
/* 402 */       journalBuffer.append(journal.getSystem());
/* 403 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 406 */       journalBuffer.append("<td class=\"function\">");
/*     */ 
/* 408 */       if (journal.getFunction() == null) {
/*     */ 
/* 410 */          journalBuffer.append("&nbsp;");
/*     */ 
/*     */ 
/*     */       } else {
/* 414 */          journalBuffer.append(journal.getFunction());
/*     */       }
/* 416 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/*     */ 
/* 420 */       journalBuffer.append("<td class=\"journal\">");
/* 421 */       journalBuffer.append("<a class = \"call\" href=\"MessageIdPage.jsp?messageId=");
/* 422 */       journalBuffer.append(journal.getMessageId());
/* 423 */       journalBuffer.append("\" title=\"select to message_id\" target=\"haupt\">");
/* 424 */       journalBuffer.append(journal.getMessageId());
/* 425 */       journalBuffer.append("</a>");
/* 426 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 429 */       journalBuffer.append("<td>");
/*     */ 
/* 431 */       if (journal.getRequestMessage() != null && !journal.getRequestMessage().equals("<null>")) {
/*     */ 
/*     */       } else {
/* 434 */          journalBuffer.append("&nbsp;");
/*     */ 
/*     */ 
/*     */ 
/* 438 */          journalBuffer.append("<a class=\"section\" name=\"memo\" href=\"#memo\" title=\"");
/* 439 */          journalBuffer.append("request_memo\" onClick = \"openMessage('request','false','");
/* 440 */          journalBuffer.append(index);
/* 441 */          journalBuffer.append("')\">");
/* 442 */          journalBuffer.append("<image src=\"/xbus/admin/images/memo.gif\" border=0 align=left></a>");
/*     */ 
/*     */       }
/*     */ 
/* 446 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 449 */       journalBuffer.append("<td class=\"journal\">");
/* 450 */       journalBuffer.append(journal.getRequestTimestamp());
/* 451 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 454 */       journalBuffer.append("<td>");
/*     */ 
/* 456 */       if (journal.getResponseMessage() != null && !journal.getResponseMessage().equals("<null>")) {
/*     */ 
/*     */       } else {
/* 459 */          journalBuffer.append("&nbsp;");
/*     */ 
/*     */ 
/*     */ 
/* 463 */          journalBuffer.append("<a class=\"section\" name=\"memo\" href=\"#memo\" title=\"");
/* 464 */          journalBuffer.append("response_memo\" onClick = \"openMessage('response','false','");
/* 465 */          journalBuffer.append(index);
/* 466 */          journalBuffer.append("')\">");
/* 467 */          journalBuffer.append("<image src=\"/xbus/admin/images/memo.gif\" border=0 align=left></a>");
/*     */ 
/*     */       }
/*     */ 
/* 471 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 474 */       journalBuffer.append("<td class=\"journal\">");
/* 475 */       journalBuffer.append(journal.getResponseTimestamp());
/* 476 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 479 */       journalBuffer.append("<td class=\"journal\">");
/* 480 */       journalBuffer.append(journal.getReturncode());
/* 481 */       journalBuffer.append("</td>");
/*     */ 
/*     */ 
/* 484 */       journalBuffer.append("<td>");
/* 485 */       if (journal.getErrormessage() != null && !journal.getErrormessage().equals("<null>")) {
/*     */ 
/*     */       } else {
/* 488 */          journalBuffer.append("&nbsp;");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 493 */          journalBuffer.append("<a class=\"section\" name=\"memo\" href=\"#memo\" title=\"");
/* 494 */          journalBuffer.append("error_memo\" onClick = \"openMessage('error','false','");
/* 495 */          journalBuffer.append(index);
/* 496 */          journalBuffer.append("')\">");
/* 497 */          journalBuffer.append("<image src=\"/xbus/admin/images/memo.gif\" border=0 align=left></a>");
/*     */ 
/*     */       }
/*     */ 
/* 501 */       journalBuffer.append("</td></tr>");
/*     */ 
/* 503 */       return journalBuffer.toString();
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
/*     */    public String getDetailsAsTable() {
/* 516 */       StringBuffer detailsBuffer = new StringBuffer();
/* 517 */       ReadJournal journal = (ReadJournal)this.journalMap.get(this.journIndex);
/*     */ 
/* 519 */       if (this.details.equals("true")) {
/*     */ 
/* 521 */          detailsBuffer.append("<br><br><table width=\"100%\" border=0 cellpadding=0 cellspacing=0>");
/* 522 */          detailsBuffer.append("<tr class=\"journal\"><td colspan=\"2\">");
/* 523 */          detailsBuffer.append("<h4>Details</h4>");
/* 524 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td colspan=\"2\">&nbsp;&nbsp;&nbsp;</td></tr>");
/*     */ 
/* 526 */          detailsBuffer.append("<tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">");
/* 527 */          detailsBuffer.append("Number:");
/* 528 */          detailsBuffer.append("</td><td>");
/* 529 */          detailsBuffer.append(journal.getNumber());
/* 530 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td width=\"160\" valign=\"top\">");
/* 531 */          detailsBuffer.append("Type:");
/* 532 */          detailsBuffer.append("</td><td>");
/* 533 */          detailsBuffer.append(journal.getType());
/* 534 */          detailsBuffer.append("</td></tr><tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">");
/* 535 */          detailsBuffer.append("System:");
/* 536 */          detailsBuffer.append("</td><td>");
/* 537 */          detailsBuffer.append(journal.getSystem());
/* 538 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td width=\"160\" valign=\"top\">");
/* 539 */          detailsBuffer.append("Function:");
/* 540 */          detailsBuffer.append("</td><td>");
/* 541 */          detailsBuffer.append(journal.getFunction());
/* 542 */          detailsBuffer.append("</td></tr><tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">");
/* 543 */          detailsBuffer.append("Message Id:");
/* 544 */          detailsBuffer.append("</td><td>");
/* 545 */          detailsBuffer.append(journal.getMessageId());
/* 546 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td width=\"160\" valign=\"top\">");
/* 547 */          detailsBuffer.append("Returncode:");
/* 548 */          detailsBuffer.append("</td><td>");
/* 549 */          detailsBuffer.append(journal.getReturncode());
/* 550 */          detailsBuffer.append("</td></tr><tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">");
/* 551 */          detailsBuffer.append("Request Timestamp:");
/* 552 */          detailsBuffer.append("</td><td>");
/* 553 */          detailsBuffer.append(journal.getRequestTimestamp());
/* 554 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td width=\"160\" valign=\"top\">");
/* 555 */          detailsBuffer.append("Request Message:");
/* 556 */          detailsBuffer.append("</p>");
/* 557 */          detailsBuffer.append("</td><td>");
/* 558 */          if (journal.getRequestMessage() != null) {
/*     */ 
/* 560 */             detailsBuffer.append("<PRE>");
/* 561 */             detailsBuffer.append(this.replaceChar(journal.getRequestMessage()));
/* 562 */             detailsBuffer.append("</PRE>");
/*     */ 
/*     */ 
/*     */          } else {
/* 566 */             detailsBuffer.append("null");
/*     */          }
/* 568 */          detailsBuffer.append("</td></tr><tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">");
/* 569 */          detailsBuffer.append("Response Timestamp:");
/* 570 */          detailsBuffer.append("</td><td>");
/* 571 */          detailsBuffer.append(journal.getResponseTimestamp());
/* 572 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td width=\"160\" valign=\"top\">");
/* 573 */          detailsBuffer.append("Response Message:");
/* 574 */          detailsBuffer.append("</p>");
/* 575 */          detailsBuffer.append("</td><td>");
/* 576 */          if (journal.getResponseMessage() != null) {
/*     */ 
/* 578 */             detailsBuffer.append("<PRE>");
/* 579 */             detailsBuffer.append(this.replaceChar(journal.getResponseMessage()));
/* 580 */             detailsBuffer.append("</PRE>");
/*     */ 
/*     */ 
/*     */          } else {
/* 584 */             detailsBuffer.append("null");
/*     */          }
/* 586 */          detailsBuffer.append("</td></tr><tr bgcolor=\"#E7E9E6\"><td width=\"160\" valign=\"top\">");
/* 587 */          detailsBuffer.append("Errorcode:");
/* 588 */          detailsBuffer.append("</td><td>");
/* 589 */          detailsBuffer.append(journal.getErrorcode());
/* 590 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td width=\"160\" valign=\"top\">");
/* 591 */          detailsBuffer.append("Errormessage:");
/* 592 */          detailsBuffer.append("</p>");
/* 593 */          detailsBuffer.append("</td><td>");
/* 594 */          if (journal.getErrormessage() != null) {
/*     */ 
/* 596 */             detailsBuffer.append(this.replaceChar(journal.getErrormessage()));
/*     */ 
/*     */ 
/*     */          } else {
/* 600 */             detailsBuffer.append("null");
/*     */          }
/* 602 */          detailsBuffer.append("</td></tr>");
/*     */ 
/* 604 */          detailsBuffer.append("<tr bgcolor=\"#E7E9E6\"><td colspan=\"2\">&nbsp;&nbsp;</td></tr>");
/* 605 */          detailsBuffer.append("<tr><td colspan=\"2\">&nbsp;&nbsp;</td></tr>");
/* 606 */          detailsBuffer.append("<tr class=\"journal\"><td colspan=2 align=left><a class=\"section\" href= \"javascript:history.back()\"><--- Back</a>");
/*     */ 
/* 608 */          detailsBuffer.append("</td></tr></table>");
/*     */ 
/*     */ 
/*     */ 
/* 612 */       } else if ("request".equals(this.message)) {
/*     */ 
/*     */ 
/* 615 */          detailsBuffer.append("<table width=\"400\" border=0 cellpadding=0 cellspacing=0><tr class=\"journal\"><td colspan=\"2\"><p class=\"zwkopf\">");
/* 616 */          detailsBuffer.append("Request Message");
/* 617 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td colspan=\"2\">&nbsp;&nbsp;&nbsp;</td></tr>");
/*     */ 
/* 619 */          detailsBuffer.append("<tr bgcolor=\"#E7E9E6\"><td width=\"10\">&nbsp;</td><td>");
/*     */ 
/* 621 */          if (journal.getRequestMessage() != null) {
/*     */ 
/* 623 */             detailsBuffer.append("<PRE>");
/* 624 */             detailsBuffer.append(this.replaceChar(journal.getRequestMessage()));
/* 625 */             detailsBuffer.append("</PRE>");
/*     */ 
/*     */ 
/*     */          } else {
/* 629 */             detailsBuffer.append("null");
/*     */          }
/* 631 */          detailsBuffer.append("</td></tr>");
/* 632 */          detailsBuffer.append("<tr><td colspan=\"2\">&nbsp;&nbsp;</td></tr>");
/* 633 */          detailsBuffer.append("<tr class=\"journal\" ><td colspan=2 align=left><a class=\"section\" href= \"javascript:self.close()\"><--- Close</a>");
/* 634 */          detailsBuffer.append("</td></tr></table>");
/*     */ 
/*     */ 
/* 637 */       } else if ("response".equals(this.message)) {
/*     */ 
/*     */ 
/* 640 */          detailsBuffer.append("<table width=\"400\" border=0 cellpadding=0 cellspacing=0><tr class=\"journal\"><td colspan=\"2\"><p class=\"zwkopf\">");
/* 641 */          detailsBuffer.append("Response Message");
/* 642 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td colspan=\"2\">&nbsp;&nbsp;&nbsp;</td></tr>");
/*     */ 
/* 644 */          detailsBuffer.append("<tr bgcolor=\"#E7E9E6\"><td width=\"10\">&nbsp;</td><td>");
/* 645 */          if (journal.getResponseMessage() != null) {
/*     */ 
/* 647 */             detailsBuffer.append("<PRE>");
/* 648 */             detailsBuffer.append(this.replaceChar(journal.getResponseMessage()));
/* 649 */             detailsBuffer.append("</PRE>");
/*     */ 
/*     */ 
/*     */          } else {
/* 653 */             detailsBuffer.append("null");
/*     */          }
/* 655 */          detailsBuffer.append("</td></tr>");
/* 656 */          detailsBuffer.append("<tr><td colspan=\"2\">&nbsp;&nbsp;</td></tr>");
/* 657 */          detailsBuffer.append("<tr class=\"journal\" ><td colspan=2 align=left><a class=\"section\" href= \"javascript:self.close()\"><--- Close</a>");
/* 658 */          detailsBuffer.append("</td></tr></table>");
/*     */ 
/*     */ 
/*     */ 
/* 662 */       } else if ("error".equals(this.message)) {
/*     */ 
/* 664 */          detailsBuffer.append("<table width=\"400\" border=0 cellpadding=0 cellspacing=0><tr class=\"journal\"><td colspan=\"2\"><p class=\"zwkopf\">");
/* 665 */          detailsBuffer.append("Errormessage");
/* 666 */          detailsBuffer.append("</td></tr><tr class=\"journal\"><td colspan=\"2\">&nbsp;&nbsp;&nbsp;</td></tr>");
/*     */ 
/* 668 */          detailsBuffer.append("<tr bgcolor=\"#E7E9E6\"><td width=\"10\">&nbsp;</td><td>");
/* 669 */          if (journal.getErrormessage() != null) {
/*     */ 
/* 671 */             detailsBuffer.append(this.replaceChar(journal.getErrormessage()));
/*     */ 
/*     */ 
/*     */          } else {
/* 675 */             detailsBuffer.append("null");
/*     */          }
/* 677 */          detailsBuffer.append("</td></tr>");
/* 678 */          detailsBuffer.append("<tr><td colspan=\"2\">&nbsp;&nbsp;</td></tr>");
/* 679 */          detailsBuffer.append("<tr class=\"journal\" ><td colspan=2 align=left><a class=\"section\" href= \"javascript:self.close()\"><--- Close</a>");
/* 680 */          detailsBuffer.append("</td></tr></table>");
/*     */ 
/*     */       }
/*     */ 
/* 684 */       return detailsBuffer.toString();
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getCollumnsAsTableRows() {
/* 695 */       StringBuffer columnNameBuffer = new StringBuffer();
/*     */ 
/* 697 */       columnNameBuffer.append("<tr align=\"center\" bgcolor=");
/* 698 */       columnNameBuffer.append("#D1D0C7");
/* 699 */       columnNameBuffer.append(">");
/* 700 */       columnNameBuffer.append("<td class=\"selection\">");
/* 701 */       columnNameBuffer.append("&nbsp;");
/* 702 */       columnNameBuffer.append("</td>");
/* 703 */       columnNameBuffer.append("<td class=\"selection\">");
/* 704 */       columnNameBuffer.append("No");
/* 705 */       columnNameBuffer.append("</td>");
/* 706 */       columnNameBuffer.append("<td class=\"selection\">");
/* 707 */       columnNameBuffer.append("Type");
/* 708 */       columnNameBuffer.append("</td>");
/* 709 */       columnNameBuffer.append("<td class=\"selection\">");
/* 710 */       columnNameBuffer.append("System");
/* 711 */       columnNameBuffer.append("</td>");
/* 712 */       columnNameBuffer.append("<td class=\"selection\">");
/* 713 */       columnNameBuffer.append("Function");
/* 714 */       columnNameBuffer.append("</td>");
/* 715 */       columnNameBuffer.append("<td class=\"selection\">");
/* 716 */       columnNameBuffer.append("Message Id");
/* 717 */       columnNameBuffer.append("</td>");
/* 718 */       columnNameBuffer.append("<td class=\"selection\">");
/* 719 */       columnNameBuffer.append("&nbsp;");
/* 720 */       columnNameBuffer.append("</td>");
/* 721 */       columnNameBuffer.append("<td class=\"selection\">");
/* 722 */       columnNameBuffer.append("Request Timestamp");
/* 723 */       columnNameBuffer.append("</td>");
/* 724 */       columnNameBuffer.append("<td class=\"selection\">");
/* 725 */       columnNameBuffer.append("&nbsp;");
/* 726 */       columnNameBuffer.append("</td>");
/* 727 */       columnNameBuffer.append("<td class=\"selection\">");
/* 728 */       columnNameBuffer.append("Response Timestamp");
/* 729 */       columnNameBuffer.append("</td>");
/* 730 */       columnNameBuffer.append("<td class=\"selection\">");
/* 731 */       columnNameBuffer.append("Returncode");
/* 732 */       columnNameBuffer.append("</td>");
/* 733 */       columnNameBuffer.append("<td class=\"selection\">");
/* 734 */       columnNameBuffer.append("&nbsp;");
/*     */ 
/* 736 */       columnNameBuffer.append("</td></tr>");
/*     */ 
/* 738 */       return columnNameBuffer.toString();
/*     */    }
}

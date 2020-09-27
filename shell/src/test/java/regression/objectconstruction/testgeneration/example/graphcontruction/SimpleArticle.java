package regression.objectconstruction.testgeneration.example.graphcontruction;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class SimpleArticle implements ArticleMeta, Serializable, Cloneable, ContentSetable {
  private static final long serialVersionUID = -1368796410854055279L;
  
  private String title = "";
  
  private String editSummary = "";
  
  private String text = "";
  
  private String editor = "";
  
  private boolean minorEdit = false;
  
  private Date editTimestamp = INIT_DATE;
  
  private String revId = "";
  
  public static final Date INIT_DATE = new Date(0L);
  
  public SimpleArticle() {}
  
  public SimpleArticle(ContentAccessable ca) {
    if (ca.getTitle() != null)
      this.title = ca.getTitle(); 
    if (ca.getText() != null)
      this.text = ca.getText(); 
    if (ca.getEditSummary() != null)
      this.editSummary = ca.getEditSummary(); 
    if (ca.getEditor() != null)
      this.editor = ca.getEditor(); 
    setMinorEdit(ca.isMinorEdit());
  }
  
//  public SimpleArticle(ArticleMeta sa) {
//    this(sa);
//    if (sa.getEditTimestamp() != null)
//      this.editTimestamp = sa.getEditTimestamp(); 
//    if (sa.getRevisionId() != null)
//      this.revId = sa.getRevisionId(); 
//  }
  
  public final Object clone() throws CloneNotSupportedException {
    super.clone();
    return new SimpleArticle(this);
  }
  
  @Deprecated
  public SimpleArticle(String text, String title) {
    this.text = text;
    this.title = title;
  }
  
  public SimpleArticle(String title) {
    this.title = title;
  }
  
  public String getEditSummary() {
    return this.editSummary;
  }
  
  public void setEditSummary(String s) {
    this.editSummary = s;
  }
  
  public boolean isMinorEdit() {
    return this.minorEdit;
  }
  
  public void setMinorEdit(boolean minor) {
    this.minorEdit = minor;
  }
  
  @Deprecated
  public String getLabel() {
    return getTitle();
  }
  
  public String getTitle() {
    return this.title;
  }
  
  @Deprecated
  public void setLabel(String label) {
    setTitle(label);
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getText() {
    return this.text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public void addText(String text) {
    setText(getText() + text);
  }
  
  public void addTextnl(String text) {
    setText(getText() + "\n" + text);
  }
  
  public String getEditor() {
    return this.editor;
  }
  
  public void setEditor(String editor) {
    this.editor = editor;
  }
  
  public boolean isRedirect() {
    Pattern pattern = Pattern.compile("#(.*)redirect(.*)", 2);
    if (pattern.matcher(this.text).matches())
      return true; 
    return false;
  }
  
  public Date getEditTimestamp() {
    return this.editTimestamp;
  }
  
  public void setEditTimestamp(String editTimestamp) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    try {
      setEditTimestamp(sdf.parse(editTimestamp));
    } catch (ParseException e) {
      sdf = new SimpleDateFormat("MM/dd/yy' 'HH:mm:ss");
      setEditTimestamp(sdf.parse(editTimestamp));
    } 
  }
  
  public void setEditTimestamp(Date d) {
    this.editTimestamp = d;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (!(obj instanceof SimpleArticle))
      return false; 
    SimpleArticle other = (SimpleArticle)obj;
    if (this.editTimestamp == null) {
      if (other.editTimestamp != null)
        return false; 
    } else if (!this.editTimestamp.equals(other.editTimestamp)) {
      return false;
    } 
    if (this.revId == null) {
      if (other.revId != null)
        return false; 
    } else if (!this.revId.equals(other.revId)) {
      return false;
    } 
    if (this.text == null) {
      if (other.text != null)
        return false; 
    } else if (!this.text.equals(other.text)) {
      return false;
    } 
    if (this.title == null) {
      if (other.title != null)
        return false; 
    } else if (!this.title.equals(other.title)) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + ((this.editTimestamp == null) ? 0 : this.editTimestamp.hashCode());
    result = 31 * result + ((this.revId == null) ? 0 : this.revId.hashCode());
    result = 31 * result + ((this.text == null) ? 0 : this.text.hashCode());
    result = 31 * result + ((this.title == null) ? 0 : this.title.hashCode());
    return result;
  }
  
  public String getRevisionId() {
    return this.revId;
  }
  
  public void setRevisionId(String revId) {
    this.revId = revId;
  }
}

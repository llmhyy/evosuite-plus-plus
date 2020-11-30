package feature.objectconstruction.testgeneration.example.graphcontruction;

import java.util.Date;

public interface ArticleMeta extends ContentAccessable {
  boolean isRedirect();
  
  Date getEditTimestamp();
  
  String getRevisionId();
}
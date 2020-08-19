package regression.objectconstruction.testgeneration.example.graphcontruction;

public interface WikiBot {
  Article readContent(String paramString) throws ActionException, ProcessException;
  
  Article readContent(String paramString, int paramInt) throws ActionException, ProcessException;
  
  SimpleArticle readData(String paramString, int paramInt) throws ActionException, ProcessException;
  
  SimpleArticle readData(String paramString) throws ActionException, ProcessException;
  
  void writeContent(SimpleArticle paramSimpleArticle) throws ActionException, ProcessException;
  
  void postDelete(String paramString) throws ActionException, ProcessException;
  
  void login(String paramString1, String paramString2) throws ActionException;
    
  String getWikiType();
  
  boolean hasCacheHandler();
}
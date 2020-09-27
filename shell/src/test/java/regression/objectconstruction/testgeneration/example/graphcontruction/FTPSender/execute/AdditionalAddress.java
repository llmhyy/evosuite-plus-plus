package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.util.List;

public interface AdditionalAddress {
  List getAddresses() throws XException;
  
  boolean hasMarker(String paramString) throws XException;
  
  String replaceMarker(String paramString1, String paramString2) throws XException;
  
  String getValue(String paramString1, String paramString2, String paramString3) throws XException;
}
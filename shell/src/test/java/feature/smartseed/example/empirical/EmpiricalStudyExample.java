package feature.smartseed.example.empirical;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class EmpiricalStudyExample {
	String[] lesSuffixes;
	String laDescription;
	private MenuItemList rootList;
	private ArrayList<Object> orphans;

	public HSSFWorkbook wb;
	private TemplateWorkbook tWorkbook;
	private TemplateSheet tSheet;

	// 8
	public boolean accept(File f) {
		String suffixe = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if ((i > 0) && (i < s.length() - 1)) {
			suffixe = s.substring(i + 1).toLowerCase();
		}
		return (suffixe != null) 
				&& (appartient(suffixe));
	}

	public boolean appartient(String suffixe) {
		for (int i = 0; i < lesSuffixes.length; i++) {
			if (suffixe.equals(lesSuffixes[i])) {
				return true;
			}
		}
		return false;
	}

	public void MonFilter(String[] lesSuffixes, String laDescription) {
		this.lesSuffixes = lesSuffixes;
		this.laDescription = laDescription;
	}

	// 72 addMenuItem
	public void addMenuItem(IMenuItem item, String name) {
		if (item == null) {
			return;
		}
		item.setName(name);
		if (item.getParent() == null) {
			addMenuItemToList(item, rootList);
		} else {
			MenuItemList parentlist = findParentList(item, rootList);
			if (parentlist == null) {

				orphans.add(item);
			} else {
				addMenuItemToList(item, parentlist);
			}
		}
	}

	private MenuItemList findParentList(IMenuItem item, MenuItemList parentList) {
		if (item.getParent() == null) {
			return null;
		}
		if ((parentList.getMenuItem() != null) && (parentList.getMenuItem().getContained().equals(item.getParent()))) {
			parentList.getMenuItem().setLeaf(false);
			return parentList;
		}
		Iterator<Object> children = parentList.getChildrenIterator();
		while (children.hasNext()) {
			MenuItemList childList = (MenuItemList) children.next();
			MenuItemList found = findParentList(item, childList);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private void addMenuItemToList(IMenuItem item, MenuItemList parentlist) {
		item.setIndex(parentlist.getChildrenSize());
		MenuItemList menulist = new MenuItemList(item);

		attachOrphans(menulist);
		parentlist.addChild(menulist);
	}

	private void attachOrphans(MenuItemList menulist) {
		for (int i = 0; i < orphans.size(); i++) {
			IMenuItem current = (IMenuItem) orphans.get(i);
			MenuItemList list = new MenuItemList(current);
			orphans.remove(i);
			if ((current.getParent() != null) && (current.getParent().equals(menulist.getMenuItem().getContained()))) {
				menulist.addChild(list);
			} else {
				rootList.addChild(list);
			}
			attachOrphans(list);
		}
	}
	// 3 addResource

	// 5 parse
	public TemplateWorkbook parse(Set<String> excludedSheetNames) {
		int nSheets = wb.getNumberOfSheets();
		for (int i = 0; i < nSheets; i++) {
			HSSFSheet sheet = wb.getSheetAt(i);
			String sheetName = wb.getSheetName(i);
			if (excludedSheetNames != null 
					&& !excludedSheetNames.contains(sheetName)) {
				System.currentTimeMillis();
			}
		}
		return tWorkbook;
	}

	// 26 loadInstructions
	public static boolean loadInstructions(String paramString) {
		int i = 0;
		if (paramString.isEmpty()) {
			System.currentTimeMillis();
		}
		if (paramString.equalsIgnoreCase("q")) {
			System.exit(0);
		}
		return true;
	}
	
	//5 chooseFontFamily
	public int chooseFontFamily(HSSFFont font, int defaultFontFamily)
	{
		String fontName = font.getFontName();
		int fontFamily = defaultFontFamily;
		if( "Arial".equals(fontName)) {	fontFamily = 1;	}
		else if( "Courier".equals(fontName)) {	fontFamily = 0;	}
		else if( "Courier New".equals(fontName)) {	fontFamily = 0;	}
		else if( "Times New Roman".equals(fontName)) {	fontFamily = 2;	}
		return fontFamily;
	}
	
	//8 setColumnClass
	public void setColumnClass(int posColonne, Class<?> type) {
		if (type.equals(String.class)) {
			System.currentTimeMillis();
		} else if (type.equals(Boolean.class)) {
			System.currentTimeMillis();
		} else {
			System.currentTimeMillis();
		}
	}
	
	public String[] columnNames;
	//13 addColumn
    public void addColumn(short ordinalPosition, String columnName) {
        int expectedPosition = columnNames.length + 1;
        if (ordinalPosition == expectedPosition)
        	System.currentTimeMillis();
        else {
        	if (ordinalPosition > expectedPosition || !columnNames[ordinalPosition - 1].equals(columnName))
        		throw new IllegalArgumentException("ordinalPosition is expected to be " + expectedPosition + ", " +
                    "found: " + ordinalPosition);
        }
    }
    
	public EmpiricalStudyExample(String[] columnNames, int ordinalPosition) {
		this.columnNames = columnNames;
		if (ordinalPosition != 1) {
			throw new IllegalArgumentException("ordinalPosition is expected to be 1, found: " + ordinalPosition);
		}
	}
    
    private static final String FILENAME = "org/databene/jdbacl/databene.db_dialect.properties";
	public static void getDialectForProduct(String productName) {
		String normalizedProductName = productName.toLowerCase().replace(' ', '_');
		Map<String, String> mappings = Config.readProperties(FILENAME);
		for (Map.Entry<String, String> entry : mappings.entrySet())
			if (normalizedProductName.contains(entry.getKey())) {
				System.currentTimeMillis();
			}
	}
	
//	public void getElement(String path) {
//		int i = path.indexOf('.');
//
//		if (i == 0) {
//			path = path.substring(1);
//			i = path.indexOf('.');
//		}
//		String subName;
//		String topName;
//		if (i > 0) {
//			topName = path.substring(0, i);
//			subName = path.substring(i + 1);
//		} else {
//			topName = path;
//			subName = null;
//		}
//
//		for (int j = 0; j < XmlElement.subElements.size(); j++) {
//			if (((XmlElement) XmlElement.subElements.get(j)).getName().equals(topName)) {
//				if (subName != null) {
//					return;
//				}
//				return;
//			}
//		}
//	}
//	
//	public void removeElement(XmlElement e) {
//		XmlElement child = null;
//
//		for (int i = 0; i < XmlElement.subElements.size(); i++) {
//			child = (XmlElement) XmlElement.subElements.get(i);
//
//			if (child == e) {
//				System.currentTimeMillis();
//			}
//		}
//	}
	
	public void stringReplaceAll(StringBuffer source, char token, String replacement) {
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == token) {
				System.currentTimeMillis();
			}
		}
	}
 
//	private Set<XmlElement> accounts;
//	public void getAuthenticationInfo(Principal subjectIdentity) throws IllegalAccessException, NoSuchMethodException,
//			InvocationTargetException, InstantiationException, ClassNotFoundException {
//		for (XmlElement entry : accounts) {
//			if (entry.getUsername().equals(subjectIdentity.getName())) {
//				System.currentTimeMillis();
//			}
//		}
//		return;
//	}
//	
//	public void setAccounts(Set<XmlElement> accounts) {
//		this.accounts = accounts;
//	}
	
	protected Set<AuthorizationModule> authorizationModules;
	public void isAuthorized(AuthorizedAction action) {

		for (AuthorizationModule module : authorizationModules) {
			
			if (module.supports(action)) {
				System.currentTimeMillis();
			}
		}
	}
	
}

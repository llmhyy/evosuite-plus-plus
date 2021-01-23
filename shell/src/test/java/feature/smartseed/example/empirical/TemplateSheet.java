package feature.smartseed.example.empirical;

import org.apache.poi.hssf.usermodel.HSSFSheet;

public class TemplateSheet {
	private final String sheetName;
	private final HSSFSheet sheet;
	public TemplateSheet(String sheetName,HSSFSheet sheet)
	{
		this.sheetName=sheetName;
		this.sheet = sheet;
	}
}

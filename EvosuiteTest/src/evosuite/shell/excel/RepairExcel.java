package evosuite.shell.excel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import evosuite.shell.experiment.SFConfiguration;

/**
 * 
 * @author lyly
 */
public class RepairExcel {
	
	@Test
	public void repair() throws IOException {
		String file = SFConfiguration.sfBenchmarkFolder + "/evoTest-reports/35_corina_evotest.xlsx";
		String newFile = SFConfiguration.sfBenchmarkFolder + "/evoTest-reports/35_corina_evotest_repair.xlsx";
		repair(file, newFile);
	}

	private void repair(String file, String repairFile) throws IOException {
		ExcelReader reader = new ExcelReader(new File(file), 0);
		List<List<Object>> data = reader.listData("data");
		List<String> headers = reader.listHeader("data");

		ExcelWriter writer = new ExcelWriter(new File(repairFile));
		writer.createSheet("data", headers.toArray(new String[headers.size()]), 0);
		writer.writeSheet("data", data );
		reader.close();
	}
}

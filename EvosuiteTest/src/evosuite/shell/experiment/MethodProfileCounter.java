package evosuite.shell.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.utils.CollectionUtil;
import org.junit.Test;

import evosuite.shell.FileUtils;
import evosuite.shell.FlagMethodProfilesFilter.Remarks;
import evosuite.shell.excel.ExcelReader;

public class MethodProfileCounter {
	
	@Test
	public void run() throws IOException {
//		String workingDir = System.getProperty("user.dir") + "/experiments/SF100/reports";
		String workingDir = "/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports";
		String reportFile = workingDir + "/methodProfiles.txt";
		File file = new File(workingDir + "/flagMethodProfiles.xlsx");
		ExcelReader reader = new ExcelReader(file, 0);
		List<RowData> data = toRowData(reader.listData("data"));
		
		Map<String, List<RowData>> dataMap = toMap(data);
		Map<Type, List<String>> statistics = new LinkedHashMap<>();
		System.out.println("size=" + dataMap.keySet().size());
		for (String methodId : dataMap.keySet()) {
			Type type = checkType(methodId, dataMap.get(methodId));
			CollectionUtil.getListInitIfEmpty(statistics, type).add(methodId);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Statistic: \n")
		.append("==================================================================\n");
		for (Type type : Type.values()) {
			Map<String, List<String>> targetMehods = getTargetMethodsByType(statistics, type);
			int total = 0;
			for (String key : targetMehods.keySet()) {
				total += targetMehods.get(key).size();
			}
			sb.append(type).append(":\t").append(total).append("\n");
		}
		sb.append("==================================================================\n");
		FileUtils.writeFile(reportFile, sb.toString(), false);
		for (Type type : Type.values()) {
			int total = 0;
			Map<String, List<String>> targetMehods = getTargetMethodsByType(statistics, type);
			sb = new StringBuilder();
			Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
			for (String projectName : projectFolders.keySet()) {
				String projectId = projectFolders.get(projectName).getName();
				sb	.append("#------------------------------------------------------------------------\n")
					.append("#Project=").append(projectName).append("  -  ").append(projectId).append("\n")
					.append("#------------------------------------------------------------------------\n");
				for (String method : CollectionUtil.nullToEmpty(targetMehods.get(projectName))) {
					sb.append(method).append("\n");
					total ++;
				}
			}
			sb.append("#-------- Total: ").append(total).append("--------");
			FileUtils.writeFile(workingDir + "/targetMethod_byType_" + type + ".txt", sb.toString(), false);
		}
	}
	
	private Map<String, List<String>> getTargetMethodsByType(Map<Type, List<String>> statistics, Type type) {
		switch (type) {
		case instrumentable:
			return mergeMap(getTargetMethodsByType(statistics, Type.interested), getTargetMethodsByType(statistics, Type.uninterested));
		case interested:
			return mergeMap(getTargetMethodsByType(statistics, Type.determined), getTargetMethodsByType(statistics, Type.undetermined));
		case uninstrumentable:
			return mergeMap(getTargetMethodsByType(statistics, Type.nocode), getTargetMethodsByType(statistics, Type.uninstrumentable_others));
		case determined:
			return mergeMap(getTargetMethodsByType(statistics, Type.primitive), getTargetMethodsByType(statistics, Type.not_primitive));
		default:
			break;
		}
		Map<String, List<String>> result = new HashMap<>();
		for (String method : CollectionUtil.nullToEmpty(statistics.get(type))) {
			int idx = method.indexOf("#");
			CollectionUtil.getListInitIfEmpty(result, method.substring(0, idx)).add(method.substring(idx + 1));
		}
		return result;
	}
	
	private Map<String, List<String>> mergeMap(Map<String, List<String>> map1, Map<String, List<String>> map2) {
		for (String key : map2.keySet()) {
			CollectionUtil.getListInitIfEmpty(map1, key).addAll(map2.get(key));
		}
		return map1;
	}

	private Type checkType(String methodId, List<RowData> list) {
		if (!isInstrumentable(list)) {
			if (hasNoSource(list)) {
				return Type.nocode;
			}
			return Type.uninstrumentable_others;
		}
		
		if (!isInterested(list)) {
			return Type.uninterested;
		}
		if (isDetermined(list)) {
			if (isPrimitive(list)) {
				return Type.primitive;
			}
			return Type.not_primitive;
		}
		return Type.undetermined;
	}
	
	private boolean isPrimitive(List<RowData> list) {
		for (RowData row : list) {
			if (row.primitive > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean isDetermined(List<RowData> list) {
		for (RowData row : list) {
			if (row.const01 > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean isInstrumentable(List<RowData> list) {
		for (RowData row : list) {
			if (!Remarks.UNINSTRUMENTABLE.getText().equals(row.remarks)
					&& !Remarks.NO_SOURCE.getText().equals(row.remarks)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasNoSource(List<RowData> list) {
		for (RowData row : list) {
			if (!Remarks.NO_SOURCE.getText().equals(row.remarks)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isInterested(List<RowData> list) {
		for (RowData row : list) {
			if (row.const01Branch > 0 || row.getFieldBranch > 0 || row.iloadBranch > 0) {
				return true;
			}
		}
		return false;
	}

	private static enum Type {
		uninstrumentable, 
			nocode,
			uninstrumentable_others,
		instrumentable,
			interested,
				determined,
					primitive,
					not_primitive,
				undetermined,
			uninterested,
	}
	
	private Map<String, List<RowData>> toMap(List<RowData> data) {
		Map<String, List<RowData>> map = new LinkedHashMap<String, List<RowData>>();
		for (RowData row : data) {
			CollectionUtil.getListInitIfEmpty(map, row.projectName + "#" + row.targetMethod).add(row);
		}
		int total = 0;
		for (List<RowData> values : map.values()) {
			total += values.size();
		}
		System.out.println(total);
		return map;
	}
	
	private List<RowData> toRowData(List<List<Object>> data) {
		List<RowData> rowData = new ArrayList<MethodProfileCounter.RowData>(data.size());
		for (List<Object> row : data) {
			RowData rData = new RowData();
			int idx = 0;
			rData.projectId = toString(row.get(idx ++));
			rData.projectName = toString(row.get(idx ++));
			rData.targetMethod = toString(row.get(idx ++));
			rData.flagMethod = toString(row.get(idx ++));
			rData.branch = toInt(row.get(idx ++));
			rData.const01 = toInt(row.get(idx ++));
			rData.const01Branch = toInt(row.get(idx ++));
			rData.getField = toInt(row.get(idx ++));
			rData.getFieldBranch = toInt(row.get(idx ++));
			rData.iload = toInt(row.get(idx ++));
			rData.iloadBranch = toInt(row.get(idx ++));
			rData.invokeMethods = toInt(row.get(idx ++));
			rData.others = toInt(row.get(idx ++));
			rData.remarks = toString(row.get(idx ++));
			rData.primitive = ((Boolean)row.get(idx++)) ? 1 : 0;
			rowData.add(rData);
		}
		return rowData;
	}
	
	private int toInt(Object obj) {
		if (obj == null) {
			return 0;
		}
		Number num = (Number) obj;
		return num.intValue();
	}
	
	private String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		return (String)obj;
	}
	
	private static class RowData {
		String projectId;
		String projectName;
		String targetMethod;
		String flagMethod;
		int branch;
		int const01;
		int const01Branch;
		int getField;
		int getFieldBranch;
		int iload;
		int iloadBranch;
		int invokeMethods;
		int others;
		String remarks;
		int primitive;
	}
}

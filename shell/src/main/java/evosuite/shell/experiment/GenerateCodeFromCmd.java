package evosuite.shell.experiment;

public class GenerateCodeFromCmd {

	public static void main(String[] args) {
		String cmd = "-inclusiveFile !TARGET_METHOD_FILE! -testLevel lMethod -target !PROJECT!.jar -iteration 3 -Dsearch_budget 100 -Dinstrument_context true -Dp_test_delete 0 -Dp_test_change 0.9 -Dp_test_insert 0.1 -Dp_change_parameter 0.1 -Dlocal_search_rate 3 -Dp_functional_mocking 0 -Dmock_if_no_generator false -Dfunctional_mocking_percent 0 -Dprimitive_reuse_probability 0 -Dmin_initial_tests 10 -Dmax_initial_tests 20 -Ddse_probability 0 -Dinstrument_libraries true -Dinstrument_parent true -Dmax_attempts 100 -Dassertions false -Delite 10 -Ddynamic_pool 0.0 -Dlocal_search_ensure_double_execution false -generateMOSuite -Dstrategy MOSUITE -Dalgorithm MOSA -criterion fbranch";
		StringBuilder sb = new StringBuilder("String[] args = new String[] {");
		String[] frags = cmd.split(" ");
		for (int i = 0; i < frags.length; i++) {
			if (frags[i].startsWith("-")) {
				sb.append("\n");
			}
			sb.append("\"").append(frags[i]).append("\", ");
		}
		System.out.println(sb);
 	}
}

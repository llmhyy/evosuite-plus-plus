//package feature.objectconstruction.testgeneration.testcase;
//
//import java.awt.Component;
//import java.awt.event.MouseEvent;
//import java.beans.VetoableChangeListener;
//import java.beans.VetoableChangeListenerProxy;
//import java.text.Collator;
//import java.text.RuleBasedCollator;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Locale;
//
//import javax.swing.JTable;
//import org.evosuite.runtime.mock.java.lang.MockThrowable;
//import org.exolab.jms.message.MessageId;
//import org.exolab.jms.net.uri.URI;
//import org.exolab.jms.net.uri.URI.MalformedURIException;
//import org.exolab.jms.net.uri.URIHelper;
//import org.exolab.jms.scheduler.Scheduler;
//import org.exolab.jms.scheduler.SerialTask;
//import org.exolab.jms.tranlog.ExternalXid;
//import org.junit.Test;
//
//import net.sf.xbus.admin.html.JournalBean;
//import net.sf.xbus.base.core.XException;
//import net.sourceforge.beanbin.search.index.cache.Properties;
//import net.sourceforge.beanbin.search.index.cache.Results;
//import com.browsersoft.openhre.hl7.impl.config.HL7DataTypeImpl;
//import com.browsersoft.openhre.hl7.impl.config.HL7SegmentImpl;
//import com.browsersoft.openhre.hl7.impl.parser.HL7CheckerStateImpl;
//import com.ib.client.ExecutionFilter;
//import com.objectmentors.patterns.SingletonConstructionException;
//import com.objectmentors.state.Event;
//import com.objectmentors.state.StartState;
//import com.objectmentors.state.Transition;
//import corina.Range;
//import corina.Sample;
//import corina.Year;
//import corina.editor.DecadalModel;
//import corina.manip.Truncate;
//import corina.map.MapPanel;
//import corina.map.View;
//import corina.map.tools.ToolBox;
//import corina.map.tools.ZoomInTool;
//import corina.prefs.components.ColorRenderer;
//import corina.site.SiteDB;
//import corina.util.SimpleLog;
//import de.paragon.explorer.model.AttributeModel;
//import de.paragon.explorer.model.AttributeModelComparator;
//import dk.statsbiblioteket.summa.common.util.CollatorFactory;
//
//public class InMethodNullPointerExceptionTest {
//	/*
//	 * null parameter (event0) passed to constructor
//	 */
//	@Test
//	public void omjstate_Transition_equals_1() throws SingletonConstructionException {
//		// I10 Branch 2 IFEQ L65;true
//		String string0 = ":A-A i7UG";
//		Event event0 = null;
//		StartState startState0 = new StartState();
//		Transition transition0 = new Transition(string0, event0, startState0, startState0);
//		String string1 = "D1K";
//		Transition transition1 = new Transition(string1, event0, startState0, startState0);
//		boolean boolean0 = transition0.equals(transition1);
//	}
//	
//	/*
//	 * L30: Properties props = (Properties)this.map.get(key);
//	 * This is null.
//	 */
//	@Test
//	public void beanbin_Results_clear_1() {
//		// I4 Branch 2 IFNULL L46;false
//		Results results0 = new Results();
//		Object object0 = null;
//		String string0 = "ta";
//		
//		// Manually added
//		Properties properties0 = new Properties();
//		results0.add(object0, properties0);
//		
//		results0.clear(object0, string0);
//	}
//	
//	/*
//	 * Some fields in executionFilter0, executionFilter1 are not set.
//	 */
//	@Test
//	public void tullibee_ExecutionFilter_equals_1() {
//		// I73 Branch 9 IFEQ L82;false
//		ExecutionFilter executionFilter0 = new ExecutionFilter();
//		ExecutionFilter executionFilter1 = new ExecutionFilter(executionFilter0.m_clientId, 
//				executionFilter0.m_side, 
//				executionFilter0.m_symbol,
//				executionFilter0.m_acctCode, 
//				executionFilter0.m_time, 
//				executionFilter0.m_symbol, 
//				executionFilter0.m_exchange
//		);
//		String string0 = "T,hn!i";
//		executionFilter0.m_side = string0;
//		ExecutionFilter executionFilter2 = new ExecutionFilter();
//		String string1 = "";
//		executionFilter2.m_acctCode = string1;
//		String string2 = "";
//		executionFilter1.m_acctCode = string2;
//		executionFilter0.m_exchange = string2;
//		ExecutionFilter executionFilter3 = new ExecutionFilter();
//		int int0 = 0;
//		executionFilter3.m_clientId = int0;
//		String string3 = "com/ib/client/ExecutionFilter#equals(Ljava/lang/Object;)Z";
//		executionFilter3.m_exchange = string3;
//		String string4 = "";
//		executionFilter0.m_acctCode = string4;
//		ExecutionFilter executionFilter4 = new ExecutionFilter();
//		int int1 = 4117;
//		String string5 = "|$ZAn(S<ce3Tl";
//		String string6 = "com.ib.client.ExecutionFiler";
//		String string7 = "";
//		String string8 = "";
//		String string9 = null;
//		String string10 = null;
//		ExecutionFilter executionFilter5 = new ExecutionFilter(int1, string5, string6, string7, string8, string9, string10);
//		String string11 = "o|vaC}";
//		executionFilter5.m_secType = string11;
//		ExecutionFilter executionFilter6 = new ExecutionFilter();
//		int int2 = 51;
//		String string12 = "y?\"*FuPQ4\\\\!JpUz";
//		String string13 = "8&3aus1b&|,:";
//		String string14 = "Z!>`BlQ4/";
//		String string15 = "WQmXx;?";
//		String string16 = "";
//		String string17 = "";
//		ExecutionFilter executionFilter7 = new ExecutionFilter(int2, string12, string13, string14, string15, string16, string17);
//		int int3 = 1598;
//		String string18 = "Rsdq;?A'o-u:+>l>GG~";
//		String string19 = "r";
//		String string20 = "<K^(";
//		String string21 = "";
//		String string22 = "J/'XtaU0W.>7R<3Ukky";
//		String string23 = "";
//		ExecutionFilter executionFilter8 = new ExecutionFilter(int3, string18, string19, string20, string21, string22, string23);
//		int int4 = 0;
//		String string24 = "";
//		String string25 = "";
//		String string26 = "Cg>O/0Bs>y";
//		String string27 = "+]";
//		String string28 = "DKdh4:Xv";
//		String string29 = "";
//		ExecutionFilter executionFilter9 = new ExecutionFilter(int4, string24, string25, string26, string27, string28, string29);
//		ExecutionFilter executionFilter10 = new ExecutionFilter();
//		boolean boolean0 = executionFilter0.equals(executionFilter1);
//	}
//	
//	/*
//	 * Some fields in executionFilter0, executionFilter1 are not set.
//	 */
//	@Test
//	public void tullibee_ExecutionFilter_equals_1_filtered() {
//		// I73 Branch 9 IFEQ L82;false
//		ExecutionFilter executionFilter0 = new ExecutionFilter();
//		ExecutionFilter executionFilter1 = new ExecutionFilter(executionFilter0.m_clientId, 
//				executionFilter0.m_side, 
//				executionFilter0.m_symbol,
//				executionFilter0.m_acctCode, 
//				executionFilter0.m_time, 
//				executionFilter0.m_symbol, 
//				executionFilter0.m_exchange
//		);
//		String string0 = "T,hn!i";
//		executionFilter0.m_side = string0;
//		String string2 = "";
//		executionFilter1.m_acctCode = string2;
//		executionFilter0.m_exchange = string2;
//		String string4 = "";
//		executionFilter0.m_acctCode = string4;
//		boolean boolean0 = executionFilter0.equals(executionFilter1);
//	}
//	
//	/*
//	 * Sample is null.
//	 * Wrong constructor used.
//	 */
//	@Test
//	public void corina_DecadalModel_setValueAt_1() {
//		// I7 Branch 28 IFNE L191;true
//		DecadalModel decadalModel0 = new DecadalModel();
//		String string0 = "bad offset to createPosition";
//		VetoableChangeListener vetoableChangeListener0 = null;
//		VetoableChangeListenerProxy vetoableChangeListenerProxy0 = new VetoableChangeListenerProxy(string0, vetoableChangeListener0);
//		int int0 = 1;
//		int int1 = 99;
//		boolean boolean0 = true;
//		Sample sample0 = decadalModel0.getSample();
//		decadalModel0.enableEditing(boolean0);
//		decadalModel0.setValueAt(vetoableChangeListenerProxy0, int0, int1);
//	}
//	
//	/*
//	 * Sample, Range are null.
//	 */
//	@Test
//	public void corina_Truncate_cropTo_1() {
//		// I51 Branch 2 IFLE L154;true
//		Sample sample0 = null;
////		Sample sample0 = new Sample();
//		Truncate truncate0 = new Truncate(sample0);
//		Range range0 = null;
////		Range range0 = new Range();
//		truncate0.cropTo(range0);
//	}
//	
//	/*
//	 * Missing crop call before uncrop.
//	 */
//	@Test
//	public void corina_Truncate_uncrop_1() {
//		// I7 Branch 9 IFNONNULL L266;true
//		Sample sample0 = new Sample();
//		Year year0 = sample0.getStart();
//		int int0 = (-1795);
//		Range range0 = new Range(year0, int0);
//		sample0.range = range0;
//		Truncate truncate0 = new Truncate(sample0);
////		truncate0.cropTo(range0);
//		truncate0.uncrop();
//	}
//	
//	/*
//	 * Null parameter.
//	 */
//	@Test
//	public void corina_ZoomInTool_mouseDragged_1() {
//		// I51 Branch 2 IFLE L154;false
//		byte[] byteArray0 = new byte[7];
//		byte byte0 = (byte)107;
//		byteArray0[0] = byte0;
////		boolean boolean0 = FileSystemHandling.shouldAllThrowIOExceptions();
//		MapPanel mapPanel0 = null;
//		View view0 = new View();
//		ToolBox toolBox0 = null;
//		ZoomInTool zoomInTool0 = new ZoomInTool(mapPanel0, view0, toolBox0);
//		MouseEvent mouseEvent0 = null;
//		zoomInTool0.mouseDragged(mouseEvent0);
//	}
//	
//	/*
//	 * Null parameter passed in.
//	 */
//	@Test
//	public void corina_ColorRenderer_getTableCellRendererComponent_1() {
//		// I179 Branch 4 IFNE L178;true
//		boolean boolean0 = true;
//		ColorRenderer colorRenderer0 = new ColorRenderer(boolean0);
//		JTable jTable0 = new JTable();
//		Object object0 = null;
//		boolean boolean1 = false;
//		boolean boolean2 = false;
//		int int0 = 2114;
//		int int1 = 0;
//		Component component0 = colorRenderer0.getTableCellRendererComponent(jTable0, object0, boolean1, boolean2, int0, int1);
//	}
//	
//	/*
//	 * Singleton pattern, complicated.
//	 */
//	@Test
//	public void corina_SiteDB_getSiteNames_1() {
//		// I41 Branch 30 IF_ICMPNE L200;false
//		SiteDB siteDB0 = new SiteDB();
//		List<Object> list0 = siteDB0.getSiteNames();
//	}
//	
//	/*
//	 * Null parameter passed to constructor.
//	 */
//	@Test
//	public void corina_SimpleLog_warn_1() {
//		// I7 Branch 35 IF_ICMPGT L220;true
//		String string0 = null;
////		String string0 = "";
//		SimpleLog simpleLog0 = new SimpleLog(string0);
//		ClassLoader classLoader0 = ClassLoader.getSystemClassLoader();
//		String string1 = "y\\\\^~d]I";
//		String string2 = "fd3hd=MT8";
//		MockThrowable mockThrowable0 = new MockThrowable();
//		SecurityException securityException0 = new SecurityException(string2, mockThrowable0);
//		SecurityException securityException1 = new SecurityException(securityException0);
//		MockThrowable mockThrowable1 = new MockThrowable(string1, securityException1);
//		simpleLog0.warn((Object) classLoader0, (Throwable) mockThrowable1);
//	}
//	
//	/*
//	 * Null parameter passed to method.
//	 */
//	@Test
//	public void summa_CollatorFactory_adjustAASorting_1() {
//		// I22 Branch 4 IFLE L279;true
//		Collator collator0 = null;
//		Locale locale0 = Locale.GERMAN;
//		Locale locale1 = Locale.TAIWAN;
//		String string0 = locale1.getDisplayCountry();
//		String string1 = locale0.getDisplayName(locale1);
//		String string2 = "5.& Y8'~@X,N5]";
//		boolean boolean0 = true;
//		RuleBasedCollator ruleBasedCollator0 = (RuleBasedCollator)CollatorFactory.createCollator(locale0, string2, boolean0);
//		Comparator<String> comparator0 = CollatorFactory.wrapCollator(collator0);
//		boolean boolean1 = false;
//		RuleBasedCollator ruleBasedCollator1 = (RuleBasedCollator)CollatorFactory.createCollator(locale1, boolean1);
//		Collator collator1 = CollatorFactory.adjustAASorting(collator0);
//	}
//	
//	/*
//	 * Attributes not set.
//	 */
//	@Test
//	public void objectexplorer_AttributeModelComparator_compare_1() {
//		// I30 Branch 2 IF_ICMPLT L56;true
//		AttributeModelComparator attributeModelComparator0 = new AttributeModelComparator();
//		AttributeModel attributeModel0 = new AttributeModel();
//		int int0 = attributeModelComparator0.compare(attributeModel0, attributeModel0);
//	}
//	
//	/*
//	 * Wrong constructor.
//	 */
//	@Test
//	public void openjms_MessageId_equals_1() {
//		// I36 Branch 6 IFLE L121;true
//		MessageId messageId0 = new MessageId();
//		MessageId messageId1 = new MessageId();
//		MessageId messageId2 = new MessageId();
//		boolean boolean0 = messageId0.equals(messageId1);
//	}
//	
//	/*
//	 * Null parameter passed to constructor.
//	 */
//	@Test
//	public void openjms_SerialTask_stop_1() throws InterruptedException { 
//		// I3 Branch 14 IFNONNULL L256;true
//		Runnable runnable0 = null;
//		Scheduler scheduler0 = null;
//		SerialTask serialTask0 = new SerialTask(runnable0, scheduler0);
//		long long0 = 0L;
////		System.setCurrentTimeMillis(long0);
//		serialTask0.stop();
//		boolean boolean0 = serialTask0.schedule();
//	}
//	
//	/*
//	 * Wrong constructor used (_global is null)
//	 */
//	@Test
//	public void openjms_ExternalXid_equals_1() { 
//		// I10 Branch 19 IF_ICMPLE L371;true
//		byte[] byteArray0 = new byte[9];
//		byte byte0 = (byte)34;
//		byteArray0[0] = byte0;
//		byte byte1 = (byte)16;
//		byteArray0[1] = byte1;
//		byteArray0[2] = byte0;
//		byteArray0[3] = byteArray0[0];
//		byte byte2 = (byte)117;
//		byteArray0[3] = byte2;
//		byteArray0[5] = byteArray0[3];
//		byte byte3 = (byte) (-26);
//		byteArray0[5] = byte3;
//		ExternalXid externalXid0 = new ExternalXid();
//		ExternalXid externalXid1 = new ExternalXid();
//		ExternalXid externalXid2 = new ExternalXid(externalXid1);
//		boolean boolean0 = externalXid0.equals(externalXid2);
//	}
//	
//	/*
//	 * Null passed into method.
//	 */
//	@Test
//	public void openjms_URIHelper_getURISansQuery_1() {
//		// I53 Branch 14 IFNONNULL L172;false
//		URI uRI0 = null;
//		URI uRI1 = URIHelper.getURISansQuery(uRI0);
//	}
//	
//	@Test
//	public void openjms_URIHelper_getURISansQuery_2() throws MalformedURIException {
//		// I53 Branch 14 IFNONNULL L172;false
//		URI uRI0 = null;
//		String string0 = "AKE2";
//		URI uRI1 = new URI(string0, string0);
//		URI uRI2 = URIHelper.getURISansQuery(uRI0);
////		URI uRI2 = URIHelper.getURISansQuery(uRI1);
//	}
//	
//	/*
//	 * Handler is null. Complicated case?
//	 */
//	@Test
//	public void openhre_HL7CheckerStateImpl_eventBeginField_1() {
//		// I7 Branch 17 IFNE L109;true
//		HL7CheckerStateImpl hL7CheckerStateImpl0 = new HL7CheckerStateImpl();
//		HL7DataTypeImpl hL7DataTypeImpl0 = new HL7DataTypeImpl();
//		hL7CheckerStateImpl0.setActualSubComponentDataType(hL7DataTypeImpl0);
//		boolean boolean0 = false;
//		int int0 = hL7CheckerStateImpl0.getActualDataTypeSubPartsPossition();
//		hL7CheckerStateImpl0.resetBeginSegment();
//		HL7SegmentImpl hL7SegmentImpl0 = new HL7SegmentImpl();
//		hL7CheckerStateImpl0.setActualSegment(hL7SegmentImpl0);
//		hL7CheckerStateImpl0.resetBeginRepeatableField();
//		int int1 = (-630);
//		boolean boolean1 = true;
//		hL7CheckerStateImpl0.setSeriousError(boolean1);
//		hL7CheckerStateImpl0.addToActualFieldLength(int1);
//		int int2 = hL7CheckerStateImpl0.getActualDataTypeSubPartsPossition();
//		hL7CheckerStateImpl0.eventBeginField(boolean0);		
//	}
//	
//	/*
//	 * ReadJournal journal = (ReadJournal)this.journalMap.get(this.journIndex);
//	 * journal is null
//	 * 
//	 * complicated case
//	 */
//	@Test
//	public void xbus_JournalBean_getDetailsAsTable_1() throws XException {
//		// I811 Branch 36 IFNULL L645;true
//		JournalBean journalBean0 = new JournalBean();
//		JournalBean journalBean1 = new JournalBean();
//		String string0 = "#";
//		journalBean1.setJournIndex(string0);
//		String string1 = "tpue";
//		journalBean1.setDetails(string1);
//		String string2 = "response";
//		journalBean1.setMessage(string2);
////		journalBean1.setOrderBy("Number");
////		journalBean1.setSorting("Descending");
////		journalBean1.getDataAsTableRows();
//		String string3 = journalBean1.getDetailsAsTable();
//	}
//}
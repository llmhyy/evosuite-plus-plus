package feature.objectconstruction.testgeneration.testcase;

import java.beans.EventSetDescriptor;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Consumer;

import javax.imageio.metadata.IIOMetadataNode;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButtonMenuItem;

import org.junit.Test;
import org.w3c.dom.Node;

//import java.awt.Graphics2D;
//import java.awt.Point;
//import java.awt.Rectangle;
//
//import org.jfree.chart.annotations.CategoryPointerAnnotation;
//import org.jfree.chart.axis.CategoryAxis3D;
//import org.jfree.chart.axis.DateAxis;
//import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import dk.statsbiblioteket.summa.common.util.CollatorFactory;
import net.sf.xbus.admin.html.JournalBean;

import com.lts.application.ApplicationException;
import com.lts.application.data.ApplicationData;
//import net.sf.xisemele.impl.OperationsHelperImpl; // Type is not visible
import com.lts.application.data.coll.ADCAdaptor;
import com.lts.application.data.coll.ADCEvent;
import com.lts.application.data.coll.ADCListenerAdaptor;
import com.lts.util.TreeNode;
import com.lts.util.deepcopy.DeepCopier;
import com.lts.util.deepcopy.DeepCopyException;

import weka.core.BinarySparseInstance;
import weka.core.Instances;
import weka.gui.beans.Appender;
import weka.gui.beans.BeanInstance;
import weka.gui.beans.DataSetEvent;
import weka.gui.beans.DataSourceListener;
import weka.gui.beans.FlowByExpression;
import weka.gui.beans.ImageSaver;
import weka.gui.beans.InstanceEvent;
import weka.gui.beans.MetaBean;
import weka.gui.beans.ScatterPlotMatrix;

public class NullPointerExceptionTest {
	
	@Test
	public void sanityCheck() {
		return;
	}

	/*
	 * JUnit fails whenever I import jfreechart. Why?
	 */
//	@Test
//	public void jfreechart_draw() {
//		CategoryPointerAnnotation categoryPointerAnnotation0 = new CategoryPointerAnnotation("//org/jfree/base/jcommon.properties", "//org/jfree/base/jcommon.properties", 3129.7920286612, 2272.101440485533);
//		CombinedRangeCategoryPlot combinedRangeCategoryPlot0 = new CombinedRangeCategoryPlot();
//		Point point0 = new Point(10, 10);
//		Rectangle rectangle0 = new Rectangle();
//		CategoryAxis3D categoryAxis3D0 = new CategoryAxis3D("//org/jfree/base/jcommon.properties");
//		DateAxis dateAxis0 = new DateAxis();
//		categoryPointerAnnotation0.draw((Graphics2D) null, combinedRangeCategoryPlot0, rectangle0, categoryAxis3D0, dateAxis0);
//	}
	
	/*
	 * Not sure what is going on here.
	 * Why is EvoObj passing null to the method?
	 */
	@Test
	public void summa_adjustAASorting() throws ParseException {
		Collator collator0 = null;
		Locale locale0 = Locale.KOREA;
		boolean boolean0 = false;
		RuleBasedCollator ruleBasedCollator0 = (RuleBasedCollator)CollatorFactory.createCollator(locale0, boolean0);
		Collator collator1 = CollatorFactory.adjustAASorting(collator0); // Why collator0 and not ruleBasedCollator0?
//		Collator collator1 = CollatorFactory.adjustAASorting(ruleBasedCollator0); // Works fine
	}
	
	/*
	 * No way to put key-value pairs in journalMap.
	 */
	@Test
	public void xbus_getDetailsAsTable() {
		JournalBean journalBean0 = new JournalBean();
		String string0 = "request";
		journalBean0.setDetails(string0);
		journalBean0.setMessage(string0);
		String string1 = journalBean0.getDetailsAsTable();
	}
	
	/*
	 * Type not visible.
	 */
//	@Test
//	public void xisemele_children() {
//		OperationsHelperImpl operationsHelperImpl0 = new OperationsHelperImpl();
//		IIOMetadataNode iIOMetadataNode0 = new IIOMetadataNode();
//		IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode();
//		IIOMetadataNode iIOMetadataNode2 = (IIOMetadataNode)iIOMetadataNode0.insertBefore(iIOMetadataNode1, iIOMetadataNode1);
//		IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode();
//		IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode();
//		LinkedList<Node> linkedList0 = new LinkedList<Node>();
//		IIOMetadataNode iIOMetadataNode5 = (IIOMetadataNode)iIOMetadataNode1.appendChild(iIOMetadataNode2);
//		String string0 = iIOMetadataNode0.getTagName();
//		LinkedList<Node> linkedList1 = new LinkedList<Node>();
//		LinkedList<Node> linkedList2 = new LinkedList<Node>();
//		Node node0 = iIOMetadataNode1.getNextSibling();
//		List<Node> list0 = operationsHelperImpl0.children(iIOMetadataNode2);
//		List<Node> list1 = operationsHelperImpl0.children(iIOMetadataNode4);
//		List<Node> list2 = operationsHelperImpl0.children(iIOMetadataNode0);
//	}
	
	/*
	 * Not sure what is happening here either.
	 */
//	@Test
//	public void caloriecount_deepCopyData() throws DeepCopyException, ApplicationException {
//		ADCAdaptor aDCAdaptor0 = new ADCAdaptor();
//		TreeNode treeNode0 = new TreeNode();
//		HashMap<String, TreeNode> hashMap0 = new HashMap<String, TreeNode>();
//		LinkedHashSet<ApplicationData> linkedHashSet0 = new LinkedHashSet<ApplicationData>();
//		aDCAdaptor0.myElements = (Collection<ApplicationData>) linkedHashSet0;
//		boolean boolean0 = true;
//		boolean boolean1 = linkedHashSet0.retainAll(aDCAdaptor0.myElements);
//		Iterator<ApplicationData> iterator0 = aDCAdaptor0.iterator();
//		String[] stringArray0 = new String[5];
//		hashMap0.clear();
//		String string0 = "I-23)s";
//		boolean boolean2 = aDCAdaptor0.isDirty();
//		aDCAdaptor0.initialize(linkedHashSet0);
//		TreeNode treeNode1 = new TreeNode(treeNode0);
//		int int0 = linkedHashSet0.size();
//		stringArray0[0] = string0;
//		LinkedList<TreeNode> linkedList0 = new LinkedList<TreeNode>();
//		treeNode0.setChildren(aDCAdaptor0);
//		ADCAdaptor aDCAdaptor1 = new ADCAdaptor();
//		TreeNode treeNode2 = new TreeNode();
//		treeNode0.updateFrom(treeNode2);
//		String[] stringArray1 = aDCAdaptor0.toArray(stringArray0);
//		aDCAdaptor0.clear();
//		boolean boolean3 = linkedHashSet0.isEmpty();
//		Object[] objectArray0 = aDCAdaptor0.toArray((Object[]) stringArray0);
//		ADCListenerAdaptor aDCListenerAdaptor0 = new ADCListenerAdaptor();
//		boolean boolean4 = true;
//		ADCAdaptor aDCAdaptor2 = (ADCAdaptor)aDCAdaptor0.deepCopy(boolean4);
//		ADCEvent aDCEvent0 = new ADCEvent();
//		Consumer<Object> consumer0 = null;
//		aDCAdaptor2.forEach(consumer0);
//		aDCEvent0.element = (ApplicationData) aDCAdaptor2;
//		aDCEvent0.element = (ApplicationData) aDCAdaptor2;
//		aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//		aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//		boolean boolean5 = aDCAdaptor0.add((ApplicationData) aDCAdaptor1);
//		aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//		aDCEvent0.element.postDeserialize();
//		boolean boolean6 = aDCAdaptor1.isEmpty();
//		Iterator<ApplicationData> iterator1 = aDCAdaptor0.iterator();
//		Integer integer0 = new Integer(int0);
//		int int1 = aDCAdaptor0.size();
//		aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//		aDCAdaptor0.addADCListener(aDCListenerAdaptor0);
//		linkedHashSet0.clear();
//		boolean boolean7 = false;
//		aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//		DeepCopier deepCopier0 = aDCAdaptor0.continueDeepCopy(hashMap0, boolean7);
//		Consumer<Object> consumer1 = null;
//		boolean boolean8 = aDCAdaptor0.equals(aDCEvent0.element);
//		aDCAdaptor2.forEach(consumer1);
//		Object object0 = aDCAdaptor2.deepCopy(boolean7);
//		boolean boolean9 = false;
//		aDCEvent0.element.deepCopyData(object0, hashMap0, boolean9);
//		boolean boolean10 = aDCAdaptor0.removeAll(linkedList0);
//		Object[] objectArray1 = aDCAdaptor1.toArray();
//		boolean boolean11 = aDCAdaptor0.myDirty;
//		Integer integer1 = new Integer(int0);
//		Object[] objectArray2 = linkedList0.toArray();
//		TreeNode treeNode3 = new TreeNode(aDCAdaptor0.myElements);
//		Object object1 = new Object();
//		aDCAdaptor1.update(aDCAdaptor0);
//		aDCAdaptor0.postDeserialize();
//		aDCAdaptor0.addADCListener(aDCListenerAdaptor0);
//		aDCAdaptor0.clear();
//		aDCAdaptor0.update(aDCEvent0.element);
//		boolean boolean12 = aDCAdaptor1.remove(aDCAdaptor0.myElements);
//		boolean boolean13 = linkedHashSet0.containsAll(aDCAdaptor1);
//		Object[] objectArray3 = aDCAdaptor1.toArray();
//		boolean boolean14 = aDCAdaptor0.equals(linkedList0);
//		boolean boolean15 = aDCAdaptor0.retainAll(aDCAdaptor0.myElements);
//		Object[] objectArray4 = aDCAdaptor2.toArray();
//		boolean boolean16 = aDCAdaptor0.isEmpty();
//		ADCEvent.EventType aDCEvent_EventType0 = ADCEvent.EventType.all;
//		aDCEvent0.event = aDCEvent_EventType0;
//		boolean boolean17 = aDCAdaptor0.equals(linkedList0);
//		ADCListenerAdaptor aDCListenerAdaptor1 = new ADCListenerAdaptor();
//		aDCListenerAdaptor1.eventOccurred(aDCEvent0);
//		aDCListenerAdaptor1.eventOccurred(aDCEvent0);
//		aDCAdaptor0.addADCListener(aDCListenerAdaptor1);
//		ADCAdaptor aDCAdaptor3 = new ADCAdaptor();
//		boolean boolean18 = aDCAdaptor1.contains(aDCAdaptor3);
//		aDCEvent0.element = (ApplicationData) aDCAdaptor0;
//		Object object2 = aDCAdaptor0.deepCopy(boolean5);
//		TreeNode treeNode4 = new TreeNode(treeNode1);
//		aDCAdaptor1.initialize(aDCAdaptor0);
//		aDCAdaptor1.postDeserialize();
//		boolean boolean19 = aDCAdaptor3.isEmpty();
//		boolean boolean20 = aDCAdaptor0.add(aDCEvent0.element);
//		aDCAdaptor3.myDirty = boolean1;
//		TreeNode treeNode5 = new TreeNode();
//		aDCAdaptor1.initialize(linkedHashSet0);
//		Object object3 = aDCAdaptor3.deepCopy();
//		boolean boolean21 = false;
//		aDCAdaptor3.setDirty(boolean21);
//		boolean boolean22 = aDCAdaptor1.isDirty();
//		boolean boolean23 = aDCAdaptor0.remove(aDCAdaptor2);
//		boolean boolean24 = aDCAdaptor2.myDirty;
//		aDCAdaptor2.clear();
//		aDCAdaptor0.clear();
//		ADCListenerAdaptor aDCListenerAdaptor2 = new ADCListenerAdaptor();
//		boolean boolean25 = aDCAdaptor1.removeADCListener(aDCListenerAdaptor2);
//		boolean boolean26 = aDCAdaptor1.containsAll(linkedHashSet0);
//		boolean boolean27 = aDCAdaptor1.equals(deepCopier0);
//		aDCAdaptor0.deepCopyData(treeNode0, hashMap0, boolean0);
//	}
	
//	// Method not visible
//	@Test
//	public void itr0_weka_renderOffScreenImage_L129_true() {
//		ScatterPlotMatrix scatterPlotMatrix0 = new ScatterPlotMatrix();
//		String string0 = "@hZ+XTb?T";
//		JRadioButtonMenuItem jRadioButtonMenuItem0 = new JRadioButtonMenuItem(string0);
//		Instances instances0 = null;
//		DataSetEvent dataSetEvent0 = new DataSetEvent(jRadioButtonMenuItem0, instances0);
//		ImageSaver imageSaver0 = new ImageSaver();
//		scatterPlotMatrix0.addImageListener(imageSaver0);
//		String string1 = "CREATE UNIQUE INDEX Key_IDX ON ";
//		scatterPlotMatrix0.setOffscreenXAxis(string1);
//		String string2 = "t)kA/OMr";
//		scatterPlotMatrix0.setOffscreenXAxis(string2);
//		scatterPlotMatrix0.renderOffscreenImage(dataSetEvent0);
//	}
	
	/*
	 * NPE caused by this line (L1325):
	 * 
	 * ((InstanceListener)this.m_downstream[this.m_indexOfTrueStep]).acceptInstance(this.m_ie);
	 * 
	 * - this.m_downstream is null
	 * - set in addDataSourceListener
	 */
	@Test
	public void itr0_weka_acceptInstance_I34_B70_false() {
		FlowByExpression flowByExpression0 = new FlowByExpression();
		Object object0 = flowByExpression0.getTreeLock();
		int int0 = 5362;
		BinarySparseInstance binarySparseInstance0 = new BinarySparseInstance(int0);
		int int1 = 919;
		InstanceEvent instanceEvent0 = new InstanceEvent(object0, binarySparseInstance0, int1);
//		flowByExpression0.addDataSourceListener(new Appender()); // Manually added
		flowByExpression0.acceptInstance(instanceEvent0);
	}
	
	/*
	 * Same case as above (itr0_weka_acceptInstace_I34_B70_false)
	 */
	@Test
	public void itr0_weka_acceptInstance_I54_B71_true() {
		FlowByExpression flowByExpression0 = new FlowByExpression();
		Object object0 = flowByExpression0.getTreeLock();
		int int0 = 4064;
		BinarySparseInstance binarySparseInstance0 = new BinarySparseInstance(int0);
		int int1 = 2322;
		InstanceEvent instanceEvent0 = new InstanceEvent(object0, binarySparseInstance0, int1);
		flowByExpression0.addDataSourceListener(new Appender());
		flowByExpression0.acceptInstance(instanceEvent0);
	}
	
	// Again we have a null value passed to the method.
	@Test
	public void itr1_weka_getSuitableTargets_I85_B79_true() {
		MetaBean metaBean0 = new MetaBean();
		String string0 = "!5!duf1s+z]";
		int int0 = 57;
		int int1 = 266;
		Integer[] integerArray0 = new Integer[5];
		int int2 = (-1435);
		Integer integer0 = new Integer(int2);
		integerArray0[0] = integer0;
		Integer integer1 = JLayeredPane.DEFAULT_LAYER;
		integerArray0[1] = integer1;
		Integer integer2 = JLayeredPane.POPUP_LAYER;
		integerArray0[2] = integer2;
		Integer integer3 = JLayeredPane.POPUP_LAYER;
		integerArray0[3] = integer3;
		Integer integer4 = JLayeredPane.POPUP_LAYER;
		integerArray0[4] = integer4;
		BeanInstance beanInstance0 = new BeanInstance((JComponent) metaBean0, string0, int0, int1, integerArray0);
		boolean boolean0 = false;
		metaBean0.shiftBeans(beanInstance0, boolean0);
		Vector<Object> vector0 = metaBean0.getBeansInSubFlow();
		EventSetDescriptor eventSetDescriptor0 = null;
		Vector<BeanInstance> vector1 = metaBean0.getSuitableTargets(eventSetDescriptor0);
	}
}
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

	@Test
	public void summa_adjustAASorting() throws ParseException {
		Collator collator0 = null;
		Locale locale0 = Locale.KOREA;
		boolean boolean0 = false;
		RuleBasedCollator ruleBasedCollator0 = (RuleBasedCollator)CollatorFactory.createCollator(locale0, boolean0);
		Collator collator1 = CollatorFactory.adjustAASorting(collator0);
	}

	@Test
	public void xbus_getDetailsAsTable() {
		JournalBean journalBean0 = new JournalBean();
		String string0 = "request";
		journalBean0.setDetails(string0);
		journalBean0.setMessage(string0);
		String string1 = journalBean0.getDetailsAsTable();
	}
	
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
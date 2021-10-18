package feature.objectconstruction.testgeneration.testcase;

import java.awt.Color;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.Proxy;
import java.net.URI;
import java.text.AttributedCharacterIterator;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import javax.imageio.metadata.IIOMetadataNode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Box;
import javax.swing.DebugGraphics;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableModel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.evosuite.runtime.mock.java.io.MockFile;
import org.evosuite.runtime.mock.java.io.MockFileInputStream;
import org.evosuite.runtime.mock.java.io.MockFileOutputStream;
import org.evosuite.runtime.mock.java.io.MockPrintWriter;
import org.evosuite.runtime.mock.java.lang.MockThread;
import org.evosuite.runtime.mock.java.lang.MockThrowable;
import org.evosuite.runtime.mock.java.util.MockDate;
import org.evosuite.runtime.mock.java.util.MockGregorianCalendar;
import org.evosuite.runtime.testdata.EvoSuiteFile;
import org.evosuite.runtime.testdata.FileSystemHandling;
import org.exolab.jms.common.threads.ThreadPoolFactory;
import org.exolab.jms.config.Configuration;
import org.exolab.jms.message.MessageId;
import org.exolab.jms.scheduler.Scheduler;
import org.exolab.jms.scheduler.SerialTask;
import org.exolab.jms.selector.Expression;
import org.exolab.jms.selector.parser.SelectorAST;
import org.exolab.jms.selector.parser.SelectorTreeParser;
import org.exolab.jms.tranlog.ExternalXid;
import org.exolab.jms.tranlog.TransactionState;
import org.exolab.jms.util.CommandLine;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.title.DateTitle;
import org.jfree.chart.title.ImageTitle;
import org.jfree.chart.title.ShortTextTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.PaintAlpha;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.Range;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.SlidingGanttCategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.DefaultWindDataset;
import org.jsecurity.ri.session.DelegatingSession;
import org.jsecurity.ri.util.StringPrincipal;
import org.jsecurity.ri.util.UsernamePrincipal;
import org.jsecurity.spring.servlet.security.AuthenticationInterceptor;
import org.junit.Test;

import com.jigen.msi.ResourceDescriptor;
import com.jigen.msi.ResourcesDirectory;
import com.lts.application.ApplicationException;
import com.lts.application.data.ApplicationData;
import com.lts.application.data.coll.ADCAdaptor;
import com.lts.caloriecount.data.CalorieCountData;
import com.lts.caloriecount.data.CalorieCountDataElements;
import com.lts.caloriecount.data.budget.Budget;
import com.lts.caloriecount.data.entry.EntryList;
import com.lts.caloriecount.data.food.FoodList;
import com.lts.caloriecount.data.frequent.FrequentFoodList;
import com.lts.caloriecount.data.meal.MealList;
import com.lts.swing.treetable.DefaultTableTreeModel;
import com.lts.swing.treetable.DefaultTreeTableModel;
import com.lts.swing.treetable.TreeTable;
import com.lts.util.TreeNode;
import com.lts.util.notifyinglist.NotifyingListAdaptor;
import com.lts.xml.simple.SimpleElement;
import com.soops.CEN4010.JMCA.JParser.xmlParser.SaxProcessor;

import antlr.RecognitionException;
import corina.Sample;
import corina.editor.DecadalModel;
import corina.manip.Truncate;
import corina.map.LabelSet;
import corina.map.MapFrame;
import corina.map.MapPanel;
import corina.map.View;
import corina.map.tools.ToolBox;
import corina.map.tools.ZoomInTool;
import corina.prefs.components.ColorRenderer;
import corina.site.Site;
import corina.site.SiteDB;
import corina.util.SimpleLog;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.protobuf.AccessEventProtobufEncoder;
import de.huxhorn.lilith.data.access.protobuf.generated.AccessProto;
import de.huxhorn.lilith.data.eventsource.LoggerContext;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.xml.StackTraceElementWriter;
import de.huxhorn.lilith.sender.MessageWriteByteStrategy;
import de.huxhorn.lilith.sender.SimpleSendBytesService;
import de.huxhorn.lilith.sender.SocketDataOutputStreamFactory;
import de.paragon.explorer.model.ArrayAttributeModel;
import de.paragon.explorer.model.AttributeModelComparator;
import de.paragon.explorer.model.StandardAttributeModel;
import dk.statsbiblioteket.summa.common.Record;
import dk.statsbiblioteket.summa.common.util.CollatorFactory;
import dk.statsbiblioteket.summa.common.util.LineInputStream;
import dk.statsbiblioteket.summa.common.util.StringMap;
import dk.statsbiblioteket.summa.ingest.stream.MarcXmlWriterFixed;
import dk.statsbiblioteket.summa.storage.api.QueryOptions;
import fr.unice.gfarce.interGraph.SharedListSelectionHandlerFormation;
import macaw.businessLayer.BasketVariableReference;
import macaw.businessLayer.SupportingDocument;
import macaw.presentationLayer.BasketTree;
import macaw.presentationLayer.VariableSearchPanel;
import macaw.system.SessionProperties;
import net.sf.xbus.admin.html.JournalBean;
import net.sf.xbus.base.xml.IteratedWhitespaceInElementDeletion;
import net.sourceforge.ifxfv3.beans.AcctTaxInfo;
import net.sourceforge.ifxfv3.beans.BankInfo;
import net.sourceforge.ifxfv3.beans.BankSvcRq_TypeSequence;
import net.sourceforge.ifxfv3.beans.BankSvcRq_TypeSequenceItem;
import net.sourceforge.ifxfv3.beans.IFX_Type;
import net.sourceforge.ifxfv3.beans.LoanAcctId;
import net.sourceforge.ifxfv3.beans.MaxCurAmt;
import net.sourceforge.ifxfv3.beans.MediaAcctAdjMsgRec;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.util.SimpleCache;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import sun.awt.SunHints;
import weka.clusterers.Cobweb;
import weka.core.Attribute;
import weka.core.BinarySparseInstance;
import weka.core.Instances;
import weka.core.WekaException;
import weka.gui.SimpleCLIPanel;
import weka.gui.beans.Appender;
import weka.gui.beans.BeanInstance;
import weka.gui.beans.ClassAssigner;
import weka.gui.beans.Clusterer;
import weka.gui.beans.DataSetEvent;
import weka.gui.beans.FlowByExpression;
import weka.gui.beans.ImageSaver;
import weka.gui.beans.InstanceEvent;
import weka.gui.beans.MetaBean;
import weka.gui.beans.PredictionAppender;
import weka.gui.beans.Saver;
import weka.gui.beans.ScatterPlotMatrix;
import weka.gui.beans.StripChart;
import weka.gui.beans.SubstringLabelerRules;
import weka.gui.beans.TestSetEvent;
import weka.gui.beans.TextViewer;
import weka.gui.beans.TrainingSetEvent;
import weka.gui.beans.TrainingSetMaker;
import weka.gui.boundaryvisualizer.BoundaryPanel;
import weka.gui.boundaryvisualizer.KDDataGenerator;
import weka.gui.graphvisualizer.DotParser;
import weka.gui.graphvisualizer.GraphEdge;
import weka.gui.graphvisualizer.GraphNode;
import weka.gui.graphvisualizer.HierarchicalBCEngine;
import weka.gui.knowledgeflow.MainKFPerspective;
import weka.gui.knowledgeflow.StepVisual;
import weka.gui.knowledgeflow.VisibleLayout;
import weka.gui.knowledgeflow.steps.BoundaryPlotterInteractiveView;
import weka.gui.knowledgeflow.steps.BoundaryPlotterStepEditorDialog;
import weka.gui.knowledgeflow.steps.ExecuteProcessStepEditorDialog;
import weka.gui.knowledgeflow.steps.JoinStepEditorDialog;
import weka.gui.knowledgeflow.steps.SaverStepEditorDialog;
import weka.gui.knowledgeflow.steps.ScatterPlotMatrixInteractiveView;
import weka.gui.knowledgeflow.steps.SorterStepEditorDialog;
import weka.gui.knowledgeflow.steps.SubstringLabelerStepEditorDialog;
import weka.gui.knowledgeflow.steps.TextViewerInteractiveView;
import weka.gui.simplecli.AbstractCommand;
import weka.gui.simplecli.Exit;
import weka.gui.simplecli.Help;
import weka.gui.simplecli.History;
import weka.gui.sql.InfoPanel;
import weka.gui.streams.InstanceTable;
import weka.gui.streams.InstanceViewer;
import weka.gui.treevisualizer.Node;
import weka.gui.visualize.PlotData2D;
import weka.knowledgeflow.BaseExecutionEnvironment;
import weka.knowledgeflow.Data;
import weka.knowledgeflow.StepManagerImpl;
import weka.knowledgeflow.steps.ASEvaluator;
import weka.knowledgeflow.steps.BoundaryPlotter;
import weka.knowledgeflow.steps.DataVisualizer;
import weka.knowledgeflow.steps.ExecuteProcess;
import weka.knowledgeflow.steps.Filter;
import weka.knowledgeflow.steps.Join;
import weka.knowledgeflow.steps.ModelPerformanceChart;
import weka.knowledgeflow.steps.StorePropertiesInEnvironment;
import weka.knowledgeflow.steps.WriteWekaLog;
import wheel.ErrorPage;
import wheel.asm.AnnotationVisitor;
import wheel.asm.ClassWriter;
import wheel.asm.FieldVisitor;
import wheel.asm.Label;
import wheel.components.ActionExpression;
import wheel.components.Block;
import wheel.components.ComponentCreator;
import wheel.components.Form;
import wheel.components.Link;
import wheel.components.Table;
import wheel.components.TableBlock;
import wheel.components.XmlEntityRef;
import wheel.enhance.WheelAnnotatedField;
import wheel.enhance.WheelAnnotationVisitor;
import wheel.enhance.WheelFieldVisitor;
import wheel.util.ActionRegistry;

public class NullPointerExceptionTest_121021 {
	// Total of 171 unique identifiers.
	
	/*
	 * Null object passed in indirectly (Stack#setSize)
	 */
	@Test
	public void evoobj_weka_BeanInstance_addBeanInstances_0() {
	  // I34 Branch 24 IFEQ L215;false
	  // In-method
	  Stack<Object> stack0 = new Stack<Object>();
	  int int0 = 1495;
	  stack0.setSize(int0);
	  Saver saver0 = new Saver();
	  BeanInstance.addBeanInstances(stack0, saver0);
	}

	/*
	 * DataVisualiser#renderOffscreenImage is not visible
	 */
//	@Test
//	public void evoobj_weka_DataVisualizer_renderOffscreenImage_1() {
//	  // I34 Branch 10 IF_ICMPGE L125;true
//	  // In-method
//	  ScatterPlotMatrix scatterPlotMatrix0 = new ScatterPlotMatrix();
//	  JSpinner jSpinner0 = new JSpinner();
//	  JSpinner.NumberEditor jSpinner_NumberEditor0 = new JSpinner.NumberEditor(jSpinner0);
//	  FocusTraversalPolicy focusTraversalPolicy0 = jSpinner0.getFocusTraversalPolicy();
//	  ArrayList<Attribute> arrayList0 = new ArrayList<Attribute>();
//	  Instances instances0 = new Instances("Weka Classifier Visualize: ", arrayList0, 81);
//	  DataSetEvent dataSetEvent0 = new DataSetEvent(jSpinner_NumberEditor0, instances0);
//	  scatterPlotMatrix0.addPropertyChangeListener("@relation", jSpinner_NumberEditor0);
//	  ImageSaver imageSaver0 = new ImageSaver();
//	  scatterPlotMatrix0.addImageListener(imageSaver0);
//	  scatterPlotMatrix0.setOffscreenAdditionalOpts((String) null);
//	  scatterPlotMatrix0.renderOffscreenImage(dataSetEvent0);
//	}

	/*
	 * Used parameter not set (m_downstream)
	 */
	@Test
	public void evoobj_weka_FlowByExpression_acceptInstance_7() {
	  // I34 Branch 70 IF_ICMPGE L543;false
	  // In-method
	  FlowByExpression flowByExpression0 = new FlowByExpression();
	  JTextArea jTextArea0 = new JTextArea();
	  int int0 = 1809;
	  BinarySparseInstance binarySparseInstance0 = new BinarySparseInstance(int0);
	  int int1 = 62;
	  InstanceEvent instanceEvent0 = new InstanceEvent(jTextArea0, binarySparseInstance0, int1);
	  flowByExpression0.acceptInstance(instanceEvent0);
	}

//	@Test
//	public void evoobj_weka_Loader_notifyStructureAvailable_2() {
//	  // I14 Branch 23 IF_ICMPGE L213;false
//	  // Out-method
//	  Loader loader0 = new Loader();
//	  AttributeSummarizer attributeSummarizer0 = new AttributeSummarizer();
//	  loader0.addDataSourceListener(attributeSummarizer0);
//	  EvoSuiteFile evoSuiteFile0 = null;
//	  String string0 = "set$UseRelativePath";
//	  boolean boolean0 = FileSystemHandling.appendLineToFile(evoSuiteFile0, string0);
//	  Filter filter0 = new Filter();
//	  loader0.addInstanceListener(filter0);
//	  PipedReader pipedReader0 = new PipedReader();
//	  PipedWriter pipedWriter0 = new PipedWriter();
//	  PipedReader pipedReader1 = new PipedReader(pipedWriter0);
//	  BeanContextServicesSupport beanContextServicesSupport0 = new BeanContextServicesSupport();
//	  String string1 = "4eL6Y;_2YA}{";
//	  Locale locale0 = new Locale(string1);
//	  boolean boolean1 = true;
//	  boolean boolean2 = true;
//	  BeanContextServicesSupport beanContextServicesSupport1 = new BeanContextServicesSupport(beanContextServicesSupport0, locale0, boolean1, boolean2);
//	  BeanContextServicesSupport beanContextServicesSupport2 = new BeanContextServicesSupport(beanContextServicesSupport1, locale0, boolean2);
//	  BeanContextSupport beanContextSupport0 = new BeanContextSupport();
//	  BeanContextSupport beanContextSupport1 = new BeanContextSupport(beanContextSupport0);
//	  loader0.setBeanContext(beanContextSupport1);
//	  int int0 = 5135;
//	  BufferedReader bufferedReader0 = new BufferedReader(pipedReader1, int0);
//	  Instances instances0 = attributeSummarizer0.m_visualizeDataSet;
//	  Instances instances1 = attributeSummarizer0.m_visualizeDataSet;
//	  Instances instances2 = new Instances(instances1);
//	  Instances instances3 = Instances.mergeInstances(instances0, instances1);
//	  loader0.notifyStructureAvailable(instances3);
//	}
	
	/*
	 * Non-visible field m_subFlow
	 */
//	@Test
//	public void evoobj_weka_MetaBean_getSuitableTargets_3() {
//	  // I8 Branch 80 IFLE L593;true
//	  // In-method
//	  MetaBean metaBean0 = new MetaBean();
//	  Vector<Object> vector0 = metaBean0.m_subFlow;
//	  metaBean0.setInputs(vector0);
//	  Vector<Object> vector1 = metaBean0.getInputs();
//	  int int0 = 83;
//	  vector0.setSize(int0);
//	  MockJFileChooser mockJFileChooser0 = new MockJFileChooser();
//	  String string0 = "0^s0jk`{\\20j7Cn)Ghg";
//	  Class<Point> class0 = Point.class;
//	  Method[] methodArray0 = new Method[0];
//	  Method method0 = null;
//	  EventSetDescriptor eventSetDescriptor0 = new EventSetDescriptor(string0, class0, methodArray0, method0, method0, method0);
//	  Vector<BeanInstance> vector2 = metaBean0.getSuitableTargets(eventSetDescriptor0);
//	}

	/*
	 * Used parameter not set (m_subFlow has insufficient elements).
	 */
	@Test
	public void evoobj_weka_MetaBean_shiftBeans_6() {
	  // I10 Branch 17 IFNULL L178;false
	  // In-method
	  MetaBean metaBean0 = new MetaBean();
	  Vector<Object> vector0 = new Vector<Object>(1244, 411);
	  metaBean0.setSubFlow(vector0);
	  Vector<Object> vector1 = metaBean0.getSubFlow();
	  vector1.setSize(2013);
	  Integer[] integerArray0 = new Integer[3];
	  Integer integer0 = JLayeredPane.DRAG_LAYER;
	  integerArray0[0] = integer0;
	  Integer integer1 = JLayeredPane.FRAME_CONTENT_LAYER;
	  integerArray0[1] = integer1;
	  Integer integer2 = JLayeredPane.MODAL_LAYER;
	  integerArray0[2] = integer2;
	  BeanInstance beanInstance0 = new BeanInstance(metaBean0, "debugTipText", 443, 11, integerArray0);
	  metaBean0.shiftBeans(beanInstance0, false);
	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_weka_Saver_acceptTestSet_5() {
	  // I34 Branch 24 IFEQ L215;false
	  // In-method
	  Saver saver0 = new Saver();
	  ContainerOrderFocusTraversalPolicy containerOrderFocusTraversalPolicy0 = new ContainerOrderFocusTraversalPolicy();
	  saver0.setFocusTraversalPolicy(containerOrderFocusTraversalPolicy0);
	  TestSetEvent testSetEvent0 = null;
	  saver0.acceptTestSet(testSetEvent0);
	}
	
	/*
	 * Non-visible method notifyTrainingSetProduced
	 */
//	@Test
//	public void evoobj_weka_TrainingSetMaker_notifyTrainingSetProduced_3() {
//	  // I8 Branch 8 IFLE L120;true
//	  // In-method
//	  TrainingSetMaker trainingSetMaker0 = new TrainingSetMaker();
//	  String string0 = " - found";
//	  MetaBean metaBean0 = new MetaBean();
//	  TextViewer textViewer0 = new TextViewer();
//	  trainingSetMaker0.addTrainingSetListener(textViewer0);
//	  trainingSetMaker0.disconnectionNotification(string0, metaBean0);
//	  boolean boolean0 = trainingSetMaker0.isFocusable();
//	  DebugGraphics debugGraphics0 = new DebugGraphics();
//	  TrainingSetEvent trainingSetEvent0 = null;
//	  trainingSetMaker0.notifyTrainingSetProduced(trainingSetEvent0);
//	}

	/*
	 * Non-visible member m_trainingData.
	 */
//	@Test
//	public void evoobj_weka_BoundaryPanel_setTrainingData_1() {
//	  // I233 Branch 35 IF_ICMPGE L284;true
//	  // In-method
//	  BoundaryPanel boundaryPanel0 = new BoundaryPanel(7, 3500);
//	  Instances instances0 = boundaryPanel0.m_trainingData;
//	  Instances instances1 = boundaryPanel0.m_trainingData;
//	  boundaryPanel0.setTrainingData((Instances) null);
//	}

	/*
	 * Used parameter not set (m_instances)
	 */
	@Test
	public void evoobj_weka_KDDataGenerator_generateInstances_3() throws Exception {
	  // I28 Branch 9 IFLE L125;false
	  // In-method
	  KDDataGenerator kDDataGenerator0 = new KDDataGenerator();
	  int[] intArray0 = new int[4];
	  int int0 = 923;
	  intArray0[0] = int0;
	  intArray0[0] = intArray0[0];
	  int int1 = 129;
	  intArray0[2] = int1;
	  intArray0[3] = intArray0[1];
	  double[][] doubleArray0 = kDDataGenerator0.generateInstances(intArray0);
	}

//	@Test
//	public void evoobj_weka_DotParser_writeDOT_1() {
//	  // I12 Branch 75 IF_ICMPGE L562;true
//	  // In-method
//	  String string0 = "^u^O}\"%@L\"zBw/9&";
//	  String string1 = "[5S;yrFK=D8:tQ";
//	  ArrayList<GraphNode> arrayList0 = new ArrayList<GraphNode>();
//	  ArrayList<GraphEdge> arrayList1 = null;
//	  DotParser.writeDOT(string0, string1, arrayList0, arrayList1);
//	}
	
	/*
	 * Non-visible method copy2DArray.
	 */
//	@Test
//	public void evoobj_weka_HierarchicalBCEngine_clearTemps_and_EdgesFromNodes_9() {
//	  // I34 Branch 10 IF_ICMPGE L125;true
//	  // In-method
//	  ArrayList<GraphNode> arrayList0 = null;
//	  int int0 = 2279;
//	  ArrayList<GraphEdge> arrayList1 = new ArrayList<GraphEdge>(int0);
//	  int int1 = 0;
//	  int int2 = 262;
//	  HierarchicalBCEngine hierarchicalBCEngine0 = new HierarchicalBCEngine(arrayList0, arrayList1, int1, int2);
//	  int[][] intArray0 = new int[8][9];
//	  int[] intArray1 = new int[5];
//	  intArray1[0] = int0;
//	  intArray1[1] = int0;
//	  intArray1[2] = int2;
//	  intArray1[3] = int1;
//	  intArray1[4] = int1;
//	  intArray0[0] = intArray1;
//	  int[] intArray2 = new int[8];
//	  intArray2[0] = int0;
//	  intArray2[1] = int1;
//	  intArray2[2] = int2;
//	  intArray2[3] = int2;
//	  intArray2[4] = int2;
//	  intArray2[5] = int1;
//	  intArray2[6] = int0;
//	  intArray0[1] = intArray2;
//	  int[] intArray3 = new int[2];
//	  intArray3[0] = int1;
//	  intArray3[1] = int0;
//	  intArray0[2] = intArray3;
//	  int[] intArray4 = new int[3];
//	  intArray4[0] = int0;
//	  intArray4[1] = int0;
//	  intArray4[2] = int0;
//	  intArray0[3] = intArray4;
//	  int[] intArray5 = new int[2];
//	  intArray5[0] = int2;
//	  intArray5[1] = int1;
//	  intArray0[4] = intArray5;
//	  int[] intArray6 = new int[3];
//	  intArray6[0] = int1;
//	  intArray6[1] = int2;
//	  int int3 = (-1382);
//	  intArray6[2] = int3;
//	  intArray0[5] = intArray6;
//	  hierarchicalBCEngine0.copy2DArray(intArray0, intArray0);
//	  hierarchicalBCEngine0.clearTemps_and_EdgesFromNodes();
//	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_weka_MainKFPerspective_copyStepsToClipboard_1() throws WekaException {
	  // I62 Branch 6 IF_ICMPGE L100;true
	  // In-method
	  MainKFPerspective mainKFPerspective0 = new MainKFPerspective();
	  List<StepVisual> list0 = null;
	  mainKFPerspective0.copyStepsToClipboard(list0);
	}

//	@Test
//	public void evoobj_weka_StepVisual_createVisual_5() {
//	  // I28 Branch 2 IFLE L94;false
//	  // In-method
//	  Clusterer clusterer0 = new Clusterer();
//	  StepManagerImpl stepManagerImpl0 = new StepManagerImpl(clusterer0);
//	  StepVisual stepVisual0 = StepVisual.createVisual(stepManagerImpl0);
//	}

//	@Test
//	public void evoobj_weka_StepVisual_loadIcon_1() {
//	  // I28 Branch 9 IFLE L125;true
//	  // Out-method
//	  ClassLoader classLoader0 = null;
//	  String string0 = "B#";
//	  StripChart stripChart0 = new StripChart();
//	  StepManagerImpl stepManagerImpl0 = new StepManagerImpl(stripChart0);
//	  int int0 = JComponent.UNDEFINED_CONDITION;
//	  String string1 = JComponent.TOOL_TIP_TEXT_KEY;
//	  StepVisual stepVisual0 = StepVisual.createVisual(stepManagerImpl0);
//	  Loader loader0 = new Loader();
//	  stepVisual0.invalidate();
//	  Data data0 = new Data(stepVisual0.BASE_ICON_PATH);
//	  stripChart0.processIncoming(data0);
//	  float float0 = stepVisual0.getAlignmentX();
//	  StepVisual stepVisual1 = new StepVisual();
//	  int int1 = 3924;
//	  stepVisual0.setY(int1);
//	  int int2 = stepVisual1.getY();
//	  Point point0 = new Point();
//	  Point point1 = new Point();
//	  String string2 = "' does not have any interactive view components";
//	  stepVisual1.setStepName(string2);
//	  boolean boolean0 = true;
//	  stepVisual1.setDisplayConnectors(boolean0);
//	  ImageIcon imageIcon0 = StepVisual.loadIcon(classLoader0, string0);
//	}

	/*
	 * Non-visible method addAll
	 */
//	@Test
//	public void evoobj_weka_VisibleLayout_addAll_8() {
//	  // I86 Branch 14 IFEQ L134;false
//	  // Out-method
//	  MainKFPerspective mainKFPerspective0 = new MainKFPerspective();
//	  VisibleLayout visibleLayout0 = new VisibleLayout(mainKFPerspective0);
//	  LinkedList<StepManagerImpl> linkedList0 = new LinkedList<StepManagerImpl>();
//	  boolean boolean0 = false;
//	  List<StepVisual> list0 = visibleLayout0.addAll(linkedList0, boolean0);
//	}
	
	/*
	 * Non-visible method findClosestConnections
	 */
//	@Test
//	public void evoobj_weka_VisibleLayout_findClosestConnections_0() {
//	  // I294 Branch 38 IF_ICMPLE L294;true
//	  // Out-method
//	  MainKFPerspective mainKFPerspective0 = new MainKFPerspective();
//	  VisibleLayout visibleLayout0 = new VisibleLayout(mainKFPerspective0);
//	  Point point0 = visibleLayout0.getLocationOnScreen();
//	  int int0 = (-480);
//	  Map<String, List<StepManagerImpl[]>> map0 = (Map<String, List<StepManagerImpl[]>>)visibleLayout0.findClosestConnections(point0, int0);
//	}

	/*
	 * Used parameter not set (m_step)
	 */
	@Test
	public void evoobj_weka_BoundaryPlotterInteractiveView_init_2() throws WekaException {
	  // I8 Branch 1 IFLE L89;true
	  // In-method
	  BoundaryPlotterInteractiveView boundaryPlotterInteractiveView0 = new BoundaryPlotterInteractiveView();
	  boundaryPlotterInteractiveView0.init();
	}

	/*
	 * Used parameter not set (m_step)
	 */
	@Test
	public void evoobj_weka_BoundaryPlotterStepEditorDialog_layoutEditor_9() {
	  // I8 Branch 1 IFLE L89;false
	  // In-method
	  BoundaryPlotterStepEditorDialog boundaryPlotterStepEditorDialog0 = new BoundaryPlotterStepEditorDialog();
	  boundaryPlotterStepEditorDialog0.layoutEditor();
	}
	
	/*
	 * Non-visible method okPressed.
	 */
//	@Test
//	public void evoobj_weka_ExecuteProcessStepEditorDialog_okPressed_0() {
//	  // I62 Branch 6 IF_ICMPGE L100;false
//	  // In-method
//	  ExecuteProcessStepEditorDialog executeProcessStepEditorDialog0 = new ExecuteProcessStepEditorDialog();
//	  executeProcessStepEditorDialog0.okPressed();
//	}
	
	/*
	 * Non-visible method layoutEditor.
	 */
//	@Test
//	public void evoobj_weka_JoinStepEditorDialog_layoutEditor_8() {
//	  // I8 Branch 8 IFLE L120;true
//	  // In-method
//	  JoinStepEditorDialog joinStepEditorDialog0 = new JoinStepEditorDialog();
//	  joinStepEditorDialog0.layoutEditor();
//	}
	
	/*
	 * Non-visible member m_firstListModel.
	 */
//	@Test
//	public void evoobj_weka_JoinStepEditorDialog_okPressed_7() {
//	  // I12 Branch 15 IFLE L155;true
//	  // In-method
//	  JoinStepEditorDialog joinStepEditorDialog0 = new JoinStepEditorDialog();
//	  DefaultListModel<String> defaultListModel0 = new DefaultListModel<String>();
//	  joinStepEditorDialog0.m_firstListModel = defaultListModel0;
//	  String string0 = "-snapshCot";
//	  defaultListModel0.addElement(string0);
//	  String string1 = "vQ!vwF)}:YG?";
//	  boolean boolean0 = joinStepEditorDialog0.getInheritsPopupMenu();
//	  defaultListModel0.addElement(string1);
//	  long long0 = 120L;
//	  float float0 = Component.TOP_ALIGNMENT;
//	  System.setCurrentTimeMillis(long0);
//	  joinStepEditorDialog0.okPressed();
//	}
	
	/*
	 * Non-visible method setupFileSaver.
	 */
//	@Test
//	public void evoobj_weka_SaverStepEditorDialog_setupFileSaver_6() {
//	  // I28 Branch 2 IFLE L94;true
//	  // In-method
//	  SaverStepEditorDialog saverStepEditorDialog0 = new SaverStepEditorDialog();
//	  weka.knowledgeflow.steps.Saver saver0 = new weka.knowledgeflow.steps.Saver();
//	  saverStepEditorDialog0.setupFileSaver(saver0);
//	}

	/*
	 * Used parameter not set (m_listModel)
	 */
	@Test
	public void evoobj_weka_SorterStepEditorDialog_okPressed_0() {
	  // I8 Branch 8 IFLE L120;true
	  // In-method
	  SorterStepEditorDialog sorterStepEditorDialog0 = new SorterStepEditorDialog();
	  sorterStepEditorDialog0.okPressed();
	}
	
	/*
	 * Non-visible member m_listModel.
	 */
//	@Test
//	public void evoobj_weka_SubstringLabelerStepEditorDialog_okPressed_4() {
//	  // I28 Branch 9 IFLE L125;false
//	  // In-method
//	  SubstringLabelerStepEditorDialog substringLabelerStepEditorDialog0 = new SubstringLabelerStepEditorDialog();
//	  DefaultListModel<SubstringLabelerRules.SubstringLabelerMatchRule> defaultListModel0 = new DefaultListModel<SubstringLabelerRules.SubstringLabelerMatchRule>();
//	  long long0 = (-2430L);
////	  System.setCurrentTimeMillis(long0);
//	  substringLabelerStepEditorDialog0.m_listModel = defaultListModel0;
//	  long long1 = 9L;
//	  System.setCurrentTimeMillis(long1);
//	  substringLabelerStepEditorDialog0.okPressed();
//	}

//	@Test
//	public void evoobj_weka_TextViewerInteractiveView_init_3() {
//	  // I28 Branch 2 IFLE L94;false
//	  // In-method
//	  TextViewerInteractiveView textViewerInteractiveView0 = new TextViewerInteractiveView();
//	  TextViewer textViewer0 = new TextViewer();
//	  textViewerInteractiveView0.setStep(textViewer0);
//	  textViewerInteractiveView0.init();
//	}
	
	/*
	 * Non-visible method visualize.
	 */
//	@Test
//	public void evoobj_weka_TextViewerInteractiveView_visualize_3() {
//	  // I86 Branch 7 IFEQ L103;true
//	  // In-method
//	  TextViewerInteractiveView textViewerInteractiveView0 = new TextViewerInteractiveView();
//	  String string0 = "_64^_";
//	  int int0 = (-699);
//	  int int1 = 119;
//	  textViewerInteractiveView0.visualize(string0, int0, int1);
//	}
	
	/*
	 * Non-visible member m_Owner.
	 */
//	@Test
//	public void evoobj_weka_Exit_doExecute_2() {
//	  // I62 Branch 6 IF_ICMPGE L100;false
//	  // In-method
//	  Exit exit0 = new Exit();
//	  SimpleCLIPanel simpleCLIPanel0 = new SimpleCLIPanel();
//	  exit0.m_Owner = simpleCLIPanel0;
//	  String[] stringArray0 = null;
//	  exit0.doExecute(stringArray0);
//	}

	/*
	 * Non-visible method doExecute.
	 */
//	@Test
//	public void evoobj_weka_Help_doExecute_2() {
//	  // I62 Branch 6 IF_ICMPGE L100;true
//	  // Out-method
//	  Help help0 = new Help();
//	  String string0 = null;
//	  String string1 = "weka.core.AbstractInstanc";
//	  History history0 = new History();
//	  int int0 = help0.compareTo(history0);
//	  AbstractCommand abstractCommand0 = AbstractCommand.getCommand(string1);
//	  String string2 = null;
//	  AbstractCommand abstractCommand1 = AbstractCommand.getCommand(string2);
//	  AbstractCommand abstractCommand2 = AbstractCommand.getCommand(string0);
//	  String string3 = " ";
//	  List<AbstractCommand> list0 = AbstractCommand.getCommands();
//	  SimpleCLIPanel simpleCLIPanel0 = new SimpleCLIPanel();
//	  String string4 = "";
//	  String string5 = "";
//	  TransferHandler transferHandler0 = new TransferHandler(string5);
//	  simpleCLIPanel0.setTransferHandler(transferHandler0);
//	  boolean boolean0 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  long long0 = 2529L;
//	  long long1 = 0L;
//	  simpleCLIPanel0.firePropertyChange(string4, long0, long1);
//	  JPopupMenu jPopupMenu0 = simpleCLIPanel0.getComponentPopupMenu();
//	  help0.setOwner(simpleCLIPanel0);
//	  AbstractCommand abstractCommand3 = AbstractCommand.getCommand(string3);
//	  String[] stringArray0 = new String[0];
//	  help0.execute(stringArray0);
//	  String[] stringArray1 = null;
//	  String string6 = help0.getParameterHelp();
//	  String string7 = help0.getParameterHelp();
//	  String string8 = help0.getHelp();
//	  String string9 = help0.getName();
//	  String string10 = help0.getParameterHelp();
//	  String string11 = help0.getHelp();
//	  String string12 = help0.getParameterHelp();
//	  String string13 = help0.getHelp();
//	  String string14 = help0.getHelp();
//	  String string15 = help0.getHelp();
//	  String string16 = help0.getHelp();
//	  String string17 = help0.getParameterHelp();
//	  help0.doExecute(stringArray1);
//	}
	
	/*
	 * Non-visible member m_buttonCopy.
	 */
//	@Test
//	public void evoobj_weka_InfoPanel_setButtons_2() {
//	  // I28 Branch 2 IFLE L94;true
//	  // Out-method
//	  FileSystemHandling fileSystemHandling0 = new FileSystemHandling();
//	  JFrame jFrame0 = null;
//	  InfoPanel infoPanel0 = new InfoPanel(jFrame0);
//	  JSpinner jSpinner0 = new JSpinner();
//	  String string0 = "q{uzaXs#\\9<>7oh/`W";
//	  JSpinner.NumberEditor jSpinner_NumberEditor0 = new JSpinner.NumberEditor(jSpinner0, string0);
//	  String string1 = "qYcx[4e$x_]gre~'Wu";
//	  EvoSuiteFile evoSuiteFile0 = null;
//	  byte[] byteArray0 = new byte[8];
//	  byte byte0 = (byte)126;
//	  byteArray0[0] = byte0;
//	  byte byte1 = (byte) (-33);
//	  byteArray0[1] = byte1;
//	  byte byte2 = (byte)5;
//	  byteArray0[1] = byte2;
//	  byte byte3 = (byte) (-81);
//	  byteArray0[3] = byte3;
//	  byte byte4 = (byte)0;
//	  byteArray0[4] = byte4;
//	  byte byte5 = (byte)0;
//	  boolean boolean0 = FileSystemHandling.appendDataToFile(evoSuiteFile0, byteArray0);
//	  byteArray0[5] = byte5;
//	  byteArray0[6] = byte3;
//	  byte byte6 = (byte)27;
//	  byteArray0[7] = byte6;
//	  boolean boolean1 = FileSystemHandling.appendDataToFile(evoSuiteFile0, byteArray0);
//	  JButton jButton0 = new JButton(string1);
//	  infoPanel0.m_ButtonCopy = jButton0;
//	  int int0 = 2;
//	  int int1 = 1574;
//	  boolean boolean2 = true;
//	  ListSelectionEvent listSelectionEvent0 = new ListSelectionEvent(jSpinner_NumberEditor0, int0, int1, boolean2);
//	  infoPanel0.createPanel();
//	  AncestorListener ancestorListener0 = null;
//	  jSpinner0.removeAncestorListener(ancestorListener0);
//	  JViewport jViewport0 = new JViewport();
//	  ComponentListener[] componentListenerArray0 = jViewport0.getComponentListeners();
//	  int int2 = 1;
//	  int int3 = (-579);
//	  Image image0 = jViewport0.createImage(int2, int3);
//	  Insets insets0 = jViewport0.getInsets();
//	  int int4 = 16;
//	  int int5 = 0;
//	  insets0.right = int5;
//	  int int6 = 0;
//	  jViewport0.addNotify();
//	  int int7 = 2496;
//	  int int8 = 1028;
//	  insets0.set(int4, int6, int7, int8);
//	  int int9 = (-2265);
//	  insets0.left = int9;
//	  boolean boolean3 = infoPanel0.getFocusTraversalKeysEnabled();
//	  String string2 = "\"";
//	  Insets insets1 = (Insets)insets0.clone();
//	  boolean boolean4 = false;
//	  boolean boolean5 = true;
//	  infoPanel0.firePropertyChange(string2, boolean4, boolean5);
//	  MouseMotionListener[] mouseMotionListenerArray0 = jButton0.getMouseMotionListeners();
//	  infoPanel0.append(insets0);
//	  String string3 = "<l17Y5frD+aeRp<;";
//	  ImageIcon imageIcon0 = new ImageIcon();
//	  int int10 = imageIcon0.getIconWidth();
//	  ActionMap actionMap0 = new ActionMap();
//	  int int11 = jButton0.getHeight();
//	  jButton0.setActionMap(actionMap0);
//	  DebugGraphics debugGraphics0 = new DebugGraphics();
//	  DebugGraphics debugGraphics1 = new DebugGraphics();
//	  DebugGraphics debugGraphics2 = new DebugGraphics(debugGraphics1, jSpinner_NumberEditor0);
//	  int int12 = 1094;
//	  int int13 = (-1361);
//	  infoPanel0.clear();
//	  imageIcon0.paintIcon(infoPanel0.m_ButtonCopy, debugGraphics2, int12, int13);
//	  imageIcon0.setImageObserver(jButton0);
//	  jButton0.setPressedIcon(imageIcon0);
//	  String string4 = null;
//	  infoPanel0.append(string3, string4);
//	  infoPanel0.clear();
//	  infoPanel0.setFocus();
//	  boolean boolean6 = infoPanel0.copyToClipboard();
//	  String string5 = "weka.gui.sql.InfoPanel$3";
//	  String string6 = ":";
//	  infoPanel0.append(string5, string6);
//	  String string7 = "~O^j<Z~z[G.,C,IC#";
//	  String string8 = "Too many values to display.";
//	  infoPanel0.append(string7, string8);
//	  boolean boolean7 = infoPanel0.copyToClipboard();
//	  String string9 = "empty_small.gif";
//	  int int14 = listSelectionEvent0.getLastIndex();
//	  int int15 = listSelectionEvent0.getFirstIndex();
//	  String string10 = "-V=o~,-^tYT|";
//	  infoPanel0.append(string9, string10);
//	  infoPanel0.clear();
//	  infoPanel0.createPanel();
//	  boolean boolean8 = infoPanel0.copyToClipboard();
//	  infoPanel0.createPanel();
//	  infoPanel0.setFocus();
//	  boolean boolean9 = infoPanel0.copyToClipboard();
//	  infoPanel0.createPanel();
//	  infoPanel0.setFocus();
//	  infoPanel0.clear();
//	  infoPanel0.clear();
//	  infoPanel0.setFocus();
//	  infoPanel0.createPanel();
//	  infoPanel0.setFocus();
//	  int int16 = infoPanel0.getY();
//	  infoPanel0.setFocus();
//	  infoPanel0.setFocus();
//	  String string11 = "";
//	  String string12 = "Copy";
//	  infoPanel0.append(string11, string12);
//	  String string13 = null;
//	  String string14 = "((^Qw^v1B\"fV*V^o";
//	  infoPanel0.append(string13, string14);
//	  String string15 = ">'F3{9I]!XQ29}}]";
//	  String string16 = null;
//	  infoPanel0.append(string15, string16);
//	  infoPanel0.setButtons(listSelectionEvent0);
//	}

	/*
	 * Used parameter not set (m_Instances)
	 */
	@Test
	public void evoobj_weka_InstanceTable_input_2() throws Exception {
	  // I28 Branch 2 IFLE L94;false
	  // In-method
	  InstanceTable instanceTable0 = new InstanceTable();
	  double double0 = 0.0;
	  int[] intArray0 = new int[3];
	  int int0 = (-870);
	  intArray0[0] = int0;
	  int int1 = 19;
	  intArray0[1] = int1;
	  int int2 = (-74);
	  intArray0[2] = int2;
	  int int3 = (-1926);
	  BinarySparseInstance binarySparseInstance0 = new BinarySparseInstance(double0, intArray0, int3);
	  instanceTable0.input(binarySparseInstance0);
	}

	/*
	 * Complicated case (indirect null parameter)
	 */
	@Test
	public void evoobj_weka_InstanceTable_inputFormat_9() throws IOException {
	  // I8 Branch 1 IFLE L89;false
	  // Out-method
	  InstanceTable instanceTable0 = new InstanceTable();
	  Reader reader0 = null;
	  Instances instances0 = new Instances(reader0);
	  int int0 = (-37);
	  int int1 = (-373);
	  Instances instances1 = new Instances(instances0, int0, int1);
	  instanceTable0.inputFormat(instances1);
	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_weka_InstanceViewer_inputFormat_2() throws Exception {
	  // I28 Branch 2 IFLE L94;true
	  // In-method
	  EvoSuiteFile evoSuiteFile0 = null;
	  byte[] byteArray0 = new byte[4];
	  byte byte0 = (byte) (-69);
	  byteArray0[0] = byte0;
	  byte byte1 = (byte)0;
	  byteArray0[1] = byte1;
	  byte byte2 = (byte)0;
	  byteArray0[2] = byte2;
	  byte byte3 = (byte)73;
	  byteArray0[3] = byte3;
	  boolean boolean0 = FileSystemHandling.appendDataToFile(evoSuiteFile0, byteArray0);
	  InstanceViewer instanceViewer0 = new InstanceViewer();
	  Instances instances0 = null;
	  double double0 = 310.0;
	  int[] intArray0 = new int[7];
	  intArray0[0] = (int) byteArray0[2];
	  intArray0[1] = (int) byteArray0[2];
	  int int0 = 1;
	  intArray0[2] = int0;
	  int int1 = 2;
	  intArray0[3] = int1;
	  intArray0[4] = (int) byteArray0[0];
	  intArray0[5] = (int) byte1;
	  intArray0[6] = (int) byte2;
	  int int2 = 4076;
	  BinarySparseInstance binarySparseInstance0 = new BinarySparseInstance(double0, intArray0, int2);
	  instanceViewer0.input(binarySparseInstance0);
	  instanceViewer0.inputFormat(instances0);
	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_weka_Node_getInstances_5() {
	  // I8 Branch 1 IFLE L89;false
	  // Out-method
	  String string0 = null;
	  String string1 = "c]*o>&onEO#tt tBY&^";
	  int int0 = 71;
	  int int1 = 4;
	  Color color0 = Color.GREEN;
	  String string2 = "Sorted array with NaN (doubles): ";
	  Node node0 = new Node(string0, string1, int0, int1, color0, string2);
	  Instances instances0 = node0.getInstances();
	}

	/*
	 * Non-visible member m_executorService.
	 */
//	@Test
//	public void evoobj_weka_BaseExecutionEnvironment_startClientExecutionService_8() {
//	  // I51 Branch 12 IFNULL L130;false
//	  // In-method
//	  BaseExecutionEnvironment baseExecutionEnvironment0 = new BaseExecutionEnvironment();
//	  int int0 = 1690;
//	  int int1 = 2702;
//	  TimeUnit timeUnit0 = TimeUnit.MILLISECONDS;
//	  boolean boolean0 = false;
//	  ArrayBlockingQueue<Runnable> arrayBlockingQueue0 = new ArrayBlockingQueue<Runnable>(int0, boolean0);
//	  LinkedTransferQueue<Runnable> linkedTransferQueue0 = new LinkedTransferQueue<Runnable>(arrayBlockingQueue0);
//	  ThreadPoolExecutor threadPoolExecutor0 = new ThreadPoolExecutor(int0, int1, int1, timeUnit0, linkedTransferQueue0);
//	  baseExecutionEnvironment0.m_executorService = (ExecutorService) threadPoolExecutor0;
//	  int int2 = 1334;
//	  int int3 = 1093;
//	  baseExecutionEnvironment0.startClientExecutionService(int2, int3);
//	}

	/*
	 * Non-visible member m_sourceStep.
	 */
//	@Test
//	public void evoobj_weka_StepManagerImpl_connectionIsIncremental_7() {
//	  // I769 Branch 99 IFNULL L342;false
//	  // Out-method
//	  String string0 = "estimator doesn't alter original datasets";
//	  Data data0 = new Data(string0);
//	  PredictionAppender predictionAppender0 = new PredictionAppender();
//	  PredictionAppender predictionAppender1 = new PredictionAppender();
//	  data0.m_sourceStep = (Step) predictionAppender1;
//	  Classifier classifier0 = new Classifier();
//	  Map<String, String> map0 = SetVariables.internalToMap(string0);
//	  Classifier classifier1 = new Classifier();
//	  String string1 = predictionAppender0.globalInfo();
//	  StepManagerImpl stepManagerImpl0 = new StepManagerImpl(predictionAppender0);
//	  Defaults defaults0 = predictionAppender1.getDefaultSettings();
//	  int int0 = stepManagerImpl0.numIncomingConnections();
//	  String string2 = "W2}Qf\"L)c";
//	  String string3 = stepManagerImpl0.environmentSubstitute(string2);
//	  boolean boolean0 = stepManagerImpl0.isStreamFinished(data0);
//	  stepManagerImpl0.clearAllStepOutputListeners();
//	  Logger logger0 = stepManagerImpl0.getLog();
//	  String string4 = "StepOutputListener '";
//	  predictionAppender1.setName(string4);
//	  String string5 = "y*N(\"Ce";
//	  StepManagerImpl stepManagerImpl1 = null;
//	  boolean boolean1 = true;
//	  data0.setSourceStep(predictionAppender1);
//	  stepManagerImpl0.m_stepIsFinished = boolean1;
//	  boolean boolean2 = true;
//	  String string6 = "3-9-3";
//	  data0.setConnectionName(string6);
//	  boolean boolean3 = stepManagerImpl0.addOutgoingConnection(string5, stepManagerImpl1, boolean2);
//	  String string7 = "y%?=AD]mE3x9j(";
//	  boolean boolean4 = true;
//	  stepManagerImpl0.m_adjustForGraphicalRendering = boolean4;
//	  classifier0.start();
//	  boolean boolean5 = true;
//	  boolean boolean6 = stepManagerImpl0.addOutgoingConnection(string7, stepManagerImpl1, boolean5);
//	  String string8 = "";
//	  int int1 = (-6);
//	  HashMap<String, Object> hashMap0 = new HashMap<String, Object>(int1);
//	  data0.m_payloadMap = (Map<String, Object>) hashMap0;
//	  String string9 = "5qH$Gn*h";
//	  stepManagerImpl0.statusMessage(string9);
//	  LoggingLevel loggingLevel0 = LoggingLevel.ERROR;
//	  stepManagerImpl0.setLoggingLevel(loggingLevel0);
//	  StepManager stepManager0 = stepManagerImpl0.getOutgoingConnectedStepWithName(string8);
//	  StepOutputListener stepOutputListener0 = null;
//	  String string10 = "3-9-3-snapshot";
//	  predictionAppender1.start();
//	  stepManagerImpl0.removeStepOutputListener(stepOutputListener0, string10);
//	  boolean boolean7 = stepManagerImpl0.getStepMustRunSingleThreaded();
//	  String string11 = "-6km8>3HOi[>$\";#";
//	  hashMap0.clear();
//	  stepManagerImpl0.logDebug(string11);
//	  String string12 = "3-9-3";
//	  stepManagerImpl0.removeOutgoingConnection(string12, stepManagerImpl1);
//	  String string13 = "-S <number>\n\tThe seed value for randomizing the data (default: 1).";
//	  MinkowskiDistance minkowskiDistance0 = new MinkowskiDistance();
//	  double double0 = (-1.0E100);
//	  double[] doubleArray0 = null;
//	  int[] intArray0 = new int[6];
//	  intArray0[0] = int0;
//	  intArray0[1] = int0;
//	  intArray0[2] = int0;
//	  intArray0[3] = minkowskiDistance0.R_WIDTH;
//	  intArray0[4] = minkowskiDistance0.R_WIDTH;
//	  int int2 = 1222;
//	  intArray0[5] = int2;
//	  double double1 = (-1282.808);
//	  int int3 = 1633;
//	  SparseInstance sparseInstance0 = new SparseInstance(double1, doubleArray0, intArray0, int3);
//	  SparseInstance sparseInstance1 = new SparseInstance(sparseInstance0);
//	  int int4 = 3;
//	  double[][] doubleArray1 = new double[3][3];
//	  doubleArray1[0] = doubleArray0;
//	  doubleArray1[1] = doubleArray0;
//	  doubleArray1[2] = doubleArray0;
//	  minkowskiDistance0.updateRangesFirst(sparseInstance1, int4, doubleArray1);
//	  int int5 = 531;
//	  SparseInstance sparseInstance2 = new SparseInstance(double0, doubleArray0, intArray0, int5);
//	  int int6 = (-1415);
//	  sparseInstance2.insertAttributeAt(int6);
//	  boolean boolean8 = stepManagerImpl0.addOutgoingConnection(string5, stepManagerImpl0);
//	  String string14 = minkowskiDistance0.getRevision();
//	  stepManagerImpl0.setStepProperty(string13, minkowskiDistance0);
//	  stepManagerImpl0.setManagedStep(classifier1);
//	  String string15 = null;
//	  stepManagerImpl0.logDetailed(string15);
//	  String string16 = "\"/29V0^yFs#\\\"-Cdn";
//	  boolean boolean9 = true;
//	  boolean boolean10 = stepManagerImpl0.addOutgoingConnection(string16, stepManagerImpl1, boolean9);
//	  String string17 = null;
//	  stepManagerImpl0.logLow(string17);
//	  boolean boolean11 = stepManagerImpl0.isStopRequested();
//	  String string18 = "4Z+^W&{u2.M5k2";
//	  int int7 = stepManagerImpl0.numOutgoingConnectionsOfType(string18);
//	  ExecutionEnvironment executionEnvironment0 = stepManagerImpl0.getExecutionEnvironment();
//	  String string19 = stepManagerImpl0.getName();
//	  stepManagerImpl0.setLog(logger0);
//	  boolean boolean12 = false;
//	  stepManagerImpl0.setStepMustRunSingleThreaded(boolean12);
//	  boolean boolean13 = predictionAppender1.isStopRequested();
//	  String string20 = "$9@i7I[vO.p}Q";
//	  StepManager stepManager1 = stepManagerImpl0.findStepInFlow(string20);
//	  Step step0 = stepManagerImpl0.getInfoStep();
//	  String string21 = "weka/knowledgeflow/StepManagerImpl#numIncomingConnectionsOfType(Ljava/lang/String;)I";
//	  List<StepManager> list0 = stepManagerImpl0.getOutgoingConnectedStepsOfConnectionType(string21);
//	  int int8 = stepManagerImpl0.numIncomingConnections();
//	  String string22 = stepManagerImpl0.getName();
//	  stepManagerImpl0.processIncoming(data0);
//	  String string23 = null;
//	  stepManagerImpl0.logDetailed(string23);
//	  Data[] dataArray0 = new Data[6];
//	  dataArray0[0] = data0;
//	  dataArray0[0] = data0;
//	  dataArray0[2] = data0;
//	  dataArray0[3] = data0;
//	  dataArray0[4] = data0;
//	  dataArray0[5] = data0;
//	  stepManagerImpl0.outputData(dataArray0);
//	  boolean boolean14 = StepManagerImpl.connectionIsIncremental(data0);
//	}
	
	/*
	 * Non-visible member m_isRanking.
	 */
//	@Test
//	public void evoobj_weka_ASEvaluator_applyFiltering_0() {
//	  // I86 Branch 59 IF_ICMPGE L438;true
//	  // In-method
//	  ASEvaluator aSEvaluator0 = new ASEvaluator();
//	  FileSystemHandling fileSystemHandling0 = new FileSystemHandling();
//	  String string0 = "LO5kD_SDom";
//	  StepManagerImpl stepManagerImpl0 = new StepManagerImpl(aSEvaluator0);
//	  StepManagerImpl stepManagerImpl1 = new StepManagerImpl(aSEvaluator0);
//	  boolean boolean0 = true;
//	  boolean boolean1 = stepManagerImpl0.addOutgoingConnection(string0, stepManagerImpl1, boolean0);
//	  aSEvaluator0.setStepManager(stepManagerImpl0);
//	  int[] intArray0 = new int[6];
//	  int int0 = 14;
//	  intArray0[0] = int0;
//	  aSEvaluator0.m_isRanking = boolean0;
//	  Instances instances0 = null;
//	  Integer integer0 = JLayeredPane.DRAG_LAYER;
//	  Integer integer1 = JLayeredPane.FRAME_CONTENT_LAYER;
//	  long long0 = 43200051L;
//	  System.setCurrentTimeMillis(long0);
//	  aSEvaluator0.applyFiltering(string0, intArray0, instances0, integer0, integer1);
//	}

//	@Test
//	public void evoobj_weka_Appender_makeOutputHeader_5() {
//	  // I294 Branch 38 IF_ICMPLE L294;true
//	  // In-method
//	  Appender appender0 = new Appender();
//	  ArrayDeque<Instances> arrayDeque0 = new ArrayDeque<Instances>();
//	  LinkedList<Instances> linkedList0 = new LinkedList<Instances>();
//	  Stack<String> stack0 = new Stack<String>();
//	  Instances instances0 = appender0.makeOutputHeader(linkedList0);
//	}
	
	/*
	 * Non-visible method getAttIndex.
	 */
//	@Test
//	public void evoobj_weka_BoundaryPlotter_getAttIndex_3() {
//	  // I86 Branch 31 IF_ICMPGE L261;false
//	  // In-method
//	  BoundaryPlotter boundaryPlotter0 = new BoundaryPlotter();
//	  String string0 = "c-T`'2|";
//	  Instances instances0 = null;
//	  int int0 = boundaryPlotter0.getAttIndex(string0, instances0);
//	}
	
	/*
	 * Undefined constructor 
	 * new StepManagerImpl(classAssigner0)
	 */
//	@Test
//	public void evoobj_weka_ClassAssigner_processIncoming_1() {
//	  // I51 Branch 5 IFNULL L99;false
//	  // In-method
//	  ClassAssigner classAssigner0 = new ClassAssigner();
//	  String string0 = "instance";
//	  Object object0 = new Object();
//	  StepManagerImpl stepManagerImpl0 = new StepManagerImpl(classAssigner0);
//	  classAssigner0.m_stepManager = (StepManager) stepManagerImpl0;
//	  Data data0 = new Data(string0);
//	  classAssigner0.processIncoming(data0);
//	}
	
	/*
	 * The method stepInit() is undefined for the type Clusterer
	 */
//	@Test
//	public void evoobj_weka_Clusterer_stepInit_0() {
//	  // I34 Branch 3 IF_ICMPGE L94;true
//	  // In-method
//	  Clusterer clusterer0 = new Clusterer();
//	  Cobweb cobweb0 = new Cobweb();
//	  String string0 = clusterer0.globalInfo();
//	  clusterer0.setWrappedAlgorithm(cobweb0);
//	  clusterer0.stepInit();
//	}

	/*
	 * The method createOffscreenPlot(PlotData2D) from the type DataVisualizer is not visible
	 */
//	@Test
//	public void evoobj_weka_DataVisualizer_createOffscreenPlot_7() {
//	  // I86 Branch 14 IFEQ L134;true
//	  // In-method
//	  DataVisualizer dataVisualizer0 = new DataVisualizer();
//	  PlotData2D plotData2D0 = null;
//	  BufferedImage bufferedImage0 = dataVisualizer0.createOffscreenPlot(plotData2D0);
//	}

	/*
	 * The method checkStructure(Instances) from the type ExecuteProcess is not visible
	 */
//	@Test
//	public void evoobj_weka_ExecuteProcess_checkStructure_8() {
//	  // I132 Branch 60 IFEQ L442;false
//	  // In-method
//	  ExecuteProcess executeProcess0 = new ExecuteProcess();
//	  Instances instances0 = null;
//	  String string0 = executeProcess0.getStaticArgs();
//	  String string1 = executeProcess0.getDynamicWorkingDirField();
//	  executeProcess0.checkStructure(instances0);
//	}

	/*
	 * The field Filter.m_incrementalData is not visible
	 */
//	@Test
//	public void evoobj_weka_Filter_processStreaming_7() {
//	  // I36 Branch 41 IFLE L328;true
//	  // In-method
//	  Filter filter0 = new Filter();
//	  Data data0 = filter0.m_incrementalData;
//	  filter0.processStreaming((Data) null);
//	}

	/*
	 * The method processSubsequentBatch(Instances, String, Integer, Integer) from the type Filter is not visible
	 */
//	@Test
//	public void evoobj_weka_Filter_processSubsequentBatch_1() {
//	  // I233 Branch 35 IF_ICMPGE L284;false
//	  // Out-method
//	  Filter filter0 = new Filter();
//	  String string0 = "$s5XN";
//	  Instances instances0 = filter0.outputStructureForConnectionType(string0);
//	  String string1 = "xC";
//	  Integer integer0 = JLayeredPane.DEFAULT_LAYER;
//	  Integer integer1 = JLayeredPane.PALETTE_LAYER;
//	  filter0.processSubsequentBatch(instances0, string1, integer0, integer1);
//	}

	/*
	 * The method processBatch(Data) is undefined for the type FlowByExpression
	 */
//	@Test
//	public void evoobj_weka_FlowByExpression_processBatch_7() {
//	  // I169 Branch 33 IF_ICMPGE L272;false
//	  // In-method
//	  FlowByExpression flowByExpression0 = new FlowByExpression();
//	  String string0 = " sumResult:";
//	  ArrayList<Attribute> arrayList0 = new ArrayList<Attribute>();
//	  int int0 = 770;
//	  Instances instances0 = new Instances(string0, arrayList0, int0);
//	  Instances instances1 = new Instances(instances0);
//	  Data data0 = new Data(string0, instances1);
//	  flowByExpression0.processBatch(data0);
//	}

	/*
	 * Used parameter is not set (m_sourceStep)
	 */
	@Test
	public void evoobj_weka_Join_processIncoming_1() throws WekaException {
	  // I21 Branch 18 IF_ICMPGE L179;true
	  // Out-method
	  Join join0 = new Join();
	  Data data0 = new Data(join0.KEY_SPEC_SEPARATOR);
	  join0.processIncoming(data0);
	}

	/*
	 * Used parameter is not set (m_stepManager)
	 */
	@Test
	public void evoobj_weka_ModelPerformanceChart_clearPlotData_5() {
	  // I62 Branch 43 IF_ICMPGE L331;false
	  // In-method
	  ModelPerformanceChart modelPerformanceChart0 = new ModelPerformanceChart();
	  modelPerformanceChart0.clearPlotData();
	  ModelPerformanceChart modelPerformanceChart1 = new ModelPerformanceChart();
	  boolean boolean0 = modelPerformanceChart0.isStopRequested();
	}

	/*
	 * m_streamingOutputStructure cannot be resolved or is not a field
	 */
//	@Test
//	public void evoobj_weka_PredictionAppender_processBatchClassifierCase_2() {
//	  // I58 Branch 48 IF_ICMPGE L361;true
//	  // In-method
//	  PredictionAppender predictionAppender0 = new PredictionAppender();
//	  Data data0 = new Data();
//	  ArrayList<Attribute> arrayList0 = new ArrayList<Attribute>();
//	  Instances instances0 = predictionAppender0.m_streamingOutputStructure;
//	  predictionAppender0.processBatchClassifierCase(data0, instances0, instances0);
//	}

	/*
	 * The method stepInit() is undefined for the type PredictionAppender
	 */
//	@Test
//	public void evoobj_weka_PredictionAppender_processIncrementalClassifier_5() {
//	  // I47 Branch 11 IFNULL L129;true
//	  // In-method
//	  PredictionAppender predictionAppender0 = new PredictionAppender();
//	  predictionAppender0.stepInit();
//	  StepManagerImpl stepManagerImpl0 = new StepManagerImpl(predictionAppender0);
//	  predictionAppender0.setStepManager(stepManagerImpl0);
//	  NaiveBayesMultinomialUpdateable naiveBayesMultinomialUpdateable0 = new NaiveBayesMultinomialUpdateable();
//	  Data data0 = predictionAppender0.m_instanceData;
//	  Instance instance0 = null;
//	  boolean boolean0 = true;
//	  predictionAppender0.setAppendProbabilities(boolean0);
//	  predictionAppender0.processIncrementalClassifier(data0, instance0);
//	}

	/*
	 * The field StorePropertiesInEnvironment.m_structureCheckComplete is not visible
	 */
//	@Test
//	public void evoobj_weka_StorePropertiesInEnvironment_processIncoming_5() {
//	  // I51 Branch 12 IFNULL L130;true
//	  // In-method
//	  StorePropertiesInEnvironment storePropertiesInEnvironment0 = new StorePropertiesInEnvironment();
//	  String string0 = "environment";
//	  boolean boolean0 = true;
//	  storePropertiesInEnvironment0.m_structureCheckComplete = boolean0;
//	  storePropertiesInEnvironment0.m_structureCheckComplete = boolean0;
//	  StepManagerImpl stepManagerImpl0 = new StepManagerImpl(storePropertiesInEnvironment0);
//	  storePropertiesInEnvironment0.m_stepManager = (StepManager) stepManagerImpl0;
//	  Data data0 = new Data(string0);
//	  storePropertiesInEnvironment0.processIncoming(data0);
//	}

	/*
	 * Used parameter is not set (m_stepManager)
	 */
	@Test
	public void evoobj_weka_WriteWekaLog_processIncoming_4() throws WekaException {
	  // I34 Branch 3 IF_ICMPGE L94;false
	  // In-method
	  WriteWekaLog writeWekaLog0 = new WriteWekaLog();
	  String string0 = "kcz.";
	  AttributedCharacterIterator.Attribute attributedCharacterIterator_Attribute0 = AttributedCharacterIterator.Attribute.LANGUAGE;
	  Data data0 = new Data(string0, attributedCharacterIterator_Attribute0);
	  String string1 = "; j++) {\n      dist[j] = RtoP(Fs, j);\n    }\n    return dist;\n";
	  ScatterPlotMatrixInteractiveView scatterPlotMatrixInteractiveView0 = new ScatterPlotMatrixInteractiveView();
	  Data data1 = new Data(string1, scatterPlotMatrixInteractiveView0);
	  String string2 = "5_e8@3^efh%`et";
	  String string3 = (String)data1.getPayloadElement(string2, string2);
	  writeWekaLog0.processIncoming(data1);
	}
	
	/*
	 * The method drawFirstPassShape(Graphics2D, int, int, int, Shape) from the type XYLine3DRenderer is not visible
	 */
//	@Test
//	public void evoobj_jfreechart_XYLine3DRenderer_drawFirstPassShape_4() {
//	  // I14 Branch 6 IFNULL L440;true
//	  // In-method
//	  XYLine3DRenderer xYLine3DRenderer0 = new XYLine3DRenderer();
//	  Graphics2D graphics2D0 = null;
//	  int int0 = 0;
//	  int int1 = (-1163);
//	  int int2 = (-2125);
//	  Font font0 = JFreeChart.DEFAULT_TITLE_FONT;
//	  xYLine3DRenderer0.setBaseLegendTextFont(font0);
//	  RoundRectangle2D.Float roundRectangle2D_Float0 = new RoundRectangle2D.Float();
//	  xYLine3DRenderer0.drawFirstPassShape(graphics2D0, int0, int1, int2, roundRectangle2D_Float0);
//	}
	
	/*
	 * java.lang.NoClassDefFoundError: org/jfree/ui/Drawable
	 */
//	@Test
//	public void evoobj_jfreechart_ImageTitle_equals_8() {
//	  // I413 Branch 16 IFNE L561;true
//	  // Out-method
//	  Image image0 = null;
//	  ImageTitle imageTitle0 = new ImageTitle(image0);
//	  Box box0 = Box.createVerticalBox();
//	  boolean boolean0 = imageTitle0.equals(box0);
//	}

	/*
	 * The type org.jfree.ui.Size2D cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_jfreechart_ShortTextTitle_arrangeFN_4() {
//	  // I465 Branch 18 IF_ACMPNE L569;false
//	  // In-method
//	  ShortTextTitle shortTextTitle0 = new ShortTextTitle("org/jfree/text/TextUtilities#drawRotatedString(Ljava/lang/String;Ljava/awt/Graphics2D;FFDFF)V");
//	  Size2D size2D0 = shortTextTitle0.arrangeFN((Graphics2D) null, (-2.0132659012255611E9));
//	}

	/*
	 * The type org.jfree.ui.Size2D cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_jfreechart_ShortTextTitle_arrangeRR_2() {
//	  // I482 Branch 19 IF_ACMPNE L575;false
//	  // In-method
//	  ShortTextTitle shortTextTitle0 = new ShortTextTitle("org.jfree.chart.axis.CategoryAxis");
//	  TextUtilities.setUseFontMetricsGetStringBounds(false);
//	  Size2D size2D0 = shortTextTitle0.arrangeRR((Graphics2D) null, (Range) null, (Range) null);
//	}

	/*
	 * The type org.jfree.ui.Size2D cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_jfreechart_TextTitle_arrange_2() {
//	  // I26 Branch 7 IFNULL L442;false
//	  // In-method
//	  EvoSuiteFile evoSuiteFile0 = null;
//	  boolean boolean0 = FileSystemHandling.shouldThrowIOException(evoSuiteFile0);
//	  String string0 = "org.jfree.chart.renderer.xy.StandardXYItemRenderer$State";
//	  int int0 = (-1988);
//	  Font font0 = new Font(string0, int0, int0);
//	  TextTitle textTitle0 = new TextTitle(string0, font0);
//	  Graphics2D graphics2D0 = null;
//	  Range range0 = null;
//	  RectangleConstraint rectangleConstraint0 = new RectangleConstraint(range0, range0);
//	  Size2D size2D0 = textTitle0.arrange(graphics2D0, rectangleConstraint0);
//	}

	/*
	 * The type org.jfree.ui.Size2D cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_jfreechart_TextTitle_arrangeRR_2() {
//	  // I672 Branch 30 IF_ICMPNE L616;true
//	  // Out-method
//	  DateTitle dateTitle0 = new DateTitle();
//	  GraphicsStream graphicsStream0 = null;
//	  int int0 = 306;
//	  Graphics2D graphics2D0 = null;
//	  Range range0 = null;
//	  RectangleConstraint rectangleConstraint0 = new RectangleConstraint(range0, range0);
//	  Size2D size2D0 = dateTitle0.arrange(graphics2D0, rectangleConstraint0);
//	  PDFGraphics2D pDFGraphics2D0 = new PDFGraphics2D(graphicsStream0, int0, int0, dateTitle0.visible);
//	  Range range1 = Range.scale(range0, int0);
//	  Size2D size2D1 = dateTitle0.arrangeRR(pDFGraphics2D0, range1, range1);
//	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_jfreechart_PaintAlpha_cloneImage_2() {
	  // I465 Branch 18 IF_ACMPNE L569;true
	  // In-method
	  BufferedImage bufferedImage0 = null;
	  boolean boolean0 = true;
	  boolean boolean1 = PaintAlpha.setLegacyAlpha(boolean0);
	  PaintAlpha paintAlpha0 = new PaintAlpha();
	  BufferedImage bufferedImage1 = PaintAlpha.cloneImage(bufferedImage0);
	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_jfreechart_PaintAlpha_darker_7() {
	  // I34 Branch 2 IF_ICMPNE L252;false
	  // In-method
	  float float0 = 0.75F;
	  int int0 = 1578;
	  boolean boolean0 = false;
	  Color color0 = new Color(int0, boolean0);
	  float float1 = 0.0F;
	  GradientPaint gradientPaint0 = new GradientPaint(float0, float1, color0, float0, int0, color0);
	  GradientPaint gradientPaint1 = (GradientPaint)PaintAlpha.darker(gradientPaint0);
	  float float2 = (-4654.74F);
	  float float3 = 255.0F;
	  Color color1 = null;
	  float float4 = 0.0F;
	  float float5 = 2.0F;
	  GradientPaint gradientPaint2 = new GradientPaint(float2, float3, color1, float4, float5, color1);
	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_jfreechart_ResourceBundleWrapper_getBundle_2() {
	  // I59 Branch 4 IF_ICMPNE L259;false
	  // In-method
	  String string0 = "org.jfree.chart.util.ResourceBundleWrapper";
	  Locale locale0 = null;
	  ResourceBundle resourceBundle0 = ResourceBundleWrapper.getBundle(string0, locale0);
	}

	/*
	 * java.lang.NoClassDefFoundError: org/jfree/date/MonthConstants
	 */
//	@Test
//	public void evoobj_jfreechart_DatasetUtilities_findItemIndicesForX_1() {
//	  // I21 Branch 255 IFEQ L1212;false
//	  // In-method
//	  DynamicTimeSeriesCollection dynamicTimeSeriesCollection0 = new DynamicTimeSeriesCollection(269, 8);
//	  int[] intArray0 = DatasetUtilities.findItemIndicesForX(dynamicTimeSeriesCollection0, (-3677), (-487.737));
//	}

	/*
	 * Complicated case (indirect null parameter)
	 */
	@Test
	public void evoobj_jfreechart_DatasetUtilities_findMinimumRangeValue_8() {
	  // I25 Branch 188 IFEQ L732;true
	  // In-method
	  GanttCategoryDataset ganttCategoryDataset0 = null;
	  int int0 = (-115);
	  int int1 = (-983);
	  SlidingGanttCategoryDataset slidingGanttCategoryDataset0 = new SlidingGanttCategoryDataset(ganttCategoryDataset0, int0, int1);
	  Number number0 = DatasetUtilities.findMinimumRangeValue(slidingGanttCategoryDataset0);
	}

	/*
	 * The type org.jfree.util.PublicCloneable cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_jfreechart_DatasetUtilities_findMinimumStackedRangeValue_3() {
//	  // I195 Branch 228 IFEQ L1067;false
//	  // In-method
//	  TaskSeriesCollection taskSeriesCollection0 = new TaskSeriesCollection();
//	  int int0 = 666;
//	  SlidingGanttCategoryDataset slidingGanttCategoryDataset0 = new SlidingGanttCategoryDataset(taskSeriesCollection0, int0, int0);
//	  Number number0 = DatasetUtilities.findMinimumStackedRangeValue(slidingGanttCategoryDataset0);
//	  String string0 = "`+DRj(!]A]^)~)I8O";
//	  String string1 = null;
//	  String string2 = "I";
//	  String string3 = "";
//	  JDBCCategoryDataset jDBCCategoryDataset0 = new JDBCCategoryDataset(string0, string1, string2, string3);
//	}

	/*
	 * java.lang.NoClassDefFoundError: org/jfree/date/MonthConstants
	 */
//	@Test
//	public void evoobj_jfreechart_DynamicTimeSeriesCollection_appendData_3() {
//	  // I788 Branch 36 IFEQ L650;true
//	  // In-method
//	  int int0 = 111;
//	  int int1 = 3190;
//	  DynamicTimeSeriesCollection dynamicTimeSeriesCollection0 = new DynamicTimeSeriesCollection(int0, int1);
//	  float[] floatArray0 = null;
//	  int int2 = 92;
//	  int int3 = 339;
//	  dynamicTimeSeriesCollection0.appendData(floatArray0, int2, int3);
//	}

	/*
	 * java.lang.NoClassDefFoundError: org/jfree/data/MonthConstants
	 */
//	@Test
//	public void evoobj_jfreechart_Second_equals_1() {
//	  // I69 Branch 5 IF_ICMPNE L262;true
//	  // Out-method
//	  long long0 = 901L;
//	  MockDate mockDate0 = new MockDate(long0);
//	  TimeZone timeZone0 = null;
//	  Second second0 = new Second(mockDate0, timeZone0);
//	  Object object0 = new Object();
//	  int int0 = 1255;
//	  int int1 = 46;
//	  int int2 = 2614;
//	  int int3 = 2943;
//	  int int4 = (-3224);
//	  int int5 = 0;
//	  MockGregorianCalendar mockGregorianCalendar0 = new MockGregorianCalendar(second0.DEFAULT_TIME_ZONE);
//	  long long1 = second0.getFirstMillisecond(mockGregorianCalendar0);
//	  boolean boolean0 = second0.equals(object0);
//	}

	/*
	 * The method getRawDataItem(RegularTimePeriod) from the type TimeSeries is not visible
	 */
//	@Test
//	public void evoobj_jfreechart_TimeSeries_getRawDataItem_0() {
//	  // I274 Branch 15 IF_ACMPNE L541;false
//	  // Out-method
//	  int int0 = (-1555);
//	  int int1 = 0;
//	  int int2 = (-4264);
//	  int int3 = 502;
//	  int int4 = 1422;
//	  int int5 = 193;
//	  MockDate mockDate0 = new MockDate(int0, int1, int2, int3, int4, int5);
//	  SimpleTimePeriod simpleTimePeriod0 = new SimpleTimePeriod(mockDate0, mockDate0);
//	  String string0 = "";
//	  String string1 = ", but the TimeSeries is expecting an instance of ";
//	  TimeSeries timeSeries0 = new TimeSeries(simpleTimePeriod0, string0, string1);
//	  MockDate mockDate1 = new MockDate(int3, int2, int3, int0, int3, int1);
//	  MockDate mockDate2 = new MockDate(int1, int5, int3, int2, int5, int4);
//	  Day day0 = new Day();
//	  double double0 = 0.0;
//	  TimeSeriesDataItem timeSeriesDataItem0 = timeSeries0.getRawDataItem(day0);
//	  boolean boolean0 = simpleTimePeriod0.equals(timeSeriesDataItem0);
//	  boolean boolean1 = true;
//	  String string2 = "xAnchor";
//	  timeSeries0.setDescription(string2);
//	  timeSeries0.add(day0, double0, boolean1);
//	  TimeSeries timeSeries1 = null;
//	  Collection<Object> collection0 = timeSeries0.getTimePeriodsUniqueToOtherSeries(timeSeries1);
//	  int int6 = 1167;
//	  Number number0 = timeSeries0.getValue(int6);
//	  String string3 = timeSeries0.getDomainDescription();
//	  List<Object> list0 = timeSeries0.getItems();
//	  Class<Object> class0 = timeSeries1.getTimePeriodClass();
//	  TimeSeriesDataItem timeSeriesDataItem1 = timeSeries0.getRawDataItem(day0);
//	}

	/*
	 * The type org.jfree.util.PublicCloneable cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_jfreechart_DefaultWindDataset_equals_7() {
//	  // I41 Branch 12 IFEQ L505;true
//	  // Out-method
//	  String[] stringArray0 = new String[2];
//	  String string0 = "Invalid series index: ";
//	  stringArray0[0] = string0;
//	  String string1 = "The number of series keys does not match the :number of series in the data array.";
//	  stringArray0[1] = string1;
//	  Object[][][] objectArrayArrayArray0 = null;
//	  DefaultWindDataset defaultWindDataset0 = new DefaultWindDataset(stringArray0, objectArrayArrayArray0);
//	  DefaultKeyedValues defaultKeyedValues0 = new DefaultKeyedValues();
//	  DefaultPieDataset defaultPieDataset0 = new DefaultPieDataset(defaultKeyedValues0);
//	  SeriesChangeEvent seriesChangeEvent0 = new SeriesChangeEvent(defaultPieDataset0);
//	  defaultWindDataset0.seriesChanged(seriesChangeEvent0);
//	  Object object0 = null;
//	  int int0 = (-1481);
//	  int int1 = 856;
//	  Number number0 = defaultWindDataset0.getWindDirection(int0, int1);
//	  int int2 = 0;
//	  int int3 = defaultWindDataset0.getItemCount(int2);
//	  int int4 = 620;
//	  boolean boolean0 = false;
//	  IntervalXYDelegate intervalXYDelegate0 = new IntervalXYDelegate(defaultWindDataset0, boolean0);
//	  defaultWindDataset0.addChangeListener(intervalXYDelegate0);
//	  int int5 = (-4886);
//	  Number number1 = defaultWindDataset0.getY(int4, int5);
//	  List<Object> list0 = new Vector<Float>(int4);
//	  List<Object> list1 = DefaultWindDataset.seriesNameListFromDataArray(objectArrayArrayArray0);
//	  boolean boolean1 = defaultWindDataset0.equals(object0);
//	}

	/*
	 * java.lang.NoClassDefFoundError: org/jfree/util/PublicCloneable
	 */
//	@Test
//	public void evoobj_jfreechart_DefaultWindDataset_getItemCount_1() {
//	  // I26 Branch 7 IFNULL L442;false
//	  // Out-method
//	  String[] stringArray0 = new String[8];
//	  String string0 = "tasks";
//	  stringArray0[0] = string0;
//	  String string1 = "! AS32CO_";
//	  stringArray0[1] = string1;
//	  String string2 = ")TbA0W87K";
//	  stringArray0[2] = string2;
//	  String string3 = "^&u%?|kAP8?$l";
//	  stringArray0[3] = string3;
//	  stringArray0[4] = stringArray0[3];
//	  String string4 = "";
//	  stringArray0[5] = string4;
//	  String string5 = "seriesKeys";
//	  stringArray0[6] = string5;
//	  String string6 = "v%#\\q-_Grci,x\\e";
//	  stringArray0[7] = string6;
//	  Object[][][] objectArrayArrayArray0 = null;
//	  DefaultWindDataset defaultWindDataset0 = new DefaultWindDataset(stringArray0, objectArrayArrayArray0);
//	  int int0 = 318;
//	  int int1 = defaultWindDataset0.getItemCount(int0);
//	}

	/*
	 * VFSDirectoryEntryTableModel cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_jedit_VFSDirectoryEntryTableModel_collapse_6() {
//	  // I64 Branch 7 IFNE L194;true
//	  // In-method
//	  VFSDirectoryEntryTableModel vFSDirectoryEntryTableModel0 = new VFSDirectoryEntryTableModel();
//	  UrlVFS urlVFS0 = new UrlVFS();
//	  vFSDirectoryEntryTableModel0.collapse(urlVFS0, 649);
//	}

//	@Test
//	public void evoobj_jedit_BSHArguments_getArguments_0() {
//	  // I17 Branch 2 IFEQ L152;false
//	  // In-method
//	  int int0 = 1709;
//	  BSHArguments bSHArguments0 = new BSHArguments(int0);
//	  BSHCastExpression bSHCastExpression0 = new BSHCastExpression(int0);
//	  bSHArguments0.jjtAddChild(bSHCastExpression0, int0);
//	  CallStack callStack0 = new CallStack();
//	  Interpreter interpreter0 = new Interpreter();
//	  System.setCurrentTimeMillis(int0);
//	  System.setCurrentTimeMillis(int0);
//	  System.setCurrentTimeMillis(int0);
//	  Object[] objectArray0 = bSHArguments0.getArguments(callStack0, interpreter0);
//	}

//	@Test
//	public void evoobj_jedit_BSHBlock_eval_7() {
//	  // I7 Branch 1 IFEQ L150;false
//	  // In-method
//	  int int0 = 334;
//	  BSHBlock bSHBlock0 = new BSHBlock(int0);
//	  CallStack callStack0 = null;
//	  String string0 = "K?RcGieW";
//	  StringReader stringReader0 = new StringReader(string0);
//	  MockFileOutputStream mockFileOutputStream0 = new MockFileOutputStream(string0, bSHBlock0.isSynchronized);
//	  MockPrintStream mockPrintStream0 = new MockPrintStream(string0);
//	  BshClassManager bshClassManager0 = new BshClassManager();
//	  NameSpace nameSpace0 = NameSpace.JAVACODE;
//	  Interpreter interpreter0 = new Interpreter();
//	  boolean boolean0 = false;
//	  Primitive primitive0 = (Primitive)bSHBlock0.eval(callStack0, interpreter0, boolean0);
//	}

//	@Test
//	public void evoobj_jedit_BSHBlock_evalBlock_6() {
//	  // I6 Branch 4 IFEQ L176;false
//	  // In-method
//	  boolean boolean0 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  BSHBlock bSHBlock0 = new BSHBlock(79);
//	  ExternalNameSpace externalNameSpace0 = new ExternalNameSpace();
//	  BshClassManager bshClassManager0 = new BshClassManager();
//	  BlockNameSpace blockNameSpace0 = new BlockNameSpace(externalNameSpace0);
//	  NameSpace nameSpace0 = NameSpace.JAVACODE;
//	  CallStack callStack0 = new CallStack(externalNameSpace0);
//	  BSHCastExpression bSHCastExpression0 = new BSHCastExpression(3423);
//	  bSHBlock0.jjtAddChild(bSHCastExpression0, 537);
//	  Interpreter interpreter0 = new Interpreter();
//	  Primitive primitive0 = (Primitive)bSHBlock0.evalBlock(callStack0, interpreter0, false, (BSHBlock.NodeFilter) null);
//	}

//	@Test
//	public void evoobj_jedit_BSHForStatement_eval_1() {
//	  // I7 Branch 1 IFEQ L150;false
//	  // In-method
//	  int int0 = 33;
//	  BSHForStatement bSHForStatement0 = new BSHForStatement(int0);
//	  BshClassManager bshClassManager0 = new BshClassManager();
//	  boolean boolean0 = true;
//	  bSHForStatement0.hasExpression = boolean0;
//	  ExternalNameSpace externalNameSpace0 = new ExternalNameSpace();
//	  CallStack callStack0 = new CallStack(externalNameSpace0);
//	  String string0 = "";
//	  bSHForStatement0.setSourceFile(string0);
//	  Interpreter interpreter0 = null;
//	  long long0 = (-2059L);
//	  System.setCurrentTimeMillis(long0);
//	  Object object0 = bSHForStatement0.eval(callStack0, interpreter0);
//	}

//	@Test
//	public void evoobj_jedit_BSHIfStatement_eval_7() {
//	  // I42 Branch 3 IFEQ L158;false
//	  // In-method
//	  BSHIfStatement bSHIfStatement0 = new BSHIfStatement((-2011));
//	  CallStack callStack0 = new CallStack();
//	  Object object0 = bSHIfStatement0.eval(callStack0, (Interpreter) null);
//	}

//	@Test
//	public void evoobj_jedit_BSHReturnStatement_eval_8() {
//	  // I7 Branch 1 IFEQ L150;false
//	  // Out-method
//	  int int0 = 13;
//	  BSHReturnStatement bSHReturnStatement0 = new BSHReturnStatement(int0);
//	  ExternalNameSpace externalNameSpace0 = new ExternalNameSpace();
//	  CallStack callStack0 = new CallStack(externalNameSpace0);
//	  String string0 = "ydwA";
//	  StringReader stringReader0 = new StringReader(string0);
//	  OutputStream outputStream0 = null;
//	  boolean boolean0 = true;
//	  MockPrintStream mockPrintStream0 = new MockPrintStream(outputStream0, boolean0);
//	  boolean boolean1 = false;
//	  Interpreter interpreter0 = null;
//	  String string1 = "";
//	  Interpreter interpreter1 = new Interpreter(stringReader0, mockPrintStream0, mockPrintStream0, boolean1, externalNameSpace0, interpreter0, string1);
//	  Object object0 = bSHReturnStatement0.eval(callStack0, interpreter1);
//	}

//	@Test
//	public void evoobj_jedit_BSHReturnType_getTypeDescriptor_2() {
//	  // I7 Branch 1 IFEQ L150;false
//	  // In-method
//	  int int0 = 1753;
//	  BSHReturnType bSHReturnType0 = new BSHReturnType(int0);
//	  BshClassManager bshClassManager0 = new BshClassManager();
//	  NameSpace nameSpace0 = NameSpace.JAVACODE;
//	  NameSpace nameSpace1 = NameSpace.JAVACODE;
//	  CallStack callStack0 = new CallStack();
//	  String string0 = "ThOe class ";
//	  StringReader stringReader0 = new StringReader(string0);
//	  ByteArrayOutputStream byteArrayOutputStream0 = new ByteArrayOutputStream(int0);
//	  FilterOutputStream filterOutputStream0 = new FilterOutputStream(byteArrayOutputStream0);
//	  BufferedOutputStream bufferedOutputStream0 = new BufferedOutputStream(filterOutputStream0);
//	  MockPrintStream mockPrintStream0 = new MockPrintStream(bufferedOutputStream0);
//	  Interpreter interpreter0 = new Interpreter();
//	  String string1 = "mG*V.\\jbKc=79";
//	  String string2 = bSHReturnType0.getTypeDescriptor(callStack0, interpreter0, string1);
//	}

//	@Test
//	public void evoobj_jedit_ClassGeneratorUtil_generateReturnCode_4() {
//	  // I80 Branch 74 IFEQ L891;false
//	  // In-method
//	  String string0 = "D";
//	  CodeVisitor codeVisitor0 = null;
//	  ClassGeneratorUtil.generateReturnCode(string0, codeVisitor0);
//	}

//	@Test
//	public void evoobj_jedit_Interpreter_main_1() {
//	  // I82 Branch 9 IFNE L200;true
//	  // In-method
//	  Properties properties0 = System.getProperties();
//	  String[] stringArray0 = new String[1];
//	  Interpreter.main(stringArray0);
//	}

//	@Test
//	public void evoobj_jedit_NameSpace_getClassManager_3() {
//	  // I20 Branch 24 IFNE L342;true
//	  // Out-method
//	  BshClassManager bshClassManager0 = new BshClassManager();
//	  String string0 = "rr+aY,Lw=>e";
//	  NameSpace nameSpace0 = new NameSpace(bshClassManager0, string0);
//	  String string1 = "/";
//	  NameSpace nameSpace1 = new NameSpace(nameSpace0, bshClassManager0, string1);
//	  String string2 = "int";
//	  Primitive primitive0 = (Primitive)nameSpace1.JAVACODE.getVariable(string2);
//	  String string3 = "org.gjt.sp.jedit.bsh.NameSpace$CommandPathEntry";
//	  String string4 = "ReturnType";
//	  NameSpace nameSpace2 = new NameSpace(nameSpace1, string4);
//	  String string5 = "org/gjt/sp/jedit/bsh/ClassGeneratorUtil$ConstructorArgs";
//	  Class<Object> class0 = nameSpace2.getClass(string5);
//	  boolean boolean0 = false;
//	  ReflectManagerImpl reflectManagerImpl0 = (ReflectManagerImpl)ReflectManager.getReflectManager();
//	  Interpreter.LOCALSCOPING = boolean0;
//	  String string6 = " from source: ";
//	  Variable variable0 = nameSpace2.getImportedVar(string6);
//	  boolean boolean1 = true;
//	  nameSpace0.setLocalVariable(string3, variable0, boolean1);
//	  Class<BshClassManager> class1 = BshClassManager.class;
//	  String string7 = null;
//	  Class[] classArray0 = (Class[]) Array.newInstance(Class.class, 2);
//	  Class<Integer> class2 = Integer.class;
//	  classArray0[0] = class2;
//	  Class<Integer> class3 = Integer.class;
//	  classArray0[1] = class3;
//	  boolean boolean2 = false;
//	  Method method0 = bshClassManager0.getResolvedMethod(class1, string7, classArray0, boolean2);
//	  String string8 = "KYm_~q'(%}\"n] [";
//	  nameSpace1.classLoaderChanged();
//	  NameSpace nameSpace3 = new NameSpace(nameSpace1, string8);
//	  Object object0 = nameSpace1.getClassInstance();
//	  String string9 = "|jC";
//	  String string10 = nameSpace3.JAVACODE.getPackage();
//	  nameSpace3.setPackage(string9);
//	  String string11 = "$+(";
//	  Object[] objectArray0 = new Object[0];
//	  String string12 = "9r@fm;a2";
//	  StringReader stringReader0 = new StringReader(string12);
//	  int int0 = 0;
//	  String string13 = "i\"zMKm5_BV*+Ce[#\\yh";
//	  Class<Integer> class4 = Integer.class;
//	  String string14 = "/org/gjt/sp/jedit/bsh/commands/";
//	  Modifiers modifiers0 = new Modifiers();
//	  String string15 = "/org/gjt/sp/jedit/bsh/commands/";
//	  boolean boolean3 = modifiers0.hasModifier(string15);
//	  String string16 = modifiers0.toString();
//	  nameSpace1.JAVACODE.setTypedVariable(string13, class4, string14, modifiers0);
//	  String string17 = "~.a";
//	  Class<String> class5 = String.class;
//	  nameSpace3.removeCommandPath(string17, class5);
//	  ByteArrayOutputStream byteArrayOutputStream0 = new ByteArrayOutputStream(int0);
//	  boolean boolean4 = true;
//	  String string18 = "4e*qX]Ns)";
//	  MockPrintStream mockPrintStream0 = new MockPrintStream(byteArrayOutputStream0, boolean4, string18);
//	  boolean boolean5 = true;
//	  char[] charArray0 = new char[7];
//	  char char0 = 'c';
//	  charArray0[0] = char0;
//	  char char1 = '\'';
//	  charArray0[1] = char1;
//	  char char2 = '^';
//	  charArray0[2] = char2;
//	  char char3 = '1';
//	  charArray0[3] = char3;
//	  char char4 = '^';
//	  String string19 = nameSpace1.JAVACODE.getInvocationText();
//	  charArray0[4] = char4;
//	  char char5 = '4';
//	  nameSpace3.JAVACODE.clear();
//	  charArray0[5] = char5;
//	  char char6 = '2';
//	  charArray0[6] = char6;
//	  byte[] byteArray0 = new byte[0];
//	  mockPrintStream0.write(byteArray0);
//	  int int1 = stringReader0.read(charArray0);
//	  boolean boolean6 = false;
//	  Interpreter interpreter0 = new Interpreter(stringReader0, mockPrintStream0, mockPrintStream0, boolean6, nameSpace1);
//	  String string20 = "/";
//	  Interpreter interpreter1 = new Interpreter(stringReader0, mockPrintStream0, mockPrintStream0, boolean5, nameSpace2, interpreter0, string20);
//	  Object object1 = nameSpace0.invokeMethod(string11, objectArray0, interpreter1);
//	  nameSpace1.setClassInstance(object1);
//	  String string21 = "t|-#{Nn8J$A";
//	  InputStream inputStream0 = nameSpace2.getCommand(string21);
//	  String string22 = "/";
//	  mockPrintStream0.println();
//	  Class<Object> class6 = nameSpace1.getClass(string22);
//	  This this0 = nameSpace3.getGlobal(interpreter1);
//	  String string23 = null;
//	  This this1 = nameSpace3.JAVACODE.getThis(interpreter0);
//	  interpreter1.sourceFileInfo = string23;
//	  Object object2 = nameSpace1.getClassInstance();
//	  interpreter0.println(object2);
//	  Class<Name> class7 = Name.class;
//	  nameSpace2.setClassStatic(class7);
//	  Parser parser0 = new Parser(stringReader0);
//	  interpreter1.parser = parser0;
//	  boolean boolean7 = false;
//	  Interpreter interpreter2 = new Interpreter(stringReader0, mockPrintStream0, mockPrintStream0, boolean7, nameSpace3);
//	  Interpreter.sharedObject = this0;
//	  This this2 = nameSpace0.getSuper(interpreter2);
//	  String string24 = null;
//	  InputStream inputStream1 = nameSpace3.getCommand(string24);
//	  nameSpace3.classLoaderChanged();
//	  int int2 = (-344);
//	  BSHPrimitiveType bSHPrimitiveType0 = new BSHPrimitiveType(int2);
//	  String string25 = "Hb/";
//	  int int3 = 0;
//	  Integer integer0 = new Integer(int3);
//	  nameSpace0.setVariable(string25, integer0);
//	  nameSpace3.setNode(bSHPrimitiveType0);
//	  Variable[] variableArray0 = nameSpace1.getDeclaredVariables();
//	  String[] stringArray0 = nameSpace1.getAllNames();
//	  This this3 = nameSpace2.getGlobal(interpreter2);
//	  nameSpace1.setClassManager(bshClassManager0);
//	  String string26 = "/";
//	  String string27 = "";
//	  InputStream inputStream2 = nameSpace3.JAVACODE.getCommand(string27);
//	  nameSpace3.setPackage(string26);
//	  Class<Name> class8 = Name.class;
//	  nameSpace1.importStatic(class8);
//	  String string28 = "<LETTER>";
//	  Class<String> class9 = String.class;
//	  nameSpace0.removeCommandPath(string28, class9);
//	  String string29 = "Typed variable: ";
//	  nameSpace1.importCommands(string29);
//	  BshClassManager bshClassManager1 = nameSpace3.getClassManager();
//	}

//	@Test
//	public void evoobj_jedit_NameSpace_getPackage_0() {
//	  // I67 Branch 129 IFNONNULL L1397;true
//	  // Out-method
//	  BshClassManager bshClassManager0 = new BshClassManager();
//	  String string0 = "31}Cb }uGyh";
//	  NameSpace nameSpace0 = new NameSpace(bshClassManager0, string0);
//	  String string1 = null;
//	  NameSpace nameSpace1 = new NameSpace(nameSpace0, string1);
//	  String string2 = "6$xY}=Sx,_.5M)#";
//	  NameSpace nameSpace2 = new NameSpace(nameSpace1, bshClassManager0, string2);
//	  String string3 = "s";
//	  SimpleNode simpleNode0 = nameSpace2.getNode();
//	  nameSpace1.importCommands(string3);
//	  String string4 = null;
//	  String string5 = "";
//	  nameSpace2.JAVACODE.importClass(string5);
//	  Object object0 = null;
//	  boolean boolean0 = true;
//	  boolean boolean1 = true;
//	  nameSpace1.setVariable(string4, object0, boolean0, boolean1);
//	  String string6 = nameSpace2.getPackage();
//	}

//	@Test
//	public void evoobj_jedit_Parser_VariableDeclarator_0() {
//	  // I172 Branch 114 IFNE L1253;false
//	  // Out-method
//	  Parser parser0 = new Parser((ParserTokenManager) null);
//	  parser0.RelationalExpression();
//	  parser0.VariableDeclarator();
//	}

//	@Test
//	public void evoobj_jedit_Primitive_wrap_6() {
//	  // I247 Branch 273 IFNONNULL L175;false
//	  // In-method
//	  Object[] objectArray0 = new Object[3];
//	  Object object0 = new Object();
//	  objectArray0[0] = object0;
//	  Class[] classArray0 = null;
//	  Object[] objectArray1 = Primitive.wrap(objectArray0, classArray0);
//	}

//	@Test
//	public void evoobj_jedit_JEditBuffer_getLineText_5() {
//	  // I36 Branch 30 IFEQ L433;false
//	  // Out-method
//	  JEditEmbeddedTextArea jEditEmbeddedTextArea0 = new JEditEmbeddedTextArea();
//	  ElasticTabStopBufferListener elasticTabStopBufferListener0 = new ElasticTabStopBufferListener(jEditEmbeddedTextArea0);
//	  int int0 = 0;
//	  JEditBuffer.Listener jEditBuffer_Listener0 = new JEditBuffer.Listener(elasticTabStopBufferListener0, int0);
//	}

//	@Test
//	public void evoobj_jedit_KillRing_add_5() {
//	  // I173 Branch 17 IFNE L223;false
//	  // In-method
//	  KillRing killRing0 = new KillRing();
//	  UndoManager.RemovedContent undoManager_RemovedContent0 = new UndoManager.RemovedContent("page");
//	  killRing0.add(undoManager_RemovedContent0);
//	}

//	@Test
//	public void evoobj_jedit_BufferSet_sort_0() {
//	  // I29 Branch 25 IFNE L342;true
//	  // Out-method
//	  BufferSet bufferSet0 = new BufferSet((BufferSet) null);
//	  bufferSet0.sort();
//	}

//	@Test
//	public void evoobj_jedit_DockingLayoutManager_saveAs_7() {
//	  // I6 Branch 4 IFEQ L176;false
//	  // In-method
//	  View view0 = null;
//	  DockingLayoutManager.saveAs(view0);
//	}

//	@Test
//	public void evoobj_jedit_HistoryTextField_processMouseEvent_4() {
//	  // I46 Branch 20 IFNULL L321;false
//	  // In-method
//	  HistoryTextField historyTextField0 = new HistoryTextField();
//	  int int0 = 501;
//	  long long0 = 4011L;
//	  int int1 = 5;
//	  int int2 = 50;
//	  int int3 = 148;
//	  historyTextField0.requestFocus();
//	  int int4 = (-50);
//	  boolean boolean0 = true;
//	  MenuElement[] menuElementArray0 = new MenuElement[2];
//	  String string0 = "org.gjt.sp.jedit.help.HelpViewer$LinkHandler";
//	  ImageIcon imageIcon0 = new ImageIcon(string0);
//	  JCheckBoxMenuItem jCheckBoxMenuItem0 = new JCheckBoxMenuItem();
//	  menuElementArray0[0] = (MenuElement) jCheckBoxMenuItem0;
//	  String string1 = "";
//	  boolean boolean1 = true;
//	  JRadioButtonMenuItem jRadioButtonMenuItem0 = new JRadioButtonMenuItem(string1, imageIcon0, boolean1);
//	  menuElementArray0[1] = (MenuElement) jRadioButtonMenuItem0;
//	  MenuSelectionManager menuSelectionManager0 = null;
//	  MenuDragMouseEvent menuDragMouseEvent0 = new MenuDragMouseEvent(historyTextField0, int0, long0, int1, int2, int3, int4, boolean0, menuElementArray0, menuSelectionManager0);
//	  JRootPane jRootPane0 = new JRootPane();
//	  HistoryTextField.MouseHandler historyTextField_MouseHandler0 = historyTextField0.new MouseHandler();
//	  historyTextField_MouseHandler0.mouseReleased(menuDragMouseEvent0);
//	  historyTextField_MouseHandler0.mouseDragged(menuDragMouseEvent0);
//	  String string2 = JTextComponent.FOCUS_ACCELERATOR_KEY;
//	  historyTextField_MouseHandler0.mousePressed(menuDragMouseEvent0);
//	  String string3 = "gj3>E<V/j%7A>\"";
//	  historyTextField0.setModel(string3);
//	  historyTextField0.processMouseEvent(menuDragMouseEvent0);
//	}

//	@Test
//	public void evoobj_jedit_KeyEventWorkaround_processKeyEvent_6() {
//	  // I12 Branch 130 IFNONNULL L1467;true
//	  // In-method
//	  KeyEvent keyEvent0 = null;
//	  KeyEventWorkaround.numericKeypadKey();
//	  KeyEventWorkaround keyEventWorkaround0 = new KeyEventWorkaround();
//	  Log.closeStream();
//	  boolean boolean0 = KeyEventWorkaround.isMacControl(keyEvent0);
//	  Debug.ALT_KEY_PRESSED_DISABLED = boolean0;
//	  KeyEventWorkaround.numericKeypadKey();
//	  int int0 = (-699);
//	  Log.MAXLINES = int0;
//	  int int1 = 1001;
//	  Log.MAXLINES = int1;
//	  Log.closeStream();
//	  boolean boolean1 = false;
//	  Debug.DUMP_KEY_EVENTS = boolean1;
//	  int int2 = 310;
//	  Log.MAXLINES = int2;
//	  boolean boolean2 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  boolean boolean3 = KeyEventWorkaround.isMacControl(keyEvent0);
//	  boolean boolean4 = KeyEventWorkaround.isMacControl(keyEvent0);
//	  boolean boolean5 = KeyEventWorkaround.isMacControl(keyEvent0);
//	  boolean boolean6 = KeyEventWorkaround.isNumericKeypad(int0);
//	  KeyEvent keyEvent1 = KeyEventWorkaround.processKeyEvent(keyEvent0);
//	}

//	@Test
//	public void evoobj_jedit_PasteSpecialDialog_ok_5() {
//	  // I6 Branch 4 IFEQ L176;false
//	  // Out-method
//	  View view0 = null;
//	  JEditEmbeddedTextArea jEditEmbeddedTextArea0 = new JEditEmbeddedTextArea();
//	  PasteSpecialDialog pasteSpecialDialog0 = new PasteSpecialDialog(view0, jEditEmbeddedTextArea0);
//	  pasteSpecialDialog0.ok();
//	}

//	@Test
//	public void evoobj_jedit_OpenBracketIndentRule_apply_1() {
//	  // I7 Branch 1 IFEQ L150;true
//	  // In-method
//	  char char0 = 'r';
//	  boolean boolean0 = false;
//	  OpenBracketIndentRule openBracketIndentRule0 = new OpenBracketIndentRule(char0, boolean0);
//	  int int0 = 1032;
//	  float float0 = 2828.0F;
//	  Hashtable<IndentAction.AlignOffset, Integer> hashtable0 = new Hashtable<IndentAction.AlignOffset, Integer>(int0, float0);
//	  HashMap<IndentAction.AlignOffset, Object> hashMap0 = new HashMap<IndentAction.AlignOffset, Object>(char0);
//	  JEditBuffer jEditBuffer0 = new JEditBuffer(hashMap0);
//	  int int1 = (-62);
//	  int int2 = 0;
//	  int int3 = (-539);
//	  Comparator<Object> comparator0 = null;
//	  PriorityQueue<IndentAction.NoIncrease> priorityQueue0 = new PriorityQueue<IndentAction.NoIncrease>(comparator0);
//	  ArrayList<IndentAction.NoIncrease> arrayList0 = new ArrayList<IndentAction.NoIncrease>(priorityQueue0);
//	  LinkedList<IndentAction> linkedList0 = new LinkedList<IndentAction>(arrayList0);
//	  openBracketIndentRule0.apply(jEditBuffer0, int1, int2, int3, linkedList0);
//	}

//	@Test
//	public void evoobj_jedit_jEdit_pluginError_0() {
//	  // I15 Branch 246 IF_ICMPGE L297;true
//	  // In-method
//	  String string0 = "-server=";
//	  String string1 = "cd";
//	  Object[] objectArray0 = new Object[3];
//	  jEdit.pluginError(string0, string1, objectArray0);
//	}

//	@Test
//	public void evoobj_jedit_EnhancedCheckBoxMenuItem_paint_4() {
//	  // I58 Branch 6 IFNE L194;true
//	  // Out-method
//	  EnhancedCheckBoxMenuItem enhancedCheckBoxMenuItem0 = new EnhancedCheckBoxMenuItem("readonly.gxif", "", (ActionContext) null);
//	  DebugGraphics debugGraphics0 = new DebugGraphics();
//	  enhancedCheckBoxMenuItem0.paint(debugGraphics0);
//	}

//	@Test
//	public void evoobj_jedit_ReloadWithEncodingProvider_actionPerformed_4() {
//	  // I64 Branch 7 IFNE L194;false
//	  // In-method
//	  ReloadWithEncodingProvider reloadWithEncodingProvider0 = new ReloadWithEncodingProvider();
//	  byte[] byteArray0 = new byte[9];
//	  byte byte0 = (byte) (-103);
//	  BufferHistory.load();
//	  byteArray0[0] = byte0;
//	  byteArray0[1] = byteArray0[0];
//	  byteArray0[2] = byteArray0[1];
//	  byteArray0[3] = byteArray0[2];
//	  byteArray0[3] = byteArray0[3];
//	  byteArray0[4] = byteArray0[1];
//	  byteArray0[5] = byteArray0[4];
//	  byteArray0[6] = byteArray0[3];
//	  Boolean boolean0 = Boolean.TRUE;
//	  byteArray0[5] = byteArray0[5];
//	  String string0 = "other-encoding";
//	  byteArray0[2] = byteArray0[2];
//	  ImageIcon imageIcon0 = new ImageIcon();
//	  JRadioButtonMenuItem jRadioButtonMenuItem0 = new JRadioButtonMenuItem(string0);
//	  int int0 = (-1);
//	  ImageIcon imageIcon1 = new ImageIcon(string0);
//	  String string1 = "-SplitPaneDivider.border";
//	  ActionEvent actionEvent0 = new ActionEvent(jRadioButtonMenuItem0, int0, string1);
//	  Log.closeStream();
//	  Boolean boolean1 = Boolean.valueOf(string1);
//	  jRadioButtonMenuItem0.setMnemonic(byte0);
//	  MenuKeyListener menuKeyListener0 = null;
//	  jRadioButtonMenuItem0.removeMenuKeyListener(menuKeyListener0);
//	  System.setCurrentTimeMillis(byteArray0[0]);
//	  Boolean boolean2 = Boolean.TRUE;
//	  System.setCurrentTimeMillis(byteArray0[2]);
//	  boolean boolean3 = reloadWithEncodingProvider0.updateEveryTime();
//	  int int1 = (-627);
//	  MenuDragMouseListener menuDragMouseListener0 = null;
//	  jRadioButtonMenuItem0.removeMenuDragMouseListener(menuDragMouseListener0);
//	  int int2 = 623;
//	  jRadioButtonMenuItem0.repaint(int1, int0, int2, byteArray0[0]);
//	  System.setCurrentTimeMillis(byteArray0[5]);
//	  reloadWithEncodingProvider0.actionPerformed(actionEvent0);
//	}

//	@Test
//	public void evoobj_jedit_ShortcutsOptionPane__save_0() {
//	  // I7 Branch 1 IFEQ L150;true
//	  // In-method
//	  ShortcutsOptionPane shortcutsOptionPane0 = new ShortcutsOptionPane();
//	  shortcutsOptionPane0._save();
//	}

//	@Test
//	public void evoobj_jedit_InstallPanel_handleMessage_3() {
//	  // I17 Branch 2 IFEQ L152;false
//	  // Out-method
//	  InstallPanel installPanel0 = new InstallPanel((PluginManager) null, false);
//	  EditorExiting editorExiting0 = new EditorExiting(installPanel0);
//	  installPanel0.handleMessage(editorExiting0);
//	}

//	@Test
//	public void evoobj_jedit_PluginList_dump_4() {
//	  // I6 Branch 19 IFEQ L310;false
//	  // Out-method
//	  PluginList pluginList0 = new PluginList((Task) null);
//	  pluginList0.dump();
//	}

//	@Test
//	public void evoobj_jedit_PluginListHandler_endElement_7() {
//	  // I20 Branch 24 IFNE L342;true
//	  // Out-method
//	  File file0 = null;
//	  String string0 = ",Ob_P7.PoCX=H";
//	  String string1 = "Gj";
//	  LocalFileSaveTask localFileSaveTask0 = new LocalFileSaveTask(file0, string0, string1);
//	  PluginList pluginList0 = new PluginList(localFileSaveTask0);
//	  String string2 = "FROM";
//	  PluginListHandler pluginListHandler0 = new PluginListHandler(pluginList0, string2);
//	  String string3 = "J(\\OhF75\"J^}if$&";
//	  String string4 = "wGd-Lu1;kumD4^hbgA";
//	  String string5 = "hl-<'";
//	  pluginListHandler0.endElement(string3, string4, string5);
//	}

//	@Test
//	public void evoobj_jedit_PluginResURLConnection_connect_6() {
//	  // I41 Branch 5 IFNE L189;false
//	  // Out-method
//	  PluginResURLConnection pluginResURLConnection0 = new PluginResURLConnection((URL) null);
//	  pluginResURLConnection0.connect();
//	}

//	@Test
//	public void evoobj_jedit_ParserRuleSet_addRule_2() {
//	  // I69 Branch 8 IFNULL L196;false
//	  // In-method
//	  ParserRuleSet parserRuleSet0 = new ParserRuleSet(")", "Wt{[=gO");
//	  parserRuleSet0.addRule((ParserRule) null);
//	}

//	@Test
//	public void evoobj_jedit_SyntaxUtilities_regionMatches_7() {
//	  // I17 Branch 2 IFEQ L152;true
//	  // In-method
//	  boolean boolean0 = true;
//	  char[] charArray0 = null;
//	  int int0 = 0;
//	  int int1 = 1774;
//	  Segment segment0 = new Segment(charArray0, int0, int1);
//	  int int2 = 0;
//	  char[] charArray1 = new char[2];
//	  char char0 = 'D';
//	  charArray1[0] = char0;
//	  char char1 = ')';
//	  charArray1[1] = char1;
//	  char char2 = 'M';
//	  charArray1[1] = char2;
//	  SyntaxUtilities syntaxUtilities0 = new SyntaxUtilities();
//	  boolean boolean1 = SyntaxUtilities.regionMatches(boolean0, segment0, int2, charArray1);
//	}

//	@Test
//	public void evoobj_jedit_BufferHandler_transactionComplete_5() {
//	  // I230 Branch 40 IFEQ L483;true
//	  // In-method
//	  BufferHandler bufferHandler0 = new BufferHandler((DisplayManager) null, (TextArea) null, (JEditBuffer) null);
//	  bufferHandler0.transactionComplete((JEditBuffer) null);
//	}

//	@Test
//	public void evoobj_jedit_Gutter_setBuffer_2() {
//	  // I6 Branch 19 IFEQ L310;true
//	  // Out-method
//	  JEditEmbeddedTextArea jEditEmbeddedTextArea0 = new JEditEmbeddedTextArea();
//	  Gutter gutter0 = jEditEmbeddedTextArea0.getGutter();
//	  JEditBuffer jEditBuffer0 = new JEditBuffer();
//	  gutter0.setBuffer(jEditBuffer0);
//	}

//	@Test
//	public void evoobj_jedit_InputMethodSupport_getLocationOffset_1() {
//	  // I58 Branch 6 IFNE L194;false
//	  // Out-method
//	  JEditEmbeddedTextArea jEditEmbeddedTextArea0 = new JEditEmbeddedTextArea();
//	  InputMethodSupport inputMethodSupport0 = new InputMethodSupport(jEditEmbeddedTextArea0);
//	  int int0 = 494;
//	  int int1 = 7;
//	  TextHitInfo textHitInfo0 = inputMethodSupport0.getLocationOffset(int0, int1);
//	}

//	@Test
//	public void evoobj_jedit_HtmlUtilities_appendString2html_0() {
//	  // I69 Branch 8 IFNULL L196;true
//	  // In-method
//	  StringBuilder stringBuilder0 = new StringBuilder();
//	  HtmlUtilities.appendString2html(stringBuilder0, "o=0r$mCu9&G{>Z");
//	  StringBuilder stringBuilder1 = new StringBuilder();
//	  StringBuffer stringBuffer0 = new StringBuffer(stringBuilder1);
//	  StringBuilder stringBuilder2 = new StringBuilder(stringBuffer0);
//	  StringBuilder stringBuilder3 = stringBuilder2.append(true);
//	  StringBuilder stringBuilder4 = stringBuilder2.replace(0, 506, "");
//	  LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
//	  LinkedList<Integer> linkedList1 = new LinkedList<Integer>(linkedList0);
//	  String string0 = HtmlUtilities.highlightString("#000000I", "", linkedList1);
//	  StringBuilder stringBuilder5 = stringBuilder2.append((String) null);
//	  LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
//	  String string1 = HtmlUtilities.highlightString("view.style.", "'qa87dnoaA EbQPE", linkedList2);
//	  IntStream intStream0 = stringBuilder2.codePoints();
//	  boolean boolean0 = linkedList1.containsAll(linkedList2);
//	  linkedList2.forEach((Consumer<? super Integer>) null);
//	}

	/*
	 * The method addResource(ResourceDescriptor, LinkedList<String>) from the type ResourcesDirectory is not visible
	 */
//	@Test
//	public void evoobj_jigen_ResourcesDirectory_addResource_3() {
//	  // I9 Branch 4 IFNULL L42;true
//	  // In-method
//	  ResourcesDirectory resourcesDirectory0 = new ResourcesDirectory();
//	  ResourceDescriptor resourceDescriptor0 = null;
//	  LinkedList<String> linkedList0 = new LinkedList<String>();
//	  String string0 = "@^3mD;\\j8bF]`t\"z4Vu";
//	  String string1 = resourcesDirectory0.getFolderName();
//	  boolean boolean0 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  linkedList0.addFirst(string1);
//	  linkedList0.push(string0);
//	  boolean boolean1 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  TreeMap<String, ResourceDescriptor> treeMap0 = resourcesDirectory0.getResources();
//	  resourcesDirectory0.addResource(resourceDescriptor0, linkedList0);
//	}

	/*
	 * Operator cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_jnfe_EnvNFe_equals_2() {
//	  // I77 Branch 10 IFNULL L88;true
//	  // In-method
//	  Operator operator0 = new Operator();
//	  Entity entity0 = new Entity();
//	  Emitente emitente0 = new Emitente();
//	  JNFeSerie jNFeSerie0 = new JNFeSerie();
//	  EnvNFe envNFe0 = new EnvNFe(jNFeSerie0);
//	  long long0 = 0L;
//	  EnvNFe envNFe1 = new EnvNFe(jNFeSerie0);
//	  Long long1 = new Long(long0);
//	  String string0 = null;
//	  int int0 = 694;
//	  JNFeSerie jNFeSerie1 = new JNFeSerie(emitente0, string0, int0);
//	  envNFe0.setSerie(jNFeSerie1);
//	  boolean boolean0 = envNFe0.equals(envNFe1);
//	}

	/*
	 * The field SharedListSelectionHandlerFormation.ligneSelectionneFormation is not visible
	 */
//	@Test
//	public void evoobj_gfarcegestionfa_SharedListSelectionHandlerFormation_valueChanged_7() {
//	  // I16 Branch 2 IFEQ L68;false
//	  // In-method
//	  SharedListSelectionHandlerFormation sharedListSelectionHandlerFormation0 = new SharedListSelectionHandlerFormation();
//	  Object object0 = new Object();
//	  int int0 = 2297;
//	  int int1 = 532;
//	  Vector<String> vector0 = new Vector<String>(int1, int0);
//	  Stack<Object> stack0 = new Stack<Object>();
//	  SharedListSelectionHandlerFormation.ligneSelectionneFormation = int1;
//	  JTable jTable0 = new JTable(SharedListSelectionHandlerFormation.ligneSelectionneFormation, int1);
//	  int int2 = 924;
//	  boolean boolean0 = false;
//	  boolean boolean1 = false;
//	  jTable0.changeSelection(int2, int2, boolean0, boolean1);
//	  FenetrePrincipale.tableFormation = jTable0;
//	  boolean boolean2 = false;
//	  ListSelectionEvent listSelectionEvent0 = new ListSelectionEvent(object0, int0, int1, boolean2);
//	  long long0 = 748L;
//	  System.setCurrentTimeMillis(long0);
//	  sharedListSelectionHandlerFormation0.valueChanged(listSelectionEvent0);
//	}

	/*
	 * Used parameter not set (sessionManager)
	 */
	@Test
	public void evoobj_jsecurity_DelegatingSession_setAttribute_5() {
	  // I21 Branch 4 IFEQ L151;false
	  // In-method
	  DelegatingSession delegatingSession0 = new DelegatingSession();
	  Object object0 = new Object();
	  Proxy.Type proxy_Type0 = Proxy.Type.HTTP;
	  delegatingSession0.setAttribute(object0, proxy_Type0);
	}

	/*
	 * Used parameter not set (username)
	 */
	@Test
	public void evoobj_jsecurity_UsernamePrincipal_equals_3() {
	  // I13 Branch 2 IFEQ L135;true
	  // In-method
	  UsernamePrincipal usernamePrincipal0 = new UsernamePrincipal();
	  StringPrincipal stringPrincipal0 = new StringPrincipal("zS:PJl");
	  boolean boolean0 = usernamePrincipal0.equals(stringPrincipal0);
	}

	/*
	 * The type org.springframework.web.servlet.handler.HandlerInterceptorAdapter cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_jsecurity_AuthenticationInterceptor_preHandle_4() {
//	  // I30 Branch 3 IFEQ L136;false
//	  // In-method
//	  AuthenticationInterceptor authenticationInterceptor0 = new AuthenticationInterceptor();
//	  HttpServletRequest httpServletRequest0 = null;
//	  HttpServletResponse httpServletResponse0 = null;
//	  SunHints.Key sunHints_Key0 = (SunHints.Key)RenderingHints.KEY_ANTIALIASING;
//	  boolean boolean0 = authenticationInterceptor0.preHandle(httpServletRequest0, httpServletResponse0, sunHints_Key0);
//	}

	/*
	 * LocalVar cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_jmca_LocalVar_closeState_6() {
//	  // I3 Branch 4 IFEQ L108;true
//	  // In-method
//	  SaxProcessor saxProcessor0 = new SaxProcessor();
//	  LocalVar localVar0 = new LocalVar(saxProcessor0);
//	  localVar0.processState(";w{l7!\u0081mq:f$");
//	  localVar0.processState(",q7-[&\"\"taZJA@(Jn*");
//	  Attributes2Impl attributes2Impl0 = new Attributes2Impl();
//	  String string0 = attributes2Impl0.getType(0);
//	  saxProcessor0.startElement("LocalVariableDeclaration", "LocalVariableDeclaration", "VariableDeclaratorId", attributes2Impl0);
//	  localVar0.processState("VariableDeclaratorId");
//	  localVar0.closeState("identifier");
//	}

	/*
	 * Statement cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_jmca_Statement_processState_2() {
//	  // I3 Branch 4 IFEQ L108;false
//	  // In-method
//	  SaxProcessor saxProcessor0 = new SaxProcessor();
//	  Statement statement0 = new Statement(saxProcessor0);
//	  String string0 = "cla4s";
//	  statement0.closeState(string0);
//	  statement0.closeState(string0);
//	  String string1 = "ForStatement";
//	  statement0.processState(string1);
//	}

	/*
	 * TryStatement cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_jmca_TryStatement_processState_4() {
//	  // I6 Branch 2 IF_ICMPLE L71;false
//	  // In-method
//	  int int0 = 2442;
//	  PipedInputStream pipedInputStream0 = new PipedInputStream();
//	  DataInputStream dataInputStream0 = new DataInputStream(pipedInputStream0);
//	  PushbackInputStream pushbackInputStream0 = new PushbackInputStream(dataInputStream0, int0);
//	  SaxProcessor saxProcessor0 = new SaxProcessor();
//	  TryStatement tryStatement0 = new TryStatement(saxProcessor0);
//	  String string0 = "KxU0Od1\" 2c,";
//	  tryStatement0.closeState(string0);
//	  String string1 = "[bxQfXc*s";
//	  tryStatement0.processState(string1);
//	  Enumeration<InputStream> enumeration0 = null;
//	  SequenceInputStream sequenceInputStream0 = new SequenceInputStream(enumeration0);
//	}

	/*
	 * Null parameter passed into constructor.
	 */
	@Test
	public void evoobj_jwbf_Article_getEditSummary_5() {
	  // I41 Branch 6 IFNONNULL L76;true
	  // In-method
	  MediaWikiBot mediaWikiBot0 = new MediaWikiBot((HttpActionClient) null);
	  Article article0 = new Article(mediaWikiBot0, (SimpleArticle) null);
	  String string0 = article0.getEditSummary();
	}

	/*
	 * java.lang.NoClassDefFoundError: org/jdom/JDOMException
	 */
//	@Test
//	public void evoobj_jwbf_MediaWikiBot_readData_2() throws ActionException, ProcessException {
//	  // I76 Branch 10 IFNULL L82;true
//	  // In-method
//	  HttpActionClient httpActionClient0 = null;
//	  MediaWikiBot mediaWikiBot0 = new MediaWikiBot(httpActionClient0);
//	  String string0 = "/";
//	  String string1 = "^xI26m2";
//	  MockFile mockFile0 = new MockFile(string0, string1);
//	  int int0 = 0;
//	  SimpleCache simpleCache0 = new SimpleCache(mockFile0, int0);
//	  mediaWikiBot0.setCacheHandler(simpleCache0);
//	  String string2 = "<qPDB$3:jc";
//	  int int1 = 3654;
//	  SimpleArticle simpleArticle0 = mediaWikiBot0.readData(string2, int1);
//	}

	/*
	 * OperationsHelperImpl cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_xisemele_OperationsHelperImpl_children_4() {
//	  // I21 Branch 2 IFLE L63;true
//	  // In-method
//	  OperationsHelperImpl operationsHelperImpl0 = new OperationsHelperImpl();
//	  IIOMetadataNode iIOMetadataNode0 = new IIOMetadataNode();
//	  LinkedList<Node> linkedList0 = new LinkedList<Node>();
//	  Stream<Node> stream0 = linkedList0.stream();
//	  Stream<Node> stream1 = linkedList0.parallelStream();
//	  LinkedList<Node> linkedList1 = new LinkedList<Node>();
//	  IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode();
//	  IIOMetadataNode iIOMetadataNode2 = (IIOMetadataNode)iIOMetadataNode0.insertBefore(iIOMetadataNode1, iIOMetadataNode1);
//	  Node node0 = iIOMetadataNode1.getFirstChild();
//	  String string0 = "R";
//	  String string1 = null;
//	  boolean boolean0 = iIOMetadataNode0.hasAttributeNS(string0, string1);
//	  IIOMetadataNode iIOMetadataNode3 = (IIOMetadataNode)iIOMetadataNode0.insertBefore(iIOMetadataNode0, node0);
//	  LinkedList<Node> linkedList2 = new LinkedList<Node>();
//	  IIOMetadataNode iIOMetadataNode4 = (IIOMetadataNode)iIOMetadataNode1.cloneNode(boolean0);
//	  LinkedList<Node> linkedList3 = new LinkedList<Node>();
//	  IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode();
//	  IIOMetadataNode iIOMetadataNode6 = (IIOMetadataNode)iIOMetadataNode0.appendChild(iIOMetadataNode2);
//	  LinkedList<Node> linkedList4 = new LinkedList<Node>();
//	  LinkedList<Node> linkedList5 = new LinkedList<Node>();
//	  List<Node> list0 = operationsHelperImpl0.children(iIOMetadataNode0);
//	}

	/*
	 * OperationsHelperImpl cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_xisemele_OperationsHelperImpl_find_3() {
//	  // I41 Branch 8 IF_ICMPNE L30;true
//	  // In-method
//	  OperationsHelperImpl operationsHelperImpl0 = new OperationsHelperImpl();
//	  Document document0 = null;
//	  String string0 = "net/sf/xisemele/impl/OperationsHelperImpl#find(Lorg/w3c/dom/Document;Ljava/lang/String;)Lorg/w3c/dom/Node;";
//	  Node node0 = operationsHelperImpl0.find(document0, string0);
//	}

	/*
	 * OperationsHelperImpl cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_xisemele_OperationsHelperImpl_nodeWithName_0() {
//	  // I55 Branch 11 IF_ICMPGE L47;true
//	  // In-method
//	  OperationsHelperImpl operationsHelperImpl0 = new OperationsHelperImpl();
//	  LinkedList<Node> linkedList0 = new LinkedList<Node>();
//	  IIOMetadataNode iIOMetadataNode0 = new IIOMetadataNode();
//	  boolean boolean0 = linkedList0.offerLast(iIOMetadataNode0);
//	  Node node0 = operationsHelperImpl0.nodeWithName(linkedList0, "^8^u43lgXwiXvTJ");
//	}

	/*
	 * The field DecadalModel.row_max is not visible
	 */
//	@Test
//	public void evoobj_corina_DecadalModel_setValueAt_3() {
//	  // I4 Branch 36 IFNE L226;true
//	  // In-method
//	  Sample sample0 = new Sample();
//	  DecadalModel decadalModel0 = new DecadalModel(sample0);
//	  decadalModel0.row_max = sample0.MR;
//	  decadalModel0.countRows();
//	  DecadalModel decadalModel1 = new DecadalModel();
//	  DecadalModel decadalModel2 = new DecadalModel();
//	  int int0 = 0;
//	  Integer integer0 = GVTAttributedCharacterIterator.TextAttribute.ARABIC_INITIAL;
//	  int int1 = 6;
//	  List<PropertyChangeListenerProxy> list0 = null;
//	  sample0.data = list0;
//	  decadalModel0.fireTableCellUpdated(int1, int0);
//	  int int2 = 2;
//	  decadalModel0.setValueAt(decadalModel2, int0, int2);
//	}

	/*
	 * Null parameter passed into constructor
	 */
	@Test
	public void evoobj_corina_Truncate_cropTo_1() throws IOException {
	  // I4 Branch 1 IFNONNULL L107;true
	  // Out-method
	  String string0 = null;
	  Sample sample0 = new Sample(string0);
	  Truncate truncate0 = new Truncate(sample0);
	  truncate0.cropTo(sample0.range);
	}

	/*
	 * Null parameter passed into constructor
	 */
	@Test
	public void evoobj_corina_LabelSet_setSelected_8() {
	  // I4 Branch 10 IFEQ L299;true
	  // Out-method
	  List<String> list0 = null;
	  LabelSet labelSet0 = new LabelSet(list0);
	  Site site0 = new Site();
	  boolean boolean0 = false;
	  labelSet0.setSelected(site0, boolean0);
	}

	/*
	 * Complicated case (App.prefs null?)
	 */
	@Test
	public void evoobj_corina_ZoomInTool_mouseDragged_6() {
	  // I51 Branch 2 IFLE L154;true
	  // Out-method
	  Site site0 = new Site();
	  MapFrame mapFrame0 = new MapFrame();
	  LabelSet labelSet0 = new LabelSet();
	  MapPanel mapPanel0 = new MapPanel(mapFrame0, labelSet0);
	  View view0 = mapPanel0.getView();
	  ToolBox toolBox0 = new ToolBox(view0, mapPanel0, mapFrame0);
	  ZoomInTool zoomInTool0 = new ZoomInTool(mapPanel0, view0, toolBox0);
	  ZoomInTool zoomInTool1 = new ZoomInTool(mapPanel0, view0, toolBox0);
	  MouseEvent mouseEvent0 = null;
	  zoomInTool0.mouseDragged(mouseEvent0);
	}

	/*
	 * Complicated case (indirect null parameter)
	 */
	@Test
	public void evoobj_corina_ColorRenderer_getTableCellRendererComponent_1() {
	  // I51 Branch 2 IFLE L154;false
	  // In-method
	  boolean boolean0 = false;
	  ColorRenderer colorRenderer0 = new ColorRenderer(boolean0);
	  TableModel tableModel0 = null;
	  DefaultTableColumnModel defaultTableColumnModel0 = new DefaultTableColumnModel();
	  JTable jTable0 = new JTable(tableModel0, defaultTableColumnModel0);
	  JDesktopPane jDesktopPane0 = new JDesktopPane();
	  JInternalFrame jInternalFrame0 = jDesktopPane0.getSelectedFrame();
	  boolean boolean1 = true;
	  boolean boolean2 = false;
	  int int0 = 103;
	  int int1 = 34;
	  Component component0 = colorRenderer0.getTableCellRendererComponent(jTable0, jInternalFrame0, boolean1, boolean2, int0, int1);
	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_corina_Site_equals_0() {
	  // I23 Branch 29 IFNE L193;false
	  // In-method
	  Site site0 = new Site();
	  Object object0 = null;
	  boolean boolean0 = site0.equals(object0);
	}

	/*
	 * Cannot cast from Stack<Site> to List<Object>
	 */
//	@Test
//	public void evoobj_corina_SiteDB_getSiteNames_9() {
//	  // I54 Branch 31 IF_ICMPNE L202;true
//	  // In-method
//	  SiteDB siteDB0 = new SiteDB();
//	  Stack<Site> stack0 = new Stack<Site>();
//	  Site site0 = new Site();
//	  Site site1 = stack0.push(site0);
//	  boolean boolean0 = stack0.add(site0);
//	  int int0 = 630;
//	  stack0.setSize(int0);
//	  siteDB0.sites = (List<Object>) stack0;
//	  Object object0 = new Object();
//	  long long0 = 3478L;
//	  System.setCurrentTimeMillis(int0);
//	  System.setCurrentTimeMillis(long0);
//	  List<Object> list0 = siteDB0.getSiteNames();
//	}

	/*
	 * Used parameter not set (logName)
	 */
	@Test
	public void evoobj_corina_SimpleLog_fatal_2() {
	  // I12 Branch 38 IFNE L238;true
	  // In-method
	  String string0 = null;
	  SimpleLog simpleLog0 = new SimpleLog(string0);
	  String string1 = "rxLu-Lw~]_8s!";
	  FileDescriptor fileDescriptor0 = new FileDescriptor();
	  MockFileInputStream mockFileInputStream0 = new MockFileInputStream(fileDescriptor0);
	  MockThrowable mockThrowable0 = new MockThrowable();
	  simpleLog0.trace(mockFileInputStream0, mockThrowable0);
	  SimpleLog simpleLog1 = new SimpleLog(string1);
	  BufferedInputStream bufferedInputStream0 = new BufferedInputStream(mockFileInputStream0);
	  simpleLog1.debug(bufferedInputStream0);
	  PushbackInputStream pushbackInputStream0 = new PushbackInputStream(bufferedInputStream0);
	  simpleLog0.debug(pushbackInputStream0, mockThrowable0);
	  boolean boolean0 = simpleLog0.isInfoEnabled();
	  simpleLog0.fatal(simpleLog1);
	}

	/*
	 * The type de.huxhorn.sulky.codec.Encoder cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_lilith_AccessEventProtobufEncoder_convertStringMap_4() {
//	  // I107 Branch 25 IFEQ L116;true
//	  // Out-method
//	  int int0 = 0;
//	  float float0 = 814.0F;
//	  HashMap<String, String> hashMap0 = new HashMap<String, String>(int0, float0);
//	  String string0 = "";
//	  String string1 = "";
//	  String string2 = hashMap0.putIfAbsent(string0, string1);
//	  HashMap<String, String> hashMap1 = new HashMap<String, String>(hashMap0);
//	  boolean boolean0 = false;
//	  hashMap0.clear();
//	  AccessEventProtobufEncoder accessEventProtobufEncoder0 = new AccessEventProtobufEncoder(boolean0);
//	  boolean boolean1 = accessEventProtobufEncoder0.isCompressing();
//	  boolean boolean2 = true;
//	  accessEventProtobufEncoder0.setCompressing(boolean2);
//	  AccessEvent accessEvent0 = new AccessEvent();
//	  String string3 = "Put7&Ut!1y*9";
//	  accessEvent0.setRemoteHost(string3);
//	  accessEvent0.setRequestHeaders(hashMap1);
//	  int int1 = 0;
//	  accessEvent0.setStatusCode(int1);
//	  String string4 = "";
//	  accessEvent0.setServerName(string4);
//	  String string5 = accessEvent0.toString();
//	  byte[] byteArray0 = accessEventProtobufEncoder0.encode(accessEvent0);
//	  boolean boolean3 = true;
//	  BiConsumer<Object, Object> biConsumer0 = null;
//	  hashMap1.forEach(biConsumer0);
//	  accessEventProtobufEncoder0.setCompressing(boolean3);
//	  LoggerContext loggerContext0 = new LoggerContext();
//	  Object object0 = new Object();
//	  boolean boolean4 = loggerContext0.equals(object0);
//	  AccessProto.LoggerContext accessProto_LoggerContext0 = AccessEventProtobufEncoder.convert(loggerContext0);
//	  HashMap<String, String[]> hashMap2 = new HashMap<String, String[]>();
//	  HashMap<String, String[]> hashMap3 = new HashMap<String, String[]>();
//	  AccessProto.StringArrayMap accessProto_StringArrayMap0 = AccessEventProtobufEncoder.convertStringArrayMap(hashMap3);
//	  AccessProto.StringMap accessProto_StringMap0 = AccessEventProtobufEncoder.convertStringMap(hashMap1);
//	}

	/*
	 * java.lang.NoClassDefFoundError: de/huxhorn/sulky/stax/GenericStreamWriter
	 */
//	@Test
//	public void evoobj_lilith_StackTraceElementWriter_write_3() throws XMLStreamException {
//	  // I10 Branch 8 IFNULL L106;true
//	  // In-method
//	  StackTraceElementWriter stackTraceElementWriter0 = new StackTraceElementWriter();
//	  XMLStreamWriter xMLStreamWriter0 = null;
//	  String string0 = "}oF&duK)P$bwf2`vpr";
//	  String string1 = null;
//	  String string2 = "";
//	  int int0 = (-2689);
//	  String string3 = "fQ$SyGTxm}nXtKM:&e";
//	  String string4 = "@OO";
//	  boolean boolean0 = false;
//	  ExtendedStackTraceElement extendedStackTraceElement0 = new ExtendedStackTraceElement(string0, string1, string2, int0, string3, string4, boolean0);
//	  boolean boolean1 = false;
//	  stackTraceElementWriter0.write(xMLStreamWriter0, extendedStackTraceElement0, boolean1);
//	}

	/*
	 * java.lang.NoClassDefFoundError: de/huxhorn/sulky/io/TimeoutOutputStream
	 */
//	@Test
//	public void evoobj_lilith_SimpleSendBytesService_sendBytes_7() throws IOException {
//	  // I49 Branch 6 IFLE L85;true
//	  // Out-method
//	  String string0 = "b=";
//	  int int0 = 2450;
//	  SocketDataOutputStreamFactory socketDataOutputStreamFactory0 = new SocketDataOutputStreamFactory(string0, int0, int0, int0);
//	  MessageWriteByteStrategy messageWriteByteStrategy0 = new MessageWriteByteStrategy();
//	  SimpleSendBytesService simpleSendBytesService0 = new SimpleSendBytesService(socketDataOutputStreamFactory0, messageWriteByteStrategy0);
//	  byte[] byteArray0 = new byte[2];
//	  String string1 = null;
//	  MockFileOutputStream mockFileOutputStream0 = new MockFileOutputStream(string1);
//	  DataOutputStream dataOutputStream0 = new DataOutputStream(mockFileOutputStream0);
//	  messageWriteByteStrategy0.writeBytes(dataOutputStream0, byteArray0);
//	  byte byte0 = (byte)0;
//	  byteArray0[0] = byte0;
//	  byte byte1 = (byte) (-62);
//	  byteArray0[1] = byte1;
//	  simpleSendBytesService0.sendBytes(byteArray0);
//	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_summa_CollatorFactory_adjustAASorting_8() throws ParseException {
	  // I3 Branch 5 IFNONNULL L301;false
	  // Out-method
	  Collator collator0 = null;
	  Comparator<String> comparator0 = CollatorFactory.wrapCollator(collator0);
	  RuleBasedCollator ruleBasedCollator0 = (RuleBasedCollator)Collator.getInstance();
	  Locale locale0 = Locale.CANADA_FRENCH;
	  String string0 = locale0.getDisplayVariant();
	  String string1 = locale0.toLanguageTag();
	  String string2 = locale0.getDisplayLanguage();
	  String string3 = locale0.getDisplayName();
	  boolean boolean0 = true;
	  Comparator<String> comparator1 = CollatorFactory.wrapCollator(ruleBasedCollator0);
	  Collator collator1 = CollatorFactory.adjustAASorting(collator0);
	  Object object0 = new Object();
	  Object object1 = new Object();
	  String string4 = locale0.getDisplayVariant();
	  int int0 = collator1.compare(object0, object1);
	  Comparator<Object> comparator2 = collator1.thenComparing(ruleBasedCollator0);
	  RuleBasedCollator ruleBasedCollator1 = new RuleBasedCollator(string2);
	  ToDoubleFunction<Object> toDoubleFunction0 = null;
	  RuleBasedCollator ruleBasedCollator2 = new RuleBasedCollator(string4);
	  String string5 = "da";
	  RuleBasedCollator ruleBasedCollator3 = new RuleBasedCollator(string0);
	  Locale locale1 = Locale.SIMPLIFIED_CHINESE;
	  String string6 = "nv\"=<u 7-?>bdg}rL+";
	  String string7 = locale0.getUnicodeLocaleType(string6);
	  String string8 = locale1.getScript();
	  String string9 = locale1.getDisplayScript(locale0);
	  ToLongFunction<Object> toLongFunction0 = null;
	  String string10 = null;
	  RuleBasedCollator ruleBasedCollator4 = new RuleBasedCollator(string10);
	  Comparator<Integer> comparator3 = Comparator.nullsFirst((Comparator<? super Integer>) ruleBasedCollator4);
	  Comparator<String> comparator4 = CollatorFactory.wrapCollator(ruleBasedCollator2);
	  Comparator<Object> comparator5 = ruleBasedCollator4.reversed();
	  String string11 = locale0.getDisplayVariant(locale1);
	  Comparator<String> comparator6 = Comparator.comparingLong((ToLongFunction<? super String>) toLongFunction0);
	  Collator collator2 = CollatorFactory.createCollator(locale0);
	  Comparator<Object> comparator7 = ruleBasedCollator4.reversed();
	  CollatorFactory collatorFactory0 = new CollatorFactory();
	  Collator collator3 = CollatorFactory.createCollator(locale1, boolean0);
	  Collator collator4 = CollatorFactory.fixCollator(collator3);
	  Comparator<String> comparator8 = CollatorFactory.wrapCollator(collator1);
	  Comparator<String> comparator9 = CollatorFactory.wrapCollator(collator1);
	  String string12 = "O";
	  RuleBasedCollator ruleBasedCollator5 = new RuleBasedCollator(string12);
	  Comparator<Object> comparator10 = ruleBasedCollator5.thenComparing(collator1);
	  ToIntFunction<Object> toIntFunction0 = null;
	  RuleBasedCollator ruleBasedCollator6 = new RuleBasedCollator(string2);
	  RuleBasedCollator ruleBasedCollator7 = new RuleBasedCollator(string5);
	  Collator collator5 = Collator.getInstance();
	  Comparator<Integer> comparator11 = Comparator.comparingDouble((ToDoubleFunction<? super Integer>) toDoubleFunction0);
	  Comparator<String> comparator12 = CollatorFactory.wrapCollator(ruleBasedCollator0);
	  Locale locale2 = null;
	  Collator collator6 = CollatorFactory.adjustAASorting(ruleBasedCollator4);
	  Object object2 = new Object();
	  Collator collator7 = CollatorFactory.createCollator(locale2, string11, boolean0);
	  Collator collator8 = CollatorFactory.createCollator(locale2);
	  String string13 = "ParseException while pa.rsing\n";
	  String string14 = "gX`p>U";
	  String string15 = "QA`NEEDED";
	  boolean boolean1 = collator7.equals(string14, string15);
	  RuleBasedCollator ruleBasedCollator8 = new RuleBasedCollator(string15);
	  Locale.setDefault(locale0);
	  String string16 = "+_tisGI*s$5t9";
	  Locale locale3 = new Locale(string4);
	  int int1 = ruleBasedCollator2.compare(string16, locale3);
	  String string17 = "8-b}uq`7t9a<~P";
	  Locale locale4 = Locale.CHINESE;
	  int int2 = 14;
	  ruleBasedCollator0.setStrength(int2);
	  Collator collator9 = CollatorFactory.adjustAASorting(ruleBasedCollator0);
	  Collator collator10 = CollatorFactory.fixCollator(ruleBasedCollator4);
	  Comparator<String> comparator13 = CollatorFactory.wrapCollator(collator7);
	  Comparator<Object> comparator14 = ruleBasedCollator0.reversed();
	  Collator collator11 = Collator.getInstance();
	  Comparator<String> comparator15 = CollatorFactory.wrapCollator(collator9);
	  Collator collator12 = CollatorFactory.adjustAASorting(collator5);
	  Collator collator13 = CollatorFactory.createCollator(locale0);
	  boolean boolean2 = true;
	  Collator collator14 = CollatorFactory.createCollator(locale1, boolean2);
	  Comparator<Locale> comparator16 = Comparator.comparingInt((ToIntFunction<? super Locale>) toIntFunction0);
	  RuleBasedCollator ruleBasedCollator9 = new RuleBasedCollator(string17);
	  RuleBasedCollator ruleBasedCollator10 = new RuleBasedCollator(string17);
	  Comparator<String> comparator17 = CollatorFactory.wrapCollator(collator6);
	  Collator collator15 = CollatorFactory.createCollator(locale3);
	  RuleBasedCollator ruleBasedCollator11 = new RuleBasedCollator(string13);
	  RuleBasedCollator ruleBasedCollator12 = new RuleBasedCollator(string3);
	  Collator collator16 = Collator.getInstance(locale3);
	  Collator collator17 = Collator.getInstance();
	  RuleBasedCollator ruleBasedCollator13 = new RuleBasedCollator(string5);
	  RuleBasedCollator ruleBasedCollator14 = new RuleBasedCollator(string15);
	  Collator collator18 = CollatorFactory.createCollator(locale4, boolean0);
	  RuleBasedCollator ruleBasedCollator15 = new RuleBasedCollator(string6);
	  RuleBasedCollator ruleBasedCollator16 = new RuleBasedCollator(string0);
	  Locale locale5 = Locale.SIMPLIFIED_CHINESE;
	  Set<Character> set0 = locale5.getExtensionKeys();
	  String[] stringArray0 = Locale.getISOCountries();
	  Collator collator19 = CollatorFactory.fixCollator(ruleBasedCollator4);
	  String string18 = "R0Q<?`(jU-oCTxlQ[";
	  RuleBasedCollator ruleBasedCollator17 = new RuleBasedCollator(string18);
	  Collator collator20 = Collator.getInstance(locale3);
	  String string19 = "english";
	  RuleBasedCollator ruleBasedCollator18 = new RuleBasedCollator(string19);
	  Comparator<String> comparator18 = CollatorFactory.wrapCollator(ruleBasedCollator18);
	  Collator collator21 = CollatorFactory.fixCollator(collator18);
	  boolean boolean3 = true;
	  Collator collator22 = CollatorFactory.createCollator(locale1, boolean3);
	  Collator collator23 = CollatorFactory.createCollator(locale3);
	  RuleBasedCollator ruleBasedCollator19 = new RuleBasedCollator(string16);
	  Comparator<String> comparator19 = CollatorFactory.wrapCollator(ruleBasedCollator1);
	  Comparator<String> comparator20 = CollatorFactory.wrapCollator(collator14);
	  Collator collator24 = CollatorFactory.adjustAASorting(collator0);
	}

	/*
	 * Null parameter passed into constructor.
	 */
	@Test
	public void evoobj_summa_LineInputStream_available_4() throws IOException {
	  // I3 Branch 18 IFNONNULL L544;true
	  // In-method
	  InputStream inputStream0 = null;
	  LineInputStream lineInputStream0 = new LineInputStream(inputStream0);
	  LineInputStream lineInputStream1 = new LineInputStream(lineInputStream0);
	  int int0 = lineInputStream0.available();
	}

	/*
	 * AnselToUnicode cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_summa_MarcXmlWriterFixed_getDataElement_4() {
//	  // I24 Branch 20 IFEQ L547;false
//	  // In-method
//	  ByteArrayOutputStream byteArrayOutputStream0 = new ByteArrayOutputStream();
//	  int int0 = 1;
//	  BufferedOutputStream bufferedOutputStream0 = new BufferedOutputStream(byteArrayOutputStream0, int0);
//	  MarcXmlWriterFixed marcXmlWriterFixed0 = new MarcXmlWriterFixed(bufferedOutputStream0);
//	  boolean boolean0 = true;
//	  marcXmlWriterFixed0.setUnicodeNormalization(boolean0);
//	  ErrorHandler errorHandler0 = null;
//	  boolean boolean1 = false;
//	  AnselToUnicode anselToUnicode0 = new AnselToUnicode(errorHandler0, boolean1);
//	  marcXmlWriterFixed0.setConverter(anselToUnicode0);
//	  String string0 = null;
//	  char[] charArray0 = marcXmlWriterFixed0.getDataElement(string0);
//	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_summa_QueryOptions_getNewRecord_6() {
	  // I3 Branch 12 IFNONNULL L431;false
	  // In-method
	  boolean boolean0 = true;
	  Boolean boolean1 = new Boolean(boolean0);
	  String string0 = "RECORDMETA";
	  Boolean boolean2 = new Boolean(string0);
	  int int0 = 243;
	  int int1 = (-568);
	  boolean boolean3 = false;
	  boolean boolean4 = false;
	  boolean boolean5 = Boolean.logicalAnd(boolean3, boolean4);
	  StringMap stringMap0 = new StringMap();
	  QueryOptions queryOptions0 = new QueryOptions(boolean1, boolean2, int1, int0);
	  QueryOptions queryOptions1 = new QueryOptions(queryOptions0);
	  Record record0 = null;
	  Record record1 = queryOptions1.getNewRecord(record0);
	}

	/*
	 * Used parameter not set (name)
	 */
	@Test
	public void evoobj_objectexplorer_AttributeModelComparator_compare_3() {
	  // I39 Branch 3 IFLE L93;true
	  // In-method
	  AttributeModelComparator attributeModelComparator0 = new AttributeModelComparator();
	  ArrayAttributeModel arrayAttributeModel0 = new ArrayAttributeModel();
	  StandardAttributeModel standardAttributeModel0 = new StandardAttributeModel();
	  int int0 = attributeModelComparator0.compare(arrayAttributeModel0, standardAttributeModel0);
	}

	/*
	 * FormatConverter cannot be resolved
	 */
//	@Test
//	public void evoobj_openjms_FormatConverter_getDouble_5() {
//	  // I10 Branch 28 IF_ICMPLE L482;false
//	  // In-method
//	  double double0 = FormatConverter.getDouble((Object) null);
//	}

	/*
	 * The type org.exolab.jms.message.FormatConverter is not visible
	 */
//	@Test
//	public void evoobj_openjms_FormatConverter_getShort_1() {
//	  // I48 Branch 7 IFLE L121;true
//	  // Out-method
//	  Object object0 = new Object();
//	  Object object1 = null;
//	  float float0 = org.exolab.jms.message.FormatConverter.getFloat(object1);
//	  Object object2 = new Object();
//	  char char0 = FormatConverter.getChar(object2);
//	  short short0 = FormatConverter.getShort(object0);
//	}

	/*
	 * Used parameter not set (_id)
	 */
	@Test
	public void evoobj_openjms_MessageId_equals_5() {
	  // I4 Branch 3 IF_ACMPNE L118;false
	  // In-method
	  String string0 = "";
	  MessageId messageId0 = new MessageId(string0);
	  MessageId messageId1 = new MessageId();
	  boolean boolean0 = messageId0.equals(messageId1);
	}

	/*
	 * java.lang.NoClassDefFoundError: org/exolab/castor/xml/ValidationException
	 */
//	@Test
//	public void evoobj_openjms_SerialTask_stop_3() {
//	  // I31 Branch 13 IF_ICMPLT L243;true
//	  // Out-method
//	  MockThread mockThread0 = new MockThread();
//	  Configuration configuration0 = new Configuration();
//	  ThreadPoolFactory threadPoolFactory0 = null;
//	  Scheduler scheduler0 = new Scheduler(configuration0, threadPoolFactory0);
//	  SerialTask serialTask0 = new SerialTask(mockThread0, scheduler0);
//	  serialTask0.stop();
//	}

	/*
	 * The type org.exolab.jms.selector.SLong is not visible
	 */
//	@Test
//	public void evoobj_openjms_SLong_add_1() {
//	  // I9 Branch 1 IFNONNULL L82;false
//	  // In-method
//	  SLong sLong0 = new org.exolab.jms.selector.SLong(1L);
//	  SNumber sNumber0 = sLong0.add((SNumber) null);
//	}

	/*
	 * Complicated case (?)
	 */
	@Test
	public void evoobj_openjms_SelectorTreeParser_primaryExpression_1() throws RecognitionException {
	  // I37 Branch 20 IF_ICMPLT L385;true
	  // Out-method
	  SelectorTreeParser selectorTreeParser0 = new SelectorTreeParser();
	  SelectorAST selectorAST0 = new SelectorAST();
	  Expression expression0 = selectorTreeParser0.selector(selectorAST0);
	  Expression expression1 = selectorTreeParser0.betweenExpression(selectorAST0);
	  Expression expression2 = selectorTreeParser0.literal(selectorAST0);
	  Expression expression3 = selectorTreeParser0.primaryExpression(selectorAST0);
	}

	/*
	 * Used parameter was not set (_global)
	 */
	@Test
	public void evoobj_openjms_ExternalXid_equals_6() {
	  // I10 Branch 16 IF_ICMPLE L334;false
	  // In-method
	  ExternalXid externalXid0 = new ExternalXid();
	  ExternalXid externalXid1 = new ExternalXid();
	  byte[] byteArray0 = externalXid1.getBranchQualifier();
	  boolean boolean0 = externalXid0.equals(externalXid1);
	}

	/*
	 * Null object passed into constructor.
	 */
	@Test
	public void evoobj_openjms_TransactionState_equals_9() throws IOException {
	  // I9 Branch 1 IFNONNULL L82;true
	  // Out-method
	  TransactionState transactionState0 = TransactionState.PREPARED;
	  String string0 = "org.apache.oro.io.ANwkFilenameFilter";
	  boolean boolean0 = transactionState0.isOpened();
	  OutputStream outputStream0 = null;
	  ObjectOutputStream objectOutputStream0 = new ObjectOutputStream(outputStream0);
	  transactionState0.writeExternal(objectOutputStream0);
	  boolean boolean1 = transactionState0.equals(string0);
	}

	/*
	 * Null parameter passed into method.
	 */
	@Test
	public void evoobj_openjms_CommandLine_add_8() {
	  // I48 Branch 7 IFLE L121;false
	  // Out-method
	  String[] stringArray0 = new String[9];
	  stringArray0[0] = "I&LW";
	  stringArray0[1] = "";
	  stringArray0[2] = "";
	  stringArray0[3] = "zk6n4|!R+TBZ>]!`e";
	  stringArray0[4] = "";
	  stringArray0[5] = "W?o(f]}*";
	  stringArray0[6] = "";
	  stringArray0[7] = "";
	  stringArray0[8] = "-sx6vFe%";
	  CommandLine commandLine0 = new CommandLine(stringArray0);
	  boolean boolean0 = commandLine0.isSwitch("");
	  boolean boolean1 = commandLine0.isSwitch("T]cJQDffQ,VT;ZyA");
	  String string0 = "";
	  String string1 = "c";
	  boolean boolean2 = commandLine0.exists("org.exolab.jms.util.CommandLine");
	  String string2 = commandLine0.value("IsbdHhD!>-\"");
	  String string3 = commandLine0.value("nG1oEky\" ");
	  boolean boolean3 = commandLine0.isSwitch("");
	  String string4 = "j/RRzp";
	  boolean boolean4 = commandLine0.isSwitch("Gvjq:CdB_bis@`>");
	  boolean boolean5 = commandLine0.add("", "j/RRzp");
	  boolean boolean6 = true;
	  boolean boolean7 = commandLine0.add("", "zk6n4|!R+TBZ>]!`e", false);
	  boolean boolean8 = commandLine0.isSwitch("$D");
	  boolean boolean9 = commandLine0.isParameter("");
	  String string5 = commandLine0.value("org.exolab.jms.util.CommandLine", "HsdQ1tF<v");
	  boolean boolean10 = commandLine0.exists("");
	  boolean boolean11 = commandLine0.isParameter((String) null);
	  String string6 = "rmY;KdDi)-J]";
	  String string7 = "org/exolab/jms/util/CommandLine#processCommandLine([Ljava/lang/String;)V";
	  boolean boolean12 = commandLine0.add(string6, string7);
	  String string8 = null;
	  String string9 = "";
	  boolean boolean13 = commandLine0.isSwitch(string9);
	  String string10 = ":YZ(V:L5Qn%";
	  String string11 = "${7gw nK,# #)";
	  boolean boolean14 = commandLine0.add(string10, string11);
	  String string12 = "";
	  boolean boolean15 = commandLine0.add(string4, string12);
	  boolean boolean16 = commandLine0.isSwitch(string8);
	  String string13 = "";
	  String string14 = " o1E^;B#3@rl&";
	  boolean boolean17 = commandLine0.isSwitch(string14);
	  String string15 = "org/exolab/jms/util/CommandLine#processCommandLine([Ljava/lang/String;)V";
	  String string16 = commandLine0.value(string15);
	  boolean boolean18 = commandLine0.exists(string13);
	  String string17 = "]PVX8=Cw";
	  String string18 = "Y-oqD@#IdQHYrIV\"f|";
	  boolean boolean19 = commandLine0.exists(string18);
	  boolean boolean20 = commandLine0.isSwitch(string17);
	  String string19 = "^";
	  String string20 = "";
	  String string21 = " !@cibZ\"Che8";
	  String string22 = "";
	  boolean boolean21 = commandLine0.exists(string22);
	  String string23 = "rYzoxa{gXdI!8";
	  String string24 = "";
	  String string25 = commandLine0.value(string23, string24);
	  boolean boolean22 = commandLine0.exists(string21);
	  String string26 = commandLine0.value(string19, string20);
	  String string27 = "";
	  String string28 = commandLine0.value(string27);
	  String string29 = "";
	  String string30 = null;
	  boolean boolean23 = commandLine0.add(string29, string30);
	  String string31 = "";
	  String string32 = "";
	  boolean boolean24 = commandLine0.add(string31, string32);
	  String string33 = "ea{t-Wa6";
	  String string34 = "x\\#/$#Ga|P%/$MJ";
	  String string35 = "nE4i>Jt{%HP";
	  boolean boolean25 = commandLine0.isSwitch(string35);
	  boolean boolean26 = commandLine0.add(string33, string34);
	  String string36 = "-";
	  String string37 = "vLW.^hyYmh#~:?6";
	  boolean boolean27 = commandLine0.add(string36, string37);
	  String string38 = "";
	  String string39 = "9<";
	  boolean boolean28 = commandLine0.add(string38, string39);
	  String string40 = "";
	  String string41 = "ZHCI|_96";
	  boolean boolean29 = commandLine0.add(string40, string41);
	  String string42 = "";
	  boolean boolean30 = commandLine0.isSwitch(string42);
	  String string43 = "";
	  boolean boolean31 = commandLine0.exists(string43);
	  String string44 = "|";
	  boolean boolean32 = commandLine0.isSwitch(string44);
	  String string45 = "8";
	  boolean boolean33 = commandLine0.isSwitch(string45);
	  String string46 = "";
	  boolean boolean34 = commandLine0.exists(string46);
	  String string47 = "org/exolab/jms/util/CommandLine#processCommandLine([Ljava/lang/String;)V";
	  String string48 = "OXn";
	  boolean boolean35 = commandLine0.add(string47, string48);
	  String string49 = "QS&9t,|G=tbJx";
	  String string50 = "-";
	  boolean boolean36 = commandLine0.add(string49, string50);
	  String string51 = "#7`>!3=gN_)zRC";
	  String string52 = "1_W.l";
	  boolean boolean37 = commandLine0.add(string51, string52);
	  String string53 = "_";
	  String string54 = commandLine0.value(string53);
	  String string55 = "S~+!";
	  String string56 = "";
	  String string57 = commandLine0.value(string55, string56);
	  String string58 = null;
	  boolean boolean38 = commandLine0.isSwitch(string58);
	  String string59 = "";
	  boolean boolean39 = commandLine0.isParameter(string59);
	  String string60 = "";
	  boolean boolean40 = commandLine0.isSwitch(string60);
	  String string61 = "";
	  String string62 = commandLine0.value(string61);
	  boolean boolean41 = commandLine0.add(string0, string1, boolean6);
	}

	/*
	 * Complicated case (?)
	 */
	@Test
	public void evoobj_lhamacaw_BasketTree_updateSelectedBasketVariableReference_4() {
	  // I28 Branch 5 IFGT L-1;true
	  // In-method
	  SessionProperties sessionProperties0 = new SessionProperties();
	  SupportingDocument supportingDocument0 = new SupportingDocument();
	  sessionProperties0.setProperty((String) null, supportingDocument0);
	  BasketTree basketTree0 = new BasketTree(sessionProperties0);
	  BasketVariableReference basketVariableReference0 = new BasketVariableReference();
	  basketTree0.updateSelectedBasketVariableReference(basketVariableReference0);
	}

	/*
	 * Complicated case (?)
	 */
	@Test
	public void evoobj_lhamacaw_VariableSearchPanel_hasSearchResults_9() {
	  // I38 Branch 13 IFNE L-1;true
	  // Out-method
	  SessionProperties sessionProperties0 = new SessionProperties();
	  VariableSearchPanel variableSearchPanel0 = new VariableSearchPanel(sessionProperties0);
	  boolean boolean0 = variableSearchPanel0.hasSearchResults();
	}

	/*
	 * The method initialize(Collection<ApplicationData>) from the type ADCAdaptor is not visible
	 */
//	@Test
//	public void evoobj_caloriecount_ADCAdaptor_deepCopyData_1() {
//	  // I19 Branch 3 IFEQ L-1;false
//	  // Out-method
//	  ADCAdaptor aDCAdaptor0 = new ADCAdaptor();
//	  Object object0 = new Object();
//	  Map<Integer, Object> map0 = null;
//	  boolean boolean0 = true;
//	  TreeNode treeNode0 = new TreeNode();
//	  TreeNode treeNode1 = new TreeNode(treeNode0);
//	  LinkedHashSet<ApplicationData> linkedHashSet0 = new LinkedHashSet<ApplicationData>();
//	  aDCAdaptor0.initialize(linkedHashSet0);
//	  int int0 = (-34);
//	  Integer integer0 = new Integer(int0);
//	  TreeNode treeNode2 = new TreeNode(treeNode1, aDCAdaptor0, integer0);
//	  ADCAdaptor aDCAdaptor1 = new ADCAdaptor();
//	  treeNode2.setData(aDCAdaptor1);
//	  aDCAdaptor1.initialize(aDCAdaptor0);
//	  TreeNode treeNode3 = new TreeNode();
//	  ADCAdaptor aDCAdaptor2 = new ADCAdaptor();
//	  treeNode1.initialize(treeNode0, aDCAdaptor0, aDCAdaptor2);
//	  int int1 = 1;
//	  Integer integer1 = new Integer(int1);
//	  treeNode3.initialize(treeNode0, aDCAdaptor1, integer1);
//	  treeNode0.addChild(treeNode0);
//	  boolean boolean1 = false;
//	  Iterator<ApplicationData> iterator0 = aDCAdaptor1.iterator();
//	  ADCAdaptor aDCAdaptor3 = (ADCAdaptor)aDCAdaptor0.deepCopy(boolean1);
//	  treeNode3.initialize(treeNode2, aDCAdaptor1, aDCAdaptor3);
//	  boolean boolean2 = aDCAdaptor2.add(aDCAdaptor0);
//	  boolean boolean3 = aDCAdaptor3.contains(treeNode3);
//	  LinkedHashSet[] linkedHashSetArray0 = null;
//	  LinkedHashSet[] linkedHashSetArray1 = (LinkedHashSet[])aDCAdaptor0.toArray((LinkedHashSet<Integer>[]) linkedHashSetArray0);
//	  ADCListenerAdaptor aDCListenerAdaptor0 = new ADCListenerAdaptor();
//	  ADCEvent aDCEvent0 = new ADCEvent();
//	  aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//	  aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//	  aDCListenerAdaptor0.eventOccurred(aDCEvent0);
//	  aDCAdaptor1.addADCListener(aDCListenerAdaptor0);
//	  aDCAdaptor0.clear();
//	  ADCAdaptor aDCAdaptor4 = new ADCAdaptor();
//	  boolean boolean4 = aDCAdaptor1.add(aDCAdaptor4);
//	  boolean boolean5 = aDCAdaptor0.isDirty();
//	  aDCAdaptor1.clear();
//	  boolean boolean6 = aDCAdaptor1.removeAll(aDCAdaptor0);
//	  ADCAdaptor aDCAdaptor5 = new ADCAdaptor();
//	  boolean boolean7 = aDCAdaptor0.add(aDCAdaptor5);
//	  Object[] objectArray0 = aDCAdaptor2.toArray();
//	  aDCAdaptor2.clear();
//	  ApplicationDataListHelper applicationDataListHelper0 = new ApplicationDataListHelper();
//	  boolean boolean8 = true;
//	  aDCAdaptor3.setDirty(boolean8);
//	  aDCAdaptor0.myHelper = applicationDataListHelper0;
//	  Object object1 = aDCAdaptor4.deepCopy(boolean6);
//	  boolean boolean9 = aDCAdaptor0.remove(integer1);
//	  aDCAdaptor0.clear();
//	  aDCAdaptor0.myElements = (Collection<ApplicationData>) aDCAdaptor2;
//	  boolean boolean10 = aDCAdaptor3.isEmpty();
//	  boolean boolean11 = aDCAdaptor0.removeAll(aDCAdaptor4);
//	  aDCAdaptor5.initialize(aDCAdaptor0);
//	  aDCAdaptor1.postDeserialize();
//	  Object object2 = aDCAdaptor4.deepCopy();
//	  int int2 = 17;
//	  aDCAdaptor5.postDeserialize();
//	  Integer integer2 = new Integer(int2);
//	  int int3 = 184;
//	  LinkedHashSet<TreeNode> linkedHashSet1 = new LinkedHashSet<TreeNode>();
//	  Iterator<TreeNode> iterator1 = linkedHashSet1.iterator();
//	  LinkedHashSet<Object> linkedHashSet2 = new LinkedHashSet<Object>();
//	  String string0 = "rx_-ec!:g1q";
//	  applicationDataListHelper0.fire(int3, linkedHashSet2, string0);
//	  boolean boolean12 = aDCAdaptor4.contains(integer2);
//	  aDCAdaptor2.postDeserialize();
//	  Iterator<ApplicationData> iterator2 = aDCAdaptor4.iterator();
//	  HashMap<ADCAdaptor, Integer> hashMap0 = new HashMap<ADCAdaptor, Integer>();
//	  HashMap<ADCAdaptor, Integer> hashMap1 = new HashMap<ADCAdaptor, Integer>(int3);
//	  BiConsumer<ADCAdaptor, Object> biConsumer0 = null;
//	  hashMap1.forEach(biConsumer0);
//	  TreeNode treeNode4 = new TreeNode(treeNode3);
//	  aDCAdaptor3.postDeserialize();
//	  boolean boolean13 = aDCAdaptor4.isEmpty();
//	  boolean boolean14 = true;
//	  Object object3 = aDCAdaptor4.deepCopy(boolean14);
//	  Object object4 = aDCAdaptor5.deepCopy();
//	  boolean boolean15 = aDCAdaptor4.contains(object4);
//	  aDCAdaptor1.update(aDCAdaptor0);
//	  boolean boolean16 = aDCAdaptor0.isEmpty();
//	  Object object5 = aDCAdaptor1.deepCopy();
//	  boolean boolean17 = aDCAdaptor2.add(aDCAdaptor4);
//	  boolean boolean18 = aDCAdaptor2.retainAll(aDCAdaptor0);
//	  LinkedHashSet<Object> linkedHashSet3 = new LinkedHashSet<Object>();
//	  boolean boolean19 = aDCAdaptor0.remove(linkedHashSet3);
//	  Object[] objectArray1 = aDCAdaptor3.toArray();
//	  boolean boolean20 = false;
//	  aDCAdaptor5.setDirty(boolean20);
//	  HashMap<Integer, Integer> hashMap2 = new HashMap<Integer, Integer>(int2);
//	  boolean boolean21 = aDCAdaptor3.contains(linkedHashSet3);
//	  int int4 = 21;
//	  Object object6 = new Object();
//	  HashMap<Integer, String> hashMap3 = new HashMap<Integer, String>();
//	  applicationDataListHelper0.fire(int4, object6, hashMap3);
//	  boolean boolean22 = aDCAdaptor2.isDirty();
//	  aDCAdaptor3.deepCopyData(object0, map0, boolean0);
//	}

	/*
	 * The method getDataElement(CalorieCountDataElements) from the type CalorieCountData is not visible
	 */
//	@Test
//	public void evoobj_caloriecount_CalorieCountData_setBudget_8() {
//	  // I25 Branch 19 IFEQ L-1;false
//	  // Out-method
//	  CalorieCountData calorieCountData0 = new CalorieCountData();
//	  calorieCountData0.postDeserialize();
//	  Budget budget0 = calorieCountData0.getBudget();
//	  CalorieCountData.ourElements = calorieCountData0.ourElements;
//	  FoodList foodList0 = calorieCountData0.getFoods();
//	  String string0 = "a=";
//	  double double0 = 0.0;
//	  SimpleElement simpleElement0 = new SimpleElement(foodList0.TAG_FOOD_LIST, string0);
//	  SimpleElement simpleElement1 = new SimpleElement(foodList0.TAG_FOOD_LIST);
//	  String string1 = "^1t";
//	  int int0 = 131;
//	  SimpleElement simpleElement2 = simpleElement0.createChild(string1, int0);
//	  calorieCountData0.deserializeFrom(simpleElement1);
//	  calorieCountData0.populateFromElement(simpleElement1);
//	  calorieCountData0.serializeTo(simpleElement2);
//	  FrequentFoodList frequentFoodList0 = new FrequentFoodList();
//	  CalorieCountData.CalorieCountDataElementsWrapper calorieCountData_CalorieCountDataElementsWrapper0 = new CalorieCountData.CalorieCountDataElementsWrapper();
//	  CalorieCountDataElements[] calorieCountDataElementsArray0 = calorieCountData_CalorieCountDataElementsWrapper0.values();
//	  Iterator<Object> iterator0 = frequentFoodList0.iterator();
//	  String string2 = "I~";
//	  calorieCountData0.addEntry(string2, frequentFoodList0);
//	  MealList mealList0 = new MealList();
//	  MealList mealList1 = new MealList();
//	  boolean boolean0 = true;
//	  budget0.deepCopyData(mealList1, calorieCountData0.myElements, boolean0);
//	  MealList mealList2 = new MealList();
//	  CalorieCountDataElements calorieCountDataElements0 = CalorieCountDataElements.valueOf(string1);
//	  CalorieCountDataElements[] calorieCountDataElementsArray1 = calorieCountData_CalorieCountDataElementsWrapper0.values();
//	  CalorieCountDataElements calorieCountDataElements1 = CalorieCountDataElements.Food;
//	  int[] intArray0 = new int[3];
//	  intArray0[0] = int0;
//	  mealList0.removeAll(intArray0);
//	  double double1 = new Double(double0);
//	  CalorieCountDataElements[] calorieCountDataElementsArray2 = CalorieCountData.ourElements;
//	  Ccde ccde0 = calorieCountData0.getDataElement(calorieCountDataElements1);
//	  EntryList entryList0 = calorieCountData0.getMeals();
//	  String string3 = "";
//	  calorieCountData0.setEntry(string3, mealList0);
//	  CalorieCountDataElements calorieCountDataElements2 = CalorieCountDataElements.Food;
//	  long long0 = new Integer(int0);
//	  calorieCountData0.deserializeFrom(simpleElement2);
//	  FoodList foodList1 = calorieCountData0.getFoods();
//	  FoodList foodList2 = new FoodList();
//	  mealList1.clear();
//	  CalorieCountData calorieCountData1 = new CalorieCountData();
//	  String string4 = "";
//	  int int1 = (-543);
//	  Stream<Meal> stream0 = mealList0.stream();
//	  String string5 = "/@FSu0Eo{";
//	  String string6 = "PU-@NW*26~[ovY";
//	  long long1 = simpleElement2.getTimeValueOfChild(string6);
//	  CalorieCountDataElements calorieCountDataElements3 = CalorieCountDataElements.Meal;
//	  TimeOfDay timeOfDay0 = budget0.getStartOfDay();
//	  String string7 = CalorieCountData.ENTRY_LIST_NAME;
//	  SimpleElement simpleElement3 = new SimpleElement(foodList1.TAG_FOOD_LIST, int0);
//	  int int2 = 1057;
//	  calorieCountData1.setFoods(foodList0);
//	  Food food0 = new Food(int2, string1, int1, timeOfDay0.TAG_MINUTE);
//	  FrequentFood frequentFood0 = frequentFoodList0.getEntryForFood(food0);
//	  frequentFood0.deserializeFrom(simpleElement0);
//	  boolean boolean1 = calorieCountData0.getDirty();
//	  boolean boolean2 = true;
//	  timeOfDay0.deepCopyData(frequentFood0, calorieCountData0.myElements, boolean2);
//	  Object object0 = calorieCountData0.deepCopy();
//	  String string8 = CalorieCountData.ENTRY_LIST_NAME;
//	  String string9 = "M u/G5iO.B3*YK %q";
//	  simpleElement0.setValue(string9);
//	  calorieCountData0.addEntry(string4, timeOfDay0);
//	  Object[] objectArray0 = entryList0.toArray();
//	  calorieCountData0.replaceWith(calorieCountData1);
//	  EnumWrapper<CalorieCountDataElements> enumWrapper0 = calorieCountData1.getEnumWrapper();
//	  CalorieCountDataElements[] calorieCountDataElementsArray3 = CalorieCountData.ourElements;
//	  CalorieCountDataElements calorieCountDataElements4 = CalorieCountDataElements.Entries;
//	  int int3 = new Integer(int1);
//	  EnumWrapper<CalorieCountDataElements> enumWrapper1 = calorieCountData0.getEnumWrapper();
//	  int int4 = 31;
//	  long long2 = (-13L);
//	  simpleElement2.setValue(long2);
//	  Food food1 = foodList0.getFood(int1);
//	  Meal meal0 = mealList1.createMeal(food1);
//	  boolean boolean3 = food0.getDirty();
//	  boolean boolean4 = calorieCountData1.isDirty();
//	  meal0.setFood(food1);
//	  boolean boolean5 = calorieCountData1.getDirty();
//	  long long3 = (-1461L);
//	  meal0.setTime(long3);
//	  calorieCountData1.addMeal(meal0);
//	  calorieCountData1.setMeals(mealList1);
//	  EntryList entryList1 = calorieCountData0.getEntryList();
//	  calorieCountData1.buildEntries();
//	  calorieCountData1.postDeserialize();
//	  EntryList entryList2 = new EntryList();
//	  EntryList entryList3 = calorieCountData0.getEntryList();
//	  calorieCountData1.serializeTo(simpleElement2);
//	  FoodList foodList3 = calorieCountData0.getFoods();
//	  FrequentFoodList frequentFoodList1 = calorieCountData1.getFrequentFoods();
//	  CalorieCountDataElements calorieCountDataElements5 = CalorieCountDataElements.Meal;
//	  Ccde ccde1 = calorieCountData1.getDataElement(calorieCountDataElements5);
//	  calorieCountData0.deserializeFrom(simpleElement3);
//	  calorieCountData0.serializeTo(simpleElement1);
//	  String string10 = CalorieCountData.ENTRY_LIST_NAME;
//	  Budget budget1 = calorieCountData1.getBudget();
//	  calorieCountData0.populateFromElement(simpleElement2);
//	  FrequentFoodList frequentFoodList2 = calorieCountData0.getFrequentFoods();
//	  EntryList entryList4 = calorieCountData1.getEntryList();
//	  CalorieCountDataElements calorieCountDataElements6 = CalorieCountDataElements.Food;
//	  Ccde ccde2 = calorieCountData1.getDataElement(calorieCountDataElements6);
//	  EntryList entryList5 = calorieCountData0.getEntryList();
//	  calorieCountData1.buildEntries();
//	  calorieCountData1.setEntryList(entryList5);
//	  calorieCountData1.setMeals(mealList0);
//	  FoodList foodList4 = calorieCountData0.getFoods();
//	  calorieCountData1.serializeTo(simpleElement0);
//	  calorieCountData0.setFrequentFoods(frequentFoodList2);
//	  SimpleElement simpleElement4 = calorieCountData0.createSerializationElement();
//	  calorieCountData0.setFoods(foodList0);
//	  calorieCountData0.setMeals(mealList0);
//	  calorieCountData0.setFoods(foodList4);
//	  EntryList entryList6 = calorieCountData1.getEntryList();
//	  SimpleElement simpleElement5 = simpleElement0.createChild(string5, int4);
//	  calorieCountData0.setBudget(budget0);
//	}

	/*
	 * Failure to reproduce.
	 */
//	@Test
//	public void evoobj_caloriecount_CalorieCountData_setEntryList_6() {
//	  // I14 Branch 45 IFEQ L-1;false
//	  // Out-method
//	  CalorieCountData calorieCountData0 = new CalorieCountData();
//	  EntryList entryList0 = calorieCountData0.getEntryList();
//	  calorieCountData0.setEntryList(entryList0);
//	}

	/*
	 * Failure to reproduce.
	 */
//	@Test
//	public void evoobj_caloriecount_CalorieCountData_setFrequentFoods_6() throws ApplicationException {
//	  // I27 Branch 20 IFNE L-1;false
//	  // Out-method
//	  CalorieCountData calorieCountData0 = new CalorieCountData();
//	  List<String> list0 = new NotifyingListAdaptor<String>();
//	  FrequentFoodList frequentFoodList0 = new FrequentFoodList();
//	  calorieCountData0.setFrequentFoods(frequentFoodList0);
//	}

	/*
	 * The field TreeTable.tree is not visible
	 */
//	@Test
//	public void evoobj_caloriecount_TreeTable_updateUI_8() {
//	  // I13 Branch 8 IFEQ L-1;true
//	  // In-method
//	  byte[] byteArray0 = new byte[1];
//	  DefaultTreeTableModel defaultTreeTableModel0 = new DefaultTreeTableModel();
//	  TreeTable treeTable0 = new TreeTable(defaultTreeTableModel0);
//	  TreeTable.TreeTableCellRenderer treeTable_TreeTableCellRenderer0 = treeTable0.tree;
//	  treeTable_TreeTableCellRenderer0.updateUI();
//	  treeTable0.updateUI();
//	  byte byte0 = (byte)53;
//	  byteArray0[0] = byte0;
//	  byteArray0[0] = byteArray0[0];
//	  byteArray0[0] = byte0;
//	  boolean boolean0 = treeTable0.isBackgroundSet();
//	  DefaultTableTreeModel defaultTableTreeModel0 = new DefaultTableTreeModel();
//	  TreeTable treeTable1 = new TreeTable(defaultTableTreeModel0);
//	}

	/*
	 * The constructor Attribute(String) is not visible
	 */
//	@Test
//	public void evoobj_wheelwebtool_ClassWriter_toByteArray_4() {
//	  // I219 Branch 27 IF_ICMPGE L139;false
//	  // In-method
//	  int int0 = (-340);
//	  ClassWriter classWriter0 = new ClassWriter(int0);
//	  String string0 = "a}*";
//	  wheel.asm.Attribute attribute0 = new wheel.asm.Attribute(string0);
//	  classWriter0.visitAttribute(attribute0);
//	  byte[] byteArray0 = classWriter0.toByteArray();
//	}

	/*
	 * The type wheel.asm.FieldWriter is not visible
	 */
//	@Test
//	public void evoobj_wheelwebtool_FieldWriter_put_6() {
//	  // I4 Branch 15 IFNULL L127;false
//	  // In-method
//	  int int0 = 161;
//	  ClassWriter classWriter0 = new ClassWriter(int0);
//	  int int1 = 92;
//	  String string0 = "mo/z";
//	  String string1 = "wheel.asm.FieldWriter";
//	  String string2 = "&\\";
//	  String string3 = "";
//	  wheel.asm.FieldWriter fieldWriter0 = new FieldWriter(classWriter0, int1, string0, string1, string2, string3);
//	  int int2 = fieldWriter0.getSize();
//	  int int3 = 2192;
//	  ByteVector byteVector0 = new ByteVector(int3);
//	  String string4 = "";
//	  String string5 = "";
//	  Attribute attribute0 = new Attribute(string5);
//	  fieldWriter0.visitAttribute(attribute0);
//	  Attribute attribute1 = new Attribute(string4);
//	  fieldWriter0.visitAttribute(attribute1);
//	  boolean boolean0 = true;
//	  AnnotationWriter annotationWriter0 = (AnnotationWriter)fieldWriter0.visitAnnotation(string1, boolean0);
//	  fieldWriter0.put(byteVector0);
//	}

	/*
	 * The field Label.status is not visible
	 * The method visitSubroutine(Label, long, int) from the type Label is not visible
	 */
//	@Test
//	public void evoobj_wheelwebtool_Label_visitSubroutine_2() {
//	  // I79 Branch 21 IFEQ L127;true
//	  // In-method
//	  Label label0 = new Label();
//	  int int0 = 2836;
//	  label0.status = int0;
//	  Label label1 = new Label();
//	  long long0 = 0L;
//	  int int1 = (-1940);
//	  label0.visitSubroutine(label1, long0, int1);
//	}

	/*
	 * The constructor TableBlock(Component) is not visible
	 * MXSerializer cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_wheelwebtool_Block__render_4() {
//	  // I28 Branch 1 IFLE L47;false
//	  // Out-method
//	  wheel.components.Component component0 = null;
//	  TableBlock tableBlock0 = new TableBlock(component0);
//	  MXSerializer mXSerializer0 = new MXSerializer();
//	  tableBlock0._render(mXSerializer0);
//	}

	/*
	 * The constructor Form(String) is not visible
	 */
//	@Test
//	public void evoobj_wheelwebtool_ComponentCreator_checkboxGroup_2() {
//	  // I79 Branch 21 IFEQ L127;true
//	  // In-method
//	  String string0 = "]0Z\\@q5:Y";
//	  Form form0 = new Form(string0);
//	  ComponentCreator componentCreator0 = new ComponentCreator(form0);
//	  String string1 = "nY(a3Pz";
//	  StringSelectModel stringSelectModel0 = new StringSelectModel();
//	  String string2 = "A>%]";
//	  ElExpression elExpression0 = new ElExpression(string2);
//	  CheckboxGroup checkboxGroup0 = componentCreator0.checkboxGroup(string1, stringSelectModel0, elExpression0);
//	}

	/*
	 * Used parameter not set (forComponent)
	 */
	@Test
	public void evoobj_wheelwebtool_ComponentCreator_form_0() {
	  // I529 Branch 39 IFEQ L172;false
	  // In-method
	  wheel.components.Component component0 = null;
	  ComponentCreator componentCreator0 = new ComponentCreator(component0);
	  String string0 = "<~.-.`DD<Mz0DLG&";
	  ActionExpression actionExpression0 = null;
	  Form form0 = componentCreator0.form(string0, actionExpression0);
	}

	/*
	 * The constructor Table(Component, String) is not visible
	 */
//	@Test
//	public void evoobj_wheelwebtool_Table__clear_1() {
//	  // I79 Branch 21 IFEQ L127;true
//	  // Out-method
//	  String string0 = "NT~+QE-gX/GA";
//	  XmlEntityRef xmlEntityRef0 = new XmlEntityRef(string0);
//	  String string1 = null;
//	  Table table0 = new Table(xmlEntityRef0, string1);
//	  TableBlock tableBlock0 = table0.tfoot();
//	  table0._clear();
//	}

	/*
	 * Null parameter passed to method.
	 */
	@Test
	public void evoobj_wheelwebtool_WheelAnnotationVisitor_visit_8() {
	  // I28 Branch 1 IFLE L47;true
	  // Out-method
	  AnnotationVisitor annotationVisitor0 = null;
	  WheelAnnotationVisitor wheelAnnotationVisitor0 = new WheelAnnotationVisitor(annotationVisitor0);
	  String string0 = "";
	  Object object0 = new Object();
	  String string1 = null;
	  String string2 = "";
	  AnnotationVisitor annotationVisitor1 = wheelAnnotationVisitor0.visitAnnotation(string1, string2);
	  wheelAnnotationVisitor0.visit(string0, object0);
	}

	/*
	 * Null parameter passed into constructor
	 */
	@Test
	public void evoobj_wheelwebtool_WheelFieldVisitor_visitEnd_1() {
	  // I44 Branch 2 IFLE L49;false
	  // In-method
	  FieldVisitor fieldVisitor0 = null;
	  LinkedList<WheelAnnotatedField> linkedList0 = new LinkedList<WheelAnnotatedField>();
	  int int0 = 2;
	  String string0 = "bJ[G .F*%U1 9e'";
	  String string1 = "D";
	  String string2 = "";
	  WheelFieldVisitor wheelFieldVisitor0 = new WheelFieldVisitor(fieldVisitor0, linkedList0, int0, string0, string1, string2);
	  wheelFieldVisitor0.visitEnd();
	}

	/*
	 * The type wheel.util.Entry is not visible
	 */
//	@Test
//	public void evoobj_wheelwebtool_ActionRegistry_needsRebuilding_5() {
//	  // I81 Branch 8 IF_ACMPNE L95;false
//	  // In-method
//	  ActionRegistry actionRegistry0 = new ActionRegistry();
//	  ErrorPage errorPage0 = new ErrorPage();
//	  Block block0 = (Block)errorPage0.tt();
//	  String string0 = "|I6_ep-(1a+$ \"M/rW";
//	  String string1 = "*\\;d4/^M0p=nk*#";
//	  boolean boolean0 = false;
//	  wheel.util.Entry entry0 = actionRegistry0.addEntry(string0, string1, boolean0);
//	  String string2 = "Y}}Py>g";
//	  boolean boolean1 = actionRegistry0.isActionMethod(block0, string2);
//	  String string3 = "PGbM^";
//	  Block block1 = (Block)errorPage0.h5();
//	  String string4 = "Fkaz'";
//	  String string5 = "*;jwI";
//	  boolean boolean2 = false;
//	  String string6 = "wheel.components.StandaloneComponent";
//	  boolean boolean3 = actionRegistry0.isActionMethod(errorPage0, string6);
//	  Entry entry1 = actionRegistry0.addEntry(string4, string5, boolean2);
//	  String string7 = "v>Sc'}nI";
//	  String string8 = null;
//	  boolean boolean4 = false;
//	  Block block2 = (Block)errorPage0.b();
//	  Entry entry2 = actionRegistry0.addEntry(string7, string8, boolean4);
//	  Entry entry3 = new Entry(string6, string4, boolean4);
//	  String string9 = "wheel.util.ActionRegistry";
//	  String string10 = "";
//	  boolean boolean5 = false;
//	  Entry entry4 = actionRegistry0.addEntry(string9, string10, boolean5);
//	  String string11 = "M#xD||!),*=gBO@y";
//	  String string12 = null;
//	  boolean boolean6 = false;
//	  Entry entry5 = actionRegistry0.addEntry(string11, string12, boolean6);
//	  String string13 = "";
//	  Block block3 = (Block)errorPage0.h4();
//	  entry5.className = string13;
//	  String string14 = "F]F|<svm{Ja)eu!.T";
//	  entry5.className = string14;
//	  Link link0 = (Link)errorPage0.a(entry5);
//	  Boolean boolean7 = new Boolean(string7);
//	  String string15 = "k>Ri";
//	  boolean boolean8 = actionRegistry0.isActionMethod(errorPage0, string15);
//	  String string16 = null;
//	  Entry entry6 = new Entry(string9, string9, boolean1);
//	  String string17 = "b)";
//	  boolean boolean9 = actionRegistry0.isActionMethod(errorPage0, string17);
//	  String string18 = "9";
//	  String string19 = "o?CBi0(";
//	  boolean boolean10 = true;
//	  Entry entry7 = actionRegistry0.addEntry(string18, string19, boolean10);
//	  String string20 = "W_4:AR:VH";
//	  boolean boolean11 = actionRegistry0.isActionMethod(block1, string20);
//	  String string21 = "r=W]**=mH7IJS9x";
//	  boolean boolean12 = actionRegistry0.isActionMethod(block1, string21);
//	  String string22 = "\"og6bpSu-YZo";
//	  String string23 = "*I";
//	  boolean boolean13 = false;
//	  Entry entry8 = actionRegistry0.addEntry(string22, string23, boolean13);
//	  String string24 = "3jUO8@";
//	  String string25 = "wheel.components.StandaloneComponent";
//	  boolean boolean14 = false;
//	  Entry entry9 = actionRegistry0.addEntry(string24, string25, boolean14);
//	  Block block4 = (Block)errorPage0.fieldset();
//	  Boolean boolean15 = new Boolean(string14);
//	  String string26 = "";
//	  boolean boolean16 = actionRegistry0.isActionMethod(block0, string26);
//	  String string27 = "wheel.components.StandaloneComponent";
//	  boolean boolean17 = actionRegistry0.isActionMethod(block1, string27);
//	  String string28 = "Kc]&}p$1V";
//	  boolean boolean18 = actionRegistry0.isActionMethod(errorPage0, string28);
//	  String string29 = "4Q8P";
//	  errorPage0._clear();
//	  String string30 = "";
//	  boolean boolean19 = false;
//	  Entry entry10 = actionRegistry0.addEntry(string29, string30, boolean19);
//	  String string31 = "7m9'fe|";
//	  String string32 = "#";
//	  boolean boolean20 = false;
//	  Entry entry11 = actionRegistry0.addEntry(string31, string32, boolean20);
//	  String string33 = "";
//	  String string34 = "2C[=sXm[j";
//	  entry5.methodName = string34;
//	  String string35 = "";
//	  boolean boolean21 = true;
//	  Link link1 = (Link)link0.a();
//	  Entry entry12 = actionRegistry0.addEntry(string33, string35, boolean21);
//	  Boolean boolean22 = new Boolean(boolean2);
//	  Entry entry13 = new Entry(string9, string29, boolean21);
//	  String string36 = "_S~P`";
//	  boolean boolean23 = actionRegistry0.isActionMethod(block2, string36);
//	  String string37 = "wheel.components.StandaloneComponent";
//	  boolean boolean24 = actionRegistry0.isActionMethod(link0, string37);
//	  String string38 = "7[yk";
//	  String string39 = "";
//	  boolean boolean25 = false;
//	  block2._setGeneratedId(boolean4);
//	  Entry entry14 = actionRegistry0.addEntry(string38, string39, boolean25);
//	  String string40 = "";
//	  boolean boolean26 = actionRegistry0.isActionMethod(block0, string40);
//	  String string41 = "6n!<aeW6'+egO5|KT";
//	  boolean boolean27 = actionRegistry0.isActionMethod(block2, string41);
//	  Boolean boolean28 = new Boolean(boolean1);
//	  String string42 = "wheel.util.ActionRegistry";
//	  boolean boolean29 = actionRegistry0.isActionMethod(block3, string42);
//	  String string43 = "";
//	  String string44 = "wheel/util/ActionRegistry#needsRebuilding(Lwheel/components/Component;Ljava/lang/String;)Z";
//	  boolean boolean30 = true;
//	  Entry entry15 = actionRegistry0.addEntry(string43, string44, boolean30);
//	  String string45 = "wheel/util/ActionRegistry#needsRebuilding(Lwheel/components/Component;Ljava/lang/String;)Z";
//	  String string46 = "wheel.components.Radio";
//	  boolean boolean31 = false;
//	  Entry entry16 = actionRegistry0.addEntry(string45, string46, boolean31);
//	  String string47 = "wheel.util.ActionRegistry";
//	  boolean boolean32 = false;
//	  String string48 = "=J,AE^s:&G9u9uF4\\";
//	  errorPage0._setSubmitTarget(string33);
//	  Entry entry17 = actionRegistry0.addEntry(string48, string29, (boolean) boolean7);
//	  Entry entry18 = actionRegistry0.addEntry(string47, string19, boolean32);
//	  boolean boolean33 = actionRegistry0.isActionMethod(link0, string16);
//	  boolean boolean34 = actionRegistry0.isActionMethod(block3, string36);
//	  String string49 = null;
//	  String string50 = "jmB{\"7xu:dD5";
//	  boolean boolean35 = true;
//	  Entry entry19 = actionRegistry0.addEntry(string49, string50, boolean35);
//	  String string51 = "asset/wheel/components/jquery.js?expires=88";
//	  String string52 = "b^`\\A;M*";
//	  boolean boolean36 = false;
//	  Entry entry20 = actionRegistry0.addEntry(string51, string52, boolean36);
//	  boolean boolean37 = actionRegistry0.needsRebuilding(errorPage0, string3);
//	}

	/*
	 * Used parameter not set.
	 */
	@Test
	public void evoobj_xbus_JournalBean_getDetailsAsTable_3() {
	  // I688 Branch 34 IFNULL L621;false
	  // In-method
	  JournalBean journalBean0 = new JournalBean();
	  String string0 = "nt.sf.xbus.base.core.trace.Trace";
	  String string1 = "request";
	  journalBean0.setMessage(string1);
	  journalBean0.setDetails(string0);
	  journalBean0.setRequestTimeMax(string0);
	  String string2 = journalBean0.getDetailsAsTable();
	}

	/*
	 * RPCElement cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_xbus_IteratedWhitespaceInElementDeletion_iteratedProcedure_9() {
//	  // I3 Branch 5 IFNULL L145;true
//	  // In-method
//	  IteratedWhitespaceInElementDeletion iteratedWhitespaceInElementDeletion0 = new IteratedWhitespaceInElementDeletion();
//	  String string0 = "CQp?\u0081";
//	  String string1 = "net.sf.bus.base.xml.IteratedWhitespaceInElementDeletion";
//	  Object[] objectArray0 = new Object[11];
//	  objectArray0[0] = (Object) string0;
//	  objectArray0[0] = (Object) string0;
//	  RPCElement rPCElement0 = new RPCElement(string1);
//	  long long0 = 0L;
//	  String string2 = null;
//	  RPCElement rPCElement1 = (RPCElement)rPCElement0.addTextNode(string2);
//	  System.setCurrentTimeMillis(long0);
//	  long long1 = (-630L);
//	  System.setCurrentTimeMillis(long1);
//	  iteratedWhitespaceInElementDeletion0.iteratedProcedure(rPCElement0);
//	}

	/*
	 * RecordTypeDescriptionChecker cannot be resolved to a type
	 */
//	@Test
//	public void evoobj_xbus_RecordTypeDescriptionChecker_typeIdsAreUnique_3() {
//	  // I560 Branch 126 IF_ICMPLT L270;false
//	  // In-method
//	  RecordTypeDescriptionChecker recordTypeDescriptionChecker0 = new RecordTypeDescriptionChecker();
//	  LinkedList<WMLImgElementImpl> linkedList0 = new LinkedList<WMLImgElementImpl>();
//	  WMLImgElementImpl wMLImgElementImpl0 = null;
//	  linkedList0.addFirst(wMLImgElementImpl0);
//	  Object[] objectArray0 = TypeDesc.noObjects;
//	  RecordTypeDescriptionChecker recordTypeDescriptionChecker1 = RecordTypeDescriptionChecker.getInstance();
//	  boolean boolean0 = recordTypeDescriptionChecker0.typeIdsAreUnique(linkedList0, linkedList0);
//	}

	/*
	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_ifx_framework_AcctTaxInfo_equals_9() {
//	  // I4 Branch 1 IF_ACMPNE L74;false
//	  // Out-method
//	  AcctTaxInfo acctTaxInfo0 = new AcctTaxInfo();
//	  File file0 = null;
//	  MockPrintWriter mockPrintWriter0 = new MockPrintWriter(file0);
//	  acctTaxInfo0.marshal(mockPrintWriter0);
//	  Object object0 = new Object();
//	  String string0 = null;
//	  StringReader stringReader0 = new StringReader(string0);
//	  AcctTaxInfo acctTaxInfo1 = AcctTaxInfo.unmarshalAcctTaxInfo(stringReader0);
//	  boolean boolean0 = acctTaxInfo0.equals(object0);
//	}

	/*
	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_ifx_framework_BankInfo_equals_1() {
//	  // I107 Branch 27 IFNULL L107;false
//	  // In-method
//	  BankInfo bankInfo0 = new BankInfo();
//	  bankInfo0.validate();
//	  BankInfo bankInfo1 = new BankInfo();
//	  boolean boolean0 = bankInfo0.equals(bankInfo1);
//	  BankInfo bankInfo2 = new BankInfo();
//	  Object object0 = new Object();
//	  String string0 = "=8- !b9w*";
//	  MockPrintWriter mockPrintWriter0 = new MockPrintWriter(string0);
//	  char[] charArray0 = new char[7];
//	  char char0 = 't';
//	  charArray0[0] = char0;
//	  char char1 = '\"';
//	  charArray0[1] = char1;
//	  char char2 = '>';
//	  charArray0[2] = char2;
//	  char char3 = '3';
//	  charArray0[3] = char3;
//	  char char4 = ',';
//	  charArray0[4] = char4;
//	  char char5 = 'I';
//	  charArray0[5] = char5;
//	  char char6 = 'f';
//	  charArray0[6] = char6;
//	  mockPrintWriter0.println(charArray0);
//	  String string1 = "X8W@'jX&<mCv";
//	  bankInfo2.setBankId(string1);
//	  String string2 = null;
//	  int int0 = 2147;
//	  int int1 = 7;
//	  mockPrintWriter0.write(string2, int0, int1);
//	}

	/*
	 * Marshaller cannot be resolved to a variable
	 */
//	@Test
//	public void evoobj_ifx_framework_BankSvcRq_TypeSequence_equals_4() {
//	  // I54 Branch 6 IFGT L85;false
//	  // Out-method
//	  BankSvcRq_TypeSequence bankSvcRq_TypeSequence0 = new BankSvcRq_TypeSequence();
//	  boolean boolean0 = bankSvcRq_TypeSequence0.isValid();
//	  PipedReader pipedReader0 = new PipedReader();
//	  BankSvcRq_TypeSequence bankSvcRq_TypeSequence1 = new BankSvcRq_TypeSequence();
//	  Object object0 = new Object();
//	  BankSvcRq_TypeSequenceItem bankSvcRq_TypeSequenceItem0 = new BankSvcRq_TypeSequenceItem();
//	  bankSvcRq_TypeSequence0.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem0);
//	  Marshaller.enableDebug = true;
//	  bankSvcRq_TypeSequence1.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem0);
//	  boolean boolean1 = bankSvcRq_TypeSequence0.equals(bankSvcRq_TypeSequence1);
//	  BankSvcRq_TypeSequenceItem bankSvcRq_TypeSequenceItem1 = bankSvcRq_TypeSequence1.getBankSvcRq_TypeSequenceItem();
//	  boolean boolean2 = bankSvcRq_TypeSequence1.equals(bankSvcRq_TypeSequence0);
//	  StringWriter stringWriter0 = new StringWriter();
//	  bankSvcRq_TypeSequence1.marshal(stringWriter0);
//	  CharArrayWriter charArrayWriter0 = new CharArrayWriter();
//	  XMLFilterImpl xMLFilterImpl0 = new XMLFilterImpl();
//	  XMLFilterImpl xMLFilterImpl1 = new XMLFilterImpl();
//	  String string0 = "CustSyn0cRq";
//	  Object object1 = new Object();
//	  Locator2Impl locator2Impl0 = new Locator2Impl();
//	  Locator2Impl locator2Impl1 = new Locator2Impl();
//	  LocatorImpl locatorImpl0 = new LocatorImpl(locator2Impl1);
//	  xMLFilterImpl0.setDocumentLocator(locatorImpl0);
//	  String string1 = "hKSRdnyC";
//	  xMLFilterImpl0.parse(string1);
//	  xMLFilterImpl1.setProperty(string0, object1);
//	  XMLReaderAdapter xMLReaderAdapter0 = new XMLReaderAdapter(xMLFilterImpl1);
//	  String string2 = "DepAcctIdTo";
//	  Locator2Impl locator2Impl2 = new Locator2Impl();
//	  String string3 = null;
//	  String string4 = "Yr";
//	  char[] charArray0 = new char[10];
//	  char char0 = ':';
//	  charArray0[0] = char0;
//	  String string5 = "";
//	  locator2Impl2.setPublicId(string5);
//	  charArray0[1] = char0;
//	  charArray0[2] = charArray0[1];
//	  charArray0[3] = charArray0[1];
//	  char char1 = 'f';
//	  charArray0[4] = char1;
//	  char char2 = ']';
//	  charArray0[3] = char2;
//	  char char3 = 'w';
//	  String string6 = null;
//	  String string7 = "/g|!1%'5iC=";
//	  String string8 = "net.sourceforge.ifxfv3.beans.EmploymentHistory_TypeDescriptor$6";
//	  Attributes attributes0 = null;
//	  xMLReaderAdapter0.startDocument();
//	  Attributes2Impl attributes2Impl0 = new Attributes2Impl(attributes0);
//	  xMLReaderAdapter0.startElement(string6, string7, string8, attributes2Impl0);
//	  charArray0[6] = char3;
//	  int int0 = 378;
//	  int int1 = 297;
//	  xMLFilterImpl0.ignorableWhitespace(charArray0, int0, int1);
//	  Exception exception0 = null;
//	  SAXParseException sAXParseException0 = new SAXParseException(string1, string7, string3, int0, charArray0[6]);
//	  xMLFilterImpl1.warning(sAXParseException0);
//	  String string9 = "{ZdqDLQ";
//	  int int2 = locator2Impl2.getLineNumber();
//	  charArrayWriter0.write(charArray0);
//	  String string10 = "wtNf";
//	  String string11 = "Ni4oy$yNPmL64L/XqO3";
//	  xMLFilterImpl1.setDocumentLocator(locator2Impl2);
//	  int int3 = (-2911);
//	  int int4 = 51;
//	  SAXParseException sAXParseException1 = new SAXParseException(string9, string10, string11, int3, int4);
//	  BankSvcRq_TypeSequence bankSvcRq_TypeSequence2 = new BankSvcRq_TypeSequence();
//	  SAXParseException sAXParseException2 = new SAXParseException(string5, string3, string4, char3, int4, exception0);
//	  int int5 = sAXParseException0.getLineNumber();
//	  Throwable throwable0 = sAXParseException1.getCause();
//	  Throwable throwable1 = sAXParseException0.initCause(sAXParseException2);
//	  SAXParseException sAXParseException3 = new SAXParseException(string2, locator2Impl2, sAXParseException2);
//	  String string12 = sAXParseException1.getSystemId();
//	  sAXParseException3.addSuppressed(throwable0);
//	  xMLFilterImpl1.error(sAXParseException3);
//	  bankSvcRq_TypeSequence0.marshal(xMLReaderAdapter0);
//	  bankSvcRq_TypeSequence1.marshal(charArrayWriter0);
//	  bankSvcRq_TypeSequence1.marshal(xMLFilterImpl0);
//	  bankSvcRq_TypeSequence1.marshal(stringWriter0);
//	  BankSvcRq_TypeSequenceItem bankSvcRq_TypeSequenceItem2 = new BankSvcRq_TypeSequenceItem();
//	  bankSvcRq_TypeSequence0.validate();
//	  ByteArrayOutputStream byteArrayOutputStream0 = new ByteArrayOutputStream();
//	  BufferedOutputStream bufferedOutputStream0 = new BufferedOutputStream(byteArrayOutputStream0);
//	  MockPrintStream mockPrintStream0 = new MockPrintStream(string1, string4);
//	  byte[] byteArray0 = new byte[10];
//	  byte byte0 = (byte)20;
//	  byteArray0[0] = byte0;
//	  byte byte1 = (byte)1;
//	  byteArray0[1] = byteArray0[0];
//	  bankSvcRq_TypeSequence0.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem2);
//	  pipedReader0.reset();
//	  byteArray0[2] = byte1;
//	  byteArray0[3] = byteArray0[0];
//	  byteArray0[4] = byteArray0[1];
//	  byte byte2 = (byte)69;
//	  byteArray0[5] = byte2;
//	  byte byte3 = (byte)14;
//	  byteArray0[6] = byte3;
//	  DefaultHandler defaultHandler0 = new DefaultHandler();
//	  xMLFilterImpl0.setContentHandler(defaultHandler0);
//	  byteArray0[7] = byteArray0[4];
//	  byte byte4 = (byte)16;
//	  byteArray0[7] = byte4;
//	  int int6 = 24;
//	  int int7 = 6;
//	  bufferedOutputStream0.write(byteArray0, int6, int7);
//	  int int8 = (-1110);
//	  int int9 = 17;
//	  bufferedOutputStream0.write(byteArray0, int8, int9);
//	  MockPrintWriter mockPrintWriter0 = new MockPrintWriter(mockPrintStream0);
//	  mockPrintWriter0.print(charArray0);
//	  bankSvcRq_TypeSequence1.marshal(mockPrintWriter0);
//	  bankSvcRq_TypeSequence2.validate();
//	  boolean boolean3 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  BankSvcRq_TypeSequenceItem bankSvcRq_TypeSequenceItem3 = bankSvcRq_TypeSequence0.getBankSvcRq_TypeSequenceItem();
//	  bankSvcRq_TypeSequence2.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem3);
//	  BankSvcRq_TypeSequenceItem bankSvcRq_TypeSequenceItem4 = new BankSvcRq_TypeSequenceItem();
//	  bankSvcRq_TypeSequence2.validate();
//	  boolean boolean4 = bankSvcRq_TypeSequence1.isValid();
//	  XMLReaderAdapter xMLReaderAdapter1 = new XMLReaderAdapter();
//	  xMLFilterImpl0.startDocument();
//	  bankSvcRq_TypeSequence0.marshal(xMLReaderAdapter1);
//	  BankSvcRq_TypeSequence bankSvcRq_TypeSequence3 = new BankSvcRq_TypeSequence();
//	  bankSvcRq_TypeSequence0.validate();
//	  bankSvcRq_TypeSequence0.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem3);
//	  CharArrayWriter charArrayWriter1 = charArrayWriter0.append(charArray0[4]);
//	  boolean boolean5 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  bankSvcRq_TypeSequence1.validate();
//	  bankSvcRq_TypeSequence3.validate();
//	  boolean boolean6 = FileSystemHandling.shouldAllThrowIOExceptions();
//	  bankSvcRq_TypeSequence0.marshal(charArrayWriter0);
//	  boolean boolean7 = bankSvcRq_TypeSequence3.isValid();
//	  bankSvcRq_TypeSequence3.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem0);
//	  BankSvcRq_TypeSequence bankSvcRq_TypeSequence4 = new BankSvcRq_TypeSequence();
//	  bankSvcRq_TypeSequence1.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem0);
//	  BankSvcRq_TypeSequence bankSvcRq_TypeSequence5 = new BankSvcRq_TypeSequence();
//	  bankSvcRq_TypeSequence1.marshal(xMLFilterImpl1);
//	  BankSvcRq_TypeSequenceItem bankSvcRq_TypeSequenceItem5 = bankSvcRq_TypeSequence2.getBankSvcRq_TypeSequenceItem();
//	  RecXferInqRq recXferInqRq0 = new RecXferInqRq();
//	  bankSvcRq_TypeSequenceItem5.setRecXferInqRq(recXferInqRq0);
//	  bankSvcRq_TypeSequence3.setBankSvcRq_TypeSequenceItem(bankSvcRq_TypeSequenceItem5);
//	  BankSvcRq_TypeSequence bankSvcRq_TypeSequence6 = new BankSvcRq_TypeSequence();
//	  boolean boolean8 = bankSvcRq_TypeSequence6.equals(object0);
//	}

	/*
	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_ifx_framework_IFX_Type_equals_6() {
//	  // I4 Branch 1 IF_ACMPNE L121;true
//	  // Out-method
//	  net.sourceforge.ifxfv3.beans.IFX_Type iFX_Type0 = new IFX_Type();
//	  Object object0 = new Object();
//	  boolean boolean0 = iFX_Type0.equals(iFX_Type0);
//	  boolean boolean1 = iFX_Type0.isValid();
//	  File file0 = null;
//	  MockFileOutputStream mockFileOutputStream0 = new MockFileOutputStream(file0);
//	  int int0 = 0;
//	  byte[] byteArray0 = new byte[0];
//	  int int1 = 0;
//	  int int2 = 1659;
//	  mockFileOutputStream0.write(byteArray0, int1, int2);
//	  mockFileOutputStream0.write(int0);
//	  byte[] byteArray1 = new byte[8];
//	  byte byte0 = (byte)0;
//	  byteArray1[0] = byte0;
//	  byte byte1 = (byte) (-77);
//	  String string0 = "ugL[QY`$@}";
//	  MockPrintWriter mockPrintWriter0 = new MockPrintWriter(string0);
//	  String string1 = "^w5%+7";
//	  mockPrintWriter0.print(string1);
//	  int int3 = 0;
//	  int int4 = 0;
//	  PrintWriter printWriter0 = mockPrintWriter0.append(string0, int3, int4);
//	  iFX_Type0.marshal(mockPrintWriter0);
//	  Writer writer0 = null;
//	  boolean boolean2 = false;
//	  IFX_TypeSequence iFX_TypeSequence0 = iFX_Type0.getIFX_TypeSequence();
//	  String string2 = null;
//	  DefaultHandler defaultHandler0 = new DefaultHandler();
//	  Locator locator0 = null;
//	  Locator2Impl locator2Impl0 = new Locator2Impl(locator0);
//	  defaultHandler0.setDocumentLocator(locator2Impl0);
//	  iFX_Type0.marshal(defaultHandler0);
//	  boolean boolean3 = iFX_TypeSequence0.equals(string2);
//	  int int5 = (-1581);
//	  IFX_TypeSequenceSequence iFX_TypeSequenceSequence0 = iFX_TypeSequence0.getIFX_TypeSequenceSequence(int5);
//	  iFX_Type0.setIFX_TypeSequence(iFX_TypeSequence0);
//	  MockPrintWriter mockPrintWriter1 = new MockPrintWriter(writer0, boolean2);
//	  char[] charArray0 = new char[5];
//	  char char0 = 'Q';
//	  charArray0[0] = char0;
//	  char char1 = 't';
//	  charArray0[1] = char1;
//	  char char2 = 'f';
//	  charArray0[2] = char2;
//	  char char3 = 'Z';
//	  charArray0[3] = char3;
//	  charArray0[4] = char1;
//	  int int6 = 452;
//	  int int7 = 56320;
//	  mockPrintWriter1.write(charArray0, int6, int7);
//	  iFX_Type0.marshal(mockPrintWriter1);
//	  byteArray1[1] = byte1;
//	  byte byte2 = (byte) (-21);
//	  byteArray1[2] = byte2;
//	  byte byte3 = (byte)0;
//	  byteArray1[3] = byte3;
//	  byte byte4 = (byte)0;
//	  byteArray1[4] = byte4;
//	  byte byte5 = (byte)0;
//	  byteArray1[4] = byte5;
//	  byte byte6 = (byte) (-29);
//	  byteArray1[6] = byte6;
//	  byte byte7 = (byte)3;
//	  byte[] byteArray2 = new byte[9];
//	  byteArray2[0] = byte3;
//	  byteArray2[1] = byteArray2[0];
//	  byteArray2[1] = byte0;
//	  byteArray2[3] = byte0;
//	  byteArray2[4] = byte3;
//	  byteArray2[5] = byte5;
//	  byteArray2[6] = byteArray2[5];
//	  byte byte8 = (byte) (-79);
//	  byteArray2[7] = byte8;
//	  byteArray2[8] = byte2;
//	  mockFileOutputStream0.write(byteArray2);
//	  byteArray1[7] = byte7;
//	  mockFileOutputStream0.write(byteArray1);
//	  boolean boolean4 = false;
//	  MockPrintWriter mockPrintWriter2 = new MockPrintWriter(mockFileOutputStream0, boolean4);
//	  double double0 = 0.0;
//	  FileChannel fileChannel0 = mockFileOutputStream0.getChannel();
//	  mockPrintWriter2.print(double0);
//	  mockPrintWriter2.flush();
//	  iFX_Type0.marshal(mockPrintWriter2);
//	  IFX_TypeSequence iFX_TypeSequence1 = iFX_Type0.getIFX_TypeSequence();
//	  iFX_Type0.validate();
//	  int int8 = 1962;
//	  IFX_TypeSequenceSequence iFX_TypeSequenceSequence1 = new IFX_TypeSequenceSequence();
//	  iFX_TypeSequence1.setIFX_TypeSequenceSequence(int8, iFX_TypeSequenceSequence1);
//	  iFX_TypeSequence1.addIFX_TypeSequenceSequence(iFX_TypeSequenceSequence1);
//	  boolean boolean5 = iFX_TypeSequence1.isValid();
//	  iFX_Type0.validate();
//	  DefaultHandler2 defaultHandler2_0 = new DefaultHandler2();
//	  iFX_Type0.marshal(defaultHandler2_0);
//	  iFX_Type0.setIFX_TypeSequence(iFX_TypeSequence1);
//	  String string3 = "\\['MLm|v<";
//	  long long0 = 1L;
//	  mockPrintWriter2.print(long0);
//	  StringReader stringReader0 = new StringReader(string3);
//	  String string4 = "";
//	  defaultHandler2_0.skippedEntity(string4);
//	  boolean boolean6 = iFX_Type0.equals(string1);
//	  IFX_Type iFX_Type1 = new IFX_Type();
//	  IFX_Type iFX_Type2 = IFX_Type.unmarshalIFX_Type(stringReader0);
//	  boolean boolean7 = false;
//	  Marshaller.enableDebug = boolean7;
//	  iFX_Type1.validate();
//	  boolean boolean8 = iFX_Type0.equals(charArray0[1]);
//	  iFX_Type2.marshal(mockPrintWriter2);
//	  iFX_Type1.marshal(defaultHandler2_0);
//	  MockPrintWriter mockPrintWriter3 = new MockPrintWriter(string1, string0);
//	  iFX_Type1.marshal(mockPrintWriter3);
//	  IFX_TypeSequence2 iFX_TypeSequence2_0 = iFX_Type0.getIFX_TypeSequence2();
//	  iFX_Type1.marshal(mockPrintWriter2);
//	  String string5 = null;
//	  mockPrintWriter3.print(string5);
//	  IFX_Type iFX_Type3 = IFX_Type.unmarshalIFX_Type(stringReader0);
//	  boolean boolean9 = iFX_Type3.isValid();
//	  iFX_Type2.marshal(mockPrintWriter2);
//	  iFX_Type2.setIFX_TypeSequence2(iFX_TypeSequence2_0);
//	  int int9 = 2919;
//	  mockPrintWriter2.println(int9);
//	  IFX_TypeSequence2SequenceSequence[] iFX_TypeSequence2SequenceSequenceArray0 = new IFX_TypeSequence2SequenceSequence[6];
//	  IFX_TypeSequence2SequenceSequence iFX_TypeSequence2SequenceSequence0 = new IFX_TypeSequence2SequenceSequence();
//	  iFX_TypeSequence2SequenceSequenceArray0[0] = iFX_TypeSequence2SequenceSequence0;
//	  IFX_TypeSequence2SequenceSequence iFX_TypeSequence2SequenceSequence1 = new IFX_TypeSequence2SequenceSequence();
//	  iFX_TypeSequence2SequenceSequenceArray0[1] = iFX_TypeSequence2SequenceSequence1;
//	  IFX_TypeSequence2SequenceSequence iFX_TypeSequence2SequenceSequence2 = iFX_TypeSequence2_0.getIFX_TypeSequence2SequenceSequence(byteArray2[3]);
//	  iFX_TypeSequence2SequenceSequenceArray0[2] = iFX_TypeSequence2SequenceSequence2;
//	  IFX_TypeSequence2SequenceSequence iFX_TypeSequence2SequenceSequence3 = new IFX_TypeSequence2SequenceSequence();
//	  iFX_TypeSequence2SequenceSequenceArray0[3] = iFX_TypeSequence2SequenceSequence3;
//	  IFX_TypeSequence2SequenceSequence iFX_TypeSequence2SequenceSequence4 = new IFX_TypeSequence2SequenceSequence();
//	  XMLReaderAdapter xMLReaderAdapter0 = new XMLReaderAdapter();
//	  iFX_TypeSequence2SequenceSequence3.marshal(xMLReaderAdapter0);
//	  iFX_TypeSequence2SequenceSequenceArray0[4] = iFX_TypeSequence2SequenceSequence4;
//	  IFX_TypeSequence2SequenceSequence iFX_TypeSequence2SequenceSequence5 = new IFX_TypeSequence2SequenceSequence();
//	  iFX_TypeSequence2SequenceSequenceArray0[5] = iFX_TypeSequence2SequenceSequence5;
//	  iFX_TypeSequence2_0.setIFX_TypeSequence2SequenceSequence(iFX_TypeSequence2SequenceSequenceArray0);
//	  boolean boolean10 = false;
//	  mockPrintWriter3.println(boolean10);
//	  IFX_Type iFX_Type4 = new IFX_Type();
//	  boolean boolean11 = iFX_Type1.isValid();
//	  iFX_Type3.validate();
//	  iFX_Type2.marshal(mockPrintWriter3);
//	  IFX_Type iFX_Type5 = new IFX_Type();
//	  iFX_Type5.marshal(mockPrintWriter2);
//	  iFX_Type4.validate();
//	  iFX_Type2.validate();
//	  IFX_TypeSequence iFX_TypeSequence2 = iFX_Type1.getIFX_TypeSequence();
//	  iFX_Type0.validate();
//	  boolean boolean12 = iFX_Type4.isValid();
//	  IFX_TypeSequence2 iFX_TypeSequence2_1 = iFX_Type1.getIFX_TypeSequence2();
//	  IFX_Type iFX_Type6 = IFX_Type.unmarshalIFX_Type(stringReader0);
//	  boolean boolean13 = false;
//	  MockPrintWriter mockPrintWriter4 = new MockPrintWriter(writer0, boolean13);
//	  boolean boolean14 = true;
//	  Marshaller.enableDebug = boolean14;
//	  iFX_TypeSequence2_1.marshal(writer0);
//	  iFX_Type5.marshal(mockPrintWriter4);
//	  IFX_TypeSequence2 iFX_TypeSequence2_2 = iFX_Type5.getIFX_TypeSequence2();
//	  iFX_Type4.setIFX_TypeSequence2(iFX_TypeSequence2_1);
//	  iFX_Type3.validate();
//	  IFX_TypeSequence iFX_TypeSequence3 = new IFX_TypeSequence();
//	  iFX_Type3.setIFX_TypeSequence(iFX_TypeSequence3);
//	  IFX_TypeSequence2 iFX_TypeSequence2_3 = iFX_Type4.getIFX_TypeSequence2();
//	  String string6 = "";
//	  String string7 = "org.exolab.castor.util.Messages";
//	  MockPrintWriter mockPrintWriter5 = new MockPrintWriter(string6, string7);
//	  iFX_Type6.marshal(mockPrintWriter5);
//	  IFX_TypeSequence2 iFX_TypeSequence2_4 = iFX_Type4.getIFX_TypeSequence2();
//	  iFX_Type6.setIFX_TypeSequence(iFX_TypeSequence1);
//	  boolean boolean15 = iFX_Type3.isValid();
//	  iFX_Type3.setIFX_TypeSequence2(iFX_TypeSequence2_0);
//	  iFX_Type3.marshal(defaultHandler2_0);
//	  iFX_Type6.validate();
//	  IFX_TypeSequence iFX_TypeSequence4 = iFX_Type3.getIFX_TypeSequence();
//	  iFX_Type5.setIFX_TypeSequence2(iFX_TypeSequence2_2);
//	  iFX_Type5.setIFX_TypeSequence2(iFX_TypeSequence2_4);
//	  boolean boolean16 = iFX_Type6.isValid();
//	  IFX_TypeSequence2 iFX_TypeSequence2_5 = iFX_Type5.getIFX_TypeSequence2();
//	  iFX_Type1.marshal(writer0);
//	  boolean boolean17 = iFX_Type0.equals(object0);
//	}

	/*
	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_ifx_framework_LoanAcctId_equals_5() {
//	  // I4 Branch 1 IF_ACMPNE L121;true
//	  // Out-method
//	  LoanAcctId loanAcctId0 = new LoanAcctId();
//	  URI uRI0 = null;
//	  MockFile mockFile0 = new MockFile(uRI0);
//	  MockPrintWriter mockPrintWriter0 = new MockPrintWriter(mockFile0);
//	  loanAcctId0.marshal(mockPrintWriter0);
//	  Locale locale0 = Locale.CANADA_FRENCH;
//	  boolean boolean0 = loanAcctId0.equals(locale0);
//	}

	/*
	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_ifx_framework_MaxCurAmt_equals_8() {
//	  // I4 Branch 1 IF_ACMPNE L121;true
//	  // Out-method
//	  MaxCurAmt maxCurAmt0 = new MaxCurAmt();
//	  MaxCurAmt maxCurAmt1 = new MaxCurAmt();
//	  String string0 = null;
//	  StringReader stringReader0 = new StringReader(string0);
//	  MaxCurAmt maxCurAmt2 = MaxCurAmt.unmarshalMaxCurAmt(stringReader0);
//	  MaxCurAmt maxCurAmt3 = new MaxCurAmt();
//	  maxCurAmt3.validate();
//	  String string1 = "net.sourceforge.ifxfv3.beansCustAudRq_TypeDescriptor$1";
//	  String string2 = null;
//	  MockPrintWriter mockPrintWriter0 = new MockPrintWriter(string1, string2);
//	  boolean boolean0 = maxCurAmt1.isValid();
//	  maxCurAmt1.marshal(mockPrintWriter0);
//	  boolean boolean1 = maxCurAmt0.isValid();
//	  boolean boolean2 = maxCurAmt0.equals(maxCurAmt1);
//	}

	/*
	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
	 */
//	@Test
//	public void evoobj_ifx_framework_MediaAcctAdjMsgRec_equals_1() {
//	  // I4 Branch 1 IF_ACMPNE L121;false
//	  // Out-method
//	  MediaAcctAdjMsgRec mediaAcctAdjMsgRec0 = new MediaAcctAdjMsgRec();
//	  Writer writer0 = null;
//	  MockPrintWriter mockPrintWriter0 = new MockPrintWriter(writer0);
//	  mediaAcctAdjMsgRec0.marshal(mockPrintWriter0);
//	  Object object0 = new Object();
//	  boolean boolean0 = mediaAcctAdjMsgRec0.equals(object0);
//	}
}

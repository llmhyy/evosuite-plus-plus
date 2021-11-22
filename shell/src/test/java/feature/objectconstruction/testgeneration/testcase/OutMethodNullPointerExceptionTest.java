//package feature.objectconstruction.testgeneration.testcase;
//
//import java.awt.event.MouseEvent;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutput;
//import java.io.ObjectOutputStream;
//import java.io.PipedReader;
//import java.io.PipedWriter;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.net.URI;
//import java.rmi.server.ObjID;
//import java.text.Collator;
//import java.text.ParseException;
//import java.text.RuleBasedCollator;
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedHashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Stack;
//import java.util.Vector;
//import java.util.function.BiFunction;
//
//import javax.management.remote.JMXPrincipal;
//import javax.swing.JLabel;
//import javax.swing.JLayeredPane;
//import javax.swing.JTable;
//import javax.swing.JTextPane;
//import javax.swing.table.DefaultTableColumnModel;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.text.StyledDocument;
//import javax.swing.tree.TreeNode;
//
//import org.databene.jdbacl.model.CompositeDBObject;
//import org.databene.jdbacl.model.DBCatalog;
//import org.databene.jdbacl.model.DBCheckConstraint;
//import org.databene.jdbacl.model.DBForeignKeyConstraint;
//import org.databene.jdbacl.model.DBSchema;
//import org.databene.jdbacl.model.DBTable;
//import org.databene.jdbacl.model.DefaultDBTable;
//import org.evosuite.runtime.mock.java.io.MockFile;
//import org.evosuite.runtime.mock.java.io.MockPrintStream;
//import org.evosuite.runtime.mock.java.io.MockPrintWriter;
//import org.evosuite.runtime.mock.java.lang.MockThread;
//import org.evosuite.runtime.mock.javax.swing.MockDefaultListSelectionModel;
//import org.evosuite.runtime.testdata.FileSystemHandling;
//import org.exolab.jms.authentication.AuthenticationMgr;
//import org.exolab.jms.authentication.UserManager;
//import org.exolab.jms.common.threads.DefaultThreadPoolFactory;
//import org.exolab.jms.common.threads.ThreadListener;
//import org.exolab.jms.config.Configuration;
//import org.exolab.jms.events.BasicEventManager;
//import org.exolab.jms.gc.GarbageCollectionService;
//import org.exolab.jms.net.connector.ConnectionRequestInfo;
//import org.exolab.jms.net.connector.ManagedConnection;
//import org.exolab.jms.net.connector.ManagedConnectionAcceptor;
//import org.exolab.jms.net.connector.ResourceException;
//import org.exolab.jms.net.connector.URIRequestInfo;
//import org.exolab.jms.net.http.HTTPRequestInfo;
//import org.exolab.jms.net.http.HTTPSManagedConnectionFactory;
//import org.exolab.jms.net.orb.UnicastDelegate;
//import org.exolab.jms.net.rmi.RMIManagedConnectionFactory;
//import org.exolab.jms.net.rmi.RMIRequestInfo;
//import org.exolab.jms.net.socket.SocketRequestInfo;
//import org.exolab.jms.net.tcp.TCPSRequestInfo;
//import org.exolab.jms.net.util.Properties;
//import org.exolab.jms.net.vm.VMManagedConnectionFactory;
//import org.exolab.jms.persistence.DatabaseService;
//import org.exolab.jms.scheduler.Scheduler;
//import org.exolab.jms.scheduler.SerialTask;
//import org.exolab.jms.selector.Expression;
//import org.exolab.jms.selector.ExpressionFactory;
//import org.exolab.jms.selector.parser.SelectorAST;
//import org.exolab.jms.selector.parser.SelectorTreeParser;
//import org.exolab.jms.service.ServiceException;
//import org.exolab.jms.service.ServiceThreadListener;
//import org.exolab.jms.tranlog.ExternalXid;
//import org.exolab.jms.tranlog.TransactionState;
//import org.junit.Test;
//import org.w3c.dom.Node;
//import org.w3c.dom.events.EventListener;
//
//import com.browsersoft.openhre.hl7.api.config.HL7MessageGroupItem;
//import com.browsersoft.openhre.hl7.api.regular.ExpressionElementMapper;
//import com.browsersoft.openhre.hl7.api.regular.ExpressionMatrix;
//import com.browsersoft.openhre.hl7.api.regular.MessageTracerHandler;
//import com.browsersoft.openhre.hl7.impl.config.HL7ConfigurationImpl;
//import com.browsersoft.openhre.hl7.impl.config.HL7DataTypeMapImpl;
//import com.browsersoft.openhre.hl7.impl.config.HL7MessageGroupImpl;
//import com.browsersoft.openhre.hl7.impl.config.HL7MessageImpl;
//import com.browsersoft.openhre.hl7.impl.parser.HL7CheckerImpl;
//import com.browsersoft.openhre.hl7.impl.parser.HL7CheckerStateImpl;
//import com.browsersoft.openhre.hl7.impl.regular.ExpressionElementMapperImpl;
//import com.browsersoft.openhre.hl7.impl.regular.ExpressionElementMapperItemImpl;
//import com.browsersoft.openhre.hl7.impl.regular.ExpressionImpl;
//import com.browsersoft.openhre.hl7.impl.regular.ExpressionMatrixImpl;
//import com.browsersoft.openhre.hl7.impl.regular.ExpressionPartImpl;
//import com.browsersoft.openhre.hl7.impl.regular.MessageTracerImpl;
//import com.ib.client.ExecutionFilter;
//import com.lts.application.data.ApplicationData;
//import com.lts.application.data.coll.ADCAdaptor;
//import com.lts.caloriecount.data.CalorieCountData;
//import com.lts.caloriecount.data.entry.EntryList;
//import com.lts.xml.simple.SimpleElement;
//import com.objectmentors.state.EndState;
//import com.objectmentors.state.Event;
//import com.objectmentors.state.StartState;
//import com.objectmentors.state.Transition;
//import com.soops.CEN4010.JMCA.JParser.xmlParser.SaxProcessor;
//import com.soops.CEN4010.JMCA.JParser.xmlParser.State;
//import com.sun.webkit.dom.HTMLDocumentImpl;
//import com.sun.webkit.dom.HTMLInputElementImpl;
//import com.sun.webkit.dom.HTMLSelectElementImpl;
//
//import antlr.ASTFactory;
//import antlr.BaseAST;
//import antlr.CommonAST;
//import antlr.CommonASTWithHiddenTokens;
//import antlr.RecognitionException;
//import antlr.Token;
//import antlr.TreeParser;
//import antlr.collections.AST;
//import antlr.collections.ASTEnumeration;
//import corina.Range;
//import corina.Sample;
//import corina.Year;
//import corina.editor.DecadalModel;
//import corina.map.MapPanel;
//import corina.map.View;
//import corina.map.tools.ToolBox;
//import corina.map.tools.ZoomInTool;
//import de.huxhorn.lilith.data.access.AccessEvent;
//import de.huxhorn.lilith.data.access.HttpStatus;
//import de.huxhorn.lilith.data.access.protobuf.AccessEventProtobufEncoder;
//import de.huxhorn.lilith.data.access.protobuf.generated.AccessProto;
//import de.huxhorn.lilith.data.eventsource.LoggerContext;
//import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
//import de.huxhorn.lilith.data.logging.xml.StackTraceElementWriter;
//import de.huxhorn.lilith.swing.preferences.table.AccessStatusTypePreviewRenderer;
//import de.huxhorn.lilith.swing.table.ColorScheme;
//import dk.statsbiblioteket.summa.common.index.FieldProvider;
//import dk.statsbiblioteket.summa.common.index.IndexField;
//import dk.statsbiblioteket.summa.common.index.IndexGroup;
//import dk.statsbiblioteket.summa.common.shell.ShellContextImpl;
//import dk.statsbiblioteket.summa.common.util.CollatorFactory;
//import dk.statsbiblioteket.util.reader.CharSequenceReader;
//import macaw.presentationLayer.VariableSearchPanel;
//import macaw.system.SessionProperties;
//import net.sf.xbus.admin.html.JournalBean;
//import net.sourceforge.beanbin.BeanBinException;
//import net.sourceforge.beanbin.reflect.resolve.GetImplementationsFromJar;
//import net.sourceforge.ifxfv3.beans.BankAcctTrnImgInqRs;
//import net.sourceforge.ifxfv3.beans.BankAcctTrnImgInqRsSequence;
//import net.sourceforge.jwbf.core.contentRep.Article;
//import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;
//import net.sourceforge.jwbf.zim.bots.ZimWikiBot;
//import wheel.asm.AnnotationVisitor;
//import wheel.components.Any;
//import wheel.components.CheckboxGroup;
//import wheel.components.ComponentCreator;
//import wheel.components.ElExpression;
//import wheel.components.FileInput;
//import wheel.components.Form;
//import wheel.components.TextArea;
//import wheel.components.XmlEntityRef;
//import wheel.enhance.WheelAnnotationVisitor;
//import wheel.json.JSONArray;
//import wheel.json.JSONObject;
//import wheel.json.JSONTokener;
//import wheel.util.ActionRegistry;
//import wheel.util.StringSelectModel;
//
//public class OutMethodNullPointerExceptionTest {
//	/*
//	 * NoClassDefFoundError: org/databene/commons/Named
//	 */
//	@Test
//	public void jdbacl_DBCheckConstraint_isEquivalent_1() {
//		// I4 Branch 6 IF_ACMPNE L228;true
//		String string0 = "replaceFirst";
//		String string1 = "";
//		String string2 = null;
//		DBCheckConstraint dBCheckConstraint0 = new DBCheckConstraint(string0, string1, string2);
//		DBCheckConstraint dBCheckConstraint1 = null;
//		String string3 = dBCheckConstraint0.toString();
//		boolean boolean0 = dBCheckConstraint0.isEquivalent(dBCheckConstraint1);
//		String string4 = dBCheckConstraint0.getTableName();
//		String string5 = dBCheckConstraint0.toString();
//		boolean boolean1 = dBCheckConstraint0.isEquivalent(dBCheckConstraint1);
//	}
//	
//	/*
//	 * Non-visible fields.
//	 */
////	@Test
////	public void jdbacl_DBCheckConstraint_isEquivalent_2() {
////		String string0 = "org/databene/jdbacl/model/DBCheckConstraint#isEquivalent(Lorg/databene/jdbacl/model/DBCheckConstraint;)Z";
////		String string1 = "org/databene/jdbacl/model/DBCheckConstraint#isIdentical(Lorg/databene/jdbacl/model/DBObject;)Z";
////		String string2 = "toCharArray";
////		DBCheckConstraint dBCheckConstraint0 = new DBCheckConstraint(string0, string1, string2);
////		String string3 = dBCheckConstraint0.getConditionText();
////		String string4 = null;
////		String string5 = "S}sGe9RF?";
////		String string6 = dBCheckConstraint0.toString();
////		String string7 = null;
////		String string8 = "";
////		dBCheckConstraint0.setDoc(string8);
////		dBCheckConstraint0.setName(string7);
////		String string9 = "Bfs";
////		DBCheckConstraint dBCheckConstraint1 = new DBCheckConstraint(string4, string5, string9);
////		String[] stringArray0 = dBCheckConstraint1.getColumnNames();
////		boolean boolean0 = dBCheckConstraint1.isEquivalent(dBCheckConstraint0);
////		String string10 = "";
////		dBCheckConstraint0.objectType = string10;
////		String string11 = "";
////		boolean boolean1 = dBCheckConstraint0.isIdentical(dBCheckConstraint1);
////		String string12 = "codePointCount";
////		dBCheckConstraint1.setDoc(string12);
////		dBCheckConstraint0.doc = string11;
////		Object object0 = null;
////		boolean boolean2 = dBCheckConstraint0.equals(object0);
////		String string13 = dBCheckConstraint1.getConditionText();
////		DBTable dBTable0 = dBCheckConstraint1.getTable();
////		String string14 = "}}>a1";
////		dBCheckConstraint1.setName(string14);
////		boolean boolean3 = dBCheckConstraint1.isIdentical(dBCheckConstraint0);
////		String string15 = "0@;E)+`9@KDn*$nWN6";
////		String string16 = "I";
////		dBCheckConstraint1.setDoc(string16);
////		dBCheckConstraint1.setName(string15);
////		boolean boolean4 = dBCheckConstraint1.isIdentical(dBCheckConstraint0);
////		dBCheckConstraint1.owner = (CompositeDBObject<Object>) dBTable0;
////		String string17 = dBCheckConstraint0.getConditionText();
////		boolean boolean5 = dBCheckConstraint1.isIdentical(dBCheckConstraint1);
////		String string18 = dBCheckConstraint1.toString();
////		String string19 = "6L'EX7.Tp>NCi8Z9O7";
////		dBCheckConstraint0.setDoc(string19);
////		boolean boolean6 = dBCheckConstraint0.isIdentical(dBCheckConstraint1);
////		String[] stringArray1 = dBCheckConstraint1.getColumnNames();
////		String string20 = dBCheckConstraint1.toString();
////		String string21 = dBCheckConstraint0.toString();
////		DBTable dBTable1 = dBCheckConstraint1.getTable();
////		DBTable dBTable2 = dBCheckConstraint1.getTable();
////		String string22 = "org.databene.jdbacl.model.DBCheckConstraint";
////		dBCheckConstraint1.objectType = string22;
////		String[] stringArray2 = dBCheckConstraint1.getColumnNames();
////		String string23 = dBCheckConstraint0.getTableName();
////		String string24 = dBCheckConstraint0.getConditionText();
////		String string25 = dBCheckConstraint0.getTableName();
////		String[] stringArray3 = dBCheckConstraint1.getColumnNames();
////		String string26 = "$>@-]JBU<Hx*_r.Tp?";
////		dBCheckConstraint1.setDoc(string26);
////		boolean boolean7 = dBCheckConstraint0.isIdentical(dBTable0);
////		String[] stringArray4 = dBCheckConstraint0.getColumnNames();
////		String string27 = dBCheckConstraint0.getConditionText();
////		String string28 = dBCheckConstraint0.getTableName();
////		String string29 = "KyW ";
////		dBCheckConstraint1.objectType = string29;
////		String string30 = dBCheckConstraint0.toString();
////		boolean boolean8 = dBCheckConstraint0.equals(dBTable0);
////		String string31 = dBCheckConstraint0.getConditionText();
////		boolean boolean9 = dBCheckConstraint1.isIdentical(dBCheckConstraint0);
////		String string32 = "org/databene/jdbacl/model/DBCheckConstraint#isIdentical(Lorg/databene/jdbacl/model/DBObject;)Z";
////		dBCheckConstraint1.setDoc(string32);
////		boolean boolean10 = dBCheckConstraint0.isIdentical(dBCheckConstraint1);
////		boolean boolean11 = dBCheckConstraint1.isIdentical(dBTable0);
////		String string33 = dBCheckConstraint0.getConditionText();
////		String string34 = dBCheckConstraint1.getTableName();
////		String string35 = dBCheckConstraint1.getConditionText();
////		String string36 = dBCheckConstraint1.getTableName();
////		String string37 = dBCheckConstraint0.getConditionText();
////		boolean boolean12 = dBCheckConstraint0.isIdentical(dBTable0);
////		String string38 = dBCheckConstraint0.toString();
////		String string39 = dBCheckConstraint0.getConditionText();
////		String string40 = dBCheckConstraint1.getTableName();
////		boolean boolean13 = dBCheckConstraint0.isIdentical(dBCheckConstraint1);
////		String string41 = dBCheckConstraint0.getTableName();
////		DBTable dBTable3 = dBCheckConstraint1.getTable();
////		String string42 = dBCheckConstraint1.getTableName();
////		String[] stringArray5 = dBCheckConstraint0.getColumnNames();
////		String string43 = dBCheckConstraint0.toString();
////		String string44 = dBCheckConstraint0.getTableName();
////		String string45 = dBCheckConstraint1.toString();
////		String string46 = dBCheckConstraint1.getTableName();
////		dBCheckConstraint1.owner = (CompositeDBObject<Object>) dBTable1;
////		String[] stringArray6 = dBCheckConstraint0.getColumnNames();
////		String[] stringArray7 = Locale.getISOLanguages();
////		DefaultDBTable defaultDBTable0 = new DefaultDBTable();
////		boolean boolean14 = dBCheckConstraint1.isIdentical(defaultDBTable0);
////		String string47 = "";
////		String string48 = "";
////		DBCatalog dBCatalog0 = new DBCatalog(string48);
////		DBSchema dBSchema0 = new DBSchema(string47, dBCatalog0);
////		boolean boolean15 = defaultDBTable0.isIdentical(dBSchema0);
////		String string49 = dBCheckConstraint0.toString();
////		String string50 = dBCheckConstraint0.getConditionText();
////		DBForeignKeyConstraint dBForeignKeyConstraint0 = defaultDBTable0.getForeignKeyConstraint(stringArray5);
////		String[] stringArray8 = dBCheckConstraint0.getColumnNames();
////		String[] stringArray9 = dBCheckConstraint0.getColumnNames();
////		String string51 = dBCheckConstraint0.getConditionText();
////		String string52 = dBCheckConstraint1.getTableName();
////		boolean boolean16 = dBCheckConstraint0.isIdentical(dBTable2);
////		String string53 = dBCheckConstraint1.toString();
////		boolean boolean17 = dBCheckConstraint0.isIdentical(defaultDBTable0);
////		String string54 = dBCheckConstraint1.getConditionText();
////		boolean boolean18 = dBCheckConstraint0.isIdentical(dBCheckConstraint1);
////		String[] stringArray10 = dBCheckConstraint0.getColumnNames();
////		String string55 = dBCheckConstraint0.getConditionText();
////		String[] stringArray11 = dBCheckConstraint1.getColumnNames();
////		boolean boolean19 = dBCheckConstraint0.isIdentical(dBTable1);
////		boolean boolean20 = dBCheckConstraint1.isIdentical(dBTable2);
////		String string56 = dBCheckConstraint1.getConditionText();
////		String string57 = dBCheckConstraint1.getTableName();
////		String string58 = dBCheckConstraint1.toString();
////		String string59 = dBCheckConstraint1.toString();
////		boolean boolean21 = dBCheckConstraint1.isIdentical(dBTable3);
////		boolean boolean22 = dBCheckConstraint1.isEquivalent(dBCheckConstraint1);
////		String string60 = dBCheckConstraint1.getTableName();
////		String string61 = dBCheckConstraint1.toString();
////		String[] stringArray12 = dBCheckConstraint1.getColumnNames();
////		String string62 = dBCheckConstraint0.getTableName();
////		String string63 = dBCheckConstraint0.getTableName();
////		String string64 = dBCheckConstraint1.getTableName();
////		String string65 = dBCheckConstraint0.toString();
////		boolean boolean23 = dBCheckConstraint0.isEquivalent(dBCheckConstraint1);
////	}
//	
//	/*
//	 * Non-visible method call.
//	 */
////	@Test
////	public void omjstate_Transition_equals_1() {
////		// I17 Branch 3 IFLE L47;true
////		String string0 = "[YJR 1_!8,";
////		String string1 = "0+";
////		int int0 = 2193;
////		Vector<Integer> vector0 = new Vector<Integer>(int0);
////		Object object0 = new Object();
////		Event event0 = new Event(string1, vector0, object0);
////		EndState endState0 = EndState.getSingleton();
////		StartState startState0 = new StartState();
////		Transition transition0 = new Transition(string0, event0, endState0, startState0);
////		String string2 = null;
////		Transition transition1 = new Transition(string2, event0, startState0, startState0);
////		transition0.evaluateGuardConditions();
////		boolean boolean0 = transition1.willTrigger(startState0, event0);
////		String string3 = "Uz40 ";
////		String string4 = "com.objectmentors.state.Transition";
////		Event event1 = new Event(string3, vector0, string4);
////		boolean boolean1 = event0.equals(event1);
////		String string5 = "";
////		StringMatchesGuardCondition stringMatchesGuardCondition0 = new StringMatchesGuardCondition(string5);
////		String string6 = stringMatchesGuardCondition0.toString();
////		transition1.addGuardCondition((IGuardCondition) stringMatchesGuardCondition0);
////		String string7 = "";
////		StringMatchesGuardCondition stringMatchesGuardCondition1 = new StringMatchesGuardCondition(string7);
////		String string8 = stringMatchesGuardCondition1.toString();
////		transition0.addGuardCondition((IGuardCondition) stringMatchesGuardCondition1);
////		StartState startState1 = (StartState)transition0.getOutcomeState();
////		transition1.addGuardCondition((IGuardCondition) stringMatchesGuardCondition1);
////		StartState startState2 = StartState.getSingleton();
////		boolean boolean2 = event0.equals(event1);
////		String string9 = null;
////		String string10 = "com.objectmentors.state.EndState";
////		boolean boolean3 = event1.equals(event0);
////		boolean boolean4 = transition0.equals(transition0);
////		StartState startState3 = new StartState();
////		StartState startState4 = (StartState)transition0.trigger(startState3, event0);
////		transition0.addGuardCondition((IGuardCondition) stringMatchesGuardCondition1);
////		StringMatchesGuardCondition stringMatchesGuardCondition2 = new StringMatchesGuardCondition(string10);
////		Event event2 = new Event(string9, vector0, stringMatchesGuardCondition2);
////		boolean boolean5 = transition0.equals(transition1);
////		boolean boolean6 = transition0.willTrigger(startState1, event2);
////		boolean boolean7 = event0.equals(event1);
////		Event event3 = transition1.getTriggerEvent();
////		transition0.addGuardCondition((IPredicate) stringMatchesGuardCondition1);
////		boolean boolean8 = event3.equals(event0);
////		transition0.addGuardCondition((IGuardCondition) stringMatchesGuardCondition1);
////		boolean boolean9 = event3.equals(event2);
////		transition1.addGuardCondition((IPredicate) stringMatchesGuardCondition2);
////		boolean boolean10 = transition1.equals(transition1);
////		StartState startState5 = (StartState)transition0.trigger(startState0, event3);
////		transition0.evaluateGuardConditions();
////		Event event4 = transition0.getTriggerEvent();
////		boolean boolean11 = transition1.willTrigger(startState1, event2);
////		Event event5 = transition1.getTriggerEvent();
////		int int1 = 1010;
////		IntegerGreaterThanGuardCondition integerGreaterThanGuardCondition0 = new IntegerGreaterThanGuardCondition(int1);
////		Object object1 = new Object();
////		Object object2 = new Object();
////		boolean boolean12 = integerGreaterThanGuardCondition0.evaluate(object2);
////		boolean boolean13 = integerGreaterThanGuardCondition0.evaluate(object1);
////		boolean boolean14 = transition1.equals(transition1);
////		transition1.addGuardCondition((IGuardCondition) integerGreaterThanGuardCondition0);
////		EndState endState1 = (EndState)transition0.getInitialState();
////		EndState endState2 = (EndState)transition0.getInitialState();
////		EndState endState3 = EndState.getSingleton();
////		transition0.addGuardCondition((IPredicate) integerGreaterThanGuardCondition0);
////		boolean boolean15 = event2.equals(event0);
////		String string11 = transition1.toString();
////		IState iState0 = transition1.getInitialState();
////		Event event6 = transition0.getTriggerEvent();
////		boolean boolean16 = transition1.willTrigger(startState4, event2);
////		boolean boolean17 = event0.equals(event5);
////		IState iState1 = transition1.getInitialState();
////		String string12 = "com/objectmentors/state/Event#equals(Lcom/objectmentors/state/Event;)Z";
////		String string13 = "&";
////		Object object3 = new Object();
////		Event event7 = new Event(string13, vector0, object3);
////		boolean boolean18 = event6.equals(event7);
////		int int2 = (-661);
////		IntegerGreaterThanGuardCondition integerGreaterThanGuardCondition1 = new IntegerGreaterThanGuardCondition(int2);
////		Event event8 = new Event(string12, vector0, integerGreaterThanGuardCondition1);
////		boolean boolean19 = transition0.equals(transition1);
////		boolean boolean20 = transition1.willTrigger(endState3, event6);
////		boolean boolean21 = transition1.equals(transition1);
////		transition1.evaluateGuardConditions();
////		Event event9 = null;
////		IState iState2 = transition1.trigger(startState2, event9);
////		transition1.addGuardCondition((IGuardCondition) stringMatchesGuardCondition2);
////		StartState startState6 = new StartState();
////		String string14 = transition0.toString();
////		StartState startState7 = new StartState();
////		IState iState3 = transition0.getOutcomeState();
////		IState iState4 = transition0.trigger(endState1, event4);
////		IState iState5 = transition1.getOutcomeState();
////		Event event10 = transition0.getTriggerEvent();
////		transition1.addGuardCondition((IGuardCondition) integerGreaterThanGuardCondition0);
////		Event event11 = transition1.getTriggerEvent();
////		IState iState6 = transition0.getOutcomeState();
////		IState iState7 = transition0.trigger(startState7, event3);
////		String string15 = "/{gu;0h3.pUckJbcu*W";
////		StringMatchesGuardCondition stringMatchesGuardCondition3 = new StringMatchesGuardCondition(string15);
////		transition1.addGuardCondition((IPredicate) stringMatchesGuardCondition3);
////		IState iState8 = transition0.getOutcomeState();
////		IState iState9 = transition1.getInitialState();
////		String string16 = "FZGZ h@}fn^`qYArDU";
////		int int3 = 97;
////		int int4 = (-17);
////		Vector<Object> vector1 = new Vector<Object>(int3, int4);
////		String string17 = "com.objectmentors.state.Event";
////		StringMatchesGuardCondition stringMatchesGuardCondition4 = new StringMatchesGuardCondition(string17);
////		Event event12 = new Event(string16, vector1, stringMatchesGuardCondition4);
////		boolean boolean22 = event12.equals(event4);
////		IState iState10 = transition0.trigger(iState8, event12);
////		Event event13 = transition0.getTriggerEvent();
////		IState iState11 = transition0.trigger(endState3, event6);
////		IState iState12 = transition1.getInitialState();
////		EndState endState4 = new EndState();
////		String string18 = null;
////		String string19 = "";
////		StringMatchesGuardCondition stringMatchesGuardCondition5 = new StringMatchesGuardCondition(string19);
////		Event event14 = new Event(string18, vector1, stringMatchesGuardCondition5);
////		boolean boolean23 = transition0.willTrigger(endState4, event14);
////		boolean boolean24 = transition0.equals(transition1);
////	}
//	
//	/*
//	 * Null parameter passed to MockFile constructor.
//	 */
//	@Test
//	public void beanbin_GetImplementationsFromJar_getImplementations_1() throws BeanBinException { 
//		// I4 Branch 1 IFEQ L71;true
//		Class<Integer> class0 = Integer.class;
//		URI uRI0 = null;
//		MockFile mockFile0 = new MockFile(uRI0);
//		GetImplementationsFromJar getImplementationsFromJar0 = new GetImplementationsFromJar(class0, mockFile0);
//		List<Class> list0 = (List<Class>) getImplementationsFromJar0.getImplementations();
//	}
//	
//	/*
//	 * Incorrectly classed as out-method.
//	 */
////	@Test
////	public void tullibee_ExecutionFilter_equals_1() { 
////		// I38 Branch 5 IF_ICMPNE L75;true
////		ExecutionFilter executionFilter0 = new ExecutionFilter();
////		int int0 = (-79);
////		String string0 = "14_";
////		String string1 = "";
////		String string2 = "}";
////		String string3 = "ZKTh?nY>|)Z3c!Ho?";
////		executionFilter0.m_acctCode = string3;
////		String string4 = "";
////		executionFilter0.m_acctCode = string4;
////		String string5 = "={JQfvu`fc>PPjg\"qE";
////		String string6 = "";
////		executionFilter0.m_exchange = string6;
////		String string7 = "f(d%S8";
////		String string8 = "u_),4";
////		ExecutionFilter executionFilter1 = new ExecutionFilter(int0, string0, string1, string2, string5, string7, string8);
////		int int1 = 290;
////		String string9 = null;
////		String string10 = "pR?_-+sVKdBupMPb";
////		String string11 = "com/ib/client/ExecutionFilter#equals(Ljava/lang/Object;)Z";
////		int int2 = 0;
////		executionFilter1.m_clientId = int2;
////		String string12 = "1~^sq2^j%`";
////		executionFilter0.m_secType = string12;
////		String string13 = "Z|uwnfQE";
////		String string14 = "f!:uG>MV;GRnB}`";
////		String string15 = "";
////		ExecutionFilter executionFilter2 = new ExecutionFilter(int1, string9, string10, string11, string13, string14, string15);
////		String string16 = "com/ib/client/ExecutionFilter#equals(Ljava/lang/Object;)Z";
////		executionFilter2.m_time = string16;
////		String string17 = "";
////		executionFilter2.m_secType = string17;
////		String string18 = "l0IvOyTIgA*kE0DsIT";
////		executionFilter2.m_side = string18;
////		int int3 = 0;
////		String string19 = "";
////		String string20 = "&M";
////		String string21 = "";
////		String string22 = "zH-u%T6=5@Hd\"IUa";
////		String string23 = "N=h";
////		String string24 = "";
////		ExecutionFilter executionFilter3 = new ExecutionFilter(int3, string19, string20, string21, string22, string23, string24);
////		String string25 = "3G(A:b";
////		String string26 = "bh*Bdiqf\"Qddw0#^IX$";
////		executionFilter0.m_exchange = string26;
////		String string27 = "omFra.{AU!82/~";
////		executionFilter0.m_acctCode = string27;
////		String string28 = "";
////		executionFilter1.m_acctCode = string28;
////		executionFilter3.m_symbol = string25;
////		ExecutionFilter executionFilter4 = new ExecutionFilter();
////		int int4 = 3;
////		String string29 = null;
////		String string30 = "";
////		String string31 = "com.ib.client.ExecutionFilter";
////		String string32 = "";
////		String string33 = "";
////		executionFilter0.m_acctCode = string33;
////		String string34 = "D#OB";
////		String string35 = "";
////		ExecutionFilter executionFilter5 = new ExecutionFilter(int4, string29, string30, string31, string32, string34, string35);
////		String string36 = "l>'p0\\\\<<!";
////		executionFilter5.m_exchange = string36;
////		ExecutionFilter executionFilter6 = new ExecutionFilter();
////		int int5 = (-3016);
////		executionFilter6.m_clientId = int5;
////		int int6 = 67;
////		String string37 = "";
////		String string38 = "";
////		executionFilter1.m_time = string38;
////		String string39 = "F3KIA Ph%u#.<-p8a";
////		String string40 = "HF}keWKY)irp58}";
////		String string41 = "";
////		String string42 = "As|Q`b,;W";
////		String string43 = "";
////		ExecutionFilter executionFilter7 = new ExecutionFilter(int6, string37, string39, string40, string41, string42, string43);
////		ExecutionFilter executionFilter8 = new ExecutionFilter();
////		String string44 = "%>\\\\2zh{/V/A]-";
////		executionFilter8.m_exchange = string44;
////		ExecutionFilter executionFilter9 = new ExecutionFilter();
////		ExecutionFilter executionFilter10 = new ExecutionFilter();
////		ExecutionFilter executionFilter11 = new ExecutionFilter();
////		String string45 = "";
////		executionFilter11.m_acctCode = string45;
////		int int7 = 73;
////		executionFilter11.m_clientId = int7;
////		ExecutionFilter executionFilter12 = new ExecutionFilter();
////		ExecutionFilter executionFilter13 = new ExecutionFilter();
////		String string46 = "4/i?e^Y";
////		executionFilter13.m_acctCode = string46;
////		int int8 = (-2857);
////		String string47 = "8Af]`HzJ";
////		String string48 = "9]O";
////		String string49 = "]V@%n(^>;Y ]k`";
////		String string50 = null;
////		String string51 = "";
////		String string52 = "";
////		ExecutionFilter executionFilter14 = new ExecutionFilter(int8, string47, string48, string49, string50, string51, string52);
////		int int9 = 0;
////		String string53 = "";
////		String string54 = "";
////		String string55 = "";
////		String string56 = "";
////		String string57 = "3/po=uhKn~X=hQ)2$";
////		String string58 = "{}yvZ+C@Ls";
////		ExecutionFilter executionFilter15 = new ExecutionFilter(int9, string53, string54, string55, string56, string57, string58);
////		int int10 = (-989);
////		String string59 = "";
////		String string60 = "";
////		String string61 = "`1]<4\\\\<>H_hQQdYNYz";
////		String string62 = "";
////		String string63 = null;
////		String string64 = "";
////		ExecutionFilter executionFilter16 = new ExecutionFilter(int10, string59, string60, string61, string62, string63, string64);
////		ExecutionFilter executionFilter17 = new ExecutionFilter();
////		int int11 = (-1399);
////		String string65 = "com.ib.client.ExecutionFilter";
////		String string66 = "*n_]lsdEWb[7hk|q";
////		String string67 = "";
////		String string68 = "Hea";
////		String string69 = "";
////		String string70 = "com/ib/client/ExecutionFilter#equals(Ljava/lang/Object;)Z";
////		ExecutionFilter executionFilter18 = new ExecutionFilter(int11, string65, string66, string67, string68, string69, string70);
////		boolean boolean0 = executionFilter0.equals(executionFilter1);
////	}
//	
//	/*
//	 * Non-visible type.
//	 */
////	@Test
////	public void jmca_ClassOrInterfaceBody_processState_1() {
////		// I11 Branch 1 IF_ICMPGE L52;true
////		SaxProcessor saxProcessor0 = new SaxProcessor();
////		ClassOrInterfaceBody classOrInterfaceBody0 = new ClassOrInterfaceBody(saxProcessor0);
////		classOrInterfaceBody0.parent = (XMLParser) saxProcessor0;
////		String string0 = "M);%";
////		classOrInterfaceBody0.closeState(string0);
////		String string1 = "X- 6v`j,-";
////		String string2 = "v{h";
////		saxProcessor0.processingInstruction(string1, string2);
////		String string3 = "\\";
////		String string4 = "nd_{7xN>R29Rj|";
////		classOrInterfaceBody0.closeState(string4);
////		String string5 = null;
////		classOrInterfaceBody0.closeState(string5);
////		classOrInterfaceBody0.processState(string3);
////	}
//	
//	/*
//	 * Non-visible type, members
//	 */
////	@Test
////	public void jmca_Statement_processState_1() {
////		// I3 Branch 4 IFEQ L108;false
////		SaxProcessor saxProcessor0 = new SaxProcessor();
////		Stack<State> stack0 = saxProcessor0.returnState;
////		saxProcessor0.returnState = stack0;
////		Statement statement0 = new Statement(saxProcessor0);
////		String string0 = "ForStatement";
////		statement0.closeState(string0);
////		statement0.processState(string0);
////		SaxProcessor saxProcessor1 = new SaxProcessor();
////		Statement statement1 = new Statement(saxProcessor1);
////		statement1.parent = (XMLParser) saxProcessor1;
////		AttributesImpl attributesImpl0 = new AttributesImpl();
////		Attributes2Impl attributes2Impl0 = new Attributes2Impl();
////		boolean boolean0 = FileSystemHandling.shouldAllThrowIOExceptions();		
////	}
//	
//	/*
//	 * Null parameter passed to MockFile constructor.
//	 */
//	@Test
//	public void jwbf_Article_getEditSummary_1() {
//		// I41 Branch 6 IFNONNULL L76;false
//		String string0 = "jm0D{+HRk";
//		String string1 = null;
//		MockFile mockFile0 = new MockFile(string0, string1);
//		ZimWikiBot zimWikiBot0 = new ZimWikiBot(mockFile0);
//		File file0 = null;
//		SimpleFile simpleFile0 = new SimpleFile(file0);
//		Article article0 = new Article(zimWikiBot0, simpleFile0);
//		String string2 = article0.getEditSummary();
//	}
//	
//	/*
//	 * Null parameter passed into Range#endOfRow
//	 * Does this count as a out-method case?
//	 */
//	@Test
//	public void corina_Range_endOfRow_1() { 
//		// I21 Branch 13 IFLE L340;true
//		Range range0 = new Range();
//		Year year0 = null;
//		boolean boolean0 = range0.endOfRow(year0);
//		Year year1 = new Year();
//		boolean boolean1 = range0.startOfRow(year1);
//		Year year2 = year1.cropToCentury();
//		int int0 = range0.hashCode();
//		int int1 = year2.intValue();
//		int int2 = 1;
//		int int3 = 0;
//		Year year3 = new Year(int2, int3);
//		int int4 = year1.column();
//		int int5 = year3.column();
//		Year year4 = year1.nextCentury();
//		Range range1 = new Range(year1, year3);
//		String string0 = year1.toString();
//		Year year5 = year3.cropToCentury();
//		int int6 = year3.column();
//		int int7 = year3.intValue();
//		int int8 = range1.overlap(range1);
//		Range range2 = range0.intersection(range1);
//		int int9 = range1.rows();
//		String string1 = range0.toString();
//		Year year6 = year3.cropToCentury();
//		int int10 = year3.intValue();
//		Object object0 = new Object();
//		int int11 = 86;
//		Year year7 = year3.add(int11);
//		int int12 = 0;
//		Year year8 = year3.add(int12);
//		int int13 = range2.rows();
//		boolean boolean2 = range0.equals(object0);
//		int int14 = year3.intValue();
//		boolean boolean3 = range0.contains(year3);
//		Year year9 = Year.min(year8, year5);
//		int int15 = 0;
//		Year year10 = year9.cropToCentury();
//		Year year11 = year8.add(int15);
//		int int16 = range1.span();
//		int int17 = range0.rows();
//		Range range3 = new Range();
//		Range range4 = range3.union(range3);
//		Range range5 = new Range();
//		Object object1 = new Object();
//		int int18 = 28;
//		Range range6 = range3.redateBy(int18);
//		boolean boolean4 = range0.equals(object1);
//		Range range7 = range1.intersection(range0);
//		int int19 = range7.hashCode();
//		boolean boolean5 = range0.contains(range1);
//		String string2 = range5.toStringWithSpan();
//		boolean boolean6 = range1.contains(year0);
//		boolean boolean7 = range7.contains(range5);
//		int int20 = year3.column();
//		int int21 = range7.hashCode();
//		int int22 = year5.column();
//		Year year12 = range0.getEnd();
//		boolean boolean8 = range6.endOfRow(year3);
//		int int23 = (-1);
//		Range range8 = range0.redateBy(int23);
//		boolean boolean9 = range0.contains(year10);
//		String string3 = range0.toStringWithSpan();
//		int int24 = range3.hashCode();
//		Range range9 = new Range(string2);
//		Range range10 = new Range();
//		String string4 = "";
//		Range range11 = new Range(string4);
//		int int25 = range5.overlap(range11);
//		int int26 = range6.hashCode();
//		Year year13 = range10.getStart();
//		Range range12 = range7.redateStartTo(year5);
//		Range range13 = range0.redateEndTo(year1);
//		boolean boolean10 = range9.contains(range3);
//		int int27 = range3.hashCode();
//		Range range14 = new Range(year1, int10);
//		boolean boolean11 = range2.contains(range2);
//		boolean boolean12 = range7.contains(range1);
//		Range range15 = range12.redateEndTo(year4);
//		boolean boolean13 = range0.endOfRow(year1);
//	}
//	
//	/*
//	 * Can't find class GVTAttributedCharacterIterator
//	 */
////	@Test
////	public void corina_DecedalModel_setValueAt_1() {
////		// I7 Branch 28 IFNE L191;false
////		Sample sample0 = new Sample();
////		Boolean boolean0 = GVTAttributedCharacterIterator.TextAttribute.OVERLINE_ON;
////		DecadalModel decadalModel0 = new DecadalModel();
////		Integer integer0 = JLayeredPane.POPUP_LAYER;
////		int int0 = 0;
////		String string0 = decadalModel0.getColumnName(sample0.MR);
////		int int1 = 1;
////		decadalModel0.setValueAt(string0, int0, int1);
////	}
//	
//	/*
//	 * Null parameter passed into ZoomInTool#mouseDragged
//	 * Is this counted as a out-method case?
//	 */
//	@Test
//	public void corina_ZoomInTool_mouseDragged_1() {
//		// I51 Branch 2 IFLE L154;false
//		MapPanel mapPanel0 = null;
//		View view0 = new View();
//		ToolBox toolBox0 = null;
//		ZoomInTool zoomInTool0 = new ZoomInTool(mapPanel0, view0, toolBox0);
//		MouseEvent mouseEvent0 = null;
//		zoomInTool0.mouseDragged(mouseEvent0);
//		Boolean boolean0 = Boolean.TRUE;
//	}
//	
//	/*
//	 * Can't resolve a class.
//	 */
////	@Test
////	public void lilith_AccessEventProtobufEncoder_convertStringMap_1() {
////		// I338 Branch 25 IFEQ L187;false
////		HashMap<String, String> hashMap0 = new HashMap<String, String>();
////		boolean boolean0 = true;
////		AccessEventProtobufEncoder accessEventProtobufEncoder0 = new AccessEventProtobufEncoder(boolean0);
////		hashMap0.clear();
////		Object object0 = new Object();
////		String string0 = hashMap0.remove(object0);
////		AccessEvent accessEvent0 = new AccessEvent();
////		accessEvent0.setResponseHeaders(hashMap0);
////		String string1 = "com.google.protobuf.GeneratedMessage";
////		String string2 = "";
////		accessEvent0.setRequestURI(string2);
////		String string3 = null;
////		String string4 = hashMap0.putIfAbsent(string1, string3);
////		byte[] byteArray0 = accessEventProtobufEncoder0.encode(accessEvent0);
////		AccessEvent accessEvent1 = null;
////		int int0 = 433;
////		accessEvent0.setStatusCode(int0);
////		byte[] byteArray1 = accessEventProtobufEncoder0.encode(accessEvent1);
////		LoggerContext loggerContext0 = new LoggerContext();
////		String string5 = loggerContext0.toString();
////		String string6 = loggerContext0.toString();
////		loggerContext0.setProperties(hashMap0);
////		accessEvent0.setRequestHeaders(hashMap0);
////		AccessProto.LoggerContext accessProto_LoggerContext0 = AccessEventProtobufEncoder.convert(loggerContext0);
////		boolean boolean1 = accessEventProtobufEncoder0.isCompressing();
////		byte[] byteArray2 = accessEventProtobufEncoder0.encode(accessEvent0);
////		AccessProto.LoggerContext accessProto_LoggerContext1 = AccessEventProtobufEncoder.convert(loggerContext0);
////		AccessEvent accessEvent2 = new AccessEvent();
////		AccessProto.AccessEvent accessProto_AccessEvent0 = AccessEventProtobufEncoder.convert(accessEvent2);
////		boolean boolean2 = true;
////		Object object1 = null;
////		boolean boolean3 = loggerContext0.equals(object1);
////		accessEventProtobufEncoder0.setCompressing(boolean2);
////		AccessProto.AccessEvent accessProto_AccessEvent1 = AccessEventProtobufEncoder.convert(accessEvent0);
////		int int1 = 4325;
////		float float0 = 0.86739725F;
////		loggerContext0.setProperties(hashMap0);
////		HashMap<String, String[]> hashMap1 = new HashMap<String, String[]>(int1, float0);
////		String string7 = "";
////		String string8 = null;
////		BiFunction<Object, String[], String[]> biFunction0 = null;
////		String[] stringArray0 = hashMap1.compute(string8, biFunction0);
////		String[] stringArray1 = new String[5];
////		String string9 = "]>vc^f0Mf^#^/+;B7";
////		String[] stringArray2 = hashMap1.computeIfPresent(string9, biFunction0);
////		int int2 = 2708;
////		HashMap<String, String[]> hashMap2 = new HashMap<String, String[]>(int2);
////		hashMap1.putAll(hashMap2);
////		stringArray1[0] = string4;
////		stringArray1[1] = string1;
////		stringArray1[2] = string1;
////		stringArray1[3] = string5;
////		stringArray1[4] = string1;
////		BiFunction<Object, Object, String[]> biFunction1 = null;
////		String[] stringArray3 = hashMap1.merge(string7, stringArray1, biFunction1);
////		AccessProto.StringArrayMap accessProto_StringArrayMap0 = AccessEventProtobufEncoder.convertStringArrayMap(hashMap1);
////		AccessProto.LoggerContext accessProto_LoggerContext2 = AccessEventProtobufEncoder.convert(loggerContext0);
////		AccessProto.AccessEvent accessProto_AccessEvent2 = AccessEventProtobufEncoder.convert(accessEvent0);
////		HashMap<String, String[]> hashMap3 = new HashMap<String, String[]>(accessProto_AccessEvent0.REMOTE_USER_FIELD_NUMBER, accessProto_AccessEvent2.RESPONSE_HEADERS_FIELD_NUMBER);
////		Set<Map.Entry<String, String[]>> set0 = (Set<Map.Entry<String, String[]>>)hashMap3.entrySet();
////		Object object2 = hashMap3.clone();
////		AccessProto.StringArrayMap accessProto_StringArrayMap1 = AccessEventProtobufEncoder.convertStringArrayMap(hashMap3);
////		AccessEvent accessEvent3 = new AccessEvent();
////		String string10 = "";
////		accessEvent3.setRemoteHost(string10);
////		AccessProto.AccessEvent accessProto_AccessEvent3 = AccessEventProtobufEncoder.convert(accessEvent3);
////		AccessProto.LoggerContext accessProto_LoggerContext3 = AccessEventProtobufEncoder.convert(loggerContext0);
////		byte[] byteArray3 = accessEventProtobufEncoder0.encode(accessEvent0);
////		boolean boolean4 = accessEventProtobufEncoder0.isCompressing();
////		String string11 = "(^('0}Q'yx&9*SdZ4";
////		accessEvent0.setMethod(string11);
////		String string12 = "com.google.protobuf.DescriptorProtos$FieldOptions$Builder";
////		accessEvent0.setProtocol(string12);
////		boolean boolean5 = accessEventProtobufEncoder0.isCompressing();
////		byte[] byteArray4 = accessEventProtobufEncoder0.encode(accessEvent1);
////		AccessProto.LoggerContext accessProto_LoggerContext4 = AccessEventProtobufEncoder.convert(loggerContext0);
////		boolean boolean6 = accessEventProtobufEncoder0.isCompressing();
////		boolean boolean7 = accessEventProtobufEncoder0.isCompressing();
////		String string13 = "LStmbs";
////		accessEvent3.setProtocol(string13);
////		AccessProto.AccessEvent accessProto_AccessEvent4 = AccessEventProtobufEncoder.convert(accessEvent2);
////		boolean boolean8 = accessEventProtobufEncoder0.isCompressing();
////		AccessEvent accessEvent4 = new AccessEvent();
////		Collection<String> collection0 = hashMap0.values();
////		String string14 = "6uwI^i'@Taf";
////		accessEvent4.setProtocol(string14);
////		byte[] byteArray5 = accessEventProtobufEncoder0.encode(accessEvent4);
////		int int3 = 0;
////		accessEvent3.setLocalPort(int3);
////		byte[] byteArray6 = accessEventProtobufEncoder0.encode(accessEvent3);
////		AccessProto.LoggerContext accessProto_LoggerContext5 = AccessEventProtobufEncoder.convert(loggerContext0);
////		hashMap1.clear();
////		boolean boolean9 = accessEventProtobufEncoder0.isCompressing();
////		LoggerContext loggerContext1 = new LoggerContext();
////		long long0 = 1L;
////		String string15 = loggerContext1.toString();
////		Long long1 = Long.valueOf(long0);
////		long long2 = 1596L;
////		long long3 = (-2909L);
////		long long4 = 1248L;
////		long long5 = (-370L);
////		long long6 = Long.min(long4, long5);
////		long long7 = Long.remainderUnsigned(long2, long3);
////		long long8 = (-7446L);
////		long long9 = (-3044L);
////		int int4 = Long.compare(long8, long9);
////		String string16 = loggerContext1.toString();
////		loggerContext1.setBirthTime(long1);
////		AccessProto.LoggerContext accessProto_LoggerContext6 = AccessEventProtobufEncoder.convert(loggerContext1);
////		boolean boolean10 = false;
////		accessEventProtobufEncoder0.setCompressing(boolean10);
////		AccessProto.StringArrayMap accessProto_StringArrayMap2 = AccessEventProtobufEncoder.convertStringArrayMap(hashMap3);
////		boolean boolean11 = accessEventProtobufEncoder0.isCompressing();
////		AccessProto.AccessEvent accessProto_AccessEvent5 = AccessEventProtobufEncoder.convert(accessEvent0);
////		byte[] byteArray7 = accessEventProtobufEncoder0.encode(accessEvent3);
////		AccessProto.AccessEvent accessProto_AccessEvent6 = AccessEventProtobufEncoder.convert(accessEvent4);
////		AccessProto.AccessEvent accessProto_AccessEvent7 = AccessEventProtobufEncoder.convert(accessEvent0);
////		boolean boolean12 = true;
////		accessEventProtobufEncoder0.setCompressing(boolean12);
////		AccessProto.StringArrayMap accessProto_StringArrayMap3 = AccessEventProtobufEncoder.convertStringArrayMap(hashMap3);
////		byte[] byteArray8 = accessEventProtobufEncoder0.encode(accessEvent3);
////		AccessEvent accessEvent5 = new AccessEvent();
////		String string17 = "de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto$NestedDiagnosticContext$Builder";
////		String string18 = "";
////		accessEvent5.setRequestURI(string18);
////		accessEvent5.setServerName(string17);
////		accessEvent5.setResponseHeaders(hashMap0);
////		byte[] byteArray9 = accessEventProtobufEncoder0.encode(accessEvent5);
////		LoggerContext loggerContext2 = new LoggerContext();
////		String string19 = "de.huxhorn.lilith.data.logging.protobuf.generated.LoggingProto$NestedDiagnosticContext$Builder";
////		int int5 = 2209;
////		Long long10 = Long.valueOf(string19, int5);
////		loggerContext2.setBirthTime(long10);
////		AccessProto.LoggerContext accessProto_LoggerContext7 = AccessEventProtobufEncoder.convert(loggerContext2);
////		AccessProto.StringArrayMap accessProto_StringArrayMap4 = AccessEventProtobufEncoder.convertStringArrayMap(hashMap1);
////		boolean boolean13 = accessEventProtobufEncoder0.isCompressing();
////		AccessProto.LoggerContext accessProto_LoggerContext8 = AccessEventProtobufEncoder.convert(loggerContext0);
////		AccessProto.AccessEvent accessProto_AccessEvent8 = AccessEventProtobufEncoder.convert(accessEvent1);
////		AccessProto.AccessEvent accessProto_AccessEvent9 = AccessEventProtobufEncoder.convert(accessEvent5);
////		boolean boolean14 = accessEventProtobufEncoder0.isCompressing();
////		AccessProto.AccessEvent accessProto_AccessEvent10 = AccessEventProtobufEncoder.convert(accessEvent1);
////		byte[] byteArray10 = accessEventProtobufEncoder0.encode(accessEvent1);
////		AccessProto.LoggerContext accessProto_LoggerContext9 = AccessEventProtobufEncoder.convert(loggerContext2);
////		AccessProto.AccessEvent accessProto_AccessEvent11 = AccessEventProtobufEncoder.convert(accessEvent3);
////		AccessProto.AccessEvent accessProto_AccessEvent12 = AccessEventProtobufEncoder.convert(accessEvent5);
////		byte[] byteArray11 = accessEventProtobufEncoder0.encode(accessEvent5);
////		AccessProto.LoggerContext accessProto_LoggerContext10 = AccessEventProtobufEncoder.convert(loggerContext1);
////		AccessProto.LoggerContext accessProto_LoggerContext11 = AccessEventProtobufEncoder.convert(loggerContext2);
////		byte[] byteArray12 = accessEventProtobufEncoder0.encode(accessEvent4);
////		AccessProto.LoggerContext accessProto_LoggerContext12 = AccessEventProtobufEncoder.convert(loggerContext1);
////		AccessProto.StringArrayMap accessProto_StringArrayMap5 = AccessEventProtobufEncoder.convertStringArrayMap(hashMap3);
////		boolean boolean15 = accessEventProtobufEncoder0.isCompressing();
////		byte[] byteArray13 = accessEventProtobufEncoder0.encode(accessEvent5);
////		boolean boolean16 = false;
////		accessEventProtobufEncoder0.setCompressing(boolean16);
////		boolean boolean17 = false;
////		accessEventProtobufEncoder0.setCompressing(boolean17);
////		boolean boolean18 = true;
////		accessEventProtobufEncoder0.setCompressing(boolean18);
////		AccessProto.StringMap accessProto_StringMap0 = AccessEventProtobufEncoder.convertStringMap(hashMap0);
////	}
//	
//	/*
//	 * Cannot resolve XMLStreamRecorder
//	 */
////	@Test
////	public void lilith_StackTraceElementWriter_write_1() {
////		// I12 Branch 2 IFNULL L68;true
////		StackTraceElementWriter stackTraceElementWriter0 = new StackTraceElementWriter();
////		StringWriter stringWriter0 = new StringWriter();
////		boolean boolean0 = false;
////		MockPrintWriter mockPrintWriter0 = new MockPrintWriter(stringWriter0, boolean0);
////		XMLStreamRecorder xMLStreamRecorder0 = new XMLStreamRecorder(mockPrintWriter0);
////		mockPrintWriter0.close();
////		ExtendedStackTraceElement extendedStackTraceElement0 = new ExtendedStackTraceElement();
////		float float0 = (-1.0F);
////		mockPrintWriter0.println(float0);
////		boolean boolean1 = true;
////		String string0 = stackTraceElementWriter0.getPreferredPrefix();
////		stackTraceElementWriter0.write((XMLStreamWriter) xMLStreamRecorder0, extendedStackTraceElement0, boolean1);
////	}
//	
//	/*
//	 * HtmlTransferable is not visible.
//	 */
////	@Test
////	public void lilith_HtmlTransferable_getTransferData_1() {
////		// I21 Branch 3 IFEQ L70;true
////		String string0 = "-sIh?[eq;$D!V]";
////		HtmlTransferable htmlTransferable0 = new HtmlTransferable(string0);
////		Class<String> class0 = String.class;
////		DataFlavor[] dataFlavorArray0 = htmlTransferable0.getTransferDataFlavors();
////		DataFlavor[] dataFlavorArray1 = htmlTransferable0.getTransferDataFlavors();
////		String string1 = "J<sdu\\u0080ds3<_=bMS=Ck";
////		DataFlavor dataFlavor0 = new DataFlavor(class0, string1);
////		DataFlavor dataFlavor1 = (DataFlavor)htmlTransferable0.XHTML_FLAVOR.clone();
////		String string2 = htmlTransferable0.getHtml();
////		InputStreamReader inputStreamReader0 = (InputStreamReader)htmlTransferable0.PLAIN_TEXT_FLAVOR.getReaderForText(htmlTransferable0);
////		boolean boolean0 = htmlTransferable0.isDataFlavorSupported(dataFlavor0);
////		boolean boolean1 = htmlTransferable0.isDataFlavorSupported(dataFlavor0);
////		String string3 = null;
////		String string4 = "de.huxhorn.lilith.swing.HtmlTransferable";
////		DataFlavor.allHtmlFlavor = htmlTransferable0.XHTML_FLAVOR;
////		DataFlavor dataFlavor2 = new DataFlavor(string3, string4);
////		String string5 = dataFlavor0.getDefaultRepresentationClassAsString();
////		String string6 = "";
////		dataFlavor2.setHumanPresentableName(string6);
////		DataFlavor[] dataFlavorArray2 = new DataFlavor[0];
////		DataFlavor dataFlavor3 = DataFlavor.selectBestTextFlavor(dataFlavorArray2);
////		boolean boolean2 = htmlTransferable0.isDataFlavorSupported(dataFlavor2);
////		String string7 = htmlTransferable0.getHtml();
////		boolean boolean3 = htmlTransferable0.isDataFlavorSupported(dataFlavor2);
////		String string8 = "org.apache.commons.io.filefilter.WildcardFileFilter";
////		int int0 = 0;
////		PipedInputStream pipedInputStream0 = new PipedInputStream(int0);
////		ObjectInputStream objectInputStream0 = new ObjectInputStream(pipedInputStream0);
////		String string9 = objectInputStream0.readUTF();
////		dataFlavor3.readExternal(objectInputStream0);
////		boolean boolean4 = dataFlavor3.isFlavorSerializedObjectType();
////		boolean boolean5 = dataFlavor3.isMimeTypeEqual(string8);
////		String string10 = htmlTransferable0.getHtml();
////		boolean boolean6 = htmlTransferable0.isDataFlavorSupported(dataFlavor3);
////		String string11 = htmlTransferable0.getHtml();
////		Object object0 = htmlTransferable0.getTransferData(dataFlavor0);
////	}
//	
//	/*
//	 * Null parameter passed into JTextPane constructor.
//	 */
//	@Test
//	public void lilith_AccessStatusTypePreviewRenderer_resolveColorScheme_1() {
//		// I12 Branch 2 IFNULL L68;true
//		AccessStatusTypePreviewRenderer accessStatusTypePreviewRenderer0 = new AccessStatusTypePreviewRenderer();
//		Object[][] objectArray0 = new Object[2][2];
//		Object[] objectArray1 = new Object[7];
//		objectArray1[0] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray1[1] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray1[2] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray1[3] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray1[4] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray1[5] = (Object) accessStatusTypePreviewRenderer0;
//		Object object0 = new Object();
//		objectArray1[6] = object0;
//		objectArray0[0] = objectArray1;
//		Object[] objectArray2 = new Object[9];
//		objectArray2[0] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray2[1] = object0;
//		objectArray2[2] = object0;
//		objectArray2[3] = (Object) accessStatusTypePreviewRenderer0;
//		DefaultTableModel defaultTableModel0 = new DefaultTableModel(objectArray0, objectArray0[0]);
//		DefaultTableColumnModel defaultTableColumnModel0 = new DefaultTableColumnModel();
//		MockDefaultListSelectionModel mockDefaultListSelectionModel0 = new MockDefaultListSelectionModel();
//		JTable jTable0 = new JTable(defaultTableModel0, defaultTableColumnModel0, mockDefaultListSelectionModel0);
//		HttpStatus.Type httpStatus_Type0 = HttpStatus.Type.SERVER_ERROR;
//		boolean boolean0 = false;
//		boolean boolean1 = false;
//		int int0 = 12;
//		int int1 = 0;
//		JLabel jLabel0 = (JLabel)accessStatusTypePreviewRenderer0.getTableCellRendererComponent(jTable0, httpStatus_Type0, boolean0, boolean1, int0, int1);
//		objectArray2[4] = object0;
//		objectArray2[5] = object0;
//		objectArray2[6] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray2[7] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray2[8] = (Object) accessStatusTypePreviewRenderer0;
//		objectArray0[1] = objectArray0[0];
//		JTable jTable1 = new JTable(objectArray0, objectArray1);
//		StyledDocument styledDocument0 = null;
//		JTextPane jTextPane0 = new JTextPane(styledDocument0);
//		boolean boolean2 = true;
//		boolean boolean3 = true;
//		int int2 = 2250;
//		int int3 = (-214);
//		ColorScheme colorScheme0 = accessStatusTypePreviewRenderer0.resolveColorScheme(jTable1, jTextPane0, boolean2, boolean3, int2, int3);
//	}
//	
//	/*
//	 * Null parameters passed to IndexGroup constructor.
//	 */
//	@Test
//	public void summa_IndexGroup_equals_1() throws ParseException {
//		// I16 Branch 15 IFLE L511;true
//		Node node0 = null;
//		FieldProvider<IndexField<Object, String, String>> fieldProvider0 = null;
//		IndexGroup<IndexField<Object, String, String>> indexGroup0 = new IndexGroup<IndexField<Object, String, String>>(node0, fieldProvider0);
//		Object object0 = new Object();
//		boolean boolean0 = indexGroup0.equals(object0);
//	}
//	
//	/*
//	 * Unable to resolve ConsoleReader.
//	 */
////	@Test
////	public void summa_ShellContextImpl_info_1() {
////		// I3 Branch 3 IFNONNULL L276;false
////		ConsoleReader consoleReader0 = null;
////		String string0 = "mirror-path";
////		MockFile mockFile0 = new MockFile(string0);
////		MockPrintStream mockPrintStream0 = new MockPrintStream(mockFile0);
////		String string1 = null;
////		String string2 = "G%_\\\\91{hdD$Zo3*i";
////		MockPrintStream mockPrintStream1 = new MockPrintStream(string1, string2);
////		boolean boolean0 = false;
////		ShellContextImpl shellContextImpl0 = new ShellContextImpl(consoleReader0, mockPrintStream0, mockPrintStream1, boolean0);
////		String string3 = " @fg V3UJxa-=k|Cg7:";
////		shellContextImpl0.info(string3);
////	}
//	
//	/*
//	 * Null parameter passed to CollatorFactory#fixCollator.
//	 */
//	@Test
//	public void summa_CollatorFactory_adjustAASorting_1() {
//		// I3 Branch 5 IFNONNULL L301;false
//		Collator collator0 = null;
//		String string0 = "da";
//		String string1 = "";
//		String string2 = "";
//		Locale locale0 = new Locale(string0, string1, string2);
//		Locale locale1 = Locale.GERMAN;
//		boolean boolean0 = false;
//		RuleBasedCollator ruleBasedCollator0 = (RuleBasedCollator)CollatorFactory.createCollator(locale0, boolean0);
//		Comparator<String> comparator0 = CollatorFactory.wrapCollator(collator0);
//		Comparator<String> comparator1 = CollatorFactory.wrapCollator(collator0);
//		Collator collator1 = CollatorFactory.fixCollator(collator0);
//		Collator collator2 = CollatorFactory.adjustAASorting(collator0);
//	}
//	
//	/*
//	 * Cannot resolve GenericReplayCharSequence
//	 */
////	@Test
////	public void summa_CharSequenceReader_1() {
////		// I120 Branch 2 IFEQ L254;true
////		byte[] byteArray0 = null;
////		long long0 = 708L;
////		long long1 = 0L;
////		String string0 = "e03<f|";
////		GenericReplayCharSequence genericReplayCharSequence0 = new GenericReplayCharSequence(byteArray0, long0, long1, string0);
////		CharSequenceReader charSequenceReader0 = new CharSequenceReader(genericReplayCharSequence0);
////		CharSequenceReader charSequenceReader1 = charSequenceReader0.setSource(genericReplayCharSequence0);
////	}
//	
//	/*
//	 * MessageProperties is not visible.
//	 */
////	@Test
////	public void openjms_MessageProperties_setProperty_1() {
////		// I37 Branch 23 IF_ICMPLT L422;true
////		MessageProperties messageProperties0 = new MessageProperties();
////		MessageProperties messageProperties1 = new MessageProperties();
////		Enumeration<Object> enumeration0 = messageProperties1.getPropertyNames();
////		String string0 = "e";
////		String string1 = "AcmD";
////		short short0 = (short)355;
////		messageProperties0.setShortProperty(string1, short0);
////		double double0 = messageProperties0.getDoubleProperty(string0);
////		short short1 = new Short(short0);
////		String string2 = "org.apache.oro.io.GlobFilenameFilter";
////		MockPrintStream mockPrintStream0 = new MockPrintStream(string1);
////		ObjectOutputStream objectOutputStream0 = new ObjectOutputStream(mockPrintStream0);
////		messageProperties0.writeExternal(objectOutputStream0);
////		byte[] byteArray0 = new byte[1];
////		byte byte0 = (byte)123;
////		byteArray0[0] = byte0;
////		ByteArrayInputStream byteArrayInputStream0 = new ByteArrayInputStream(byteArray0);
////		ObjectInputStream objectInputStream0 = new ObjectInputStream(byteArrayInputStream0);
////		messageProperties0.readExternal(objectInputStream0);
////		messageProperties0.setProperty(string0, string2);
////	}
//	
//	/*
//	 * NoClassDefFoundError: org/exolab/castor/xml/ValidationException
//	 */
////	@Test
////	public void openjms_SerialTask_stop_1() {
////		// I31 Branch 13 IF_ICMPLT L243;true
////		ThreadGroup threadGroup0 = null;
////		String string0 = "";
////		MockThread mockThread0 = new MockThread(threadGroup0, string0);
////		Configuration configuration0 = new Configuration();
////		ServiceThreadListener serviceThreadListener0 = new ServiceThreadListener();
////		DefaultThreadPoolFactory defaultThreadPoolFactory0 = new DefaultThreadPoolFactory(serviceThreadListener0);
////		Scheduler scheduler0 = new Scheduler(configuration0, defaultThreadPoolFactory0);
////		SerialTask serialTask0 = new SerialTask(mockThread0, scheduler0);
////		serialTask0.stop();
////	}
//	
//	/*
//	 * ?
//	 */
//	@Test
//	public void openjms_SelectorTreeParser_primaryExpression_1() throws RecognitionException, IOException {
//		// I10 Branch 43 IF_ICMPLE L751;true
//		SelectorTreeParser selectorTreeParser0 = new SelectorTreeParser();
//		SelectorAST selectorAST0 = new SelectorAST();
//		Expression expression0 = selectorTreeParser0.selector(selectorAST0);
//		TreeParser.panic();
//		TreeParser.ASTNULL = selectorTreeParser0.ASTNULL;
//		String string0 = selectorTreeParser0.ASTNULL.toStringList();
//		String string1 = TreeParser.ASTNULL.toString();
//		String string2 = selectorTreeParser0.ASTNULL.toStringTree();
//		String string3 = TreeParser.ASTNULL.toStringTree();
//		Expression expression1 = selectorTreeParser0.isExpression(selectorAST0);
//		String string4 = TreeParser.ASTNULL.toString();
//		Expression expression2 = selectorTreeParser0.selector(selectorAST0);
//		int int0 = 28;
//		selectorAST0.setType(int0);
//		String string5 = selectorTreeParser0.getTokenName(int0);
//		boolean boolean0 = false;
//		BaseAST.setVerboseStringConversion(boolean0, selectorTreeParser0._tokenNames);
//		Expression expression3 = selectorTreeParser0.selector(selectorAST0);
//		String string6 = ">";
//		selectorTreeParser0.setASTNodeClass(string6);
//		Expression expression4 = selectorTreeParser0.selector(selectorAST0);
//		selectorAST0.initialize((AST) selectorAST0);
//		String string7 = selectorTreeParser0.getTokenName(int0);
//		Expression expression5 = selectorTreeParser0.unaryTerm(selectorAST0);
//		String string8 = selectorTreeParser0.getTokenName(int0);
//		Expression expression6 = selectorTreeParser0.selector(TreeParser.ASTNULL);
//		Expression expression7 = selectorTreeParser0.term(selectorAST0);
//		HashSet<Locale.LanguageRange> hashSet0 = new HashSet<Locale.LanguageRange>(int0);
//		CommonAST commonAST0 = new CommonAST();
//		String string9 = selectorTreeParser0.getTokenName(int0);
//		String string10 = selectorTreeParser0.getTokenName(int0);
//		boolean boolean1 = commonAST0.equalsTreePartial(selectorAST0);
//		Expression expression8 = selectorTreeParser0.literal(selectorTreeParser0.ASTNULL);
//		Expression expression9 = selectorTreeParser0.primaryExpression(commonAST0);
//		Expression expression10 = selectorTreeParser0.literal(selectorAST0);
//		Expression expression11 = selectorTreeParser0.expression(selectorAST0);
//		Expression expression12 = selectorTreeParser0.literal(selectorAST0);
//		String string11 = selectorTreeParser0.getTokenName(int0);
//		Expression expression13 = selectorTreeParser0.betweenExpression(TreeParser.ASTNULL);
//		Expression expression14 = selectorTreeParser0.expression(TreeParser.ASTNULL);
//		boolean boolean2 = commonAST0.equalsList(selectorTreeParser0.ASTNULL);
//		String string12 = selectorTreeParser0.getTokenName(int0);
//		String string13 = selectorTreeParser0.getTokenName(int0);
//		ASTEnumeration aSTEnumeration0 = commonAST0.findAll(selectorAST0);
//		ASTEnumeration aSTEnumeration1 = TreeParser.ASTNULL.findAllPartial(TreeParser.ASTNULL);
//		Expression expression15 = selectorTreeParser0.literal(selectorAST0);
//		boolean boolean3 = selectorAST0.equalsTree(selectorAST0);
//		Expression expression16 = selectorTreeParser0.primaryExpression(TreeParser.ASTNULL);
//		HashSet<Integer> hashSet1 = new HashSet<Integer>();
//		Expression expression17 = selectorTreeParser0.primaryExpression(TreeParser.ASTNULL);
//		selectorAST0.setNextSibling(selectorTreeParser0.ASTNULL);
//		Expression expression18 = selectorTreeParser0.expression(selectorAST0);
//		SelectorAST selectorAST1 = new SelectorAST();
//		boolean boolean4 = selectorAST1.equalsTree(commonAST0);
//		boolean boolean5 = selectorAST1.equalsList(selectorAST0);
//		boolean boolean6 = selectorAST1.equalsTreePartial(selectorAST0);
//		String string14 = TreeParser.ASTNULL.toStringList();
//		selectorAST0.setFirstChild(commonAST0);
//		boolean boolean7 = selectorAST1.equalsListPartial(selectorTreeParser0.ASTNULL);
//		String string15 = selectorTreeParser0.getTokenName(int0);
//		Expression expression19 = selectorTreeParser0.likeExpression(commonAST0);
//		HashSet<Locale.LanguageRange> hashSet2 = new HashSet<Locale.LanguageRange>();
//		String string16 = TreeParser.ASTNULL.toStringList();
//		CommonASTWithHiddenTokens commonASTWithHiddenTokens0 = new CommonASTWithHiddenTokens();
//		boolean boolean8 = selectorAST1.equals((AST) TreeParser.ASTNULL);
//		String string17 = "";
//		String string18 = selectorTreeParser0.getTokenName(int0);
//		selectorTreeParser0.reportWarning(string17);
//		int int1 = (-4080);
//		String string19 = "(+0vgk\\\\u[U;t}";
//		Token token0 = new Token(int0, string19);
//		selectorAST0.initialize(token0);
//		selectorAST0.initialize(int1, string19);
//		Expression expression20 = selectorTreeParser0.primaryExpression(selectorTreeParser0.ASTNULL);
//		Expression expression21 = selectorTreeParser0.likeExpression(selectorAST0);
//		Expression expression22 = selectorTreeParser0.inExpression(commonAST0);
//		String string20 = "";
//		selectorTreeParser0.traceIn(string20, selectorAST0);
//		Expression expression23 = selectorTreeParser0.term(selectorAST1);
//		Expression expression24 = selectorTreeParser0.betweenExpression(TreeParser.ASTNULL);
//		Expression expression25 = selectorTreeParser0.literal(commonASTWithHiddenTokens0);
//		Expression expression26 = selectorTreeParser0.expression(selectorAST0);
//		ASTFactory aSTFactory0 = new ASTFactory();
//		selectorTreeParser0.setASTFactory(aSTFactory0);
//		MockFile mockFile0 = new MockFile(string19);
//		boolean boolean9 = mockFile0.setReadOnly();
//		boolean boolean10 = false;
//		boolean boolean11 = mockFile0.setWritable(boolean10);
//		boolean boolean12 = true;
//		boolean boolean13 = mockFile0.setExecutable(boolean12);
//		MockPrintWriter mockPrintWriter0 = new MockPrintWriter(string12);
//		selectorAST0.xmlSerializeRootClose(mockPrintWriter0);
//		int int2 = (-4600);
//		token0.setColumn(int2);
//		String string21 = selectorTreeParser0.getTokenName(int1);
//		ExpressionFactory expressionFactory0 = null;
//		selectorTreeParser0.initialise(expressionFactory0);
//		Expression expression27 = selectorTreeParser0.selector(selectorAST1);
//		Expression expression28 = selectorTreeParser0.betweenExpression(selectorAST1);
//		Expression expression29 = selectorTreeParser0.literal(TreeParser.ASTNULL);
//		Expression expression30 = selectorTreeParser0.inExpression(selectorAST0);
//		HashSet<Locale> hashSet3 = new HashSet<Locale>();
//		Expression expression31 = selectorTreeParser0.primaryExpression(selectorAST0);
//		Expression expression32 = selectorTreeParser0.betweenExpression(commonASTWithHiddenTokens0);
//		Expression expression33 = selectorTreeParser0.unaryTerm(selectorAST1);
//		Expression expression34 = selectorTreeParser0.primaryExpression(TreeParser.ASTNULL);
//		SelectorAST selectorAST2 = new SelectorAST();
//		selectorAST0.initialize((AST) selectorTreeParser0.ASTNULL);
//		Expression expression35 = selectorTreeParser0.inExpression(selectorAST0);
//		HashSet<String> hashSet4 = new HashSet<String>(token0.INVALID_TYPE);
//		Expression expression36 = selectorTreeParser0.inExpression(commonAST0);
//		String string22 = selectorTreeParser0.getTokenName(int2);
//		selectorAST2.setFirstChild(commonAST0);
//		Expression expression37 = selectorTreeParser0.term(commonAST0);
//		selectorAST2.initialize((AST) selectorAST0);
//		HashSet<Locale> hashSet5 = new HashSet<Locale>(token0.NULL_TREE_LOOKAHEAD);
//		Expression expression38 = selectorTreeParser0.isExpression(selectorAST2);
//		Expression expression39 = selectorTreeParser0.likeExpression(selectorAST1);
//		Expression expression40 = selectorTreeParser0.likeExpression(selectorAST0);
//		Expression expression41 = selectorTreeParser0.primaryExpression(selectorAST2);
//		Expression expression42 = selectorTreeParser0.primaryExpression(selectorAST0);
//	}
//	
//	/*
//	 * BrokeredConnectionControl is not visible.
//	 */
////	@Test
////	public void openjms_MapMessageHandler_setBody_1() {
////		// I9 Branch 1 IFNONNULL L82;false
////		BrokeredConnectionControl brokeredConnectionControl0 = null;
////		BrokeredConnection30 brokeredConnection30_0 = new BrokeredConnection30(brokeredConnectionControl0);
////		DestinationStore destinationStore0 = new DestinationStore(brokeredConnection30_0);
////		MapMessageHandler mapMessageHandler0 = new MapMessageHandler(destinationStore0, brokeredConnection30_0);
////		MapMessageImpl mapMessageImpl0 = new MapMessageImpl();
////		mapMessageHandler0.setBody(mapMessageImpl0, mapMessageImpl0);
////	}
//	
//	/*
//	 * Wrongly classified as out-method?
//	 */
////	@Test
////	public void openjms_ExternalXid_equals_1() {
////		// I3 Branch 18 IFLT L371;false
////		byte[] byteArray0 = new byte[1];
////		byte byte0 = (byte)39;
////		byteArray0[0] = byte0;
////		ExternalXid externalXid0 = new ExternalXid();
////		ExternalXid externalXid1 = new ExternalXid();
////		ExternalXid externalXid2 = new ExternalXid();
////		boolean boolean0 = externalXid1.equals(externalXid2);
////	}
//	
//	/*
//	 * Null parameter passed to MockPrintStream.
//	 */
//	@Test
//	public void openjms_TransactionState_equals_1() throws IOException {
//		// I9 Branch 1 IFNONNULL L82;true
//		TransactionState transactionState0 = new TransactionState();
//		boolean boolean0 = transactionState0.isPrepared();
//		Object object0 = new Object();
//		String string0 = null;
//		String string1 = "<VWA/V(";
//		MockPrintStream mockPrintStream0 = new MockPrintStream(string0, string1);
//		ObjectOutputStream objectOutputStream0 = new ObjectOutputStream(mockPrintStream0);
//		transactionState0.writeExternal(objectOutputStream0);
//		boolean boolean1 = transactionState0.equals(object0);
//	}
//	
//	/*
//	 * Null parameter passed to CommandLine#value.
//	 */
//	@Test
//	public void openjms_CommandLine_add_1() {
//		// I37 Branch 10 IF_ICMPLT L142;true
//		String[] stringArray0 = new String[1];
//		String string0 = "org.exolab.jms.util.CommandLine";
//		stringArray0[0] = string0;
//		org.exolab.jms.util.CommandLine commandLine0 = new org.exolab.jms.util.CommandLine(stringArray0);
//		String string1 = "K4=yeY(";
//		String string2 = "";
//		boolean boolean0 = commandLine0.exists(string2);
//		String string3 = commandLine0.value(string1);
//		String string4 = "";
//		String string5 = "F5\\\"";
//		String string6 = "";
//		String string7 = "";
//		boolean boolean1 = commandLine0.add(string6, string7);
//		boolean boolean2 = commandLine0.add(string4, string5);
//		String string8 = "";
//		String string9 = "X!";
//		boolean boolean3 = commandLine0.exists(string9);
//		String string10 = "";
//		String string11 = "";
//		String string12 = null;
//		String string13 = "org/exolab/jms/util/CommandLine#processCommandLine([Ljava/lang/String;)V";
//		String string14 = commandLine0.value(string12, string13);
//		String string15 = "!";
//		boolean boolean4 = commandLine0.exists(string15);
//		String string16 = "";
//		boolean boolean5 = commandLine0.add(string11, string16);
//		String string17 = null;
//		boolean boolean6 = commandLine0.exists(string17);
//		String string18 = "^^`U&VK";
//		boolean boolean7 = commandLine0.isSwitch(string18);
//		String string19 = commandLine0.value(string10);
//		String string20 = "";
//		String string21 = "L6Rk'-ZJLDw";
//		String string22 = "org/exolab/jms/util/CommandLine#processCommandLine([Ljava/lang/String;)V";
//		boolean boolean8 = commandLine0.exists(string22);
//		String string23 = "j";
//		String string24 = commandLine0.value(string21, string23);
//		String string25 = "VE7$";
//		String string26 = "4F6^J/bB&46K)bow";
//		boolean boolean9 = commandLine0.exists(string26);
//		String string27 = "org.exolab.jms.util.CommandLine";
//		boolean boolean10 = commandLine0.add(string25, string27);
//		boolean boolean11 = true;
//		String string28 = "";
//		boolean boolean12 = commandLine0.isParameter(string28);
//		String string29 = "";
//		String string30 = commandLine0.value(string29);
//		String string31 = "";
//		String string32 = "";
//		boolean boolean13 = commandLine0.add(string31, string32);
//		String string33 = "nLv(_pi|G4Z!W";
//		boolean boolean14 = commandLine0.isSwitch(string33);
//		String string34 = "org/exolab/jms/util/CommandLine#processCommandLine([Ljava/lang/String;)V";
//		boolean boolean15 = commandLine0.exists(string34);
//		String string35 = "";
//		String string36 = commandLine0.value(string35);
//		String string37 = "_,a,r&zMv1rMJ";
//		boolean boolean16 = commandLine0.isSwitch(string37);
//		String string38 = "xYa:Riv)>bJ";
//		String string39 = commandLine0.value(string1);
//		boolean boolean17 = commandLine0.isParameter(string38);
//		String string40 = "F\\\\2yn{M";
//		String string41 = "Vp8;o'u3p((lKe";
//		String string42 = commandLine0.value(string40, string41);
//		String string43 = "";
//		String string44 = "=";
//		boolean boolean18 = commandLine0.add(string43, string44);
//		String string45 = "-";
//		boolean boolean19 = commandLine0.exists(string45);
//		String string46 = "~PY";
//		String string47 = "";
//		String string48 = commandLine0.value(string46, string47);
//		String string49 = ".";
//		String string50 = commandLine0.value(string49);
//		String string51 = "18Y%.";
//		boolean boolean20 = commandLine0.isSwitch(string51);
//		String string52 = "`KHrWR&w%Ed";
//		String string53 = commandLine0.value(string52);
//		String string54 = "";
//		String string55 = "";
//		boolean boolean21 = commandLine0.add(string54, string55);
//		String string56 = "!DPU)&Rf:u2$";
//		String string57 = commandLine0.value(string56);
//		String string58 = "";
//		boolean boolean22 = commandLine0.exists(string58);
//		String string59 = "";
//		String string60 = "-";
//		boolean boolean23 = commandLine0.isSwitch(string60);
//		boolean boolean24 = commandLine0.isSwitch(string59);
//		String string61 = "";
//		String string62 = "";
//		boolean boolean25 = commandLine0.add(string61, string62);
//		boolean boolean26 = commandLine0.add(string8, string20, boolean11);
//	}
//	
//	/*
//	 * NoClassDefFoundError: org/exolab/castor/xml/ValidationException
//	 */
////	@Test
////	public void openjms_GarbageCollectionService_doStart_1() throws ServiceException {
////		Configuration configuration0 = new Configuration();
////		ThreadListener threadListener0 = null;
////		DefaultThreadPoolFactory defaultThreadPoolFactory0 = new DefaultThreadPoolFactory(threadListener0);
////		BasicEventManager basicEventManager0 = new BasicEventManager(defaultThreadPoolFactory0);
////		GarbageCollectionService garbageCollectionService0 = new GarbageCollectionService(configuration0, basicEventManager0);
////		garbageCollectionService0.doStart();
////	}
//	
//	/*
//	 * Null parameter passed into HTTPRequestInfo constructor.
//	 */
//	@Test
//	public void openjms_HTTPRequestInfo_equals_1() throws ResourceException {
//		// I23 Branch 3 IFNONNULL L122;false
//		org.exolab.jms.net.uri.URI uRI0 = null;
//		String string0 = null;
//		Properties properties0 = new Properties(string0);
//		HTTPRequestInfo hTTPRequestInfo0 = new HTTPRequestInfo(uRI0, properties0);
//		HTTPRequestInfo hTTPRequestInfo1 = new HTTPRequestInfo(uRI0);
//		boolean boolean0 = hTTPRequestInfo1.equals(hTTPRequestInfo1);
//	}
//	
//	/*
//	 * Null parameter (URI) passed to RMIRequestInfo constructor.
//	 */
//	@Test
//	public void openjms_HTTPSManagedConnectionFactory_createManagedConnection_1() throws ResourceException {
//		// I18 Branch 1 IFNONNULL L101;false
//		HTTPSManagedConnectionFactory hTTPSManagedConnectionFactory0 = new HTTPSManagedConnectionFactory();
//		String string0 = "Failed to set property=java.protocol.handler.pkgs";
//		JMXPrincipal jMXPrincipal0 = new JMXPrincipal(string0);
//		org.exolab.jms.net.uri.URI uRI0 = null;
//		String string1 = "lPQi_2hR,<pphw";
//		Properties properties0 = new Properties(string1);
//		RMIRequestInfo rMIRequestInfo0 = new RMIRequestInfo(uRI0, properties0);
//		ManagedConnection managedConnection0 = hTTPSManagedConnectionFactory0.createManagedConnection(jMXPrincipal0, rMIRequestInfo0);
//	}
//	
//	/*
//	 * ObjectRef is not visible.
//	 */
////	@Test
////	public void openjms_ObjectRef_getProxy_1() {
////		// I31 Branch 2 IFNONNULL L104;false
////		ObjID objID0 = new ObjID();
////		InputStream inputStream0 = null;
////		ObjectInputStream objectInputStream0 = new ObjectInputStream(inputStream0);
////		Class<ObjectRef> class0 = ObjectRef.class;
////		org.exolab.jms.net.orb.ObjectRef objectRef0 = new ObjectRef(objID0, objectInputStream0, class0);
////		URI[] uRIArray0 = objectRef0.getURIs();
////		Object object0 = new Object();
////		boolean boolean0 = FileSystemHandling.shouldAllThrowIOExceptions();
////		URI uRI0 = null;
////		Proxy proxy0 = objectRef0.getProxy(uRI0);
////	}
//	
//	/*
//	 * Null parameter passed to ObjID#write
//	 */
//	@Test
//	public void openjms_UnicastDelegate_dispose_1() throws IOException {
//		// I31 Branch 2 IFNONNULL L104;true
//		int int0 = 36;
//		ObjID objID0 = new ObjID(int0);
//		String string0 = "S3\\\"FO/Qf&Ij.[0a%";
//		ObjectOutput objectOutput0 = null;
//		objID0.write(objectOutput0);
//		UnicastDelegate unicastDelegate0 = new UnicastDelegate(objID0, string0);
//		unicastDelegate0.dispose();
//	}
//	
//	/*
//	 * NoClassDefFoundError: org/exolab/castor/xml/ValidationException
//	 */
////	@Test
////	public void openjms_RMIManagedConnectionFactory_createManagedConnectionAcceptor_1() throws ResourceException {
////		// I23 Branch 3 IFNONNULL L122;false
////		RMIManagedConnectionFactory rMIManagedConnectionFactory0 = new RMIManagedConnectionFactory();
////		Configuration configuration0 = new Configuration();
////		DatabaseService databaseService0 = new DatabaseService(configuration0);
////		UserManager userManager0 = new UserManager(configuration0, databaseService0);
////		AuthenticationMgr authenticationMgr0 = new AuthenticationMgr(userManager0);
////		org.exolab.jms.net.uri.URI uRI0 = null;
////		RMIRequestInfo rMIRequestInfo0 = new RMIRequestInfo(uRI0);
////		LinkedList<Object> linkedList0 = new LinkedList<Object>();
////		String string0 = "{";
////		JMXPrincipal jMXPrincipal0 = new JMXPrincipal(string0);
////		ConnectionRequestInfo connectionRequestInfo0 = null;
////		ManagedConnection managedConnection0 = rMIManagedConnectionFactory0.matchManagedConnections(linkedList0, jMXPrincipal0, connectionRequestInfo0);
////		ManagedConnectionAcceptor managedConnectionAcceptor0 = rMIManagedConnectionFactory0.createManagedConnectionAcceptor(authenticationMgr0, rMIRequestInfo0);
////	}
//	
//	/*
//	 * Null parameter passed to RMIRequestInfo constructor.
//	 */
//	@Test
//	public void openjms_RMIRequestInfo_equals_1() throws ResourceException {
//		// I18 Branch 1 IFNONNULL L101;true
//		org.exolab.jms.net.uri.URI uRI0 = null;
//		int int0 = 2238;
//		float float0 = 0.0F;
//		HashMap<String, Object> hashMap0 = new HashMap<String, Object>();
//		String string0 = "Ysdf!P";
//		Properties properties0 = new Properties(hashMap0, string0);
//		RMIRequestInfo rMIRequestInfo0 = new RMIRequestInfo(uRI0, properties0);
//		String string1 = "";
//		boolean boolean0 = rMIRequestInfo0.equals(string1);
//	}
//	
//	/*
//	 * Null parameter passed to SocketRequestInfo constructor.
//	 */
//	@Test
//	public void openjms_SocketRequestInfo_equals_1() throws ResourceException {
//		// I23 Branch 3 IFNONNULL L122;true
//		org.exolab.jms.net.uri.URI uRI0 = null;
//		SocketRequestInfo socketRequestInfo0 = new SocketRequestInfo(uRI0);
//		Object object0 = new Object();
//		boolean boolean0 = socketRequestInfo0.equals(object0);
//	}
//	
//	/*
//	 * TCPSManagedConnection is not visible.
//	 */
////	@Test
////	public void openjms_TCPSManagedConnection_createSocket_1() {
////		// I18 Branch 1 IFNONNULL L101;true
////		String string0 = "\\\"5";
////		JMXPrincipal jMXPrincipal0 = new JMXPrincipal(string0);
////		URI uRI0 = null;
////		org.exolab.jms.net.uri.URI uRI1 = new URI(uRI0);
////		Properties properties0 = null;
////		TCPSRequestInfo tCPSRequestInfo0 = new TCPSRequestInfo(uRI1, properties0);
////		TCPSManagedConnection tCPSManagedConnection0 = new TCPSManagedConnection(jMXPrincipal0, tCPSRequestInfo0);
////		Socket socket0 = tCPSManagedConnection0.createSocket(tCPSRequestInfo0);
////	}
//	
//	/*
//	 * VMManagedConnectionAcceptor is not visible.
//	 */
////	@Test
////	public void openjms_VMManagedConnectionAcceptor_close_1() {
////		// I18 Branch 6 IFNE L230;true
////		Configuration configuration0 = new Configuration();
////		DatabaseService databaseService0 = new DatabaseService(configuration0);
////		UserManager userManager0 = new UserManager(configuration0, databaseService0);
////		AuthenticationMgr authenticationMgr0 = new AuthenticationMgr(userManager0);
////		org.exolab.jms.net.uri.URI uRI0 = null;
////		URIRequestInfo uRIRequestInfo0 = new URIRequestInfo(uRI0);
////		VMManagedConnectionFactory vMManagedConnectionAcceptor0 = new org.exolab.jms.net.vm.VMManagedConnectionAcceptor(authenticationMgr0, uRIRequestInfo0);
////		vMManagedConnectionAcceptor0.close();
////	}
//	
//	/*
//	 * VMManagedConnectionAcceptor is not visible.
//	 */
////	@Test
////	public void openjms_VMManagedConnectionFactory_createManagedConnectionAcceptor_1() {
////		// I23 Branch 3 IFNONNULL L122;true
////		VMManagedConnectionFactory vMManagedConnectionFactory0 = new VMManagedConnectionFactory();
////		Configuration configuration0 = new Configuration();
////		DatabaseService databaseService0 = new DatabaseService(configuration0);
////		UserManager userManager0 = new UserManager(configuration0, databaseService0);
////		AuthenticationMgr authenticationMgr0 = new AuthenticationMgr(userManager0);
////		org.exolab.jms.net.uri.URI uRI0 = null;
////		HashMap<VMManagedConnectionAcceptor, Integer> hashMap0 = new HashMap<VMManagedConnectionAcceptor, Integer>();
////		HashMap<VMManagedConnectionAcceptor, Integer> hashMap1 = new HashMap<VMManagedConnectionAcceptor, Integer>(hashMap0);
////		String string0 = "org.exolab.jms.net.vm.VMManagedConnectionFactory";
////		Properties properties0 = new Properties(hashMap1, string0);
////		HTTPRequestInfo hTTPRequestInfo0 = new HTTPRequestInfo(uRI0);
////		ManagedConnectionAcceptor managedConnectionAcceptor0 = vMManagedConnectionFactory0.createManagedConnectionAcceptor(authenticationMgr0, hTTPRequestInfo0);
////	}
//	
//	/*
//	 * Complicated case.
//	 */
//	@Test
//	public void lhamacaw_VariableSearchPanel_hasSearchResults_1() {
//		// I38 Branch 13 IFNE L-1;false
//		SessionProperties sessionProperties0 = new SessionProperties();
//		VariableSearchPanel variableSearchPanel0 = new VariableSearchPanel(sessionProperties0);
//		boolean boolean0 = variableSearchPanel0.hasSearchResults();
//	}
//	
//	/*
//	 * Complicated case.
//	 */
//	@Test
//	public void openhre_HL7CheckerImpl_endField_1() {
//		// I10 Branch 10 IF_ICMPGE L63;false
//		HL7CheckerImpl hL7CheckerImpl0 = new HL7CheckerImpl();
//		hL7CheckerImpl0.endOfRecord();
//		HL7CheckerStateImpl hL7CheckerStateImpl0 = new HL7CheckerStateImpl();
//		hL7CheckerImpl0.setState(hL7CheckerStateImpl0);
//		hL7CheckerImpl0.endField();
//	}
//	
//	/*
//	 * Compilation error.
//	 */
////	@Test
////	public void openhre_MessageTracerImpl_processGroupContent_1() {
////		// I7 Branch 26 IFNE L148;true
////		MessageTracerImpl messageTracerImpl0 = new MessageTracerImpl();
////		ExpressionImpl expressionImpl0 = new ExpressionImpl();
////		ExpressionPartImpl expressionPartImpl0 = new ExpressionPartImpl();
////		String string0 = expressionPartImpl0.toString();
////		expressionImpl0.addItem(expressionPartImpl0);
////		HL7MessageGroupImpl hL7MessageGroupImpl0 = new HL7MessageGroupImpl();
////		ExpressionElementMapperImpl expressionElementMapperImpl0 = new ExpressionElementMapperImpl();
////		int int0 = expressionElementMapperImpl0.size();
////		String string1 = expressionImpl0.toString((ExpressionElementMapper) expressionElementMapperImpl0);
////		expressionElementMapperImpl0.clearAll();
////		messageTracerImpl0.doEndItem(expressionImpl0, hL7MessageGroupImpl0);
////		String string2 = expressionImpl0.toString((ExpressionElementMapper) expressionElementMapperImpl0);
////		ExpressionImpl expressionImpl1 = new ExpressionImpl();
////		HL7MessageGroupImpl hL7MessageGroupImpl1 = new HL7MessageGroupImpl();
////		messageTracerImpl0.doBeginItem(expressionImpl1, hL7MessageGroupImpl0);
////		messageTracerImpl0.doEndItem(expressionImpl1, hL7MessageGroupImpl0);
////		String string3 = expressionImpl1.toString((ExpressionElementMapper) expressionElementMapperImpl0);
////		int int1 = 5;
////		int int2 = 1710;
////		HL7MessageGroupItem hL7MessageGroupItem0 = hL7MessageGroupImpl0.getItem(int2);
////		expressionImpl1.addItem(expressionPartImpl0);
////		int int3 = hL7MessageGroupImpl1.size();
////		boolean boolean0 = true;
////		int int4 = expressionElementMapperImpl0.size();
////		hL7MessageGroupImpl1.setRepeatable(boolean0);
////		hL7MessageGroupImpl1.addItem(hL7MessageGroupImpl0);
////		boolean boolean1 = false;
////		hL7MessageGroupImpl0.setRepeatable(boolean1);
////		ExpressionPartImpl expressionPartImpl1 = new ExpressionPartImpl();
////		expressionImpl1.addItem(expressionPartImpl1);
////		hL7MessageGroupImpl1.addItem(hL7MessageGroupImpl0);
////		String string4 = "org.apache.axis.wsdl.symbolTable.BaseTypeMapping";
////		expressionImpl1.readFromStringForDebug(string4);
////		int int5 = hL7MessageGroupImpl1.size();
////		hL7MessageGroupImpl1.removeItem(int1);
////		int int6 = (-2057);
////		expressionPartImpl0.setElementID(int6);
////		messageTracerImpl0.processGroup(expressionImpl1, hL7MessageGroupImpl1);
////		messageTracerImpl0.processEnd();
////		int int7 = (-980);
////		expressionImpl0.setNumberOfElementTypes(int7);
////		String string5 = "";
////		messageTracerImpl0.processNextSegment(string5);
////		HL7MessageImpl hL7MessageImpl0 = new HL7MessageImpl();
////		expressionImpl0.addItem(expressionPartImpl0);
////		hL7MessageImpl0.setGroup(hL7MessageGroupImpl0);
////		String string6 = "H1I`\\\\If*RujW]J:~f";
////		hL7MessageGroupImpl0.setAdditional(string6);
////		String string7 = hL7MessageImpl0.toString();
////		hL7MessageImpl0.setGroup(hL7MessageGroupImpl0);
////		messageTracerImpl0.buildMatrixForMessage(hL7MessageImpl0);
////		messageTracerImpl0.doBeginItem(expressionImpl1, hL7MessageGroupImpl1);
////		boolean boolean2 = false;
////		hL7MessageGroupImpl1.setRepeatable(boolean2);
////		expressionImpl1.addItem(expressionPartImpl0);
////		HL7CheckerImpl hL7CheckerImpl0 = new HL7CheckerImpl();
////		hL7CheckerImpl0.endOfRecord();
////		HL7ConfigurationImpl hL7ConfigurationImpl0 = new HL7ConfigurationImpl();
////		HL7DataTypeMapImpl hL7DataTypeMapImpl0 = new HL7DataTypeMapImpl();
////		hL7ConfigurationImpl0.setDataTypes(hL7DataTypeMapImpl0);
////		HTMLDocumentImpl hTMLDocumentImpl0 = null;
////		String string8 = "";
////		HTMLSelectElementImpl hTMLSelectElementImpl0 = new HTMLSelectElementImpl(hTMLDocumentImpl0, string8);
////		HTMLDocumentImpl hTMLDocumentImpl1 = new HTMLDocumentImpl();
////		String string9 = "";
////		HTMLInputElementImpl hTMLInputElementImpl0 = new HTMLInputElementImpl(hTMLDocumentImpl1, string9);
////		String string10 = "com.browsersoft.openhre.hl7.impl.regular.MessageTracerImpl";
////		EventListener eventListener0 = null;
////		boolean boolean3 = true;
////		boolean boolean4 = hTMLInputElementImpl0.hasChildNodes();
////		String string11 = "y#a&_";
////		hL7CheckerImpl0.messageTracerErrorEvent(string11);
////		hTMLInputElementImpl0.addEventListener(string10, eventListener0, boolean3);
////		hL7ConfigurationImpl0.readConfigurations(hTMLSelectElementImpl0, hTMLDocumentImpl0, hTMLSelectElementImpl0, hTMLInputElementImpl0);
////		hL7CheckerImpl0.endField();
////		hL7CheckerImpl0.setConfiguration(hL7ConfigurationImpl0);
////		messageTracerImpl0.setHandler(hL7CheckerImpl0);
////		ExpressionElementMapperImpl expressionElementMapperImpl1 = new ExpressionElementMapperImpl();
////		String string12 = expressionImpl1.toString((ExpressionElementMapper) expressionElementMapperImpl1);
////		String string13 = "!Y4+(x.";
////		hL7CheckerImpl0.beginField();
////		hL7CheckerImpl0.subComponent(string13);
////		String string14 = hL7MessageImpl0.toString();
////		messageTracerImpl0.setHandler(hL7CheckerImpl0);
////		messageTracerImpl0.doEndItem(expressionImpl1, hL7MessageGroupImpl1);
////		int int8 = 1455;
////		expressionImpl1.removeItem(int8);
////		int int9 = (-2339);
////		expressionImpl0.setItem(int9, expressionPartImpl1);
////		String string15 = expressionImpl1.toString((ExpressionElementMapper) expressionElementMapperImpl0);
////		messageTracerImpl0.doEndItem(expressionImpl0, hL7MessageGroupImpl1);
////		String string16 = "Thirteen";
////		messageTracerImpl0.processNextSegment(string16);
////		messageTracerImpl0.buildMatrixForMessage(hL7MessageImpl0);
////		String string17 = "Not yet implemented. 74";
////		messageTracerImpl0.processNextSegment(string17);
////		ExpressionMatrixImpl expressionMatrixImpl0 = new ExpressionMatrixImpl();
////		int int10 = hL7MessageGroupImpl1.size();
////		messageTracerImpl0.setMatrix(expressionMatrixImpl0);
////		ExpressionElementMapper expressionElementMapper0 = messageTracerImpl0.getMapper();
////		messageTracerImpl0.processGroup(expressionImpl1, hL7MessageGroupImpl1);
////		String string18 = "dD0I9";
////		messageTracerImpl0.processNextSegment(string18);
////		messageTracerImpl0.setMatrix(expressionMatrixImpl0);
////		ExpressionMatrix expressionMatrix0 = messageTracerImpl0.getMatrix();
////		ExpressionElementMapper expressionElementMapper1 = messageTracerImpl0.getMapper();
////		messageTracerImpl0.buildMatrixForMessage(hL7MessageImpl0);
////		String string19 = "@:lrq7v6S(o7uMc6)>B";
////		boolean boolean5 = true;
////		String string20 = expressionMatrixImpl0.outNoStandardConnections(boolean5, expressionElementMapperImpl0);
////		hL7MessageImpl0.setID(string19);
////		ExpressionElementMapperImpl expressionElementMapperImpl2 = new ExpressionElementMapperImpl();
////		int int11 = 0;
////		int int12 = 1457;
////		ExpressionElementMapperItemImpl expressionElementMapperItemImpl0 = new ExpressionElementMapperItemImpl();
////		expressionElementMapperImpl2.setItem(int12, expressionElementMapperItemImpl0);
////		boolean boolean6 = true;
////		String string21 = expressionMatrixImpl0.outNoStandardConnections(boolean6, expressionElementMapper1);
////		expressionElementMapperImpl2.removeItem(int11);
////		messageTracerImpl0.setMapper(expressionElementMapperImpl2);
////		messageTracerImpl0.setMatrix(expressionMatrixImpl0);
////		messageTracerImpl0.doBeginItem(expressionImpl0, hL7MessageGroupImpl0);
////		messageTracerImpl0.buildMatrixForMessage(hL7MessageImpl0);
////		messageTracerImpl0.doEndItem(expressionImpl1, hL7MessageGroupItem0);
////		ExpressionElementMapper expressionElementMapper2 = null;
////		messageTracerImpl0.setMapper(expressionElementMapper2);
////		messageTracerImpl0.processEnd();
////		MessageTracerHandler messageTracerHandler0 = messageTracerImpl0.getHandler();
////		messageTracerImpl0.processGroup(expressionImpl1, hL7MessageGroupImpl1);
////		ExpressionMatrixImpl expressionMatrixImpl1 = new ExpressionMatrixImpl();
////		messageTracerImpl0.setMatrix(expressionMatrixImpl0);
////		hL7MessageImpl0.setGroup(hL7MessageGroupImpl0);
////		ExpressionElementMapperImpl expressionElementMapperImpl3 = new ExpressionElementMapperImpl();
////		MessageTracerHandler messageTracerHandler1 = messageTracerImpl0.getHandler();
////		messageTracerImpl0.processGroup(expressionImpl1, hL7MessageGroupImpl0);
////		String string22 = "com/browsersoft/openhre/hl7/impl/regular/MessageTracerImpl#processGroup(Lcom/browsersoft/openhre/hl7/api/regular/Expression;Lcom/browsersoft/openhre/hl7/api/config/HL7MessageGroup;)V";
////		messageTracerImpl0.processNextSegment(string22);
////		messageTracerImpl0.doEndItem(expressionImpl1, hL7MessageGroupItem0);
////		messageTracerImpl0.processEnd();
////		ExpressionMatrixImpl expressionMatrixImpl2 = new ExpressionMatrixImpl();
////		messageTracerImpl0.setMatrix(expressionMatrixImpl2);
////		messageTracerImpl0.buildMatrixForMessage(hL7MessageImpl0);
////		messageTracerImpl0.processEnd();
////		messageTracerImpl0.setMatrix(expressionMatrixImpl2);
////		messageTracerImpl0.setMapper(expressionElementMapper2);
////		messageTracerImpl0.setHandler(messageTracerHandler1);
////		messageTracerImpl0.processEnd();
////		messageTracerImpl0.buildMatrixForMessage(hL7MessageImpl0);
////		messageTracerImpl0.processEnd();
////		messageTracerImpl0.processGroupContent(expressionImpl0, hL7MessageGroupImpl0);
////	}
//	
//	/*
//	 * Field not visible.
//	 */
////	@Test
////	public void caloriecount_ADCAdaptor_deepCopyData_1() {
////		// I19 Branch 3 IFEQ L-1;false
////		ADCAdaptor aDCAdaptor0 = new ADCAdaptor();
////		TreeNode treeNode0 = new TreeNode();
////		LinkedHashSet<ApplicationData> linkedHashSet0 = new LinkedHashSet<ApplicationData>();
////		aDCAdaptor0.myElements = (Collection<ApplicationData>) linkedHashSet0;
////		String string0 = "Q";
////		TreeNode treeNode1 = new TreeNode(treeNode0);
////		Object[] objectArray0 = linkedHashSet0.toArray();
////		TreeNode treeNode2 = new TreeNode();
////		LinkedList<ADCAdaptor> linkedList0 = new LinkedList<ADCAdaptor>();
////		HashMap<String, Integer> hashMap0 = new HashMap<String, Integer>();
////		boolean boolean0 = aDCAdaptor0.isEmpty();
////		Spliterator<ApplicationData> spliterator0 = aDCAdaptor0.spliterator();
////		boolean boolean1 = false;
////		ADCAdaptor aDCAdaptor1 = (ADCAdaptor)aDCAdaptor0.deepCopy();
////		boolean boolean2 = aDCAdaptor0.addAll(linkedHashSet0);
////		BiFunction<Object, Object, Integer> biFunction0 = null;
////		hashMap0.replaceAll(biFunction0);
////		int int0 = 121;
////		Integer integer0 = new Integer(int0);
////		Integer integer1 = new Integer(int0);
////		HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
////		Object object0 = new Object();
////		Object object1 = aDCAdaptor0.deepCopy();
////		ADCAdaptor aDCAdaptor2 = new ADCAdaptor();
////		treeNode1.updateFrom(aDCAdaptor2);
////		ADCAdaptor aDCAdaptor3 = new ADCAdaptor();
////		Object object2 = aDCAdaptor3.deepCopy();
////		Object object3 = new Object();
////		TreeNode treeNode3 = new TreeNode(treeNode1);
////		hashMap1.putAll(hashMap0);
////		boolean boolean3 = aDCAdaptor0.removeAll(linkedHashSet0);
////		aDCAdaptor0.postDeserialize();
////		Object object4 = treeNode0.deepCopy();
////		boolean boolean4 = aDCAdaptor2.isDirty();
////		boolean boolean5 = aDCAdaptor1.contains(aDCAdaptor0.myElements);
////		boolean boolean6 = aDCAdaptor1.isEmpty();
////		boolean boolean7 = aDCAdaptor2.myDirty;
////		ADCListenerAdaptor aDCListenerAdaptor0 = new ADCListenerAdaptor();
////		ADCEvent aDCEvent0 = new ADCEvent();
////		aDCEvent0.element = (ApplicationData) aDCAdaptor2;
////		aDCListenerAdaptor0.eventOccurred(aDCEvent0);
////		boolean boolean8 = aDCAdaptor2.equals(aDCAdaptor2);
////		HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
////		hashMap0.putAll(hashMap2);
////		aDCAdaptor0.initialize(linkedHashSet0);
////		boolean boolean9 = false;
////		aDCAdaptor0.setDirty(boolean9);
////		TreeNode treeNode4 = new TreeNode((Object) treeNode3);
////		Object object5 = new Object();
////		boolean boolean10 = aDCAdaptor2.add((ApplicationData) aDCAdaptor3);
////		String[] stringArray0 = new String[2];
////		stringArray0[0] = string0;
////		Object[] objectArray1 = aDCAdaptor0.toArray();
////		stringArray0[0] = string0;
////		stringArray0[1] = stringArray0[0];
////		String[] stringArray1 = aDCAdaptor3.toArray(stringArray0);
////		aDCAdaptor0.initialize(aDCAdaptor3);
////		aDCAdaptor2.update(aDCEvent0.element);
////		int int1 = 175;
////		Integer integer2 = new Integer(int1);
////		boolean boolean11 = aDCAdaptor2.contains(spliterator0);
////		Object object6 = new Object();
////		Object object7 = aDCAdaptor0.deepCopy();
////		boolean boolean12 = aDCAdaptor1.contains(object7);
////		Object object8 = aDCAdaptor0.deepCopy();
////		HashMap<String, ADCAdaptor> hashMap3 = new HashMap<String, ADCAdaptor>();
////		TreeNode treeNode5 = new TreeNode();
////		ADCAdaptor aDCAdaptor4 = new ADCAdaptor();
////		boolean boolean13 = aDCAdaptor0.remove(aDCAdaptor4);
////		boolean boolean14 = aDCAdaptor2.isEmpty();
////		Object object9 = aDCAdaptor0.deepCopy();
////		aDCAdaptor0.deepCopyData(treeNode2, hashMap0, boolean1);
////	}
//	
//	/*
//	 * Non-visible method call
//	 */
////	@Test
////	public void caloriecount_CalorieCountData_setEntryList_1() {
////		// I14 Branch 45 IFEQ L-1;false
////		CalorieCountData calorieCountData0 = new CalorieCountData();
////		EntryList entryList0 = calorieCountData0.getEntryList();
////		SimpleElement simpleElement0 = calorieCountData0.createSerializationElement();
////		String string0 = "";
////		String string1 = "AyYn{7>`qfV:N[h8jI5";
////		calorieCountData0.ourElements = calorieCountData0.ourElements;
////		boolean boolean0 = false;
////		long long0 = simpleElement0.getTimeValueOfChild(string1, boolean0);
////		long long1 = new Long(long0);
////		String string2 = "com.lts.pest.tree.TreeListenerHelper";
////		String string3 = "R";
////		simpleElement0.setAttributeValue(string2, string3);
////		String string4 = "frequent";
////		calorieCountData0.buildEntries();
////		calorieCountData0.deserializeFrom(simpleElement0);
////		simpleElement0.setAttributeValue(string0, string4);
////		double double0 = new Long(long0);
////		calorieCountData0.populateFromElement(simpleElement0);
////		CalorieCountData.CalorieCountDataElementsWrapper calorieCountData_CalorieCountDataElementsWrapper0 = new CalorieCountData.CalorieCountDataElementsWrapper();
////		String string5 = "lastSelected";
////		CalorieCountDataElements[] calorieCountDataElementsArray0 = CalorieCountDataElements.values();
////		int int0 = simpleElement0.getIntValueOfChild(string5);
////		String string6 = calorieCountData0.getEntryName();
////		CalorieCountDataElements[] calorieCountDataElementsArray1 = CalorieCountDataElements.values();
////		CalorieCountDataElements[] calorieCountDataElementsArray2 = CalorieCountData.ourElements;
////		long long2 = new Long(long1);
////		CalorieCountDataElements calorieCountDataElements0 = CalorieCountDataElements.Meal;
////		calorieCountData0.serializeTo(simpleElement0);
////		int int1 = 2842;
////		calorieCountData0.createFrequentFood(int1);
////		CalorieCountDataElements calorieCountDataElements1 = CalorieCountDataElements.Food;
////		calorieCountData0.checkElement(calorieCountDataElements1);
////		CalorieCountDataElements calorieCountDataElements2 = CalorieCountDataElements.Meal;
////		calorieCountData0.checkElement(calorieCountDataElements2);
////		FoodList foodList0 = new FoodList();
////		SimpleElement simpleElement1 = simpleElement0.nameToChild(string4);
////		calorieCountData0.setFoods(foodList0);
////		int int2 = new Integer(int0);
////		FrequentFoodList frequentFoodList0 = new FrequentFoodList();
////		int int3 = 1011;
////		Food food0 = new Food();
////		frequentFoodList0.increment(food0);
////		FrequentFood frequentFood0 = frequentFoodList0.getEntryForFoodId(int1);
////		boolean boolean1 = calorieCountData0.isDirty();
////		Consumer<Food> consumer0 = null;
////		foodList0.forEach(consumer0);
////		FrequentFood frequentFood1 = new FrequentFood(int3, food0);
////		calorieCountData0.setFrequentFoods(frequentFoodList0);
////		Budget budget0 = calorieCountData0.getBudget();
////		calorieCountData0.deserializeFrom(simpleElement0);
////		StringBuffer stringBuffer0 = new StringBuffer();
////		food0.addValues(stringBuffer0);
////		EntryList entryList1 = calorieCountData0.getEntryList();
////		int int4 = 2;
////		calorieCountData0.createFrequentFood(int4);
////		String string7 = CalorieCountData.TAG_TAG;
////		Budget budget1 = calorieCountData0.getBudget();
////		calorieCountData0.setBudget(budget0);
////		calorieCountData0.serializeTo(simpleElement1);
////		calorieCountData0.postDeserialize();
////		calorieCountData0.setFrequentFoods(frequentFoodList0);
////		calorieCountData0.serializeTo(simpleElement0);
////		int int5 = (-1650);
////		calorieCountData0.createFrequentFood(int5);
////		calorieCountData0.setFrequentFoods(frequentFoodList0);
////		Meal meal0 = new Meal();
////		calorieCountData0.addMeal(meal0);
////		EnumWrapper<CalorieCountDataElements> enumWrapper0 = new CalorieCountData.CalorieCountDataElementsWrapper();
////		CalorieCountDataElements calorieCountDataElements3 = CalorieCountDataElements.Budget;
////		boolean boolean2 = calorieCountData0.getDirty();
////		Ccde ccde0 = calorieCountData0.getDataElement(calorieCountDataElements3);
////		calorieCountData0.deserializeFrom(simpleElement1);
////		calorieCountData0.serializeTo(simpleElement1);
////		FoodList foodList1 = calorieCountData0.getFoods();
////		calorieCountData0.setFoods(foodList1);
////		int int6 = 317;
////		calorieCountData0.createFrequentFood(int6);
////		calorieCountData0.serializeTo(simpleElement1);
////		calorieCountData0.setFoods(foodList1);
////		calorieCountData0.serializeTo(simpleElement1);
////		calorieCountData0.setEntryList(entryList0);
////	}
//	
//	/*
//	 * Type not visible.
//	 */
////	@Test
////	public void wheelwebtool_FieldWriter_put_1() {
////		// I87 Branch 22 IFEQ L128;true
////		ClassWriter classWriter0 = null;
////		int int0 = 1942;
////		String string0 = "wheel/asm/Item#isEqualTo(Lwheel/asm/Item;)Z";
////		String string1 = "o*=V%6Gb_<DX?:P>(=";
////		String string2 = "klfu5d*tIOh1";
////		Object object0 = new Object();
////		wheel.asm.FieldWriter fieldWriter0 = new FieldWriter(classWriter0, int0, string0, string1, string2, object0);
////		ByteVector byteVector0 = new ByteVector();
////		fieldWriter0.put(byteVector0);
////	}
//	
//	/*
//	 * Non-visible member access.
//	 */
////	@Test
////	public void wheelwebtool_Label_visitSubroutine_1() {
////		// I168 Branch 25 IFEQ L134;false
////		wheel.asm.Label label0 = new wheel.asm.Label();
////		wheel.asm.Label label1 = label0.successor;
////		long long0 = 0L;
////		int int0 = 0;
////		int int1 = 17;
////		ClassWriter classWriter0 = new ClassWriter(int1);
////		int int2 = 124;
////		String string0 = "amS6s_WH{bs_4C_s.rO";
////		String string1 = "";
////		String string2 = "7xsS rP+2";
////		String[] stringArray0 = null;
////		boolean boolean0 = false;
////		Edge edge0 = new Edge();
////		label0.successors = edge0;
////		boolean boolean1 = false;
////		MethodWriter methodWriter0 = new MethodWriter(classWriter0, int2, string0, string1, string2, stringArray0, boolean0, boolean1);
////		ByteVector byteVector0 = classWriter0.pool;
////		int int3 = 309;
////		boolean boolean2 = false;
////		label0.put(methodWriter0, byteVector0, int3, boolean2);
////		label0.visitSubroutine(label1, long0, int0);
////	}
//	
//	/*
//	 * Non-visible constructors.
//	 */
////	@Test
////	public void wheelwebtool_ComponentCreator_checkboxGroup_1() {
////		// I53 Branch 20 IFEQ L125;true
////		String string0 = "";
////		Form form0 = new Form(string0);
////		String string1 = "import_static";
////		String string2 = "";
////		FileInput fileInput0 = new FileInput(form0, string1, string2);
////		ComponentCreator componentCreator0 = new ComponentCreator(fileInput0);
////		String string3 = "ol";
////		StringSelectModel stringSelectModel0 = new StringSelectModel();
////		String string4 = "dt";
////		ElExpression elExpression0 = new ElExpression(string4);
////		CheckboxGroup checkboxGroup0 = componentCreator0.checkboxGroup(string3, stringSelectModel0, elExpression0);
////	}
//	
//	/*
//	 * Null parameter passed to WheelAnnotationVisitor#visitAnnotation.
//	 */
//	@Test
//	public void wheelwebtool_WheelAnnotationVisitor_visit_1() {
//		// I28 Branch 1 IFLE L47;true
//		AnnotationVisitor annotationVisitor0 = null;
//		WheelAnnotationVisitor wheelAnnotationVisitor0 = new WheelAnnotationVisitor(annotationVisitor0);
//		String string0 = null;
//		String string1 = "session";
//		AnnotationVisitor annotationVisitor1 = wheelAnnotationVisitor0.visitAnnotation(string0, string1);
//		String string2 = ".s.IFJDCS";
//		String string3 = "wheel.enhance.WheelAnnotationVisitor";
//		wheelAnnotationVisitor0.visit(string2, string3);
//	}
//	
//	/*
//	 * Non-visible method call.
//	 */
////	@Test
////	public void wheelwebtool_JSONArray_toJSONObject_1() {
////		// I474 Branch 37 IFEQ L166;false
////		String string0 = "AoV9y";
////		JSONTokener jSONTokener0 = new JSONTokener(string0);
////		JSONArray jSONArray0 = new JSONArray();
////		int int0 = 0;
////		JSONObject jSONObject0 = jSONArray0.toJSONObject(jSONArray0);
////		JSONArray jSONArray1 = new JSONArray(jSONObject0);
////		int int1 = 0;
////		int int2 = (-1108);
////		boolean boolean0 = jSONArray0.getBoolean(int2);
////		int int3 = jSONArray1.optInt(int1);
////		int int4 = (-1877);
////		String string1 = "o wr%98";
////		int int5 = 82;
////		boolean boolean1 = false;
////		JSONArray jSONArray2 = jSONArray0.put(int5, boolean1);
////		boolean boolean2 = false;
////		JSONObject jSONObject1 = jSONObject0.put(string1, boolean2);
////		long long0 = jSONArray1.optLong(int4);
////		int int6 = 66;
////		int int7 = (-1706);
////		String string2 = jSONArray0.getString(int7);
////		JSONObject jSONObject2 = jSONArray0.optJSONObject(int6);
////		int int8 = 0;
////		String string3 = "";
////		String string4 = jSONArray0.optString(int8, string3);
////		int int9 = 1142;
////		boolean boolean3 = jSONArray1.isNull(int9);
////		int int10 = 2121;
////		LinkedList<Object> linkedList0 = new LinkedList<Object>();
////		JSONArray jSONArray3 = jSONArray1.put(int10, (Collection) linkedList0);
////		int int11 = (-31);
////		JSONArray jSONArray4 = jSONArray2.put(int11);
////		JSONArray jSONArray5 = jSONArray0.put((Collection) linkedList0);
////		String string5 = "Jsg@jv[u^]Co^P";
////		char char0 = '+';
////		String string6 = jSONTokener0.nextString(char0);
////		JSONObject jSONObject3 = jSONObject0.put(string5, (Collection) linkedList0);
////		int int12 = new Integer(int0);
////		HashMap<Integer, Byte> hashMap0 = new HashMap<Integer, Byte>(int9);
////		Collection<Byte> collection0 = hashMap0.values();
////		String string7 = null;
////		String string8 = jSONArray0.join(string7);
////		JSONArray jSONArray6 = jSONArray0.put((Map) hashMap0);
////		long long1 = 2034L;
////		int int13 = 3475;
////		JSONObject jSONObject4 = jSONArray1.optJSONObject(int13);
////		Long long2 = new Long(long1);
////		Long long3 = new Long(long1);
////		Long long4 = new Long(int0);
////		JSONArray jSONArray7 = new JSONArray(jSONObject0);
////		JSONArray jSONArray8 = jSONArray7.put((Collection) linkedList0);
////		int int14 = new Integer(int12);
////		int int15 = (-3244);
////		String string9 = "I~>m";
////		boolean boolean4 = jSONTokener0.skipPast(string9);
////		String string10 = "";
////		int int16 = 10;
////		String string11 = jSONArray6.optString(int16);
////		String string12 = jSONArray1.optString(int15, string10);
////		int int17 = 0;
////		boolean boolean5 = true;
////		String string13 = jSONTokener0.toString();
////		JSONArray jSONArray9 = jSONArray3.put(int17, boolean5);
////		BiFunction<Integer, Object, Byte> biFunction0 = null;
////		hashMap0.replaceAll(biFunction0);
////		JSONArray jSONArray10 = new JSONArray();
////		int int18 = (-626);
////		JSONObject jSONObject5 = jSONArray3.getJSONObject(int18);
////		int int19 = 0;
////		double double0 = Double.NaN;
////		double double1 = jSONArray10.optDouble(int19, double0);
////		int int20 = (-969);
////		Object object0 = jSONArray0.opt(int20);
////		String string14 = jSONArray6.toString();
////		JSONArray jSONArray11 = new JSONArray(jSONObject4);
////		int int21 = 35;
////		int int22 = jSONArray3.optInt(int21);
////		boolean boolean6 = false;
////		JSONArray jSONArray12 = jSONArray11.put(boolean6);
////		int int23 = 0;
////		long long5 = 203L;
////		long long6 = (-1536L);
////		long long7 = Long.min(long5, long6);
////		boolean boolean7 = false;
////		boolean boolean8 = jSONArray12.optBoolean(int23, boolean7);
////		int int24 = 150;
////		int int25 = jSONArray3.getInt(int24);
////		int int26 = (-3089);
////		boolean boolean9 = jSONArray8.optBoolean(int26);
////		int int27 = (-351);
////		Object object1 = jSONArray9.opt(int27);
////		int int28 = 7;
////		JSONArray jSONArray13 = jSONArray9.put(int28);
////		int int29 = 189;
////		HashMap<Integer, Object> hashMap1 = new HashMap<Integer, Object>();
////		JSONArray jSONArray14 = jSONArray12.put(int29, (Map) hashMap1);
////		int int30 = (-611);
////		JSONObject jSONObject6 = jSONArray1.optJSONObject(int30);
////		int int31 = 1363;
////		String string15 = jSONArray9.toString();
////		int int32 = 189;
////		JSONArray jSONArray15 = jSONArray12.getJSONArray(int32);
////		double double2 = Double.NaN;
////		String string16 = "";
////		String string17 = jSONObject0.getString(string16);
////		double double3 = jSONArray13.optDouble(int31, double2);
////		int int33 = (-1260);
////		long long8 = jSONArray14.optLong(int33);
////		int int34 = 1024;
////		Object object2 = null;
////		JSONArray jSONArray16 = jSONArray2.put(int34, object2);
////		JSONObject jSONObject7 = jSONArray5.toJSONObject(jSONArray8);
////		int int35 = 3105;
////		double double4 = 0.0;
////		double double5 = jSONArray1.optDouble(int35, double4);
////		int int36 = 0;
////		JSONObject jSONObject8 = jSONArray3.getJSONObject(int36);
////		int int37 = 82;
////		long long9 = (-4896L);
////		JSONArray jSONArray17 = jSONArray12.put(int37, long9);
////		int int38 = 71;
////		int int39 = 0;
////		String string18 = jSONArray17.toString(int39);
////		String string19 = null;
////		Long long10 = Long.getLong(string19);
////		boolean boolean10 = jSONArray17.getBoolean(int38);
////		int int40 = (-13);
////		JSONObject jSONObject9 = jSONArray3.getJSONObject(int40);
////		int int41 = 52;
////		JSONArray jSONArray18 = jSONArray5.getJSONArray(int41);
////		String string20 = jSONArray14.toString();
////		JSONArray jSONArray19 = new JSONArray();
////		int int42 = 2178;
////		int int43 = jSONArray9.getInt(int42);
////		double double6 = new Integer(int29);
////		JSONArray jSONArray20 = jSONArray9.put((Collection) linkedList0);
////		int int44 = 29;
////		int int45 = 93;
////		JSONArray jSONArray21 = jSONArray16.put(int44, int45);
////		int int46 = 0;
////		String string21 = jSONArray5.optString(int46);
////		int int47 = 4302;
////		JSONArray jSONArray22 = jSONArray17.put(int47);
////		JSONObject jSONObject10 = new JSONObject(string4);
////		int int48 = (-2792);
////		long long11 = 0L;
////		long long12 = jSONArray5.optLong(int48, long11);
////		int int49 = 1534;
////		int int50 = (-2611);
////		String string22 = jSONArray14.toString(int49, int50);
////		int int51 = 73;
////		boolean boolean11 = false;
////		boolean boolean12 = jSONArray5.optBoolean(int51, boolean11);
////		JSONObject jSONObject11 = jSONArray0.toJSONObject(jSONArray1);
////	}
//	
//	/*
//	 * NoClassDefFoundError: org/mvel/ConversionHandler
//	 */
////	@Test
////	public void wheelwebtool_ActionRegistry_needsRebuilding_1() {
////		// I35 Branch 7 IFEQ L90;true
////		ActionRegistry actionRegistry0 = new ActionRegistry();
////		String string0 = null;
////		XmlEntityRef xmlEntityRef0 = new XmlEntityRef(string0);
////		String string1 = ".{0ZSoJh";
////		String string2 = "";
////		TextArea textArea0 = new TextArea(xmlEntityRef0, string1, string2);
////		Any any0 = new Any(textArea0, string1);
////		String string3 = "9;(Y>B";
////		boolean boolean0 = actionRegistry0.needsRebuilding(any0, string3);
////	}
//	
//	/*
//	 * Wrongly classified as out-method?
//	 */
//	@Test
//	public void xbus_JournalBean_getDetailsAsTable_1() {
//		// I811 Branch 36 IFNULL L645;true
//		JournalBean journalBean0 = new JournalBean();
//		String string0 = "response";
//		journalBean0.setFunction(string0);
//		journalBean0.setMessage(string0);
//		boolean boolean0 = FileSystemHandling.shouldAllThrowIOExceptions();
//		String string1 = "4\\\\/L;t3AU)ih=vkS(Q";
//		journalBean0.setDetails(string1);
//		String string2 = journalBean0.getDetailsAsTable();
//		String string3 = journalBean0.getDetailsAsTable();
//	}
//	
//	/*
//	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
//	 */
////	@Test
////	public void ifx_framework_BankAcctTrnImgInqRs_equals_1() {
////		// I4 Branch 1 IF_ACMPNE L74;false
////		BankAcctTrnImgInqRs bankAcctTrnImgInqRs0 = new BankAcctTrnImgInqRs();
////		PipedReader pipedReader0 = null;
////		PipedWriter pipedWriter0 = new PipedWriter(pipedReader0);
////		String string0 = null;
////		int int0 = 437;
////		int int1 = 995;
////		pipedWriter0.write(string0, int0, int1);
////		String string1 = "Mml";
////		pipedWriter0.write(string1);
////		bankAcctTrnImgInqRs0.marshal((Writer) pipedWriter0);
////		Object object0 = new Object();
////		BankAcctTrnImgInqRsSequence bankAcctTrnImgInqRsSequence0 = bankAcctTrnImgInqRs0.getBankAcctTrnImgInqRsSequence();
////		bankAcctTrnImgInqRs0.setBankAcctTrnImgInqRsSequence(bankAcctTrnImgInqRsSequence0);
////		boolean boolean0 = bankAcctTrnImgInqRs0.equals(object0);
////	}
//	
//	/*
//	 * The type org.exolab.castor.xml.ValidationException cannot be resolved. It is indirectly referenced from required .class files
//	 */
////	@Test
////	public void ifx_framework_BankAcctTrnImgInqRsSequence_equals_1() {
////		// I4 Branch 1 IF_ACMPNE L74;true
////		BankAcctTrnImgInqRsSequence bankAcctTrnImgInqRsSequence0 = new BankAcctTrnImgInqRsSequence();
////		Object object0 = null;
////		String string0 = "DepAppInqRs";
////		String string1 = null;
////		MockFile mockFile0 = new MockFile(string0, string1);
////		String string2 = "net.sourceforge.ifxfv3.beans.ChksumMsgRecDescriptor$2";
////		MockPrintStream mockPrintStream0 = new MockPrintStream(mockFile0, string2);
////		boolean boolean0 = true;
////		MockPrintWriter mockPrintWriter0 = new MockPrintWriter(mockPrintStream0, boolean0);
////		bankAcctTrnImgInqRsSequence0.marshal((Writer) mockPrintWriter0);
////		Status_Type status_Type0 = bankAcctTrnImgInqRsSequence0.getStatus();
////		boolean boolean1 = bankAcctTrnImgInqRsSequence0.equals(object0);
////	}
//}
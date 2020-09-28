//package regression.objectconstruction.testgeneration.example.graphcontruction.ListBoxFigureBuilder.setStaticAttributesVisible;
//
//import de.paragon.explorer.model.AttributeModel;
//import de.paragon.explorer.model.NullObject;
//import de.paragon.explorer.model.ObjectHeaderModel;
//import de.paragon.explorer.model.ObjectModelPart;
//import de.paragon.explorer.util.ResourceBundlePurchaser;
//import de.paragon.explorer.util.StandardEnumeration;
//import java.lang.reflect.Modifier;
//import java.util.Enumeration;
//import org.apache.log4j.Logger;
//
//public final class ListBoxFigureBuilder {
//  private static final String ERROR_WHILE_UPDATING_FIGURES = "listboxfigurebuilder.error_while_updating_figures";
//  
//  private static final String ERROR_WHILE_GETTING_FIGURES = "listboxfigurebuilder.error_while_getting_figures";
//  
//  private static final String ERROR_WHILE_SETTING_ALL_ATTRIBUTES_VISIBLE = "listboxfigurebuilder.error_while_setting_all_attributes_visible";
//  
//  private static final String ERROR_WHILE_SETTING_ALL_ATTRIBUTES_UNVISIBLE = "listboxfigurebuilder.error_while_setting_all_attributes_unvisible";
//  
//  private static final String ERROR_WHILE_INIT_MODELL = "listboxfigurebuilder.error_while_init_modell";
//  
//  private static final String ERROR_WHILE_BUILDING_MODELL = "listboxfigurebuilder.error_while_building_modell";
//  
//  private static final String ERROR_WHILE_BUILDING_LISTBOXFIGURE = "listboxfigurebuilder.error_while_building_listboxfigure";
//  
//  private static final Logger logger = LoggerFactory.make();
//  
//  private static ListBoxFigureBuilder singleton;
//  
//  public static ListBoxFigureBuilder getInstance() {
//    return getSingleton();
//  }
//  
//  private static ListBoxFigureBuilder getSingleton() {
//    if (singleton == null)
//      setSingleton(new ListBoxFigureBuilder()); 
//    return singleton;
//  }
//  
//  private static void setSingleton(ListBoxFigureBuilder builder) {
//    singleton = builder;
//  }
//  
//  protected void addTextBoxFigure(TextBoxFigure teBoFi) {
//    try {
//      ((ObjectModelPart)teBoFi.getModel()).getObjectModel().getFigure().add(teBoFi);
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_listboxfigure"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_listboxfigure"));
//    } 
//  }
//  
//  protected void buildHeaderUnderline(ObjectModel objModl) {
//    TextBoxFigure teBoFig = (TextBoxFigure)objModl.getHeaderModel().getFigure();
//    int width = getFigureWidth(teBoFig);
//    int height = 1;
//    int y = 0, x = 0;
//    try {
//      objModl.getFigure().add(new FilledRectangleFigure(x, y, width, height));
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"));
//    } 
//  }
//  
//  public void buildListBoxFigure(ObjectModel objModl) {
//    ListBoxFigure liBoFi = (ListBoxFigure)objModl.getFigure();
//    getTextBoxFigureBuilder().createTextBoxFigures(objModl);
//    initializeListBoxFigure(liBoFi);
//  }
//  
//  private int computeListBoxFigureWidth(ListBoxFigure list) {
//    ObjectHeaderModel headModl = ((ObjectModel)list.getModel()).getHeaderModel();
//    StandardEnumeration attrModls = ((ObjectModel)list.getModel()).getAttributeModels();
//    int x = getFigureWidth(headModl.getFigure());
//    while (attrModls.hasMoreElements()) {
//      AttributeModel tempModel = (AttributeModel)attrModls.nextElement();
//      if (((TextBoxFigure)tempModel.getFigure()).isVisible())
//        x = Math.max(getFigureWidth(tempModel.getFigure()), x); 
//    } 
//    return x;
//  }
//  
//  public void createNewListBoxFigure(ObjectModel objModl) {
//    ExplorerFieldListBoxFigure exFiLiBoFi = new ExplorerFieldListBoxFigure();
//    objModl.setFigure(exFiLiBoFi);
//    exFiLiBoFi.setModel((Model)objModl);
//    getExplorerFigureBuilder().addListBoxFigure(exFiLiBoFi);
//  }
//  
//  public void createNewListBoxFigure(ObjectModel objModl, TextBoxFigure tbf) {
//    ExplorerFieldListBoxFigure exFiLiBoFi = new ExplorerFieldListBoxFigure();
//    if (tbf.getParent() instanceof ExplorerFieldListBoxFigure)
//      ((ExplorerFieldListBoxFigure)tbf.getParent()).addChild(exFiLiBoFi); 
//    objModl.setFigure(exFiLiBoFi);
//    exFiLiBoFi.setModel((Model)objModl);
//    getExplorerFigureBuilder().addListBoxFigure(exFiLiBoFi);
//  }
//  
//  private ExplorerFigureBuilder getExplorerFigureBuilder() {
//    return ExplorerFigureBuilder.getInstance();
//  }
//  
//  private int getFigureHeight(Figure figure) {
//    int tempHeight = 0;
//    try {
//      tempHeight = (figure.getDisplayBox().getRectangle()).height;
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"));
//    } 
//    return tempHeight;
//  }
//  
//  private int getFigureWidth(Figure figure) {
//    int tempWidth = 0;
//    try {
//      tempWidth = (figure.getDisplayBox().getRectangle()).width;
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"));
//    } 
//    return tempWidth;
//  }
//  
//  private TextBoxFigureBuilder getTextBoxFigureBuilder() {
//    return TextBoxFigureBuilder.getInstance();
//  }
//  
//  public void initializeListBoxFigure(ListBoxFigure liBoFi) {
//    StandardEnumeration figParts;
//    setListBoxFigureWidths(liBoFi, computeListBoxFigureWidth(liBoFi));
//    try {
//      figParts = liBoFi.getFigures();
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_init_modell"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_init_modell"));
//      figParts = null;
//    } 
//    setFigureHeight(liBoFi, 0);
//    if (figParts != null)
//      while (figParts.hasMoreElements()) {
//        RectangleFigure tempFig = (RectangleFigure)figParts.nextElement();
//        if (tempFig.isVisible()) {
//          try {
//            tempFig.moveBy(0, getFigureHeight(liBoFi));
//          } catch (Exception ex) {
//            logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_init_modell"), ex);
//            Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_init_modell"));
//          } 
//          setFigureHeight(liBoFi, getFigureHeight(liBoFi) + getFigureHeight(tempFig));
//        } 
//      }  
//  }
//  
//  public void setAllAttributesUnvisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setAllAttributesVisible(false);
//    StandardEnumeration parts = objModl.getAttributeModels();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        ((TextBoxFigure)attrModl.getFigure()).setUnvisible();
//      } 
//      parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"));
//    } 
//  }
//  
//  public void setAllAttributesVisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setAllAttributesVisible(true);
//    StandardEnumeration parts = objModl.getAttributeModels();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        if (objModl.getObjectViewManager().shouldBeVisible(attrModl))
//          ((TextBoxFigure)attrModl.getFigure()).setVisible(); 
//      } 
//      parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"));
//    } 
//  }
//  
//  public void setAllUnexploredAttributesUnvisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setUnexploredAttributesVisible(false);
//    StandardEnumeration parts = objModl.getAttributeModels();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        if (attrModl.getConnectionModel() == null)
//          ((TextBoxFigure)attrModl.getFigure()).setUnvisible(); 
//      } 
//      parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"));
//    } 
//  }
//  
//  public void setAllUnexploredAttributesVisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setUnexploredAttributesVisible(true);
//    StandardEnumeration parts = objModl.getAttributeModels();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        if (objModl.getObjectViewManager().shouldBeVisible(attrModl))
//          ((TextBoxFigure)attrModl.getFigure()).setVisible(); 
//      } 
//      parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"));
//    } 
//  }
//  
//  private void setFigureHeight(Figure figure, int y) {
//    try {
//      (figure.getDisplayBox().getRectangle()).height = y;
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"));
//    } 
//  }
//  
//  private void setFigureWidth(Figure figure, int x) {
//    try {
//      (figure.getDisplayBox().getRectangle()).width = x;
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_building_modell"));
//    } 
//  }
//  
//  private void setListBoxFigureWidths(ListBoxFigure list, int width) {
//    StandardEnumeration figParts;
//    try {
//      figParts = list.getFigures();
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_getting_figures"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_getting_figures"));
//      figParts = null;
//    } 
//    setFigureWidth(list, width);
//    if (figParts != null)
//      while (figParts.hasMoreElements())
//        setFigureWidth((Figure)figParts.nextElement(), width);  
//  }
//  
//  public void setNullAttributesUnvisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setNullAttributesVisible(false);
//    StandardEnumeration parts = objModl.getAttributeModels();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        if (NullObject.isNullObject(attrModl.getValue()))
//          ((TextBoxFigure)attrModl.getFigure()).setUnvisible(); 
//      } 
//      parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"));
//    } 
//  }
//  
//  public void setNullAttributesVisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setNullAttributesVisible(true);
//    Enumeration<?> parts = objModl.getAttributeModels().getVector().elements();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        if (objModl.getObjectViewManager().shouldBeVisible(attrModl))
//          ((TextBoxFigure)attrModl.getFigure()).setVisible(); 
//      } 
//      StandardEnumeration<RectangleFigure> standardEnumeration = liBoFi.getFigures();
//      while (standardEnumeration.hasMoreElements()) {
//        RectangleFigure liBoFiPart = standardEnumeration.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      standardEnumeration = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (standardEnumeration.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)standardEnumeration.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"));
//    } 
//  }
//  
//  public void setSingleAttributeUnvisible(TextBoxFigure texBoFi) {
//    ListBoxFigure liBoFi = (ListBoxFigure)texBoFi.getParent();
//    try {
//      texBoFi.setUnvisible();
//      StandardEnumeration parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"));
//    } 
//  }
//  
//  public void setSingleAttributeVisible(TextBoxFigure texBoFi) {
//    ListBoxFigure liBoFi = (ListBoxFigure)texBoFi.getParent();
//    try {
//      texBoFi.setVisible();
//      StandardEnumeration parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"));
//    } 
//  }
//  
//  public void setStaticAttributesUnvisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setStaticAttributesVisible(false);
//    StandardEnumeration parts = objModl.getAttributeModels();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        if (Modifier.isStatic(attrModl.getModifiers()))
//          ((TextBoxFigure)attrModl.getFigure()).setUnvisible(); 
//      } 
//      parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_unvisible"));
//    } 
//  }
//  
//  public void setStaticAttributesVisible(ListBoxFigure liBoFi) {
//    ObjectModel objModl = (ObjectModel)liBoFi.getModel();
//    objModl.getObjectViewManager().setStaticAttributesVisible(true);
//    StandardEnumeration parts = objModl.getAttributeModels();
//    try {
//      while (parts.hasMoreElements()) {
//        AttributeModel attrModl = (AttributeModel)parts.nextElement();
//        if (objModl.getObjectViewManager().shouldBeVisible(attrModl))
//          ((TextBoxFigure)attrModl.getFigure()).setVisible(); 
//      } 
//      parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_setting_all_attributes_visible"));
//    } 
//  }
//  
//  public void updateObject(ListBoxFigure liBoFi) {
//    try {
//      getTextBoxFigureBuilder().updateHeader((ObjectModel)liBoFi.getModel());
//      getTextBoxFigureBuilder().updateAttributes((ObjectModel)liBoFi.getModel());
//      StandardEnumeration parts = liBoFi.getFigures();
//      while (parts.hasMoreElements()) {
//        RectangleFigure liBoFiPart = (RectangleFigure)parts.nextElement();
//        (liBoFiPart.getBounds()).width = 0;
//        (liBoFiPart.getBounds()).x = (liBoFi.getBounds()).x;
//        (liBoFiPart.getBounds()).y = (liBoFi.getBounds()).y;
//      } 
//      TextBoxFigure teBoFi = (TextBoxFigure)((ObjectModel)liBoFi.getModel()).getHeaderModel().getFigure();
//      teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//      teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      parts = ((ObjectModel)liBoFi.getModel()).getAttributeModels();
//      while (parts.hasMoreElements()) {
//        teBoFi = (TextBoxFigure)((AttributeModel)parts.nextElement()).getFigure();
//        teBoFi.setBounds(getTextBoxFigureBuilder().computeDisplayBox(teBoFi));
//        teBoFi.moveBy((liBoFi.getBounds()).x, (liBoFi.getBounds()).y);
//      } 
//    } catch (Exception ex) {
//      logger.error(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_updating_figures"), ex);
//      Warning.showWarning(ResourceBundlePurchaser.getMessage("listboxfigurebuilder.error_while_updating_figures"));
//    } 
//  }
//}

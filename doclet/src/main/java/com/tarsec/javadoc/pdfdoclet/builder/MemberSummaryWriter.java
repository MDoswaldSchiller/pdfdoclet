package com.tarsec.javadoc.pdfdoclet.builder;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.tarsec.javadoc.pdfdoclet.Configuration;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import jdk.javadoc.doclet.DocletEnvironment;

/**
 *
 * @author mdo
 */
public class MemberSummaryWriter extends AbstractSummaryWriter
{
  public MemberSummaryWriter(DocletEnvironment environment, Document pdfDocument)
  {
    super(environment, pdfDocument);
  }

  public void appendMemberTable(TypeElement type) throws DocumentException
  {
    List<VariableElement> fields = ElementFilter.fieldsIn(type.getEnclosedElements());
    if (!fields.isEmpty()) {
      FieldSummaryWriter fieldSummaryWriter = new FieldSummaryWriter(environment, pdfDocument);
      fieldSummaryWriter.addFieldsSummary(type, fields);
    }
    
    if (Configuration.isShowInheritedSummaryActive()) {
      InheritedFieldsWriter inheritedFieldsWriter = new InheritedFieldsWriter(environment, pdfDocument);
      inheritedFieldsWriter.addInheritedFieldsInfo(type);
    }
    
    List<ExecutableElement> constructors = ElementFilter.constructorsIn(type.getEnclosedElements());
    if (!constructors.isEmpty()) {
      ConstructorSummaryWriter constructorSummaryWriter = new ConstructorSummaryWriter(environment, pdfDocument);
      constructorSummaryWriter.addConstructorsSummary(type, constructors);
    }

    List<ExecutableElement> methods = ElementFilter.methodsIn(type.getEnclosedElements());
    if (!methods.isEmpty()) {
      MethodSummaryWriter methodSummaryWriter = new MethodSummaryWriter(environment, pdfDocument);
      methodSummaryWriter.addMethodsSummary(type, methods);
    }
    
    if (Configuration.isShowInheritedSummaryActive()) {
      InheritedMethodsWriter inheritedMethodsWriter = new InheritedMethodsWriter(environment, pdfDocument);
      inheritedMethodsWriter.addInheritedMethodsInfo(type);
    }
  }
}

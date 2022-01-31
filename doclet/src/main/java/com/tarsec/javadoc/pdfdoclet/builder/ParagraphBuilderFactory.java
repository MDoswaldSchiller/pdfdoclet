package com.tarsec.javadoc.pdfdoclet.builder;

import javax.lang.model.element.TypeElement;
import jdk.javadoc.doclet.DocletEnvironment;

/**
 *
 * @author mdo
 */
public class ParagraphBuilderFactory
{
  private final DocletEnvironment environment;

  public ParagraphBuilderFactory(DocletEnvironment environment)
  {
    this.environment = environment;
  }
  
  
  public ClassParagraph createClassParagraph(TypeElement classType)
  {
    return new ClassParagraph(environment, classType);
  }
}

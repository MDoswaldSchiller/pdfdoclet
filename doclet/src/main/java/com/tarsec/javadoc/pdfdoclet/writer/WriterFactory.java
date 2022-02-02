package com.tarsec.javadoc.pdfdoclet.writer;

import javax.lang.model.element.TypeElement;
import jdk.javadoc.doclet.DocletEnvironment;

/**
 *
 * @author mdo
 */
public class WriterFactory
{
  private final DocletEnvironment environment;

  public WriterFactory(DocletEnvironment environment)
  {
    this.environment = environment;
  }
  
  
  public ClassWriter createClassParagraph(TypeElement classType)
  {
    return new ClassWriter(environment, classType);
  }
}

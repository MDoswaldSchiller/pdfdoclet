/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/filter/FilteredClassDoc.java,v 1.1 2007/07/18 22:15:08 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.filter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.tarsec.javadoc.pdfdoclet.Configuration;

/**
 * Wraps a Javadoc RootDoc object to allow to filter out packages / classes /
 * methods transparently.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class FilteredClassDoc implements ClassDoc
{

  /**
   * Wrapped ClassDoc object reference.
   */
  private ClassDoc classDoc = null;

  /**
   * Default constructor.
   */
  public FilteredClassDoc()
  {
  }

  /**
   * Creates an instance of this class which wraps the given ClassDoc object.
   *
   * @param wrapped The ClassDoc to be wrapped.
   */
  public FilteredClassDoc(ClassDoc wrapped)
  {
    this.classDoc = wrapped;
  }

  /**
   * Allows to set the ClassDoc object that whould be wrapped by this class.
   *
   * @param wrapped The ClassDoc to be wrapped.
   */
  public void setClassDoc(ClassDoc wrapped)
  {
    this.classDoc = wrapped;
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#definesSerializableFields()
   */
  @Override
  public boolean definesSerializableFields()
  {
    return classDoc.definesSerializableFields();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#isAbstract()
   */
  @Override
  public boolean isAbstract()
  {
    return classDoc.isAbstract();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#isExternalizable()
   */
  @Override
  public boolean isExternalizable()
  {
    return classDoc.isExternalizable();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#isSerializable()
   */
  @Override
  public boolean isSerializable()
  {
    return classDoc.isSerializable();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#superclass()
   */
  @Override
  public ClassDoc superclass()
  {
    return classDoc.superclass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#importedClasses()
   */
  @Override
  public ClassDoc[] importedClasses()
  {
    // TODO: Check if these classes must be filtered as well
    return classDoc.importedClasses();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#innerClasses()
   */
  @Override
  public ClassDoc[] innerClasses()
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of fields
      return classDoc.innerClasses();
    }
    return Filter.createClassesList(classDoc.innerClasses());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#interfaces()
   */
  @Override
  public ClassDoc[] interfaces()
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of fields
      return classDoc.interfaces();
    }
    return Filter.createClassesList(classDoc.interfaces());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#subclassOf(com.sun.javadoc.ClassDoc)
   */
  @Override
  public boolean subclassOf(ClassDoc arg0)
  {
    return classDoc.subclassOf(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#innerClasses(boolean)
   */
  @Override
  public ClassDoc[] innerClasses(boolean arg0)
  {
    // TODO: Filter inner classes as well
    return classDoc.innerClasses(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#constructors()
   */
  @Override
  public ConstructorDoc[] constructors()
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of fields
      return classDoc.constructors();
    }
    return Filter.createConstructorList(classDoc.constructors());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#constructors(boolean)
   */
  @Override
  public ConstructorDoc[] constructors(boolean arg0)
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of fields
      return classDoc.constructors(arg0);
    }
    return Filter.createConstructorList(classDoc.constructors(arg0));
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#fields()
   */
  @Override
  public FieldDoc[] fields()
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of fields
      return classDoc.fields();
    }
    return Filter.createFieldList(classDoc.fields());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#serializableFields()
   */
  @Override
  public FieldDoc[] serializableFields()
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of fields
      return classDoc.serializableFields();
    }
    return Filter.createFieldList(classDoc.serializableFields());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#fields(boolean)
   */
  @Override
  public FieldDoc[] fields(boolean arg0)
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of fields
      return classDoc.fields(arg0);
    }
    return Filter.createFieldList(classDoc.fields(arg0));
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#methods()
   */
  @Override
  public MethodDoc[] methods()
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of methods
      return classDoc.methods();
    }
    return Filter.createMethodList(classDoc.methods());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#serializationMethods()
   */
  @Override
  public MethodDoc[] serializationMethods()
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of methods
      return classDoc.serializationMethods();
    }
    return Filter.createMethodList(classDoc.serializationMethods());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#methods(boolean)
   */
  @Override
  public MethodDoc[] methods(boolean arg0)
  {
    if (!Configuration.isFilterActive()) {
      // If no filtering is active, return the original list of methods
      return classDoc.methods(arg0);
    }
    return Filter.createMethodList(classDoc.methods(arg0));
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#importedPackages()
   */
  @Override
  public PackageDoc[] importedPackages()
  {
    // Packages need not to be filtered
    return classDoc.importedPackages();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#findClass(java.lang.String)
   */
  @Override
  public ClassDoc findClass(String arg0)
  {
    return classDoc.findClass(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#modifierSpecifier()
   */
  @Override
  public int modifierSpecifier()
  {
    return classDoc.modifierSpecifier();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isFinal()
   */
  @Override
  public boolean isFinal()
  {
    return classDoc.isFinal();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isPackagePrivate()
   */
  @Override
  public boolean isPackagePrivate()
  {
    return classDoc.isPackagePrivate();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isPrivate()
   */
  @Override
  public boolean isPrivate()
  {
    return classDoc.isPrivate();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isProtected()
   */
  @Override
  public boolean isProtected()
  {
    return classDoc.isProtected();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isPublic()
   */
  @Override
  public boolean isPublic()
  {
    return classDoc.isPublic();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isStatic()
   */
  @Override
  public boolean isStatic()
  {
    return classDoc.isStatic();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#containingClass()
   */
  @Override
  public ClassDoc containingClass()
  {
    return classDoc.containingClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#containingPackage()
   */
  @Override
  public PackageDoc containingPackage()
  {
    return classDoc.containingPackage();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#modifiers()
   */
  @Override
  public String modifiers()
  {
    return classDoc.modifiers();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#qualifiedName()
   */
  @Override
  public String qualifiedName()
  {
    return classDoc.qualifiedName();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Type#asClassDoc()
   */
  @Override
  public ClassDoc asClassDoc()
  {
    return classDoc.asClassDoc();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Type#dimension()
   */
  @Override
  public String dimension()
  {
    return classDoc.dimension();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Type#qualifiedTypeName()
   */
  @Override
  public String qualifiedTypeName()
  {
    return classDoc.qualifiedTypeName();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Type#typeName()
   */
  @Override
  public String typeName()
  {
    return classDoc.typeName();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isClass()
   */
  @Override
  public boolean isClass()
  {
    return classDoc.isClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isConstructor()
   */
  @Override
  public boolean isConstructor()
  {
    return classDoc.isConstructor();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isError()
   */
  @Override
  public boolean isError()
  {
    return classDoc.isError();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isException()
   */
  @Override
  public boolean isException()
  {
    return classDoc.isException();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isField()
   */
  @Override
  public boolean isField()
  {
    return classDoc.isField();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isIncluded()
   */
  @Override
  public boolean isIncluded()
  {
    return classDoc.isIncluded();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isInterface()
   */
  @Override
  public boolean isInterface()
  {
    return classDoc.isInterface();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isMethod()
   */
  @Override
  public boolean isMethod()
  {
    return classDoc.isMethod();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isOrdinaryClass()
   */
  @Override
  public boolean isOrdinaryClass()
  {
    return classDoc.isOrdinaryClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#seeTags()
   */
  @Override
  public SeeTag[] seeTags()
  {
    return classDoc.seeTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#position()
   */
  @Override
  public SourcePosition position()
  {
    return classDoc.position();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#firstSentenceTags()
   */
  @Override
  public Tag[] firstSentenceTags()
  {
    return classDoc.firstSentenceTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#inlineTags()
   */
  @Override
  public Tag[] inlineTags()
  {
    return classDoc.inlineTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags()
   */
  @Override
  public Tag[] tags()
  {
    return classDoc.tags();
  }

  /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Object arg0)
  {
    return classDoc.compareTo(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#commentText()
   */
  @Override
  public String commentText()
  {
    return classDoc.commentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#getRawCommentText()
   */
  @Override
  public String getRawCommentText()
  {
    return classDoc.getRawCommentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#name()
   */
  @Override
  public String name()
  {
    return classDoc.name();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
   */
  @Override
  public void setRawCommentText(String arg0)
  {
    classDoc.setRawCommentText(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags(java.lang.String)
   */
  @Override
  public Tag[] tags(String arg0)
  {
    return classDoc.tags(arg0);
  }

}

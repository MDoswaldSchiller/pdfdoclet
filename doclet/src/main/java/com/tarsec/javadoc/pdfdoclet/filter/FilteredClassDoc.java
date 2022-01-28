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
 * Wraps a Javadoc RootDoc object to allow to filter out
 * packages / classes / methods transparently.
 * 
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class FilteredClassDoc implements ClassDoc {

    /** Wrapped ClassDoc object reference. */
    private ClassDoc classDoc = null;

    /**
     * Default constructor.
     */
    public FilteredClassDoc() {
    }
    
    /**
     * Creates an instance of this class which wraps
     * the given ClassDoc object.
     * 
     * @param wrapped The ClassDoc to be wrapped.
     */
    public FilteredClassDoc(ClassDoc wrapped) {
        this.classDoc = wrapped;
    }
    
    /**
     * Allows to set the ClassDoc object that whould
     * be wrapped by this class.
     * 
     * @param wrapped The ClassDoc to be wrapped.
     */
    public void setClassDoc(ClassDoc wrapped) {
        this.classDoc = wrapped;
    }
    
    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#definesSerializableFields()
     */
    public boolean definesSerializableFields() {
        return classDoc.definesSerializableFields();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#isAbstract()
     */
    public boolean isAbstract() {
        return classDoc.isAbstract();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#isExternalizable()
     */
    public boolean isExternalizable() {
        return classDoc.isExternalizable();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#isSerializable()
     */
    public boolean isSerializable() {
        return classDoc.isSerializable();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#superclass()
     */
    public ClassDoc superclass() {
        return classDoc.superclass();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#importedClasses()
     */
    public ClassDoc[] importedClasses() {
        // TODO: Check if these classes must be filtered as well
        return classDoc.importedClasses();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#innerClasses()
     */
    public ClassDoc[] innerClasses() {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of fields
            return classDoc.innerClasses();
        }
        return Filter.createClassesList(classDoc.innerClasses());
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#interfaces()
     */
    public ClassDoc[] interfaces() {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of fields
            return classDoc.interfaces();
        }
        return Filter.createClassesList(classDoc.interfaces());
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#subclassOf(com.sun.javadoc.ClassDoc)
     */
    public boolean subclassOf(ClassDoc arg0) {
        return classDoc.subclassOf(arg0);
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#innerClasses(boolean)
     */
    public ClassDoc[] innerClasses(boolean arg0) {
        // TODO: Filter inner classes as well
        return classDoc.innerClasses(arg0);
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#constructors()
     */
    public ConstructorDoc[] constructors() {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of fields
            return classDoc.constructors();
        }
        return Filter.createConstructorList(classDoc.constructors());
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#constructors(boolean)
     */
    public ConstructorDoc[] constructors(boolean arg0) {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of fields
            return classDoc.constructors(arg0);
        }
        return Filter.createConstructorList(classDoc.constructors(arg0));
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#fields()
     */
    public FieldDoc[] fields() {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of fields
            return classDoc.fields();
        }
        return Filter.createFieldList(classDoc.fields());
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#serializableFields()
     */
    public FieldDoc[] serializableFields() {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of fields
            return classDoc.serializableFields();
        }
        return Filter.createFieldList(classDoc.serializableFields());
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#fields(boolean)
     */
    public FieldDoc[] fields(boolean arg0) {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of fields
            return classDoc.fields(arg0);
        }
        return Filter.createFieldList(classDoc.fields(arg0));
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#methods()
     */
    public MethodDoc[] methods() {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of methods
            return classDoc.methods();
        }
        return Filter.createMethodList(classDoc.methods());
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#serializationMethods()
     */
    public MethodDoc[] serializationMethods() {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of methods
            return classDoc.serializationMethods();
        }
        return Filter.createMethodList(classDoc.serializationMethods());
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#methods(boolean)
     */
    public MethodDoc[] methods(boolean arg0) {
        if(!Configuration.isFilterActive()) {
            // If no filtering is active, return the original list of methods
            return classDoc.methods(arg0);
        }
        return Filter.createMethodList(classDoc.methods(arg0));
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#importedPackages()
     */
    public PackageDoc[] importedPackages() {
        // Packages need not to be filtered
        return classDoc.importedPackages();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ClassDoc#findClass(java.lang.String)
     */
    public ClassDoc findClass(String arg0) {
        return classDoc.findClass(arg0);
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#modifierSpecifier()
     */
    public int modifierSpecifier() {
        return classDoc.modifierSpecifier();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isFinal()
     */
    public boolean isFinal() {
        return classDoc.isFinal();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isPackagePrivate()
     */
    public boolean isPackagePrivate() {
        return classDoc.isPackagePrivate();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isPrivate()
     */
    public boolean isPrivate() {
        return classDoc.isPrivate();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isProtected()
     */
    public boolean isProtected() {
        return classDoc.isProtected();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isPublic()
     */
    public boolean isPublic() {
        return classDoc.isPublic();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#isStatic()
     */
    public boolean isStatic() {
        return classDoc.isStatic();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#containingClass()
     */
    public ClassDoc containingClass() {
        return classDoc.containingClass();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#containingPackage()
     */
    public PackageDoc containingPackage() {
        return classDoc.containingPackage();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#modifiers()
     */
    public String modifiers() {
        return classDoc.modifiers();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.ProgramElementDoc#qualifiedName()
     */
    public String qualifiedName() {
        return classDoc.qualifiedName();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Type#asClassDoc()
     */
    public ClassDoc asClassDoc() {
        return classDoc.asClassDoc();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Type#dimension()
     */
    public String dimension() {
        return classDoc.dimension();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Type#qualifiedTypeName()
     */
    public String qualifiedTypeName() {
        return classDoc.qualifiedTypeName();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Type#typeName()
     */
    public String typeName() {
        return classDoc.typeName();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isClass()
     */
    public boolean isClass() {
        return classDoc.isClass();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isConstructor()
     */
    public boolean isConstructor() {
        return classDoc.isConstructor();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isError()
     */
    public boolean isError() {
        return classDoc.isError();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isException()
     */
    public boolean isException() {
        return classDoc.isException();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isField()
     */
    public boolean isField() {
        return classDoc.isField();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isIncluded()
     */
    public boolean isIncluded() {
        return classDoc.isIncluded();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isInterface()
     */
    public boolean isInterface() {
        return classDoc.isInterface();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isMethod()
     */
    public boolean isMethod() {
        return classDoc.isMethod();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isOrdinaryClass()
     */
    public boolean isOrdinaryClass() {
        return classDoc.isOrdinaryClass();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#seeTags()
     */
    public SeeTag[] seeTags() {
        return classDoc.seeTags();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#position()
     */
    public SourcePosition position() {
        return classDoc.position();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#firstSentenceTags()
     */
    public Tag[] firstSentenceTags() {
        return classDoc.firstSentenceTags();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#inlineTags()
     */
    public Tag[] inlineTags() {
        return classDoc.inlineTags();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags()
     */
    public Tag[] tags() {
        return classDoc.tags();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        return classDoc.compareTo(arg0);
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#commentText()
     */
    public String commentText() {
        return classDoc.commentText();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#getRawCommentText()
     */
    public String getRawCommentText() {
        return classDoc.getRawCommentText();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#name()
     */
    public String name() {
        return classDoc.name();
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
     */
    public void setRawCommentText(String arg0) {
        classDoc.setRawCommentText(arg0);
    }

    /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags(java.lang.String)
     */
    public Tag[] tags(String arg0) {
        return classDoc.tags(arg0);
    }

}

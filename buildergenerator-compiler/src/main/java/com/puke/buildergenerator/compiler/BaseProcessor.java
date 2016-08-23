package com.puke.buildergenerator.compiler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author zijiao
 * @version 16/8/22
 */
public abstract class BaseProcessor extends AbstractProcessor {

    protected Messager mMessageer;
    protected Filer mFiler;
    protected Elements mElements;
    protected Types mTypes;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessageer = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mTypes = processingEnv.getTypeUtils();
    }

    public void print(Diagnostic.Kind kind, String message, Object... params) {
        if (mMessageer != null) {
            mMessageer.printMessage(kind, String.format(message, params));
        }
    }

    public void info(String message, Object... params) {
        print(Diagnostic.Kind.NOTE, message, params);
    }

    public void error(String message, Object... params) {
        print(Diagnostic.Kind.ERROR, message, params);
    }

}

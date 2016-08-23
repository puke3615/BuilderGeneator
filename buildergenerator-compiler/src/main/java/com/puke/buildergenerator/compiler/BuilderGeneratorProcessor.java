package com.puke.buildergenerator.compiler;

import com.google.auto.service.AutoService;
import com.puke.buildergenerator.api.Builder;
import com.puke.buildergenerator.api.Exceptions;
import com.puke.buildergenerator.api.Item;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author zijiao
 * @version 16/8/22
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class BuilderGeneratorProcessor extends BaseProcessor {

    private static final String CLASSNAME_POSTFIX = "Builder";
    private static final List<String> annotations = Arrays.asList(
            Builder.class.getName()
    );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set;
        if (roundEnv != null
                && (set = roundEnv.getElementsAnnotatedWith(Builder.class)) != null
                && set.size() > 0) {
            for (Element element : set) {
                handleElement(element);
            }
        }
        return false;
    }

    //校验合法性
    private void validateElement(Element element) {
        if (element == null) {
            Exceptions.apt("the element is null.");
        }
        ElementKind kind = element.getKind();
        if (kind != ElementKind.CONSTRUCTOR) {
            Exceptions.apt("the element annotated must be a constructor.");
        }
        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            Exceptions.apt("the modifier of constructor should not is private.");
        }
    }

    private void handleElement(Element element) {
        validateElement(element);
        BuilderInfo builderInfo = collectBuilderInfo(element);
        createJavaFile(element, builderInfo);
    }

    //收集注解信息
    private BuilderInfo collectBuilderInfo(Element element) {
        BuilderInfo builderInfo = new BuilderInfo();
        Builder builder = element.getAnnotation(Builder.class);
        if (builder != null) {
            builderInfo.prefix = builder.value();
        }
        StringBuilder values = new StringBuilder();
        info("============================");
        if (element instanceof ExecutableElement) {
            ExecutableElement ee = (ExecutableElement) element;
            for (VariableElement ve : ee.getParameters()) {
                if (values.length() > 0) {
                    values.append(", ");
                }
                Item item = ve.getAnnotation(Item.class);
                if (item == null) {
                    values.append(getDefaultValue(ve));
                    continue;
                }
                BuilderInfo.Item itemInfo = new BuilderInfo.Item();
                String param = ve.getSimpleName().toString();
                itemInfo.type = ve.asType();
                itemInfo.property = "".equals(item.value()) ? param : item.value();
                values.append(itemInfo.property);
                builderInfo.add(itemInfo);
            }
        }
        builderInfo.values = values;
        info("============================");
        return builderInfo;
    }

    private static String getDefaultValue(VariableElement ve) {
        String type = ve.asType().toString();
        if ("int".equals(type)
                || "byte".equals(type)
                || "float".equals(type)
                || "double".equals(type)
                || "short".equals(type)
                || "long".equals(type)) {
            return "0";
        } else if ("java.lang.String".equals(type)) {
            return "\"\"";
        } else if ("boolean".equals(type)) {
            return "false";
        } else {
            return "null";
        }
    }


    //创建文件
    private void createJavaFile(Element element, BuilderInfo builderInfo) {
        if (builderInfo == null || element == null) {
            return;
        }
        final String packageName = mElements.getPackageOf(element).toString();
        final Element targetTypeElement = element.getEnclosingElement();
        final String className = targetTypeElement.getSimpleName() + CLASSNAME_POSTFIX;
        final ClassName builderClassName = ClassName.get(packageName, className);

        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> methodSpecs = new ArrayList<>();
        final String methodPrefix = builderInfo.prefix;
        if (builderInfo.builders != null) {
            for (BuilderInfo.Item item : builderInfo.builders) {
                //添加属性
                TypeName filedTypeName = ClassName.get(item.type);
                FieldSpec fieldSpec = FieldSpec.builder(filedTypeName, item.property, Modifier.PRIVATE)
                        .build();
                fieldSpecs.add(fieldSpec);

                //添加方法
                String methodName = item.property;
                if (methodPrefix != null && !"".equals(methodPrefix)) {
                    String first = item.property.substring(0, 1);
                    methodName = methodPrefix + item.property.replaceFirst(first, first.toUpperCase());
                }
                MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .addModifiers(Modifier.FINAL)
                        .addParameter(TypeName.get(item.type), item.property)
                        .addStatement("this.$L = $L", item.property, item.property)
                        .addStatement("return this")
                        .returns(builderClassName)
                        .build();
                methodSpecs.add(methodSpec);
            }
        }

        MethodSpec buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .returns(TypeName.get(targetTypeElement.asType()))
                .addStatement("return new $T($L)", targetTypeElement, builderInfo.values.toString())
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addFields(fieldSpecs)
                .addMethods(methodSpecs)
                .addMethod(buildMethod)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .addFileComment("this is a generated file.")
                .skipJavaLangImports(true)
                .build();

        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
            Exceptions.apt("the builder file is created failure.");
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(annotations);
    }
}

package com.dhl.fin.api.common.mybatisgenerator;

import org.mybatis.generator.api.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.XmlFileMergerJaxp;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * @author CuiJianbo
 * @since 2023/6/6
 */
public class DhlMybatisGenerator {

    private Configuration configuration;
    private ShellCallback shellCallback;
    private List<GeneratedJavaFile> generatedJavaFiles = new ArrayList();
    private List<GeneratedXmlFile> generatedXmlFiles = new ArrayList();
    private List<GeneratedKotlinFile> generatedKotlinFiles = new ArrayList();
    private List<String> warnings;
    private Set<String> projects = new HashSet();

    public DhlMybatisGenerator(Configuration configuration, ShellCallback shellCallback, List<String> warnings) throws InvalidConfigurationException {
        if (configuration == null) {
            throw new IllegalArgumentException(Messages.getString("RuntimeError.2"));
        } else {
            this.configuration = configuration;
            if (shellCallback == null) {
                this.shellCallback = new DefaultShellCallback(false);
            } else {
                this.shellCallback = shellCallback;
            }

            if (warnings == null) {
                this.warnings = new ArrayList();
            } else {
                this.warnings = warnings;
            }

            this.configuration.validate();
        }
    }

    public void generate(ProgressCallback callback) throws SQLException, IOException, InterruptedException {
        this.generate(callback, (Set) null, (Set) null, true);
    }

    public void generate(ProgressCallback callback, Set<String> contextIds) throws SQLException, IOException, InterruptedException {
        this.generate(callback, contextIds, (Set) null, true);
    }

    public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames) throws SQLException, IOException, InterruptedException {
        this.generate(callback, contextIds, fullyQualifiedTableNames, true);
    }

    public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames, boolean writeFiles) throws SQLException, IOException, InterruptedException {
        if (callback == null) {
            callback = new NullProgressCallback();
        }

        this.generatedJavaFiles.clear();
        this.generatedXmlFiles.clear();
        ObjectFactory.reset();
        RootClassInfo.reset();
        Object contextsToRun;
        if (contextIds != null && !contextIds.isEmpty()) {
            contextsToRun = new ArrayList();
            Iterator var6 = this.configuration.getContexts().iterator();

            while (var6.hasNext()) {
                Context context = (Context) var6.next();
                if (contextIds.contains(context.getId())) {
                    ((List) contextsToRun).add(context);
                }
            }
        } else {
            contextsToRun = this.configuration.getContexts();
        }

        if (!this.configuration.getClassPathEntries().isEmpty()) {
            ClassLoader classLoader = ClassloaderUtility.getCustomClassloader(this.configuration.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }

        int totalSteps = 0;

        Context context;
        Iterator var11;
        for (var11 = ((List) contextsToRun).iterator(); var11.hasNext(); totalSteps += context.getIntrospectionSteps()) {
            context = (Context) var11.next();
        }

        ((ProgressCallback) callback).introspectionStarted(totalSteps);
        var11 = ((List) contextsToRun).iterator();

        while (var11.hasNext()) {
            context = (Context) var11.next();
            context.introspectTables((ProgressCallback) callback, this.warnings, fullyQualifiedTableNames);
        }

        totalSteps = 0;

        for (var11 = ((List) contextsToRun).iterator(); var11.hasNext(); totalSteps += context.getGenerationSteps()) {
            context = (Context) var11.next();
        }

        ((ProgressCallback) callback).generationStarted(totalSteps);
        var11 = ((List) contextsToRun).iterator();

        while (var11.hasNext()) {
            context = (Context) var11.next();
            context.generateFiles((ProgressCallback) callback, this.generatedJavaFiles, this.generatedXmlFiles, this.generatedKotlinFiles, this.warnings);
        }

        if (writeFiles) {
            ((ProgressCallback) callback).saveStarted(this.generatedXmlFiles.size() + this.generatedJavaFiles.size());
            var11 = this.generatedXmlFiles.iterator();

            while (var11.hasNext()) {
                GeneratedXmlFile gxf = (GeneratedXmlFile) var11.next();
                this.projects.add(gxf.getTargetProject());
                this.writeGeneratedXmlFile(gxf, (ProgressCallback) callback);
            }

            var11 = this.generatedJavaFiles.iterator();

            while (var11.hasNext()) {
                GeneratedJavaFile gjf = (GeneratedJavaFile) var11.next();
                this.projects.add(gjf.getTargetProject());
                this.writeGeneratedJavaFile(gjf, (ProgressCallback) callback);
            }

            var11 = this.generatedKotlinFiles.iterator();

            while (var11.hasNext()) {
                GeneratedKotlinFile gkf = (GeneratedKotlinFile) var11.next();
                this.projects.add(gkf.getTargetProject());
                this.writeGeneratedKotlinFile(gkf, (ProgressCallback) callback);
            }

            var11 = this.projects.iterator();

            while (var11.hasNext()) {
                String project = (String) var11.next();
                this.shellCallback.refreshProject(project);
            }
        }

        ((ProgressCallback) callback).done();
    }

    private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback) throws InterruptedException, IOException {
        try {
            File directory = this.shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
            File targetFile = new File(directory, gjf.getFileName());
            String source;
            if (targetFile.exists()) {
                if (this.shellCallback.isMergeSupported()) {
                    source = this.shellCallback.mergeJavaFile(gjf.getFormattedContent(), targetFile, MergeConstants.getOldElementTags(), gjf.getFileEncoding());
                } else if (this.shellCallback.isOverwriteEnabled()) {
                    source = gjf.getFormattedContent();
                    this.warnings.add(Messages.getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gjf.getFormattedContent();
                    targetFile = this.getUniqueFileName(directory, gjf.getFileName());
                    this.warnings.add(Messages.getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gjf.getFormattedContent();
            }

            source = source.replace(" implements ", " extends ");
            callback.checkCancel();
            callback.startTask(Messages.getString("Progress.15", targetFile.getName()));
            this.writeFile(targetFile, source, gjf.getFileEncoding());
        } catch (ShellException var6) {
            this.warnings.add(var6.getMessage());
        }

    }

    private void writeGeneratedKotlinFile(GeneratedKotlinFile gkf, ProgressCallback callback) throws InterruptedException, IOException {
        try {
            File directory = this.shellCallback.getDirectory(gkf.getTargetProject(), gkf.getTargetPackage());
            File targetFile = new File(directory, gkf.getFileName());
            String source;
            if (targetFile.exists()) {
                if (this.shellCallback.isOverwriteEnabled()) {
                    source = gkf.getFormattedContent();
                    this.warnings.add(Messages.getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gkf.getFormattedContent();
                    targetFile = this.getUniqueFileName(directory, gkf.getFileName());
                    this.warnings.add(Messages.getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gkf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(Messages.getString("Progress.15", targetFile.getName()));
            this.writeFile(targetFile, source, gkf.getFileEncoding());
        } catch (ShellException var6) {
            this.warnings.add(var6.getMessage());
        }

    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback) throws InterruptedException, IOException {
        try {
            File directory = this.shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
            File targetFile = new File(directory, gxf.getFileName());
            String source;
            if (targetFile.exists()) {
                if (gxf.isMergeable()) {
                    source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
                } else if (this.shellCallback.isOverwriteEnabled()) {
                    source = gxf.getFormattedContent();
                    this.warnings.add(Messages.getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gxf.getFormattedContent();
                    targetFile = this.getUniqueFileName(directory, gxf.getFileName());
                    this.warnings.add(Messages.getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gxf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(Messages.getString("Progress.15", targetFile.getName()));
            this.writeFile(targetFile, source, "UTF-8");
        } catch (ShellException var6) {
            this.warnings.add(var6.getMessage());
        }

    }

    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        Throwable var7 = null;

        try {
            bw.write(content);
        } catch (Throwable var16) {
            var7 = var16;
            throw var16;
        } finally {
            if (bw != null) {
                if (var7 != null) {
                    try {
                        bw.close();
                    } catch (Throwable var15) {
                        var7.addSuppressed(var15);
                    }
                } else {
                    bw.close();
                }
            }

        }

    }

    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < 1000; ++i) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);
            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(Messages.getString("RuntimeError.3", directory.getAbsolutePath()));
        } else {
            return answer;
        }
    }

    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        return this.generatedJavaFiles;
    }

    public List<GeneratedKotlinFile> getGeneratedKotlinFiles() {
        return this.generatedKotlinFiles;
    }

    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        return this.generatedXmlFiles;
    }


}

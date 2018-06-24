package net.ltgt.gradle.apt;

import groovy.lang.Closure;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.plugins.ide.api.PropertiesFileContentMerger;
import org.gradle.util.ConfigureUtil;

public class EclipseJdtApt {
  private final Project project;
  private final PropertiesFileContentMerger file;

  public EclipseJdtApt(Project project, PropertiesFileContentMerger file) {
    this.project = project;
    this.file = file;
  }

  private boolean aptEnabled = true;

  public boolean isAptEnabled() {
    return aptEnabled;
  }

  public void setAptEnabled(boolean aptEnabled) {
    this.aptEnabled = aptEnabled;
  }

  private boolean reconcileEnabled = true;

  public boolean isReconcileEnabled() {
    return reconcileEnabled;
  }

  public void setReconcileEnabled(boolean reconcileEnabled) {
    this.reconcileEnabled = reconcileEnabled;
  }

  private Object genSrcDir = ".apt_generated";

  public File getGenSrcDir() {
    return project.file(genSrcDir);
  }

  public void setGenSrcDir(Object genSrcDir) {
    this.genSrcDir = Objects.requireNonNull(genSrcDir);
  }

  @Nullable private Map<String, ?> processorOptions = new LinkedHashMap<>();

  @Nullable
  public Map<String, ?> getProcessorOptions() {
    return processorOptions;
  }

  public void setProcessorOptions(@Nullable Map<String, ?> processorOptions) {
    this.processorOptions = processorOptions;
  }

  public PropertiesFileContentMerger getFile() {
    return this.file;
  }

  public void file(Closure<? super PropertiesFileContentMerger> closure) {
    ConfigureUtil.configure(closure, this.file);
  }

  public void file(Action<? super PropertiesFileContentMerger> action) {
    action.execute(this.file);
  }
}

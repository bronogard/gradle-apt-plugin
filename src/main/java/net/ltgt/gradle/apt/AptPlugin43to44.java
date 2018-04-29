package net.ltgt.gradle.apt;

import java.io.File;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.api.tasks.compile.CompileOptions;

class AptPlugin43to44 extends AptPlugin.Impl {

  static final String GENERATED_SOURCES_DESTINATION_DIR_DEPRECATION_MESSAGE =
      "The generatedSourcesDestinationDir property has been deprecated. Please use the options.annotationProcessorGeneratedSourcesDirectory property instead.";
  static final String APT_OPTIONS_PROCESSORPATH_DEPRECATION_MESSAGE =
      "The aptOptions.processorpath property has been deprecated. Please use the options.annotationProcessorPath property instead.";

  @Override
  protected AptPlugin.AptConvention createAptConvention(
      Project project, AbstractCompile task, CompileOptions compileOptions) {
    return new AptConvention43to44(project, task, compileOptions);
  }

  @Override
  protected void configureCompileTask(
      Project project, final AbstractCompile task, final CompileOptions compileOptions) {
    task.getInputs()
        .property(
            "aptOptions.annotationProcessing",
            new Callable<Object>() {
              @Override
              public Object call() {
                return task.getConvention()
                    .getPlugin(AptPlugin.AptConvention.class)
                    .getAptOptions()
                    .isAnnotationProcessing();
              }
            });
    task.getInputs()
        .property(
            "aptOptions.processors",
            new Callable<Object>() {
              @Override
              public Object call() {
                return task.getConvention()
                    .getPlugin(AptPlugin.AptConvention.class)
                    .getAptOptions()
                    .getProcessors();
              }
            })
        .optional(true);
    task.getInputs()
        .property(
            "aptOptions.processorArgs",
            new Callable<Object>() {
              @Override
              public Object call() {
                return task.getConvention()
                    .getPlugin(AptPlugin.AptConvention.class)
                    .getAptOptions()
                    .getProcessorArgs();
              }
            })
        .optional(true);

    task.doFirst(
        "configure options.compilerArgs from aptOptions",
        new Action<Task>() {
          @Override
          public void execute(Task task) {
            compileOptions
                .getCompilerArgs()
                .addAll(
                    task.getConvention()
                        .getPlugin(AptPlugin.AptConvention.class)
                        .getAptOptions()
                        .asArguments());
          }
        });
  }

  @Override
  protected AptPlugin.AptSourceSetConvention createAptSourceSetConvention(
      Project project, SourceSet sourceSet) {
    return new AptSourceSetConvention43to44(project, sourceSet);
  }

  @Override
  protected void ensureCompileOnlyConfiguration(
      Project project, SourceSet sourceSet, AptPlugin.AptSourceSetConvention convention) {
    // no-op
  }

  @Override
  protected Configuration ensureAnnotationProcessorConfiguration(
      Project project, SourceSet sourceSet, AptPlugin.AptSourceSetConvention convention) {
    Configuration annotationProcessorConfiguration =
        project.getConfigurations().create(convention.getAnnotationProcessorConfigurationName());
    annotationProcessorConfiguration.setVisible(false);
    annotationProcessorConfiguration.setDescription(
        "Annotation processors and their dependencies for " + sourceSet.getName() + ".");
    return annotationProcessorConfiguration;
  }

  @Override
  protected void configureCompileTaskForSourceSet(
      Project project,
      final SourceSet sourceSet,
      AbstractCompile task,
      final CompileOptions compileOptions) {
    compileOptions.setAnnotationProcessorPath(
        project.files(
            new Callable<FileCollection>() {
              @Override
              public FileCollection call() {
                return new DslObject(sourceSet)
                    .getConvention()
                    .getPlugin(AptPlugin.AptSourceSetConvention.class)
                    .getAnnotationProcessorPath();
              }
            }));
    compileOptions.setAnnotationProcessorGeneratedSourcesDirectory(
        project.provider(
            new Callable<File>() {
              @Override
              public File call() {
                return new DslObject(sourceSet.getOutput())
                    .getConvention()
                    .getPlugin(AptPlugin.AptSourceSetOutputConvention.class)
                    .getGeneratedSourcesDir();
              }
            }));
    sourceSet
        .getAllJava()
        .srcDir(
            project
                .files(
                    new Callable<File>() {
                      @Override
                      public File call() {
                        return compileOptions.getAnnotationProcessorGeneratedSourcesDirectory();
                      }
                    })
                .builtBy(task));
    sourceSet
        .getAllSource()
        .srcDir(
            project
                .files(
                    new Callable<File>() {
                      @Override
                      public File call() {
                        return compileOptions.getAnnotationProcessorGeneratedSourcesDirectory();
                      }
                    })
                .builtBy(task));
  }

  private static class AptSourceSetConvention43to44 extends AptPlugin.AptSourceSetConvention {
    private FileCollection annotationProcessorPath;

    private AptSourceSetConvention43to44(Project project, SourceSet sourceSet) {
      super(project, sourceSet);
    }

    @Nullable
    @Override
    public FileCollection getAnnotationProcessorPath() {
      return annotationProcessorPath;
    }

    @Override
    public void setAnnotationProcessorPath(@Nullable FileCollection annotationProcessorPath) {
      this.annotationProcessorPath = annotationProcessorPath;
    }

    @Override
    public String getCompileOnlyConfigurationName() {
      return sourceSet.getCompileOnlyConfigurationName();
    }

    @Override
    public String getAnnotationProcessorConfigurationName() {
      // HACK: we use the same naming logic/scheme as for tasks, so just use SourceSet#getTaskName
      return sourceSet.getTaskName("", "annotationProcessor");
    }
  }

  private static class AptConvention43to44 extends AptPlugin.AptConvention {
    private final Project project;
    private final AbstractCompile task;
    private final CompileOptions compileOptions;

    private final AptPlugin.AptOptions aptOptions;

    AptConvention43to44(Project project, AbstractCompile task, CompileOptions compileOptions) {
      this.project = project;
      this.task = task;
      this.compileOptions = compileOptions;
      this.aptOptions = new AptOptions43to44(project, this.task, compileOptions);
    }

    @Nullable
    @Override
    public File getGeneratedSourcesDestinationDir() {
      DeprecationLogger.nagUserWith(task, GENERATED_SOURCES_DESTINATION_DIR_DEPRECATION_MESSAGE);
      return compileOptions.getAnnotationProcessorGeneratedSourcesDirectory();
    }

    @Override
    public void setGeneratedSourcesDestinationDir(
        @Nullable final Object generatedSourcesDestinationDir) {
      DeprecationLogger.nagUserWith(task, GENERATED_SOURCES_DESTINATION_DIR_DEPRECATION_MESSAGE);
      if (generatedSourcesDestinationDir == null) {
        compileOptions.setAnnotationProcessorGeneratedSourcesDirectory((File) null);
      } else {
        compileOptions.setAnnotationProcessorGeneratedSourcesDirectory(
            project.provider(
                new Callable<File>() {
                  @Override
                  public File call() {
                    return project.file(generatedSourcesDestinationDir);
                  }
                }));
      }
    }

    @Override
    public AptPlugin.AptOptions getAptOptions() {
      return aptOptions;
    }
  }

  private static class AptOptions43to44 extends AptPlugin.AptOptions {
    private final Project project;
    private final AbstractCompile task;
    private final CompileOptions compileOptions;

    private AptOptions43to44(Project project, AbstractCompile task, CompileOptions compileOptions) {
      this.project = project;
      this.task = task;
      this.compileOptions = compileOptions;
    }

    @Nullable
    @Override
    public FileCollection getProcessorpath() {
      DeprecationLogger.nagUserWith(task, APT_OPTIONS_PROCESSORPATH_DEPRECATION_MESSAGE);
      return compileOptions.getAnnotationProcessorPath();
    }

    @Override
    public void setProcessorpath(@Nullable Object processorpath) {
      DeprecationLogger.nagUserWith(task, APT_OPTIONS_PROCESSORPATH_DEPRECATION_MESSAGE);
      if (processorpath == null || processorpath instanceof FileCollection) {
        compileOptions.setAnnotationProcessorPath((FileCollection) processorpath);
      } else {
        compileOptions.setAnnotationProcessorPath(project.files(processorpath));
      }
    }
  }
}

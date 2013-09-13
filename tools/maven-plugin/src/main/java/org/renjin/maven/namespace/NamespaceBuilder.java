package org.renjin.maven.namespace;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.renjin.eval.Context;
import org.renjin.packaging.LazyLoadFrameBuilder;
import org.renjin.parser.RParser;
import org.renjin.primitives.packaging.Namespace;
import org.renjin.sexp.Environment;
import org.renjin.sexp.FunctionCall;
import org.renjin.sexp.PairList;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.Symbol;

import com.google.common.collect.Lists;

public class NamespaceBuilder {

  private String namespaceName;
  private File sourceDirectory;
  private File environmentFile;

  public void build(String namespaceName, File sourceDirectory,
      File environmentFile) {
   
    this.namespaceName = namespaceName;
    this.sourceDirectory = sourceDirectory;
    this.environmentFile = environmentFile;
    
    compileNamespaceEnvironment();
  }


  private void compileNamespaceEnvironment() {
    List<File> sources = getRSources();
    if(isUpToDate(sources)) {
      return;
    }
    
    Context context = initContext();
  
    Namespace namespace = context.getNamespaceRegistry().createNamespace(new InitializingPackage(), namespaceName);
    maybeImportMethodsTo(context, namespace);
    evaluateSources(context, getRSources(), namespace.getNamespaceEnvironment());
    serializeEnvironment(context, namespace.getNamespaceEnvironment(), environmentFile);
  }


  private void maybeImportMethodsTo(Context context, Namespace namespace) {
    // the environment into which the package sources are evaluated
    // is not all together clear to me. Logically, what we 
    // do here with Renjin is to create an empty namespace environment,
    // import the defined symbols into it, and then evaluate the sources.
    
    // But it looks like R-2.x evaluates the sources into a namespace
    // environment that has the default packages on the search path,
    // giving them a sort of implicit import of methods.
    // We'll replicate that here for the moment
    if(isMethodsOnClasspath()) {
      Namespace methods = context.getNamespaceRegistry().getNamespace("methods");
      methods.copyExportsTo(namespace.getImportsEnvironment());
    }
    
  }


  private boolean isMethodsOnClasspath() {
    InputStream in;
    try {
      in = getClass().getResourceAsStream("/org/renjin/methods/environment");
      in.close();
      return true;
    } catch(Exception e) {
    }
    return false;
  }


  private boolean isUpToDate(List<File> sources) {
    long lastModified = 0;
    for(File source : sources) {
      if(source.lastModified() > lastModified) {
        lastModified = source.lastModified();
      }
    }
    
    if(lastModified < environmentFile.lastModified()) {
      System.out.println("namespaceEnvironment is up to date, skipping compilation");
      return true;
    }
    
    return false;
  }

  

  private Context initContext()  {
    try {
      Context context = Context.newTopLevelContext();
      context.init();
      return context;
    } catch(IOException e) {
      throw new RuntimeException("Could not initialize R top level context", e);
    }
  }

  private List<File> getRSources() {
    List<File> list = Lists.newArrayList();
    if(sourceDirectory.listFiles() != null) {
      list.addAll(Arrays.asList(sourceDirectory.listFiles()));
    }
    Collections.sort(list);
    return list;
  }


  private void evaluateSources(Context context, List<File> sources, Environment namespaceEnvironment) {
    for(File sourceFile : sources) {
      String nameUpper = sourceFile.getName().toUpperCase();
      if(nameUpper.endsWith(".R") ||
         nameUpper.endsWith(".S") ||
         nameUpper.endsWith(".Q")) {
        System.err.println("Evaluating '" + sourceFile + "'");
        try {
          FileReader reader = new FileReader(sourceFile);
          SEXP expr = RParser.parseAllSource(reader);
          reader.close();
          
          context.evaluate(expr, namespaceEnvironment);
          
        } catch (Exception e) {
          throw new RuntimeException("Exception evaluating " + sourceFile.getName(), e);
        }
      }
    }
    
    // some packages (methods) have a routine to do initialization 
    // before serializing the environment
    if(namespaceEnvironment.hasVariable(Symbol.get("..First.lib"))) {
      PairList.Builder args = new PairList.Builder();
      args.add("where", namespaceEnvironment);
      
      context.evaluate(new FunctionCall(Symbol.get("..First.lib"), args.build()), namespaceEnvironment);
    }
  }
  
  private void serializeEnvironment(Context context, Environment namespaceEnv, File environmentFile) {
    
    System.out.println("Writing namespace environment to " + environmentFile);
    try {
      LazyLoadFrameBuilder builder = new LazyLoadFrameBuilder(context);
      builder.outputTo(environmentFile.getParentFile());
      builder.build(namespaceEnv);
    } catch(IOException e) {
      throw new RuntimeException("Exception encountered serializing namespace environment", e);
    }
  }

}
  
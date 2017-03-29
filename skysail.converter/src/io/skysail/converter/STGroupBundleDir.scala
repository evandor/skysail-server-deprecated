package io.skysail.converter

import org.stringtemplate.v4.STGroupDir
import io.skysail.server.services.StringTemplateProvider
import org.osgi.framework.Bundle
import org.restlet.resource.Resource
import java.util.LinkedHashSet
import org.stringtemplate.v4.compiler.CompiledST
import java.util.Optional
import java.net.URL
import java.net.MalformedURLException
import st4hidden.org.antlr.runtime.ANTLRStringStream
import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory
import org.restlet.ext.slf4j.Slf4jLogger
import st4hidden.org.antlr.runtime.ANTLRInputStream
import java.io.IOException

object STGroupBundleDir {
  val UTF8_ENCODING = "UTF-8";
  val DELIMITER_START_CHAR = '$'
  val DELIMITER_STOP_CHAR = '$'
  val usedTemplates = new java.util.LinkedHashSet[String]()
  def clearUsedTemplates() = usedTemplates.clear()
}

class STGroupBundleDir(
  bundle: Bundle,
  resource: Resource,
  resourcePath: String,
  templateProvider: java.util.List[StringTemplateProvider])
    extends STGroupDir(
      bundle.getResource(resourcePath),
      STGroupBundleDir.UTF8_ENCODING,
      STGroupBundleDir.DELIMITER_START_CHAR,
      STGroupBundleDir.DELIMITER_STOP_CHAR) {

  val log: org.slf4j.Logger = LoggerFactory.getLogger("io.skysail.converter")

  log.debug("new STGroupBundleDir in bundle '" + bundle.getSymbolicName() + "' with path '{}'", resourcePath);

  override def load(name: String): CompiledST = {
    //		Validate.isTrue(name.startsWith("/"), "name is supposed to start with '/'");
    //		Validate.isTrue(!name.contains("."), NAME_IS_NOT_SUPPOSED_TO_CONTAIN_A_DOT);
    log.debug("trying to load compileST '" + name + "' from '" + getGroupDirName(bundle, resourcePath) + "'", name);

    return checkForResourceLevelTemplate(name)
      .orElse(checkForProvidedTemplates(name)
        .orElse(loadFromBundle(name, name)
          .orElse(null)));
  }

  /** Load .st as relative file name relative to root by prefix */
  override def loadTemplateFile(prefix: String, fileName: String): CompiledST = loadFromUrl(prefix, fileName, getUrl(fileName))

  private def checkForResourceLevelTemplate(name: String): Optional[CompiledST] = {
    if (optionalResourceClassName == null) {
      return Optional.empty();
    }
    val resourceLevelTemplate = (optionalResourceClassName() + "Stg").replace(".", "/") + "/" + name;
    val optionalCompiledST = loadFromBundle(name, resourceLevelTemplate);
    if (optionalCompiledST.isPresent()) {
      log.debug("found resourceLevelTemplate for key '" + name + "': {}", resourceLevelTemplate);
    }
    return optionalCompiledST;
  }

  private def checkForProvidedTemplates(name: String): Optional[CompiledST] = {
    //Validate.isTrue(!name.contains("."), NAME_IS_NOT_SUPPOSED_TO_CONTAIN_A_DOT);

    val templateFileName = name + ".st";
    val optionalTemplateProvider = templateProvider.asScala
      .filter { t => t.getTemplates().get(templateFileName) != null }
      .headOption
    if (optionalTemplateProvider.isDefined) {
      log.debug("found Template for key '" + name + "' in provider '{}'", optionalTemplateProvider.get.getShortName());

      val charStream = new ANTLRStringStream(optionalTemplateProvider.get.getTemplates().get(templateFileName));
      if (charStream.isInstanceOf[ANTLRStringStream] && charStream.getSourceName() == null) {
        (charStream.asInstanceOf[ANTLRStringStream]).name = templateFileName;
      }
      val split = templateFileName.split("/")
      val unqualifiedFileName = split(split.length - 1)
      val prefix = templateFileName.substring(0, templateFileName.length() - unqualifiedFileName.length())
      return Optional.ofNullable(loadTemplateFile(prefix, unqualifiedFileName, charStream))
    }
    return Optional.empty();
  }

  private def loadFromBundle(originalName: String, name: String): Optional[CompiledST] = {
    //Validate.isTrue(!name.contains("."), NAME_IS_NOT_SUPPOSED_TO_CONTAIN_A_DOT);

    val groupFileURL = determineGroupFileUrl(name);
    if (groupFileURL == null) {
      return Optional.empty();
    }
    val fileName = root + ("/" + name + ".stg").replace("//", "/");
    if (!exists(groupFileURL)) {
      return Optional.ofNullable(loadTemplateFile("/", name + ".st")); // load t.st file // NOSONAR
    }
    STGroupBundleDir.usedTemplates.add(bundleSymbolicName + ": " + groupFileURL.toString());
    try {
      loadGroupFile("/", fileName);
      log.debug(" > [loadFromBundle] found resource in " + bundleSymbolicName + ": {}", groupFileURL.toString());
      return Optional.ofNullable(rawGetTemplate(originalName));
    } catch {
      case _: Throwable =>
    }
    if (root != null && "/".trim().length() != 0) {
      loadGroupFile("/", fileName);
    }
    return Optional.ofNullable(rawGetTemplate(name));
  }

  private def optionalResourceClassName() = resource.getClass().getName()
  private def bundleSymbolicName() = bundle.getSymbolicName();

  private def determineGroupFileUrl(name: String): URL = {
    if (root == null) {
      return null;
    }
    val url = root + ("/" + name + ".stg").replace("//", "/");
    try {
      return new URL(url);
    } catch {
      case e: MalformedURLException => errMgr.internalError(null, "bad URL: " + url, e);
    }
    return null;
  }

  private def exists(url: URL): Boolean = {
    try {
      url.openConnection().connect();
    } catch {
      case _: Throwable => return false;
    }
    return true;
  }

  private def getGroupDirName(bundle: Bundle, resourcePath: String) = {
    new StringBuilder(getClass().getSimpleName()).append(": ").append(bundle.getSymbolicName())
      .append(" - ").append(resourcePath).toString();
  }

  private def loadFromUrl(prefix: String, fileName: String, f: URL): CompiledST = {
    try {
      val fs = new ANTLRInputStream(f.openStream(), encoding);
      fs.name = fileName;
      log.debug(" > [loadFromUrl] found resource in '"+bundleSymbolicName+"': {}", f.toString());
      STGroupBundleDir.usedTemplates.add(bundleSymbolicName + ": " + f.toString());
      return loadTemplateFile(prefix, fileName, fs);
    } catch {
      case _: IOException => //log.trace("resource does not exist in {}: {}", bundleSymbolicName, f.toString());
    }
    null;
  }

  private def getUrl(fileName: String): URL = {
    try {
      return new URL(root + ("/" + fileName).replace("//", "/"));
    } catch {
      case me: MalformedURLException => log.error(root + fileName, me);
    }
    return null;
  }

}

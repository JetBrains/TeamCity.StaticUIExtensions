

package jetbrains.buildServer.staticUIExtensions.config;

import jetbrains.buildServer.staticUIExtensions.ConfigurationException;
import jetbrains.buildServer.staticUIExtensions.PagePlacesCollector;
import jetbrains.buildServer.staticUIExtensions.model.*;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.XmlUtil;
import jetbrains.buildServer.web.openapi.PlaceId;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:50
 */
public class ConfigurationReaderImpl implements ConfigurationReader {
  private final PagePlacesCollector myCollector;

  public ConfigurationReaderImpl(@NotNull final PagePlacesCollector collector) {
    myCollector = collector;
  }

  @NotNull
  public Collection<Rule> parseConfiguration(@NotNull final File config) throws ConfigurationException {
    try {
      final Element root = FileUtil.parseDocument(config);
      return processXml(root);
    } catch (JDOMException e) {
      throw new ConfigurationException("Failed to parse configuration file: " + e.getMessage(), e);
    } catch (IOException e) {
      throw new ConfigurationException("Failed to read configuration file: " + e.getMessage(), e);
    }
  }

  @NotNull
  private Collection<Rule> processXml(@NotNull final Element root) throws ConfigurationException {
    final List rules = root.getChildren("rule");
    final Collection<Rule> result = new ArrayList<Rule>();
    for (Object rule : rules) {
      final Element xmlRule = (Element) rule;

      final String html = xmlRule.getAttributeValue("html-file");
      final String js = xmlRule.getAttributeValue("js-file");
      final String css = xmlRule.getAttributeValue("css-file");

      final StaticContent content = new StaticContent(html, js, css);
      if (!content.isValid()) {
        throw new ConfigurationException("Rule does not contain any file to include. " + XmlUtil.to_s(xmlRule));
      }

      final String placeId = xmlRule.getAttributeValue("place-id");

      final PlaceId place = myCollector.findByName(placeId);
      if (place == null) {
        throw new ConfigurationException("Rule contains unknown place-id: " + XmlUtil.to_s(xmlRule));
      }

      final List<UrlMatcher> matchers = new ArrayList<UrlMatcher>();
      for (Object url : xmlRule.getChildren("url")) {
        final Element xmlUrl = (Element) url;

        final List<UrlMatcher> childMatch = new ArrayList<UrlMatcher>();

        final String startsWith = xmlUrl.getAttributeValue("starts");
        if (startsWith != null) {
          childMatch.add(new StartsWithMatcher(startsWith.trim()));
        }

        final String equals = xmlUrl.getAttributeValue("equals");
        if (equals != null) {
          childMatch.add(new EqualsMatcher(equals.trim()));
        }

        final String contains = xmlUrl.getAttributeValue("contains");
        if (contains != null) {
          childMatch.add(new ContainsMatcher(contains.trim()));
        }

        if (childMatch.isEmpty()) {
          throw new ConfigurationException("No url matching rules found: " + XmlUtil.to_s(xmlUrl));
        }

        matchers.add(new AndMatcher(childMatch));
      }

      result.add(new Rule("_" + result.size(), matchers.isEmpty() ? new TrueMatcher() : new OrMatcher(matchers), place, content));
    }
    return result;
  }
}
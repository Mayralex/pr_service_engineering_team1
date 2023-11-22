package org.serviceEngineering.adrViewer.div;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class ADRParser {

    private static final Logger log = LoggerFactory.getLogger(ADRParser.class);
    private ADRParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts Markdown content to HTML format using a common Markdown parser and renderer.
     *
     * @param markdown The Markdown content to be converted to HTML.
     * @return The HTML representation of the provided Markdown content.
     */
    public static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

    /**
     * Converts an HTML representation of an Architectural Decision Record (ADR) into an ADR object.
     *
     * @param html The HTML content representing the ADR.
     * @return An ADR object containing the parsed information from the HTML content.
     * @throws NullPointerException     If required elements in the HTML content are not found.
     * @throws IllegalArgumentException If there are issues parsing the HTML content.
     */
    public static ADR convertHTMLToADR(String html) {
        Document document = Jsoup.parse(html);
        ADR adr = new ADR();
        //log.info(html);
        String title = null;
        try {
            title = document.selectFirst("h1").textNodes().get(0).toString();
        } catch (NullPointerException e) {
            log.warn(e.getMessage());
        }
        adr.setTitle(((title == null) ? "PLACEHOLDER TITLE" : title));
        adr.setContext(extractSectionText(document, "Context"));
        adr.setDecision(extractSectionText(document, "Decision"));
        //try {
        //adr.setStatus(Status.valueOf(extractSectionText(document, "Status").toUpperCase()));
        //} catch (IllegalArgumentException e) {
        //    throw new IllegalArgumentException("Error parsing ADR status from the HTML content.", e);
        //}
        adr.setStatus(extractSectionText(document, "Status"));
        adr.setConsequences(extractSectionText(document, "Consequences"));
        adr.setArtifacts(extractLinks(document, "Artifacts").toString());
        adr.setRelations(extractLinks(document, "Relations").toString());
        return adr;
    }

    /**
     * Extracts and returns the text content of a specific section identified by its header.
     * The method searches for the given section header (h2 tag) in the provided Jsoup Document.
     * If the section header is found, the method extracts and concatenates the text content
     * of all paragraph elements (p tags) following the header until the next section header (h2 tag) is encountered.
     *
     * @param doc           The Jsoup Document containing the HTML content.
     * @param sectionHeader The header of the section to extract text from.
     * @return A string containing the text content of the specified section, or an empty string if the section is not found.
     */
    private static String extractSectionText(Document doc, String sectionHeader) {
        Element h2 = doc.select("h2:contains(" + sectionHeader + ")").first();
        if (h2 != null) {
            Element nextSibling = h2.nextElementSibling();
            StringBuilder text = new StringBuilder();

            while (nextSibling != null && !nextSibling.tagName().equals("h2")) {
                if (nextSibling.tagName().equals("p")) {
                    text.append(nextSibling.text()).append("\n");
                }
                nextSibling = nextSibling.nextElementSibling();
            }

            return text.toString().trim();
        } else {
            return "";
        }
    }

    /**
     * Extract links from a specific section identified by its header.
     *
     * @param doc           The Jsoup Document containing the HTML content.
     * @param sectionHeader The header of the section to extract links from.
     * @return A map containing link text as keys and link href as values.
     */
    private static Map<String, String> extractLinks(Document doc, String sectionHeader) {
        Map<String, String> linksMap = new HashMap<>();
        Element h2 = doc.select("h2:contains(" + sectionHeader + ")").first();

        if (h2 != null) {
            Element nextSibling = h2.nextElementSibling();
            while (nextSibling != null && !nextSibling.tagName().equals("h2")) {
                if (nextSibling.tagName().equals("ul")) {
                    extractLinksFromList(linksMap, nextSibling);
                }
                nextSibling = nextSibling.nextElementSibling();
            }
        }

        return linksMap;
    }

    /**
     * Extract links from an unordered list element and add them to the links map.
     *
     * @param linksMap   The map to store the extracted links.
     * @param ulElement  The Jsoup Element representing an unordered list.
     */
    private static void extractLinksFromList(Map<String, String> linksMap, Element ulElement) {
        Elements listItems = ulElement.select("li");
        for (Element listItem : listItems) {
            Element link = listItem.selectFirst("a");
            if (link != null) {
                String linkText = link.text();
                String linkHref = link.attr("href");
                linksMap.put(linkText, linkHref);
            }
        }
    }

}


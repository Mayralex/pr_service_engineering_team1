package org.serviceEngineering.adrViewer.div;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.entity.Artifact;
import org.serviceEngineering.adrViewer.entity.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing Architectural Decision Records (ADRs) from HTML and Markdown formats.
 */
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
        String title = null;
        try {
            title = document.selectFirst("h1").textNodes().get(0).toString();
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
        //adr.setTitle(((title == null) ? "PLACEHOLDER TITLE" : title));
        if (title == null) {
            log.warn("No title found --> setting placeholder title");
            adr.setTitle("PLACEHOLDER TITLE");
        } else
            adr.setTitle(title);
        adr.setContext(extractSectionText(document, "Context"));
        adr.setDecision(extractSectionText(document, "Decision"));
        adr.setStatus(extractSectionText(document, "Status"));
        adr.setConsequences(extractSectionText(document, "Consequences"));
        adr.setArtifacts(extractArtifacts(document, "Artifacts", adr));
        adr.setRelations(extractRelations(document, "Relations", adr));
        String date = null;
        try {
            date = extractSectionText(document, "Date");
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
        //adr.setDate(((date == null) ? "9999-12-31" : date));
        if (date == null) {
            log.warn("No date found --> setting placeholder date 9999-12-31");
            adr.setDate("999-12-31");
        } else
            adr.setDate(date);
        adr.setCommit(extractSectionText(document, "Commit"));
        return adr;
    }


    /**
     * Extract the text from a section in the ADR
     * @param element a HTML element
     * @return a string of text
     */
    private static String extractSectionText(Element element) {
        StringBuilder text = new StringBuilder();

        for (org.jsoup.nodes.Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                text.append(((TextNode) node).text()).append("\n");
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                if (childElement.tagName().equals("p")) {
                    text.append(childElement.text()).append("\n");
                }
                if (childElement.tagName().equals("b") ||
                        childElement.tagName().equals("i")) {
                    text.append(childElement.text());
                } else {
                    text.append(extractSectionText(childElement));
                }
            }
        }
        return text.toString().trim();
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
                text.append(extractSectionText(nextSibling));
                nextSibling = nextSibling.nextElementSibling();
            }

            return text.toString().trim();
        } else {
            return "";
        }
    }

    /**
     * Extract Artifacts from an unordered list element and adds it to a list of artifacts
     * @param doc           The Jsoup Document containing the HTML content.
     * @param sectionHeader The header of the section to extract artifacts from.
     * @param adr           The ADR the artifact belongs to
     * @return A list of Artifacts
     */
        private static List<Artifact> extractArtifacts(Document doc, String sectionHeader, ADR adr) {
            List<Artifact> artifacts = new ArrayList<>();
            Element h2 = doc.select("h2:contains(" + sectionHeader + ")").first();

            if (h2 != null) {
                Element nextSibling = h2.nextElementSibling();
                while (nextSibling != null && !nextSibling.tagName().equals("h2")) {
                    if (nextSibling.tagName().equals("ul")) {
                        Elements listItems = nextSibling.select("li");
                        for (Element listItem : listItems) {
                            String artifactName = listItem.text();
                            Artifact artifact = new Artifact();
                            artifact.setAdr(adr);
                            artifact.setName(artifactName);
                            artifacts.add(artifact);
                        }
                    }
                    nextSibling = nextSibling.nextElementSibling();
                }
            }
            return artifacts;
        }

    /**
     * Extract Relations from an unordered list element and adds it to a list of relations
     * @param doc           The Jsoup Document containing the HTML content.
     * @param sectionHeader The header of the section to extract relations from.
     * @param adr           The ADR the relation belongs to
     * @return A list of Relations
     */
    private static List<Relation> extractRelations(Document doc, String sectionHeader, ADR adr) {
        List<Relation> relations = new ArrayList<>();
        Element h2 = doc.select("h2:contains(" + sectionHeader + ")").first();

        if (h2 != null) {
            Element nextSibling = h2.nextElementSibling();
            while (nextSibling != null && !nextSibling.tagName().equals("h2")) {
                if (nextSibling.tagName().equals("ul")) {
                    Elements listItems = nextSibling.select("li");
                    for (Element listItem : listItems) {
                        Relation relation = new Relation();
                        String relationType = getRelationType(listItem);
                        relation.setAdr(adr);
                        relation.setType(relationType);
                        String affectedAdr = listItem.text().substring(listItem.text().lastIndexOf(" ") + 1);
                        relation.setAffectedAdr(affectedAdr);
                        relations.add(relation);
                    }
                }
                nextSibling = nextSibling.nextElementSibling();
            }
        }
        return relations;
    }

    /**
     * Parses the relation Type and returns it as a string
     * @param listItem a list of relations
     * @return relation type as string
     */
    private static String getRelationType(Element listItem) {
        // Get the text before the <a> tag in the list item
        String listItemText = listItem.ownText().trim();

        return switch (listItemText) {
            case "enables" -> "enables ";
            case "deprecates" -> "deprecates ";
            case "extends" -> "extends ";
            case "is enabled by" -> "is enabled by ";
            case "is deprecated by", "deprecated by" -> "is deprecated by ";
            case "is related to", "related to" -> "is related to ";
            case "is extended by" -> "is extended by ";
            default -> ""; // Default value or handle other relation types
        };
    }
}


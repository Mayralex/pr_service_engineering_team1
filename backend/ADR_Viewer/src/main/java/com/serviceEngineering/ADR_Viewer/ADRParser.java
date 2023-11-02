package com.serviceEngineering.ADR_Viewer;

import com.serviceEngineering.ADR_Viewer.div.Status;
import com.serviceEngineering.ADR_Viewer.entity.ADR;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
public class ADRParser {

    public static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }

    public static ADR convertHTMLToADR(String html) {
        Document document = Jsoup.parse(html);
        ADR adr = new ADR();

        adr.setTitle(Objects.requireNonNull(document.selectFirst("h1")).textNodes().get(0).toString());
        adr.setContext(extractSectionText(document, "Context"));
        adr.setDecision(extractSectionText(document, "Decision"));
        adr.setStatus(Status.valueOf(extractSectionText(document, "Status").toUpperCase()));
        adr.setConsequences(extractSectionText(document, "Consequences"));
        adr.setArtifacts(extractLinks(document, "Artifacts").toString());
        adr.setRelations(extractLinks(document, "Relations").toString());
        return adr;
    }


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

    private static Map<String, String> extractLinks(Document doc, String sectionHeader) {
        Map<String, String> linksMap = new HashMap<>();
        Element h2 = doc.select("h2:contains(" + sectionHeader + ")").first();

        if (h2 != null) {
            Element nextSibling = h2.nextElementSibling();

            while (nextSibling != null && !nextSibling.tagName().equals("h2")) {
                if (nextSibling.tagName().equals("ul")) {
                    Elements listItems = nextSibling.select("li");
                    for (Element listItem : listItems) {
                        Element link = listItem.selectFirst("a");
                        if (link != null) {
                            String linkText = link.text();
                            String linkHref = link.attr("href");
                            linksMap.put(linkText, linkHref);
                        }
                    }
                }
                nextSibling = nextSibling.nextElementSibling();
            }
        }

        return linksMap;
    }
}


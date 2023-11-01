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
        Elements h2 = document.select("h2");
        for (Element element : h2) {
            /*
            TODO: big big here: The following code takes all text fields after the section.
             Meaning that inside Context we have every text until section Commit.
                --> refine the functionality to only take the relevant part
             */
            Elements pElements = element.nextElementSiblings().select("p");
            if (element.text().equals("Context")) adr.setContext(pElements.text());
            if (element.text().equals("Decision")) adr.setDecision(pElements.text());
            if (element.text().equals("Status")) adr.setStatus(Status.valueOf(pElements.get(0).text().toUpperCase()));
            if (element.text().equals("Consequences")) adr.setConsequences(pElements.text());
            if (element.text().equals("Artifacts")) adr.setArtifacts(pElements.dataNodes().toString());
            if (element.text().equals("Relations")) adr.setRelations(pElements.dataNodes().toString());
        }

        return adr;
    }
}

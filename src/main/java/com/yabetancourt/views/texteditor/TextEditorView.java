package com.yabetancourt.views.texteditor;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.richtexteditor.RichTextEditorVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import com.yabetancourt.SentiWordNet;
import org.jsoup.Jsoup;

@PageTitle("TextEditor")
@Route(value = "editor")
@RouteAlias(value = "")
public class TextEditorView extends Main {

    private final SentiWordNet swn = new SentiWordNet();
    Div wordAnalysis = new Div();
    Div resume = new Div();

    public TextEditorView() {
        addClassNames(Display.FLEX, Flex.GROW, Height.FULL);

        RichTextEditor editor = new RichTextEditor();
        editor.addClassNames(Border.RIGHT, BorderColor.CONTRAST_10, Flex.GROW);
        editor.addThemeVariants(RichTextEditorVariant.LUMO_NO_BORDER);
        editor.setValueChangeMode(ValueChangeMode.TIMEOUT);

        editor.addValueChangeListener(e -> {
            String words = Jsoup.parse(e.getValue()).text();
            wordAnalysis.removeAll();
            double total = 0.0;
            for (String word : words.split(" +")) {
                double score = swn.score(word);
                total += score;
                if (score < 0) {
                    wordAnalysis.add(createErrorBadgeItem(word));
                } else if (score > 0) {
                    wordAnalysis.add(createSuccessBadgeItem(word));
                } else {
                    wordAnalysis.add(createNeutralBadgeItem(word));
                }
            }
            resume.removeAll();
            resume.add(createGeneralDetails(total));

        });

        add(editor, createSidebar());
    }

    private Section createSidebar() {
        Section sidebar = new Section();
        sidebar.addClassNames(Background.CONTRAST_5, BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN,
                Flex.SHRINK_NONE, Padding.LARGE);
        sidebar.setWidth("300px");

        H2 title = new H2("Text details");
        title.addClassName(Accessibility.SCREEN_READER_ONLY);

        wordAnalysis.addClassNames(Display.FLEX, FlexDirection.ROW, FlexWrap.WRAP, Gap.XSMALL, Margin.Bottom.SMALL, Margin.Top.NONE,
                FontSize.SMALL, Overflow.AUTO);
        resume.addClassNames(Margin.Top.AUTO);
        sidebar.add(title, wordAnalysis, resume);
        return sidebar;
    }

    private Div createErrorBadgeItem(String value) {
        return new Div(createBadge(value, "badge", "error"));
    }

    private Div createSuccessBadgeItem(String value) {
        return new Div(createBadge(value, "badge", "success"));
    }

    private Div createNeutralBadgeItem(String value) {
        return new Div(createBadge(value, "badge", "contrast"));
    }

    private Span createBadge(String value, String... themeNames) {
        Span badge = new Span(value);
        badge.addClassName(Margin.Left.NONE);
        for (String themeName : themeNames) {
            badge.getElement().getThemeList().add(themeName);
        }
        return badge;
    }

    private Div createGeneralDetails(double total) {
        Span category;
        if (total > 0) {
            category = createBadge("Your text is positive", "badge", "success");
        } else if (total < 0) {
            category = createBadge("Your text is negative", "badge", "error");
        } else {
            category = createBadge("Your text is objective", "badge", "contrast");
        }
        Div div = new Div(category);
        div.addClassNames(Display.FLEX, FlexDirection.ROW, FontSize.MEDIUM, AlignItems.CENTER, JustifyContent.CENTER);
        return div;
    }

}

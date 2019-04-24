package com.recurse.portfolio.web;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.superscript.SuperscriptExtension;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.List;

import static com.vladsch.flexmark.ext.emoji.EmojiImageType.UNICODE_ONLY;

class MarkdownHelper {
    private static final MutableDataHolder OPTIONS = new MutableDataSet()
        .set(Parser.EXTENSIONS, List.of(
            AutolinkExtension.create(),
            EmojiExtension.create(),
            StrikethroughSubscriptExtension.create(),
            SuperscriptExtension.create(),
            TablesExtension.create(),
            TaskListExtension.create()
        ))
        .set(EmojiExtension.USE_IMAGE_TYPE, UNICODE_ONLY);

    static String renderMarkdownToHtml(String input) {
        Parser markdownParser = Parser.builder(OPTIONS).build();
        HtmlRenderer htmlRenderer = HtmlRenderer.builder(OPTIONS).build();

        String renderedMarkdown = htmlRenderer.render(
            markdownParser.parse(input)
        );

        return Jsoup.clean(
            renderedMarkdown,
            Whitelist.relaxed()
                .addTags("del", "hr")
        );
    }
}

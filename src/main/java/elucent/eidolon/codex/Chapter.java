package elucent.eidolon.codex;

import com.google.common.collect.Lists;

import java.util.List;

public class Chapter {
    final String titleKey;
    final List<Page> pages;

    public Chapter(String titleKey, Page... pages) {
        this.titleKey = titleKey;
        this.pages = Lists.newArrayList(pages);
    }

    public Page get(int i) {
        if (i >= size()) return null;
        return pages.get(i);
    }

    public int size() {
        return pages.size();
    }
}

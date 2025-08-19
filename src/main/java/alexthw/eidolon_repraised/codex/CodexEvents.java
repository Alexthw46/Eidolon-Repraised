package alexthw.eidolon_repraised.codex;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.Map;

public class CodexEvents extends Event {

    List<Category> categories;
    Map<Item, IndexPage.IndexEntry> itemToEntryMap;

    public CodexEvents(List<Category> categories, Map<Item, IndexPage.IndexEntry> itemToEntryMap) {
        this.categories = categories;
        this.itemToEntryMap = itemToEntryMap;
    }

    public static class PreInit extends CodexEvents {
        // This event is fired when the codex is initialized.
        // Use this to register pages, etc.

        public PreInit(List<Category> categories, Map<Item, IndexPage.IndexEntry> itemToEntryMap) {
            super(categories, itemToEntryMap);
        }

    }

    public static class PostInit extends CodexEvents {
        // This event is fired after the codex has been initialized.
        // Use this to modify existing pages or add new ones.

        public PostInit(List<Category> categories, Map<Item, IndexPage.IndexEntry> itemToEntryMap) {
            super(categories, itemToEntryMap);
        }

    }


}

package elucent.eidolon.api.spells;

import com.mojang.math.Vector3f;
import elucent.eidolon.registries.Signs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public class SignSequence {
    public final ArrayDeque<Sign> seq = new ArrayDeque<>();
    public Sign last = null;

    public SignSequence() {
    }

    public SignSequence(Collection<Sign> signs) {
        seq.addAll(signs);
    }

    public SignSequence(Sign... signs) {
        for (Sign s : signs) seq.addLast(s);
    }

    public void addLeft(Sign s) {
        seq.addFirst(s);
    }

    public void addRight(Sign s) {
        seq.addLast(s);
    }

    public Sign[] toArray() {
        return seq.toArray(Sign[]::new);
    }

    public void removeLeft() {
        last = seq.getFirst();
        seq.removeFirst();
    }

    public void removeRight() {
        last = seq.getLast();
        seq.removeLast();
    }

    private int count(Sign s) {
        int count = 0;
        for (Sign sign : seq) {
            if (s.equals(sign)) count++;
        }
        return count;
    }

    private boolean contains(Sign s) {
        for (Sign sign : seq) {
            if (s.equals(sign)) return true;
        }
        return false;
    }

    private boolean containsN(Sign s, int n) {
        Iterator<Sign> iterator = seq.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            if (s.equals(iterator.next())) count ++;
            if (count >= n) return true;
        }
        return false;
    }

    public boolean removeLeftmost(Sign s) {
        if (!seq.contains(s)) return false;
        last = s;
        seq.removeFirstOccurrence(s);
        return true;
    }

    public boolean removeRightmost(Sign s) {
        if (!seq.contains(s)) return false;
        last = s;
        seq.removeLastOccurrence(s);
        return true;
    }

    public int removeAll(Sign s) {
        int count = count(s);
        if (seq.contains(s)) last = s;
        seq.removeIf((i) -> i.equals(s));
        return count;
    }

    public boolean removeLeftmostN(Sign s, int n) {
        if (n == 0) return true;
        if (!containsN(s, n)) return false;
        last = s;
        for (int i = 0; i < n; i ++) seq.removeFirstOccurrence(s);
        return true;
    }

    public boolean removeRightmostN(Sign s, int n) {
        if (n == 0) return true;
        if (!containsN(s, n)) return false;
        last = s;
        for (int i = 0; i < n; i ++) seq.removeLastOccurrence(s);
        return true;
    }

    public void map(Function<Sign, Sign> tf) {
        int n = seq.size();
        for (int i = 0; i < n; i ++) {
            seq.addLast(tf.apply(seq.getFirst()));
        }
    }

    @Nullable
    public Sign getLast() {
        return last;
    }

    public CompoundTag serializeNbt() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (Sign s : seq) list.add(StringTag.valueOf(s.key.toString()));
        tag.put("seq", list);
        if (last != null) tag.putString("last", last.getRegistryName().toString());
        return tag;
    }

    public static SignSequence deserializeNbt(CompoundTag tag) {
        SignSequence s = new SignSequence();
        ListTag list = tag.getList("seq", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i ++) {
            Sign t = Signs.find(new ResourceLocation(list.getString(i)));
            if (t != null) s.seq.addLast(t);
        }
        s.last = tag.contains("last") ? Signs.find(new ResourceLocation(tag.getString("last"))) : null;
        return s;
    }

    public Vector3f getAverageColor() {
        float r = 1, g = 1, b = 1;
        for (Sign s : seq) {
            r += s.getRed();
            g += s.getGreen();
            b += s.getBlue();
        }
        r /= seq.size() + 1;
        g /= seq.size() + 1;
        b /= seq.size() + 1;
        return new Vector3f(r, g, b);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SignSequence s) {
            if (s.seq.size() != seq.size()) return false;
            Iterator<Sign> a = seq.iterator(), b = s.seq.iterator();
            while (a.hasNext() && b.hasNext()) {
                Sign sa = a.next(), sb = b.next();
                if (!sa.equals(sb)) return false;
            }
            return true;
        }
        return false;
    }
}

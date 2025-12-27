package quest.yuzhou.realmcraft.types;

public record Rank(String id, String name, int threshold) {

    @Override
    public String toString() {
        return "Rank{id='" + id + "', name='" + name + "', threshold=" + threshold + "}";
    }

}

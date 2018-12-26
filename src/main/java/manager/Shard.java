package manager;

public class Shard {

    private final String[] shards;

    public Shard(String msg, int maxSize) {
        this.shards = msg.split(""); // TODO: correct split by maxSize
    }

    public String reassemble() {
        StringBuilder sb = new StringBuilder();
        for(String s : this.shards) {
            // TODO: correct reassemble
            sb.append(s);
        }
        return sb.toString();
    }

}

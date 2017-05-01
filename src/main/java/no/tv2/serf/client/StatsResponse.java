package no.tv2.serf.client;

import java.util.Map;
import org.msgpack.type.Value;

public class StatsResponse extends ResponseBase {
    private final static StatsParser statsParser = new StatsParser();
    private final Stats stats;

    public StatsResponse(Long seq, String error, Map<String, Value> response) {
        super(seq, error);
        this.stats = statsParser.parseStats(response);
    }

    public Stats getStats() {
        return stats;
    }
}

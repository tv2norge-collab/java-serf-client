package no.tv2.serf.client;

import java.util.Map;

import org.msgpack.type.Value;

import static no.tv2.serf.client.ResponseBase.valueConverter;

public class StatsParser {

    public Stats parseStats(Map<String, Value> body) {
        Stats stats = new Stats();
        for (Map.Entry<String, Value> entry : body.entrySet()) {
            switch (entry.getKey()) {
                case "agent":
                    for (Map.Entry<String, Value> agentEntry : valueConverter().getValueMap(entry.getValue()).entrySet()) {
                        switch (agentEntry.getKey()) {
                            default:
                                stats.getAgent().setName(valueConverter().asString(agentEntry.getValue()));
                        }
                    }
                    break;
                case "runtime":
                    for (Map.Entry<String, Value> runtimeEntry : valueConverter().getValueMap(entry.getValue()).entrySet()) {
                        switch (runtimeEntry.getKey()) {
                            case "os":
                                stats.getRuntime().setOs(valueConverter().asString(runtimeEntry.getValue()));
                                break;
                            case "arch":
                                stats.getRuntime().setArch(valueConverter().asString(runtimeEntry.getValue()));
                                break;
                            case "version":
                                stats.getRuntime().setVersion(valueConverter().asString(runtimeEntry.getValue()));
                                break;
                            case "max_procs":
                                stats.getRuntime().setMaxProcs(valueConverter().asString(runtimeEntry.getValue()));
                                break;
                            case "goroutines":
                                stats.getRuntime().setGoroutines(valueConverter().asString(runtimeEntry.getValue()));
                                break;
                            case "cpu_count":
                                stats.getRuntime().setCpuCount(valueConverter().asString(runtimeEntry.getValue()));
                                break;
                        }
                    }
                    break;
                case "serf":
                    for (Map.Entry<String, Value> serfEntry : valueConverter().getValueMap(entry.getValue()).entrySet()) {
                        switch (serfEntry.getKey()) {
                            case "failed":
                                stats.getSerf().setFailed(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "left":
                                stats.getSerf().setLeft(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "event_time":
                                stats.getSerf().setEventTime(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "query_time":
                                stats.getSerf().setQueryTime(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "event_queue":
                                stats.getSerf().setEventQueue(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "members":
                                stats.getSerf().setMembers(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "member_time":
                                stats.getSerf().setMemberTime(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "intent_queue":
                                stats.getSerf().setIntentQueue(valueConverter().asString(serfEntry.getValue()));
                                break;
                            case "query_queue":
                                stats.getSerf().setQueryQueue(valueConverter().asString(serfEntry.getValue()));
                                break;
                        }
                    }
                    break;
                case "tags":
                    stats.setTags(valueConverter().getStringMap(entry.getValue()));
                    break;
            }
        }
        return stats;
    }
}

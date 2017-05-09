package no.tv2.serf.client;

import java.util.Map;

public class Stats {

    private Agent agent = new Agent();
    private Runtime runtime = new Runtime();
    private Serf serf = new Serf();
    private Map<String, String> tags;


    class Agent {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class Runtime {
        private String os;
        private String arch;
        private String version;
        private String maxProcs;
        private String goroutines;
        private String cpuCount;

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getArch() {
            return arch;
        }

        public void setArch(String arch) {
            this.arch = arch;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getMaxProcs() {
            return maxProcs;
        }

        public void setMaxProcs(String maxProcs) {
            this.maxProcs = maxProcs;
        }

        public String getGoroutines() {
            return goroutines;
        }

        public void setGoroutines(String goroutines) {
            this.goroutines = goroutines;
        }

        public String getCpuCount() {
            return cpuCount;
        }

        public void setCpuCount(String cpuCount) {
            this.cpuCount = cpuCount;
        }
    }

    class Serf {
        private String failed;
        private String left;
        private String eventTime;
        private String queryTime;
        private String eventQueue;
        private String members;
        private String memberTime;
        private String intentQueue;
        private String queryQueue;

        public String getFailed() {
            return failed;
        }

        public void setFailed(String failed) {
            this.failed = failed;
        }

        public String getLeft() {
            return left;
        }

        public void setLeft(String left) {
            this.left = left;
        }

        public String getEventTime() {
            return eventTime;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }

        public String getQueryTime() {
            return queryTime;
        }

        public void setQueryTime(String queryTime) {
            this.queryTime = queryTime;
        }

        public String getEventQueue() {
            return eventQueue;
        }

        public void setEventQueue(String eventQueue) {
            this.eventQueue = eventQueue;
        }

        public String getMembers() {
            return members;
        }

        public void setMembers(String members) {
            this.members = members;
        }

        public String getMemberTime() {
            return memberTime;
        }

        public void setMemberTime(String memberTime) {
            this.memberTime = memberTime;
        }

        public String getIntentQueue() {
            return intentQueue;
        }

        public void setIntentQueue(String intentQueue) {
            this.intentQueue = intentQueue;
        }

        public String getQueryQueue() {
            return queryQueue;
        }

        public void setQueryQueue(String queryQueue) {
            this.queryQueue = queryQueue;
        }
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    public Serf getSerf() {
        return serf;
    }

    public void setSerf(Serf serf) {
        this.serf = serf;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}

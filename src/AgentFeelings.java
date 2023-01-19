public class AgentFeelings {
    private boolean stench;
    private boolean breeze;
    private boolean glitter;
    private boolean scream;

    public AgentFeelings setStench(Boolean flag) {
        stench = flag;
        return this;
    }

    public AgentFeelings setBreeze(Boolean flag) {
        breeze = flag;
        return this;
    }

    public AgentFeelings setGlitter(Boolean flag) {
        glitter = flag;
        return this;
    }


    public AgentFeelings setScream(Boolean flag) {
        scream = flag;
        return this;
    }

    public boolean isStench() {
        return stench;
    }

    public boolean isBreeze() {
        return breeze;
    }

    public boolean isGlitter() {
        return glitter;
    }

    public boolean isScream() {
        return scream;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (stench)
            result.append("There is Stench. ");
        if (breeze)
            result.append("There is Breeze. ");
        if (glitter)
            result.append("There is Glitter. ");
        if (scream)
            result.append("There is Scream. ");
        return result.toString();
    }
}
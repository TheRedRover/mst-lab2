import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NavigatorAgent extends Agent {

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(WumpusWorldAgent.Constants.NAVIGATOR_AGENT_TYPE);
        sd.setName(WumpusWorldAgent.Constants.NAVIGATOR_SERVICE_DESCRIPTION);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new LocationRequestsServer());
        System.out.println("Navigator [" + getAID().getName() + "] is ready.");
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Navigator [" + getAID().getName() + "] terminating.");
    }

    private class LocationRequestsServer extends CyclicBehaviour {

        int time = 0;

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                if (parseSpeleologistMessageRequest(msg.getContent())){
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REQUEST);
                    reply.setContent(WumpusWorldAgent.Constants.INFORMATION_PROPOSAL_NAVIGATOR);
                    System.out.println("Navigator [" + getAID().getName() + "]: " + WumpusWorldAgent.Constants.INFORMATION_PROPOSAL_NAVIGATOR);
                    myAgent.send(reply);
                } else if (parseSpeleologistMessageProposal(msg.getContent()))
                {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    String advice = getAdvice(msg.getContent());
                    reply.setContent(advice);
                    System.out.println("Navigator [" + getAID().getName() + "]: " + advice);
                    myAgent.send(reply);

                } else
                    System.out.println("Navigator [" + getAID().getName() + "]: Wrong message.");
            } else {
                block();
            }
        }

        private boolean parseSpeleologistMessageRequest(String instruction) {
            String regex = "\\bAdvice\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(instruction);
            if (matcher.find()) {
                String res = matcher.group();
                return res.length() > 0;
            }

            return false;
        }

        private boolean parseSpeleologistMessageProposal(String instruction) {
            String regex = "\\bGiving\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(instruction);
            if (matcher.find()) {
                String res = matcher.group();
                return res.length() > 0;
            }

            return false;
        }

        private String getAdvice(String content){
            boolean stench = false;
            boolean breeze = false;
            boolean glitter = false;
            boolean scream = false;
            String advicedAction = "";

            for (Map.Entry<Integer, String> entry : STATES.entrySet()) {
                String value = entry.getValue();
                Pattern pattern = Pattern.compile("\\b" + value + "\\b", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    switch (value){
                        case "Stench": stench = true;
                        case "Breeze": breeze = true;
                        case "Glitter": glitter = true;
                        case "Scream": scream = true;
                    }
                }
            }
            Random rand = new Random();
            int randomNum = rand.nextInt((3 - 1) + 1) + 1;
            switch (randomNum){
                case 1: advicedAction = WumpusWorldAgent.Constants.MESSAGE_RIGHT; time++; break;
                case 2: advicedAction = WumpusWorldAgent.Constants.MESSAGE_FORWARD; time++; break;
                case 3: advicedAction = WumpusWorldAgent.Constants.MESSAGE_LEFT; time++; break;
            }

            return WumpusWorldAgent.Constants.ACTION_PROPOSAL + advicedAction;
        }


        final Map<Integer, String> STATES = new LinkedHashMap<Integer, String>() {{
            put(1, "Stench");
            put(2, "Breeze");
            put(3, "Glitter");
            put(4, "Scream");
        }};

    }
}
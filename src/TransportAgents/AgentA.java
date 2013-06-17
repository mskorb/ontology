package TransportAgents;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jadeOWL.base.OntologyManager;
import jadeOWL.base.messaging.ACLOWLMessage;

import java.io.File;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * A simple example of an agent that prepares and sends a class query
 */
public class AgentA extends Agent{

	private static final long serialVersionUID = 6572191406480216410L;
	OntologyManager ontologyManager;
	
	protected void setup(){
		
		System.out.println("Agent " + getLocalName() + " started");
		
		ontologyManager = new OntologyManager();
		
		try {
			//Load the base ontology
			OWLOntology pizzaOntology = ontologyManager.loadAndMapOntology(new File("ontologies/transport.owl"), "http://www.co-ode.org/ontologies/pizza/pizza.owl");
			
			//Ask for all countries using custom query
			
			//Create a new empty query ontology
			OWLOntology countryQueryOntology = ontologyManager.getQueryManager().createNewOWLQueryOntology(this);
			
			//Get reference to the "Country" class
			OWLClass countryClass = ontologyManager.getDataFactory().getOWLClass(pizzaOntology, "Country");
			
			//Create a query class that asks for all individuals of class "Country"
			ontologyManager.getQueryManager().createCustomQueryClass(countryQueryOntology, "countryQuery", countryClass);

			//Add behaviour that sends the query ontology
			addBehaviour(new AskForPizzasBehaviour(this,countryQueryOntology));
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void takeDown(){
		//Clean up
		ontologyManager.removeAndUnmapAllOntologies();
	}
	
    class AskForPizzasBehaviour extends SimpleBehaviour 
    {   
		private static final long serialVersionUID = 1697260008899699044L;
		private boolean finished = false;
    	private int iteration = 0;
    	private OWLOntology m_queryOntology;
    	
        public AskForPizzasBehaviour(Agent a, OWLOntology queryOntology) {
            super(a);
            m_queryOntology = queryOntology;
        }
        
        public void action() 
        {
        	iteration++;
        	if(1 == iteration){
        		block(1000);
        	} else
        	{
        		System.out.println("Agent " + getLocalName() + " asking for Transport");
        		ACLOWLMessage msg = new ACLOWLMessage(ACLMessage.QUERY_IF);
        		msg.addReceiver(new AID("pizzeria", AID.ISLOCALNAME));
        		msg.setOntology("http://www.co-ode.org/ontologies/pizza/pizza.owl");
        		try {
        			//Fill the content of the message with ontology
					msg.setContentOntology(m_queryOntology);
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		send(msg);
        		
        		finished = true;
        	}
        }
        
        public boolean done() {  
            return finished;
        }
        
    }
}

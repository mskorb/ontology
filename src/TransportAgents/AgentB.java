package TransportAgents;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jadeOWL.base.OntologyManager;
import jadeOWL.base.messaging.ACLOWLMessage;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * A simple example of an agent that receives a class query.
 */
public class AgentB extends Agent{

	private static final long serialVersionUID = -4291470359857743631L;
	OntologyManager ontologyManager;
	OWLOntology pizzaOntology;
	
	protected void setup(){
		System.out.println("Agent " + getLocalName() + " zaczyna");
		
		ontologyManager = new OntologyManager();
		
		//Load the pizza ontology
		File pizzaFile = new File("ontologies/transport.owl");
		try {
			pizzaOntology = ontologyManager.loadAndMapOntology(pizzaFile, "http://www.co-ode.org/ontologies/pizza/pizza.owl");
		
			//Listen for client messages
			addBehaviour(new RecievePizzaMessages(this));
		
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void takeDown(){
		//Clean up
		ontologyManager.removeAndUnmapAllOntologies();
	}
	
	class RecievePizzaMessages extends CyclicBehaviour{

		private static final long serialVersionUID = 7569347209298378146L;

		public RecievePizzaMessages(Agent a){
			super(a);
		}
		
		public void action() 
        {
		   System.out.println("Agent " + getLocalName() + " nasluchuje o transporcie");
		   ACLOWLMessage msg= (ACLOWLMessage) blockingReceive();
		   System.out.println("Agent " + getLocalName() + " received message");
           if (msg!=null){
        	   //Print the content of the message
               //System.out.println( " - " + myAgent.getLocalName() + " <- " + msg.getContent() );
               try {
            	//Extract the ontology from the message
				OWLOntology ontology = msg.getContentOntology(ontologyManager,myAgent);

				System.out.println("Received ontology:\n" + ontology);
				
				//Get query classes from the ontology
				Set<OWLClass> filteredSet = ontologyManager.getQueryManager().filterOWLQueryClasses(ontology);
				
				//Print the found query classes
				/*Iterator<OWLClass> iterator = filteredSet.iterator();
				System.out.println("Filtered query classes:");
				while(iterator.hasNext()){
					System.out.println(iterator.next());
				}*/
				
				if(!filteredSet.isEmpty()){
					//Get the instances that answer the query
					
					//Remember to include the received query ontology...
					//Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().getInstancesForClassQuery(filteredSet.iterator().next(), pizzaOntology, ontology);
					
					//...or import the ontologies into the query ontology and let the reasoner do all the work
					ontologyManager.addImportToOntology(ontology, pizzaOntology);
					Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().getInstancesForClassQuery(filteredSet.iterator().next(), ontology);
					
					//Print all answers 
					Iterator<OWLNamedIndividual> it = individuals.iterator();
					System.out.println("Query answer:");
					while(it.hasNext()){
						System.out.println(it.next());
					}
				}
				
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           }
           block();
        }
	}
	
}

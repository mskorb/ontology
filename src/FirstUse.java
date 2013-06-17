import jadeOWL.base.DataFactory;
import jadeOWL.base.ManchesterQueryBuilder;
import jadeOWL.base.OntologyManager;
import jadeOWL.base.QueryManager;

import java.io.File;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * A simple introduction to JadeOWL that presents how to set up references to JadeOWL classes,
 *  how to load, map and unload an ontology.
 */
public class FirstUse {

	OntologyManager m_ontologyManager;
	
	OWLOntologyManager m_owlOntologyManager;
	DataFactory m_dataFactory;
	OWLDataFactory m_owlDataFactory;
	QueryManager m_queryManager;
	ManchesterQueryBuilder m_queryBuilder;

	public FirstUse() throws OWLOntologyCreationException{
		
		//Prepare classes to work with ontologies
		m_ontologyManager = new OntologyManager();
		
		m_owlOntologyManager = m_ontologyManager.getOWLOntologyManager();
		m_dataFactory = m_ontologyManager.getDataFactory();
		m_owlDataFactory = m_ontologyManager.getOWLDataFactory();
		m_queryManager = m_ontologyManager.getQueryManager();
		
		m_queryBuilder = new ManchesterQueryBuilder();
		
		//load an ontology from the ontology file
		OWLOntology pizzaOntology = m_ontologyManager.loadOntology(new File("ontologies/Pizza.owl"));
			
		IRI webIRI = m_ontologyManager.getOntologyIRI(pizzaOntology);
		IRI localIRI = m_ontologyManager.getOntologyDocumentIRI(pizzaOntology);
			
		//print information about file and web IRI
		System.out.println("web IRI: " + webIRI);
		System.out.println("local IRI: " + localIRI);
			
		//Map the IRIs
		m_ontologyManager.addIRIMapping(webIRI, localIRI);
			
		//Check if the mapping works
		IRI testIRI = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl");
		System.out.println("Is Pizza.owl managed by this manager?: " + m_ontologyManager.containsOntology(testIRI));
			
		//Free the resources
		m_ontologyManager.removeAndUnmapOntology(pizzaOntology);
        
	}
	
	public static void main(String[] args){
		try {
			new FirstUse();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

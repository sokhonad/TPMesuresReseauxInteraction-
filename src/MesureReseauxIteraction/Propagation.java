package MesureReseauxIteraction;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceEdge;

public class Propagation {
  private Graph graph;
  private int fiabilite;
  private int nombreDeJour;
  private double degreMoyen;
  private double beta;
  private double mu;
  private int nbNonConvaincu;
  private Node noeudContamine;
  
  public double[] tab1;

  

  public Propagation(double beta, double mu, int nombreDeJour, int fiabilite) {
    this.degreMoyen = 6.62;
    this.beta = beta;
    this.mu = mu;
    this.fiabilite = fiabilite;
    this.nombreDeJour = nombreDeJour;
    System.setProperty("org.graphstream.ui", "swing");
    this.graph = new DefaultGraph("graph");
    FileSource fs = new FileSourceEdge();
    fs.addSink(this.graph);
    try {
      fs.readAll("Data/com-dblp.ungraph.txt");
    } catch( IOException e) {
      e.printStackTrace();
    } finally {
      fs.removeSink(this.graph);
    }

  }
  public Graph getGraph() {
    return graph;
  }

  public int getFiabilite() {
    return fiabilite;
  }

  public int getNombreDeJour() {
    return nombreDeJour;
  }

  public double getDegreMoyen() {
    return degreMoyen;
  }
  public void setDegreMoyen(double degreMoyen) {
    this.degreMoyen = degreMoyen;
  }
  public double getBeta() {
    return beta;
  }

  public double getMu() {
    return mu;
  }

  public int getNbNonConvaincu() {
    return nbNonConvaincu;
  }
  private void initialisationScenario(Graph graph) {
    graph.nodes().forEach(n -> n.clearAttributes());
    graph.nodes().forEach(n -> n.setAttribute("contaminer", Etat.SAIN));
  }

  private void infecterPatientZero(Graph graph) {
    Random random = new Random();
    int m = 0;
    int aleatoire;
    Node nodeContamine = null;
    int nbNoeudSain = (int) graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.SAIN).count();
    aleatoire = random.nextInt(nbNoeudSain + 1);
    Iterator<Node> it = graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.SAIN).iterator();
    while(it.hasNext() && m != aleatoire) {
      m++;
      nodeContamine = it.next();
    }
    nodeContamine.setAttribute("contaminer", Etat.CONTAMINE);
    this.noeudContamine = nodeContamine;
  }
  
  public void scenariosAucuneImunisation(Graph _graph) {
    double[] nbContamineMoyenne = new double[this.nombreDeJour + 1];
    double[] nbContamine = new double[this.nombreDeJour + 1];
    this.tab1 = new double[(this.nombreDeJour + 1) * 2];
    System.out.println("beta : " + this.beta + "    mu : " + this.mu);
    for (int i = 0 ; i < this.fiabilite ; i++) {
      this.initialisationScenario(_graph);
      this.infecterPatientZero(_graph);
      nbContamine = this.simulation(_graph);
      for (int j = 0 ; j < nbContamine.length ; j++) {
        nbContamineMoyenne[j] += nbContamine[j];
      }
      System.out.println("i : " + i); 
    }
    this.nbNonConvaincu = _graph.getNodeCount() - (int)(_graph.nodes().filter(n -> n.getAttribute("contamine") == Etat.CONVAINCU).count());
    for (int j = 0 ; j < nbContamine.length ; j++) {
      nbContamineMoyenne[j] = nbContamineMoyenne[j] / this.fiabilite;
      nbContamineMoyenne[j] = (nbContamineMoyenne[j] * 100)/this.nbNonConvaincu;
      this.tab1[(j * 2)] = j;
      this.tab1[(j * 2) + 1] = nbContamineMoyenne[j];
    }
  }

}

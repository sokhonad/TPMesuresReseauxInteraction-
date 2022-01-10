package MesureReseauxIteraction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.algorithm.Toolkit;
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
  public double[] tab2;
  public double[] tab3;



  

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
  
  public double[] simulation(Graph graph) {
    int nbContamine = 1;
    double[] nbContamineParJour = new double[this.nombreDeJour + 1];
    for (int i = 0 ;  i <= this.nombreDeJour ; i++) {
      nbContamineParJour[i] = nbContamine;
      graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.CONTAMINE).forEach(noeudContamine -> {
        double ran = Math.random();
        if (ran < this.beta) {
          noeudContamine.neighborNodes().filter(n -> n.getAttribute("contaminer") == Etat.SAIN).forEach(voisinC -> voisinC.setAttribute("contaminer", Etat.FUTURCONTAMINE));
        }
        if (ran < this.mu) {
          noeudContamine.setAttribute("contaminer", Etat.SAIN);
        }
      });
      graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.FUTURCONTAMINE).
      forEach(n -> n.setAttribute("contaminer", Etat.CONTAMINE));
      nbContamine = (int) graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.CONTAMINE).count();
    }
    return (nbContamineParJour);
  }
  public void scenariosAucuneImunisation(Graph graph) {
    double[] nbContamineMoyenne = new double[this.nombreDeJour + 1];
    double[] nbContamine = new double[this.nombreDeJour + 1];
    this.tab1 = new double[(this.nombreDeJour + 1) * 2];
    System.out.println("beta : " + this.beta + "    mu : " + this.mu);
    for (int i = 0 ; i < this.fiabilite ; i++) {
      this.initialisationScenario(graph);
      this.infecterPatientZero(graph);
      nbContamine = this.simulation(graph);
      for (int j = 0 ; j < nbContamine.length ; j++) {
        nbContamineMoyenne[j] += nbContamine[j];
      }
      System.out.println("i : " + i); 
    }
    this.nbNonConvaincu = graph.getNodeCount() - (int)(graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.CONVAINCU).count());
    for (int j = 0 ; j < nbContamine.length ; j++) {
      nbContamineMoyenne[j] = nbContamineMoyenne[j] / this.fiabilite;
      nbContamineMoyenne[j] = (nbContamineMoyenne[j] * 100)/this.nbNonConvaincu;
      this.tab1[(j * 2)] = j;
      this.tab1[(j * 2) + 1] = nbContamineMoyenne[j];
    }
  }
  
  public void scenariosImunisationAleatoire(Graph _graph) {
    double[] nbContamineMoyenne = new double[this.nombreDeJour + 1];
    double[] nbContamine = new double[this.nombreDeJour + 1];
    this.tab2 = new double[(this.nombreDeJour + 1) * 2];
    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nscénario de l'imunisation aléatoire : ");
    System.out.println("beta : " + this.beta + "    mu : " + this.mu);
    for (int i = 0 ; i < this.fiabilite ; i++) {
      this.initialisationScenario(_graph);
      initScenariosImunisationAleatoire(_graph);
      this.infecterPatientZero(_graph); 
      nbContamine = this.simulation(_graph);
      for (int j = 0 ; j < nbContamine.length ; j++) {
        nbContamineMoyenne[j] += nbContamine[j];
      }
      System.out.println("i : " + i);
    }
    this.nbNonConvaincu = _graph.getNodeCount() - (int)(_graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.CONVAINCU).count());
    for (int j = 0 ; j < nbContamine.length ; j++) {
      nbContamineMoyenne[j] = nbContamineMoyenne[j] / this.fiabilite;
      nbContamineMoyenne[j] = (nbContamineMoyenne[j] * 100)/this.nbNonConvaincu;
      this.tab2[(j * 2)] = j;
      this.tab2[(j * 2) + 1] = nbContamineMoyenne[j];
    }
  }
  
  public void scenariosImunisationSelective(Graph _graph) {
    double[] nbContamineMoyenne = new double[this.nombreDeJour + 1];
    this.tab3 = new double[(this.nombreDeJour + 1) * 2];
    double[] nbContamine = new double[this.nombreDeJour + 1];
    System.out.println("\n\n\n\n\n\n\n\nscénario de l'imunisation sélective : ");
    System.out.println("beta : " + this.beta + "    mu : " + this.mu);
    for (int iteration = 0 ; iteration < this.fiabilite ; iteration++) {
      this.initialisationScenario(_graph);
      initScenariosImunisationSelective(_graph);
      this.infecterPatientZero(_graph);
      nbContamine = this.simulation(_graph);
      for (int j = 0 ; j < nbContamine.length ; j++) {
        nbContamineMoyenne[j] += nbContamine[j];
      }
      System.out.println("itertion : " + iteration);
    }           
    this.nbNonConvaincu = _graph.getNodeCount() - (int)(_graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.CONVAINCU).count());
    for (int j = 0 ; j < nbContamine.length ; j++) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
      nbContamineMoyenne[j] = nbContamineMoyenne[j] / this.fiabilite;
      nbContamineMoyenne[j] = (nbContamineMoyenne[j] * 100)/this.nbNonConvaincu;
      this.tab3[(j * 2)] = j;
      this.tab3[(j * 2) + 1] = nbContamineMoyenne[j];
    } 
  }
  
  public void initScenariosImunisationSelective(Graph _graph) {
    _graph.nodes().forEach(n -> {
      if (Math.random() <= 0.5) {
        int nbVoisin = (int) n.neighborNodes().count();
        double[] alea = {Math.random() * nbVoisin};
        if (alea[0] % 1 != 0) {
          alea[0] += 1;
        }
        alea[0] = alea[0] - (alea[0] % 1);
        int[] i = {0};
        n.neighborNodes().forEach(voisin -> {
          i[0]++;
          if (i[0] == alea[0]) {
            voisin.setAttribute("contaminer", Etat.CONVAINCU);
          }
        });
      }
    });
  }
  
  public double[] getTab1() {
    return tab1;
  }
  public void setTab1(double[] tab1) {
    this.tab1 = tab1;
  }
  public void initScenariosImunisationAleatoire(Graph graph) {
    graph.nodes().forEach(n -> {
      if (Math.random() <= 0.5) {
        n.setAttribute("contaminer", Etat.CONVAINCU);
      }
    });
  }

  public double getMoyenneDegre(Graph graph) {
    return Toolkit.averageDegree(graph);
  }
  public double seuilEpidemiquetheoriqueReseauSansScenario(Graph graph) {
    this.initialisationScenario(graph);
    this.initScenariosImunisationAleatoire(graph);
    double seuilEpidemique =  this.degreMoyen / this.getMoyenneDegre(graph);
    return seuilEpidemique;
  }
  public void simulationSurLesGraph() {
//    this.scenariosAucuneImunisation(this.graph);
//    write("scenarios1.data", tab1);
//    this.scenariosImunisationAleatoire(graph);
//    write("scenarios2.data", tab2);
    this.scenariosImunisationSelective(graph);
    write("scenarios3.data", tab3);

  }
  
  private  void write(String filename, double[] tab) {
    double[] copie=new double[tab.length/2];
    int j=0;
    for (int i = 1; i < tab.length-1; i+=2) {
      copie[j]=tab[i];
      j++;
    }
    try {
      File file = new File(System.getProperty("user.dir") + "/" + filename);
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      for (int i = 0 ; i < copie.length-1 ; i++) {
        bw.write(i + "    " + copie[i]);
        bw.newLine();
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  

  public static void main(String... arg) {
    Propagation Propagation = new Propagation( Math.pow(7, -1), Math.pow(14, -1), 100, 3);
    Propagation.simulationSurLesGraph();
//    for (int i = 0; i < Propagation.getTab1().length; i++) {
//      System.out.println(Propagation.getTab1()[i]+"|");
//    }
//    ;
  }

}
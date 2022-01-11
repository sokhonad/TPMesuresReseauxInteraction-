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
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceEdge;

public class Propagation {
  private Graph graph;
  public Graph graphAleatoire;
  public Graph graphAleaBarabasiAlbert;
  private int fiabilite;
  private int nombreDeJour;
  private double degreMoyen;
  private double beta;
  private double mu;
  private int nbNonConvaincu;
  private Node noeudContamine;

  public double[] tableauDonneesGrapheScientique;
  public double[] tableauDonneesgraphAleaBarabasiAlbert;
  public double[] tableauDonneesgrapheAleatoire;





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
    graphAleatoire=MesureReseauxIteraction.graphAleatoireGraphStream(graph.getNodeCount(), Toolkit.averageDegree(graph));
    graphAleaBarabasiAlbert=MesureReseauxIteraction.grapheBarabasiAlbert(graph.getNodeCount(), Toolkit.averageDegree(graph));
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
    this.tableauDonneesGrapheScientique = new double[(this.nombreDeJour + 1) * 2];
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
      this.tableauDonneesGrapheScientique[(j * 2)] = j;
      this.tableauDonneesGrapheScientique[(j * 2) + 1] = nbContamineMoyenne[j];
    }
  }

  public void scenariosImunisationAleatoire(Graph graph) {
    double[] nbContamineMoyenne = new double[this.nombreDeJour + 1];
    double[] nbContamine = new double[this.nombreDeJour + 1];
    this.tableauDonneesgraphAleaBarabasiAlbert = new double[(this.nombreDeJour + 1) * 2];
    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nscénario de l'imunisation aléatoire : ");
    System.out.println("beta : " + this.beta + "    mu : " + this.mu);
    for (int i = 0 ; i < this.fiabilite ; i++) {
      this.initialisationScenario(graph);
      initScenariosImunisationAleatoire(graph);
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
      this.tableauDonneesgraphAleaBarabasiAlbert[(j * 2)] = j;
      this.tableauDonneesgraphAleaBarabasiAlbert[(j * 2) + 1] = nbContamineMoyenne[j];
    }
  }

  public void scenariosImunisationSelective(Graph graph) {
    double[] nbContamineMoyenne = new double[this.nombreDeJour + 1];
    this.tableauDonneesgrapheAleatoire = new double[(this.nombreDeJour + 1) * 2];
    double[] nbContamine = new double[this.nombreDeJour + 1];
    System.out.println("\n\n\n\n\n\n\n\nscénario de l'imunisation sélective : ");
    System.out.println("beta : " + this.beta + "    mu : " + this.mu);
    for (int iteration = 0 ; iteration < this.fiabilite ; iteration++) {
      this.initialisationScenario(graph);
      initScenariosImunisationSelective(graph);
      this.infecterPatientZero(graph);
      nbContamine = this.simulation(graph);
      for (int j = 0 ; j < nbContamine.length ; j++) {
        nbContamineMoyenne[j] += nbContamine[j];
      }
      System.out.println("itertion : " + iteration);
    }           
    this.nbNonConvaincu = graph.getNodeCount() - (int)(graph.nodes().filter(n -> n.getAttribute("contaminer") == Etat.CONVAINCU).count());
    for (int j = 0 ; j < nbContamine.length ; j++) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
      nbContamineMoyenne[j] = nbContamineMoyenne[j] / this.fiabilite;
      nbContamineMoyenne[j] = (nbContamineMoyenne[j] * 100)/this.nbNonConvaincu;
      this.tableauDonneesgrapheAleatoire[(j * 2)] = j;
      this.tableauDonneesgrapheAleatoire[(j * 2) + 1] = nbContamineMoyenne[j];
    } 
  }

  public void initScenariosImunisationSelective(Graph graph) {
    graph.nodes().forEach(n -> {
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
    return tableauDonneesGrapheScientique;
  }
  public void setTab1(double[] tab1) {
    this.tableauDonneesGrapheScientique = tab1;
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


  public void simulationSurGrapheReseauxScientique() {
    this.scenariosAucuneImunisation(this.graph);
    write("scenarios1ReseauxScientique.data", tableauDonneesGrapheScientique);
    this.scenariosImunisationAleatoire(graph);
    write("scenarios2ReseauxScientique.data", tableauDonneesgraphAleaBarabasiAlbert);
    this.scenariosImunisationSelective(graph);
    write("scenarios3ReseauxScientique.data", tableauDonneesgrapheAleatoire);

  }
  public void simulationSurGraphAleatoire() {
    this.scenariosAucuneImunisation(graphAleatoire);
    write("scenarios1GraphAleatoire.data", tableauDonneesGrapheScientique);
    this.scenariosImunisationAleatoire(graphAleatoire);
    write("scenarios2GraphAleatoire.data", tableauDonneesgraphAleaBarabasiAlbert);
    this.scenariosImunisationSelective(graphAleatoire);
    write("scenarios3GraphAleatoire.data", tableauDonneesgrapheAleatoire);

  }
  public void simulationSurGraphALBarabasiAlbert() {
    this.scenariosAucuneImunisation(graphAleaBarabasiAlbert);
    write("scenarios1graphAleaBarabasiAlbert.data", tableauDonneesGrapheScientique);
    this.scenariosImunisationAleatoire(graphAleaBarabasiAlbert);
    write("scenarios2ALBarabasiAlbert.data", tableauDonneesgraphAleaBarabasiAlbert);
    this.scenariosImunisationSelective(graphAleaBarabasiAlbert);
    write("scenarios3ALBarabasiAlbert.data", tableauDonneesgrapheAleatoire);

  }

  public double seuilEpidemiquetheoriqueReseauAleatoire() {
    double seuilEpidemique = 1 / (this.degreMoyen + 1);
    return seuilEpidemique;
  }

  private int getDegreMoyenSain(Node n) {
    int degreMoyen = (int) n.neighborNodes().filter(voisin -> voisin.getAttribute("contaminer") == Etat.SAIN).count();
    return degreMoyen;
  }

  private double[] getDegreMoyenSommetAccesibleParLeVirus(Graph graph) {
    double[] degreMoyen = {0, 0};
    Graph a = new SingleGraph("a");
    Graph b = new SingleGraph("b");
    a.addNode(this.noeudContamine.getId());
    while (a.getNodeCount() > 0) {
      graph.nodes().filter(n -> a.getNode(n.getId()) != null).forEach(n-> {
        n.neighborNodes().filter(nC -> b.getNode(nC.getId()) == null).filter(nb -> a.getNode(nb.getId()) == null).forEach(bVoisin -> a.addNode(bVoisin.getId()));
        a.removeNode(n.getId());
        b.addNode(n.getId());
      });
    }
    graph.nodes().filter(n -> b.getNode(n.getId()) != null).forEach(n -> {degreMoyen[0] += this.getDegreMoyenSain(n); degreMoyen[1] += Math.pow(this.getDegreMoyenSain(n), 2);});
    degreMoyen[0] = degreMoyen[0] / b.getNodeCount();
    degreMoyen[1] = degreMoyen[1] / b.getNodeCount();
    return degreMoyen;
  }

  public double seuilEpidemiquetheoriqueReseauScientifiqueScenarioImunisationAleatoire(Graph graph) {
    this.initialisationScenario(graph);
    this.initScenariosImunisationAleatoire(graph);
    this.infecterPatientZero(graph);
    double[] degreMoyen = this.getDegreMoyenSommetAccesibleParLeVirus(graph);
    double seuilEpidemique = degreMoyen[0] / degreMoyen[1];
    return seuilEpidemique;
  }

  public double seuilEpidemiquetheoriqueReseauScientifiqueScenarioImunisationSelective(Graph graph) {
    this.initialisationScenario(graph);
    this.initScenariosImunisationSelective(graph);
    this.infecterPatientZero(graph);
    double[] degreMoyen = this.getDegreMoyenSommetAccesibleParLeVirus(graph);
    double seuilEpidemique = degreMoyen[0] / degreMoyen[1];
    return seuilEpidemique;
  }



  public double seuilEpidemiquetheoriqueReseauSansScenario(Graph graph) {
    this.initialisationScenario(graph);
    this.initScenariosImunisationAleatoire(graph);
    double seuilEpidemique =  this.degreMoyen / this.getMoyenneDegre(graph);
    return seuilEpidemique;
  }


  public void affichedegreMoyenGroup0Et1(Graph graph) {
    this.initialisationScenario(graph);
    graph.forEach(n -> {
      n.setAttribute("groupe0", false);
      n.setAttribute("groupe1", false);
    });
    graph.nodes().forEach(n -> {
      if (Math.random() <= 0.5) {
        n.setAttribute("groupe0", true);
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
            voisin.setAttribute("groupe1", true);
          }
        });
      }
    });
    double[] degreMoyen = {0, 0};
    int[] nbNoeud = {0, 0};
    graph.nodes().forEach(n -> {
      if ((boolean)(n.getAttribute("groupe0")) == true) {
        nbNoeud[0]++;
        degreMoyen[0] += n.getDegree();
      }
      if ((boolean)(n.getAttribute("groupe1")) == true) {
        nbNoeud[1]++;
        degreMoyen[1] += n.getDegree();
      }
    });
    degreMoyen[0] = degreMoyen[0] / nbNoeud[0];
    degreMoyen[1] = degreMoyen[1] / nbNoeud[1];
    System.out.println("nombre de noeud groupe 0 " + nbNoeud[0] + " degre moyen groupe 0 : " + degreMoyen[0]);
    System.out.println("nombre de noeud groupe 1 " + nbNoeud[1] + " degre moyen groupe 1 : " + degreMoyen[1]);
  }


  private void testDeDifferenceSeuils() {
    System.out.println("seuil épidémique du réseau sans scénario : " + this.seuilEpidemiquetheoriqueReseauSansScenario(this.graph));
    System.out.println("seuil épidémique du réseau Aleatoire : " + this.seuilEpidemiquetheoriqueReseauAleatoire());
    System.out.println("seuil épidémique du réseau sans scénario : " + this.seuilEpidemiquetheoriqueReseauSansScenario(this.graphAleaBarabasiAlbert));

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
    Propagation.simulationSurGrapheReseauxScientique();
    Propagation.simulationSurGraphAleatoire();
    Propagation.simulationSurGraphALBarabasiAlbert();
    Propagation.testDeDifferenceSeuils();
    Propagation.affichedegreMoyenGroup0Et1(Propagation.getGraph());
  }

}
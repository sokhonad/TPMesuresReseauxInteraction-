package Graphe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceEdge;

public class Graphe {

  public Graph graph;

  public Graphe() {
    System.setProperty("org.graphstream.ui", "swing");
    this.graph = new DefaultGraph("graph");
    FileSource fs = new FileSourceEdge();
    fs.addSink(this.graph);
    System.setProperty("org.graphstream.ui", "swing");
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
  private void mesuresDeBase() {
    System.out.println("Nombre de nœuds: "+graph.getNodeCount());
    System.out.println("Nombre de liens: "+graph.getEdgeCount());
    System.out.println("Degré moyen: "+Toolkit.averageDegree(graph));
    System.out.println("le coefficient de clustering: "+Toolkit.averageClusteringCoefficient(graph));
    System.out.println("isConnected: "+Toolkit.isConnected(graph));
  }

  private static Graph graphAleatoireGraphStream(int taille,double degre) {
    Graph graphAleatoireGraphStream = new DefaultGraph("graphAleatoire");
    Generator gen = new RandomGenerator(degre, false, false);
    gen.addSink(graphAleatoireGraphStream);
    gen.begin();
    while (graphAleatoireGraphStream.getNodeCount() < taille) {
      gen.nextEvents();

    }
    gen.end();  
    return graphAleatoireGraphStream;
  }

  private  void write(String filename, int[] distributionDeDegre) {
    try {
      File file = new File(System.getProperty("user.dir") + "/" + filename);
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      for (int i = 0 ; i < distributionDeDegre.length ; i++) {
        bw.write(i + "    " + (double)distributionDeDegre[i]/graph.getNodeCount());
        bw.newLine();
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private static double[] getDistanceMoyenne(Graph graph) {
    Graph noeudTraite = new SingleGraph("noeudTraite");
    int nbNoeudMax = 1000;
    ArrayList<Integer> distList = new ArrayList<Integer>();
    double[] distributionDistance;
    int distance;
    double distanceMoyenne = 0;
    graph.edges().forEach(e -> e.setAttribute("poid", 1));
    Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "dist", "poid");
    dijkstra.init(graph);
    int i = 0;
    int s = (int)(Math.random() * ((graph.getNodeCount())));
    Node noeudSource = graph.getNode(s);
    noeudTraite.addNode(noeudSource.getId());
    dijkstra.setSource(noeudSource);
    dijkstra.compute();
    Node noeudATrouver = null;
    int t = 0;
    while (i < nbNoeudMax) {
      do {
        t = (int)(Math.random() * ((graph.getNodeCount())));
        noeudATrouver = graph.getNode(t);
      }while (noeudTraite.getNode(noeudATrouver.getId()) != null);
      if (noeudTraite.getNode(noeudATrouver.getId()) == null) {
        noeudTraite.addNode(noeudATrouver.getId());
      }
      distance = dijkstra.getPath(noeudATrouver).getNodeCount();
      distList.add(distance);
      distanceMoyenne += distance;
      i++;
    }
    int[] max = {0};
    distList.forEach(n -> {
      if (max[0] < n){
        max[0] = n;
      }
    });
    distributionDistance = new double[max[0] + 1];
    distList.forEach(n -> distributionDistance[n]++);
    for (int j = 0 ; j < distributionDistance.length ; j++) {
      distributionDistance[j] = distributionDistance[j] / nbNoeudMax;
    }
    distanceMoyenne = distanceMoyenne / nbNoeudMax;
    System.out.println("distance moyenne : " + distanceMoyenne);
    return distributionDistance;
  }
  
  
  public static Graph grapheBarabasiAlbert(int size, double degree) {
    Graph graphBarabasiAlbert = new SingleGraph("grapheBarabasiAlbert");
    Generator barabasiAlbertGenerator = new BarabasiAlbertGenerator((int) degree);
    barabasiAlbertGenerator.addSink(graphBarabasiAlbert);
    barabasiAlbertGenerator.begin();
    for (int i = 0; i < size; i++) {
        barabasiAlbertGenerator.nextEvents();
    }
    barabasiAlbertGenerator.end();
    return graphBarabasiAlbert;
}


  public static void main(String[] args) {
    Graphe g= new Graphe();
    //g.getGraph().display();
    g.mesuresDeBase();
    //g.write("distribition.data", Toolkit.degreeDistribution(g.getGraph()));
    getDistanceMoyenne(g.getGraph());

    System.out.println("resultat graphe aleatoire************");
    System.out.println("le coefficient de clustering de graphe aleatoire: ");
    System.out.println(Toolkit.averageClusteringCoefficient(graphAleatoireGraphStream(g.getGraph().getNodeCount(),Toolkit.averageDegree(g.getGraph()))));
    System.out.println("Aleatoire isConnected: ");
    System.out.println(Toolkit.isConnected(graphAleatoireGraphStream(g.getGraph().getNodeCount(),Toolkit.averageDegree(g.getGraph()))
        ));
   double tab[]= getDistanceMoyenne(graphAleatoireGraphStream(g.getGraph().getNodeCount(),Toolkit.averageDegree(g.getGraph())));
   for (int i = 0; i < tab.length; i++) {
    System.out.println(tab[i]);
  }
    System.out.println("fin programme");


  }
}

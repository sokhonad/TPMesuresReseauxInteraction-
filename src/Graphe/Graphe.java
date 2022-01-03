package Graphe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
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

  public static void main(String[] args) {
    Graphe g= new Graphe();
     //g.getGraph().display();
    g.mesuresDeBase();
    g.write("distribition.data", Toolkit.degreeDistribution(g.getGraph()));
    
    System.out.println("resultat graphe aleatoire");
    System.out.println("le coefficient de clustering de graphe aleatoire: ");
    System.out.println(Toolkit.averageClusteringCoefficient(graphAleatoireGraphStream(g.getGraph().getNodeCount(),Toolkit.averageDegree(g.getGraph()))));
    System.out.println("Aleatoire isConnected: ");
    System.out.println(Toolkit.isConnected(graphAleatoireGraphStream(g.getGraph().getNodeCount(),Toolkit.averageDegree(g.getGraph()))
));
    System.out.println("fin programme");


  }
}

package Graphe;

import java.io.IOException;

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
    //graph.display();
    try {
      fs.readAll("Data/com-dblp.ungraph.txt");
      mesures() ;
    } catch( IOException e) {
      e.printStackTrace();
    } finally {
      fs.removeSink(this.graph);
    }
  }
  private void mesures() {
    System.out.println("Nombre de nœuds: "+graph.getNodeCount());
    System.out.println("Nombre de liens: "+graph.getEdgeCount());
    System.out.println("Degré moyen: "+getDegreMoyen(graph));
  }
  private float getDegreMoyen(Graph graph) {
    float[] degreMoyen = {0};
    graph.nodes().forEach(noeud -> degreMoyen[0] += noeud.getDegree());
    degreMoyen[0] = degreMoyen[0] / graph.getNodeCount();
    return (degreMoyen[0]);
  }
  
  public static void main(String[] args) {
    Graphe g= new Graphe();
    System.out.println("fin");
    
  }
}

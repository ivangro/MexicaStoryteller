package mexica.engagement;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.*;
import java.util.*;
import mexica.MexicaParameters;
import subgraph.Solution;


/**
 * Graph with connections between similar atoms
 * @author Ivan Guerrero
 */
public class AtomConectivityGraph {
    private Graph<Atom, String> connectivityGraph;
    private List<Cluster> clusters;
    private String description;
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Determines the connections between the atoms in the given store.<br>
     * Two atoms are linked if their similarity value is higher than the ACAS_CONSTANT.
     * @param store The atom's store to be analyzed
     */
    public void calculateConnectivityGraph(AtomsStore store) {
        description = "Connectivity Graph\n";
        connectivityGraph = new SparseMultigraph<>();
        clusters = new ArrayList<>();
        int totalAtoms, totalNextActions;
        
        List<Atom> atoms = new ArrayList<>();
        for (Cell c : store.getCells()) {
            atoms.addAll(c.getAtoms());
        }
        totalAtoms = atoms.size();
        totalNextActions = 0;
        
        for (int i=0; i<atoms.size(); i++) {
            Atom a1 = atoms.get(i);
            totalNextActions += a1.getNextActions().size();
            connectivityGraph.addVertex(a1);
            for (int j=i+1; j<atoms.size(); j++) {
                Atom a2 = atoms.get(j);
                Solution sol = AtomComparer.compareInclusion(a1, a2);
                if (sol != null && sol.getSimilarity() >= MexicaParameters.ACAS_CONSTANT)
                    connectivityGraph.addEdge(a1.getAtomLabel() + "/" + a2.getAtomLabel(), a1, a2);
            }
        }
        description += "Atoms\n";
        for (Atom a : atoms) {
            Collection<Atom> neighbors = connectivityGraph.getNeighbors(a);
            if (neighbors != null)
                a.setConnectivityCount(neighbors, totalNextActions, totalAtoms);
            else
                a.setConnectivityCount(new ArrayList<Atom>(), totalNextActions, totalAtoms);
            //description += "\t" + a + " CONN: \t" + a.getConnectivityCount() + "\t" + a.getConnectivityClass() + "\n";
        }
        
        int clusterID = 0;
        Set<Atom> visited = new HashSet<>();
        boolean atomAdded;
        
        while (!atoms.isEmpty()) {
            Atom atom = atoms.remove(0);
            Set<Atom> cluster = new HashSet<>();
            cluster.add(atom);
            atomAdded = false;
            while (!cluster.isEmpty()) {
                Atom next = cluster.toArray(new Atom[0])[0];
                cluster.remove(next);
                Collection<Atom> neighbors = connectivityGraph.getNeighbors(next);
                if (neighbors != null) {
                    for (Atom neighbor : neighbors) {
                        if (!visited.contains(neighbor)) {
                            neighbor.setClusterID(clusterID);
                            cluster.add(neighbor);
                            visited.add(neighbor);
                            atomAdded = true;
                        }
                    }
                }
            }
            if (atomAdded) {
                clusterID++;
            }
        }
        
        atoms = new ArrayList<>();
        for (Cell c : store.getCells()) {
            atoms.addAll(c.getAtoms());
        }
        
        for (Atom a : atoms) {
            addAtomToCluster(a, totalAtoms);
            description += "\t" + a + ": \t" + a.getClusterID() + "\t" + a.getNeighborCount() + "\t" + a.getNeighborClass() + "\n";
        }
        description += "Clusters\n";
        DijkstraShortestPath<Atom, String> sp = new DijkstraShortestPath(connectivityGraph);
        
        for (Cluster c : clusters) {
            description += "\t" + c.getDescription() + "\n";
            description += "\t\t" + c.getAtomList() + "\n";
            List<String> path = determineDiameter(c, sp);
            c.setDiameter(path.size());
            description += "\t\tDiameter: " + c.getDiameter() + "\n";
            double avgMinDist = determineAverageMinDistance(c, sp);
            c.setAverageMinDistance(avgMinDist);
            description += "\t\tAvarage min distance: " + avgMinDist + "\n";
            double avgInDegree = determineAverageInDegree(c, connectivityGraph);
            description += "\t\tAverage In degree: " + avgInDegree + "\n";
        }
    }
    
    public Graph<Atom, String> getConnectivityGraph() {
        return connectivityGraph;
    }
    
    public List<Cluster> getClusters() {
        return clusters;
    }
    
    public Cluster getCluster(Atom a) {
        for (Cluster cluster : clusters) {
            if (cluster.containsAtom(a))
                return cluster;
        }
        return null;
    }

    private void addAtomToCluster(Atom a, int totalAtoms) {
        Cluster myCluster = new Cluster(totalAtoms);
        myCluster.setId(a.getClusterID());
        
        if (clusters.indexOf(myCluster) >= 0) {
            myCluster = clusters.get(clusters.indexOf(myCluster));
            myCluster.addAtom(a);
        }
        else {
            myCluster.addAtom(a);
            clusters.add(myCluster);
        }
    }
    
    /**
     * Determines the maximum distance between any two atoms inside the given clusters
     * @param cluster The cluster to be analyzed
     * @param sp The shortest paths inside the C-map
     * @return The longest shortest path between the nodes inside the cluster
     */
    public List<String> determineDiameter(Cluster cluster, DijkstraShortestPath<Atom, String> sp) {
        List<String> maxPath = new ArrayList<>();
        List<Atom> atoms = cluster.getAtomList();
        for (int i=0; i<atoms.size(); i++) {
            Atom source = atoms.get(i);
            for (int j=i+1; j<atoms.size(); j++) {
                Atom target = atoms.get(j);
                List<String> path = sp.getPath(source, target);
                if (path.size() > maxPath.size()) {
                    maxPath = path;
                }
            }
        }
        
        return maxPath;
    }
    
    public double determineAverageMinDistance(Cluster cluster, DijkstraShortestPath<Atom, String> sp) {
        double minDist = 0;
        int paths = 0;
        List<Atom> atoms = cluster.getAtomList();
        for (int i=0; i<atoms.size(); i++) {
            Atom source = atoms.get(i);
            for (int j=i+1; j<atoms.size(); j++, paths++) {
                Atom target = atoms.get(j);
                List<String> path = sp.getPath(source, target);
                minDist += path.size();
            }
        }
        
        if (paths > 0)
            minDist /= paths;
        
        return minDist;
    }
    
    public double determineAverageInDegree(Cluster cluster, Graph<Atom, String> graph) {
        double inDegree = 0;
        
        List<Atom> atoms = cluster.getAtomList();
        for (int i=0; i<atoms.size(); i++) {
            Atom source = atoms.get(i);
            inDegree += graph.getIncidentEdges(source).size();
        }
        
        if (!atoms.isEmpty())
            inDegree /= atoms.size();
        
        return inDegree;
    }
}
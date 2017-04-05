import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/**
 * @author Benjamin Meeder
 * @author Adam De Gala
 */
public class GraphProcessor 
{
	private class Vertex
	{
		public String name;
		boolean visited = false; //All vertices start out not visited. 
		ArrayList<String> outEdges = new ArrayList<>();
		protected Vertex(String name)
		{
			this.name = name;
		}
		public void add(String add)
		{
			outEdges.add(add);
		}
	}
	
	private class Graph
	{
		public HashMap<String, Vertex> GraphList = new HashMap<>();
		public int size;

		public Graph(int size)
		{
			this.size = size;
		}

		public void add(String start, String end)
		{
			if(GraphList.containsKey(start))
				GraphList.get(start).add(end);
			else
			{
				//We need to a new vertex
				GraphList.put(start, new Vertex(start));
				GraphList.get(start).add(end);
			}
			if(!GraphList.containsKey(end))
				GraphList.put(end,new Vertex(end));

		}

		public void addSCC(String start, String end)
		{
			if(GraphList.containsKey(start))
				GraphList.get(start).add(end); //Get the start vertex and add end to its list of edge
			else
			{
				GraphList.put(start, new Vertex(start));
				GraphList.get(start).add(end);
			}

		}

		public int outDegree(String v)
		{
			if(GraphList.containsKey(v))
				return GraphList.get(v).outEdges.size();
			else 
				return -1;
		}

		public void DFSprint(String name, String sccKey) {
			//Find Correct Vertex
			Vertex v = GraphList.get(name);
			System.out.print(v.name + " ");
			v.visited = true;
			sccGraph.addSCC(sccKey, v.name);
			for(String e : v.outEdges)
			{
				Vertex f = GraphList.get(e);
				if(!f.visited)
					DFSprint(f.name, sccKey);
			}
		}

		public boolean beenVisited(String name) {
			Vertex v = GraphList.get(name);
			return v.visited;
		}

		public void printGraph() {
			Set<String> keys = GraphList.keySet();
			for(String e : keys)
			{
				Vertex v = GraphList.get(e);
				System.out.print(v.name + " ");
				for(String f : v.outEdges)
				{
					System.out.print(f + " ");
				}
				System.out.println();
				System.out.println();
			}
		}

	}

	Graph graph;
	Graph grGraph;
	Graph sccGraph;
	public GraphProcessor (String graphData)
	{
		//Builds Graph
        try
		{
			File file = new File(graphData);
			Scanner in = new Scanner(file);
			int size = in.nextInt();
			graph = new Graph(size );
			
			//Question: This is O(n^2) to build graph, any way better?
			//I could sort it and make it nlogn
			while(in.hasNextLine())
			{
				String start = in.next();
				String end = in.next();
				if(in.hasNextLine())
					in.nextLine(); //Move Scanner to next line
				graph.add(start, end);
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		computeSCCGraph();
	}

	public int outDegree(String v)
	{
		return graph.outDegree(v);
	}
	
	public boolean sameComponent(String u, String v)
	{
		Set<String> keys = sccGraph.GraphList.keySet();
		for(String e : keys)
		{
			Vertex vertex = sccGraph.GraphList.get(e);
			if(vertex.outEdges.contains(u) && vertex.outEdges.contains(v))
				return true;
		}
		return false;
	}

    public ArrayList<String> componentVertices(String v) {
        ArrayList<String> out = new ArrayList<>();
        Set<String> keys = sccGraph.GraphList.keySet();
        for(String e : keys)
        {
            Vertex vertex = sccGraph.GraphList.get(e);
            if(vertex.outEdges.contains(v))
                out.addAll(vertex.outEdges);
        }
        return out;
    }

	public int largestComponents()
	{
		int max = 0;
		Set<String> keys = sccGraph.GraphList.keySet();
		for(String e : keys)
		{
			Vertex v = sccGraph.GraphList.get(e);
			if(v.outEdges.size() > max)
				max = v.outEdges.size();
		}
		return max;
	}
	public int numComponents()
	{
		return sccGraph.GraphList.size();	
	}
	public ArrayList<String> bfsPath(String u, String v)
	{
		Set<String> keys = graph.GraphList.keySet();
		for(String e : keys)
		{
			graph.GraphList.get(e).visited = false; //Make sure every node starts as unchecked.
		}

		
		LinkedList<pathHolder> queue = new LinkedList<>();
		graph.GraphList.get(u).visited = true;
		ArrayList<String> path = new ArrayList<>();
		path.add(u);
		queue.add(new pathHolder(u,path));
		
		while(!queue.isEmpty())
		{
			pathHolder p = queue.poll();
			Vertex vertex = graph.GraphList.get(p.name);
			for(String e : vertex.outEdges)
			{
				ArrayList<String> newPath = new ArrayList<>();
				newPath.addAll(p.path);
				newPath.add(e);
				if(e.equals(v))
					return newPath;
				else
					queue.add(new pathHolder(e,newPath));
				
			}
		}
		return new ArrayList<>();
	}
	
	private class pathHolder
	{
		public pathHolder(String name, ArrayList<String> path)
		{
			this.name = name;
			this.path = path;
		}
		public String name;
		public ArrayList<String> path;
	}
	
	
	private void computeSCCGraph()
	{
		//It is built into the vertex class to be unvisited at start
		//Get Finishes times. 
		Stack<Vertex> finishTimes = new Stack<>(); //Question: Can we use this Stack
		Set<String> keys = graph.GraphList.keySet();
		for(String e : keys)
		{
			if(!graph.beenVisited(e))
			{
				DFSordering(e, finishTimes);
			}
		}
		
		grGraph = new Graph(graph.size);  
		sccGraph = new Graph(graph.size); //Create the two addition graphs
		computeGR(); //Compute GR from graph;
		
		
		//System.out.println("Size of stack is " + finishTimes.size());
		//Now we have the finish times 
		while(finishTimes.size() != 0)
		{
			Vertex v = finishTimes.pop();//Find corresponding vertex in GR
			if(!grGraph.beenVisited(v.name))
			{
				grGraph.DFSprint(v.name, v.name);
				System.out.println();
				System.out.println();
			}
		}
	}
	
	private void DFSordering(String key, Stack<Vertex> finishTimes) 
	{
		Vertex v = graph.GraphList.get(key);
		graph.GraphList.get(key).visited = true;
		for(String e : v.outEdges)
		{
			
			Vertex f = graph.GraphList.get(e);
			if(!f.visited)
				DFSordering(f.name, finishTimes);

		}
		finishTimes.push(v); //After all vertexs are processed, push the graph on. 
		//System.out.println("Size of stack is " + finishTimes.size());
	}

	//Compute GR (Flipped directions)
	private void computeGR()
	{
		Set<String> keys = graph.GraphList.keySet();
		for(String e : keys)
		{
			Vertex v = graph.GraphList.get(e);
			for(String f : v.outEdges)
			{
				grGraph.add(f, v.name);
			}
		}
	}
	
	public String generateReport() {
        int max = 0;
        String maxString = "";
        Set<String> keys = graph.GraphList.keySet();
        for(String e : keys)
        {
            int current = outDegree(e);
			if(current > max) {
                max = current;
                maxString = e;
            }
        }
        String out = "Highest out degree: " + maxString + "\n";
        out += Integer.toString(max) + '\n';
        out += "Number of components of the graph: " + Integer.toString(numComponents()) + "\n";
        out += "Size of the largest component: " + Integer.toString(largestComponents()) + "\n";
        out += "";

		return out;
	}
}

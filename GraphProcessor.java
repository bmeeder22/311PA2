import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class GraphProccessor 
{
	//Class for vertex
	private class Vertex
	{
		public String name;
		boolean visited = false; //All vertexs start out unvistied. 
		ArrayList<String> outEdges = new ArrayList<String>();    //Question: should we have an ArrayList of vertex or strings?
		protected Vertex(String name)
		{
			this.name = name;
		}
		public void add(String add)
		{
			//Question do have to consider multiple out edges to the same vertex
			outEdges.add(add);
		}
	}
	
	//Class for graph, uses adjacency list. 
	private class Graph
	{
		public HashMap<String, Vertex> GraphList = new HashMap<String, Vertex>();
		//public Vertex[] sccPos; //public access of vertexs
		public int size;
		public Graph(int size)
		{
			//graphList = new Vertex[size];
			this.size = size;
		}
		public void add(String start, String end)
		{
			//System.out.println("added " +  start +" " + end);
			if(GraphList.containsKey(start))
				GraphList.get(start).add(end);//Get the start vertex and add end to its list of edge
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
			//System.out.println("added " +  start +" " + end);
			if(GraphList.containsKey(start))
				GraphList.get(start).add(end);//Get the start vertex and add end to its list of edge
			else
			{
				//We need to a new vertex
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
		public void DFSprint(String name, String sccKey)
		{
			//Find Correct Vertex
			Vertex v = GraphList.get(name);
			System.out.print(v.name + " ");
			v.visited = true;
			sccGraph.addSCC(sccKey, v.name);
			for(String e : v.outEdges)
			{
				Vertex f = GraphList.get(e);
				if(f.visited == false)
					DFSprint(f.name, sccKey);
			}
		}
		public boolean beenVisited(String name)
		{
			Vertex v = GraphList.get(name);
			return v.visited;
		}
		public void printGraph()
		{
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
	public GraphProccessor (String graphData)
	{
		//Builds Graph
		
		//Question: how are we handeling file exceptions
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
	
	//Returns out degree of V. 
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
	public int numComponets()
	{
		return sccGraph.GraphList.size();	
	}
	public ArrayList<String> bfsPath(String u, String v)
	{
		Set<String> keys = graph.GraphList.keySet();
		for(String e : keys)
		{
			graph.GraphList.get(e).visited = false; //Make sure everynode starts as unchecked. 
		}

		
		LinkedList<pathHolder> queue = new LinkedList<pathHolder>();
		graph.GraphList.get(u).visited = true;
		ArrayList<String> path = new ArrayList<String>();
		path.add(u);
		queue.add(new pathHolder(u,path));
		
		while(!queue.isEmpty())
		{
			pathHolder p = queue.poll();
			Vertex vertex = graph.GraphList.get(p.name);
			for(String e : vertex.outEdges)
			{
				ArrayList<String> newPath = new ArrayList<String>();
				newPath.addAll(p.path);
				newPath.add(e);
				if(e.equals(v))
					return newPath;
				else
					queue.add(new pathHolder(e,newPath));
				
			}
		}
		ArrayList<String> dummy = new ArrayList<String>();
		return dummy;
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
	public void tester()
	{
		System.out.println("Info about SCC ");
		System.out.println("Number of SCC's " +  numComponets());
		System.out.println("Largest SCC is "  + largestComponents());
		
	}
	
	
	private void computeSCCGraph()
	{
		//It is built into the vertex class to be unvisted at start
		//Get Finishes times. 
		Stack<Vertex> finishTimes = new Stack<Vertex>(); //Question: Can we use this Stack 
		Set<String> keys = graph.GraphList.keySet();
		for(String e : keys)
		{
			if(graph.beenVisited(e) == false)
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
			if(grGraph.beenVisited(v.name) == false)
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
			if(f.visited == false)
				DFSordering(f.name, finishTimes);

		}
		finishTimes.push(v); //After all vertexs are processed, push the graph on. 
		//System.out.println("Size of stack is " + finishTimes.size());
	}

	//Compute GR (Fliped directions)
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
	
}

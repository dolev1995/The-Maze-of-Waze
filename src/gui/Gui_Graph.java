package gui;



import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener; 
import java.util.LinkedList;
import java.util.List; 
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import utils.Point3D;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.Serializable;

import algorithms.Graph_Algo;
import dataStructure.graph;
import dataStructure.node_data;
import dataStructure.edge_data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Gui_Graph extends JFrame implements ActionListener ,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected graph g;
	private static int if_change;
	int minx= Integer.MAX_VALUE;
	int miny= Integer.MAX_VALUE;
	public Gui_Graph()
	{
		this.g = null;
		init();
	}
	public Gui_Graph(graph n)
	{
		this.g = n;
		init();
		viewDial.start();
	}
	public void init()
	{
		this.setSize(500, 500);
		this.setTitle("the maze of waze");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);
		Menu menu = new Menu("menu");
		menuBar.add(menu);
		Menu algo = new Menu("algo");
		menuBar.add(algo);

		MenuItem saveing = new MenuItem("save the graph");
		saveing.addActionListener(this);

		MenuItem loading = new MenuItem("load the graph");
		loading.addActionListener(this);

		MenuItem Drawgraph = new MenuItem("Draw graph");
		Drawgraph.addActionListener(this);

		MenuItem shortestPathDist = new MenuItem("shortest Path Dist");
		shortestPathDist.addActionListener(this);
		MenuItem shortestPathTrace = new MenuItem("shortest Path route");
		shortestPathTrace.addActionListener(this);
		MenuItem TSP = new MenuItem("TSP");
		TSP.addActionListener(this);
		
		MenuItem connect = new MenuItem("connect");
		connect.addActionListener(this);
		

		menu.add(saveing);
		menu.add(loading);
		algo.add(Drawgraph);
		algo.add(shortestPathDist);
		algo.add(connect);
		algo.add(shortestPathTrace);
		algo.add(TSP);
		if(this.g!=null) {
			Collection<node_data> b= this.g.getV()	;
			Iterator<node_data> iter=b.iterator();

			while(iter.hasNext()) {
				node_data c = iter.next();
				Point3D of_c= c.getLocation();
				minx= Math.min(minx, of_c.ix());
				miny= Math.min(miny, of_c.iy());
			}
if(minx<0) minx= Math.abs(minx);
if(miny<80)miny= Math.abs(miny)+60;
			}
		
	}


	@Override
	public void actionPerformed(ActionEvent Command)
	{
		String str = Command.getActionCommand();		
		switch(str) 
		{
		case "save the graph":
			Graph_Algo gg = new Graph_Algo();
			gg.init(this.g);
			JFrame parentFrame = new JFrame();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify a file to save");   
			int userSelection = fileChooser.showSaveDialog(parentFrame);

			if (userSelection == JFileChooser.APPROVE_OPTION)
			{
				File fileToSave = fileChooser.getSelectedFile();
				String file= fileToSave.getAbsolutePath();
				gg.save(file);		
				System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			}
			break;
		case "load the graph":
			Graph_Algo g_a = new Graph_Algo();
			JFrame parentFrame1 = new JFrame();
			JFileChooser fileChooser1 = new JFileChooser();
			fileChooser1.setDialogTitle("Specify a file to load");   
			int userSelection1 = fileChooser1.showOpenDialog(parentFrame1);
			if (userSelection1 == JFileChooser.APPROVE_OPTION) 
			{
				File fileToLoad = fileChooser1.getSelectedFile();
				String file= fileToLoad.getAbsolutePath();
				g_a.init(file);
				this.g=g_a.copy();
				repaint();
				System.out.println("Load from file: " + fileToLoad.getAbsolutePath());
			}
			break;
		case "TSP":
			TSP(this.getGraphics());
			break;
		case "Draw graph":
			repaint();
			break;
		case "shortest Path route":
			shortpath(this.getGraphics());
			break;
		case "shortest Path Dist":
			JFrame in = new JFrame();
			try 
			{
				String source = JOptionPane.showInputDialog(in,"the key of source = ");
				String dest = JOptionPane.showInputDialog(in,"the key of dest = ");

				Graph_Algo a = new Graph_Algo();
				a.init(this.g);
				if(this.g.getNode(Integer.parseInt(source))==null||this.g.getNode(Integer.parseInt(dest))==null) 
				{
			    JOptionPane.showMessageDialog(in, "one or more frome vertex are givven not exist");
			    break;

				}
				double ans = a.shortestPathDist(Integer.parseInt(source), Integer.parseInt(dest));
				String out = Double.toString(ans);	
				JOptionPane.showMessageDialog(in, "shortest Path Dist = " + out);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			break;
		case "connect":
			isConnected();
			
			break;
		}
	}


	public void  paint(Graphics d)
	{
			

		super.paint(d);
		if(this.g != null)
		{
			Collection <node_data> node = g.getV();
			for (node_data node_data : node)
			{
				Point3D p = node_data.getLocation();
				d.setColor(Color.gray);
				d.fillOval((p.ix()+minx),(p.iy()+miny),9,9);

				d.setColor(Color.RED);
				d.drawString(Integer.toString(node_data.getKey()), (p.ix()+minx)-3, (p.iy()+miny)-3);

				
				Collection<edge_data> edges = g.getE(node_data.getKey());
				if(edges!=null) {
					for (edge_data e : edges)
					{	
						d.setColor(Color.GREEN);
						((Graphics2D) d).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
						Point3D p2 = g.getNode(e.getDest()).getLocation();
						d.drawLine((p.ix()+minx)+5, (p.iy()+miny)+5, (p2.ix()+minx)+5, (p2.iy()+miny)+5);
						d.setColor(Color.MAGENTA);
						d.fillOval((int)(((p.ix()+minx)*0.2)+(0.8*(p2.ix()+minx)))+2, (int)(((p.iy()+miny)*0.2)+(0.8*(p2.iy()+miny))), 9, 9);
						String sss = ""+String.valueOf(e.getWeight());
						d.drawString(sss, 1+(int)(((p.ix()+minx)*0.2)+(0.8*(p2.ix()+minx))), (int)(((p.iy()+miny)*0.2)+(0.8*(p2.iy()+miny)))-2);
					}
				}
			}	
		}

		
		}
	Runnable b = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	};
	Thread viewDial = new Thread(){

		public void run() {
			while(true) {
				if(Gui_Graph.if_change==g.getMC()) {
					try {
						synchronized (g) {


							g.wait();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if_change=g.getMC();
				repaint();
			}

		}
	};
	
	public void isConnected()
	{
		JFrame in = new JFrame();
		Graph_Algo graph = new Graph_Algo();
		graph.init(this.g);
		boolean connect = graph.isConnected();
		if (connect == true) {
			JOptionPane.showMessageDialog(in, "connect");
		}
		else {
			JOptionPane.showMessageDialog(in, "no connect");
		}
		
	}
	public void shortpath(Graphics d)
	{
		JFrame in = new JFrame();
		try 
		{
			String source = JOptionPane.showInputDialog(in,"the key of source = ");
			String dest = JOptionPane.showInputDialog(in,"the key of dest = ");

			Graph_Algo a = new Graph_Algo();
			a.init(this.g);
			if(this.g.getNode(Integer.parseInt(source))==null||this.g.getNode(Integer.parseInt(dest))==null) 
			{
		    JOptionPane.showMessageDialog(in, "one or more frome vertex are givven not exist");

			}
			else {
				((Graphics2D) d).setStroke(new BasicStroke(8,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			List<node_data> b= a.shortestPath(Integer.parseInt(source), Integer.parseInt(dest));
			Iterator<node_data> iter= b.iterator();
			node_data first = iter.next();
			System.out.println(first);

			while(iter.hasNext())
			{
				Point3D p = first.getLocation();
				d.setColor(Color.gray);
				d.fillOval((p.ix()+minx),(p.iy()+miny),16,16);

				node_data second = iter.next();
				System.out.println(second);

				Point3D p2 = second.getLocation();
				d.setColor(Color.ORANGE);
				d.drawLine((p.ix()+minx)+5, (p.iy()+miny)+5, (p2.ix()+minx)+5, (p2.iy()+miny)+5);
				d.setColor(Color.BLACK);
				d.setFont(new Font("Default", 2, 30) );
				d.drawString(Integer.toString(first.getKey()), (p.ix()+minx)-3, (p.iy()+miny)-3);
				d.setColor(Color.gray);
				d.fillOval((p2.ix()+minx),(p2.iy()+miny),16,16);
				d.setColor(Color.BLACK);
				d.drawString(Integer.toString(second.getKey()), (p2.ix()+minx)-3, (p2.iy()+miny)-3);

				d.setColor(Color.MAGENTA);
				d.fillOval((int)(((p.ix()+minx)*0.2)+(0.8*(p2.ix()+minx)))+2, (int)(((p.iy()+miny)*0.2)+(0.8*(p2.iy()+miny))), 12, 12);
				String sss = ""+String.valueOf(this.g.getEdge(first.getKey(), second.getKey()).getWeight());
				d.drawString(sss, 1+(int)(((p.ix()+minx)*0.2)+(0.8*(p2.ix()+minx))), (int)(((p.iy()+miny)*0.2)+(0.8*(p2.iy()+miny)))-2);
				first=second;
		} }}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	private void TSP(Graphics d) {
		JFrame in = new JFrame();
		try 
		{
			String targets_String= JOptionPane.showInputDialog(in,"enter targets split by ','  ");
			String arr[]= targets_String.split(",");
			List<Integer> targets = new ArrayList<Integer>();
			for(int i=0;i<arr.length;i++) {
				try {
					targets.add(Integer.parseInt(arr[i]));
				}
				catch(Exception E) {
					JOptionPane.showMessageDialog(in, "one or more frome targets are given not valid , please check that you insert INTEGER split by \",\" ");
				}
			}

			Graph_Algo a = new Graph_Algo();
			a.init(this.g);
			List<node_data> b= a.TSP(targets);
			if(b==null) {
				
				JOptionPane.showMessageDialog(in, "there isnt a path(or this targets isnt SCC or you are insert non-exist vertex in targets");
			}
			((Graphics2D) d).setStroke(new BasicStroke(8,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		Iterator<node_data> iter= b.iterator();
		node_data first = iter.next();
		System.out.println(first);

		while(iter.hasNext())
		{
			Point3D p = first.getLocation();
			d.setColor(Color.gray);
			d.fillOval((p.ix()+minx),(p.iy()+miny),16,16);

			node_data second = iter.next();
			System.out.println(second);

			Point3D p2 = second.getLocation();
			d.setColor(Color.ORANGE);
			d.drawLine((p.ix()+minx)+5, (p.iy()+miny)+5, (p2.ix()+minx)+5, (p2.iy()+miny)+5);
			d.setColor(Color.BLACK);
			d.setFont(new Font("Default", 2, 30) );
			d.drawString(Integer.toString(first.getKey()), (p.ix()+minx)-3, (p.iy()+miny)-3);
			d.setColor(Color.gray);
			d.fillOval((p2.ix()+minx),(p2.iy()+miny),16,16);
			d.setColor(Color.BLACK);
			d.drawString(Integer.toString(second.getKey()), (p2.ix()+minx)-3, (p2.iy()+miny)-3);

			d.setColor(Color.MAGENTA);
			d.fillOval((int)(((p.ix()+minx)*0.2)+(0.8*(p2.ix()+minx)))+2, (int)(((p.iy()+miny)*0.2)+(0.8*(p2.iy()+miny))), 12, 12);
			String sss = ""+String.valueOf(this.g.getEdge(first.getKey(), second.getKey()).getWeight());
			d.drawString(sss, 1+(int)(((p.ix()+minx)*0.2)+(0.8*(p2.ix()+minx))), (int)(((p.iy()+miny)*0.2)+(0.8*(p2.iy()+miny)))-2);
			first=second;
		}}
		catch(Exception e) {

		}

	}
	}



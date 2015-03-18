package com.project.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JFrame;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphEvent;
import org.gephi.graph.api.GraphEvent.EventType;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import processing.core.PApplet;

public class GraphToolkitExample {

    public void GraphPanel(File file) throws FileNotFoundException {
        //Init a project - and therefore a workspace
        /*ProjectController - Manage projects and workspaces states*/
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        /*create a new project*/
        pc.newProject();
        
        /*
         Workspace - has a set of modules graphs.
         This class has a Lookup mechanism to let modules store their model in the
         workspace is lookup and query it when needed.
         Outherwise, it's in the workspace that's stored the current model of graph.
         */
        Workspace workspace = pc.getCurrentWorkspace();

        //Import file
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        container = importController.importFile(file);
        container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //Preview configuration
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        
        
        
       
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 10f);
        previewModel.getProperties().putValue(PreviewProperty.CATEGORY_EDGE_ARROWS, 10f);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        previewController.refreshPreview();

        //New Processing target, get the PApplet
        ProcessingTarget target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
        PApplet applet = target.getApplet();
        applet.init();

        //Refresh the preview and reset the zoom
        previewController.render(target);
        target.refresh();
        target.resetZoom();

        //Add the applet to a JFrame and display
        JFrame frame = new JFrame("Systematic Review of MOJO");
        frame.setLayout(new BorderLayout());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(applet, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public GraphToolkitExample(File filename, String url, String nameFile) throws FileNotFoundException {

        //GraphPanel(filename);

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);

        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        /*AttributeModel - Represent the data of model (edges and nodes)*/
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
       
        /*GraphModel - represents the  structure of graph */
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        
        /*PreviewModel - contains properties of model*/
        PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        
        /*ImportController - controls the importation of file. */
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);

        //Import file
        Container container;
       
        try {
            // File file = new File(getClass().getResource("/org/gephi/toolkit/demos/resources/polblogs.gml").toURI());
            container = importController.importFile(filename);
            container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        UndirectedGraph graph = graphModel.getUndirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());

        //Run YifanHuLayout for 100 passes - The layout always takes the current visible view
        ForceAtlas2Builder layoutBuilder = new ForceAtlas2Builder();
        ForceAtlas2 layout = layoutBuilder.buildLayout();
        layout.setGraphModel(graphModel);

        layout.resetPropertiesValues();
         layout.setBarnesHutOptimize(true);
         layout.setGravity(2.0);
         layout.setEdgeWeightInfluence(0.0);
         layout.setThreadsCount(2);
         layout.setOutboundAttractionDistribution(true);
         layout.setScalingRatio(3.0);
         layout.setJitterTolerance(0.05);
         layout.initAlgo();

        for (int i = 0; i < 200 && layout.canAlgo(); i++) {
         layout.goAlgo();
         }
         layout.endAlgo();
        
        //Get Centrality
        GraphDistance distance = new GraphDistance();
        distance.setDirected(true);
        distance.execute(graphModel, attributeModel);

		//Rank color by Degree
		/*Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
         AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_COLOR);
         colorTransformer.setColors(new Color[]{new Color(0xFEF0D9), new Color(0xB30000)});
         rankingController.transform(degreeRanking,colorTransformer);*/
		//List node columns
        /*AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
         AttributeModel model2 = ac.getModel();
         for (AttributeColumn col : model2.getNodeTable().getColumns()) {
         System.out.println(col);
         }*/
        //AttributeColumn sourceCol = model2.getNodeTable().getColumn("newcomer");
        //System.out.println("teste: "+ sourceCol.toString());
		/*for (Node n : graphModel.getGraph().getNodes()) {
         //System.out.println(n.getNodeData().getAttributes().getValue("newcomer"));
         //n.getNodeData().setLabel((String)n.getNodeData().getAttributes().getValue("Id"));
         if(n.getNodeData().getAttributes().getValue("newcomer").equals("true")){
         n.getNodeData().setColor(1f, 0.6f, 1f);
         n.getNodeData().setLabel((String)n.getNodeData().getAttributes().getValue("Id"));
         }else{
         n.getNodeData().setLabel("");
         }
         }*/
        /*for (Edge e : graphModel.getGraph().getEdges()) {
         e.getEdgeData().setLabel(e.getSource().getNodeData().getId() + " - " + e.getTarget().getNodeData().getId());
         }*/
        for (Node n : graphModel.getGraph().getNodes()) {
            if (n.getNodeData().getLabel().equals("Chaves-etal:2013")) {
                n.getNodeData().setColor(1f, 0f, 0f);
            }
        }

        //Rank size by centrality
        AttributeColumn centralityColumn = attributeModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Ranking centralityRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, centralityColumn.getId());
        AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
        sizeTransformer.setMinSize(5);
        sizeTransformer.setMaxSize(10);
        rankingController.transform(centralityRanking, sizeTransformer);

        //Properties of edges
        model.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.FALSE);
        model.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.BLACK));
        model.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.TRUE);
        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));

        // Properties of nodes
        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.FALSE);
        model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(5));

        //Export
        ExportController pdf = Lookup.getDefault().lookup(ExportController.class);

        try {
            String name = url.concat(nameFile).concat(".pdf");
            pdf.exportFile(new File(name));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}

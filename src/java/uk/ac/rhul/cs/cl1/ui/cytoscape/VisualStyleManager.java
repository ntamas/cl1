package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.Color;
import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.LinearNumberToColorInterpolator;
import cytoscape.visual.mappings.ObjectMapping;

/**
 * Class responsible for registering and maintaining ClusterONE related
 * visual styles in Cytoscape.
 * 
 * @author ntamas
 */
public class VisualStyleManager {
	/**
	 * VizMapper visual style name for coloring nodes by their status
	 */
	public static final String VISUAL_STYLE_BY_STATUS = "ClusterONE - Status";
	
	/**
	 * VizMapper visual style name for coloring nodes by their status
	 */
	public static final String VISUAL_STYLE_BY_AFFINITY = "ClusterONE - Affinity";
	
	/**
	 * Ensure that the ClusterONE VizMapper styles are registered
	 * 
	 * @param  recreate   whether to re-create the styles even if they exist
	 */
	public static void ensureVizMapperStylesRegistered(boolean recreate) {
		VisualMappingManager visualMappingManager = Cytoscape.getVisualMappingManager();
		
		if (visualMappingManager == null)
			return;      /* no visual mapping manager, we cannot do anything */
		
		CalculatorCatalog calculatorCatalog = visualMappingManager.getCalculatorCatalog();
		if (calculatorCatalog == null)
			return;      /* no calculator catalog, we cannot do anything */
		
		ensureVisualStyleByStatusRegistered(calculatorCatalog, recreate);
		ensureVisualStyleByAffinityRegistered(calculatorCatalog, recreate);
	}
	
	/**
	 * Ensure that the visual style that colors nodes by their statuses is registered
	 * 
	 * @param  catalog    the Cytoscape {@link CalculatorCatalog} where the style
	 *                    has to be registered
	 * @param  recreate   whether to re-create the styles even if they exist
	 */
	private static void ensureVisualStyleByStatusRegistered(
			CalculatorCatalog catalog, boolean recreate) {
		if (catalog.getVisualStyleNames().contains(VISUAL_STYLE_BY_STATUS)) {
			if (!recreate)
				return;
			catalog.removeVisualStyle(VISUAL_STYLE_BY_STATUS);
		}
		
		/* Get the default visual style (if exists) */
		VisualStyle defaultStyle = catalog.getVisualStyle("default");
		if (defaultStyle == null)
			defaultStyle = new VisualStyle("default");
		
		VisualStyle myStyle = new VisualStyle(defaultStyle, VISUAL_STYLE_BY_STATUS);
		NodeAppearanceCalculator nac = myStyle.getNodeAppearanceCalculator();
		
		/* Create the node color calculator */
		DiscreteMapping colorMapping = new DiscreteMapping(Color.WHITE, CytoscapePlugin.ATTRIBUTE_STATUS, ObjectMapping.NODE_MAPPING);
		colorMapping.putMapValue("Outlier", Color.LIGHT_GRAY);
		colorMapping.putMapValue("Cluster", Color.RED);
		colorMapping.putMapValue("Overlap", Color.ORANGE);
		Calculator nodeColorCalculator = new BasicCalculator("Vertex color calculator", colorMapping, VisualPropertyType.NODE_FILL_COLOR);
		
		/* Create the node shape calculator */
		DiscreteMapping shapeMapping = new DiscreteMapping(NodeShape.RECT, CytoscapePlugin.ATTRIBUTE_STATUS, ObjectMapping.NODE_MAPPING);
		shapeMapping.putMapValue("Outlier", NodeShape.ELLIPSE);
		shapeMapping.putMapValue("Cluster", NodeShape.RECT);
		shapeMapping.putMapValue("Overlap", NodeShape.DIAMOND);
		Calculator nodeShapeCalculator = new BasicCalculator("Vertex shape calculator", shapeMapping, VisualPropertyType.NODE_SHAPE);
		
		nac.setCalculator(nodeColorCalculator);
		nac.setCalculator(nodeShapeCalculator);
		
		myStyle.setNodeAppearanceCalculator(nac);
		
		catalog.addVisualStyle(myStyle);
	}
	
	/**
	 * Ensure that the visual style that colors nodes by their affinities to
	 * a cluster is registered
	 * 
	 * @param  catalog    the Cytoscape {@link CalculatorCatalog} where the style
	 *                    has to be registered
	 * @param  recreate   whether to re-create the styles even if they exist
	 */
	private static void ensureVisualStyleByAffinityRegistered(
			CalculatorCatalog catalog, boolean recreate) {
		if (catalog.getVisualStyleNames().contains(VISUAL_STYLE_BY_AFFINITY)) {
			if (!recreate)
				return;
			catalog.removeVisualStyle(VISUAL_STYLE_BY_AFFINITY);
		}
		
		/* Get the maximum absolute value corresponding to the affinity attribute (if any) */
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		double maxAbsValue = 0.0;
		
		@SuppressWarnings("unchecked")
		List<Node> nodeList = (List<Node>)currentNetwork.nodesList();
		for (Node node: nodeList) {
			Object obj = nodeAttrs.getAttribute(node.getIdentifier(), CytoscapePlugin.ATTRIBUTE_AFFINITY);
			if (obj == null)
				continue;
			
			try {
				Double affinity = (Double)obj;
				if (Math.abs(affinity) > maxAbsValue)
					maxAbsValue = Math.abs(affinity);
			} catch (ClassCastException ex) {
			}
		}
		
		/* Get the default visual style (if exists) */
		VisualStyle defaultStyle = catalog.getVisualStyle("default");
		if (defaultStyle == null)
			defaultStyle = new VisualStyle("default");
		
		VisualStyle myStyle = new VisualStyle(defaultStyle, VISUAL_STYLE_BY_AFFINITY);
		NodeAppearanceCalculator nac = myStyle.getNodeAppearanceCalculator();
		
		/* Create the node color calculator */
		ContinuousMapping colorMapping = new ContinuousMapping(Color.WHITE, ObjectMapping.NODE_MAPPING);
		Color minColor = Color.blue;
		Color midColor = Color.white;
		Color maxColor = Color.red;
		colorMapping.setControllingAttributeName(CytoscapePlugin.ATTRIBUTE_AFFINITY, Cytoscape.getCurrentNetwork(), false);
		colorMapping.setInterpolator(new LinearNumberToColorInterpolator());
		colorMapping.addPoint(-maxAbsValue, new BoundaryRangeValues(minColor, minColor, minColor));
		colorMapping.addPoint(0.0, new BoundaryRangeValues(midColor, midColor, midColor));
		colorMapping.addPoint(maxAbsValue, new BoundaryRangeValues(maxColor, maxColor, maxColor));
		Calculator nodeColorCalculator = new BasicCalculator("Vertex color calculator", colorMapping, VisualPropertyType.NODE_FILL_COLOR);
		
		/* Create the node shape calculator */
		DiscreteMapping shapeMapping = new DiscreteMapping(NodeShape.RECT, CytoscapePlugin.ATTRIBUTE_STATUS, ObjectMapping.NODE_MAPPING);
		shapeMapping.putMapValue("Outlier", NodeShape.ELLIPSE);
		shapeMapping.putMapValue("Cluster", NodeShape.RECT);
		shapeMapping.putMapValue("Overlap", NodeShape.DIAMOND);
		Calculator nodeShapeCalculator = new BasicCalculator("Vertex shape calculator", shapeMapping, VisualPropertyType.NODE_SHAPE);
		
		nac.setCalculator(nodeColorCalculator);
		nac.setCalculator(nodeShapeCalculator);
		
		myStyle.setNodeAppearanceCalculator(nac);
		
		catalog.addVisualStyle(myStyle);
	}
	
	/**
	 * Updates the continuous mapping used to colour the vertices by their affinities to
	 * reflect the new range of affinity values after some of the attribute values changed.
	 */
	public static void updateAffinityStyleRange() {
		// TODO Auto-generated method stub
	}
}

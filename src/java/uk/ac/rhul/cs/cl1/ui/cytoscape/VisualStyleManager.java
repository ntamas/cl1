package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.Color;

import cytoscape.Cytoscape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;

/**
 * Class responsible for registering and maintaining Cluster ONE related
 * visual styles in Cytoscape.
 * 
 * @author ntamas
 */
public class VisualStyleManager {
	/**
	 * VizMapper visual style name for coloring nodes by their status
	 */
	public static final String VISUAL_STYLE_BY_STATUS = "Cluster ONE - Status";
	
	/**
	 * VizMapper visual style name for coloring nodes by their status
	 */
	public static final String VISUAL_STYLE_BY_AFFINITY = "Cluster ONE - Affinity";
	
	/**
	 * Ensure that the Cluster ONE VizMapper styles are registered
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
		
		VisualStyle myStyle = new VisualStyle(VISUAL_STYLE_BY_STATUS);
		NodeAppearanceCalculator nac = myStyle.getNodeAppearanceCalculator();
		
		DiscreteMapping colorMapping = new DiscreteMapping(Color.WHITE, CytoscapePlugin.ATTRIBUTE_STATUS, ObjectMapping.NODE_MAPPING);
		colorMapping.putMapValue("Outlier", Color.LIGHT_GRAY);
		colorMapping.putMapValue("Cluster", Color.RED);
		colorMapping.putMapValue("Overlap", Color.ORANGE);
		Calculator nodeColorCalculator = new BasicCalculator("Vertex color calculator", colorMapping, VisualPropertyType.NODE_FILL_COLOR);
		
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
}

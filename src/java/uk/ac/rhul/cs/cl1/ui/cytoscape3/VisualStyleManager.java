package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.Color;
import java.awt.Paint;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

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
	 * The ClusterONE plugin app in which this visual style manager lives.
	 */
	private ClusterONECytoscapeApp app;
	
	/**
	 * Mapping from affinity scores to colors in the affinity visual style.
	 */
	private ContinuousMapping<Double,Paint> affinityColorMapping;
	
	/**
	 * The visual style for coloring nodes by their affinity.
	 */
	private VisualStyle colorNodesByAffinityVisualStyle;
	
	/**
	 * The visual style for coloring nodes by their status.
	 */
	private VisualStyle colorNodesByStatusVisualStyle;
	
	/**
	 * Constructor.
	 */
	public VisualStyleManager(ClusterONECytoscapeApp app) {
		this.app = app;
	}
	
	/**
	 * Ensure that the ClusterONE VizMapper styles are registered in the given network
	 */
	public void ensureVizMapperStylesRegistered() {
		VisualMappingManager visualMappingManager =
				app.getService(VisualMappingManager.class);
		
		if (visualMappingManager == null)
			return;      /* no visual mapping manager, we cannot do anything */
		
		ensureVisualStyleRegistered(visualMappingManager, getColorNodesByStatusVisualStyle());
		ensureVisualStyleRegistered(visualMappingManager, getColorNodesByAffinityVisualStyle());
	}
	
	/**
	 * Ensure that the given visual style is registered in the given manager.
	 * 
	 * @param  manager  the {@link VisualMappingManager} where the visual style
	 *                  has to be registered
	 * @param  style    the style to register
	 */
	private void ensureVisualStyleRegistered(VisualMappingManager manager, VisualStyle style) {
		if (style == null)
			return;
		
		Set<VisualStyle> styles = manager.getAllVisualStyles();
		if (styles.contains(style))
			return;
		
		manager.addVisualStyle(style);
	}
	
	/**
	 * Returns the visual style that colors nodes by their status in the clustering.
	 */
	public VisualStyle getColorNodesByStatusVisualStyle() {
		if (colorNodesByStatusVisualStyle == null) {
			VisualStyleFactory factory = app.getService(VisualStyleFactory.class);
			VisualMappingFunctionFactory discreteMappingFactory = app.getService(
					VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
			if (factory != null && discreteMappingFactory != null) {
				colorNodesByStatusVisualStyle = factory.createVisualStyle(VISUAL_STYLE_BY_STATUS);
				
				DiscreteMapping<String,Paint> colorMapping = (DiscreteMapping<String,Paint>)
						discreteMappingFactory.createVisualMappingFunction(
								ClusterONECytoscapeApp.ATTRIBUTE_STATUS, String.class,
								BasicVisualLexicon.NODE_FILL_COLOR);
				colorMapping.putMapValue("Outlier", Color.LIGHT_GRAY);
				colorMapping.putMapValue("Cluster", Color.RED);
				colorMapping.putMapValue("Overlap", Color.ORANGE);
				colorNodesByStatusVisualStyle.addVisualMappingFunction(colorMapping);
				
				DiscreteMapping<String,NodeShape> shapeMapping = (DiscreteMapping<String,NodeShape>)
						discreteMappingFactory.createVisualMappingFunction(
						ClusterONECytoscapeApp.ATTRIBUTE_STATUS, String.class,
						BasicVisualLexicon.NODE_SHAPE);
				shapeMapping.putMapValue("Outlier", NodeShapeVisualProperty.ELLIPSE);
				shapeMapping.putMapValue("Cluster", NodeShapeVisualProperty.RECTANGLE);
				shapeMapping.putMapValue("Overlap", NodeShapeVisualProperty.DIAMOND);
				colorNodesByStatusVisualStyle.addVisualMappingFunction(shapeMapping);
			}
		}
		return colorNodesByStatusVisualStyle;
	}
	
	/**
	 * Returns the visual style that colors nodes by their affinity scores.
	 */
	public VisualStyle getColorNodesByAffinityVisualStyle() {
		if (colorNodesByAffinityVisualStyle == null) {
			VisualStyleFactory factory = app.getService(VisualStyleFactory.class);
			VisualMappingFunctionFactory discreteMappingFactory = app.getService(
					VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
			VisualMappingFunctionFactory continuousMappingFactory = app.getService(
					VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
			if (factory != null && discreteMappingFactory != null && continuousMappingFactory != null) {
				colorNodesByAffinityVisualStyle = factory.createVisualStyle(VISUAL_STYLE_BY_AFFINITY);
				
				affinityColorMapping = (ContinuousMapping<Double,Paint>)
						continuousMappingFactory.createVisualMappingFunction(
								ClusterONECytoscapeApp.ATTRIBUTE_AFFINITY, Double.class,
								BasicVisualLexicon.NODE_FILL_COLOR);
				updateAffinityStyleRange(null);
				colorNodesByAffinityVisualStyle.addVisualMappingFunction(affinityColorMapping);
				
				DiscreteMapping<String,NodeShape> shapeMapping = (DiscreteMapping<String,NodeShape>)
						discreteMappingFactory.createVisualMappingFunction(
						ClusterONECytoscapeApp.ATTRIBUTE_STATUS, String.class,
						BasicVisualLexicon.NODE_SHAPE);
				shapeMapping.putMapValue("Outlier", NodeShapeVisualProperty.ELLIPSE);
				shapeMapping.putMapValue("Cluster", NodeShapeVisualProperty.RECTANGLE);
				shapeMapping.putMapValue("Overlap", NodeShapeVisualProperty.DIAMOND);
				colorNodesByAffinityVisualStyle.addVisualMappingFunction(shapeMapping);
			}
		}
		return colorNodesByAffinityVisualStyle;
	}
	
	/**
	 * Updates the continuous mapping used to colour the vertices by their affinities to
	 * reflect the new range of affinity values after some of the attribute values changed.
	 * 
	 * @param  network  the network to evaluate to find out the desirable range
	 */
	public void updateAffinityStyleRange(CyNetwork network) {
		double range = 0.1;
		final Color minColor = Color.blue;
		final Color midColor = Color.white;
		final Color maxColor = Color.red;
		
		if (network != null) {
			List<Double> scores = network.getDefaultNodeTable().getColumn(
					ClusterONECytoscapeApp.ATTRIBUTE_AFFINITY).getValues(Double.class);
			for (Double score: scores) {
				if (Math.abs(score) > range)
					range = Math.abs(score);
			}
		}
		
		int n = affinityColorMapping.getPointCount();
		while (n > 0) {
			n--;
			affinityColorMapping.removePoint(n);
		}
		
		affinityColorMapping.addPoint(-range,
				new BoundaryRangeValues<Paint>(minColor, minColor, minColor));
		affinityColorMapping.addPoint(0.0,
				new BoundaryRangeValues<Paint>(midColor, midColor, midColor));
		affinityColorMapping.addPoint(range,
				new BoundaryRangeValues<Paint>(maxColor, maxColor, maxColor));
	}
}

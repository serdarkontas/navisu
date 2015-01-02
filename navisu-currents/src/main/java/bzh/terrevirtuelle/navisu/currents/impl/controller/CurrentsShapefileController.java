/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bzh.terrevirtuelle.navisu.currents.impl.controller;

import bzh.terrevirtuelle.navisu.currents.impl.controller.loader.CurrentsShapefileLoader;
import bzh.terrevirtuelle.navisu.domain.currents.Current;
import bzh.terrevirtuelle.navisu.domain.currents.parameters.SHOM_CURRENTS_CLUT;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurface;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceAttributes;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Serge Morvan
 * @date 10 nov. 2014 NaVisu project
 */
public class CurrentsShapefileController {

    private static final CurrentsShapefileController INSTANCE;
    protected String path;
    private final int WIDTH = 132;
    private final int HEIGHT = 132;
    private  List<Layer> layers;
    private List<Current> currents;
    private double latRange;
    private double lonRange;
    private double minLat;
    private double minLon;
    private final double[][] gridPoints;

    static {
        INSTANCE = new CurrentsShapefileController();
    }

    private CurrentsShapefileController() {
        this.layers = new ArrayList<>();
        gridPoints = new double[WIDTH][HEIGHT];
    }

    public static CurrentsShapefileController getInstance() {
        return INSTANCE;
    }

    public final List<Layer> init(String path) {
        this.path = path;
        RenderableLayer layer = new RenderableLayer();
        layer.setName("Currents");
        CurrentsShapefileLoader shapefileLoader = new CurrentsShapefileLoader();
        layers =shapefileLoader.createLayersFromSource(new File(path));//pas d'affectation si AnalyticSurface
        currents = shapefileLoader.getCurrents();
        System.out.println("currents.size() " + Math.sqrt(currents.size()));
        AnalyticSurface surface = new AnalyticSurface();
        surface.setSector(shapefileLoader.getSector());
        surface.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        surface.setDimensions(WIDTH, HEIGHT);
        surface.setClientLayer(layer);
        layer.addRenderable(surface);
        layer.setEnabled(true);
       // layers.add(layer);//si AnalyticSurface

        latRange = shapefileLoader.getLatRange() / HEIGHT;
        lonRange = shapefileLoader.getLonRange() / WIDTH;
        minLat = Math.abs(shapefileLoader.getMinLat());
        minLon = Math.abs(shapefileLoader.getMinLon());
        int tmpI;
        double tmpD;
        int line = 0;
        int col = 0;
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                gridPoints[i][j] = SHOM_CURRENTS_CLUT.MAX;
            }
        }
        for (Current c : currents) {
            tmpD = Math.abs((Math.abs(c.getLon()) - minLon) / lonRange);
            tmpI = (int) (tmpD);
            if (tmpD - tmpI > 0) {
                if (tmpI < WIDTH - 2) {
                    col =tmpI + 1;
                } else {
                    col = tmpI;
                }
            }
            tmpD = Math.abs((Math.abs(c.getLat()) - minLat) / latRange);
            tmpI = (int) (tmpD);

            if (tmpD - tmpI > 0) {
                if (tmpI < HEIGHT - 2) {
                    line = HEIGHT-tmpI + 1;
                } else {
                    line = HEIGHT-tmpI;
                }
            }
            if (line < WIDTH && col < HEIGHT) {
                gridPoints[line][col] = c.getSpeed();
            }
        }
        /*
         for (int i = 0; i < WIDTH; i++) {
         for (int j = 0; j < HEIGHT; j++) {
         System.out.println("i : " + i + "  j : " + j + " s : " + SHOM_CURRENTS_CLUT.getColor(gridPoints[i][j]));
         }
         }
         */
        ArrayList<AnalyticSurface.GridPointAttributes> attributesList = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tmpD = gridPoints[i][j];
                if (tmpD != 0.0) {
                    attributesList.add(AnalyticSurface.createGridPointAttributes(tmpD, SHOM_CURRENTS_CLUT.getColor(tmpD)));
                }
            }
        }
        surface.setValues(attributesList);
        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setDrawShadow(false);
        attr.setInteriorOpacity(1);
        attr.setDrawOutline(false);
        attr.setOutlineWidth(0);
        surface.setSurfaceAttributes(attr);

        return layers;
    }
}
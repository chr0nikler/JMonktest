/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;

/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */

public class HelloJME3 extends SimpleApplication{
    
    public static void main (String[] args) {
        HelloJME3 app = new HelloJME3();
        app.start(); //starts the game
    }
    
    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1); //Creates cube at the origin
        Geometry geom = new Geometry("Box", b); //Creates cube geometry from the shape
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded/j3md"); 
        //creates a simple material
        mat.setColor("Color", ColorRGBA.Blue);  //sets material to blue color
        geom.setMaterial(mat); //set's the cubes material to mat
        rootNode.attachChild(geom); //make cube appear in scene
    }
}
        
        
    }
}

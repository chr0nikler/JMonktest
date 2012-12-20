/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class HelloNode extends SimpleApplication{
    
    public static void main(String[] args) {
        HelloNode app = new HelloNode();
        app.start(); //starts game
    }
    
    @Override
    public void simpleInitApp() {
        /** create a blue box at coords(1,-1,1) */
        Box box1 = new Box(Vector3f.ZERO, 1,1,1);
        Geometry blue = new Geometry("Box", box1);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);
        blue.move(1,-1,1);
        
        /** create a red box straight above the blue box */
        Box box2 = new Box(Vector3f.ZERO, 1,1,1);
        Geometry red = new Geometry("Box", box2);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);
        red.move(1,3,1);
        
        /** Create a pivot node at (0,0,0) and attach it to the root node */
        Node pivot = new Node("pivot");
        rootNode.attachChild(pivot); //put this node in the scene
        
        /** Attach the two boxes to the *pivot* node. */
        pivot.attachChild(blue);
        pivot.attachChild(red);
        /** Rotate the pivot node: Note that both boxes have rotated! */
        pivot.rotate(.4f,.4f,0f);
    }
}

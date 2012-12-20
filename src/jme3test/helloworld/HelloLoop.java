/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/** Sample 4 - how to trigger repeating actions from the main update loop.
 * In this example, we make the player character rotate. */

public class HelloLoop extends SimpleApplication{
    
    public static void main(String[] args){
        HelloLoop app = new HelloLoop();
        app.start();
    }
    protected Geometry player;
    protected Geometry p2;
    protected Material mat2;
    protected BitmapText helloText;
    float timeVar;
    
    @Override
    public void simpleInitApp() {
        timeVar = 0;
        
        Box b = new Box(Vector3f.ZERO,1,1,1);
        player = new Geometry("blue cube", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color",ColorRGBA.Blue);
        player.setMaterial(mat);
        rootNode.attachChild(player);
        
        Box b2 = new Box(Vector3f.ZERO,1,1,1);
        p2 = new Geometry("red cube", b);
        mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color",ColorRGBA.Red);
        p2.setMaterial(mat2);
        p2.setLocalTranslation(4.0f,0,0);
        rootNode.attachChild(p2);
        
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        helloText = new BitmapText(guiFont,false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Hello World");
        helloText.setLocalTranslation(300,helloText.getLineHeight(),0);
        guiNode.attachChild(helloText);
        
    }
    
    /* This is the update loop */
    @Override
    public void simpleUpdate(float tpf){
        //make the player rotate
        player.rotate(0,-2*tpf,0);
        timeVar += tpf;
        if(timeVar < 4) {
            p2.scale(1 + timeVar * 0.4f);
        } else if (timeVar < 8) {
            p2.scale(1-timeVar*0.4f);  
        } else if (timeVar > 12){
            timeVar = 0;
        }
    }
}

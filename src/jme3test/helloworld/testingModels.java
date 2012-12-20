/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author JSC
 */
public class testingModels extends SimpleApplication implements AnimEventListener{
    
    public static void main (String[] args) {
        testingModels app = new testingModels();
        app.start();
    }
    private AnimControl control;
    private AnimChannel channel;
    Node model;
    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        DirectionalLight dl  = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);
        model = (Node)assetManager.loadModel("Models/Tahu/Plane.007.mesh.j3o");
        model.setLocalScale(0.4f);
        rootNode.attachChild(model);
        control = model.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("my_animation"); 
        for (String anim : control.getAnimationNames()){
                System.out.println(anim);
        }
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
       //unused
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        //unused
    }
    
    
}

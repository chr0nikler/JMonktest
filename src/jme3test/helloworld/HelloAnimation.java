/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/** Sample 7 - how to load an OgreXML model and play an animation,
 * using channels, a controller, and an AnimEventListener. */
public class HelloAnimation extends SimpleApplication implements AnimEventListener {
    private AnimChannel channel;
    private AnimChannel channel2;
    private AnimControl control;
    Node player;
    public static void main(String[] args) {
        HelloAnimation app = new HelloAnimation();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       viewPort.setBackgroundColor(ColorRGBA.LightGray);
       initKeys();
       DirectionalLight dl  = new DirectionalLight();
       dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
       rootNode.addLight(dl);
       player = (Node) assetManager.loadModel("Models/ogre/Circle.mesh.j3o");
       player.setLocalScale(0.5f);
       rootNode.attachChild(player);
       
       control = player.getControl(AnimControl.class);
       control.addListener(this);
       channel = control.createChannel();
       channel.setAnim("stand");       
       channel2 = control.createChannel();
       channel2.setAnim("pull");
       for (String anim : control.getAnimationNames()){
                System.out.println(anim);
       }
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(animName.equals("Walk") || animName.equals("pull")){
            channel.setAnim("stand", 0.5f);
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1f);
        }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        //unused
    }

    /** Custom Keybinding: Map named actions to inputs */
    private void initKeys() {
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Walk");
        inputManager.addMapping("pull", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addListener(actionListener, "pull");
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name.equals("Walk") && !keyPressed) {
                if(!channel.getAnimationName().equals("Walk")){
                    channel.setAnim("Walk");
                    channel.setLoopMode(LoopMode.Loop);
                }
            } else if(name.equals("pull") && !keyPressed) {
                if(!channel2.getAnimationName().equals("pull")){
                    channel2.setAnim("pull");
                    channel2.setLoopMode(LoopMode.Loop);
                }
            }
        }
    };
}

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
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
/**
 *
 * @author JSC
 */
public class MyGame extends SimpleApplication implements ActionListener, AnimEventListener{
       
    private BulletAppState bulletAppState;
    Vector3f walkDirection = new Vector3f();
    
    public static void main(String[] args) {
        MyGame game = new MyGame();
        game.start();
    }
    private AnimControl control;
    private AnimChannel channel;
    private boolean left = false, right = false;
    private CharacterControl player;
    Node playerModel;
    
    @Override
    public void simpleInitApp() {
        
        DirectionalLight dl  = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        //First, create geometry (or in this case, the model)
        playerModel = (Node) assetManager.loadModel("Models/ogre/Circle.mesh.j3o");
        playerModel.setLocalScale(0.5f);
        playerModel.setLocalTranslation(0,-1f,0f);
        Node center = new Node();
        center.attachChild(playerModel);
        
        control = playerModel.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        //channel.setAnim("my_animation"); 
        //Next, attach material (no material needed for model)
        /*Material p1mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        p1mat.setColor("Color",ColorRGBA.Green);
        p1.setMaterial(p1mat);*/
        
        //Then, create physics
        CapsuleCollisionShape character = new CapsuleCollisionShape(1.2f,1.1f,1);
        player = new CharacterControl(character,0.1f);
        player.setGravity(15f);
        center.addControl(player);
        player.setPhysicsLocation(new Vector3f(0f,3f,0f));
        //Then, attach physics and make visible
        
        rootNode.attachChild(center);
        bulletAppState.getPhysicsSpace().add(center);
        
        
        //First, create geometry
        Box floor = new Box(Vector3f.ZERO,10f,0.1f,10f);
        Geometry floor_geo = new Geometry("Floor", floor);
       
        //Then, attach material
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floor_mat.setColor("Color",ColorRGBA.Gray);
        floor_geo.setMaterial(floor_mat);
        
        //Then, create physics
        RigidBodyControl box_phy = new RigidBodyControl(0);
        floor_geo.addControl(box_phy);
        box_phy.setPhysicsLocation(new Vector3f(0,-10,0));

        //Then, attach physics and make visible
        bulletAppState.getPhysicsSpace().add(box_phy);
        rootNode.attachChild(floor_geo);
        
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        
        
        flyCam.setEnabled(false);
        ChaseCamera chasecam = new ChaseCamera(cam,playerModel,inputManager);
        chasecam.setDefaultVerticalRotation(FastMath.PI/1.1f);
        
        initKeys();
    }
    
    private void initKeys() {
        inputManager.addMapping("Left",new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addListener(this,"Left");
        inputManager.addListener(this,"Right");
        inputManager.addListener(this,"Jump");
        
    }
    
    public void onAction(String binding, boolean value, float tpf){
        if(binding.equals("Left")) {
            if(value) {
                left = true;
            } else {
                left = false;
            }
            
        }
        else if (binding.equals("Right")) {
            if(value) {
                right = true;
            } else {
                right = false;
            }
        } 
        else if (binding.equals("Jump")){
            player.jump();
            channel.setAnim("my_animation");
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.25f);
        walkDirection.set(0,0,0);
        if(left){
            walkDirection.addLocal(camLeft);
        }
        if(right){
            walkDirection.addLocal(camLeft.negate());
        } 
        player.setWalkDirection(walkDirection);
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
       
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }
}

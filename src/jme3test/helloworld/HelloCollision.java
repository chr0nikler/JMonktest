/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.ColorRGBA;

/**
 * Example 9 - How to make walls and floors solid.
 * This collision code uses Physics and a custom Action Listener.
 * @author normen, with edits by Zathras
 */

public class HelloCollision extends SimpleApplication implements ActionListener {
	private Spatial sceneModel;
	private BulletAppState bulletAppState;
	private RigidBodyControl landscape;
	private CharacterControl player;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;

	public static void main(String[] args) {
		HelloCollision app = new HelloCollision();
		app.start();
	}

	public void simpleInitApp() {
		/** Setup physics */

		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		//bulletAppState.getPhysicsSpace().enableDebug(assetManager);
		//
		//We re-use the flyby camera for rotation, while positioning is handled by physics
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f,1f));
		flyCam.setMoveSpeed(100);
		setUpKeys();
		setUpLight();

		//We load the scene from the zip file and adjust its size.
		assetManager.registerLocator("town.zip", ZipLocator.class);
		sceneModel = assetManager.loadModel("main.scene");
		sceneModel.setLocalScale(2f);

		//We set up collision detection for the scene by creating a 
		//compound collision shape and a static RigidBodycontrol with mass zero
		CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
		landscape = new RigidBodyControl(sceneShape, 0);
		sceneModel.addControl(landscape);

		//We setup collision detection for the player by creating
		//a capsule collision shape and a CharacterControl. The
		//CharacterControl offers extra settings for size, stepheight,
		//jumping, falling, and gravity. We aslo put the player in
		//its starting position
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 5f,1);
		player = new CharacterControl(capsuleShape, 0.05f);
		player.setJumpSpeed(20);
		player.setFallSpeed(30);
		player.setGravity(30);
		player.setPhysicsLocation(new Vector3f(0,10,0));
		
		//We attach the scene and the player to the rootNode and the physics space
		//to make them appear in the game world.
		rootNode.attachChild(sceneModel);
		bulletAppState.getPhysicsSpace().add(landscape);
		bulletAppState.getPhysicsSpace().add(player);
	}

	private void setUpLight() {
		//We add light so we see the scene
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(al);

		DirectionalLight dl = new DirectionalLight();
		dl.setColor(ColorRGBA.White);
		dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
		rootNode.addLight(dl);
	}

	/** We over-write some navigational key mappings here, so we can 
	 * add physics-controlled walking and jumping: */
	private void setUpKeys() {
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
		inputManager.addListener(this, "Jump");
	}

	/** These are our custom actions triggered by key presses
	 * We do not walk yet, we just keep track of the direction the user preused. */
	public void onAction(String binding, boolean value, float tpf) {
		if(binding.equals("Left")){
			left = value;
		} else if (binding.equals("Right")){
			right = value;
		} else if (binding.equals("Up")){
			up = value;
		} else if (binding.equals("Down")){
			down = value;
		} else if (binding.equals("Jump")){
			player.jump();
		}
	}

	/**
	 * This is the main even loop--walking happens here.
	 * We check in whic direction the player is walking by interpreting
	 * the camera direction forward (camDir) and to the side (camleft).
	 * The setWalkDirection() command is what lets a physics-controlled player walk.
	 * We also make sure here that the camera moves with the player.
	 */
	 @Override
	 public void simpleUpdate(float tpf) {
		 Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
		 Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
		 walkDirection.set(0,0,0);
		 if(left) { walkDirection.addLocal(camLeft); }
		 if(right) { walkDirection.addLocal(camLeft.negate()); }
		 if(up) { walkDirection.addLocal(camDir); }
		 if(down) { walkDirection.addLocal(camDir.negate());}
		 player.setWalkDirection(walkDirection);
		 cam.setLocation(player.getPhysicsLocation());
	 }
}
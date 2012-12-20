/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.HillHeightMap; //for excercise 2
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import java.util.List;

/** Sample 10 - How to create fast-rendering terrains from heightmaps,
and how to use texture splatting to make the terrain look good.  */
public class HelloTerrain extends SimpleApplication implements ActionListener {

	private BulletAppState bulletAppState;
	private RigidBodyControl landscape;
	private CharacterControl player;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;
	private TerrainQuad terrain;
	Material mat_terrain;

	public static void main(String[] args) {
		HelloTerrain app = new HelloTerrain();
		app.start();
	}

	@Override
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
		/** 1. Create terrain material and load four textures into it */
		mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
		/** 1.1) Add ALPHA map (for red-blue-gree coded splat textures) */
		mat_terrain.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

		/** 1.2) Add GRASS texture into the red layer (Tex1). */
                Texture road = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
		road.setWrap(WrapMode.Repeat);
		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex1", road);
		mat_terrain.setFloat("Tex1Scale", 64f);

		/** 1.3) Add DIRT texture into the green layer (Tex2). */
		Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex2", grass);
		mat_terrain.setFloat("Tex2Scale", 32f);

		/** 1.4) Add ROAD texture into the blue layer (Tex3) */

		
		mat_terrain.setTexture("Tex3", dirt);
		mat_terrain.setFloat("Tex3Scale", 128f);

		/** 2. Create the height map */
		HillHeightMap heightmap = null;
                HillHeightMap.NORMALIZE_RANGE = 100; // optional
                try {
                    heightmap = new HillHeightMap(513, 1000, 50, 100, (byte) 3); // byte 3 is a random seed
                } catch (Exception ex) {
                }
		heightmap.load();

		/** 3. We have prepared material and heightmap.
		 * Now we create the actual terrain:
		 * 3.1) Create a TerrainQuad and name it "my terrain".
		 * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65
		 * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513
		 * 3.4) As LOD step scale we supply Vector3f(1,1,1).
		 * 3.5) We supply the prepared heightmap itself.
		 */

		int patchSize = 65;
		terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());
		/** 4. We give the terrain its material, position, & scale it, and attach it */
		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0,-100,0);
		terrain.setLocalScale(2f,1f,2f);
                rootNode.attachChild(terrain);

                CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) terrain);
		landscape = new RigidBodyControl(sceneShape, 0);
		terrain.addControl(landscape);
                
		/** 5. The LOD (lvl of detail) depends on where the camera is: */
		TerrainLodControl control = new TerrainLodControl(terrain,getCamera());
		terrain.addControl(control);
                
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
                    if(value) {left = true;} else { left = false;}
		} else if(binding.equals("Right")){
                    if(value) {right = true;} else { right = false;}
		} else if(binding.equals("Up")){
                    if(value) {up = true;} else { up = false;}
		} else if(binding.equals("Down")){
                    if(value) {down = true;} else { down = false;}
		} else if(binding.equals("Jump")){
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
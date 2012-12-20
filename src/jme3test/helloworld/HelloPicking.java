/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/** Sample 8 - how to let the user pick (select) objects in the scene 
 * using the mouse or key presses. Can be used for shooting, opening doors, etc. */
public class HelloPicking extends SimpleApplication{
    
    public static void main(String[] args) {
        HelloPicking app = new HelloPicking();
        app.start();
    }
    Node shootables;
    Node inventory;
    Geometry mark;
    boolean grabbed = false;
    Geometry closeObj;
  
  	
    @Override
    public void simpleInitApp() {
        inputManager.setCursorVisible(true);
	initCrossHairs(); // a "+" in the midle of the screen to help aiming
	initKeys(); 	//load custom key mappings
	initMark(); 	// a red shpere to mark the hit
	DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(-0.1f, -1f,1).normalizeLocal());
	//* create four colored boxes and a floor to shoot at: */
	shootables = new Node("Shootables");
        inventory = new Node("Inventory");
	rootNode.attachChild(shootables);
        rootNode.addLight(light);
        guiNode.attachChild(inventory);
        guiNode.addLight(light);
        //shootables.attachChild(makeSpatial("an Enemy", -2f, 0f, 1f));
	shootables.attachChild(makeCube("a tin can", 1f, -2f, 0f));
        shootables.attachChild(makeCube("the Sheriff", 0f,1f,-2f));
	shootables.attachChild(makeCube("the Deputy", 1f,0f,-4f));	
	shootables.attachChild(makeFloor());
    }

    /** Declaring the "shoot" action and mapping to its triggers. */
    private void initKeys() {
            inputManager.addMapping("Shoot",
                                    new KeyTrigger(KeyInput.KEY_SPACE), //trigger 1:spacebar
                                    new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); //trigger 2: left-button click
            inputManager.addListener(actionListener, "Shoot");
    }

    /** Defining the "Shoot" action: Determine what was hit and how to respond */
    private ActionListener actionListener = new ActionListener() {
            
            public void onAction(String name, boolean keyPressed, float tpf) {
                    if (name.equals("Shoot") && !keyPressed) {
                            //1. Reset Results list
                            CollisionResults results = new CollisionResults();
                            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                            shootables.collideWith(ray, results);
                            //2.    Aim the ray from the cam loc to cam direction.
                            //Ray ray =  new Ray(cam.getLocation(), cam.getDirection());
                            //3. Collect intersections between Ray and Shootables in results list
                            //shootables.collideWith(ray,results);
                            //4. Print the results).
                            System.out.println("----Collisions?" + results.size() + "-----");
                            for(int i =0; i< results.size(); i++) {
                                    //for each hit, we know distance, impact point, name of geometry.
                                    float dist = results.getCollision(i).getDistance();
                                    Vector3f pt = results.getCollision(i).getContactPoint();
                                    String hit = results.getCollision(i).getGeometry().getName();
                                    System.out.println("* Collision #" + i);
                                    System.out.println(" You shot " + hit + " at " + pt + ", " + dist + "wu away");
                                    System.out.println(grabbed);
                                    //5. use the results (we mark the hit objects)
                                    if (results.size() > 0 && grabbed == false) {
                                            //The closest collision point is what was truly hit:
                                            CollisionResult closest = results.getClosestCollision();
                                            //Let's interact - we makr the hit with a red dot.
                                            mark.setLocalTranslation(closest.getContactPoint());
                                            rootNode.attachChild(mark); 
                                            if(!closest.getGeometry().getName().equals("the Floor")){
                                                closeObj = closest.getGeometry();
                                                shootables.detachChild(closest.getGeometry());
                                                inventory.attachChild(closest.getGeometry());
                                                closest.getGeometry().setLocalScale(10f);
                                                closest.getGeometry().setLocalTranslation(20f, settings.getHeight()-30f,0f);
                                                guiNode.attachChild(closest.getGeometry());
                                                grabbed = true;
                                            }
                                            
                                            //Now change the color of the object hit
                                            //closest.getGeometry().getMaterial().setColor("Color", ColorRGBA.Black);
                                    } else if(results.size() > 0 && grabbed == true){
                                            
                                            CollisionResult closest = results.getClosestCollision();
                                            pt = closest.getContactPoint();
                                            //Let's interact - we makr the hit with a red dot.
                                            
                                            if(closest.getGeometry().getName().equals("the Floor")) {
                                                System.out.println("Removing");
                                                mark.setLocalTranslation(closest.getContactPoint());
                                                rootNode.attachChild(mark); 
                                                inventory.detachChild(closeObj);
                                                shootables.attachChild(closeObj);
                                                closeObj.setLocalScale(0.5f);
                                                closeObj.setLocalTranslation(pt.x, pt.y+1.25f, pt.z);
                                                grabbed = false;
                                            }
                                            
                                            //No hits? Then remove the red mark.
                                            //rootNode.detachChild(mark);
                                    }
                            }
                    }
            }
    };

    /** A cube object for target practice */
    protected Geometry makeCube(String name, float x, float y, float z) {
      Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
      Geometry cube = new Geometry(name, box);
      Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      mat1.setColor("Color", ColorRGBA.randomColor());
      cube.setMaterial(mat1);
      return cube;
    }
    
    protected Spatial makeSpatial(String name, float x, float y, float z){
        Spatial golem  = (Node)assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.setLocalScale(0.3f);
        return golem;
        
    }
    

    /** A floor to show that the "shot" can go through several objects. */
    protected Geometry makeFloor() {
      Box box = new Box(new Vector3f(0, -4, -5), 15, .2f, 15);
      Geometry floor = new Geometry("the Floor", box);
      Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      mat1.setColor("Color", ColorRGBA.Gray);
      floor.setMaterial(mat1);
      return floor;
    }
 
    /** A red ball that marks the last spot that was "hit" by the "shot". */
    protected void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    /** A centred plus sign to help the player aim. */
    protected void initCrossHairs() {
      guiNode.detachAllChildren();
      guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
      BitmapText ch = new BitmapText(guiFont, false);
      ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
      ch.setText("+"); // crosshairs
      ch.setLocalTranslation( // center
        settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
        settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
      guiNode.attachChild(ch);
    }
}

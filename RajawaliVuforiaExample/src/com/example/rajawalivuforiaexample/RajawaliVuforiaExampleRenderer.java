package com.example.rajawalivuforiaexample;

import java.io.Console;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.SerializedObject3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.animation.mesh.SkeletalAnimationSequence;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.ATexture.TextureType;
import rajawali.materials.textures.Texture;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import rajawali.util.RajLog;
import rajawali.vuforia.RajawaliVuforiaRenderer;
import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class RajawaliVuforiaExampleRenderer extends RajawaliVuforiaRenderer implements OnObjectPickedListener {
	private DirectionalLight mLight;
	private SkeletalAnimationObject3D mBob;
	private Object3D mF22;
	private Object3D mAndroid;
	public Object3D card;
	private RajawaliVuforiaExampleActivity activity;
	private ObjectColorPicker mPicker;

	public RajawaliVuforiaExampleRenderer(Context context) {
		super(context);
		activity = (RajawaliVuforiaExampleActivity)context;
	}

	protected void initScene() {
		mLight = new DirectionalLight(.1f, 0, -1.0f);
		mLight.setColor(1.0f, 1.0f, 0.8f);
		mLight.setPower(1);
		
		getCurrentScene().addLight(mLight);
		
		try {			
			mPicker = new ObjectColorPicker(this);
		    mPicker.setOnObjectPickedListener(this);
			//
			// -- Load Card 
			//card = new Cube(100.0f);
			card = new Plane(100f, 100f, 1, 1, Axis.Z);
			card.setDoubleSided(true);
			//card.setColor(0x9900ff);
			card.setRotX(45);
			card.setRotY(45);
			card.setX(-5f);
			card.setY(5f);
			getCurrentScene().addChild(card);
	
			Material material = new Material();
			material.addTexture(new Texture("cardfront", R.drawable.tarjeta_front));			
			//material.addTexture(new Texture("cardfront", R.drawable.tarjeta_back));
			card.setMaterial(material);
			card.setVisible(false);
			mPicker.registerObject(card);
			
			//
			// -- Load Bob (model by Katsbits
			// http://www.katsbits.com/download/models/)
			//

			LoaderMD5Mesh meshParser = new LoaderMD5Mesh(this,
					R.raw.boblampclean_mesh);
			meshParser.parse();
			mBob = (SkeletalAnimationObject3D) meshParser
					.getParsedAnimationObject();
			mBob.setScale(2);

			LoaderMD5Anim animParser = new LoaderMD5Anim("dance", this,
					R.raw.boblampclean_anim);
			animParser.parse();
			mBob.setAnimationSequence((SkeletalAnimationSequence) animParser
					.getParsedAnimationSequence());

			getCurrentScene().addChild(mBob);

			mBob.play();
			mBob.setVisible(false);

			//
			// -- Load F22 (model by KuhnIndustries
			// http://www.blendswap.com/blends/view/67634)
			//

			GZIPInputStream gzi = new GZIPInputStream(mContext.getResources()
					.openRawResource(R.raw.f22));
			ObjectInputStream fis = new ObjectInputStream(gzi);
			SerializedObject3D serializedObj = (SerializedObject3D) fis
					.readObject();
			fis.close();

			mF22 = new Object3D(serializedObj);
			mF22.setScale(30);
			getCurrentScene().addChild(mF22);
			
			Material f22Material = new Material();
			f22Material.enableLighting(true);
			f22Material.setDiffuseMethod(new DiffuseMethod.Lambert());
			f22Material.addTexture(new Texture("f22Texture", R.drawable.f22));
			f22Material.setColorInfluence(0);
			
			mF22.setMaterial(f22Material);
			
			//
			// -- Load Android
			//
			
			gzi = new GZIPInputStream(mContext.getResources()
					.openRawResource(R.raw.android));
			fis = new ObjectInputStream(gzi);
			serializedObj = (SerializedObject3D) fis
					.readObject();
			fis.close();
			
			mAndroid = new Object3D(serializedObj);
			mAndroid.setScale(14);
			getCurrentScene().addChild(mAndroid);
			
			Material androidMaterial = new Material();
			androidMaterial.enableLighting(true);
			androidMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
			androidMaterial.setSpecularMethod(new SpecularMethod.Phong());
			mAndroid.setColor(0x00dd00);
			mAndroid.setMaterial(androidMaterial);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void foundFrameMarker(int markerId, Vector3 position,
			Quaternion orientation) {
		
		if (markerId == 0) {
//			mBob.setVisible(true);
//			mBob.setPosition(position);
//			mBob.setOrientation(orientation);
			card.setVisible(true);
			card.setPosition(position);
			card.setOrientation(orientation);
		} else if (markerId == 1) {
			mAndroid.setVisible(true);
			mAndroid.setPosition(position);
			mAndroid.setOrientation(orientation);
		}
	}

	@Override
	protected void foundImageMarker(String trackableName, Vector3 position,
			Quaternion orientation) {
		if(trackableName.equals("SamsungGalaxyS4"))
		{
			mBob.setVisible(true);
			mBob.setPosition(position);
			mBob.setOrientation(orientation);
			RajLog.d(activity.getMetadataNative());
		}
		if(trackableName.equals("stones"))
		{
			mF22.setVisible(true);
			mF22.setPosition(position);
			mF22.setOrientation(orientation);
		}
		// -- also handle cylinder targets here
		// -- also handle multi-targets here
	}

	@Override
	public void noFrameMarkersFound() {
	}

	public void onDrawFrame(GL10 glUnused) {
		mBob.setVisible(false);
		mF22.setVisible(false);
		mAndroid.setVisible(false);
		card.setVisible(false);
		
		super.onDrawFrame(glUnused);
		
		if (!activity.getScanningModeNative())
		{
			activity.showStartScanButton();
		}
	}

	@Override
	public void onObjectPicked(Object3D object) {
		Log.d("onObjectPicked", "started!");		
		Log.d("onObjectPicked", String.valueOf(card.isVisible()));
		if(card.isVisible()) {		    	  								
			try {
				Material material = card.getMaterial();
				material.removeTexture(new Texture("cardfront", R.drawable.tarjeta_front));
				material.addTexture(new Texture("cardback", R.drawable.tarjeta_back));					
				card.setMaterial(material);
			} catch (TextureException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getObjectAt(float x, float y) {
	    mPicker.getObjectAt(x, y);
	}
}

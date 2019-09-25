package kr.ac.yju.com.im;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class Custom_Activity extends AppCompatActivity {

    SceneView mSceneView;
    private TextView mtextView;
    private String x_cm;
    private String y_cm;
    private String z_cm;
    private TransformationSystem transformationSystem;
    private Vector3 myCus_Scale;
    private DrawerLayout cusDrawerLayout;
    private NavigationView cusNavigationView;
    String cus_model = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_custom_);
        mSceneView = findViewById(R.id.sceneView);
        mtextView = (TextView)findViewById(R.id.custom_scaleValue);
        cusDrawerLayout = findViewById(R.id.cusDrawer);
        cusNavigationView = findViewById(R.id.nav_cus);

        cusNavigationView.setItemIconTintList(null);

        ImageButton btn1 = (ImageButton) findViewById(R.id.cusSidebar_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!cusDrawerLayout.isDrawerOpen(GravityCompat.START)) cusDrawerLayout.openDrawer(Gravity.START);
                else cusDrawerLayout.closeDrawer(Gravity.END);
            }
        });

        ImageButton btn2 = (ImageButton) findViewById(R.id.cusBack_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
            }
        });

        cusNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                cusDrawerLayout.closeDrawers();


                //사용자가 선택한 모델에 따라 각 모델의 url 값 지정 및 object 크기 조절을 위해 objectScale 배수 값 지정 (가로, 세로, 높이)
                switch (id) {
                    case R.id.desk_1:

                        //서버에 저장된 sfb 파일에 접근하기 위한 url/ 프로젝트 내부에 sfb 파일이 없고 gradle 에 따로 선언하지 않아도 서버에서 바로 파일을 받아와 사용
                        cus_model = "http://101.101.162.32:8080/3D/AR-assets/Desk.sfb";
                        myCus_Scale = new Vector3(0.9f,0.9f,0.9f);
                        createScene();
                        Toast.makeText(Custom_Activity.this, "DESK 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.chair_1:
                        cus_model = "http://101.101.162.32:8080/3D/AR-assets/chear.sfb";
                        myCus_Scale = new Vector3(0.85f,0.85f,0.85f);
                        createScene();
                        Toast.makeText(Custom_Activity.this, "CHAIR 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.bed_1:
                        cus_model = "http://101.101.162.32:8080/3D/AR-assets/Bed.sfb";
                        myCus_Scale = new Vector3(1f,1f,1f);
                        createScene();
                        Toast.makeText(Custom_Activity.this, "BED 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.closet_1:
                        cus_model = "http://101.101.162.32:8080/3D/AR-assets/0623Cloz.sfb";
                        myCus_Scale = new Vector3(1f,1f,1.1f);
                        createScene();
                        Toast.makeText(Custom_Activity.this, "CLOSET 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.table_1:
                        cus_model = "http://101.101.162.32:8080/3D/AR-assets/Table_Large_Rectangular_01.sfb";
                        myCus_Scale = new Vector3(0.9f,0.45f,0.6f);
                        createScene();
                        Toast.makeText(Custom_Activity.this, "TABLE 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.BOX_1:
                        cus_model = "http://101.101.162.32:8080/3D/AR-assets/Box.sfb";
                        myCus_Scale = new Vector3(0.7f,0.7f,0.7f);
                        createScene();
                        Toast.makeText(Custom_Activity.this, "BOX 선택.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        mSceneView.getScene().addOnUpdateListener(this::onFrameUpdate);
        transformationSystem=new TransformationSystem(getResources().getDisplayMetrics(),new FootprintSelectionVisualizer());
        mSceneView.getRenderer().setClearColor(new com.google.ar.sceneform.rendering.Color(Color.LTGRAY));
        mSceneView.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                transformationSystem.onTouch(hitTestResult,motionEvent);
            }
        });
        mSceneView.getScene().getCamera().setLocalPosition(new Vector3(0,0.2f,0));

    }

    private void modelCheck() {
        onDestroy();
    }


    private void onFrameUpdate(FrameTime frameTime) {

    }

    private void createScene(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ModelRenderable.builder()
                    .setSource(this, Uri.parse(cus_model))
                    .build()
                    .thenAccept(renderable->onRenderableLoaded(renderable))
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage())
                                .show();
                        return null;
                    });
        }
    }

    private void onRenderableLoaded(Renderable renderable){
        TransformableNode transformableNode = new TransformableNode(transformationSystem);
        transformableNode.setParent(mSceneView.getScene());
        transformableNode.getRotationController().setEnabled(true);
        transformableNode.getScaleController().setEnabled(false);
        transformableNode.getTranslationController().setEnabled(false);
        transformableNode.setLocalPosition(new Vector3(0f,0f,-1f));
        transformableNode.setLocalScale(new Vector3(myCus_Scale.x/1.6f,myCus_Scale.y/1.6f,myCus_Scale.z/1.6f));
        transformableNode.setRenderable(renderable);
        mSceneView.getScene().addChild(transformableNode);
        transformableNode.select();

        Box box = (Box) transformableNode.getRenderable().getCollisionShape();
        Vector3 size = box.getSize();
        Vector3 transformableNodeScale = transformableNode.getWorldScale();
        Vector3 finalSize =
                new Vector3(
                        (float) (Math.round(((size.x * 1.6f) * transformableNodeScale.x * 100) * 10) / 10.0),
                        (float) (Math.round(((size.y * 1.6f) * transformableNodeScale.y * 100) * 10) / 10.0),
                        (float) (Math.round(((size.z * 1.6f) * transformableNodeScale.z * 100) * 10) / 10.0));
        x_cm = String.valueOf(finalSize.x); //세로
        y_cm = String.valueOf(finalSize.y); //높이
        z_cm = String.valueOf(finalSize.z); //가로
        mtextView.setText("( 가로 : " + z_cm + "cm " + "세로 : " + x_cm + "cm " + "높이 : " + y_cm +"cm )");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mSceneView.resume();
        }
        catch (CameraNotAvailableException e){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSceneView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSceneView.destroy();
    }

    // 백 키를 눌렀을 시 경고창을 띄움
    public void onBackPressed()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Custom_Activity.this);
        builder.setMessage("메인화면으로 돌아가시겠습니까?");
        builder .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent intent = new Intent(Custom_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}

package kr.ac.yju.com.im;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.PixelCopy;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AR_Activity extends AppCompatActivity {

    private ArFragment arFragment;
    private String model;

    //사용자가 선택한 모델에 따라 object 스케일을 조정하기 위해 임의의 Vector3 변수 생성
    public Vector3 objectScale;

    private DrawerLayout armDrawerLayout;
    private NavigationView armNavigationView;
    private TextView mtextView;
    public String x_cm;
    public String y_cm;
    public String z_cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},00);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_ar_);


        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arfragment);
        armDrawerLayout = (DrawerLayout) findViewById(R.id.ardrawerLayout);
        armNavigationView = (NavigationView) findViewById(R.id.nav_ar);
        mtextView = (TextView)findViewById(R.id.scaleValue);

        armNavigationView.setItemIconTintList(null);

        ImageButton btn1 = (ImageButton) findViewById(R.id.arsidebar_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!armDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    armDrawerLayout.openDrawer(Gravity.START);
                    //armNavigationView.bringToFront();
                    //armDrawerLayout.bringToFront();
                } else {
                    armDrawerLayout.closeDrawer(Gravity.END);
                }
            }
        });

        ImageButton btn2 = (ImageButton) findViewById(R.id.back_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
            }
        });

        ImageButton btn3 = (ImageButton)findViewById(R.id.camera_btn);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        armNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                armDrawerLayout.closeDrawers();

                //사용자가 선택한 모델에 따라 각 모델의 url 값 지정 및 object 크기 조절을 위해 objectScale 배수 값 지정 (가로, 세로, 높이)
                switch (item.getItemId()) {
                    case R.id.desk_1:

                        //서버에 저장된 sfb 파일에 접근하기 위한 url/ 프로젝트 내부에 sfb 파일이 없고 gradle 에 따로 선언하지 않아도 서버에서 바로 파일을 받아와 사용
                        model = "http://101.101.162.32:8080/3D/AR-assets/Desk.sfb";
                        objectScale = new Vector3(0.9f,0.9f,0.9f);
                        Toast.makeText(AR_Activity.this, "DESK 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.chair_1:
                        model = "http://101.101.162.32:8080/3D/AR-assets/chear.sfb";
                        objectScale = new Vector3(0.85f,0.85f,0.85f);
                        Toast.makeText(AR_Activity.this, "CHAIR 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.bed_1:
                        model = "http://101.101.162.32:8080/3D/AR-assets/Bed.sfb";
                        objectScale = new Vector3(1f,1f,1f);
                        Toast.makeText(AR_Activity.this, "BED 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.closet_1:
                        model = "http://101.101.162.32:8080/3D/AR-assets/0623Cloz.sfb";
                        objectScale = new Vector3(1f,1f,1.1f);
                        Toast.makeText(AR_Activity.this, "CLOSET 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.table_1:
                        model = "http://101.101.162.32:8080/3D/AR-assets/Table_Large_Rectangular_01.sfb";
                        objectScale = new Vector3(0.9f,0.45f,0.6f);
                        Toast.makeText(AR_Activity.this, "TABLE 1 선택.", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.BOX_1:
                        model = "http://101.101.162.32:8080/3D/AR-assets/Box.sfb";
                        objectScale = new Vector3(0.7f,0.7f,0.7f);
                        Toast.makeText(AR_Activity.this, "BOX 선택.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            Anchor anchor = hitResult.createAnchor();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ModelRenderable.builder()
                        .setSource(this, Uri.parse(model))
                        .build()
                        .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                        .exceptionally(throwable -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(throwable.getMessage())
                                    .show();
                            return null;
                        });
            }
        });
    }


    public void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

        //로컬 스케일 지정 (절대크기) (절대크기를 따로 지정해도 object 와 멀어질시 원근감 표현 가능)
        transformableNode.setLocalScale(objectScale);

        //TransformableNode 의 스케일 조정 기능(핀치) 비활성화 -> 터치로 object 크기조절 불가능 / object 로테이션은 가능
        transformableNode.getScaleController().setSensitivity(0);

        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();

        //화면상에 Rendering 된 object 를 감싸는 Box 를 만들어 크기값을 Vector3 로 저장
        Box box = (Box) transformableNode.getRenderable().getCollisionShape();
        Vector3 size = box.getSize();

        //노드의 WorldScale 을 Vector3 값으로 저장
        Vector3 transformableNodeScale = transformableNode.getWorldScale();

        //Box 의 크기와 노드의 WorldScale 을 함께 계산하여 Vector3 값으로 저장 (유니티에서 단위 유닛은 미터이므로 이를 센티미터 단위로 변환하고 소수점 첫째 자리까지 자른다)
        //향후 나만의 가구 만들기에서 응용하여 사용자가 센티미터 단위를 입력하거나 SeekBar 를 통해 가로 세로 높이 값을 설정하여 object 의 크기를 조절 할 수 있을것으로 예상
        Vector3 finalSize =
                new Vector3(
                        (float) (Math.round((size.x * transformableNodeScale.x * 100) * 10) / 10.0),
                        (float) (Math.round((size.y * transformableNodeScale.y * 100) * 10) / 10.0),
                        (float) (Math.round((size.z * transformableNodeScale.z * 100) * 10) / 10.0));

        //화면에 최종 계산된 크기를 출력한다
        x_cm = String.valueOf(finalSize.x); //세로
        y_cm = String.valueOf(finalSize.y); //높이
        z_cm = String.valueOf(finalSize.z); //가로
        mtextView.setText("( 가로 : " + z_cm + "cm " + "세로 : " + x_cm + "cm " + "높이 : " + y_cm +"cm )");
    }

    private String generateFilename() {

        //현재시간을 기준으로 파일 이름 생성
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "IM/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        //사용자의 갤러리에 IM 디렉토리 생성 및 Bitmap 을 JPEG 형식으로 갤러리에 저장
        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto(){
        //PixelCopy 를 사용하여 카메라 화면과 object 를 bitmap 으로 생성
        final String filename = generateFilename();
        ArSceneView view = arFragment.getArSceneView();

        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),
                Bitmap.Config.ARGB_8888);

        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PixelCopy.request(view, bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {
                    try {
                        saveBitmapToDisk(bitmap, filename);

                        //Media Scanning 실시
                        Uri uri = Uri.parse("file://" + filename);
                        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        i.setData(uri);
                        sendBroadcast(i);

                    } catch (IOException e) {
                        Toast toast = Toast.makeText(AR_Activity.this, e.toString(),
                                Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "스크린샷이 저장되었습니다.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("갤러리에서 보기", v -> {
                        //어플 내에서 저장한 스크린샷을 확인 가능
                        File photoFile = new File(filename);

                        Uri photoURI = FileProvider.getUriForFile(AR_Activity.this,
                                AR_Activity.this.getPackageName() + ".ar.codelab.name.provider",
                                photoFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                        intent.setDataAndType(photoURI, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    });
                    snackbar.show();
                } else {
                    Toast toast = Toast.makeText(AR_Activity.this,
                            "스크린샷 저장 실패!: " + copyResult, Toast.LENGTH_LONG);
                    toast.show();
                }
                handlerThread.quitSafely();
            }, new Handler(handlerThread.getLooper()));
        }
    }
}
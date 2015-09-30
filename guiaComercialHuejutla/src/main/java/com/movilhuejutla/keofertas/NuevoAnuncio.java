package com.movilhuejutla.keofertas;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.movilhuejutla.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class NuevoAnuncio extends ActionBarActivity implements
        OnLongClickListener, OnItemClickListener, OnClickListener{

    private ImageView addPicture;
    private MaterialDialog materialDialog;
    private LinearLayout layout_upload_images;
    private int TAG_ID_ITEM = 1;
    private static final int SIZE_THUMB = 120;

    private LinearLayout.LayoutParams circleParams, linearParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_layout_nuevo_anuncio);

        //initialize LayoutParams for CircleView and LinearLayout
        circleParams = new LinearLayout.LayoutParams(200,200);
        linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layout_upload_images = (LinearLayout) findViewById(R.id.k_layout_new_ad_upload_images);
        addPicture = (ImageView) findViewById(R.id.k_layout_new_add_image);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo_anuncio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //code here...
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private LinearLayout newItemImage(Bitmap bitmap){

        //create a new LinearLayout
        LinearLayout itemLinearLayout = new LinearLayout(this);
        itemLinearLayout.setLayoutParams(linearParams);
        itemLinearLayout.setOrientation(LinearLayout.VERTICAL);
        itemLinearLayout.setPadding(4,10,10,4);

        //create a new CircleImageView
        CircleImageView circleImageView = new CircleImageView(this);
        circleImageView.setLayoutParams(circleParams);
        circleImageView.setImageBitmap(bitmap);

        //create a new NumberProgressBar
        NumberProgressBar numberProgressBar = (NumberProgressBar)
                getLayoutInflater().inflate(R.layout.k_layout_numer_progress, null);

        //add image and progress to linearLayout parent
        itemLinearLayout.addView(circleImageView);
        itemLinearLayout.addView(numberProgressBar);
        itemLinearLayout.setOnClickListener(this);
        itemLinearLayout.setTag(TAG_ID_ITEM);
        TAG_ID_ITEM++;

        return itemLinearLayout;
    }

    //Method to show AlertdDialog
    public void getImage(View v){
        showAlertDialog();

    }

    //show AlertDialog with option to take a picture
    private void showAlertDialog(){

        //create a new ArrayAdapter
        String[] values = new String[] { "Tomar de Camara", "Elegir de Galeria"};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, values);

        //create a listview and setAdapter
        ListView listView = new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setOnItemClickListener(this);
        listView.setDividerHeight(0);
        listView.setAdapter(arrayAdapter);

        //display AlertDialog with ListView
        materialDialog = new MaterialDialog(this);
        materialDialog.setContentView(listView);
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    public boolean onLongClick(View view) {
        layout_upload_images.removeView(view);
        layout_upload_images.refreshDrawableState();
        if(layout_upload_images.getChildCount() == 5)
            addPicture.setVisibility(ImageView.VISIBLE);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //if clicked item "Tomar desde Camara"
        if(position == 0){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, 1);
        }
        //if clicked item "Elegir de Galería"
        if(position == 1) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Dismiss the AlertDialog
        materialDialog.dismiss();

        //Process RequestCode & ResultCode From ActivityResult
        if(resultCode == RESULT_OK){

            //RequestCode TAKE_FROM_CAMERA
            if(requestCode == 1){

                //Get the picture´s path
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for(File temp : f.listFiles()){
                    if(temp.getName().equals("temp.jpg")){
                        f = temp;
                        break;
                    }
                }

                //Create a thumbnail
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(f.getAbsolutePath()), SIZE_THUMB, SIZE_THUMB);

                //getPosition to set de picture
                int children = layout_upload_images.getChildCount();

                //addItem to HorizontalLayout...
                layout_upload_images.addView(newItemImage(thumbnail), children-1);


                //set max limit images
                if(layout_upload_images.getChildCount() > 5)
                    addPicture.setVisibility(ImageView.GONE);

            }

            //RequestCode TAKE_FROM_GALLERY
            else if(requestCode == 2){

                //Get de picture's path
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                //Create a thumbnail
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(picturePath), SIZE_THUMB, SIZE_THUMB);

                //addItem to HorizontalLayout...
                layout_upload_images.addView(newItemImage(thumbnail));

                //limit images
                if(layout_upload_images.getChildCount() > 5)
                    addPicture.setVisibility(ImageView.GONE);
            }

        }

    }

    @Override
    public void onClick(View v) {

    }
}

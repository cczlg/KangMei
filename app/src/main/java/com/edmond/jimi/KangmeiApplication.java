package com.edmond.jimi;

import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import com.edmond.kangmei.R;

public class KangmeiApplication extends Application {
    //	public String workpath;
    public String excelpath;
    public String imagepath;
    public String dbpath;

    @Override
    public void onCreate() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, getString(R.string.err_no_sdcard), Toast.LENGTH_LONG).show();
            return;
        }
        excelpath = Environment.getExternalStorageDirectory().getPath()
                + getString(R.string.path_excel);
        File path = new File(excelpath);
        if (!path.exists()) {
            if (path.mkdirs() == false) {
                Toast.makeText(this, getString(R.string.err_cant_create_working_path), Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }

        imagepath = Environment.getExternalStorageDirectory().getPath()
                + getString(R.string.path_image);
        path = new File(imagepath);
        if (!path.exists()) {
            if (path.mkdirs() == false) {
                Toast.makeText(this, getString(R.string.err_cant_create_working_path), Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }

        dbpath = Environment.getExternalStorageDirectory().getPath()
                + getString(R.string.path_db);
        path = new File(dbpath);
        if (!path.exists()) {
            if (path.mkdirs() == false) {
                Toast.makeText(this, getString(R.string.err_cant_create_working_path), Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }
        super.onCreate();
    }

}

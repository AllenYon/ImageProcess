package com.example.BitmapEffect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Link
 * Date: 13-9-27
 * Time: AM10:20
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    static public void saveBitmap(Bitmap bitmap, File saveFile) throws Exception {
        FileOutputStream fos = new FileOutputStream(saveFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }


    static public File savePicture(byte[] data) {
        Bitmap jpeg = BitmapFactory.decodeByteArray(data, 0, data.length);
        File saveFile = new File(getDir(), System.currentTimeMillis() + ".jpeg");
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            jpeg.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveFile;
    }

    static public File getDir() {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + "BitmapEffect";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}

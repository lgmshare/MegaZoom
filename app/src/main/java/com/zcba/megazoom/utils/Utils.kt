package com.zcba.megazoom.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.gsls.gt.GT
import com.zcba.megazoom.BuildConfig
import com.zcba.megazoom.R
import java.io.File
import java.io.FileNotFoundException

/**
 * @author lim
 * @description: TODO
 * @email lgmshare@gmail.com
 * @datetime 2016/9/14 11:06
 */
object Utils {

    //得到绝对地址
    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val fileStr = cursor.getString(column_index)
        cursor.close()
        return fileStr
    }

    fun insertImageToAlbum(context: Context, file: File): Uri? {
        try {
            val resolver = context.contentResolver
            //ContentValues contentValues = getImageContentValues(context, file, System.currentTimeMillis());
            //resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            val path = MediaStore.Images.Media.insertImage(resolver, file.absolutePath, file.name, context.getString(R.string.app_name))
            val uri = Uri.parse(path)
            val uriFile = File(getRealPathFromURI(context, uri))
            //更新图库
            val intent = Intent()
            intent.action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
            intent.data = Uri.fromFile(uriFile)
            context.sendBroadcast(intent)
            return uri
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    fun log(msg: String?) {
        if (BuildConfig.DEBUG) {
            if (!msg.isNullOrEmpty()) {
                Log.d("LogHelper", msg)
            }
        }
    }
}
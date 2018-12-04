package com.example.ckddn.capstoneproject2018_2.Oblu;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


class Utilities {
    static final String LOG_DIR= "Log Files";
    static String LOG_FILE_NAME= null;
    static final int REQUEST_PERMISSIONS_LOG_STORAGE = 1003;

    static void writeHeaderToLog(Context context){
       SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
       Date now= new Date();
       LOG_FILE_NAME = "LOG_FILE_"+ formatter.format(now) + ".txt";
       try {
           String path = context.getPackageName() + File.separator + LOG_DIR;
           File root = createPathIfNotExist(path);
           if(root==null){
               LOG_FILE_NAME= null;
               return;
           }
           else {
               if (root.setExecutable(true) && root.setReadable(true) && root.setWritable(true)) {
                   Log.i("SET FILE PERMISSION ", "Set read , write and execuatable permission for log file");
               }
               MediaScannerConnection.scanFile(context, new String[]{root.toString()}, null, null);
           }
           File gpxFile= new File(root,LOG_FILE_NAME);
           FileWriter writer= new FileWriter(gpxFile,true);

           String header= String.format("%11s\t%9s\t%6s\t%6s\t%6s\t%7s\t%6s\n\n",
                   "TIME STAMP","STEP COUNT","X","Y","Z","Heading","Distance");
           writer.append(header);
           writer.flush();
           writer.close();
       }catch (Exception e){
           Log.e("WRITE LOG ERROR", e.getMessage());
       }
   }

   static File createPathIfNotExist(String relativePath){
       String testFilePath= null;
       File filePath;
       try {
           List<String> PossibleFilePaths = Arrays.asList("externalSD", "extSD", "Sdcard2", "external", "extSDcard");
           for (String sdPath : PossibleFilePaths) {
               File file = new File("/mnt/", sdPath);
               if (file.isDirectory() && file.canWrite()) {
                   testFilePath = file.getAbsolutePath();

                   String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                   File checkWritable = new File(testFilePath, "test_" + timeStamp);
                   if (checkWritable.mkdir()) {
                       if (checkWritable.delete()) {
                           Log.e("FILE ERROR", "Problem in deleting File");
                       }
                   } else {
                       testFilePath = null;
                   }
               }
           }
           if (testFilePath != null) {
               filePath = new File(testFilePath, relativePath);
           } else {
               filePath = new File(Environment.getExternalStorageDirectory(), relativePath);
           }
       }catch(Exception e){
           filePath=null;
       }
       if(filePath!=null&& !filePath.exists()){
           if(!filePath.mkdirs()){
               Log.e("FILE ERROR","problem creating app folder");
               return null;
           }
           else{
               Log.i("FILE HANDLING",testFilePath+"created successfully");
           }
       }
       return filePath;
   }
   static void writeDataToLog(Context context, StepData stepData){
       SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss",Locale.US);
       Date now= new Date();
       DecimalFormat df3= new DecimalFormat("0.00");
       try{
           if(LOG_FILE_NAME!=null&& LOG_FILE_NAME.length()>0){
               String path= File.separator+ context.getPackageName()+File.separator+LOG_DIR+File.separator;
               File root= createPathIfNotExist(path);
               if(root==null){
                   return;
               }
               File gpxFile= new File(root,LOG_FILE_NAME);
               FileWriter writer= new FileWriter(gpxFile,true);
               String data=String.format("%11s\t%9s\t%6s\t%6s\t%6s\t%7s\t%6s\n", formatter.format(now),stepData.getStepCount(),
                       df3.format(stepData.getX()),df3.format(stepData.getY()),df3.format(stepData.getZ()),
                       df3.format(stepData.getHeading()), df3.format(stepData.getDistance()));
               writer.append(data);
               writer.flush();
               writer.close();
           }
       }
       catch (IOException e){
           Log.e("FILE ERROR","Cannot write data in log");
       }
   }
}











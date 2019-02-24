package ar.com.tzulberti.archerytraining.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Used to export a view into a PDF file
 *
 */
public class PDFExporter {

    public File exportToPdf(View rootView, File fileDir, String baseFilename) throws Exception {
        Bitmap screen = this.scrollViewToBitmap(rootView);
        File pdfFile = new File(this.getDataDir(fileDir), baseFilename + ".pdf");


        Document document = new Document(PageSize.A4, 25, 25, 30, 30);

        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        screen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Image image = Image.getInstance(byteArray);
        float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float documentHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
        image.scaleToFit(documentWidth, documentHeight);

        document.add(image);
        document.close();
        return pdfFile;
    }

    public File exportToImage(View rootView, File fileDir, String baseFilename) throws Exception {
        Bitmap screen = this.scrollViewToBitmap(rootView);

        File imageFile = new File(this.getDataDir(fileDir), baseFilename + ".jpeg");
        FileOutputStream fos = new FileOutputStream(imageFile);
        screen.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        return imageFile;
    }

    private File getDataDir(File fileDir) throws Exception{
        File sdcard = Environment.getExternalStorageDirectory();
        if( sdcard == null || !sdcard.isDirectory() ) {
            throw new Exception("storage card not found");
        }

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new Exception("External storage not mounted");
        }


        File dataDir = new File(sdcard, "ArcheryTraining");
        if (dataDir.exists() && ! dataDir.isDirectory()) {
            throw new Exception("The path already exists: " + dataDir.getAbsolutePath() );
        } else if (! dataDir.exists()) {
            dataDir.mkdirs();
        }
        return dataDir;
    }

    private Bitmap scrollViewToBitmap(View view) {
        ScrollView z = (ScrollView) view;
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();
        return this.getBitmapFromView(view, totalHeight,totalWidth);
    }

    private Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
}

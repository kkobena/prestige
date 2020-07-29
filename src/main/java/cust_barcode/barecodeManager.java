/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cust_barcode;

//import com.barcodelib.barcode.Linear;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Font;
//import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import toolkits.utils.jdom;

/**
 *
 * @author Thierry Bekola
 */
public class barecodeManager {

    private static int uom = 0;        //  0 - Pixel, 1 - CM, 2 - Inch
    private static int resolution = 72;
    private static float leftMargin = 10.000f;
    private static float rightMargin = 10.000f;
    private static float topMargin = 10.000f;
    private static float bottomMargin = 10.000f;
    private static int rotate = 0;     //  0 - 0, 1 - 90, 2 - 180, 3 - 270

    private static float barWidth = 1.000f;
    private static float barHeight = 80.000f;

    public barecodeManager() {

        jdom.InitRessource();
        jdom.LoadRessource();
    }

    public String build2DBarecode(String DATA, String FileName) {
        String file = FileName;
        try {
            DataMatrixBean bean = new DataMatrixBean();
            final int dpi = 150;
            //  bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi));
            bean.setHeight(10);
            bean.setModuleWidth(10);
            // bean.doQuietZone(false);
            File outputFile = new File(jdom.barecode_file + "" + FileName + ".jpg");
            file = outputFile.getAbsolutePath();

            OutputStream out = new FileOutputStream(outputFile);
            try {
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/jpeg", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                bean.generateBarcode(canvas, DATA);
                canvas.finish();
            } finally {
                out.close();
            }
        } catch (Exception e) {
//            new logger().OCategory.error(e.getMessage());
        }
        return file;
    }

    /* public String buildLineBarecode(String DATA, String FileName) {
        String file = FileName;
        try {

            Linear linear = new Linear();
            linear.setData(DATA);

            linear.setType(Linear.CODABAR);

            setBarcodeSize(linear);
            linear.renderBarcode(jdom.barecode_file + "" + FileName + ".jpg");
      
        } catch (Exception e) {
            new logger().OCategory.error(e.getMessage());
        }
        return file;
    }
     */
 /*   
  public String buildLineBarecode(String DATA, String FileName) {
        String file = FileName;
        try {

            Linear linear = new Linear();
            linear.setData(DATA);

            linear.setType(Linear.CODABAR);

            setBarcodeSize(linear);
            linear.renderBarcode(jdom.barecode_file + "" + FileName + ".jpg");
          
        } catch (Exception e) {
            new logger().OCategory.error(e.getMessage());
        }
        return file;
    }  
     */
    public String buildLineBarecode(String DATA) {
        String file = DATA;

        try {
            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(DATA);
//            barcode128.setAltText(DATA);
            barcode128.setBaseline(10);
//            barcode128.setChecksumText(true);
//            barcode128.setCodeType(Barcode128.CODABAR);
            barcode128.setBarHeight(50);
            barcode128.setCodeType(Barcode128.CODE128);
            java.awt.Image img = barcode128.createAwtImage(Color.BLACK, Color.WHITE);
            BufferedImage bi = new BufferedImage(100, 70, BufferedImage.BITMASK);
            Graphics2D gd = bi.createGraphics();
            gd.drawImage(img, 4, 2, null);
            gd.setColor(Color.BLACK);
            gd.drawString(DATA, 10, 65);
            gd.dispose();
            File f = new File(jdom.barecode_file + "" + file + ".png");

            ImageIO.write(bi, "png", f);

        } catch (Exception e) {
            e.printStackTrace();
//            new logger().OCategory.error(e.getMessage());
        }

        return file;
    }

    public static Image getItextImageForCode39(String code, PdfWriter writer) {
        Barcode39 barcode39 = new Barcode39();
        barcode39.setCode(code);
        com.itextpdf.text.Image img = barcode39.createImageWithBarcode(writer.getDirectContent(), null, null);
        return img;
    }

  

    //dernier code de generation de code ean 13
    /* public String buildBareCodeEAN(String DATA, String file_name) {

        try {
            BarCode ean13 = new BarCode();

            ean13.setCodeToEncode(DATA);
            ean13.setSymbology(IBarCode.EAN13);
            ean13.setX(2);
            ean13.setY(50);
            ean13.setRightMargin(0);
            ean13.setLeftMargin(0);
            ean13.setTopMargin(0);
            ean13.setBottomMargin(0);
            ean13.draw(jdom.barecode_file + "" + file_name + ".png");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file_name;
    }*/
    //fin dernier code de 
    public static String buildbarcodeOther(String data, String str_file_name) {
        FileOutputStream out = null;
        try {
            //    BufferedImage imgtext = new BufferedImage(100, 70, BufferedImage.TYPE_BYTE_BINARY);
//        Graphics2D g2d = imgtext.createGraphics();
File f = new File(str_file_name);
            out = new FileOutputStream(f);
            AbstractBarcodeBean barcode = new Code128Bean();
            //     barcode.setBarHeight(50.0);
            barcode.setFontName("Calibri (Corps)");
            barcode.setFontSize(15.0);
            barcode.setMsgPosition(HumanReadablePlacement.HRP_NONE);
            barcode.setModuleWidth(0.8);
            BitmapCanvasProvider canvas
                    = new BitmapCanvasProvider(out, "image/x-png", 160, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            barcode.generateBarcode(canvas, data);
            try {
                canvas.finish();
            } catch (IOException ex) {
                Logger.getLogger(barecodeManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(barecodeManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(barecodeManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return  str_file_name;
    }

    public static String  __buildbarcodeOther(String data, String str_file_name) {
        String result = "";
        try {
          /* Linear linear = new Linear();
            linear.setData(data);
            linear.setType(Linear.CODE128);

            Font font = new Font("Calibri (Corps)", Font.BOLD, 15);
            linear.setX(5);
            linear.setY(100);

            linear.setShowText(false);
            linear.setTextFont(font);
            linear.renderBarcode(str_file_name);

         
            result = str_file_name;
*/
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return result;
    }
}

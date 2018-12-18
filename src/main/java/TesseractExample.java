
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.xmlgraphics.image.writer.ImageWriterUtil;

import javax.imageio.ImageIO;

public class TesseractExample {
    public static void main(String[] args) {

        // ImageIO.scanForPlugins(); // for server environment
        File imageFile = new File("test.pdf");
        readImage(imageFile);
        ITesseract instance = new Tesseract(); // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build only; only English data bundled
        instance.setDatapath(tessDataFolder.getParent());
        instance.setLanguage("chi_sim+eng");

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }

    public static String pdf2String(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(false);
        String result = stripper.getText(document);
        document.close();
        return result;
    }

    public static boolean extractImages(File file, String targetFolder) {
        boolean result = true;
        File fileFolder = new File(targetFolder);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        try {
            PDDocument document = PDDocument.load(file);
            PDPageTree pdPages = document.getDocumentCatalog().getPages();
            int count = 0;
            for (PDPage pdPage : pdPages) {
                PDResources resources = pdPage.getResources();
                resources.getXObjectNames();
//                Map<String, PDImageXObject> images = resources.get
//                if (images != null) {
//                    Iterator<String> imageIter = images.keySet().iterator();
//                    while (imageIter.hasNext()) {
//                        count++;
//                        String key = (String) imageIter.next();
//                        PDXObjectImage image = (PDXObjectImage) images.get(key);
//                        String name = file.getName() + "_" + count;    // 图片文件名
//                        image.write2file(targetFolder + File.separator + name);        // 保存图片
//                    }
//                }
                COSDictionary cosDictionary = pdPage.getResources().getCOSObject();
                Collection<COSBase> collection = cosDictionary.getValues();
                for (COSBase cosBase : collection) {
                    cosBase.getCOSObject();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return result;
    }

    public static void readImage(File pdfFile) {

        // 空白PDF
//        File pdfFile_out = new File("testout.pdf");

        PDDocument document = null;
        PDDocument document_out = null;
        try {
            document = PDDocument.load(pdfFile);
//            document_out = PDDocument.load(pdfFile_out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pages_size = document.getNumberOfPages();

        System.out.println("getAllPages===============" + pages_size);
        int j = 0;

        for (int i = 0; i < pages_size; i++) {
            PDPage page = document.getPage(i);
//            PDPage page1 = document_out.getPage(0);
            PDResources resources = page.getResources();
            Iterable<COSName> xobjects = resources.getXObjectNames();

            if (xobjects != null) {
                Iterator<COSName> imageIter = xobjects.iterator();
                while (imageIter.hasNext()) {
                    COSName key = imageIter.next();
                    if (resources.isImageXObject(key)) {
                        try {
                            PDImageXObject image = (PDImageXObject) resources.getXObject(key);

                            // 方式一：将PDF文档中的图片 分别存到一个空白PDF中。
//                            PDPageContentStream contentStream = new PDPageContentStream(document_out,page1, PDPageContentStream.AppendMode.APPEND,true);
//
//                            float scale = 1f;
//                            contentStream.drawImage(image, 20,20,image.getWidth()*scale,image.getHeight()*scale);
//                            contentStream.close();
//                            document_out.save("/Users/xiaolong/Downloads/123"+j+".pdf");
//
//                            System.out.println(image.getSuffix() + ","+image.getHeight() +"," + image.getWidth());


                            // 方式二：将PDF文档中的图片 分别另存为图片。
                            File file = new File("testPdf" + File.separator + j + ".png");
                            BufferedImage bufferedImage = image.getImage();
                            ImageIO.write(bufferedImage, "png", file);
//                            FileOutputStream out = new FileOutputStream(file);
//
//                            InputStream input = image.createInputStream();
//
//                            int byteCount = 0;
//                            byte[] bytes = new byte[1024];
//
//                            while ((byteCount = input.read(bytes)) > 0) {
//                                out.write(bytes, 0, byteCount);
//                            }
//
//                            out.close();
//                            input.close();


                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //image count
                        j++;
                    }
                }
            }
        }

        System.out.println(j);
    }

}


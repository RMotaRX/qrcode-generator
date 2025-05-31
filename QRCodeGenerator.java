import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class QRCodeGenerator {
    
    public static void generateQRCode(String text, String logoPath, String outputPath) 
            throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 640, 640, hints);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        if (logoPath != null && !logoPath.trim().isEmpty()) {
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                BufferedImage logo = ImageIO.read(logoFile);
                if (logo != null) {
                    System.out.println("Logo P&B carregado: " + logo.getWidth() + "x" + logo.getHeight());
                    
                    int logoSize = 80;
                    int x = (640 - logoSize) / 2;
                    int y = (640 - logoSize) / 2;
                    
                    int bgSize = logoSize + 15;
                    int bgX = (640 - bgSize) / 2;
                    int bgY = (640 - bgSize) / 2;
                    
                    for (int i = 0; i < bgSize; i++) {
                        for (int j = 0; j < bgSize; j++) {
                            int centerX = bgSize / 2;
                            int centerY = bgSize / 2;
                            double distance = Math.sqrt((i - centerX) * (i - centerX) + (j - centerY) * (j - centerY));
                            
                            if (distance <= bgSize / 2) {
                                qrImage.setRGB(bgX + i, bgY + j, 0xFFFFFF);
                            }
                        }
                    }
                    
                    for (int i = 0; i < logoSize; i++) {
                        for (int j = 0; j < logoSize; j++) {
                            int centerX = logoSize / 2;
                            int centerY = logoSize / 2;
                            double distance = Math.sqrt((i - centerX) * (i - centerX) + (j - centerY) * (j - centerY));
                            
                            if (distance <= logoSize / 2) {
                                int px = (i * (logo.getWidth() - 1)) / (logoSize - 1);
                                int py = (j * (logo.getHeight() - 1)) / (logoSize - 1);
                                
                                px = Math.max(0, Math.min(px, logo.getWidth() - 1));
                                py = Math.max(0, Math.min(py, logo.getHeight() - 1));
                                
                                int rgb = logo.getRGB(px, py);
                                qrImage.setRGB(x + i, y + j, rgb);
                            }
                        }
                    }
                }
            }
        }
        
        ImageIO.write(qrImage, "PNG", Paths.get(outputPath).toFile());
    }
    
    public static void main(String[] args) {
        try {
            String text = args.length > 0 ? args[0] : "https://example.com";
            String logoPath = args.length > 1 ? args[1] : null;
            String outputPath = args.length > 2 ? args[2] : "qrcode.png";
            
            generateQRCode(text, logoPath, outputPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
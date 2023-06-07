package gon.cue.basefullstack.util.mng;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
public class Util {

    // compress the image bytes before storing it in the database
    public static byte[] compressZLib(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
        log.info("Compressed Image Byte Size - " + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }

    // uncompress the image bytes before returning it to the angular application
    public static byte[] decompressZLib(byte[] data) {
         Inflater inflater = new Inflater();
         inflater.setInput(data);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
         byte[] buffer = new byte[1024];
         try {
             while (!inflater.finished()) {
                 int count = inflater.inflate(buffer);
                 outputStream.write(buffer, 0, count);
             }
             outputStream.close();
         } catch (IOException e) {
             log.error("Error: " + e.getMessage());
         } catch (DataFormatException e) {
             log.error("Error: " + e.getMessage());
         }
         return outputStream.toByteArray();
    }
}
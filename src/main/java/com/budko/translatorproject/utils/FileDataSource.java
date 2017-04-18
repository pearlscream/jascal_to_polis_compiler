package com.budko.translatorproject.utils;

import com.budko.translatorproject.abstraction.DataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author DBudko.
 */
public class FileDataSource implements DataSource {
    private String filePath;

    public FileDataSource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getProgramText() {
        String file = null;
        try (InputStream stream = new FileInputStream(filePath)) {
            byte[] b = new byte[stream.available()];
            stream.read(b);
            file = new String(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}

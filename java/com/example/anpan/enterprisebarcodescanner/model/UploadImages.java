package com.example.anpan.enterprisebarcodescanner.model;

import java.io.File;

/**
 * Created by Yash on 13-06-2017.
 */

public class UploadImages {

    public File imageFile;
    public String ticketId;

    public UploadImages(File imageFile, String ticketId) {
        this.imageFile = imageFile;
        this.ticketId = ticketId;
    }
}

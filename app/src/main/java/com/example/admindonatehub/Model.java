package com.example.admindonatehub;


public class Model {
    private String imagePath; // The path to the image file
    private String name;
    private String dntdetail; // Details field
    private String recordKey;

    // Default constructor (no-argument constructor)
    public Model() {
        // Default constructor required for Firebase
    }

    // Constructor
    public Model(String imagePath, String name, String dntdetail,String recordKey) {
        this.imagePath = imagePath;
        this.name = name;
        this.dntdetail = dntdetail;
        this.recordKey = recordKey;
    }

    // Getter and Setter methods for imagePath
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Getter and Setter methods for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter methods for dntdetail (details)
    public String getDntdetail() {
        return dntdetail;
    }

    public void setDntdetail(String dntdetail) {
        this.dntdetail = dntdetail;
    }

    public String getRecordKey(){return recordKey;}

}

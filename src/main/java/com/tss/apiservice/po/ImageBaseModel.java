package com.tss.apiservice.po;

import java.io.Serializable;

public class ImageBaseModel implements Serializable {
    private static final long serialVersionUID = -2663865007053422835L;

    private Integer id;

    /**
     * 图像序号
     */
    private Integer imageIndex;

    /**
     * 图像路径
     */
    private String imageDir;

    /**
     * 图像名称
     */
    private String imageName;

    /**
     * 文件类型
     */
    private String imageType;

    /**
     * 文件原名
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(Integer imageIndex) {
        this.imageIndex = imageIndex;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    @Override
    public String toString() {
        return "ImageBaseModel{" +
                "id=" + id +
                ", imageIndex=" + imageIndex +
                ", imageDir='" + imageDir + '\'' +
                ", imageName='" + imageName + '\'' +
                ", imageType='" + imageType + '\'' +
                '}';
    }
}

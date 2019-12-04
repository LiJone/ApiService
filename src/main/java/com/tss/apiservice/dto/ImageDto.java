package com.tss.apiservice.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 壮Jone
 */
@Data
public class ImageDto implements Serializable {
    private static final long serialVersionUID = -6006230092044441711L;

    private String filePathTmp;

    private String fileName;

    private String imageIndex;

    private String name;
}

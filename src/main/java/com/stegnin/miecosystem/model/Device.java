package com.stegnin.miecosystem.model;

import lombok.Data;

/**
 * @author Alexandr Stegnin
 */

@Data
public abstract class Device {

    private String ip;

    private String mac;

    private String model;

    private String name;

    private String token;

    private int port = 54321;

}

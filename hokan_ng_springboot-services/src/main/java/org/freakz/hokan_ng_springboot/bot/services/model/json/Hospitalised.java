package org.freakz.hokan_ng_springboot.bot.services.model.json;

import lombok.Data;

import javax.annotation.Generated;
import java.util.Date;


@Data
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Hospitalised {

    private String area;

    private Date date;

    private Integer dead;

    private Integer inIcu;

    private Integer inWard;

    private Integer totalHospitalised;


}

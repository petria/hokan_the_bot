package org.freakz.hokan_ng_springboot.bot.services.model.json;

import lombok.Data;

import javax.annotation.Generated;
import java.util.List;

@Data
@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class FinnishCoronaHospitalData {

    private List<Hospitalised> hospitalised;

    public List<Hospitalised> getHospitalised() {
        return hospitalised;
    }

    public void setHospitalised(List<Hospitalised> hospitalised) {
        this.hospitalised = hospitalised;
    }

}

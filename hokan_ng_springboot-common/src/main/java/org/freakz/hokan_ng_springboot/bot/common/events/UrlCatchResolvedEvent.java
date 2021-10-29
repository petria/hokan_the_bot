package org.freakz.hokan_ng_springboot.bot.common.events;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlCatchResolvedEvent implements Serializable {

    private String url;
    private String title;

}

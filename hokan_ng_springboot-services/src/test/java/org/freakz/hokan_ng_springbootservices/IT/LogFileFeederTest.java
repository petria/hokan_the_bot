package org.freakz.hokan_ng_springbootservices.IT;


import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.DataValuesService;
import org.freakz.hokan_ng_springboot.bot.services.service.patch.LogFileFeedService;
import org.freakz.hokan_ng_springboot.bot.services.service.topcounter.TopCountService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;


public class LogFileFeederTest {

    @Mock
    private DataValuesService dataValuesService;

    @Mock
    private JmsSender jsmSender;

  //  @BeforeAll
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    //    @Test
    public void testLogFileFeeder() throws IOException {
        TopCountService topCountService = new TopCountService(dataValuesService, jsmSender);

        LogFileFeedService sut = new LogFileFeedService(topCountService);
        sut.feedLogs();

    }

}

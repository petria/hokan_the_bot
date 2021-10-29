package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.jms.JmsEnvelope;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.common.models.*;
import org.freakz.hokan_ng_springboot.bot.common.service.translate.SanakirjaOrgTranslateService;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.currency.CurrencyService;
import org.freakz.hokan_ng_springboot.bot.services.service.locations.LocationsService;
import org.freakz.hokan_ng_springboot.bot.services.service.metar.MetarDataService;
import org.freakz.hokan_ng_springboot.bot.services.service.nimipaiva.NimipaivaService;
import org.freakz.hokan_ng_springboot.bot.services.service.topics.TopicService;
import org.freakz.hokan_ng_springboot.bot.services.service.urls.UrlCatchService;
import org.freakz.hokan_ng_springboot.bot.services.service.wholelinetricker.WholeLineTriggers;
import org.freakz.hokan_ng_springboot.bot.services.updaters.DataUpdater;
import org.freakz.hokan_ng_springboot.bot.services.updaters.UpdaterData;
import org.freakz.hokan_ng_springboot.bot.services.updaters.UpdaterManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 10.2.2015.
 * -
 */
@Service
@SuppressWarnings("unchecked")
public class ServicesServiceMessageHandlerImpl implements JmsServiceMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ServicesServiceMessageHandlerImpl.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private org.freakz.hokan_ng_springboot.bot.services.service.imdb.IMDBService IMDBService;

    @Autowired
    private LocationsService locationsService;

    @Autowired
    private MetarDataService metarDataService;

    @Autowired
    private NimipaivaService nimipaivaService;


    @Autowired
    private TopicService topicService;

    @Autowired
    private SanakirjaOrgTranslateService translateService;

    @Autowired
    private UpdaterManagerService updaterManagerService;

    @Autowired
    private UrlCatchService urlCatchService;

    @Autowired
    private WholeLineTriggers wholeLineTriggers;

    public ServicesServiceMessageHandlerImpl(ApplicationContext applicationContext, CurrencyService currencyService, org.freakz.hokan_ng_springboot.bot.services.service.imdb.IMDBService imdbService,
                                             MetarDataService metarDataService, NimipaivaService nimipaivaService, TopicService topicService, SanakirjaOrgTranslateService translateService,
                                             UpdaterManagerService updaterManagerService, UrlCatchService urlCatchService) {
        this.applicationContext = applicationContext;
        this.currencyService = currencyService;
        IMDBService = imdbService;
        this.metarDataService = metarDataService;
        this.nimipaivaService = nimipaivaService;
        this.topicService = topicService;
        this.translateService = translateService;
        this.updaterManagerService = updaterManagerService;
        this.urlCatchService = urlCatchService;
    }


    private boolean findHandlersMethod(ServiceRequest request, ServiceResponse response) {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String beanName : names) {
            Object obj = applicationContext.getBean(beanName);
            Class<?> objClz = obj.getClass();
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
            }
            for (Method m : objClz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(ServiceMessageHandler.class)) {
                    Annotation annotation = m.getAnnotation(ServiceMessageHandler.class);
                    ServiceMessageHandler serviceMessageHandler = (ServiceMessageHandler) annotation;
                    ServiceRequestType requestType = serviceMessageHandler.ServiceRequestType();
                    if (requestType == request.getType()) {
                        try {
                            m.invoke(obj, request, response);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("Could not call service handler for: {}", request);
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.WEATHER_REQUEST)
    public void handleWeatherRequest(ServiceRequest request, ServiceResponse response) {
        DataUpdater weatherUpdater = updaterManagerService.getUpdater("kelikameratUpdater");
        UpdaterData updaterData = new UpdaterData();
        weatherUpdater.getData(updaterData);
        List<KelikameratWeatherData> data = (List<KelikameratWeatherData>) updaterData.getData();
        response.setResponseData(request.getType().getResponseDataKey(), data);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.CHANNEL_TOPIC_GET_REQUEST)
    public void handleChannelTopicGetRequest(ServiceRequest request, ServiceResponse response) {
        ChannelSetTopic setTopic = topicService.channelTopicGet(request.getIrcMessageEvent());
        response.setResponseData(request.getType().getResponseDataKey(), setTopic);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.CHANNEL_TOPIC_SET_REQUEST)
    public void handleChannelTopicSetRequest(ServiceRequest request, ServiceResponse response) {
        topicService.channelTopicSet(request.getIrcMessageEvent());
    }


    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.CATCH_URLS_REQUEST)
    public void handleCatchUrlsRequest(ServiceRequest request, ServiceResponse response) {
        urlCatchService.catchUrls(request);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.CURRENCY_CONVERT_REQUEST)
    public void handleCurrencyConvertRequest(ServiceRequest request, ServiceResponse response) {
        String amount = (String) request.getParameters()[0];
        String from = (String) request.getParameters()[1];
        String to = (String) request.getParameters()[2];
        String currencyConvert = currencyService.googleConvert(amount, from, to);
        response.setResponseData(request.getType().getResponseDataKey(), currencyConvert);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.CURRENCY_LIST_REQUEST)
    public void handleCurrencyListRequest(ServiceRequest request, ServiceResponse response) {
        List<GoogleCurrency> currencyList = currencyService.getGoogleCurrencies();
        response.setResponseData(request.getType().getResponseDataKey(), currencyList);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.FIND_CITY_REQUEST)
    public void handleFindCityRequest(ServiceRequest request, ServiceResponse response) {
        String cityArg = (String) request.getParameters()[0];
        log.debug("Find city: {}", cityArg);
        FindCityResults results = new FindCityResults();
        results.setWorldCityDataList(locationsService.findMatchingCities(cityArg));
        response.setResponseData(request.getType().getResponseDataKey(), results);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.IMDB_DETAILED_INFO_REQUEST)
    public void handleIMDBDetailedInfoRequest(ServiceRequest request, ServiceResponse response) {
        String title = (String) request.getParameters()[0];
        IMDBDetails imdbDetails = IMDBService.getDetailedInfo(title);
        response.setResponseData(request.getType().getResponseDataKey(), imdbDetails);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.IMDB_TITLE_REQUEST)
    public void handleIMDBTitleRequest(ServiceRequest request, ServiceResponse response) {
        String title = (String) request.getParameters()[0];
        IMDBSearchResults imdbSearchResults = IMDBService.findByTitle(title);
        response.setResponseData(request.getType().getResponseDataKey(), imdbSearchResults);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.METAR_REQUEST)
    public void handleMetarRequest(ServiceRequest request, ServiceResponse response) {
        List<MetarData> data = metarDataService.getMetarData(request.getParameters());
        response.setResponseData(request.getType().getResponseDataKey(), data);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.NIMIPAIVA_DAY_REQUEST)
    public void handleNimipaivaDayRequest(ServiceRequest request, ServiceResponse response) {
        LocalDateTime day = (LocalDateTime) request.getParameters()[0];
        NimipaivaData nimipaivaData = nimipaivaService.getNamesForDay(day);
        response.setResponseData(request.getType().getResponseDataKey(), nimipaivaData);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.NIMIPAIVA_NAME_REQUEST)
    public void handleNimipaivaNameRequest(ServiceRequest request, ServiceResponse response) {
        String nameStr = (String) request.getParameters()[0];
        NimipaivaData theDay = nimipaivaService.findDayForName(nameStr);
        response.setResponseData(request.getType().getResponseDataKey(), theDay);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.TRANSLATE_REQUEST)
    public void handleTranslateRequest(ServiceRequest request, ServiceResponse response) {
        String originalText = (String) request.getParameters()[0];
        TranslateResponse translateResponse = translateService.translateText(originalText);
        response.setResponseData(request.getType().getResponseDataKey(), translateResponse);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.UPDATERS_LIST_REQUEST)
    public void handleUpdaterListRequest(ServiceRequest request, ServiceResponse response) {
        List<DataUpdaterModel> modelList = updaterManagerService.getDataUpdaterModelList();
        response.setResponseData(request.getType().getResponseDataKey(), modelList);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.UPDATERS_START_REQUEST)
    public void handleUpdaterStartRequest(ServiceRequest request, ServiceResponse response) {
        List<DataUpdaterModel> startedUpdaters = new ArrayList<>();
        for (Object toStart : request.getParameters()) {
            String updater = (String) toStart;
            DataUpdaterModel model = updaterManagerService.startUpdaterByName(updater);
            if (model != null) {
                startedUpdaters.add(model);
            }
        }
        response.setResponseData(request.getType().getResponseDataKey(), startedUpdaters);
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.WHOLE_LINE_TRIGGER)
    public void handleWholeLineTrigger(ServiceRequest request, ServiceResponse response) {
        wholeLineTriggers.checkWholeLineTrigger(request.getIrcMessageEvent());
    }


    @Override
    public void handleJmsEnvelope(JmsEnvelope envelope) throws Exception {
        ServiceRequest request = envelope.getMessageIn().getServiceRequest();
        ServiceResponse response = new ServiceResponse(request.getType());
        boolean handleDone = findHandlersMethod(request, response);
        if (!handleDone) {
            log.error("Service request NOT handled!!!!");
        }
        envelope.getMessageOut().addPayLoadObject("SERVICE_RESPONSE", response);
    }


}

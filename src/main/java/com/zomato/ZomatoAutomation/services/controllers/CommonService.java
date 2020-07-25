package com.zomato.ZomatoAutomation.services.controllers;

import com.zomato.ZomatoAutomation.commons.controllers.CommonEndPoints;
import com.zomato.ZomatoAutomation.utils.RestUtils;
import io.restassured.response.Response;

import java.util.Map;

public class CommonService {

    CommonEndPoints commonEndPoints = new CommonEndPoints();
    RestUtils restUtils = new RestUtils();

    public Response getLocations() throws Exception {
        return restUtils.get(commonEndPoints.GET_CATEGORIES);
    }

    public Response getCity(Map<String, String> queryParams) throws Exception {
        return restUtils.get(commonEndPoints.GET_CITY, queryParams);
    }

    public Response getGeoCode(Map<String, String> queryParams) throws Exception {
        return restUtils.get(commonEndPoints.GET_GEOCODE, queryParams);
    }


}

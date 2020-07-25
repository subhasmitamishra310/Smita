package com.zomato.ZomatoAutomation.services.controllers;

import com.zomato.ZomatoAutomation.commons.controllers.LocationEndPoints;
import com.zomato.ZomatoAutomation.utils.RestUtils;
import io.restassured.response.Response;

import java.util.Map;

public class LocationService {
    LocationEndPoints locationEndPoints = new LocationEndPoints();
    RestUtils restUtils = new RestUtils();

    public Response getLocations(Map<String, String> queryParams) throws Exception {
        return restUtils.get(locationEndPoints.GET_LOCATIONS, queryParams);
    }

    public Response getLocationDetails(Map<String, String> queryParams) throws Exception {
        return restUtils.get(locationEndPoints.LOCATION_DETAILS, queryParams);
    }

}

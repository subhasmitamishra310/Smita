package com.zomato.ZomatoAutomation.services.controllers;

import com.zomato.ZomatoAutomation.commons.controllers.RestaurantsEndPoints;
import com.zomato.ZomatoAutomation.utils.RestUtils;
import io.restassured.response.Response;

import java.util.Map;


public class RestaurantService {
    RestUtils restUtils = new RestUtils();
    public Response getSearchRestaurants(Map<String,String> queryParams) throws Exception
    {
        return  restUtils.get(RestaurantsEndPoints.GET_SEARCH,queryParams);
    }

}

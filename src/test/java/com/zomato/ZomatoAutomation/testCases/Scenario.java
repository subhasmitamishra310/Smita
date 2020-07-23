package com.zomato.ZomatoAutomation.testCases;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zomato.ZomatoAutomation.services.controllers.CommonService;
import com.zomato.ZomatoAutomation.services.controllers.LocationService;
import com.zomato.ZomatoAutomation.services.controllers.RestaurantService;
import com.zomato.ZomatoAutomation.setUp.Base;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class Scenario extends Base {

    LocationService locationService;
    RestaurantService restaurantService;
    Gson gson = new Gson();
    int entityId;

    @BeforeClass
    public void initializeObjects()
    {
        locationService = new LocationService();
        restaurantService = new RestaurantService();
    }

    @Test(priority = 0)
    public void getEntityIdLocation() throws Exception {
        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("query","Koramangala");
        Response response = locationService.getLocations(queryParams);

        JsonObject jsonObject = gson.fromJson(response.getBody().asString(),JsonObject.class);
        JsonArray jsonArray = jsonObject.get("location_suggestions").getAsJsonArray();
        entityId = jsonArray.get(0).getAsJsonObject().get("entity_id").getAsInt();
        System.out.println(entityId);
    }

    @Test(dependsOnMethods = "getEntityIdLocation")
    public void getRecommendedRestaurants() throws Exception {
        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("entity_id",String.valueOf(entityId));
        queryParams.put("radius",String.valueOf(3000));
        queryParams.put("cuisines","35,1018,50,85");
        queryParams.put("sort","rating");
        queryParams.put("order","desc");
        queryParams.put("entity_type","zone");
        //  queryParams.put("count","5");

        Response response = restaurantService.getSearchRestaurants(queryParams);

        JsonObject jsonObject = gson.fromJson(response.getBody().asString(),JsonObject.class);
        JsonArray jsonArray = jsonObject.get("restaurants").getAsJsonArray();

        int count = 0;
        for (JsonElement jsonElement:jsonArray)
        {
            if(count < 5)
            {
                int avg_cost_two = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("average_cost_for_two").getAsInt();
                if(avg_cost_two<=3500)
                {
                    System.out.println("Restaurant - " + ++count);
                    String name = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("name").getAsString();
                    String timings = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("timings").getAsString();
                    String rating = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("user_rating").getAsJsonObject()
                            .get("aggregate_rating").getAsString();

                    System.out.println("Name : "+ name);
                    System.out.println("Aggregate_rating : "+ rating);
                    System.out.println("timings : "+ timings);
                    System.out.println("Average_cost_for_two : "+ avg_cost_two);
                }
            }
            else
                break;
        }

    }

}

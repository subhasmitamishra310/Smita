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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class Scenario extends Base {
    LocationService locationService;
    RestaurantService restaurantService;
    CommonService commonService;
    Gson gson = new Gson();
    private int entityId;
    private int zomatoId;

    @BeforeClass
    public void initializeObjects() {
        locationService = new LocationService();
        restaurantService = new RestaurantService();
        commonService = new CommonService();


    }

    @Test(priority = 0)
    public void getRestaurantInfoByKeyword() throws Exception {
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("query", "Koramangala");
        Response response = locationService.getLocations(queryParams);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.path("status"), "success");

        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray jsonArray = jsonObject.get("location_suggestions").getAsJsonArray();
        entityId = jsonArray.get(0).getAsJsonObject().get("entity_id").getAsInt();

        System.out.println(jsonArray);
    }

    @Test(priority = 1)
    public void getBestResaurantInfo() throws Exception {
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("entity_id", "109253");
        queryParams.put("entity_type", "zone");
        Response response = locationService.getLocationDetails(queryParams);
        Assert.assertEquals(response.getStatusCode(), 200);
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("top_cuisines");
        JsonObject location = jsonObject.getAsJsonObject("location").getAsJsonObject();
        System.out.println("Location info : " + location);
        System.out.println("top cuisines are: ");

        for (JsonElement cuisines : jsonArray) {
            System.out.println(cuisines);
        }
        JsonArray bestRatedRestaurant = jsonObject.getAsJsonArray("best_rated_restaurant");
        System.out.println(bestRatedRestaurant);
        for (JsonElement jsonElement : bestRatedRestaurant) {
            System.out.println("Restaurant " + jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("name").getAsString());
            System.out.println("aggregate_rating " + jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().
                    get("user_rating").getAsJsonObject().get("aggregate_rating").getAsString());

            System.out.println("rating_text " + jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().
                    get("user_rating").getAsJsonObject().get("rating_text").getAsString());
        }
    }

    @Test(priority = 2)
    public void getZomatoIdForSpecificCity() throws Exception {
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("q", "Bengaluru");
        Response response = commonService.getCity(queryParams);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.path("status"), "success");

        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray jsonArray = jsonObject.get("location_suggestions").getAsJsonArray();
        zomatoId = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
    }

    @Test(priority = 3)
    public void getAllCategories() throws Exception {
        Response response = commonService.getLocations();
        Assert.assertEquals(response.getStatusCode(), 200);
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray categories = jsonObject.get("categories").getAsJsonArray();
        System.out.println("All category names with its Ids are : ");

        for (JsonElement categorynames : categories) {
            System.out.println(categorynames.getAsJsonObject().get("categories").getAsJsonObject().get("id").getAsInt() + "." +
                    categorynames.getAsJsonObject().get("categories").getAsJsonObject().get("name").getAsString());
        }

    }

    @Test(priority = 4)
    public void getGeoCodeOfRestaurants() throws Exception {
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("lat", String.valueOf("40.742051"));
        queryParams.put("lon", String.valueOf("-74.004821"));

        Response response = commonService.getGeoCode(queryParams);
        Assert.assertEquals(response.getStatusCode(), 200);
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray nearby_restaurants = jsonObject.get("nearby_restaurants").getAsJsonArray();

        System.out.println(nearby_restaurants);
        for (JsonElement jsonElement : nearby_restaurants) {
            System.out.println("Restaurant name " + jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("name").getAsString());
            zomatoId = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("id").getAsInt();
            System.out.println("Restaurant id " + zomatoId);
        }
    }

    @Test(dependsOnMethods = "getGeoCodeOfRestaurants", priority = 5)
    public void getReviewOfRestaurants() throws Exception {
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("res_id", String.valueOf(zomatoId));
        Response response = restaurantService.getReviewOfRestaurants(queryParams);

        Assert.assertEquals(response.getStatusCode(), 200);
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray user_reviews = jsonObject.get("user_reviews").getAsJsonArray();
        for (JsonElement jsonElement : user_reviews) {
            System.out.println("rating " + jsonElement.getAsJsonObject().get("review").getAsJsonObject().get("rating").getAsString());
            System.out.println("review_text " + jsonElement.getAsJsonObject().get("review").getAsJsonObject().get("review_text").getAsString());
            System.out.println("review_text " + jsonElement.getAsJsonObject().get("review").getAsJsonObject().get("user").getAsJsonObject().get("name").getAsString());
        }
    }

    @Test(dependsOnMethods = "getRestaurantInfoByKeyword", priority = 6)
    public void getRecommendedRestaurants() throws Exception {
        Map<String, String> queryParams = new HashMap<String, String>();
        System.out.println(entityId);
        queryParams.put("entity_id", String.valueOf(entityId));
        queryParams.put("radius", String.valueOf(3000));
        queryParams.put("cuisines", "35,1018,50,85");
        queryParams.put("sort", "rating");
        queryParams.put("order", "desc");
        queryParams.put("entity_type", "zone");

        Response response = restaurantService.getSearchRestaurants(queryParams);
        Assert.assertEquals(response.getStatusCode(), 200);
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray jsonArray = jsonObject.get("restaurants").getAsJsonArray();

        int count = 0;
        for (JsonElement jsonElement : jsonArray) {
            if (count < 5) {
                int avg_cost_two = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("average_cost_for_two").getAsInt();
                if (avg_cost_two <= 3500) {
                    System.out.println("Restaurant - " + ++count);
                    String name = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("name").getAsString();
                    String timings = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("timings").getAsString();
                    String rating = jsonElement.getAsJsonObject().get("restaurant").getAsJsonObject().get("user_rating").getAsJsonObject().get("aggregate_rating").getAsString();
                    System.out.println("Name : " + name);
                    System.out.println("Aggregate_rating : " + rating);
                    System.out.println("timings : " + timings);
                    System.out.println("Average_cost_for_two : " + avg_cost_two);
                }
            } else
                break;
        }

    }


    @Test(priority = 7)
    public void getDailyMenu() throws Exception {
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("res_id", "16507624");
        Response response = restaurantService.getDailyMenu(queryParams);

        Assert.assertEquals(response.getStatusCode(), 200);
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray dailyMenu = jsonObject.get("daily_menus").getAsJsonArray();
        System.out.println(dailyMenu);

    }
}
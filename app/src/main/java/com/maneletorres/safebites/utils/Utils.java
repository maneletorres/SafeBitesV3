package com.maneletorres.safebites.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.maneletorres.safebites.entities.Nutrient;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Utils {
    public static final String PRODUCT = "com.maneletorres.safebites.extras.PRODUCT";
    public static final String PRODUCT_A = "com.maneletorres.safebites.extras.PRODUCT_A";
    public static final String PRODUCT_B = "com.maneletorres.safebites.extras.PRODUCT_B";
    public static final String INGREDIENTS = "com.maneletorres.safebites.extras.INGREDIENTS";
    public static final String INGREDIENTS_A = "com.maneletorres.safebites.extras.INGREDIENTS_A";
    public static final String INGREDIENTS_B = "com.maneletorres.safebites.extras.INGREDIENTS_B";
    public static final String IMAGE_RESOURCE_A = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_A";
    public static final String IMAGE_RESOURCE_B = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_B";
    public static final String USER = "com.maneletorres.safebites.extras.USER";
    public static User sUser;

    public static final String HEADER_SPECIFIC_PRODUCT_URL = "https://world.openfoodfacts.org/api/v0/product/";
    public static final String TAIL_SPECIFIC_PRODUCT_URL = ".json";

    public static final int RC_SCAN = 0x0000b90f;
    public static final int RC_SCAN_OPTION_1_FIRST_EXECUTION = 11;
    public static final int RC_SCAN_OPTION_1_SECOND_EXECUTION = 12;
    public static final int RC_SCAN_OPTION_2 = 2;

    static Product extractJSONNutrients(JSONObject currentProduct, JSONObject JSONNutrients) {
        Product product = null;
        try {
            //String[] product_elements = {"code", "product_name", "image_small_url", "ingredients_text", "serving_quantity"};
            String[] product_elements = {"code", "product_name", "image_small_url", "ingredients_text", "serving_quantity", "allergens_hierarchy", "traces_hierarchy"};
            ArrayList<String> product_elements_result = new ArrayList<>();
            ArrayList<String> product_allergens = new ArrayList<>();

            for (String product_element : product_elements) {
                String element_name = "?";
                if (currentProduct.has(product_element)) {
                    if(product_element.equals("allergens_hierarchy") || product_element.equals("traces_hierarchy")){
                        product_allergens = createAllergens(product_element);
                    } else {
                        element_name = currentProduct.getString(product_element);
                    }
                }
                product_elements_result.add(element_name);
            }

            ArrayList<Nutrient> product_nutrients = new ArrayList<>();

            createNutrients(product_nutrients, JSONNutrients);
            product = new Product(
                    product_elements_result.get(0),
                    product_elements_result.get(1),
                    product_elements_result.get(2),
                    product_nutrients,
                    product_elements_result.get(3),
                    product_elements_result.get(4),
                    product_allergens
            );
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return product;
    }

    public static void createNutrients(ArrayList<Nutrient> nutrients, JSONObject JSONNutrients) {
        try {
            String[] nutrients_name = {"Energy", "Total fat", "Saturated fat",
                    "Dietary fiber", "Total carbohydrate", "Sugars",
                    "Protein", "Salt", "Calcium", "Sodium"};

            String[] JSONElements_100g = {"energy_value", "fat_100g", "saturated-fat_100g",
                    "fiber_100g", "carbohydrates_100g", "sugars_100g",
                    "proteins_100g", "salt_100g", "calcium_100g", "sodium_100g"};

            String[] JSONElements_portion = {"energy_serving",
                    "fat_serving", "saturated-fat_serving", "fiber_serving",
                    "carbohydrates_serving", "sugars_serving", "proteins_serving",
                    "salt_serving", "calcium_serving", "sodium_serving"};

            String[] JSONElements_unit = {"kcal", "fat_unit", "saturated-fat_unit", "fiber_unit",
                    "carbohydrates_unit", "sugars_unit", "proteins_unit", "salt_unit",
                    "calcium_unit", "sodium_unit"};

            int loop_length = nutrients_name.length;
            for (int i = 0; i < loop_length; i++) {
                String current_nutrient_per100g;
                if (JSONNutrients.has(JSONElements_100g[i])) {
                    current_nutrient_per100g = JSONNutrients.getString(JSONElements_100g[i]);
                    if (current_nutrient_per100g.length() == 0) {
                        continue;
                    } else {
                        if (current_nutrient_per100g.contains(".")) {
                            String decimals = current_nutrient_per100g.substring(current_nutrient_per100g.indexOf("."));
                            if (decimals.length() > 2) {
                                double current_nutrient_value = Double.parseDouble(current_nutrient_per100g);
                                current_nutrient_per100g = String.valueOf((double) Math.round(current_nutrient_value * 100) / 100);
                            }
                        }
                    }
                } else {
                    continue;
                }

                String current_nutrient_per_portion;
                if (JSONNutrients.has(JSONElements_portion[i])) {
                    current_nutrient_per_portion = JSONNutrients.getString(JSONElements_portion[i]);
                    if (current_nutrient_per_portion.length() == 0) {
                        current_nutrient_per_portion = "?";
                    } else {
                        if (current_nutrient_per_portion.contains(".")) {
                            String decimals = current_nutrient_per_portion.substring(current_nutrient_per_portion.indexOf("."));
                            if (decimals.length() > 2) {
                                double current_nutrient_value = Double.parseDouble(current_nutrient_per_portion);
                                current_nutrient_per_portion = String.valueOf((double) Math.round(current_nutrient_value * 100) / 100);
                            }
                        }
                    }
                } else {
                    current_nutrient_per_portion = "?";
                }

                String current_nutrient_unit;
                if (JSONNutrients.has(JSONElements_unit[i])) {
                    current_nutrient_unit = JSONNutrients.getString(JSONElements_unit[i]);
                    if (current_nutrient_unit.length() == 0) {
                        if (i == 0) {
                            current_nutrient_unit = "kcal";
                        } else {
                            current_nutrient_unit = "g";
                        }
                    }
                } else {
                    if (i == 0) {
                        current_nutrient_unit = "kcal";
                    } else {
                        current_nutrient_unit = "g";
                    }
                }

                nutrients.add(new Nutrient(nutrients_name[i], current_nutrient_per100g, current_nutrient_per_portion, current_nutrient_unit));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<String> createAllergens(String allergens){
        ArrayList<String> arrayList = new ArrayList<>();
        String[] allergensSplitted = allergens.split(",");
        Collections.addAll(arrayList, allergensSplitted);

        return arrayList;
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
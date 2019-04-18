package com.maneletorres.safebites.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maneletorres.safebites.AuthActivity;
import com.maneletorres.safebites.entities.Nutrient;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Utils {
    public static final String CLASS_NAME = "CLASS_NAME";
    public static final String TOAST_MESSAGE = "TOAST_MESSAGE";
    public static final String PRODUCT = "com.maneletorres.safebites.extras.PRODUCT";
    public static final String PRODUCT_A = "com.maneletorres.safebites.extras.PRODUCT_A";
    public static final String PRODUCT_B = "com.maneletorres.safebites.extras.PRODUCT_B";
    public static final String INGREDIENTS = "com.maneletorres.safebites.extras.INGREDIENTS";
    public static final String INGREDIENTS_A = "com.maneletorres.safebites.extras.INGREDIENTS_A";
    public static final String INGREDIENTS_B = "com.maneletorres.safebites.extras.INGREDIENTS_B";
    public static final String IMAGE_RESOURCE_A = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_A";
    public static final String IMAGE_RESOURCE_B = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_B";
    public static final String HEADER_SPECIFIC_PRODUCT_URL = "https://world.openfoodfacts.org/api/v0/product/";
    public static final String TAIL_SPECIFIC_PRODUCT_URL = ".json";
    public static final int RC_SCAN = 0x0000b90f;
    public static final int RC_SCAN_OPTION_1_FIRST_EXECUTION = 11;
    public static final int RC_SCAN_OPTION_1_SECOND_EXECUTION = 12;
    public static final int RC_SCAN_OPTION_2 = 2;
    public static User sUser;
    public static String sUID;

    static Product extractJSONNutrients(JSONObject currentProduct, JSONObject JSONNutrients) {
        Product product = null;
        try {
            String[] product_elements = {"code", "product_name", "image_small_url", "ingredients_text", "serving_quantity", "allergens_hierarchy", "traces_hierarchy"};
            ArrayList<String> product_elements_result = new ArrayList<>();
            ArrayList<String> product_allergens = new ArrayList<>();

            for (String product_element : product_elements) {
                String element_name = "";
                if (currentProduct.has(product_element)) {
                    if (product_element.equals("allergens_hierarchy") || product_element.equals("traces_hierarchy")) {
                        product_allergens = createAllergens(product_element);
                    } else {
                        element_name = currentProduct.getString(product_element);
                        if (element_name == null || element_name.equals("") || element_name.equals("?")) {
                            element_name = "?";
                        }
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
            String[] nutrients_name = {
                    // Ordered nutrients:
                    "Energy", "Fat", "Saturated fat", "Monounsaturated fat", "Polyunsaturated fat",
                    "Carbohydrate", "Sugars", "Polyols", "Starch", "Fiber",
                    "Protein", "Salt", "Vitamin A", "Vitamin D", "Vitamin E",
                    "Vitamin K", "Vitamin C", "Vitamin B1", "Vitamin B2", "Vitamin B3",
                    "Vitamin B6", "Vitamin B9", "Vitamin B12", "Biotin", "Pantothenic acid",
                    "Potassium", "Chloride", "Calcium", "Phosphorus", "Magnesium",
                    "Iron", "Zinc", "Copper", "Manganese", "Fluoride",
                    "Selenium", "Chromium", "Molybdenum", "Iodine",

                    // Disordered nutrients:
                    "Sodium", "Casein", "Serum proteins", "Nucleotides", "Sucrose",
                    "Glucose", "Fructose", "Lactose", "Maltose", "Maltodextrins",
                    "Butyric acid", "Caproic acid", "Caprylic acid", "Capric acid",
                    "Lauric acid", "Myristic acid", "Palmitic acid", "Stearic acid",
                    "Arachidic acid", "Behenic acid", "Lignoceric acid", "Cerotic acid",
                    "Montanic acid", "Melissic acid", "Omega 3 fat", "Alpha-linolenic acid",
                    "Eicosapentaenoic acid", "Docosahexaenoic acid", "Omega-6 fat",
                    "Linoleic acid", "Arachidonic acid", "Gamma-linolenic acid",
                    "Dihomo-gamma-linolenic acid", "Omega-9 fat", "Oleic acid", "Elaidic acid",
                    "Gondoic acid", "Mead acid", "Erucic acid", "Nervonic acid", "Trans fat",
                    "Cholesterol", "Alcohol", "Silica", "Bicarbonate", "Caffeine", "Taurine", "PH"};

            String[] JSONElements_100g = {
                    // Ordered nutrients:
                    "energy_value", "fat_value", "saturated-fat_value", "monounsaturated-fat_value", "polyunsaturated-fat_value",
                    "carbohydrates_value", "sugars_value", "polyols_value", "starch_value", "fiber_value",
                    "proteins_value", "salt_value", "vitamin-a_value", "vitamin-d_value", "vitamin-e_value",
                    "vitamin-k_value", "vitamin-c_value", "vitamin-b1_value", "vitamin-b2_value", "vitamin-pp_value",
                    "vitamin-b6_value", "vitamin-b9_value", "vitamin-b12_value", "biotin_value", "pantothenic-acid_value",
                    "potassium_value", "chloride_value", "calcium_value", "phosphorus_value", "magnesium_value",
                    "iron_value", "zinc_value", "copper_value", "manganese_value", "fluoride_value",
                    "selenium_value", "chromium_value", "molybdenum_value", "iodine_value",

                    // Disordered nutrients:
                    "sodium_value", "casein_value", "serum-proteins_value", "nucleotides_value", "sucrose_value",
                    "glucose_value", "fructose_value", "lactose_value", "maltose_value", "maltodextrins_value",
                    "butyric-acid_value", "caproic-acid_value", "caprylic-acid_value", "capric-acid_value",
                    "lauric-acid_value", "myristic-acid_value", "palmitic-acid_value", "stearic-acid_value",
                    "arachidic-acid_value", "behenic-acid_value", "lignoceric-acid_value", "cerotic-acid_value",
                    "montanic-acid_value", "melissic-acid_value", "omega-3-fat_value", "alpha-linolenic-acid_value",
                    "eicosapentaenoic-acid_value", "docosahexaenoic-acid_value", "omega-6-fat_value",
                    "linoleic-acid_value", "arachidonic-acid_value", "gamma-linolenic-acid_value",
                    "dihomo-gamma-linolenic-acid_value", "omega-9-fat_value", "oleic-acid_value", "elaidic-acid_value",
                    "gondoic-acid_value", "mead-acid_value", "erucic-acid_value", "nervonic-acid_value", "trans-fat_value",
                    "cholesterol_value", "alcohol_value ", "silica_value", "bicarbonate_value",
                    "caffeine_value", "taurine_value", "ph_value"
            };

            String[] JSONElements_portion = {
                    // Ordered nutrients:
                    "energy_serving", "fat_serving", "saturated-fat_serving", "monounsaturated-fat_serving", "polyunsaturated-fat_serving",
                    "carbohydrates_serving", "sugars_serving", "polyols_serving", "starch_serving", "fiber_serving",
                    "proteins_serving", "salt_serving", "vitamin-a_serving", "vitamin-d_serving", "vitamin-e_serving",
                    "vitamin-k_serving", "vitamin-c_serving", "vitamin-b1_serving", "vitamin-b2_serving", "vitamin-pp_serving",
                    "vitamin-b6_serving", "vitamin-b9_serving", "vitamin-b12_serving", "biotin_serving", "pantothenic-acid_serving",
                    "potassium_serving", "chloride_serving", "calcium_serving", "phosphorus_serving", "magnesium_serving",
                    "iron_serving", "zinc_serving", "copper_serving", "manganese_serving", "fluoride_serving",
                    "selenium_serving", "chromium_serving", "molybdenum_serving", "iodine_serving",

                    // Disordered nutrients:
                    "sodium_serving", "casein_serving", "serum-proteins_serving", "nucleotides_serving", "sucrose_serving",
                    "glucose_serving", "fructose_serving", "lactose_serving", "maltose_serving", "maltodextrins_serving",
                    "butyric-acid_serving", "caproic-acid_serving", "caprylic-acid_serving", "capric-acid_serving",
                    "lauric-acid_serving", "myristic-acid_serving", "palmitic-acid_serving", "stearic-acid_serving",
                    "arachidic-acid_serving", "behenic-acid_serving", "lignoceric-acid_serving", "cerotic-acid_serving",
                    "montanic-acid_serving", "melissic-acid_serving", "omega-3-fat_serving", "alpha-linolenic-acid_serving",
                    "eicosapentaenoic-acid_serving", "docosahexaenoic-acid_serving", "omega-6-fat_serving",
                    "linoleic-acid_serving", "arachidonic-acid_serving", "gamma-linolenic-acid_serving",
                    "dihomo-gamma-linolenic-acid_serving", "omega-9-fat_serving", "oleic-acid_serving", "elaidic-acid_serving",
                    "gondoic-acid_serving", "mead-acid_serving", "erucic-acid_serving", "nervonic-acid_serving", "trans-fat_serving",
                    "cholesterol_serving", "alcohol_serving ", "silica_serving", "bicarbonate_serving", "caffeine_serving", "taurine_serving", "ph_serving"};

            String[] JSONElements_unit = {
                    // Ordered nutrients:
                    "energy_unit", "fat_unit", "saturated-fat_unit", "monounsaturated-fat_unit", "polyunsaturated-fat_unit",
                    "carbohydrates_unit", "sugars_unit", "polyols_unit", "starch_unit", "fiber_unit",
                    "proteins_unit", "salt_unit", "vitamin-a_unit", "vitamin-d_unit", "vitamin-e_unit",
                    "vitamin-k_unit", "vitamin-c_unit", "vitamin-b1_unit", "vitamin-b2_unit", "vitamin-pp_unit",
                    "vitamin-b6_unit", "vitamin-b9_unit", "vitamin-b12_unit", "biotin_unit", "pantothenic-acid_unit",
                    "potassium_unit", "chloride_unit", "calcium_unit", "phosphorus_unit", "magnesium_unit",
                    "iron_unit", "zinc_unit", "copper_unit", "manganese_unit", "fluoride_unit",
                    "selenium_unit", "chromium_unit", "molybdenum_unit", "iodine_unit",

                    // Disordered nutrients:
                    "sodium_unit", "casein_unit", "serum-proteins_unit", "nucleotides_unit", "sucrose_unit",
                    "glucose_unit", "fructose_unit", "lactose_unit", "maltose_unit", "maltodextrins_unit",
                    "butyric-acid_unit", "caproic-acid_unit", "caprylic-acid_unit", "capric-acid_unit",
                    "lauric-acid_unit", "myristic-acid_unit", "palmitic-acid_unit", "stearic-acid_unit",
                    "arachidic-acid_unit", "behenic-acid_unit", "lignoceric-acid_unit", "cerotic-acid_unit",
                    "montanic-acid_unit", "melissic-acid_unit", "omega-3-fat_unit", "alpha-linolenic-acid_unit",
                    "eicosapentaenoic-acid_unit", "docosahexaenoic-acid_unit", "omega-6-fat_unit",
                    "linoleic-acid_unit", "arachidonic-acid_unit", "gamma-linolenic-acid_unit",
                    "dihomo-gamma-linolenic-acid_unit", "omega-9-fat_unit", "oleic-acid_unit", "elaidic-acid_unit",
                    "gondoic-acid_unit", "mead-acid_unit", "erucic-acid_unit", "nervonic-acid_unit", "trans-fat_unit",
                    "cholesterol_unit", "alcohol_unit ", "silica_unit", "bicarbonate_unit", "caffeine_unit", "taurine_unit", "ph_unit"};

            // 3 decimals formatter:
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat threeDecimalsFormat = new DecimalFormat("#.###", otherSymbols);
            threeDecimalsFormat.setRoundingMode(RoundingMode.CEILING);

            // 0 decimals formatter:
            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.CEILING);

            for (int i = 0; i < nutrients_name.length; i++) {
                try {
                    // Nutrient per 100g:
                    String current_nutrient_per100g;
                    if (JSONNutrients.has(JSONElements_100g[i])) {
                        current_nutrient_per100g = JSONNutrients.getString(JSONElements_100g[i]);
                        if (current_nutrient_per100g.length() == 0) {
                            continue;
                        } else {
                            current_nutrient_per100g = threeDecimalsFormat.format(Double.parseDouble(current_nutrient_per100g));
                        }
                    } else {
                        continue;
                    }

                    // Nutrient unit:
                    String current_nutrient_unit = "";
                    if (JSONNutrients.has(JSONElements_unit[i])) {
                        current_nutrient_unit = JSONNutrients.getString(JSONElements_unit[i]);
                        if (current_nutrient_unit.length() > 0 && i == 0) {
                            if (current_nutrient_unit.toUpperCase().equals("KCAL")) {
                                current_nutrient_per100g = df.format(Double.parseDouble(current_nutrient_per100g) * 4.184) + " kj / " + current_nutrient_per100g;
                                current_nutrient_unit = current_nutrient_unit.toLowerCase();
                            } else if (current_nutrient_unit.toUpperCase().equals("KJ")) {
                                current_nutrient_per100g = current_nutrient_per100g + " kJ / " + df.format(Double.parseDouble(current_nutrient_per100g) / 4.184);
                                current_nutrient_unit = " kcal";
                            }
                        }
                    }

                    // Nutrient per serving:
                    int multiplier = 1;
                    switch (current_nutrient_unit) {
                        case "mg":
                            multiplier = 1000;
                            break;
                        case "µg":
                            multiplier = 1000000;
                            break;
                        default:
                            break;
                    }

                    String current_nutrient_per_portion;
                    if (JSONNutrients.has(JSONElements_portion[i])) {
                        current_nutrient_per_portion = JSONNutrients.getString(JSONElements_portion[i]);
                        if (current_nutrient_per_portion.length() == 0) {
                            current_nutrient_per_portion = "-";
                        } else {
                            current_nutrient_per_portion = threeDecimalsFormat.format(Double.parseDouble(current_nutrient_per_portion) * multiplier);
                        }
                    } else {
                        current_nutrient_per_portion = "-";
                    }

                    nutrients.add(new Nutrient(nutrients_name[i], current_nutrient_per100g, current_nutrient_per_portion, current_nutrient_unit));
                } catch (NumberFormatException ex) {
                    ex.getMessage();
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private static ArrayList<String> createAllergens(String allergens) {
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

    // Testing:
    public static void staticListenerLoad(Activity activity) {
        DatabaseReference sUserDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(sUID);
        sUserDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.v("dataSnapshot", dataSnapshot.toString());
                Log.v("onChildRemoved2", "onChildRemoved2");

                FirebaseAuth.getInstance().getCurrentUser().delete();
                AuthUI.getInstance()
                        .signOut(activity)
                        .addOnCompleteListener(task -> {
                            // User is now signed out:
                            Intent intent = new Intent(activity, AuthActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
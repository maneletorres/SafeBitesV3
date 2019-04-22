package com.maneletorres.safebites.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.maneletorres.safebites.entities.Nutrient;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.ProductNotFormatted;
import com.maneletorres.safebites.entities.User;
import com.maneletorres.safebites.fragments.CompareFragment;
import com.maneletorres.safebites.fragments.FavoritesFragment;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class Utils {
    public static final String CLASS_NAME = "com.maneletorres.safebites.extras.CLASS_NAME";
    public static final String TOAST_MESSAGE = "com.maneletorres.safebites.extras.TOAST_MESSAGE";
    public static final String PRODUCT = "com.maneletorres.safebites.extras.PRODUCT";
    public static final String PRODUCT_A = "com.maneletorres.safebites.extras.PRODUCT_A";
    public static final String PRODUCT_B = "com.maneletorres.safebites.extras.PRODUCT_B";
    public static final String INGREDIENTS = "com.maneletorres.safebites.extras.INGREDIENTS";
    public static final String INGREDIENTS_A = "com.maneletorres.safebites.extras.INGREDIENTS_A";
    public static final String INGREDIENTS_B = "com.maneletorres.safebites.extras.INGREDIENTS_B";
    public static final String IMAGE_RESOURCE_A = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_A";
    public static final String IMAGE_RESOURCE_B = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_B";
    public static final int RC_SCAN = 0x0000b90f;
    public static final int RC_SCAN_OPTION_1_FIRST_EXECUTION = 11;
    public static final int RC_SCAN_OPTION_1_SECOND_EXECUTION = 12;
    public static final int RC_SCAN_OPTION_2 = 2;

    // Static user variables:
    public static User sUser;
    public static String sUID;
    //public static ArrayList<Product> sProducts;
    public static CompareFragment sCompareFragment;
    public static FavoritesFragment sFavoriteFragment;

    public static Product formatProduct(ProductNotFormatted product) {
        try {
            String[] nutrients_name = {
                    // Ordered nutrients:
                    "Energy", "Fat", "Saturated fat", "Monounsaturated fat", "Polyunsaturated fat",
                    "Carbohydrate", "Sugars", "Polyols", "Starch", "Fiber",
                    "Proteins", "Salt", "Vitamin A", "Vitamin D", "Vitamin E",
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
                    "cholesterol_value", "alcohol_value", "silica_value", "bicarbonate_value",
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
                    "cholesterol_serving", "alcohol_serving", "silica_serving", "bicarbonate_serving", "caffeine_serving", "taurine_serving", "ph_serving"};

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
                    "cholesterol_unit", "alcohol_unit", "silica_unit", "bicarbonate_unit", "caffeine_unit", "taurine_unit", "ph_unit"};

            // Default decimal separator modifier:
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator('.');

            // 3 decimals formatter:
            DecimalFormat threeDecimalsFormat = new DecimalFormat("#.###", otherSymbols);
            threeDecimalsFormat.setRoundingMode(RoundingMode.CEILING);

            // 1 decimal formatter:
            DecimalFormat df = new DecimalFormat("#.#", otherSymbols);
            df.setRoundingMode(RoundingMode.CEILING);

            // Section I - Creation of nutrients:
            ArrayList<Nutrient> nutrients = new ArrayList<>();

            JsonObject nutrimentsJson = product.getNutriments();
            for (int i = 0; i < nutrients_name.length; i++) {
                // Nutrient per 100g:
                String current_nutrient_per100g;
                JsonElement jsonElement1 = nutrimentsJson.get(JSONElements_100g[i]);
                if (jsonElement1 != null) {
                    current_nutrient_per100g = formatChain(jsonElement1.toString());
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
                String aux = "";
                JsonElement jsonElement2 = nutrimentsJson.get(JSONElements_unit[i]);
                if (jsonElement2 != null) {
                    current_nutrient_unit = formatChain(jsonElement2.toString());
                    if (current_nutrient_unit.length() > 0 && i == 0) {
                        aux = current_nutrient_unit;
                        if (current_nutrient_unit.toUpperCase().equals("KCAL")) {
                            current_nutrient_per100g = df.format(Double.parseDouble(current_nutrient_per100g) * 4.184) + " kj / " + df.format(Double.parseDouble(current_nutrient_per100g));
                            current_nutrient_unit = current_nutrient_unit.toLowerCase();
                        } else if (current_nutrient_unit.toUpperCase().equals("KJ")) {
                            current_nutrient_per100g = df.format(Double.parseDouble(current_nutrient_per100g)) + " kJ / " + df.format(Double.parseDouble(current_nutrient_per100g) / 4.184);
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
                JsonElement jsonElement3 = nutrimentsJson.get(JSONElements_portion[i]);
                if (jsonElement3 != null) {
                    current_nutrient_per_portion = formatChain(jsonElement3.toString());
                    if (current_nutrient_per_portion.length() == 0) {
                        current_nutrient_per_portion = "-";
                    } else {
                        if (i == 0) {
                            if (aux.toUpperCase().equals("KCAL")) {
                                current_nutrient_per_portion = df.format(Double.parseDouble(current_nutrient_per_portion) * 4.184 * multiplier) + " kj / " + df.format(Double.parseDouble(current_nutrient_per_portion));
                            } else if (aux.toUpperCase().equals("KJ")) {
                                current_nutrient_per_portion = df.format(Double.parseDouble(current_nutrient_per_portion)) + " kJ / " + df.format((Double.parseDouble(current_nutrient_per_portion) / 4.184) * multiplier);
                            }
                        } else {
                            current_nutrient_per_portion = threeDecimalsFormat.format(Double.parseDouble(current_nutrient_per_portion) * multiplier);
                        }
                    }
                } else {
                    current_nutrient_per_portion = "-";
                }

                nutrients.add(new Nutrient(nutrients_name[i], current_nutrient_per100g, current_nutrient_per_portion, current_nutrient_unit));
            }

            // Section II - Checking product attributes:
            String name = product.getProduct_name();
            if (name == null || name.length() == 0 || name.equals("?")) {
                name = "-";
            }

            String upc = product.getCode();
            if (upc == null || upc.length() == 0 || upc.equals("?")) {
                upc = "-";
            }

            String image_resource = product.getImage_small_url();
            if (image_resource == null || image_resource.length() == 0 || image_resource.equals("?")) {
                image_resource = "-";
            }

            String ingredients = product.getIngredients_text();
            if (ingredients == null || ingredients.length() == 0 || ingredients.equals("?")) {
                ingredients = "-";
            }

            String serving_size = product.getServing_size();
            if (serving_size == null || serving_size.length() == 0 || serving_size.equals("?")) {
                serving_size = "-";
            }

            // Section III - Joint creation of allergens and traces:
            ArrayList<String> allergensAndTraces = new ArrayList<>();

            JsonArray allergens = product.getAllergens_hierarchy();
            JsonArray traces = product.getTraces_hierarchy();
            if (allergens.size() > 0 && traces.size() > 0) {
                allergens.addAll(traces);
            }

            if (allergens.size() > 0) {
                allergensAndTraces = allergensJointCreation(allergens.toString().substring(1, allergens.toString().length() - 1));
            } else if (traces.size() > 0) {
                allergensAndTraces = allergensJointCreation(traces.toString().substring(1, traces.toString().length() - 1));
            }

            return new Product(upc, name, image_resource, nutrients, ingredients, serving_size, allergensAndTraces);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String formatChain(String chain) {
        String result = chain;
        if (chain.charAt(0) == '"') {
            String aux = chain.substring(1);
            char fin = aux.charAt(aux.length() - 1);
            if (fin == '"') {
                result = aux.substring(0, aux.length() - 1);
            } else {
                result = aux;
            }
        }

        return result;
    }

    private static ArrayList<String> allergensJointCreation(String allergens) {
        ArrayList<String> arrayList = new ArrayList<>();
        String[] allergensSplitted = allergens.split(",");
        String[] auxSplitted = new String[allergensSplitted.length];

        for (int i = 0; i < allergensSplitted.length; i++) {
            String auxAllergen = formatChain(allergensSplitted[i]);
            auxSplitted[i] = auxAllergen;
        }

        Collections.addAll(arrayList, auxSplitted);

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

    public static void staticListenerLoad() {
        //sProducts = new ArrayList<>();
        sUser.setProducts(new ArrayList<>());

        // Reference to the user's favorite products:
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(sUID).child("products");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Product product = dataSnapshot.getValue(Product.class);

                //sProducts.add(product);
                sUser.addProduct(product);

                sCompareFragment.updateProducts();
                sFavoriteFragment.updateProducts();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);

                boolean condition = false;
                ArrayList<Product> products = sUser.getProducts();
                for (int i = 0; i < products.size() && !condition; i++) {
                    Product currentProduct = products.get(i);
                    if (currentProduct.getUpc().equals(product.getUpc())) {
                        condition = true;
                        //sProducts.remove(currentProduct);
                        sUser.removeProduct(product);
                    }
                }

                sCompareFragment.updateProducts();
                sFavoriteFragment.updateProducts();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);

        // Reference to the user's allergies:
        DatabaseReference allergiesDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(sUID).child("allergies");
        allergiesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> userAllergens = (HashMap<String, Boolean>) dataSnapshot.getValue();
                sUser.setAllergies(userAllergens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Testing:
    public static void deleteUserInformation() {
        sUser = null;
        sUID = null;
    }
}